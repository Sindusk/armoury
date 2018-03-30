package mod.sin.armoury;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

import com.wurmonline.server.combat.Armour;
import com.wurmonline.server.combat.ArmourTypes;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateFactory;

public class ArmouryMod
implements WurmServerMod, Configurable, PreInitable, ItemTemplatesCreatedListener, ServerStartedListener {
    private Logger logger;

    // Configuration options
	public boolean bDebug = false;
	public boolean enableNonPlayerCrits = true;
	public boolean fixArmourLimitBuffBug = true;
	public boolean fixArmourLimitSpellEffect = true;
	
	// -- Armour configuration -- //
	public boolean enableArmourReductionModifications = true;
	public float unarmouredReduction = 0.05f;
	// Armour Mapping
	public String[] armourTypes = {"cloth", "leather", "studded", "chain", "plate", "drake", "dragonscale", // Worn armour pieces
									"scale", "ring", "splint"}; // Used by the system but not worn by players
	public HashMap<Integer, Float> armourTypeReduction = new HashMap<Integer, Float>();
	public HashMap<String, Integer> armourTypeReference = new HashMap<String, Integer>();
	// Armour modifiers
	public float adamantineMaterialMod = 0.05f;
	public float glimmersteelMaterialMod = 0.1f;
	public float seryllMaterialMod = 0.1f;
	// Armour limit factors
	public boolean enableCustomArmourLimitFactors = true;
	public float clothArmourLimitFactor = 0.3f;
	public float leatherArmourLimitFactor = 0.3f;
	public float studdedArmourLimitFactor = 0.0f;
	public float chainArmourLimitFactor = -0.15f;
	public float plateArmourLimitFactor = -0.3f;
	public float drakeArmourLimitFactor = -0.3f;
	public float dragonscaleArmourLimitFactor = -0.3f;
	public HashMap<Integer, Float> armourReductionOverride = new HashMap<Integer, Float>();
	// Armour movement
	public boolean enableArmourMovementModifications = true;
	public HashMap<String, Float> armourMovement = new HashMap<String, Float>();

	// - Shield configuration -- //
	public boolean enableShieldDamageEnchants = true;
	public boolean enableShieldSpeedEnchants = true;
	
	// -- Weapon configuration -- //
	public float minimumSwingTime = 3.0f;
	public boolean raresReduceSwingTime = true;
	public float rareSwingSpeedReduction = 0.2f;
	public boolean fixSavedSwingTimer = true;
	public boolean betterDualWield = true; // HIGHLY EXPERIMENTAL
	// Weapon variable changes
	public HashMap<Integer, Float> weaponDamage = new HashMap<Integer, Float>();
	public HashMap<Integer, Float> weaponSpeed = new HashMap<Integer, Float>();
	public HashMap<Integer, Float> weaponCritChance = new HashMap<Integer, Float>();
	public HashMap<Integer, Integer> weaponReach = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> weaponWeightGroup = new HashMap<Integer, Integer>();
	public HashMap<Integer, Float> weaponParryPercent = new HashMap<Integer, Float>();
	public HashMap<Integer, Double> weaponSkillPenalty = new HashMap<Integer, Double>();
    
    public ArmouryMod(){
    	this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    @Override
	public void configure(Properties properties) {
		this.logger.info("Beginning configuration... [Testing]");
		// Base configuration options
        this.bDebug = Boolean.parseBoolean(properties.getProperty("debug", Boolean.toString(this.bDebug)));
        this.enableNonPlayerCrits = Boolean.parseBoolean(properties.getProperty("enableNonPlayerCrits", Boolean.toString(this.enableNonPlayerCrits)));
        this.fixArmourLimitBuffBug = Boolean.parseBoolean(properties.getProperty("fixArmourLimitBuffBug", Boolean.toString(this.fixArmourLimitBuffBug)));
        this.fixArmourLimitSpellEffect = Boolean.parseBoolean(properties.getProperty("fixArmourLimitSpellEffect", Boolean.toString(this.fixArmourLimitSpellEffect)));
        // Armour configuration
        this.initArmourDefaults(); // Create references for use soon.
        this.enableArmourReductionModifications = Boolean.parseBoolean(properties.getProperty("enableArmourReductionModifications", Boolean.toString(this.enableArmourReductionModifications)));
        if(enableArmourReductionModifications){
        	this.unarmouredReduction = Float.parseFloat(properties.getProperty("unarmouredReduction", Float.toString(this.unarmouredReduction)));
	        for(String armourType : armourTypes){
	        	int armourNum = armourTypeReference.get(armourType);
	    		float defaultVal = armourTypeReduction.get(armourNum);
	        	armourTypeReduction.put(armourNum, Float.parseFloat(properties.getProperty(armourType+"Reduction", Float.toString(defaultVal))));
	        	if(armourTypeReduction.get(armourNum) <= 0f){
	        		this.logger.warning("[ERROR]: Armour type \""+armourType+"\" not set properly the Armour Reduction configuration in armoury.properties! It will not reduce damage at all until resolved!");
	        	}
	        }
        }
    	this.adamantineMaterialMod = Float.parseFloat(properties.getProperty("adamantineMaterialMod", Float.toString(this.adamantineMaterialMod)));
    	this.glimmersteelMaterialMod = Float.parseFloat(properties.getProperty("glimmersteelMaterialMod", Float.toString(this.glimmersteelMaterialMod)));
    	this.seryllMaterialMod = Float.parseFloat(properties.getProperty("seryllMaterialMod", Float.toString(this.seryllMaterialMod)));
    	// Armour limit factors
        this.enableCustomArmourLimitFactors = Boolean.parseBoolean(properties.getProperty("enableCustomArmourLimitFactors", Boolean.toString(this.enableCustomArmourLimitFactors)));
    	this.clothArmourLimitFactor = Float.parseFloat(properties.getProperty("clothArmourLimitFactor", Float.toString(this.clothArmourLimitFactor)));
    	this.leatherArmourLimitFactor = Float.parseFloat(properties.getProperty("leatherArmourLimitFactor", Float.toString(this.leatherArmourLimitFactor)));
    	this.studdedArmourLimitFactor = Float.parseFloat(properties.getProperty("studdedArmourLimitFactor", Float.toString(this.studdedArmourLimitFactor)));
    	this.chainArmourLimitFactor = Float.parseFloat(properties.getProperty("chainArmourLimitFactor", Float.toString(this.chainArmourLimitFactor)));
    	this.plateArmourLimitFactor = Float.parseFloat(properties.getProperty("plateArmourLimitFactor", Float.toString(this.plateArmourLimitFactor)));
    	this.drakeArmourLimitFactor = Float.parseFloat(properties.getProperty("drakeArmourLimitFactor", Float.toString(this.drakeArmourLimitFactor)));
    	this.dragonscaleArmourLimitFactor = Float.parseFloat(properties.getProperty("dragonscaleArmourLimitFactor", Float.toString(this.dragonscaleArmourLimitFactor)));
    	// Armour movement modifiers
        this.enableArmourMovementModifications = Boolean.parseBoolean(properties.getProperty("enableArmourMovementModifications", Boolean.toString(this.enableArmourMovementModifications)));
        // Shield configuration
        this.enableShieldDamageEnchants = Boolean.parseBoolean(properties.getProperty("enableShieldDamageEnchants", Boolean.toString(this.enableShieldDamageEnchants)));
        // Weapon configuration
        this.minimumSwingTime = Float.parseFloat(properties.getProperty("minimumSwingTime", Float.toString(this.minimumSwingTime)));
        this.raresReduceSwingTime = Boolean.parseBoolean(properties.getProperty("raresReduceSwingTime", Boolean.toString(this.raresReduceSwingTime)));
    	this.rareSwingSpeedReduction = Float.parseFloat(properties.getProperty("rareSwingSpeedReduction", Float.toString(this.rareSwingSpeedReduction)));
        this.fixSavedSwingTimer = Boolean.parseBoolean(properties.getProperty("fixSavedSwingTimer", Boolean.toString(this.fixSavedSwingTimer)));
        this.betterDualWield = Boolean.parseBoolean(properties.getProperty("betterDualWield", Boolean.toString(this.betterDualWield)));
    	for (String name : properties.stringPropertyNames()) {
            try {
                String value = properties.getProperty(name);
                switch (name) {
                    case "debug":
                    case "classname":
                    case "classpath":
                    case "sharedClassLoader":
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
                            //Debug("Unknown config property: " + name);
                        }
                }
            } catch (Exception e) {
                Debug("Error processing property " + name);
                e.printStackTrace();
            }
        }
        // Print values of main.java.armoury.mod configuration
        this.logger.info(" -- Mod Configuration -- ");
        this.logger.log(Level.INFO, "enableNonPlayerCrits: " + this.enableNonPlayerCrits);
        this.logger.log(Level.INFO, "fixArmourLimitBuffBug: " + this.fixArmourLimitBuffBug);
        this.logger.log(Level.INFO, "fixArmourLimitSpellEffect: " + this.fixArmourLimitSpellEffect);
        this.logger.info(" -- Armour Configuration -- ");
        this.logger.log(Level.INFO, "enableArmourReductionModifications: " + this.enableArmourReductionModifications);
        if(enableArmourReductionModifications){
            this.logger.log(Level.INFO, "unarmouredReduction: " + this.unarmouredReduction);
            this.logger.info("> Armour Reduction Settings <");
            for(String armourType : armourTypeReference.keySet()){
            	this.logger.info(armourType+" - "+((int)(armourTypeReduction.get(armourTypeReference.get(armourType))*100))+"%");
            }
        }
        this.logger.log(Level.INFO, "adamantineMaterialMod: " + this.adamantineMaterialMod);
        this.logger.log(Level.INFO, "glimmersteelMaterialMod: " + this.glimmersteelMaterialMod);
        this.logger.log(Level.INFO, "seryllMaterialMod: " + this.seryllMaterialMod);
        this.logger.log(Level.INFO, "enableCustomArmourLimitFactors: " + this.enableCustomArmourLimitFactors);
        if(enableCustomArmourLimitFactors){
            this.logger.log(Level.INFO, "clothArmourLimitFactor: " + this.clothArmourLimitFactor);
            this.logger.log(Level.INFO, "leatherArmourLimitFactor: " + this.leatherArmourLimitFactor);
            this.logger.log(Level.INFO, "studdedArmourLimitFactor: " + this.studdedArmourLimitFactor);
            this.logger.log(Level.INFO, "chainArmourLimitFactor: " + this.chainArmourLimitFactor);
            this.logger.log(Level.INFO, "plateArmourLimitFactor: " + this.plateArmourLimitFactor);
            this.logger.log(Level.INFO, "drakeArmourLimitFactor: " + this.drakeArmourLimitFactor);
            this.logger.log(Level.INFO, "dragonscaleArmourLimitFactor: " + this.dragonscaleArmourLimitFactor);
        }
        this.logger.info(" -- Shield Configuration -- ");
        this.logger.log(Level.INFO, "enableShieldDamageEnchants: " + this.enableShieldDamageEnchants);
        this.logger.info(" -- Weapon Configuration -- ");
        this.logger.log(Level.INFO, "minimumSwingTime: " + this.minimumSwingTime);
        this.logger.log(Level.INFO, "raresReduceSwingTime: " + this.raresReduceSwingTime);
        this.logger.log(Level.INFO, "rareSwingSpeedReduction: " + this.rareSwingSpeedReduction);
        this.logger.log(Level.INFO, "fixSavedSwingTimer: " + this.fixSavedSwingTimer);
        this.logger.log(Level.INFO, "betterDualWield: " + this.betterDualWield);
        this.Debug("Debugging messages are enabled.");
        this.logger.info(" -- Configuration complete -- ");
    }
	
	protected void Debug(String x) {
        if (this.bDebug) {
            System.out.println(String.valueOf(this.getClass().getSimpleName()) + ": " + x);
            System.out.flush();
            this.logger.log(Level.INFO, x);
        }
    }
	
	@Override
	public void preInit(){
		CombatTweaks.preInit(this);
		ArmourTweaks.preInit(this);
		ShieldTweaks.preInit(this);
	}
	
	@Override
	public void onItemTemplatesCreated(){
		logger.info("Creating armour template lists...");
		createArmourTemplateLists();
		ArmourTweaks.onItemTemplatesCreated(this);
	}
	
	@Override
	public void onServerStarted(){
		WeaponTweaks.onServerStarted(this);
	}
	
	public HashMap<String, Integer> armourNameToItemTemplate = new HashMap<String, Integer>();

	public ArrayList<Armour> clothArmour = new ArrayList<Armour>();
	public ArrayList<Armour> leatherArmour = new ArrayList<Armour>();
	public ArrayList<Armour> studdedArmour = new ArrayList<Armour>();
	public ArrayList<Armour> chainArmour = new ArrayList<Armour>();
	public ArrayList<Armour> plateArmour = new ArrayList<Armour>();
	public ArrayList<Armour> drakeArmour = new ArrayList<Armour>();
	public ArrayList<Armour> dragonscaleArmour = new ArrayList<Armour>();
	
	private void addArmour(ArrayList<Armour> typeList, int itemTemplate){
		ItemTemplate it = ItemTemplateFactory.getInstance().getTemplateOrNull(itemTemplate);
		if(it != null){
			armourNameToItemTemplate.put(it.getName(), itemTemplate);
		}
		typeList.add(Armour.getArmour(itemTemplate));
	}
	
	public void createArmourTemplateLists(){
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
	
    private void initArmourDefaults(){
    	armourTypeReference.put("cloth", ArmourTypes.ARMOUR_CLOTH);
    	armourTypeReduction.put(ArmourTypes.ARMOUR_CLOTH, 0.4f);
    	armourTypeReference.put("leather", ArmourTypes.ARMOUR_LEATHER);
    	armourTypeReduction.put(ArmourTypes.ARMOUR_LEATHER, 0.5f);
    	armourTypeReference.put("studded", ArmourTypes.ARMOUR_STUDDED);
    	armourTypeReduction.put(ArmourTypes.ARMOUR_STUDDED, 0.55f);
    	armourTypeReference.put("chain", ArmourTypes.ARMOUR_CHAIN);
    	armourTypeReduction.put(ArmourTypes.ARMOUR_CHAIN, 0.6f);
    	armourTypeReference.put("plate", ArmourTypes.ARMOUR_PLATE);
    	armourTypeReduction.put(ArmourTypes.ARMOUR_PLATE, 0.7f);
    	armourTypeReference.put("drake", ArmourTypes.ARMOUR_LEATHER_DRAGON);
    	armourTypeReduction.put(ArmourTypes.ARMOUR_LEATHER_DRAGON, 0.7f);
    	armourTypeReference.put("dragonscale", ArmourTypes.ARMOUR_SCALE_DRAGON);
    	armourTypeReduction.put(ArmourTypes.ARMOUR_SCALE_DRAGON, 0.75f);
    	armourTypeReference.put("scale", ArmourTypes.ARMOUR_SCALE);
    	armourTypeReduction.put(ArmourTypes.ARMOUR_SCALE, 0.5f);
    	armourTypeReference.put("ring", ArmourTypes.ARMOUR_RING);
    	armourTypeReduction.put(ArmourTypes.ARMOUR_RING, 0.55f);
    	armourTypeReference.put("splint", ArmourTypes.ARMOUR_SPLINT);
    	armourTypeReduction.put(ArmourTypes.ARMOUR_SPLINT, 0.6f);
    }
}
