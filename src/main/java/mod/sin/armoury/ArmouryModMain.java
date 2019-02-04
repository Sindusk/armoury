package mod.sin.armoury;

import com.wurmonline.server.items.Materials;
import mod.sin.lib.ArmourAssist;
import mod.sin.lib.Prop;
import mod.sin.lib.WoundAssist;
import org.gotti.wurmunlimited.modloader.interfaces.*;

import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArmouryModMain
implements WurmServerMod, Configurable, PreInitable, ItemTemplatesCreatedListener, ServerStartedListener {
	public static Logger logger = Logger.getLogger(ArmouryModMain.class.getName());

	// - Armour Configuration - //
	public static boolean enableArmourModifications = true;

	// - Shield Configuration -- //
	public static boolean enableShieldDamageEnchants = true;
	public static boolean enableShieldSpeedEnchants = true;
	
	// -- Weapon Configuration -- //
	public static float minimumSwingTime = 3.0f;
	public static boolean raresReduceSwingTime = true;
	public static float rareSwingSpeedReduction = 0.2f;
	public static boolean fixSavedSwingTimer = true;
	public static boolean betterDualWield = false; // HIGHLY EXPERIMENTAL
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

	public static byte parseArmourType(String str){
        return (byte) ArmourAssist.getArmourByte(str);
    }
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
        MaterialsTweaks.initializeMaterialMaps();
        WeaponsTweaks.initializeWeaponMaps();

		// Armour Configuration
        enableArmourModifications = Prop.getBooleanProperty("enableArmourModifications", enableArmourModifications);
    	// Shield Configuration
        enableShieldDamageEnchants = Boolean.parseBoolean(properties.getProperty("enableShieldDamageEnchants", Boolean.toString(enableShieldDamageEnchants)));
        // Weapon Configuration
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
                    case "minimumSwingTime":
                    case "rareSwingSpeedReduction":
                    case "fixedSavedSwingTimer":
                    case "betterDualWield":
                        break; // ignore properties that are already configured
                    default:
                        if (name.startsWith("armourDamageReduction")) {
                            String[] split = value.split(",");
                            byte armourType = parseArmourType(split[0]);
                            float reduction = Float.parseFloat(split[1]);
                            ArmourTemplateTweaks.addArmourDamageReduction(armourType, reduction);
                        } else if (name.startsWith("armourEffectiveness")) {
                            String[] split = value.split(";");
                            byte armourType = parseArmourType(split[0]);
                            String[] split2 = split[1].split(",");
                            ArmourTemplateTweaks.addArmourEffectiveness(armourType, split2);
                        } else if (name.startsWith("armourGlanceRate")) {
                            String[] split = value.split(";");
                            byte armourType = parseArmourType(split[0]);
                            String[] split2 = split[1].split(",");
                            ArmourTemplateTweaks.addArmourGlanceRate(armourType, split2);
                        } else if (name.startsWith("armourMovement")) {
                            String[] split = value.split(",");
                            String itemTemplate = split[0];
                            float movementPenalty = Float.parseFloat(split[1]);
                            ArmourTemplateTweaks.addArmourMovement(itemTemplate, movementPenalty);
                        } else if (name.startsWith("materialDamageReduction")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float reduction = Float.parseFloat(split[1]);
                            MaterialsTweaks.addMaterialDamageReduction(material, reduction);
                        } else if (name.startsWith("materialMovementModifier")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float modifier = Float.parseFloat(split[1]);
                            MaterialsTweaks.addMaterialMovementModifier(material, modifier);
                        } else if (name.startsWith("materialWeaponDamage")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            double mult = Double.parseDouble(split[1]);
                            WeaponsTweaks.addMaterialWeaponDamage(material, mult);
                        } else if (name.startsWith("materialWeaponSpeed")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float mult = Float.parseFloat(split[1]);
                            WeaponsTweaks.addMaterialWeaponSpeed(material, mult);
                        } else if (name.startsWith("materialWeaponParry")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float mult = Float.parseFloat(split[1]);
                            WeaponsTweaks.addMaterialWeaponParry(material, mult);
                        } else if (name.startsWith("materialWeaponArmourDamage")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            double mult = Double.parseDouble(split[1]);
                            WeaponsTweaks.addMaterialWeaponArmourDamage(material, mult);
                        } else if (name.startsWith("materialDamageModifier")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float mult = Float.parseFloat(split[1]);
                            MaterialsTweaks.addMaterialDamageModifier(material, mult);
                        } else if (name.startsWith("materialDecayModifier")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float mult = Float.parseFloat(split[1]);
                            MaterialsTweaks.addMaterialDecayModifier(material, mult);
                        } else if (name.startsWith("materialCreationBonus")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float bonus = Float.parseFloat(split[1]);
                            MaterialsTweaks.addMaterialCreationBonus(material, bonus);
                        } else if (name.startsWith("materialImproveBonus")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float bonus = Float.parseFloat(split[1]);
                            MaterialsTweaks.addMaterialImproveBonus(material, bonus);
                        } else if (name.startsWith("materialShatterResistance")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float resistance = Float.parseFloat(split[1]);
                            MaterialsTweaks.addMaterialShatterResistance(material, resistance);
                        } else if (name.startsWith("materialLockpickBonus")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float bonus = Float.parseFloat(split[1]);
                            MaterialsTweaks.addMaterialLockpickBonus(material, bonus);
                        } else if (name.startsWith("materialAnchorBonus")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float bonus = Float.parseFloat(split[1]);
                            MaterialsTweaks.addMaterialAnchorBonus(material, bonus);
                        } else if (name.startsWith("materialPendulumEffect")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float bonus = Float.parseFloat(split[1]);
                            MaterialsTweaks.addMaterialPendulumEffect(material, bonus);
                        } else if (name.startsWith("materialRepairSpeed")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float mult = Float.parseFloat(split[1]);
                            MaterialsTweaks.addMaterialRepairSpeed(material, mult);
                        } else if (name.startsWith("materialBashModifier")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            double mult = Double.parseDouble(split[1]);
                            MaterialsTweaks.addMaterialBashModifier(material, mult);
                        } else if (name.startsWith("materialSpellEffectModifier")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            float mult = Float.parseFloat(split[1]);
                            MaterialsTweaks.addMaterialSpellEffectModifier(material, mult);
                        } else if (name.startsWith("materialSpecificSpellEffectModifier")) {
                            String[] split = value.split(";");
                            byte material = parseMaterialType(split[0]);
                            String[] split2 = split[1].split(",");
                            MaterialsTweaks.addMaterialSpecificSpellEffectModifier(material, split2);
                        } else if (name.startsWith("materialDifficultyModifier")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            double mult = Double.parseDouble(split[1]);
                            MaterialsTweaks.addMaterialDifficultyModifier(material, mult);
                        } else if (name.startsWith("materialActionSpeedModifier")) {
                            String[] split = value.split(",");
                            byte material = parseMaterialType(split[0]);
                            double mult = Double.parseDouble(split[1]);
                            MaterialsTweaks.addMaterialActionSpeedModifier(material, mult);
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
        // Print configuration values
        logger.info(" -- Armour Configuration -- ");
    	logger.info("enableArmourModifications: " + enableArmourModifications);
        logger.info("> Armour Damage Reduction Settings <");
        for(byte armourType : ArmourTemplateTweaks.armourDamageReduction.keySet()){
            logger.info(String.format("Damage reduction for armour %s: %.2f%%", ArmourAssist.getArmourName(armourType), ArmourTemplateTweaks.armourDamageReduction.get(armourType)*100f));
        }
        logger.info("> Armour Effectiveness Settings <");
        for(byte armourType : ArmourTemplateTweaks.armourEffectiveness.keySet()){
            HashMap<Byte, Float> woundMap = ArmourTemplateTweaks.armourEffectiveness.get(armourType);
            for(byte woundType : woundMap.keySet()){
                String wound = WoundAssist.getWoundName(woundType);
                logger.info(String.format("Effectiveness for armour %s against %s: %.2f%%", ArmourAssist.getArmourName(armourType), wound, woundMap.get(woundType)*100f));
            }
        }
        logger.info("> Armour Glance Rate Settings <");
        for(byte armourType : ArmourTemplateTweaks.armourGlanceRates.keySet()){
            HashMap<Byte, Float> woundMap = ArmourTemplateTweaks.armourGlanceRates.get(armourType);
            for(byte woundType : woundMap.keySet()){
                String wound = WoundAssist.getWoundName(woundType);
                logger.info(String.format("Glance rate for armour %s against %s: %.2f%%", ArmourAssist.getArmourName(armourType), wound, woundMap.get(woundType)*100f));
            }
        }
        logger.info("> Armour Movement Rate Changes <");
        for(String armourName : ArmourTemplateTweaks.armourMovement.keySet()){
            logger.info(String.format("Movement penalty for armour %s changed to %.2f%%", armourName, ArmourTemplateTweaks.armourMovement.get(armourName)*100f));
        }
        logger.info(" -- Material Configuration -- ");
        logger.info("> Armour Material Damage Reduction Settings <");
        for(byte material : MaterialsTweaks.materialDamageReduction.keySet()){
            logger.info(String.format("Base DR modifier for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialDamageReduction.get(material)*100f));
        }
        logger.info("> Armour Material Movement Modifier Settings <");
        for(byte material : MaterialsTweaks.materialMovementModifier.keySet()){
            logger.info(String.format("Movement Speed modifier for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialMovementModifier.get(material)*100f));
        }
        logger.info("> Weapon Material Damage Settings <");
        for(byte material : WeaponsTweaks.materialWeaponDamage.keySet()){
            logger.info(String.format("Damage modifier for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), WeaponsTweaks.materialWeaponDamage.get(material)*100f));
        }
        logger.info("> Weapon Material Speed Settings <");
        for(byte material : WeaponsTweaks.materialWeaponSpeed.keySet()){
            logger.info(String.format("Speed modifier for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), WeaponsTweaks.materialWeaponSpeed.get(material)*100f));
        }
        logger.info("> Weapon Material Parry Settings <");
        for(byte material : WeaponsTweaks.materialWeaponParry.keySet()){
            logger.info(String.format("Parry modifier for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), WeaponsTweaks.materialWeaponParry.get(material)*100f));
        }
        logger.info("> Weapon Material Armour Damage Settings <");
        for(byte material : WeaponsTweaks.materialWeaponArmourDamage.keySet()){
            logger.info(String.format("Armour Damage modifier for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), WeaponsTweaks.materialWeaponArmourDamage.get(material)*100f));
        }
        logger.info("> Item Material Damage Modifier Settings <");
        for(byte material : MaterialsTweaks.materialDamageModifier.keySet()){
            logger.info(String.format("Damage modifier for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialDamageModifier.get(material)*100f));
        }
        logger.info("> Item Material Decay Modifier Settings <");
        for(byte material : MaterialsTweaks.materialDecayModifier.keySet()){
            logger.info(String.format("Decay modifier for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialDecayModifier.get(material)*100f));
        }
        logger.info("> Item Material Creation Bonus Settings <");
        for(byte material : MaterialsTweaks.materialCreationBonus.keySet()){
            logger.info(String.format("Creation bonus for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialCreationBonus.get(material)*100f));
        }
        logger.info("> Item Material Improve Bonus Settings <");
        for(byte material : MaterialsTweaks.materialImproveBonus.keySet()){
            logger.info(String.format("Improve bonus for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialImproveBonus.get(material)*100f));
        }
        logger.info("> Item Material Shatter Resistance Settings <");
        for(byte material : MaterialsTweaks.materialShatterResistance.keySet()){
            logger.info(String.format("Shatter resistance for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialShatterResistance.get(material)*100f));
        }
        logger.info("> Item Material Lockpick Bonus Settings <");
        for(byte material : MaterialsTweaks.materialLockpickBonus.keySet()){
            logger.info(String.format("Lockpick bonus for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialLockpickBonus.get(material)*100f));
        }
        logger.info("> Item Material Anchor Bonus Settings <");
        for(byte material : MaterialsTweaks.materialAnchorBonus.keySet()){
            logger.info(String.format("Anchor bonus for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialAnchorBonus.get(material)*100f));
        }
        logger.info("> Item Material Pendulum Effect Settings <");
        for(byte material : MaterialsTweaks.materialPendulumEffect.keySet()){
            logger.info(String.format("Pendulum effect for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialPendulumEffect.get(material)*100f));
        }
        logger.info("> Item Material Repair Speed Settings <");
        for(byte material : MaterialsTweaks.materialRepairSpeed.keySet()){
            logger.info(String.format("Repair speed for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialRepairSpeed.get(material)*100f));
        }
        logger.info("> Item Material Bash Modifier Settings <");
        for(byte material : MaterialsTweaks.materialBashModifier.keySet()){
            logger.info(String.format("Bash modifier for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialBashModifier.get(material)*100f));
        }
        logger.info("> Item Material Spell Effect Modifier Settings <");
        for(byte material : MaterialsTweaks.materialSpellEffectModifier.keySet()){
            logger.info(String.format("Spell effect modifier for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialSpellEffectModifier.get(material)*100f));
        }
        logger.info("> Item Material Specific Spell Effect Modifier Settings <");
        for(byte material : MaterialsTweaks.materialSpecificSpellEffectModifier.keySet()){
            //String name = materialNameReference.containsKey(material) ? materialNameReference.get(material) : String.valueOf(material);
            HashMap<Byte, Float> enchantMap = MaterialsTweaks.materialSpecificSpellEffectModifier.get(material);
            for(byte enchant : enchantMap.keySet()){
                logger.info(String.format("Spell Effect Power for material %s with enchant %s: %.2f%%", MaterialsTweaks.getMaterialName(material), enchant, enchantMap.get(enchant)*100f));
            }
        }
        logger.info("> Item Material Difficulty Modifier Settings <");
        for(byte material : MaterialsTweaks.materialDifficultyModifier.keySet()){
            logger.info(String.format("Difficulty modifier for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialDifficultyModifier.get(material)*100f));
        }
        logger.info("> Item Material Action Speed Modifier Settings <");
        for(byte material : MaterialsTweaks.materialActionSpeedModifier.keySet()){
            logger.info(String.format("Action Speed modifier for material %s: %.2f%%", MaterialsTweaks.getMaterialName(material), MaterialsTweaks.materialActionSpeedModifier.get(material)*100f));
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
		CombatsTweaks.preInit();
		ShieldsTweaks.preInit();
		WeaponsTweaks.preInit();
		MaterialsTweaks.preInit();
	}
	
	@Override
	public void onItemTemplatesCreated(){
		logger.info("Beginning onItemTemplatesCreated...");
	}
	
	@Override
	public void onServerStarted(){
		WeaponsTweaks.onServerStarted();
        ArmourTemplateTweaks.onServerStarted();
	}
}
