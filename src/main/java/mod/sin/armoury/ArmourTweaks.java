package mod.sin.armoury;

import com.wurmonline.server.Server;
import com.wurmonline.server.combat.Armour;
import com.wurmonline.server.combat.ArmourTypes;
import com.wurmonline.server.items.*;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import mod.sin.lib.ArmourAssist;
import mod.sin.lib.Util;
import mod.sin.lib.WoundAssist;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class ArmourTweaks {
	public static Logger logger = Logger.getLogger(ArmourTweaks.class.getName());

    //public static String[] armourTypes = {"cloth", "leather", "studded", "chain", "plate", "drake", "dragonscale", // Worn armour pieces
      //      "scale", "ring", "splint"}; // Used by the system but not worn by players

    public static HashMap<Integer, Float> armourDamageReduction = new HashMap<>();
    public static HashMap<Integer, HashMap<Byte, Float>> armourEffectiveness = new HashMap<>();
    public static HashMap<Integer, HashMap<Byte, Float>> armourGlanceRates = new HashMap<>();

    public static HashMap<Byte, Float> materialDamageReduction = new HashMap<>();
    public static HashMap<Byte, HashMap<Byte, Float>> materialEffectiveness = new HashMap<>();
    public static HashMap<Byte, HashMap<Byte, Float>> materialGlanceRate = new HashMap<>();
    public static HashMap<Byte, Float> materialMovementModifier = new HashMap<>();

    public static HashMap<String, Integer> armourNameToItemTemplate = new HashMap<>();

    public static ArrayList<Armour> clothArmour = new ArrayList<>();
    public static ArrayList<Armour> leatherArmour = new ArrayList<>();
    public static ArrayList<Armour> studdedArmour = new ArrayList<>();
    public static ArrayList<Armour> chainArmour = new ArrayList<>();
    public static ArrayList<Armour> plateArmour = new ArrayList<>();
    public static ArrayList<Armour> drakeArmour = new ArrayList<>();
    public static ArrayList<Armour> dragonscaleArmour = new ArrayList<>();

    public static float unarmouredReduction = 0.05f;

	public static float newGetArmourModFor(Item armour, byte woundType){
	    float toReturn = 0.0f;
	    if(armour != null){
	        int armourType = armour.getArmourType();
	        // Use custom armour reductions if available:
	        if(armourDamageReduction.containsKey(armourType)){
	            toReturn = armourDamageReduction.get(armourType);
            }else{ // Otherwise simply use the vanilla base DR
                toReturn = ArmourTypes.getArmourBaseDR(armourType);
	            //logger.warning("Could not find armour reduction reference for armour type "+armourType+". Using default value ("+toReturn+")");
            }
            // Overwrite again if item template is in the armour reduction override (custom armour):
            if (ArmouryMod.armourReductionOverride.containsKey(armour.getTemplateId())){
                toReturn = ArmouryMod.armourReductionOverride.get(armour.getTemplateId())-unarmouredReduction;
            }
            //logger.info("Base DR: "+toReturn);
	        if(armourType > -1){
	            byte material = armour.getMaterial();
	            // Use the material damage reduction set from configs if available
	            if(materialDamageReduction.containsKey(material)){
	                toReturn += materialDamageReduction.get(material);
                    //logger.info(String.format("Found armour made of material %s. Adding %.1f Base DR", material, materialDamageReduction.get(material)*100f));
                }else { // Otherwise simply use the vanilla material bonuses
                    toReturn += ArmourTypes.getArmourMatBonus(material);
                }
                //logger.info("Post-Material DR: "+toReturn);

                // Base armour effectiveness calculations
                float armourEff;
	            if(armourEffectiveness.containsKey(armourType) && armourEffectiveness.get(armourType).containsKey(woundType)){
	                armourEff = armourEffectiveness.get(armourType).get(woundType);
                    //logger.info(String.format("Found of type %s. Adding %.2f effectiveness.", armourType, armourEffectiveness.get(armourType).get(woundType)*100f));
                }else{
	                armourEff = ArmourTypes.getArmourEffModifier(armourType, woundType);
                }
                //logger.info("Base effectiveness: "+armourEff);

                // Material effectiveness calculations
                if(materialEffectiveness.containsKey(material) && materialEffectiveness.get(material).containsKey(woundType)){
	                float mod = materialEffectiveness.get(material).get(woundType);
                    //logger.info(String.format("Found armour made of material %s against %s. Multiplying by %.3f", material, woundType, mod));
                    toReturn *= (armourEff*mod);
                }else {
                    toReturn *= armourEff;
                }
                //logger.info("Post-Effectiveness: "+toReturn);

                toReturn *= 1.0f + Armour.getRarityArmourBonus(armour.getRarity());
                toReturn = unarmouredReduction + (float)((double)toReturn * Server.getBuffedQualityEffect(armour.getCurrentQualityLevel() / 100.0f));
            }
        }
        return 1.0f - Math.min(1.0f, toReturn);
	}

	public static float newGetArmourGlanceModifier(int armourType, byte armourMaterial, byte damageType){
	    if(armourGlanceRates.containsKey(armourType) && armourGlanceRates.get(armourType).containsKey(damageType)){
	        float toReturn = armourGlanceRates.get(armourType).get(damageType);
	        //logger.info("Base glance rate: "+toReturn);
	        if(materialGlanceRate.containsKey(armourMaterial) && materialGlanceRate.get(armourMaterial).containsKey(damageType)){
	            toReturn *= materialGlanceRate.get(armourMaterial).get(damageType);
	            //logger.info(String.format("Found material %s, adjusting glance rate by %.2f%%.", armourMaterial, materialGlanceRate.get(armourMaterial).get(damageType)));
            }
            String name = ArmourAssist.getArmourName(armourType);
	        String wound = WoundAssist.getWoundName(damageType);
	        //logger.info(String.format("Glance rate for %s against %s: %.2f", name, wound, toReturn));
            return toReturn;
        }
        logger.warning("Found no glance rate for armour type "+armourType+" against "+damageType);
	    return 0.0f;
    }

    public static float newGetMaterialMovementModifier(byte armourMaterial){
	    if(materialMovementModifier.containsKey(armourMaterial)){
	        //logger.info(String.format("Adjusting movement speed to %.2f%% because of material %s", materialMovementModifier.get(armourMaterial)*100f, armourMaterial));
	        return materialMovementModifier.get(armourMaterial);
        }
	    return 1.0f;
    }
	
	public static void setArmourLimitFactors(){
		try{
			logger.info("Setting armour limit factors");
			for(Armour armour : clothArmour){
				ReflectionUtil.setPrivateField(armour, ReflectionUtil.getField(armour.getClass(), "limitingFactor"), ArmouryMod.clothArmourLimitFactor);
			}
			for(Armour armour : leatherArmour){
				ReflectionUtil.setPrivateField(armour, ReflectionUtil.getField(armour.getClass(), "limitingFactor"), ArmouryMod.leatherArmourLimitFactor);
			}
			for(Armour armour : studdedArmour){
				ReflectionUtil.setPrivateField(armour, ReflectionUtil.getField(armour.getClass(), "limitingFactor"), ArmouryMod.studdedArmourLimitFactor);
			}
			for(Armour armour : chainArmour){
				ReflectionUtil.setPrivateField(armour, ReflectionUtil.getField(armour.getClass(), "limitingFactor"), ArmouryMod.chainArmourLimitFactor);
			}
			for(Armour armour : plateArmour){
				ReflectionUtil.setPrivateField(armour, ReflectionUtil.getField(armour.getClass(), "limitingFactor"), ArmouryMod.plateArmourLimitFactor);
			}
			for(Armour armour : drakeArmour){
				ReflectionUtil.setPrivateField(armour, ReflectionUtil.getField(armour.getClass(), "limitingFactor"), ArmouryMod.drakeArmourLimitFactor);
			}
			for(Armour armour : dragonscaleArmour){
				ReflectionUtil.setPrivateField(armour, ReflectionUtil.getField(armour.getClass(), "limitingFactor"), ArmouryMod.dragonscaleArmourLimitFactor);
			}
		} catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

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

    public static void addArmourDamageReduction(int armourType, float reduction){
	    armourDamageReduction.put(armourType, reduction);
    }

    public static void addArmourEffectiveness(int armourType, String[] split){
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

    public static void addArmourGlanceRate(int armourType, String[] split){
        float rate = Float.parseFloat(split[split.length-1]);
        ArrayList<Byte> woundTypes = getWoundTypes(split);
        for(byte woundType : woundTypes) {
            HashMap<Byte, Float> map;
            if (armourGlanceRates.containsKey(armourType)) {
                map = armourGlanceRates.get(armourType);
            } else {
                map = new HashMap<>();
            }
            map.put(woundType, rate);
            armourGlanceRates.put(armourType, map);
        }
    }

    public static void addMaterialReduction(byte material, float reduction){
	    materialDamageReduction.put(material, reduction);
    }

    public static void addMaterialEffectiveness(byte material, String[] split){
        float reduction = Float.parseFloat(split[split.length-1]);
        ArrayList<Byte> woundTypes = getWoundTypes(split);
        for(byte woundType : woundTypes) {
            HashMap<Byte, Float> map;
            if (materialEffectiveness.containsKey(material)) {
                map = materialEffectiveness.get(material);
            } else {
                map = new HashMap<>();
            }
            map.put(woundType, reduction);
            materialEffectiveness.put(material, map);
        }
    }

    public static void addMaterialGlanceRate(byte material, String[] split){
        float rate = Float.parseFloat(split[split.length-1]);
        ArrayList<Byte> woundTypes = getWoundTypes(split);
        for(byte woundType : woundTypes) {
            HashMap<Byte, Float> map;
            if (materialGlanceRate.containsKey(material)) {
                map = materialGlanceRate.get(material);
            } else {
                map = new HashMap<>();
            }
            map.put(woundType, rate);
            materialGlanceRate.put(material, map);
        }
    }

    public static void addMaterialMovementModifier(byte material, float speed){
        materialMovementModifier.put(material, speed);
    }

    public static void initializeArmourMaps(){
	    // Armour name mapping:
        /*armourNameToType.put("cloth", ArmourTypes.ARMOUR_CLOTH);
        armourNameToType.put("leather", ArmourTypes.ARMOUR_LEATHER);
        armourNameToType.put("studded", ArmourTypes.ARMOUR_STUDDED);
        armourNameToType.put("chain", ArmourTypes.ARMOUR_CHAIN);
        armourNameToType.put("plate", ArmourTypes.ARMOUR_PLATE);
        armourNameToType.put("drake", ArmourTypes.ARMOUR_LEATHER_DRAGON);
        armourNameToType.put("dragonscale", ArmourTypes.ARMOUR_SCALE_DRAGON);
        armourNameToType.put("scale", ArmourTypes.ARMOUR_SCALE);
        armourNameToType.put("ring", ArmourTypes.ARMOUR_RING);
        armourNameToType.put("splint", ArmourTypes.ARMOUR_SPLINT);
        for(String name : armourNameToType.keySet()){
            armourTypeToName.put(armourNameToType.get(name), name);
        }*/

        // Default material movement speed modifiers:
        materialMovementModifier.put(Materials.MATERIAL_ADAMANTINE, 0.95f);
        materialMovementModifier.put(Materials.MATERIAL_COPPER, 0.99f);
        materialMovementModifier.put(Materials.MATERIAL_GLIMMERSTEEL, 0.90f);
        materialMovementModifier.put(Materials.MATERIAL_GOLD, 1.05f);
        materialMovementModifier.put(Materials.MATERIAL_LEAD, 1.025f);
        materialMovementModifier.put(Materials.MATERIAL_SERYLL, 0.90f);
        materialMovementModifier.put(Materials.MATERIAL_SILVER, 1.02f);
        materialMovementModifier.put(Materials.MATERIAL_TIN, 0.98f);
        materialMovementModifier.put(Materials.MATERIAL_ZINC, 0.975f);

        // Defaults
        /*materialDamageReduction.put(Materials.MATERIAL_GOLD, -0.01f);
        materialDamageReduction.put(Materials.MATERIAL_SILVER, -0.0075f);
        materialDamageReduction.put(Materials.MATERIAL_STEEL, 0.025f);
        materialDamageReduction.put(Materials.MATERIAL_COPPER, -0.01f);
        materialDamageReduction.put(Materials.MATERIAL_IRON, 0.0f);
        materialDamageReduction.put(Materials.MATERIAL_LEAD, -0.025f);
        materialDamageReduction.put(Materials.MATERIAL_ZINC, -0.02f);
        materialDamageReduction.put(Materials.MATERIAL_BRASS, 0.01f);
        materialDamageReduction.put(Materials.MATERIAL_BRONZE, 0.01f);
        materialDamageReduction.put(Materials.MATERIAL_TIN, -0.0175f);
        materialDamageReduction.put(Materials.MATERIAL_ADAMANTINE, 0.05f);
        materialDamageReduction.put(Materials.MATERIAL_GLIMMERSTEEL, 0.1f);
        materialDamageReduction.put(Materials.MATERIAL_SERYLL, 0.1f);*/
    }

    private static void addArmour(ArrayList<Armour> typeList, int itemTemplate){
        ItemTemplate it = ItemTemplateFactory.getInstance().getTemplateOrNull(itemTemplate);
        if(it != null){
            armourNameToItemTemplate.put(it.getName(), itemTemplate);
        }
        typeList.add(Armour.getArmour(itemTemplate));
    }

    public static void createArmourTemplateLists(){
        addArmour(clothArmour, ItemList.clothHood);
        addArmour(clothArmour, ItemList.clothSleeve);
        addArmour(clothArmour, ItemList.clothJacket);
        addArmour(clothArmour, ItemList.clothShirt);
        addArmour(clothArmour, ItemList.clothGlove);
        addArmour(clothArmour, ItemList.clothHose);
        addArmour(clothArmour, ItemList.clothShoes);
        addArmour(leatherArmour, ItemList.leatherHat0);
        addArmour(leatherArmour, ItemList.leatherCap);
        addArmour(leatherArmour, ItemList.leatherSleeve);
        addArmour(leatherArmour, ItemList.leatherJacket);
        addArmour(leatherArmour, ItemList.leatherGlove);
        addArmour(leatherArmour, ItemList.leatherHose);
        addArmour(leatherArmour, ItemList.leatherBoot);
        addArmour(studdedArmour, ItemList.studdedLeatherCap);
        addArmour(studdedArmour, ItemList.studdedLeatherSleeve);
        addArmour(studdedArmour, ItemList.studdedLeatherJacket);
        addArmour(studdedArmour, ItemList.studdedLeatherGlove);
        addArmour(studdedArmour, ItemList.studdedLeatherHose);
        addArmour(studdedArmour, ItemList.studdedLeatherBoot);
        addArmour(chainArmour, ItemList.chainCoif);
        addArmour(chainArmour, ItemList.chainSleeve);
        addArmour(chainArmour, ItemList.chainJacket);
        addArmour(chainArmour, ItemList.chainGlove);
        addArmour(chainArmour, ItemList.chainHose);
        addArmour(chainArmour, ItemList.chainBoot);
        addArmour(plateArmour, ItemList.helmetGreat);
        addArmour(plateArmour, ItemList.helmetBasinet);
        addArmour(plateArmour, ItemList.helmetOpen);
        addArmour(plateArmour, ItemList.plateSleeve);
        addArmour(plateArmour, ItemList.plateJacket);
        addArmour(plateArmour, ItemList.plateGauntlet);
        addArmour(plateArmour, ItemList.plateHose);
        addArmour(plateArmour, ItemList.plateBoot);
        addArmour(drakeArmour, ItemList.dragonLeatherCap);
        addArmour(drakeArmour, ItemList.dragonLeatherSleeve);
        addArmour(drakeArmour, ItemList.dragonLeatherJacket);
        addArmour(drakeArmour, ItemList.dragonLeatherGlove);
        addArmour(drakeArmour, ItemList.dragonLeatherHose);
        addArmour(drakeArmour, ItemList.dragonLeatherBoot);
        addArmour(dragonscaleArmour, ItemList.dragonScaleSleeve);
        addArmour(dragonscaleArmour, ItemList.dragonScaleJacket);
        addArmour(dragonscaleArmour, ItemList.dragonScaleGauntlet);
        addArmour(dragonscaleArmour, ItemList.dragonScaleHose);
        addArmour(dragonscaleArmour, ItemList.dragonScaleBoot);
    }

    protected static void loadDefaultGlanceRates(){
        // Initialize glance rates:
        float[][] types = new float[11][];
        types[0] = ArmourTypes.ARMOUR_GLANCE_CRUSH;
        types[1] = ArmourTypes.ARMOUR_GLANCE_SLASH;
        types[2] = ArmourTypes.ARMOUR_GLANCE_PIERCE;
        types[3] = ArmourTypes.ARMOUR_GLANCE_BITE;
        types[4] = ArmourTypes.ARMOUR_GLANCE_BURN;
        types[5] = ArmourTypes.ARMOUR_GLANCE_POISON;
        types[6] = ArmourTypes.ARMOUR_GLANCE_INFECTION;
        types[7] = ArmourTypes.ARMOUR_GLANCE_WATER;
        types[8] = ArmourTypes.ARMOUR_GLANCE_COLD;
        types[9] = ArmourTypes.ARMOUR_GLANCE_INTERNAL;
        types[10] = ArmourTypes.ARMOUR_GLANCE_ACID;
        byte woundType = 0;
        while(woundType < types.length){
            int armourType = 0;
            while(armourType < types[woundType].length){
                HashMap<Byte, Float> map;
                if(armourGlanceRates.containsKey(armourType)){
                    map = armourGlanceRates.get(armourType);
                }else{
                    map = new HashMap<>();
                }
                if(!map.containsKey(woundType)) {
                    map.put(woundType, types[woundType][armourType]);
                    //logger.info(String.format("Putting glance rate for %s against %s to %.2f", armourTypeToName.get(armourType), WoundAssist.woundTypeToName.get(woundType), types[woundType][armourType]));
                }/*else{
                    logger.info(String.format("Glance rate for %s against %s already set to %.2f", armourTypeToName.get(armourType), WoundAssist.woundTypeToName.get(woundType), map.get(woundType)));
                }*/
                armourGlanceRates.put(armourType, map);
                armourType++;
            }
            woundType++;
        }
    }
	
	public static void preInit(){
        try {
	    	ClassPool classPool = HookManager.getInstance().getClassPool();
	    	final Class<ArmourTweaks> thisClass = ArmourTweaks.class;
	    	String replace;

			if(ArmouryMod.enableArmourModifications){
			    Util.setReason("Enable armour damage reduction modifications.");
				CtClass ctArmour = classPool.get("com.wurmonline.server.combat.Armour");
				replace = "{"
						+ "  return "+ArmourTweaks.class.getName()+".newGetArmourModFor($1, $2);"
						+ "}";
				Util.setBodyDeclared(thisClass, ctArmour, "getArmourModFor", replace);

                Util.setReason("Enable armour glance rate modifications.");
                CtClass ctArmourTypes = classPool.get("com.wurmonline.server.combat.ArmourTypes");
                replace = "{"
                        + "  return "+ArmourTweaks.class.getName()+".newGetArmourGlanceModifier($1, $2, $3);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctArmourTypes, "getArmourGlanceModifier", replace);

                Util.setReason("Enable material movement modifications.");
                replace = "{"
                        + "  return "+ArmourTweaks.class.getName()+".newGetMaterialMovementModifier($1);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctArmourTypes, "getMaterialMovementModifier", replace);
	    	}

		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void onItemTemplatesCreated(){
        createArmourTemplateLists();
        loadDefaultGlanceRates();
		try {
			if(ArmouryMod.enableArmourMovementModifications){
				logger.info("Starting armour movement modifications...");
				for(String armourName : ArmouryMod.armourMovement.keySet()){
					int armourTemplate;
					if(armourNameToItemTemplate.containsKey(armourName)){
						armourTemplate = armourNameToItemTemplate.get(armourName);
					}else{
						logger.severe("[ERROR]: Could not edit armour movement for item name \""+armourName+"\". It may be invalid.");
						continue;
					}
					Armour armourToEdit = Armour.getArmour(armourTemplate);
					if(armourToEdit != null){
						float oldValue = ReflectionUtil.getPrivateField(armourToEdit, ReflectionUtil.getField(armourToEdit.getClass(), "movemodifier"));
						ReflectionUtil.setPrivateField(armourToEdit, ReflectionUtil.getField(armourToEdit.getClass(), "movemodifier"), ArmouryMod.armourMovement.get(armourName));
						logger.info("Editing movement modifier for armour \""+armourName+"\": From "+oldValue+" to "+ ArmouryMod.armourMovement.get(armourName));
					}else{
						logger.severe("[ERROR]: Could not edit armour movement for item name \""+armourName+"\". It may be invalid.");
					}
				}
			}
			if(ArmouryMod.enableCustomArmourLimitFactors){
				setArmourLimitFactors();
			}
		} catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
}
