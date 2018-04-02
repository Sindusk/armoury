package mod.sin.armoury;

import java.util.Map;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;

import com.wurmonline.server.combat.Weapon;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateFactory;

public class WeaponTweaks {
	public static Logger logger = Logger.getLogger(WeaponTweaks.class.getName());
	public static Map<Integer, Weapon> weapons;
	
	public static void printWeapons(){
		try{
			for(int i : weapons.keySet()){
				Weapon cw = weapons.get(i);
				ItemTemplate wt = ItemTemplateFactory.getInstance().getTemplateOrNull(i);
				if(wt == null){
					logger.warning("Null weapon template for id "+i);
				}else{
					logger.info("Weapon \""+wt.sizeString+wt.getName()+"\" (ID "+i+") stats: ["+
									ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "damage"))+" damage], ["+
									ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "speed"))+" speed], ["+
									ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "critchance"))+" critchance], ["+
									ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "reach"))+" reach], ["+
									ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "weightGroup"))+" weightGroup], ["+
									ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "parryPercent"))+" parryPercent], ["+
									ReflectionUtil.getPrivateField(cw, ReflectionUtil.getField(cw.getClass(), "skillPenalty"))+" skillPenalty]"
									);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	public static void editWeaponStats(ArmouryMod mod){
		try {
			Weapon cw;
			ItemTemplate it;
			String tweakType;
			tweakType = "damage";
			logger.info("Beginning weapon "+tweakType+" tweaks...");
			for(int id : mod.weaponDamage.keySet()){
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
				float newValue = mod.weaponDamage.get(id);
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
			for(int id : mod.weaponSpeed.keySet()){
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
				float newValue = mod.weaponSpeed.get(id);
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
			for(int id : mod.weaponCritChance.keySet()){
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
				float newValue = mod.weaponCritChance.get(id);
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
			for(int id : mod.weaponReach.keySet()){
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
				int newValue = mod.weaponReach.get(id);
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
			for(int id : mod.weaponWeightGroup.keySet()){
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
				int newValue = mod.weaponWeightGroup.get(id);
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
			for(int id : mod.weaponParryPercent.keySet()){
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
				float newValue = mod.weaponParryPercent.get(id);
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
			for(int id : mod.weaponSkillPenalty.keySet()){
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
				double newValue = mod.weaponSkillPenalty.get(id);
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
	
	public static void onServerStarted(ArmouryMod mod){
		try {
			logger.info("Beginning WeaponTweaks initialization...");
			weapons = ReflectionUtil.getPrivateField(Weapon.class, ReflectionUtil.getField(Weapon.class, "weapons"));
			
			//printWeapons(); // For debugging/information purposes

			editWeaponStats(mod);
			
			//printWeapons(); // For debugging/information purposes
			
		} catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
}
