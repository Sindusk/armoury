package mod.sin.armoury;

import com.wurmonline.server.combat.ArmourTemplate;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateFactory;
import mod.sin.lib.ArmourAssist;
import mod.sin.lib.WoundAssist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class ArmourTemplateTweaks {
    public static Logger logger = Logger.getLogger(ArmourTemplateTweaks.class.getName());

    // Maps for variable changes to armour
    public static HashMap<Byte, Float> armourDamageReduction = new HashMap<>();
    public static HashMap<Byte, HashMap<Byte, Float>> armourEffectiveness = new HashMap<>();
    public static HashMap<Byte, HashMap<Byte, Float>> armourGlanceRates = new HashMap<>();
    public static HashMap<String, Float> armourMovement = new HashMap<>();

    protected static ArrayList<Byte> getWoundTypes(String[] split){
        ArrayList<Byte> woundTypes = new ArrayList<>();
        int i = 0;
        while(i < split.length-1){
            if(split[i].equalsIgnoreCase("all")){
                byte x = 0;
                while(x <= 10){
                    woundTypes.add(x);
                    x++;
                }
            }else if(split[i].equalsIgnoreCase("physical")){
                woundTypes.add((byte) 0); // Crush
                woundTypes.add((byte) 1); // Slash
                woundTypes.add((byte) 2); // Pierce
                woundTypes.add((byte) 3); // Bite
            }else if(split[i].equalsIgnoreCase("elemental")){
                woundTypes.add((byte) 4); // Burn
                woundTypes.add((byte) 8); // Cold
                woundTypes.add((byte) 10); // Acid
            }else if(split[i].equalsIgnoreCase("other")){
                woundTypes.add((byte) 5); // Poison
                woundTypes.add((byte) 6); // Infection
                woundTypes.add((byte) 7); // Water
                woundTypes.add((byte) 9); // Internal
            }else{
                woundTypes.add(WoundAssist.getWoundType(split[i]));
            }
            i++;
        }
        return woundTypes;
    }

    public static void addArmourDamageReduction(byte armourType, float reduction){
        armourDamageReduction.put(armourType, reduction);
    }

    public static void addArmourEffectiveness(byte armourType, String[] split){
        float reduction = Float.parseFloat(split[split.length-1]);
        ArrayList<Byte> woundTypes = getWoundTypes(split);
        for(byte woundType : woundTypes) {
            HashMap<Byte, Float> map;
            if (armourEffectiveness.containsKey(armourType)) {
                map = armourEffectiveness.get(armourType);
            } else {
                map = new HashMap<>();
            }
            map.put(woundType, reduction);
            armourEffectiveness.put(armourType, map);
        }
    }

    public static void addArmourGlanceRate(byte armourType, String[] split){
        float glance = Float.parseFloat(split[split.length-1]);
        ArrayList<Byte> woundTypes = getWoundTypes(split);
        for(byte woundType : woundTypes) {
            HashMap<Byte, Float> map;
            if (armourGlanceRates.containsKey(armourType)) {
                map = armourGlanceRates.get(armourType);
            } else {
                map = new HashMap<>();
            }
            map.put(woundType, glance);
            armourGlanceRates.put(armourType, map);
        }
    }

    public static void addArmourMovement(String itemTemplateName, float movementPenalty){
        armourMovement.put(itemTemplateName, movementPenalty);
    }

    public static void onServerStarted(){
        if (ArmouryModMain.enableArmourModifications){
            for (byte atype : armourDamageReduction.keySet()){
                ArmourTemplate.ArmourType armourType = ArmourAssist.getArmourType(atype);
                armourType.setBaseDR(armourDamageReduction.get(atype));
            }
            for (byte atype : armourEffectiveness.keySet()){
                ArmourTemplate.ArmourType armourType = ArmourAssist.getArmourType(atype);
                HashMap<Byte,Float> woundMap = armourEffectiveness.get(atype);
                for (byte woundType : woundMap.keySet()){
                    armourType.setEffectiveness(woundType, woundMap.get(woundType));
                }
            }
            for (byte atype : armourGlanceRates.keySet()){
                ArmourTemplate.ArmourType armourType = ArmourAssist.getArmourType(atype);
                HashMap<Byte,Float> woundMap = armourGlanceRates.get(atype);
                for (byte woundType : woundMap.keySet()){
                    armourType.setGlanceRate(woundType, woundMap.get(woundType));
                }
            }
            for (String armourName : armourMovement.keySet()){
                // Get the ItemTemplate instance for the armour name
                ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(armourName);
                if (template == null){
                    logger.warning(String.format("Could not find a valid template for armour %s to adjust movement penalty. Skipping...", armourName));
                    continue;
                }
                // Obtain the ArmourTemplate instance based on the ItemTemplate
                ArmourTemplate armourTemplate = ArmourTemplate.getArmourTemplate(template.getTemplateId());
                armourTemplate.setMoveModifier(armourMovement.get(armourName)); // Set the new movement speed.
            }
        }
    }
}
