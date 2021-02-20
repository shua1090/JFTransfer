package JFT;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

import static JFT.JFT.logger;

public class Settings {
    private static final Properties prop = new Properties();

    public static void readSettings() {
        String fileName = ".jft" + File.separator + "settings.cfg";
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
            prop.load(is);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Couldn't read settings file. This is a major issue, please report it.");
            ex.printStackTrace();
            System.exit(-1);
        }

    }

    public static void writeAllConfigs() {
        var z = new String[]{
                "password",
                "port",
                "ipv4",
                "verbosity"
        };

        var file = new File(".jft" + File.separator + "settings.cfg");

        try {

            for (var k : z) {
                if (prop.getProperty(k) == null) {
                    prop.setProperty(k, "UNSET");
                }
            }
            OutputStream out = new FileOutputStream(file);
            prop.store(out, "Modify the settings as you see fit. If a setting is not valid, the program will use a default.");
            logger.log(Level.FINEST, "Stored config settings " + prop.keys().toString());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to write to the config file. Please report this. Exiting.");
            e.printStackTrace();
            System.exit(-1);
        }

    }

    public static boolean createConfFile() {
        boolean stat = false;
        try {
            new File(".jft" + File.separator + "settings.cfg").createNewFile();
            writeAllConfigs();
            stat = true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to create config file.");
        }

        return stat;
    }

    public static String getProp(String key) {
        return prop.getProperty(key);
    }

    public static void setProp(String key, String val) {
        if (prop.getProperty(key) == null) {
            logger.log(Level.SEVERE, "'" + key + "' is not considered a valid key. Please check Documentation for valid keys.");
        } else {
            prop.setProperty(key, val);
        }
    }

    public static void main(String[] args) {
//        boolean a = createConfFile();
//        readSettings();
//        System.out.println(getProp("encrypted"));

        setProp("encrypted", "true");
        createConfFile();
        writeAllConfigs();
    }

}
