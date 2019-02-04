package mod.sin.armoury;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemSpellEffects;
import com.wurmonline.server.items.Materials;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.shared.constants.Enchants;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import mod.sin.lib.Util;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class MaterialsTweaks {
    public static Logger logger = Logger.getLogger(MaterialsTweaks.class.getName());

    // Material Modifier Maps
    public static HashMap<Byte, Float> materialDamageReduction = new HashMap<>();
    public static HashMap<Byte, Float> materialMovementModifier = new HashMap<>();
    public static HashMap<Byte, Float> materialDamageModifier = new HashMap<>();
    public static HashMap<Byte, Float> materialDecayModifier = new HashMap<>();
    public static HashMap<Byte, Float> materialCreationBonus = new HashMap<>();
    public static HashMap<Byte, Float> materialImproveBonus = new HashMap<>();
    public static HashMap<Byte, Float> materialShatterResistance = new HashMap<>();
    public static HashMap<Byte, Float> materialLockpickBonus = new HashMap<>();
    public static HashMap<Byte, Float> materialAnchorBonus = new HashMap<>();
    public static HashMap<Byte, Float> materialPendulumEffect = new HashMap<>();
    public static HashMap<Byte, Float> materialRepairSpeed = new HashMap<>();
    public static HashMap<Byte, Double> materialBashModifier = new HashMap<>();
    public static HashMap<Byte, Float> materialSpellEffectModifier = new HashMap<>();
    public static HashMap<Byte, HashMap<Byte, Float>> materialSpecificSpellEffectModifier = new HashMap<>();
    public static HashMap<Byte, Double> materialDifficultyModifier = new HashMap<>();
    public static HashMap<Byte, Double> materialActionSpeedModifier = new HashMap<>();

    public static float newGetMaterialDamageReduction(byte material){
        if(materialDamageReduction.containsKey(material)){
            //logger.info(String.format("Modifying damage by %.2f%% due to material type %s.", materialDamageModifier.get(material)*100d, MaterialsTweaks.getMaterialName(material)));
            return materialDamageReduction.get(material);
        }
        return 0.0f;
    }

    public static float newGetMaterialMovementModifier(byte armourMaterial){
        if(materialMovementModifier.containsKey(armourMaterial)){
            //logger.info(String.format("Adjusting movement speed to %.2f%% because of material %s", materialMovementModifier.get(armourMaterial)*100f, armourMaterial));
            return materialMovementModifier.get(armourMaterial);
        }
        return 1.0f;
    }

    public static float newGetMaterialDamageModifier(Item item){
        byte material = item.getMaterial();
        if(materialDamageModifier.containsKey(material)){
            //logger.info(String.format("Modifying damage by %.2f%% due to material type %s.", materialDamageModifier.get(material)*100d, MaterialsTweaks.getMaterialName(material)));
            return materialDamageModifier.get(material);
        }
        return 1.0f;
    }

    public static float newGetMaterialDecayModifier(Item item){
        byte material = item.getMaterial();
        if(materialDecayModifier.containsKey(material)){
            //logger.info(String.format("Modifying decay by %.2f%% due to material type %s.", materialDecayModifier.get(material)*100d, MaterialsTweaks.getMaterialName(material)));
            return materialDecayModifier.get(material);
        }
        return 1.0f;
    }

    public static float newGetMaterialCreationBonus(byte material){
        if(materialCreationBonus.containsKey(material)){
            //logger.info(String.format("Modifying creation bonus by %.2f%% due to material type %s.", materialCreationBonus.get(material)*100d, MaterialsTweaks.getMaterialName(material)));
            return materialCreationBonus.get(material);
        }
        return 0.0f;
    }

    public static float newGetMaterialImpBonus(Item item){
        byte material = item.getMaterial();
        if(materialImproveBonus.containsKey(material)){
            //logger.info(String.format("Modifying improve bonus by %.2f%% due to material type %s.", materialImproveBonus.get(material)*100d, MaterialsTweaks.getMaterialName(material)));
            return materialImproveBonus.get(material);
        }
        return 1.0f;
    }

    public static float newGetMaterialShatterMod(byte material){
        if(materialShatterResistance.containsKey(material)){
            //logger.info(String.format("Modifying shatter resistance by %.2f%% due to material type %s.", materialShatterResistance.get(material)*100d, MaterialsTweaks.getMaterialName(material)));
            return materialShatterResistance.get(material);
        }
        return 0.0f;
    }

    public static float newGetMaterialLockpickBonus(byte material){
        if(materialLockpickBonus.containsKey(material)){
            //logger.info(String.format("Modifying lockpick bonus by %.2f%% due to material type %s.", materialLockpickBonus.get(material)*100d, MaterialsTweaks.getMaterialName(material)));
            return materialLockpickBonus.get(material);
        }
        return 0.0f;
    }

    public static float newGetMaterialAnchorBonus(byte material){
        if(materialAnchorBonus.containsKey(material)){
            //logger.info(String.format("Modifying anchor bonus by %.2f%% due to material type %s.", materialAnchorBonus.get(material)*100d, MaterialsTweaks.getMaterialName(material)));
            return materialAnchorBonus.get(material);
        }
        return 1.0f;
    }

    public static float newGetMaterialPendulumModifier(byte material){
        if(materialPendulumEffect.containsKey(material)){
            //logger.info(String.format("Modifying pendulum effect by %.2f%% due to material type %s.", materialPendulumEffect.get(material)*100d, MaterialsTweaks.getMaterialName(material)));
            return materialPendulumEffect.get(material);
        }
        return 1.0f;
    }

    public static float newGetMaterialRepairTimeMod(Item item){
        byte material = item.getMaterial();
        if(materialRepairSpeed.containsKey(material)){
            //logger.info(String.format("Modifying repair speed by %.2f%% due to material type %s.", materialRepairSpeed.get(material)*100d, MaterialsTweaks.getMaterialName(material)));
            return materialRepairSpeed.get(material);
        }
        return 1.0f;
    }

    public static double newGetMaterialBashModifier(byte material){
        if(materialBashModifier.containsKey(material)){
            //logger.info(String.format("Modifying bash by %.2f%% due to material type %s.", materialBashModifier.get(material)*100d, MaterialsTweaks.getMaterialName(material)));
            return materialBashModifier.get(material);
        }
        return 1.0f;
    }

    public static float newGetBonusForSpellEffect(Item item, byte aEnchantment){
        ItemSpellEffects eff = item.getSpellEffects();
        if (eff != null) {
            SpellEffect skillgain = eff.getSpellEffect(aEnchantment);
            if(skillgain != null) {
                float newPower = skillgain.power;
                byte material = item.getMaterial();
                if(materialSpellEffectModifier.containsKey(material)) {
                    newPower *= materialSpellEffectModifier.get(material);
                    //logger.info(String.format("Modifying spell power by %.2f%% due to material type %s. [%s]", materialSpellEffectModifier.get(material) * 100d, MaterialsTweaks.getMaterialName(material), newPower));
                }
                if(materialSpecificSpellEffectModifier.containsKey(material) && materialSpecificSpellEffectModifier.get(material).containsKey(aEnchantment)) {
                    newPower *= materialSpecificSpellEffectModifier.get(material).get(aEnchantment);
                    //logger.info(String.format("Modifying spell power of enchant %s by %.2f%% due to material type %s. [%s]", aEnchantment, materialSpecificSpellEffectModifier.get(material).get(aEnchantment) * 100d, MaterialsTweaks.getMaterialName(material), newPower));
                }
                return newPower;
            }
        }
        return 0.0f;
    }

    public static double getNewDifficulty(Skill skill, double diff, Item item) {
        if(item != null){
            byte material = item.getMaterial();
            if(materialDifficultyModifier.containsKey(material)){
                diff *= materialDifficultyModifier.get(material);
                //logger.info(String.format("Modifying difficulty by %.2f%% due to material type %s. [%s]", materialDifficultyModifier.get(material) * 100d, MaterialsTweaks.getMaterialName(material), diff));
            }
        }
        return diff;
    }

    public static double getMaterialSpeedModifier(Item item){
        if(item != null){
            byte material = item.getMaterial();
            if(materialActionSpeedModifier.containsKey(material)){
                //logger.info(String.format("Modifying action speed by %.2f%% due to material type %s.", materialActionSpeedModifier.get(material) * 100d, MaterialsTweaks.getMaterialName(material)));
                return materialActionSpeedModifier.get(material);
            }
        }
        return 1.0f;
    }

    public static void addMaterialDamageReduction(byte material, float reduction){
        materialDamageReduction.put(material, reduction);
    }

    public static void addMaterialMovementModifier(byte material, float speed){
        materialMovementModifier.put(material, speed);
    }

    public static void addMaterialDamageModifier(byte material, float mult){
        materialDamageModifier.put(material, mult);
    }

    public static void addMaterialDecayModifier(byte material, float mult){
        materialDecayModifier.put(material, mult);
    }

    public static void addMaterialCreationBonus(byte material, float bonus){
        materialCreationBonus.put(material, bonus);
    }

    public static void addMaterialImproveBonus(byte material, float bonus){
        materialImproveBonus.put(material, bonus);
    }

    public static void addMaterialShatterResistance(byte material, float resistance){
        materialShatterResistance.put(material, resistance);
    }

    public static void addMaterialLockpickBonus(byte material, float bonus){
        materialLockpickBonus.put(material, bonus);
    }

    public static void addMaterialAnchorBonus(byte material, float bonus){
        materialAnchorBonus.put(material, bonus);
    }

    public static void addMaterialPendulumEffect(byte material, float bonus){
        materialPendulumEffect.put(material, bonus);
    }

    public static void addMaterialRepairSpeed(byte material, float mult){
        materialRepairSpeed.put(material, mult);
    }

    public static void addMaterialBashModifier(byte material, double mult){
        materialBashModifier.put(material, mult);
    }

    public static void addMaterialSpellEffectModifier(byte material, float mult){
        materialSpellEffectModifier.put(material, mult);
    }

    public static void addMaterialActionSpeedModifier(byte material, double mult){
        materialActionSpeedModifier.put(material, mult);
    }

    protected static ArrayList<Byte> getEnchants(String[] split){
        ArrayList<Byte> enchants = new ArrayList<>();
        int i = 0;
        while(i < split.length-1){
            if(split[i].equalsIgnoreCase("damage")){
                enchants.add(Enchants.BUFF_BLOODTHIRST);
                enchants.add(Enchants.BUFF_FLAMING_AURA);
                enchants.add(Enchants.BUFF_FROSTBRAND);
                enchants.add(Enchants.BUFF_ROTTING_TOUCH);
                enchants.add(Enchants.BUFF_VENOM);
            }else if(split[i].equalsIgnoreCase("skilling")){
                enchants.add(Enchants.BUFF_BLESSINGDARK);
                enchants.add(Enchants.BUFF_WIND_OF_AGES);
                enchants.add(Enchants.BUFF_CIRCLE_CUNNING);
                enchants.add((byte) 120);
            }else if(split[i].equalsIgnoreCase("armour")){
                enchants.add(Enchants.BUFF_SHARED_PAIN);
                enchants.add(Enchants.BUFF_WEBARMOUR);
            }else{
                enchants.add(Byte.parseByte(split[i]));
            }
            i++;
        }
        return enchants;
    }

    public static void addMaterialSpecificSpellEffectModifier(byte material, String[] split){
        float modifier = Float.parseFloat(split[split.length-1]);
        ArrayList<Byte> enchants = getEnchants(split);
        for(byte enchant : enchants) {
            HashMap<Byte, Float> map;
            if (materialSpecificSpellEffectModifier.containsKey(material)) {
                map = materialSpecificSpellEffectModifier.get(material);
            } else {
                map = new HashMap<>();
            }
            map.put(enchant, modifier);
            materialSpecificSpellEffectModifier.put(material, map);
        }
    }

    public static void addMaterialDifficultyModifier(byte material, double mult){
        materialDifficultyModifier.put(material, mult);
    }

    public static String getMaterialName(byte material){
        String name = Materials.convertMaterialByteIntoString(material);
        if(!name.equals("")){
            return name;
        }
        return String.valueOf(material);
    }

    protected static void initializeMaterialMaps(){
        // Material damage reduction modifiers
        materialDamageReduction.put(Materials.MATERIAL_GOLD, -0.01f);
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
        materialDamageReduction.put(Materials.MATERIAL_GLIMMERSTEEL, 0.05f);
        materialDamageReduction.put(Materials.MATERIAL_SERYLL, 0.05f);

        // Material movement modifiers
        materialMovementModifier.put(Materials.MATERIAL_ADAMANTINE, 0.95f);
        materialMovementModifier.put(Materials.MATERIAL_COPPER, 0.99f);
        materialMovementModifier.put(Materials.MATERIAL_GLIMMERSTEEL, 0.90f);
        materialMovementModifier.put(Materials.MATERIAL_GOLD, 1.05f);
        materialMovementModifier.put(Materials.MATERIAL_LEAD, 1.025f);
        materialMovementModifier.put(Materials.MATERIAL_SERYLL, 0.90f);
        materialMovementModifier.put(Materials.MATERIAL_SILVER, 1.02f);
        materialMovementModifier.put(Materials.MATERIAL_TIN, 0.98f);
        materialMovementModifier.put(Materials.MATERIAL_ZINC, 0.975f);

        // Material damage taken modifiers
        materialDamageModifier.put(Materials.MATERIAL_ADAMANTINE, 0.40f);
        materialDamageModifier.put(Materials.MATERIAL_BRASS, 0.95f);
        materialDamageModifier.put(Materials.MATERIAL_BRONZE, 0.90f);
        materialDamageModifier.put(Materials.MATERIAL_COPPER, 1.15f);
        materialDamageModifier.put(Materials.MATERIAL_GLIMMERSTEEL, 0.60f);
        materialDamageModifier.put(Materials.MATERIAL_GOLD, 1.20f);
        materialDamageModifier.put(Materials.MATERIAL_LEAD, 1.30f);
        materialDamageModifier.put(Materials.MATERIAL_SERYLL, 0.50f);
        materialDamageModifier.put(Materials.MATERIAL_SILVER, 1.025f);
        materialDamageModifier.put(Materials.MATERIAL_STEEL, 0.80f);
        materialDamageModifier.put(Materials.MATERIAL_TIN, 1.20f);
        materialDamageModifier.put(Materials.MATERIAL_ZINC, 1.25f);

        // Material decay taken modifiers
        materialDecayModifier.put(Materials.MATERIAL_ADAMANTINE, 0.40f);
        materialDecayModifier.put(Materials.MATERIAL_BRASS, 0.95f);
        materialDecayModifier.put(Materials.MATERIAL_BRONZE, 0.85f);
        materialDecayModifier.put(Materials.MATERIAL_COPPER, 0.95f);
        materialDecayModifier.put(Materials.MATERIAL_GLIMMERSTEEL, 0.60f);
        materialDecayModifier.put(Materials.MATERIAL_GOLD, 0.40f);
        materialDecayModifier.put(Materials.MATERIAL_LEAD, 0.80f);
        materialDecayModifier.put(Materials.MATERIAL_SERYLL, 0.50f);
        materialDecayModifier.put(Materials.MATERIAL_SILVER, 0.70f);
        materialDecayModifier.put(Materials.MATERIAL_STEEL, 0.70f);
        materialDecayModifier.put(Materials.MATERIAL_TIN, 0.925f);
        materialDecayModifier.put(Materials.MATERIAL_ZINC, 1.20f);

        // Material creation bonus
        materialCreationBonus.put(Materials.MATERIAL_BRASS, 0.1f);
        materialCreationBonus.put(Materials.MATERIAL_BRONZE, 0.05f);
        materialCreationBonus.put(Materials.MATERIAL_LEAD, 0.05f);
        materialCreationBonus.put(Materials.MATERIAL_TIN, 0.05f);

        // Material improve bonus
        materialImproveBonus.put(Materials.MATERIAL_BRASS, 1.025f);
        materialImproveBonus.put(Materials.MATERIAL_COPPER, 1.05f);
        materialImproveBonus.put(Materials.MATERIAL_LEAD, 1.10f);
        materialImproveBonus.put(Materials.MATERIAL_TIN, 1.025f);
        materialImproveBonus.put(Materials.MATERIAL_ZINC, 1.075f);

        // Material shatter resistance
        materialShatterResistance.put(Materials.MATERIAL_ADAMANTINE, 0.15f);
        materialShatterResistance.put(Materials.MATERIAL_GLIMMERSTEEL, 0.25f);
        materialShatterResistance.put(Materials.MATERIAL_GOLD, 0.20f);
        materialShatterResistance.put(Materials.MATERIAL_SERYLL, 1.00f);
        materialShatterResistance.put(Materials.MATERIAL_SILVER, 0.10f);

        // Material lockpick bonus
        materialLockpickBonus.put(Materials.MATERIAL_ADAMANTINE, 0.05f);
        materialLockpickBonus.put(Materials.MATERIAL_COPPER, -0.05f);
        materialLockpickBonus.put(Materials.MATERIAL_GLIMMERSTEEL, 0.05f);
        materialLockpickBonus.put(Materials.MATERIAL_GOLD, -0.025f);
        materialLockpickBonus.put(Materials.MATERIAL_LEAD, -0.05f);
        materialLockpickBonus.put(Materials.MATERIAL_SERYLL, 0.05f);
        materialLockpickBonus.put(Materials.MATERIAL_SILVER, 0.025f);
        materialLockpickBonus.put(Materials.MATERIAL_STEEL, 0.05f);
        materialLockpickBonus.put(Materials.MATERIAL_TIN, -0.025f);
        materialLockpickBonus.put(Materials.MATERIAL_ZINC, -0.025f);

        // Material anchor bonus
        materialAnchorBonus.put(Materials.MATERIAL_ADAMANTINE, 1.50f);
        materialAnchorBonus.put(Materials.MATERIAL_BRASS, 0.90f);
        materialAnchorBonus.put(Materials.MATERIAL_BRONZE, 0.85f);
        materialAnchorBonus.put(Materials.MATERIAL_COPPER, 0.95f);
        materialAnchorBonus.put(Materials.MATERIAL_GLIMMERSTEEL, 1.25f);
        materialAnchorBonus.put(Materials.MATERIAL_GOLD, 1.70f);
        materialAnchorBonus.put(Materials.MATERIAL_IRON, 0.85f);
        materialAnchorBonus.put(Materials.MATERIAL_LEAD, 1.00f);
        materialAnchorBonus.put(Materials.MATERIAL_SERYLL, 1.25f);
        materialAnchorBonus.put(Materials.MATERIAL_SILVER, 0.975f);
        materialAnchorBonus.put(Materials.MATERIAL_STEEL, 0.85f);
        materialAnchorBonus.put(Materials.MATERIAL_TIN, 0.80f);
        materialAnchorBonus.put(Materials.MATERIAL_ZINC, 0.75f);

        // Material pendulum effect
        materialPendulumEffect.put(Materials.MATERIAL_ADAMANTINE, 1.15f);
        materialPendulumEffect.put(Materials.MATERIAL_BRASS, 1.025f);
        materialPendulumEffect.put(Materials.MATERIAL_BRONZE, 1.05f);
        materialPendulumEffect.put(Materials.MATERIAL_COPPER, 0.95f);
        materialPendulumEffect.put(Materials.MATERIAL_GLIMMERSTEEL, 1.20f);
        materialPendulumEffect.put(Materials.MATERIAL_GOLD, 1.10f);
        materialPendulumEffect.put(Materials.MATERIAL_LEAD, 0.90f);
        materialPendulumEffect.put(Materials.MATERIAL_SERYLL, 1.25f);
        materialPendulumEffect.put(Materials.MATERIAL_SILVER, 1.05f);
        materialPendulumEffect.put(Materials.MATERIAL_STEEL, 1.025f);
        materialPendulumEffect.put(Materials.MATERIAL_TIN, 0.95f);
        materialPendulumEffect.put(Materials.MATERIAL_ZINC, 0.95f);

        // Material repair speed
        materialRepairSpeed.put(Materials.MATERIAL_ADAMANTINE, 0.90f);
        materialRepairSpeed.put(Materials.MATERIAL_BRONZE, 0.975f);
        materialRepairSpeed.put(Materials.MATERIAL_COPPER, 1.075f);
        materialRepairSpeed.put(Materials.MATERIAL_GLIMMERSTEEL, 0.95f);
        materialRepairSpeed.put(Materials.MATERIAL_GOLD, 1.05f);
        materialRepairSpeed.put(Materials.MATERIAL_LEAD, 1.10f);
        materialRepairSpeed.put(Materials.MATERIAL_SERYLL, 0.95f);
        materialRepairSpeed.put(Materials.MATERIAL_STEEL, 0.975f);
        materialRepairSpeed.put(Materials.MATERIAL_TIN, 1.025f);
        materialRepairSpeed.put(Materials.MATERIAL_ZINC, 1.05f);

        // Material bash modifier
        materialBashModifier.put(Materials.MATERIAL_ADAMANTINE, 1.075d);
        materialBashModifier.put(Materials.MATERIAL_BRASS, 1.05d);
        materialBashModifier.put(Materials.MATERIAL_BRONZE, 1.025d);
        materialBashModifier.put(Materials.MATERIAL_COPPER, 0.90d);
        materialBashModifier.put(Materials.MATERIAL_GLIMMERSTEEL, 1.10d);
        materialBashModifier.put(Materials.MATERIAL_GOLD, 1.10d);
        materialBashModifier.put(Materials.MATERIAL_LEAD, 1.20d);
        materialBashModifier.put(Materials.MATERIAL_SERYLL, 1.075d);
        materialBashModifier.put(Materials.MATERIAL_SILVER, 1.10d);
        materialBashModifier.put(Materials.MATERIAL_STEEL, 1.05d);
        materialBashModifier.put(Materials.MATERIAL_TIN, 0.90d);
        materialBashModifier.put(Materials.MATERIAL_ZINC, 0.85d);
    }

    public static void preInit(){
        try {
            ClassPool classPool = HookManager.getInstance().getClassPool();
            final Class<MaterialsTweaks> thisClass = MaterialsTweaks.class;
            String replace;

            if(ArmouryModMain.enableItemMaterialChanges){
                Util.setReason("Enable material damage reduction modifications.");
                CtClass ctArmourTemplate = classPool.get("com.wurmonline.server.combat.ArmourTemplate");
                replace = "{"
                        + "  return "+MaterialsTweaks.class.getName()+".newGetMaterialDamageReduction($1);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctArmourTemplate, "getArmourMatBonus", replace);

                Util.setReason("Enable material movement modifications.");
                replace = "{"
                        + "  return "+MaterialsTweaks.class.getName()+".newGetMaterialMovementModifier($1);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctArmourTemplate, "getMaterialMovementModifier", replace);

                Util.setReason("Enable material damage taken modifications.");
                CtClass ctItem = classPool.get("com.wurmonline.server.items.Item");
                replace = "{"
                        + "  return "+MaterialsTweaks.class.getName()+".newGetMaterialDamageModifier($0);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctItem, "getMaterialDamageModifier", replace);

                Util.setReason("Enable material decay modifications.");
                replace = "{"
                        + "  return "+MaterialsTweaks.class.getName()+".newGetMaterialDecayModifier($0);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctItem, "getMaterialDecayModifier", replace);

                Util.setReason("Enable material creation bonus modifications.");
                replace = "{"
                        + "  return "+MaterialsTweaks.class.getName()+".newGetMaterialCreationBonus($1);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctItem, "getMaterialCreationBonus", replace);

                Util.setReason("Enable material improvement bonus modifications.");
                replace = "{"
                        + "  return "+MaterialsTweaks.class.getName()+".newGetMaterialImpBonus($0);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctItem, "getMaterialImpBonus", replace);

                Util.setReason("Enable material shatter resistance modifications.");
                CtClass ctSpell = classPool.get("com.wurmonline.server.spells.Spell");
                replace = "{"
                        + "  return "+MaterialsTweaks.class.getName()+".newGetMaterialShatterMod($1);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctSpell, "getMaterialShatterMod", replace);

                Util.setReason("Enable material lockpick bonus modifications.");
                replace = "{"
                        + "  return "+MaterialsTweaks.class.getName()+".newGetMaterialLockpickBonus($1);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctItem, "getMaterialLockpickBonus", replace);

                Util.setReason("Enable material anchor bonus modifications.");
                replace = "{"
                        + "  return "+MaterialsTweaks.class.getName()+".newGetMaterialAnchorBonus($1);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctItem, "getMaterialAnchorBonus", replace);

                Util.setReason("Enable material pendulum effect modifications.");
                CtClass ctLocates = classPool.get("com.wurmonline.server.behaviours.Locates");
                replace = "{"
                        + "  return "+MaterialsTweaks.class.getName()+".newGetMaterialPendulumModifier($1);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctLocates, "getMaterialPendulumModifier", replace);

                Util.setReason("Enable material repair speed modifications.");
                replace = "{"
                        + "  return "+MaterialsTweaks.class.getName()+".newGetMaterialRepairTimeMod($0);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctItem, "getMaterialRepairTimeMod", replace);

                Util.setReason("Enable material bash modifications.");
                CtClass ctWeapon = classPool.get("com.wurmonline.server.combat.Weapon");
                replace = "{"
                        + "  return "+MaterialsTweaks.class.getName()+".newGetMaterialBashModifier($1);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctWeapon, "getMaterialBashModifier", replace);

                Util.setReason("Enable material spell power modifications.");
                replace = "{"
                        + "  return "+MaterialsTweaks.class.getName()+".newGetBonusForSpellEffect($0, $1);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctItem, "getBonusForSpellEffect", replace);

                Util.setReason("Enable material skill difficulty modifications.");
                CtClass ctSkill = classPool.get("com.wurmonline.server.skills.Skill");
                replace = "$1 = "+MaterialsTweaks.class.getName()+".getNewDifficulty(this, $1, $2);";
                Util.insertBeforeDeclared(thisClass, ctSkill, "checkAdvance", replace);

                Util.setReason("Enable material action speed modifications.");
                CtClass ctActions = classPool.get("com.wurmonline.server.behaviours.Actions");
                replace = "$_ = $proceed($$)*"+MaterialsTweaks.class.getName()+".getMaterialSpeedModifier(source);";
                Util.instrumentDeclared(thisClass, ctActions, "getStandardActionTime", "getStaminaModiferFor", replace);
                Util.instrumentDeclared(thisClass, ctActions, "getQuickActionTime", "getStaminaModiferFor", replace);
                Util.instrumentDeclared(thisClass, ctActions, "getVariableActionTime", "getStaminaModiferFor", replace);
                Util.instrumentDeclared(thisClass, ctActions, "getSlowActionTime", "getStaminaModiferFor", replace);
                Util.instrumentDeclared(thisClass, ctActions, "getPickActionTime", "getStaminaModiferFor", replace);
                replace = "$_ = $proceed($$)*"+MaterialsTweaks.class.getName()+".getMaterialSpeedModifier(realSource);";
                Util.instrumentDeclared(thisClass, ctActions, "getItemCreationTime", "getStaminaModiferFor", replace);
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }
}
