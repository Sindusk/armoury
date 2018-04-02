package mod.sin.armoury;

import mod.sin.lib.Util;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.CombatHandler;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.SpellEffectsEnum;
import com.wurmonline.server.items.Item;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class CombatTweaks {
	public static Item handleDualWieldAttack(CombatHandler handler, Creature opponent, float delta){
		try {
		Creature performer = ReflectionUtil.getPrivateField(handler, ReflectionUtil.getField(handler.getClass(), "creature"));
			for(Item weapon : performer.getSecondaryWeapons()){
				if(Server.rand.nextBoolean()) continue;
				float time = handler.getSpeed(weapon);
				float timer = performer.addToWeaponUsed(weapon, delta);
				if(timer > time){
					performer.deductFromWeaponUsed(weapon, time);
					return weapon;
				}
			}
			return null;
		} catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void preInit(ArmouryMod mod){
        try {
			ClassPool classPool = HookManager.getInstance().getClassPool();
			Class<CombatTweaks> thisClass = CombatTweaks.class;
	    	
	        // - Allow critical hits on creatures as well as players -
			mod.enableNonPlayerCrits = false; // Disabled for now as it's not working.
			if(mod.enableNonPlayerCrits){
		        CtClass ctCombatHandler = classPool.get("com.wurmonline.server.creatures.CombatHandler");
		        CtClass[] attackParams1 = {
		        		classPool.get("com.wurmonline.server.creatures.Creature"),
		        		classPool.get("com.wurmonline.server.items.Item"),
		        		CtPrimitiveType.booleanType
		        };
		        String desc = Descriptor.ofMethod(CtPrimitiveType.booleanType,  attackParams1);
		        Util.setReason("Enable player critical strikes on creatures.");
		        Util.instrumentDescribed(thisClass, ctCombatHandler, "attack", desc, "isPlayer", "$_ = true");
				/*ctCombatHandler.getMethod("attack", Descriptor.ofMethod(CtPrimitiveType.booleanType, attackParams1)).instrument(new ExprEditor(){
				    public void edit(MethodCall m) throws CannotCompileException {
				        if (m.getMethodName().equals("isPlayer")) {
				        	m.replace("$_ = true;");
				            return;
				        }
				    }
				});*/
		        CtClass[] attackParams2 = {
		        		classPool.get("com.wurmonline.server.creatures.Creature"),
		        		classPool.get("com.wurmonline.server.creatures.AttackAction")
		        };
		        desc = Descriptor.ofMethod(CtPrimitiveType.booleanType,  attackParams2);
		        Util.setReason("Enable player critical strikes on creatures.");
		        Util.instrumentDescribed(thisClass, ctCombatHandler, "attack", desc, "isPlayer", "$_ = true");
		        /*ctCombatHandler.getMethod("attack", Descriptor.ofMethod(CtPrimitiveType.booleanType, attackParams2)).instrument(new ExprEditor(){
				    public void edit(MethodCall m) throws CannotCompileException {
				        if (m.getMethodName().equals("isPlayer")) {
				        	m.replace("$_ = true;");
				            return;
				        }
				    }
				});*/
			}
			
			// - Fix the Armour Limit being shown in the buff bar at all times -
			if(mod.fixArmourLimitBuffBug){
				CtClass ctPlayerInfo = classPool.get("com.wurmonline.server.players.PlayerInfo");
				String replace = ""
						+ "communicator.sendRemoveSpellEffect(com.wurmonline.server.creatures.SpellEffectsEnum.ARMOUR_LIMIT_NONE);"
                		+ "communicator.sendRemoveSpellEffect(com.wurmonline.server.creatures.SpellEffectsEnum.ARMOUR_LIMIT_MEDIUM);"
                		+ "communicator.sendRemoveSpellEffect(com.wurmonline.server.creatures.SpellEffectsEnum.ARMOUR_LIMIT_HEAVY);"
                		+ "$_ = $proceed($$);";
				Util.setReason("Fix armour limit buff bug.");
				Util.instrumentDeclared(thisClass, ctPlayerInfo, "setArmourLimitingFactor", "sendRemoveSpellEffect", replace);
		        /*ctPlayerInfo.getDeclaredMethod("setArmourLimitingFactor").instrument(new ExprEditor(){
		            public void edit(MethodCall m) throws CannotCompileException {
		                if (m.getMethodName().equals("sendRemoveSpellEffect")) {
		                    m.replace("communicator.sendRemoveSpellEffect(com.wurmonline.server.creatures.SpellEffectsEnum.ARMOUR_LIMIT_NONE);"
		                    		+ "communicator.sendRemoveSpellEffect(com.wurmonline.server.creatures.SpellEffectsEnum.ARMOUR_LIMIT_MEDIUM);"
		                    		+ "communicator.sendRemoveSpellEffect(com.wurmonline.server.creatures.SpellEffectsEnum.ARMOUR_LIMIT_HEAVY);"
		                    		+ "$_ = $proceed($$);");
		                    return;
		                }
		            }
		        });*/
				replace = "if($1 == null){"
                		+ "  $1 = com.wurmonline.server.creatures.SpellEffectsEnum.ARMOUR_LIMIT_NONE;"
                		+ "}"
                		+ "$_ = $proceed($$);"
                		+ "communicator.sendAddSpellEffect($1, 100000, this.limitingArmourFactor*100.0f);";
				Util.setReason("Fix armour limit buff bug.");
				Util.instrumentDeclared(thisClass, ctPlayerInfo, "setArmourLimitingFactor", "sendAddStatusEffect", replace);
		        ctPlayerInfo.getDeclaredMethod("setArmourLimitingFactor").instrument(new ExprEditor(){
		            public void edit(MethodCall m) throws CannotCompileException {
		                if (m.getMethodName().equals("sendAddStatusEffect")) {
		                    m.replace("if($1 == null){"
		                    		+ "  $1 = com.wurmonline.server.creatures.SpellEffectsEnum.ARMOUR_LIMIT_NONE;"
		                    		+ "}"
		                    		+ "$_ = $proceed($$);"
		                    		+ "communicator.sendAddSpellEffect($1, 100000, this.limitingArmourFactor*100.0f);");
		                }
		            }
		        });
			}
			
			// - Make spell effects hud show your armour limit properly - //
			if(mod.fixArmourLimitSpellEffect){
				ReflectionUtil.setPrivateField(SpellEffectsEnum.ARMOUR_LIMIT_HEAVY, ReflectionUtil.getField(SpellEffectsEnum.ARMOUR_LIMIT_HEAVY.getClass(), "name"), "Armour Penalty");
				ReflectionUtil.setPrivateField(SpellEffectsEnum.ARMOUR_LIMIT_MEDIUM, ReflectionUtil.getField(SpellEffectsEnum.ARMOUR_LIMIT_MEDIUM.getClass(), "name"), "Armour Penalty");
				ReflectionUtil.setPrivateField(SpellEffectsEnum.ARMOUR_LIMIT_LIGHT, ReflectionUtil.getField(SpellEffectsEnum.ARMOUR_LIMIT_LIGHT.getClass(), "name"), "Armour Bonus");
				ReflectionUtil.setPrivateField(SpellEffectsEnum.ARMOUR_LIMIT_NONE, ReflectionUtil.getField(SpellEffectsEnum.ARMOUR_LIMIT_NONE.getClass(), "name"), "Armour Bonus");
			}
			
			// - Change the minimum swing timer - //
			if(mod.minimumSwingTime != 3.0f){
				CtClass ctCombatHandler = classPool.get("com.wurmonline.server.creatures.CombatHandler");
				String strBuilder = "";
				if(mod.raresReduceSwingTime){
					strBuilder += ""
							+ "if(weapon.getRarity() > 0){"
							+ "  calcspeed -= weapon.getRarity()*"+String.valueOf(mod.rareSwingSpeedReduction)+"f;"
							+ "}";
				}
				strBuilder += "$_ = $proceed("+String.valueOf(mod.minimumSwingTime)+"f, $2);";
				
				final String stringReplace = strBuilder;
				CtClass[] params1 = {
						classPool.get("com.wurmonline.server.creatures.AttackAction"),
						classPool.get("com.wurmonline.server.items.Item")
				};
				String desc = Descriptor.ofMethod(CtClass.floatType, params1);
				Util.setReason("Adjust swing timer.");
				Util.instrumentDescribed(thisClass, ctCombatHandler, "getSpeed", desc, "max", stringReplace);
				/*ctCombatHandler.getMethod("getSpeed", desc).instrument(new ExprEditor(){
		            public void edit(MethodCall m) throws CannotCompileException {
		                if (m.getMethodName().equals("max")) {
		                    m.replace(stringReplace);
		                    return;
		                }
		            }
		        });*/
				CtClass[] params2 = { classPool.get("com.wurmonline.server.items.Item") };
				desc = Descriptor.ofMethod(CtClass.floatType, params2);
				Util.setReason("Adjust swing timer.");
				Util.instrumentDescribed(thisClass, ctCombatHandler, "getSpeed", desc, "max", stringReplace);
				/*ctCombatHandler.getMethod("getSpeed", desc).instrument(new ExprEditor(){
		            public void edit(MethodCall m) throws CannotCompileException {
		                if (m.getMethodName().equals("max")) {
		                    m.replace(stringReplace);
		                    return;
		                }
		            }
		        });*/
			}
			
			// - Saved swing timer fix -
			if(mod.fixSavedSwingTimer){
				CtClass ctCreature = classPool.get("com.wurmonline.server.creatures.Creature");
				String replace = "$_ = $proceed($1, new Float(0f));";
				Util.setReason("Fix saved swing timer.");
				Util.instrumentDeclared(thisClass, ctCreature, "deductFromWeaponUsed", "put", replace);
				/*ctCreature.getDeclaredMethod("deductFromWeaponUsed").instrument(new ExprEditor(){
		            public void edit(MethodCall m) throws CannotCompileException {
		                if (m.getMethodName().equals("put")) {
		                    m.replace("$_ = $proceed($1, new Float(0f));");
		                    return;
		                }
		            }
		        });*/
			}
			
			// - Attempt for a better dual wield system -
			// This really doesn't work. I don't get dual wield and why it's so bad.
			if(mod.betterDualWield){
				CtClass ctCombatHandler = classPool.get("com.wurmonline.server.creatures.CombatHandler");
				CtClass[] params1 = {
						classPool.get("com.wurmonline.server.creatures.Creature"),
						CtClass.intType,
						CtClass.booleanType,
						CtClass.floatType,
						classPool.get("com.wurmonline.server.behaviours.Action")
				};
				String desc = Descriptor.ofMethod(CtClass.booleanType, params1);
				String replace = "if(this.creature.isPlayer()){"
                		+ "  com.wurmonline.server.items.Item weapon = CombatTweaks.handleDualWieldAttack(this, opponent, delta);"
                		+ "  if(weapon != null){"
                		+ "    lDead = attack(opponent, weapon, true);"
                		+ "  }"
                		+ "}"
                		+ "$_ = $proceed($$);";
				Util.setReason("Better Dual Wield");
				Util.instrumentDescribed(thisClass, ctCombatHandler, "attack", desc, "getSecondaryWeapons", replace);
				/*ctCombatHandler.getMethod("attack", desc).instrument(new ExprEditor(){
		            public void edit(MethodCall m) throws CannotCompileException {
		                if (m.getMethodName().equals("getSecondaryWeapons")) {
		                    m.replace("if(this.creature.isPlayer()){"
		                    		+ "  com.wurmonline.server.items.Item weapon = CombatTweaks.handleDualWieldAttack(this, opponent, delta);"
		                    		+ "  if(weapon != null){"
		                    		+ "    lDead = attack(opponent, weapon, true);"
		                    		+ "  }"
		                    		+ "}"
		                    		+ "$_ = $proceed($$);");
		                    return;
		                }
		            }
		        });*/
				/*ctCombatHandler.getMethod("attack", desc).instrument(new ExprEditor(){
		            public void edit(MethodCall m) throws CannotCompileException {
		                if (m.getMethodName().equals("nextBoolean")) {
		                    m.replace("$_ = true;");
		                    return;
		                }
		            }
		        });*/
				replace = "if(this.creature.isPlayer()){"
                		+ "  $_ = 1;"
                		+ "}else{"
                		+ "  $_ = $proceed($$);"
                		+ "}";
				Util.setReason("Better Dual Wield");
				Util.instrumentDescribed(thisClass, ctCombatHandler, "attack", desc, "getHugeMoveCounter", replace);
				/*ctCombatHandler.getMethod("attack", desc).instrument(new ExprEditor(){
		            public void edit(MethodCall m) throws CannotCompileException {
		                if (m.getMethodName().equals("getHugeMoveCounter")) {
		                    m.replace("if(this.creature.isPlayer()){"
		                    		+ "  $_ = 1;"
		                    		+ "}else{"
		                    		+ "  $_ = $proceed($$);"
		                    		+ "}");
		                    return;
		                }
		            }
		        });*/
			}
			/*CtClass ctCreature = classPool.get("com.wurmonline.server.creatures.Creature");
			ctCreature.getDeclaredMethod("addToWeaponUsed").insertBefore("logger.info(\"Timer for \"+$1.getName()+\" = \"+this.weaponsUsed.get($1));");
			
			// Conclusion: This is the "main loop" for combat. It is called very frequently.
			CtClass ctCombatHandler = classPool.get("com.wurmonline.server.creatures.CombatHandler");
			CtClass[] params1 = {
					classPool.get("com.wurmonline.server.creatures.Creature"),
					CtClass.intType,
					CtClass.booleanType,
					CtClass.floatType,
					classPool.get("com.wurmonline.server.behaviours.Action")
			};
			String desc = Descriptor.ofMethod(CtClass.booleanType, params1);
			CtMethod ctAttack = ctCombatHandler.getMethod("attack", desc);
			//ctAttack.insertBefore("logger.info(\"Calling attack(Creature, int, boolean, float, Action)\");");
			ctAttack.instrument(new ExprEditor(){
	            public void edit(MethodCall m) throws CannotCompileException {
	                if (m.getMethodName().equals("deductFromWeaponUsed")) {
	                    m.replace("logger.info(\"Deducting from weapon: \"+$2);"
	                    		+ "$_ = $proceed($$);");
	                    return;
	                }
	            }
	        });*/
			
			// Conclusion: This apparently is not used in standard combat.
			/*CtClass[] params2 = {
					classPool.get("com.wurmonline.server.creatures.Creature"),
					classPool.get("com.wurmonline.server.creatures.AttackAction")
			};
			desc = Descriptor.ofMethod(CtClass.booleanType, params2);
			ctCombatHandler.getMethod("attack", desc).insertBefore("logger.info(\"Calling attack(Creature, AttackAction)\");");*/
			
			// Conclusion: This is called only when a swing is done.
			/*CtClass[] params3 = {
					classPool.get("com.wurmonline.server.creatures.Creature"),
					classPool.get("com.wurmonline.server.items.Item"),
					CtClass.booleanType
			};
			desc = Descriptor.ofMethod(CtClass.booleanType, params3);
			ctCombatHandler.getMethod("attack", desc).insertBefore("logger.info(\"Calling attack(Creature, Item, boolean)\");");*/
			
		} catch (CannotCompileException | NotFoundException | IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
