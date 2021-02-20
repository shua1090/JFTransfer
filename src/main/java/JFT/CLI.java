package JFT;

import java.util.Arrays;
import java.util.logging.Level;

import static JFT.JFT.logger;


public class CLI {
    // 0-> Default logging; 1 -> Finer; 2 -> All;

    public static void main(String[] args) {
        logger.setLevel(Level.INFO);
        logger.log(Level.FINE, "HO");

//        CLI jArgs = new CLI();
//        JCommander helloCmd = JCommander.newBuilder()
//                .addObject(jArgs)
//                .build();
//        helloCmd.parse(args);
//        System.out.println("Hello " + jArgs.mode);

        switch (args[0]) {
            case "init": {
                JFT.initializeRepository();
                break;
            }
            case "push": {
//                if (args.length == 2 && args[1].equals("force")) {
//
//                }
                JFT.push();
                break;
            }

            case "set": {
                if (args.length != 3) {
                    logger.log(Level.SEVERE, "You did not give the proper number of arguments.");
                    System.out.println(Arrays.toString(args));
                    System.exit(-1);
                }
                Settings.setProp(args[0], args[1]);
                System.out.println(Arrays.toString(args));
            }

            case "serve": {
                var z = new Server(5000, JFT.getInput("Password: "));
            }
        }

    }
}
