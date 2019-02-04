package mod.sin.armoury;

import com.wurmonline.server.combat.Weapon;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.items.Materials;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import mod.sin.lib.Util;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class WeaponsTweaks {
	public static Logger logger = Logger.getLogger(WeaponsTweaks.class.getName());

	public static Map<Integer, Weapon> weapons; // Mirror of the Weapon class map

    public static HashMap<Byte, Double> materialWeaponDamage = new HashMap<>();
    public static HashMap<Byte, Float> materialWeaponSpeed = new HashMap<>();
    public static HashMap<Byte, Float> materialWeaponParry = new HashMap<>();
    public static HashMap<Byte, Double> materialWeaponArmourDamage = new HashMap<>();

	public static double newGetMaterialDamageBonus(byte material){
	    if(materialWeaponDamage.containsKey(material)){
	        //logger.info(String.format("Modifying damage by %.2f%% due to material type %s.", materialWeaponDamage.get(material)*100d, MaterialsTweaks.getMaterialName(material)));
	        return materialWeaponDamage.get(material);
        }
        return 1.0d;
    }

    public static float newGetBaseSpeedForWeapon(Item weapon){
        if (weapon == null || weapon.isBodyPartAttached()) {
            return 1.0f;
        }
        if(weapons.containsKey(weapon.getTemplateId())){
            Weapon weap = weapons.get(weapon.getTemplateId());
            try {
                float speed = ReflectionUtil.getPrivateField(weap, ReflectionUtil.getField(weap.getClass(), "speed"));
                //logger.info("Base speed: "+speed);
                byte material = weapon.getMaterial();
                if(materialWeaponSpeed.containsKey(material)){
                    speed *= materialWeaponSpeed.get(material);
                    //logger.info(String.format("Found material %s, modifying speed by %.2f%%. New speed: %s", MaterialsTweaks.getMaterialName(material), materialWeaponSpeed.get(material)*100f, speed));
                }
                return speed;
            } catch (IllegalAccessException | NoSuchFieldException e) {
                logger.warning("Could not find valid weapon speed for "+weapon.getName()+" ("+weapon.getTemplateId()+")");
                e.printStackTrace();
            }
        }else{
            logger.warning("Weapon map does not contain entry for "+weapon.getName()+" ("+weapon.getTemplateId()+")");
        }
        return 20.0f;
    }

    public static float newGetMaterialParryBonus(byte material){
	    if(materialWeaponParry.containsKey(material)){
            //logger.info(String.format("Modifying parry by %.2f%% due to material type %s.", materialWeaponParry.get(material)*100d, MaterialsTweaks.getMaterialName(material)));
            return materialWeaponParry.get(material);
        }
        return 1.0f;
    }

    public static double newGetMaterialArmourDamageBonus(byte material){
	    if(materialWeaponArmourDamage.containsKey(material)){
            //logger.info(String.format("Modifying armour damage by %.2f%% due to material type %s.", materialWeaponArmourDamage.get(material)*100d, MaterialsTweaks.getMaterialName(material)));
            return materialWeaponArmourDamage.get(material);
        }
	    return 1.0d;
    }

    public static void addMaterialWeaponDamage(byte material, double mult){
        materialWeaponDamage.put(material, mult);
    }

    public static void addMaterialWeaponSpeed(byte material, float mult){
        materialWeaponSpeed.put(material, mult);
    }

    public static void addMaterialWeaponParry(byte material, float mult){
        materialWeaponParry.put(material, mult);
    }

    public static void addMaterialWeaponArmourDamage(byte material, double mult){
        materialWeaponArmourDamage.put(material, mult);
    }
	
	public static void printWeapons(){
		try{
			for(int i : weapons.keySet()){
				Weapon cw = weapons.get(i);
				ItemTemplate wt = ItemTemplateFactory.getInstance().getTemplateOrNull(i);
				if(wt == null){
					logger.warning("Null weapon template for id "+i);
				}else{
					String str = "Weapon \""+wt.sizeString+wt.getName()+"\" (ID "+i+") stats: ";
					str += "["+ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "damage"))+" damage]";
					str += ", ["+ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "speed"))+" speed]";
					str += ", ["+ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "critchance"))+" critchance]";
					str += ", ["+ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "reach"))+" reach]";
					str += ", ["+ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "weightGroup"))+" weightGroup]";
					str += ", ["+ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "parryPercent"))+" parryPercent]";
					str += ", ["+ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "skillPenalty"))+" skillPenalty]";
					str += ", [TYPES: ";
					String typeString = "";
					if(wt.isWeaponAxe()){
						typeString += "Axe";
					}
					if(wt.isWeaponCrush()){
					    if(!typeString.isEmpty()){ typeString += ", "; }
					    typeString += "Crush";
                    }
                    if(wt.isWeaponKnife()){
					    if(!typeString.isEmpty()){ typeString += ", "; }
					    typeString += "Knife";
                    }
                    if(wt.isWeaponMelee()){
					    if(!typeString.isEmpty()){ typeString += ", "; }
					    typeString += "Melee";
                    }
                    if(wt.isWeaponMisc()){
					    if(!typeString.isEmpty()){ typeString += ", "; }
					    typeString += "Misc";
                    }
                    if(wt.isWeaponPierce()){
					    if(!typeString.isEmpty()){ typeString += ", "; }
					    typeString += "Pierce";
                    }
                    if(wt.isWeaponPolearm()){
					    if(!typeString.isEmpty()){ typeString += ", "; }
					    typeString += "Polearm";
                    }
                    if(wt.isWeaponSlash()){
					    if(!typeString.isEmpty()){ typeString += ", "; }
					    typeString += "Slash";
                    }
                    if(wt.isWeaponSword()){
					    if(!typeString.isEmpty()){ typeString += ", "; }
					    typeString += "Sword";
                    }
					str += typeString;
					str += "]";
					logger.info(str);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	public static void editWeaponStats(){
		try {
			Weapon cw;
			ItemTemplate it;
			String tweakType;
			tweakType = "damage";
			logger.info("Beginning weapon "+tweakType+" tweaks...");
			for(int id : ArmouryModMain.weaponDamage.keySet()){
				it = ItemTemplateFactory.getInstance().getTemplateOrNull(id);
				if(it == null){
					logger.severe("[ERROR]: Item template for id "+id+" in weapon "+tweakType+" configuration is invalid.");
					continue;
				}
				cw = weapons.get(id);
				if(cw == null){
					logger.severe("[ERROR]: Weapon for id "+id+" in the weapon "+tweakType+" configuration is invalid.");
					continue;
				}
				float oldValue = ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "damage"));
				float newValue = ArmouryModMain.weaponDamage.get(id);
				String diff;
				if(newValue > oldValue){
					diff = "+"+(newValue-oldValue);
				}else{
					diff = String.valueOf(newValue-oldValue);
				}
				logger.info("Setting damage on "+it.sizeString+it.getName()+" to "+newValue+" from "+oldValue+" ("+diff+")");
				ReflectionUtil.setPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "damage"), newValue);
			}
			tweakType = "speed";
			logger.info("Beginning weapon "+tweakType+" tweaks...");
			for(int id : ArmouryModMain.weaponSpeed.keySet()){
				it = ItemTemplateFactory.getInstance().getTemplateOrNull(id);
				if(it == null){
					logger.severe("[ERROR]: Item template for id "+id+" in weapon "+tweakType+" configuration is invalid. Please double check your configuration.");
					continue;
				}
				cw = weapons.get(id);
				if(cw == null){
					logger.severe("[ERROR]: Weapon for id "+id+" in the weapon "+tweakType+" configuration is invalid.");
					continue;
				}
				float oldValue = ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "speed"));
				float newValue = ArmouryModMain.weaponSpeed.get(id);
				String diff;
				if(newValue > oldValue){
					diff = "+"+(newValue-oldValue);
				}else{
					diff = String.valueOf(newValue-oldValue);
				}
				logger.info("Setting speed on "+it.sizeString+it.getName()+" to "+newValue+" from "+oldValue+" ("+diff+")");
				ReflectionUtil.setPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "speed"), newValue);
			}
			tweakType = "crit chance";
			logger.info("Beginning weapon "+tweakType+" tweaks...");
			for(int id : ArmouryModMain.weaponCritChance.keySet()){
				it = ItemTemplateFactory.getInstance().getTemplateOrNull(id);
				if(it == null){
					logger.severe("[ERROR]: Item template for id "+id+" in weapon "+tweakType+" configuration is invalid. Please double check your configuration.");
					continue;
				}
				cw = weapons.get(id);
				if(cw == null){
					logger.severe("[ERROR]: Weapon for id "+id+" in the weapon "+tweakType+" configuration is invalid.");
					continue;
				}
				float oldValue = ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "critchance"));
				float newValue = ArmouryModMain.weaponCritChance.get(id);
				String diff;
				if(newValue > oldValue){
					diff = "+"+(newValue-oldValue);
				}else{
					diff = String.valueOf(newValue-oldValue);
				}
				logger.info("Setting crit chance on "+it.sizeString+it.getName()+" to "+newValue+" from "+oldValue+" ("+diff+")");
				ReflectionUtil.setPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "critchance"), newValue);
			}
			tweakType = "reach";
			logger.info("Beginning weapon "+tweakType+" tweaks...");
			for(int id : ArmouryModMain.weaponReach.keySet()){
				it = ItemTemplateFactory.getInstance().getTemplateOrNull(id);
				if(it == null){
					logger.severe("[ERROR]: Item template for id "+id+" in weapon "+tweakType+" configuration is invalid. Please double check your configuration.");
					continue;
				}
				cw = weapons.get(id);
				if(cw == null){
					logger.severe("[ERROR]: Weapon for id "+id+" in the weapon "+tweakType+" configuration is invalid.");
					continue;
				}
				int oldValue = ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "reach"));
				int newValue = ArmouryModMain.weaponReach.get(id);
				String diff;
				if(newValue > oldValue){
					diff = "+"+(newValue-oldValue);
				}else{
					diff = String.valueOf(newValue-oldValue);
				}
				logger.info("Setting reach on "+it.sizeString+it.getName()+" to "+newValue+" from "+oldValue+" ("+diff+")");
				ReflectionUtil.setPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "reach"), newValue);
			}
			tweakType = "weight group";
			logger.info("Beginning weapon "+tweakType+" tweaks...");
			for(int id : ArmouryModMain.weaponWeightGroup.keySet()){
				it = ItemTemplateFactory.getInstance().getTemplateOrNull(id);
				if(it == null){
					logger.severe("[ERROR]: Item template for id "+id+" in weapon "+tweakType+" configuration is invalid. Please double check your configuration.");
					continue;
				}
				cw = weapons.get(id);
				if(cw == null){
					logger.severe("[ERROR]: Weapon for id "+id+" in the weapon "+tweakType+" configuration is invalid.");
					continue;
				}
				int oldValue = ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "weightGroup"));
				int newValue = ArmouryModMain.weaponWeightGroup.get(id);
				String diff;
				if(newValue > oldValue){
					diff = "+"+(newValue-oldValue);
				}else{
					diff = String.valueOf(newValue-oldValue);
				}
				logger.info("Setting weight group on "+it.sizeString+it.getName()+" to "+newValue+" from "+oldValue+" ("+diff+")");
				ReflectionUtil.setPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "weightGroup"), newValue);
			}
			tweakType = "parry percent";
			logger.info("Beginning weapon "+tweakType+" tweaks...");
			for(int id : ArmouryModMain.weaponParryPercent.keySet()){
				it = ItemTemplateFactory.getInstance().getTemplateOrNull(id);
				if(it == null){
					logger.severe("[ERROR]: Item template for id "+id+" in weapon "+tweakType+" configuration is invalid. Please double check your configuration.");
					continue;
				}
				cw = weapons.get(id);
				if(cw == null){
					logger.severe("[ERROR]: Weapon for id "+id+" in the weapon "+tweakType+" configuration is invalid.");
					continue;
				}
				float oldValue = ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "parryPercent"));
				float newValue = ArmouryModMain.weaponParryPercent.get(id);
				String diff;
				if(newValue > oldValue){
					diff = "+"+(newValue-oldValue);
				}else{
					diff = String.valueOf(newValue-oldValue);
				}
				logger.info("Setting parry percent on "+it.sizeString+it.getName()+" to "+newValue+" from "+oldValue+" ("+diff+")");
				ReflectionUtil.setPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "parryPercent"), newValue);
			}
			tweakType = "skill penalty";
			logger.info("Beginning weapon "+tweakType+" tweaks...");
			for(int id : ArmouryModMain.weaponSkillPenalty.keySet()){
				it = ItemTemplateFactory.getInstance().getTemplateOrNull(id);
				if(it == null){
					logger.severe("[ERROR]: Item template for id "+id+" in weapon "+tweakType+" configuration is invalid. Please double check your configuration.");
					continue;
				}
				cw = weapons.get(id);
				if(cw == null){
					logger.severe("[ERROR]: Weapon for id "+id+" in the weapon "+tweakType+" configuration is invalid.");
					continue;
				}
				double oldValue = ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "skillPenalty"));
				double newValue = ArmouryModMain.weaponSkillPenalty.get(id);
				String diff;
				if(newValue > oldValue){
					diff = "+"+(newValue-oldValue);
				}else{
					diff = String.valueOf(newValue-oldValue);
				}
				logger.info("Setting skill penalty on "+it.sizeString+it.getName()+" to "+newValue+" from "+oldValue+" ("+diff+")");
				ReflectionUtil.setPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "skillPenalty"), newValue);
			}
		} catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public static void initializeWeaponMaps(){
	    // Material weapon damage
	    materialWeaponDamage.put(Materials.MATERIAL_ADAMANTINE, 1.1d);
	    materialWeaponDamage.put(Materials.MATERIAL_BRASS, 0.99d);
	    materialWeaponDamage.put(Materials.MATERIAL_BRONZE, 0.985d);
	    materialWeaponDamage.put(Materials.MATERIAL_COPPER, 0.65d);
	    materialWeaponDamage.put(Materials.MATERIAL_GOLD, 0.975d);
	    materialWeaponDamage.put(Materials.MATERIAL_LEAD, 0.5d);
	    materialWeaponDamage.put(Materials.MATERIAL_SERYLL, 1.05d);
	    materialWeaponDamage.put(Materials.MATERIAL_TIN, 0.925d);
	    materialWeaponDamage.put(Materials.MATERIAL_ZINC, 0.9d);

	    // Material weapon speed
        materialWeaponSpeed.put(Materials.MATERIAL_GLIMMERSTEEL, 0.9f);
        materialWeaponSpeed.put(Materials.MATERIAL_GOLD, 1.05f);
        materialWeaponSpeed.put(Materials.MATERIAL_SERYLL, 0.95f);
        materialWeaponSpeed.put(Materials.MATERIAL_TIN, 0.96f);
        materialWeaponSpeed.put(Materials.MATERIAL_ZINC, 0.95f);

        // Material weapon parry
        materialWeaponParry.put(Materials.MATERIAL_SILVER, 1.025f);
        materialWeaponParry.put(Materials.MATERIAL_TIN, 1.05f);

        // Material weapon armour damage
        materialWeaponArmourDamage.put(Materials.MATERIAL_BRASS, 1.05d);
        materialWeaponArmourDamage.put(Materials.MATERIAL_BRONZE, 1.075d);
        materialWeaponArmourDamage.put(Materials.MATERIAL_GOLD, 1.05d);
        materialWeaponArmourDamage.put(Materials.MATERIAL_STEEL, 1.025d);
    }

	public static void preInit(){
		try {
			ClassPool classPool = HookManager.getInstance().getClassPool();
			final Class<WeaponsTweaks> thisClass = WeaponsTweaks.class;
			String replace;

			if(ArmouryModMain.enableWeaponMaterialChanges){
                Util.setReason("Enable weapon material damage modifications.");
                CtClass ctWeapon = classPool.get("com.wurmonline.server.combat.Weapon");
                replace = "{"
                        + "  return "+WeaponsTweaks.class.getName()+".newGetMaterialDamageBonus($1);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctWeapon, "getMaterialDamageBonus", replace);

                Util.setReason("Enable weapon material speed modifications.");
                replace = "{"
                        + "  return "+WeaponsTweaks.class.getName()+".newGetBaseSpeedForWeapon($1);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctWeapon, "getBaseSpeedForWeapon", replace);

                Util.setReason("Enable weapon material parry modifications.");
                replace = "{"
                        + "  return "+WeaponsTweaks.class.getName()+".newGetMaterialParryBonus($1);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctWeapon, "getMaterialParryBonus", replace);

                Util.setReason("Enable weapon material armour damage modifications.");
                replace = "{"
                        + "  return "+WeaponsTweaks.class.getName()+".newGetMaterialArmourDamageBonus($1);"
                        + "}";
                Util.setBodyDeclared(thisClass, ctWeapon, "getMaterialArmourDamageBonus", replace);
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void onServerStarted(){
		try {
			logger.info("Beginning WeaponsTweaks initialization...");
			weapons = ReflectionUtil.getPrivateField(Weapon.class, ReflectionUtil.getField(Weapon.class, "weapons"));
			
			//printWeapons(); // For debugging/information purposes

			editWeaponStats();
			
			printWeapons(); // For debugging/information purposes
			
		} catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
}
