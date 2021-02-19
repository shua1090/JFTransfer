package JFT;

import java.io.*;
import java.util.Arrays;
import java.util.Properties;

public class Settings {
    private static Properties prop = new Properties();;

    public static void readSettings() {

//        Properties prop = new Properties();
        String fileName = ".jft" + File.separator + "settings.cfg";
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
            prop.load(is);
        } catch (Exception ex) {
            System.out.println("Error creating Settings file: ");
            ex.printStackTrace();
        }

//        System.out.println(prop.getProperty("app.name"));
//        System.out.println(prop.getProperty("app.version"));

    }

    public static void writeAllConfigs(){
        var z = new String[]{
                "password",
                "port",
                "ipv4"
        };

        var file = new File(".jft" + File.separator + "settings.cfg");

        try {
//            prop.setProperty("key", "value");
            for (var k : z){
                try {
//                    prop.setProperty(k);
                    if (prop.getProperty(k) == null){
                        prop.setProperty(k, "UNSET");
                    }
                } catch (Exception e){System.out.println("Wait");}
            }

            OutputStream out = new FileOutputStream(file);
            prop.store(out, "Modify the settings as you see fit. If a setting is not valid, the program will use a default.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean createConfFile() {
        boolean stat = false;
        try {
            new File(".jft" + File.separator + "settings.cfg").createNewFile();
            writeAllConfigs();
            stat = true;
        } catch (Exception e) {
            stat = false;
        }

        return stat;
    }

    public static String getProp(String key) {
        return prop.getProperty(key);
    }

    public static void setProp(String key, String val) {
        prop.setProperty(key, val);
    }

    public static void main(String[] args){
//        boolean a = createConfFile();
//        readSettings();
//        System.out.println(getProp("encrypted"));

        setProp("encrypted", "true");
        createConfFile();
        writeAllConfigs();
    }

}
