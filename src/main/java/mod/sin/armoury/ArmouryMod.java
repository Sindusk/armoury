package mod.sin.armoury;

import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.items.Materials;
import mod.sin.lib.ArmourAssist;
import mod.sin.lib.Prop;
import mod.sin.lib.WoundAssist;
import org.gotti.wurmunlimited.modloader.interfaces.*;

import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArmouryMod
implements WurmServerMod, Configurable, PreInitable, ItemTemplatesCreatedListener, ServerStartedListener {
	public static Logger logger = Logger.getLogger(ArmouryMod.class.getName());

    // Configuration options
	public static boolean enableNonPlayerCrits = true;
	public static boolean fixArmourLimitBuffBug = true;
	public static boolean fixArmourLimitSpellEffect = true;

	// -- Armour configuration -- //
	public static boolean enableArmourModifications = true;
	// Armour modifiers
	//public static float adamantineMaterialMod = 0.05f;
	//public static float glimmersteelMaterialMod = 0.1f;
	//public static float seryllMaterialMod = 0.1f;
	// Armour limit factors
	public static boolean enableCustomArmourLimitFactors = true;
	public static float clothArmourLimitFactor = 0.3f;
	public static float leatherArmourLimitFactor = 0.3f;
	public static float studdedArmourLimitFactor = 0.0f;
	public static float chainArmourLimitFactor = -0.15f;
	public static float plateArmourLimitFactor = -0.3f;
	public static float drakeArmourLimitFactor = -0.3f;
	public static float dragonscaleArmourLimitFactor = -0.3f;
	public static HashMap<Integer, Float> armourReductionOverride = new HashMap<>();
	// Armour movement
	public static boolean enableArmourMovementModifications = true;
	public static HashMap<String, Float> armourMovement = new HashMap<>();

	// - Shield configuration -- //
	public static boolean enableShieldDamageEnchants = true;
	public static boolean enableShieldSpeedEnchants = true;
	
	// -- Weapon configuration -- //
	public static float minimumSwingTime = 3.0f;
	public static boolean raresReduceSwingTime = true;
	public static float rareSwingSpeedReduction = 0.2f;
	public static boolean fixSavedSwingTimer = true;
	public static boolean betterDualWield = true; // HIGHLY EXPERIMENTAL
    public static boolean enableWeaponMaterialChanges = true;
    public static boolean enableItemMaterialChanges = true;
	// Weapon variable changes
	public static HashMap<Integer, Float> weaponDamage = new HashMap<>();
	public static HashMap<Integer, Float> weaponSpeed = new HashMap<>();
	public static HashMap<Integer, Float> weaponCritChance = new HashMap<>();
	public static HashMap<Integer, Integer> weaponReach = new HashMap<>();
	public static HashMap<Integer, Integer> weaponWeightGroup = new HashMap<>();
	public static HashMap<Integer, Float> weaponParryPercent = new HashMap<>();
	public static HashMap<Integer, Double> weaponSkillPenalty = new HashMap<>();

    public static byte parseMaterialType(String str){
	    byte mat = Materials.convertMaterialStringIntoByte(str);
	    if(mat > 0){
	        return mat;
        }
        return Byte.parseByte(str);
    }

    @Override
	public void configure(Properties properties) {
		logger.info("Beginning configuration...");
		Prop.properties = properties;

		// Initialization sequences
        MaterialTweaks.initializeMaterialMaps();
        ArmourTweaks.initializeArmourMaps();
        WeaponTweaks.initializeWeaponMaps();

		// Base configuration options
        enableNonPlayerCrits = Prop.getBooleanProperty("enableNonPlayerCrits", enableNonPlayerCrits);
        //enableNonPlayerCrits = Boolean.parseBoolean(properties.getProperty("enableNonPlayerCrits", Boolean.toString(enableNonPlayerCrits)));
        fixArmourLimitBuffBug = Prop.getBooleanProperty("fixArmourLimitBuffBug", fixArmourLimitBuffBug);
        //fixArmourLimitBuffBug = Boolean.parseBoolean(properties.getProperty("fixArmourLimitBuffBug", Boolean.toString(fixArmourLimitBuffBug)));
        fixArmourLimitSpellEffect = Prop.getBooleanProperty("fixArmourLimitSpellEffect", fixArmourLimitSpellEffect);
        //fixArmourLimitSpellEffect = Boolean.parseBoolean(properties.getProperty("fixArmourLimitSpellEffect", Boolean.toString(fixArmourLimitSpellEffect)));
        enableArmourModifications = Prop.getBooleanProperty("enableArmourReductionModifications", enableArmourModifications);
        //enableArmourModifications = Boolean.parseBoolean(properties.getProperty("enableArmourModifications", Boolean.toString(enableArmourModifications)));
        ArmourTweaks.unarmouredReduction = Prop.getFloatProperty("unarmouredReduction", ArmourTweaks.unarmouredReduction);
        /*if(enableArmourModifications){
            ArmourTweaks.configure();
        }*/
    	//adamantineMaterialMod = Float.parseFloat(properties.getProperty("adamantineMaterialMod", Float.toString(adamantineMaterialMod)));
    	//glimmersteelMaterialMod = Float.parseFloat(properties.getProperty("glimmersteelMaterialMod", Float.toString(glimmersteelMaterialMod)));
    	//seryllMaterialMod = Float.parseFloat(properties.getProperty("seryllMaterialMod", Float.toString(seryllMaterialMod)));
    	// Armour limit factors
        enableCustomArmourLimitFactors = Boolean.parseBoolean(properties.getProperty("enableCustomArmourLimitFactors", Boolean.toString(enableCustomArmourLimitFactors)));
    	clothArmourLimitFactor = Float.parseFloat(properties.getProperty("clothArmourLimitFactor", Float.toString(clothArmourLimitFactor)));
    	leatherArmourLimitFactor = Float.parseFloat(properties.getProperty("leatherArmourLimitFactor", Float.toString(leatherArmourLimitFactor)));
    	studdedArmourLimitFactor = Float.parseFloat(properties.getProperty("studdedArmourLimitFactor", Float.toString(studdedArmourLimitFactor)));
    	chainArmourLimitFactor = Float.parseFloat(properties.getProperty("chainArmourLimitFactor", Float.toString(chainArmourLimitFactor)));
    	plateArmourLimitFactor = Float.parseFloat(properties.getProperty("plateArmourLimitFactor", Float.toString(plateArmourLimitFactor)));
    	drakeArmourLimitFactor = Float.parseFloat(properties.getProperty("drakeArmourLimitFactor", Float.toString(drakeArmourLimitFactor)));
    	dragonscaleArmourLimitFactor = Float.parseFloat(properties.getProperty("dragonscaleArmourLimitFactor", Float.toString(dragonscaleArmourLimitFactor)));
    	// Armour movement modifiers
        enableArmourMovementModifications = Boolean.parseBoolean(properties.getProperty("enableArmourMovementModifications", Boolean.toString(enableArmourMovementModifications)));
        // Shield configuration
        enableShieldDamageEnchants = Boolean.parseBoolean(properties.getProperty("enableShieldDamageEnchants", Boolean.toString(enableShieldDamageEnchants)));
        // Weapon configuration
        minimumSwingTime = Float.parseFloat(properties.getProperty("minimumSwingTime", Float.toString(minimumSwingTime)));
        raresReduceSwingTime = Boolean.parseBoolean(properties.getProperty("raresReduceSwingTime", Boolean.toString(raresReduceSwingTime)));
    	rareSwingSpeedReduction = Float.parseFloat(properties.getProperty("rareSwingSpeedReduction", Float.toString(rareSwingSpeedReduction)));
        fixSavedSwingTimer = Boolean.parseBoolean(properties.getProperty("fixSavedSwingTimer", Boolean.toString(fixSavedSwingTimer)));
        betterDualWield = Boolean.parseBoolean(properties.getProperty("betterDualWield", Boolean.toString(betterDualWield)));
        enableWeaponMaterialChanges = Prop.getBooleanProperty("enableWeaponMaterialChanges", enableWeaponMaterialChanges);
        enableItemMaterialChanges = Prop.getBooleanProperty("enableItemMaterialChanges", enableItemMaterialChanges);
    	for (String name : properties.stringPropertyNames()) {
            try {
                String value = properties.getProperty(name);
                switch (name) {
                    case "debug":
                    case "classname":
                    case "classpath":
                    case "sharedClassLoader":
                    case "depend.import":
                    case "depend.suggests":
                        break; //ignore
                    default:
                    	if (name.startsWith("armourMovement")) {
                        	String[] split = value.split(",");
                            String armourName = split[0];
                            float newVal = Float.parseFloat(split[1]);
                            armourMovement.put(armourName, newVal);
                        } else if (name.startsWith("armourReductionOverride")) {
                            String[] split = value.split(",");
                            int armourId = Integer.parseInt(split[0]);
                            float reductionValue = Float.parseFloat(split[1]);
                            armourReductionOverride.put(armourId, reductionValue);
                        } else if (name.startsWith("armourDamageReduction")) {
                            String[] split = value.split(",");
                            int armourId = ArmourAssist.getArmourType(split[0]);
                            float reductionValue = Float.parseFloat(split[1]);
                            ArmourTweaks.addArmourDamageReduction(armourId, reductionValue);
                        } else if (name.startsWith("armourEffectiveness")) {
                            String[] split = value.split(";");
                            int armourType = ArmourAssist.getArmourType(split[0]);
                            String[] split2 = split[1].split(",");
                            ArmourTweaks.addArmourEffectiveness(armourType, split2);
                        } else if (name.startsWith("armourGlanceRate")) {
                            String[] split = value.split(";");
                            int armourType = ArmourAssist.getArmourType(split[0]);
                            String[] split2 = split[1].split(",");
                            ArmourTweaks.addArmourGlanceRate(armourType, split2);
                        } else if (name.startsWith("materialDamageReduction")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float reduction = Float.parseFloat(split[1]);
                            ArmourTweaks.addMaterialReduction(material, reduction);
                        } else if (name.startsWith("materialEffectiveness")) {
                            String[] split = value.split(";");
                            byte material = parseMaterialType(split[0]);
                            String[] split2 = split[1].split(",");
                            ArmourTweaks.addMaterialEffectiveness(material, split2);
                        } else if (name.startsWith("materialGlanceRate")) {
                            String[] split = value.split(";");
                            byte material = parseMaterialType(split[0]);
                            String[] split2 = split[1].split(",");
                            ArmourTweaks.addMaterialGlanceRate(material, split2);
                        } else if (name.startsWith("materialMovementModifier")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float speed = Float.parseFloat(split[1]);
                            ArmourTweaks.addMaterialMovementModifier(material, speed);
                        } else if (name.startsWith("materialWeaponDamage")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            double mult = Double.parseDouble(split[1]);
                            WeaponTweaks.addMaterialWeaponDamage(material, mult);
                        } else if (name.startsWith("materialWeaponSpeed")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float mult = Float.parseFloat(split[1]);
                            WeaponTweaks.addMaterialWeaponSpeed(material, mult);
                        } else if (name.startsWith("materialWeaponParry")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float mult = Float.parseFloat(split[1]);
                            WeaponTweaks.addMaterialWeaponParry(material, mult);
                        } else if (name.startsWith("materialWeaponArmourDamage")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            double mult = Double.parseDouble(split[1]);
                            WeaponTweaks.addMaterialWeaponArmourDamage(material, mult);
                        } else if (name.startsWith("materialDamageModifier")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float mult = Float.parseFloat(split[1]);
                            MaterialTweaks.addMaterialDamageModifier(material, mult);
                        } else if (name.startsWith("materialDecayModifier")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float mult = Float.parseFloat(split[1]);
                            MaterialTweaks.addMaterialDecayModifier(material, mult);
                        } else if (name.startsWith("materialCreationBonus")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float bonus = Float.parseFloat(split[1]);
                            MaterialTweaks.addMaterialCreationBonus(material, bonus);
                        } else if (name.startsWith("materialImproveBonus")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float bonus = Float.parseFloat(split[1]);
                            MaterialTweaks.addMaterialImproveBonus(material, bonus);
                        } else if (name.startsWith("materialShatterResistance")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float resistance = Float.parseFloat(split[1]);
                            MaterialTweaks.addMaterialShatterResistance(material, resistance);
                        } else if (name.startsWith("materialLockpickBonus")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float bonus = Float.parseFloat(split[1]);
                            MaterialTweaks.addMaterialLockpickBonus(material, bonus);
                        } else if (name.startsWith("materialAnchorBonus")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float bonus = Float.parseFloat(split[1]);
                            MaterialTweaks.addMaterialAnchorBonus(material, bonus);
                        } else if (name.startsWith("materialPendulumEffect")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float bonus = Float.parseFloat(split[1]);
                            MaterialTweaks.addMaterialPendulumEffect(material, bonus);
                        } else if (name.startsWith("materialRepairSpeed")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float mult = Float.parseFloat(split[1]);
                            MaterialTweaks.addMaterialRepairSpeed(material, mult);
                        } else if (name.startsWith("materialBashModifier")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            double mult = Double.parseDouble(split[1]);
                            MaterialTweaks.addMaterialBashModifier(material, mult);
                        } else if (name.startsWith("materialSpellEffectModifier")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float mult = Float.parseFloat(split[1]);
                            MaterialTweaks.addMaterialSpellEffectModifier(material, mult);
                        } else if (name.startsWith("materialSpecificSpellEffectModifier")) {
                            String[] split = value.split(";");
                            byte material = parseMaterialType(split[0]);
                            String[] split2 = split[1].split(",");
                            MaterialTweaks.addMaterialSpecificSpellEffectModifier(material, split2);
                        } else if (name.startsWith("materialDifficultyModifier")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            double mult = Double.parseDouble(split[1]);
                            MaterialTweaks.addMaterialDifficultyModifier(material, mult);
                        } else if (name.startsWith("materialActionSpeedModifier")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            double mult = Double.parseDouble(split[1]);
                            MaterialTweaks.addMaterialActionSpeedModifier(material, mult);
                        } else if (name.startsWith("weaponDamage")) {
                        	String[] split = value.split(",");
                            int weaponId = Integer.parseInt(split[0]);
                            float newVal = Float.parseFloat(split[1]);
                            weaponDamage.put(weaponId, newVal);
                        } else if (name.startsWith("weaponSpeed")) {
                        	String[] split = value.split(",");
                            int weaponId = Integer.parseInt(split[0]);
                            float newVal = Float.parseFloat(split[1]);
                            weaponSpeed.put(weaponId, newVal);
                        } else if (name.startsWith("weaponCritChance")) {
                        	String[] split = value.split(",");
                            int weaponId = Integer.parseInt(split[0]);
                            float newVal = Float.parseFloat(split[1]);
                            weaponCritChance.put(weaponId, newVal);
                        } else if (name.startsWith("weaponReach")) {
                        	String[] split = value.split(",");
                            int weaponId = Integer.parseInt(split[0]);
                            int newVal = Integer.parseInt(split[1]);
                            weaponReach.put(weaponId, newVal);
                        } else if (name.startsWith("weaponWeightGroup")) {
                        	String[] split = value.split(",");
                            int weaponId = Integer.parseInt(split[0]);
                            int newVal = Integer.parseInt(split[1]);
                            weaponWeightGroup.put(weaponId, newVal);
                        } else if (name.startsWith("weaponParryPercent")) {
                        	String[] split = value.split(",");
                            int weaponId = Integer.parseInt(split[0]);
                            float newVal = Float.parseFloat(split[1]);
                            weaponParryPercent.put(weaponId, newVal);
                        } else if (name.startsWith("weaponSkillPenalty")) {
                        	String[] split = value.split(",");
                            int weaponId = Integer.parseInt(split[0]);
                            double newVal = Double.parseDouble(split[1]);
                            weaponSkillPenalty.put(weaponId, newVal);
                        } else {
                            logger.warning("Unknown config property: " + name);
                        }
                }
            } catch (Exception e) {
                logger.severe("Error processing property " + name);
                e.printStackTrace();
            }
        }
        // Print values of main.java.armoury.mod configuration
        logger.info(" -- Mod Configuration -- ");
        logger.log(Level.INFO, "enableNonPlayerCrits: " + enableNonPlayerCrits);
        logger.log(Level.INFO, "fixArmourLimitBuffBug: " + fixArmourLimitBuffBug);
        logger.log(Level.INFO, "fixArmourLimitSpellEffect: " + fixArmourLimitSpellEffect);
        logger.info(" -- Armour Configuration -- ");
        logger.log(Level.INFO, "enableArmourModifications: " + enableArmourModifications);
        if(enableArmourModifications){
            logger.log(Level.INFO, "unarmouredReduction: " + ArmourTweaks.unarmouredReduction);
            logger.info("> Armour Base DR Settings <");
            for(int armourType : ArmourTweaks.armourDamageReduction.keySet()){
                String name = ArmourAssist.getArmourName(armourType);
                /*String name = String.valueOf(armourType);
                if(ArmourTweaks.armourTypeToName.containsKey(armourType)){
                    name = ArmourTweaks.armourTypeToName.get(armourType);
                }*/
            	//logger.info("Base DR for "+name+": "+(ArmourTweaks.armourDamageReduction.get(armourType)*100f) +"%");
                logger.info(String.format("Base DR for %s: %.2f%%", name, ArmourTweaks.armourDamageReduction.get(armourType)*100f));
            }
            logger.info("> Armour Effectiveness Settings <");
            for(int armourType : ArmourTweaks.armourEffectiveness.keySet()){
                String name = ArmourAssist.getArmourName(armourType);
                /*String name = String.valueOf(armourType);
                if(ArmourTweaks.armourTypeToName.containsKey(armourType)){
                    name = ArmourTweaks.armourTypeToName.get(armourType);
                }*/
                HashMap<Byte, Float> woundMap = ArmourTweaks.armourEffectiveness.get(armourType);
                for(byte woundType : woundMap.keySet()){
                    String wound = WoundAssist.getWoundName(woundType);
                    logger.info(String.format("Effectiveness for armour %s against %s: %.2f%%", name, wound, woundMap.get(woundType)*100f));
                    //logger.info("Effectiveness for "+name+" against "+woundType+": "+(woundMap.get(woundType)*100f) +"%");
                }
            }
            logger.info("> Armour Glance Rate Settings <");
            for(int armourType : ArmourTweaks.armourGlanceRates.keySet()){
                String name = ArmourAssist.getArmourName(armourType);
                /*String name = String.valueOf(armourType);
                if(ArmourTweaks.armourTypeToName.containsKey(armourType)){
                    name = ArmourTweaks.armourTypeToName.get(armourType);
                }*/
                HashMap<Byte, Float> woundMap = ArmourTweaks.armourGlanceRates.get(armourType);
                for(byte woundType : woundMap.keySet()){
                    String wound = WoundAssist.getWoundName(woundType);
                    logger.info(String.format("Glance rate for armour %s against %s: %.2f%%", name, wound, woundMap.get(woundType)*100f));
                    //logger.info("Effectiveness for "+name+" against "+woundType+": "+(woundMap.get(woundType)*100f) +"%");
                }
            }
        }
        logger.info(" -- Material Configuration -- ");
        logger.info("> Armour Material Damage Reduction Settings <");
        for(byte material : ArmourTweaks.materialDamageReduction.keySet()){
            logger.info(String.format("Base DR modifier for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), ArmourTweaks.materialDamageReduction.get(material)*100f));
        }
        logger.info("> Armour Material Effectiveness Settings <");
        for(byte material : ArmourTweaks.materialEffectiveness.keySet()){
            HashMap<Byte, Float> woundMap = ArmourTweaks.materialEffectiveness.get(material);
            for(byte woundType : woundMap.keySet()){
                String wound = WoundAssist.getWoundName(woundType);
                logger.info(String.format("Effectiveness for material %s against %s: %.2f%%", MaterialTweaks.getMaterialName(material), wound, woundMap.get(woundType)*100f));
            }
        }
        logger.info("> Armour Material Glance Rate Settings <");
        for(byte material : ArmourTweaks.materialGlanceRate.keySet()){
            //String name = materialNameReference.containsKey(material) ? materialNameReference.get(material) : String.valueOf(material);
            HashMap<Byte, Float> woundMap = ArmourTweaks.materialGlanceRate.get(material);
            for(byte woundType : woundMap.keySet()){
                String wound = WoundAssist.getWoundName(woundType);
                logger.info(String.format("Glance Rate for material %s against %s: %.2f%%", MaterialTweaks.getMaterialName(material), wound, woundMap.get(woundType)*100f));
            }
        }
        logger.info("> Armour Material Movement Modifier Settings <");
        for(byte material : ArmourTweaks.materialMovementModifier.keySet()){
            logger.info(String.format("Movement Speed modifier for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), ArmourTweaks.materialMovementModifier.get(material)*100f));
        }
        logger.info("> Weapon Material Damage Settings <");
        for(byte material : WeaponTweaks.materialWeaponDamage.keySet()){
            logger.info(String.format("Damage modifier for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), WeaponTweaks.materialWeaponDamage.get(material)*100f));
        }
        logger.info("> Weapon Material Speed Settings <");
        for(byte material : WeaponTweaks.materialWeaponSpeed.keySet()){
            logger.info(String.format("Speed modifier for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), WeaponTweaks.materialWeaponSpeed.get(material)*100f));
        }
        logger.info("> Weapon Material Parry Settings <");
        for(byte material : WeaponTweaks.materialWeaponParry.keySet()){
            logger.info(String.format("Parry modifier for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), WeaponTweaks.materialWeaponParry.get(material)*100f));
        }
        logger.info("> Weapon Material Armour Damage Settings <");
        for(byte material : WeaponTweaks.materialWeaponArmourDamage.keySet()){
            logger.info(String.format("Armour Damage modifier for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), WeaponTweaks.materialWeaponArmourDamage.get(material)*100f));
        }
        logger.info("> Item Material Damage Modifier Settings <");
        for(byte material : MaterialTweaks.materialDamageModifier.keySet()){
            logger.info(String.format("Damage modifier for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), MaterialTweaks.materialDamageModifier.get(material)*100f));
        }
        logger.info("> Item Material Decay Modifier Settings <");
        for(byte material : MaterialTweaks.materialDecayModifier.keySet()){
            logger.info(String.format("Decay modifier for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), MaterialTweaks.materialDecayModifier.get(material)*100f));
        }
        logger.info("> Item Material Creation Bonus Settings <");
        for(byte material : MaterialTweaks.materialCreationBonus.keySet()){
            logger.info(String.format("Creation bonus for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), MaterialTweaks.materialCreationBonus.get(material)*100f));
        }
        logger.info("> Item Material Improve Bonus Settings <");
        for(byte material : MaterialTweaks.materialImproveBonus.keySet()){
            logger.info(String.format("Improve bonus for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), MaterialTweaks.materialImproveBonus.get(material)*100f));
        }
        logger.info("> Item Material Shatter Resistance Settings <");
        for(byte material : MaterialTweaks.materialShatterResistance.keySet()){
            logger.info(String.format("Shatter resistance for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), MaterialTweaks.materialShatterResistance.get(material)*100f));
        }
        logger.info("> Item Material Lockpick Bonus Settings <");
        for(byte material : MaterialTweaks.materialLockpickBonus.keySet()){
            logger.info(String.format("Lockpick bonus for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), MaterialTweaks.materialLockpickBonus.get(material)*100f));
        }
        logger.info("> Item Material Anchor Bonus Settings <");
        for(byte material : MaterialTweaks.materialAnchorBonus.keySet()){
            logger.info(String.format("Anchor bonus for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), MaterialTweaks.materialAnchorBonus.get(material)*100f));
        }
        logger.info("> Item Material Pendulum Effect Settings <");
        for(byte material : MaterialTweaks.materialPendulumEffect.keySet()){
            logger.info(String.format("Pendulum effect for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), MaterialTweaks.materialPendulumEffect.get(material)*100f));
        }
        logger.info("> Item Material Repair Speed Settings <");
        for(byte material : MaterialTweaks.materialRepairSpeed.keySet()){
            logger.info(String.format("Repair speed for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), MaterialTweaks.materialRepairSpeed.get(material)*100f));
        }
        logger.info("> Item Material Bash Modifier Settings <");
        for(byte material : MaterialTweaks.materialBashModifier.keySet()){
            logger.info(String.format("Bash modifier for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), MaterialTweaks.materialBashModifier.get(material)*100f));
        }
        logger.info("> Item Material Spell Effect Modifier Settings <");
        for(byte material : MaterialTweaks.materialSpellEffectModifier.keySet()){
            logger.info(String.format("Spell effect modifier for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), MaterialTweaks.materialSpellEffectModifier.get(material)*100f));
        }
        logger.info("> Item Material Specific Spell Effect Modifier Settings <");
        for(byte material : MaterialTweaks.materialSpecificSpellEffectModifier.keySet()){
            //String name = materialNameReference.containsKey(material) ? materialNameReference.get(material) : String.valueOf(material);
            HashMap<Byte, Float> enchantMap = MaterialTweaks.materialSpecificSpellEffectModifier.get(material);
            for(byte enchant : enchantMap.keySet()){
                logger.info(String.format("Spell Effect Power for material %s with enchant %s: %.2f%%", MaterialTweaks.getMaterialName(material), enchant, enchantMap.get(enchant)*100f));
            }
        }
        logger.info("> Item Material Difficulty Modifier Settings <");
        for(byte material : MaterialTweaks.materialDifficultyModifier.keySet()){
            logger.info(String.format("Difficulty modifier for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), MaterialTweaks.materialDifficultyModifier.get(material)*100f));
        }
        logger.info("> Item Material Action Speed Modifier Settings <");
        for(byte material : MaterialTweaks.materialActionSpeedModifier.keySet()){
            logger.info(String.format("Action Speed modifier for material %s: %.2f%%", MaterialTweaks.getMaterialName(material), MaterialTweaks.materialActionSpeedModifier.get(material)*100f));
        }
        /*logger.log(Level.INFO, "adamantineMaterialMod: " + adamantineMaterialMod);
        logger.log(Level.INFO, "glimmersteelMaterialMod: " + glimmersteelMaterialMod);
        logger.log(Level.INFO, "seryllMaterialMod: " + seryllMaterialMod);*/
        logger.info(" -- Armour Limit Configuration -- ");
        logger.log(Level.INFO, "enableCustomArmourLimitFactors: " + enableCustomArmourLimitFactors);
        if(enableCustomArmourLimitFactors){
            logger.log(Level.INFO, "clothArmourLimitFactor: " + clothArmourLimitFactor);
            logger.log(Level.INFO, "leatherArmourLimitFactor: " + leatherArmourLimitFactor);
            logger.log(Level.INFO, "studdedArmourLimitFactor: " + studdedArmourLimitFactor);
            logger.log(Level.INFO, "chainArmourLimitFactor: " + chainArmourLimitFactor);
            logger.log(Level.INFO, "plateArmourLimitFactor: " + plateArmourLimitFactor);
            logger.log(Level.INFO, "drakeArmourLimitFactor: " + drakeArmourLimitFactor);
            logger.log(Level.INFO, "dragonscaleArmourLimitFactor: " + dragonscaleArmourLimitFactor);
        }
        logger.info(" -- Shield Configuration -- ");
        logger.log(Level.INFO, "enableShieldDamageEnchants: " + enableShieldDamageEnchants);
        logger.info(" -- Weapon Configuration -- ");
        logger.log(Level.INFO, "minimumSwingTime: " + minimumSwingTime);
        logger.log(Level.INFO, "raresReduceSwingTime: " + raresReduceSwingTime);
        logger.log(Level.INFO, "rareSwingSpeedReduction: " + rareSwingSpeedReduction);
        logger.log(Level.INFO, "fixSavedSwingTimer: " + fixSavedSwingTimer);
        logger.log(Level.INFO, "betterDualWield: " + betterDualWield);
        logger.info(" -- Configuration complete -- ");
    }

	@Override
	public void preInit(){
		CombatTweaks.preInit();
		ArmourTweaks.preInit();
		ShieldTweaks.preInit();
		WeaponTweaks.preInit();
		MaterialTweaks.preInit();
	}
	
	@Override
	public void onItemTemplatesCreated(){
		logger.info("Beginning onItemTemplatesCreated...");
		ArmourTweaks.onItemTemplatesCreated();
	}
	
	@Override
	public void onServerStarted(){
		WeaponTweaks.onServerStarted();
        for(CreatureTemplate template : CreatureTemplateFactory.getInstance().getTemplates()){
            if(ArmourAssist.armourTypeToName.containsKey((int) template.getArmourType())) {
                logger.info(template.getName() + " - " + ArmourAssist.armourTypeToName.get((int) template.getArmourType()));
            }
        }
	}
}
