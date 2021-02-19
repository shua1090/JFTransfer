package JFT;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.beust.jcommander.Parameter;


public class CLI {
    public static Logger logger = Logger.getLogger(JFT.class.getName());


    // 0-> Default logging; 1 -> Finer; 2 -> All;

    @Parameter(names="-log", description = "level of verbosity", required = false)
    Integer mode = 0;

    @Parameter(names="password", description = "")

    public static void main(String[] args){
//        logger.setLevel(Level.INFO);
//        logger.log(Level.FINE, "HO");

        switch (args[0]){
            case "init":{
                JFT.initializeRepository();
                break;
            }
            case "push":{
                JFT.push();
                break;
            }
            case "serve":{
                Server.main(new String[]{""});
            }
        }

    }
}
