import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Settings {
    private Properties prop;
    private final FileInputStream in;

    public Settings() throws IOException {
        this.prop = new Properties();
        if (!createConfFile("jft.conf")) throw new FileNotFoundException("Settings file not found, or unable to be created"); 
        this.in = new FileInputStream("jft.conf");
        prop.load(in);
        in.close();
    }

    public Settings(String fileName) throws IOException {
        this.prop = new Properties();
        if (!createConfFile("jft.conf")) throw new FileNotFoundException("Settings file not found, or unable to be created");
        this.in = new FileInputStream(fileName);
        prop.load(in);
        in.close();
    }

    public boolean createConfFile(String name) throws IOException {
        return new File(name).createNewFile();
    }

    public Properties getProp() {
        return prop;
    }

    public void setProp(Properties prop) {
        this.prop = prop;
    }
}
