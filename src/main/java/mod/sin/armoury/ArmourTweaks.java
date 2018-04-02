package mod.sin.armoury;

import java.util.logging.Logger;

import mod.sin.lib.Util;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import com.wurmonline.server.Server;
import com.wurmonline.server.combat.Armour;
import com.wurmonline.server.combat.ArmourTypes;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.Materials;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class ArmourTweaks {
	public static Logger logger = Logger.getLogger(ArmourTweaks.class.getName());
	
	public static ArmouryMod mod;
	
	public static float newGetArmourModFor(Item armour){
		int armourType;
        float toReturn = 0.0f;
    	if (armour != null && (armourType = armour.getArmourType()) > -1) {
        	if(mod.armourTypeReduction.containsKey(armourType)){
        		toReturn = mod.armourTypeReduction.get(armourType)-mod.unarmouredReduction;
        	}else{
        		logger.severe("[ERROR]: Could not find armour reduction reference for armour type "+armourType);
        		toReturn = 0f;
        	}
        	if (mod.armourReductionOverride.containsKey(armour.getTemplateId())){
        		toReturn = mod.armourReductionOverride.get(armour.getTemplateId())-mod.unarmouredReduction;
        	}
        	if ((armourType == ArmourTypes.ARMOUR_RING || armourType == ArmourTypes.ARMOUR_CHAIN) && armour.getMaterial() == Materials.MATERIAL_STEEL){
        		toReturn += 0.02f;
        	}
            if (armour.getMaterial() == Materials.MATERIAL_GLIMMERSTEEL){
                toReturn += mod.glimmersteelMaterialMod;
            } else if(armour.getMaterial() == Materials.MATERIAL_SERYLL) {
                toReturn += mod.seryllMaterialMod;
            } else if (armour.getMaterial() == Materials.MATERIAL_ADAMANTINE) {
                toReturn += mod.adamantineMaterialMod;
            }
            toReturn *= 1.0f + Armour.getRarityArmourBonus(armour.getRarity());
            toReturn = mod.unarmouredReduction + (float)((double)toReturn * Server.getBuffedQualityEffect(armour.getCurrentQualityLevel() / 100.0f));
        }
        return 1.0f - toReturn;
	}
	
	public static void setArmourLimitFactors(ArmouryMod mod){
		try{
			logger.info("Setting armour limit factors");
			for(Armour armour : mod.clothArmour){
				ReflectionUtil.setPrivateField(armour, ReflectionUtil.getField(armour.getClass(), "limitingFactor"), mod.clothArmourLimitFactor);
			}
			for(Armour armour : mod.leatherArmour){
				ReflectionUtil.setPrivateField(armour, ReflectionUtil.getField(armour.getClass(), "limitingFactor"), mod.leatherArmourLimitFactor);
			}
			for(Armour armour : mod.studdedArmour){
				ReflectionUtil.setPrivateField(armour, ReflectionUtil.getField(armour.getClass(), "limitingFactor"), mod.studdedArmourLimitFactor);
			}
			for(Armour armour : mod.chainArmour){
				ReflectionUtil.setPrivateField(armour, ReflectionUtil.getField(armour.getClass(), "limitingFactor"), mod.chainArmourLimitFactor);
			}
			for(Armour armour : mod.plateArmour){
				ReflectionUtil.setPrivateField(armour, ReflectionUtil.getField(armour.getClass(), "limitingFactor"), mod.plateArmourLimitFactor);
			}
			for(Armour armour : mod.drakeArmour){
				ReflectionUtil.setPrivateField(armour, ReflectionUtil.getField(armour.getClass(), "limitingFactor"), mod.drakeArmourLimitFactor);
			}
			for(Armour armour : mod.dragonscaleArmour){
				ReflectionUtil.setPrivateField(armour, ReflectionUtil.getField(armour.getClass(), "limitingFactor"), mod.dragonscaleArmourLimitFactor);
			}
		} catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	public static void preInit(ArmouryMod mod){
		ArmourTweaks.mod = mod;
        try {
	    	ClassPool classPool = HookManager.getInstance().getClassPool();
	    	final Class<ArmourTweaks> thisClass = ArmourTweaks.class;
	    	
	    	if(mod.enableArmourReductionModifications){
				CtClass ctArmour = classPool.get("com.wurmonline.server.combat.Armour");
				String body = ""
						+ "{"
						+ "  return "+ArmourTweaks.class.getName()+".newGetArmourModFor($1);"
						+ "}";
				Util.setBodyDeclared(thisClass, ctArmour, "getArmourModFor", body);
				/*ctArmour.getDeclaredMethod("getArmourModFor").setBody("{ "
						+ "return "+ArmourTweaks.class.getName()+".newGetArmourModFor($1); }");*/
	    	}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void onItemTemplatesCreated(ArmouryMod mod){
		try {
			if(mod.enableArmourMovementModifications){
				logger.info("Starting armour movement modifications...");
				for(String armourName : mod.armourMovement.keySet()){
					int armourTemplate;
					if(mod.armourNameToItemTemplate.containsKey(armourName)){
						armourTemplate = mod.armourNameToItemTemplate.get(armourName);
					}else{
						logger.severe("[ERROR]: Could not edit armour movement for item name \""+armourName+"\". It may be invalid.");
						continue;
					}
					Armour armourToEdit = Armour.getArmour(armourTemplate);
					if(armourToEdit != null){
						float oldValue = ReflectionUtil.getPrivateField(armourToEdit, ReflectionUtil.getField(armourToEdit.getClass(), "movemodifier"));
						ReflectionUtil.setPrivateField(armourToEdit, ReflectionUtil.getField(armourToEdit.getClass(), "movemodifier"), mod.armourMovement.get(armourName));
						logger.info("Editing movement modifier for armour \""+armourName+"\": From "+oldValue+" to "+mod.armourMovement.get(armourName));
					}else{
						logger.severe("[ERROR]: Could not edit armour movement for item name \""+armourName+"\". It may be invalid.");
					}
				}
			}
			if(mod.enableCustomArmourLimitFactors){
				setArmourLimitFactors(mod);
			}
		} catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
}
