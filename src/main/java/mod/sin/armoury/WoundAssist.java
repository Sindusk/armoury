package mod.sin.armoury;

import java.util.HashMap;
import java.util.logging.Logger;

public class WoundAssist {
    public static Logger logger = Logger.getLogger(WoundAssist.class.getName());

    public static HashMap<String, Byte> woundNameToType = new HashMap<>();
    public static HashMap<Byte, String> woundTypeToName = new HashMap<>();

    public static void initializeWoundMaps(){
        woundNameToType.put("crush", (byte) 0);
        woundNameToType.put("slash", (byte) 1);
        woundNameToType.put("pierce", (byte) 2);
        woundNameToType.put("bite", (byte) 3);
        woundNameToType.put("burn", (byte) 4);
        woundNameToType.put("poison", (byte) 5);
        woundNameToType.put("infection", (byte) 6);
        woundNameToType.put("water", (byte) 7);
        woundNameToType.put("cold", (byte) 8);
        woundNameToType.put("internal", (byte) 9);
        woundNameToType.put("acid", (byte) 10);
        for(String name : woundNameToType.keySet()){
            woundTypeToName.put(woundNameToType.get(name), name);
        }
    }
}
