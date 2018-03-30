package mod.sin.armoury;

import java.util.logging.Logger;

import mod.sin.lib.Util;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import com.wurmonline.server.Server;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.shared.constants.Enchants;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class ShieldTweaks {
	public static Logger logger = Logger.getLogger(ShieldTweaks.class.getName());
	public static boolean checkShieldSpeed(Item shield){
		if(shield != null && shield.getSpellSpeedBonus() > Server.rand.nextInt(500)){
			return true;
		}
		return false;
	}
	
	public static void doSharedPain(Creature attacker, Creature defender, Item shield){
		if(shield.getSpellPainShare() > 0f){
			float powerReq = Server.rand.nextInt(300);
			if(!(powerReq < shield.getSpellPainShare())){
				return;
			}
			boolean playSound = false;
			if(shield.getBonusForSpellEffect(Enchants.BUFF_ROTTING_TOUCH) > 0f){
				double damage = shield.getBonusForSpellEffect(Enchants.BUFF_ROTTING_TOUCH)*31d;
				attacker.addWoundOfType(defender, Wound.TYPE_INFECTION, 0, true, 1.0f, true, damage);
				playSound = true;
			}else if(shield.getBonusForSpellEffect(Enchants.BUFF_FLAMING_AURA) > 0f){
				double damage = shield.getBonusForSpellEffect(Enchants.BUFF_FLAMING_AURA)*27d;
				attacker.addWoundOfType(defender, Wound.TYPE_BURN, 0, true, 1.0f, true, damage);
				playSound = true;
			}else if(shield.getBonusForSpellEffect(Enchants.BUFF_FROSTBRAND) > 0f){
				double damage = shield.getBonusForSpellEffect(Enchants.BUFF_FROSTBRAND)*28d;
				attacker.addWoundOfType(defender, Wound.TYPE_COLD, 0, true, 1.0f, true, damage);
				playSound = true;
			}else if(shield.getBonusForSpellEffect(Enchants.BUFF_VENOM) > 0f){
				double damage = shield.getBonusForSpellEffect(Enchants.BUFF_VENOM)*30d;
				attacker.addWoundOfType(defender, Wound.TYPE_POISON, 0, true, 1.0f, true, damage);
				playSound = true;
			}
			if(playSound){
				SoundPlayer.playSound(attacker.getTemplate().getHitSound(attacker.getSex()), attacker.getTileX(), attacker.getTileY(), true, 0.3f);
			}
		}
	}
	
	public static void preInit(ArmouryMod mod){
        try {
        	ClassPool classPool = HookManager.getInstance().getClassPool();
        	Class<ShieldTweaks> thisClass = ShieldTweaks.class;
        	if(mod.enableShieldDamageEnchants){
        		CtClass ctCombatHandler = classPool.get("com.wurmonline.server.creatures.CombatHandler");
        		String replace = ShieldTweaks.class.getName()+".doSharedPain(this.creature, defender, defShield);"
	            		+ "$_ = $proceed($$);";
        		Util.setReason("Enable shield damage enchants.");
        		Util.instrumentDeclared(thisClass, ctCombatHandler, "checkShield", "setDamage", replace);
				/*ctCombatHandler.getDeclaredMethod("checkShield").instrument(new ExprEditor(){
				    public void edit(MethodCall m) throws CannotCompileException {
				        if (m.getMethodName().equals("setDamage")) {
				            m.replace(ShieldTweaks.class.getName()+".doSharedPain(this.creature, defender, defShield);"
				            		+ "$_ = $proceed($$);");
				            return;
				        }
				    }
				});*/
			}
        	if(mod.enableShieldSpeedEnchants){
        		CtClass ctCombatHandler = classPool.get("com.wurmonline.server.creatures.CombatHandler");
        		String insert = "if("+ShieldTweaks.class.getName()+".checkShieldSpeed(defender.getShield())){"
						+ "  defender.getCombatHandler().usedShieldThisRound--;"
						+ "}";
        		Util.setReason("Enable shield speed enchants.");
        		Util.insertBeforeDeclared(thisClass, ctCombatHandler, "checkShield", insert);
				/*ctCombatHandler.getDeclaredMethod("checkShield").insertBefore(""
						+ "if("+ShieldTweaks.class.getName()+".checkShieldSpeed(defender.getShield())){"
						+ "  defender.getCombatHandler().usedShieldThisRound--;"
						+ "}");*/
        	}
		}catch (NotFoundException e) {
			e.printStackTrace();
		}
	}
}
