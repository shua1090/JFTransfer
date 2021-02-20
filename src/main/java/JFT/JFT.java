package JFT;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class JFT {
    public static HashMap<Path, String> pathBindings = new HashMap<>();
    public static ArrayList<Path> pathsInDirectoryList;
    public static ArrayList<Path> JFTIgnore = new ArrayList<>(Collections.singletonList(Path.of(".jft")));

    public static Logger logger = Logger.getLogger(JFT.class.getName());
    static String jftDir = System.getProperty("user.dir") + "/.jft/";

    static boolean isChild(File maybeChild, File possibleParent) {
        return maybeChild.getAbsolutePath().startsWith(possibleParent.getAbsolutePath());
    }

    public static ArrayList<Path> allRelativeFilePathsInDirectory() {

        ArrayList<Path> arr = new ArrayList<>();
        Path path = Path.of(System.getProperty("user.dir"));
        try (Stream<Path> pathStream = Files.walk(path).filter(Files::isRegularFile)) {

            for (Path file : (Iterable<Path>) pathStream::iterator) {
                // something that throws IOException

                boolean shouldIgnore = false;

                for (var z : JFTIgnore) {
                    if (isChild(file.toFile(), z.toFile())) {
                        shouldIgnore = true;
                    }
                }
                if (!shouldIgnore)
                    arr.add(path.relativize(file));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        pathsInDirectoryList = arr;

        return arr;
    }

    public static void getShaMDFileArray(ArrayList<Path> fileList) {

        for (var z : fileList) {
            try {
                byte[] data = Files.readAllBytes(z);
                String hash = getShaMD(data);
                pathBindings.put(z, hash);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error in file " + z + ". See stacktrace below.");
                e.printStackTrace();
//                System.out.println("ERROR IN " + z);
            }
        }

        // System.out.println(pathBindings);

    }

    /*
     * A: A B C B: A B D -> C
     *
     * A: A B D B: A B C -> D
     */

    public static String getShaMD(byte[] inputArray) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (Exception E) {
            logger.log(Level.SEVERE, "An error occurred when acquiring the SHA-256 Algorithm. Please report the stacktrace below: ");
            E.printStackTrace();
            System.exit(-1);
        }

        assert md != null;

        var hash = md.digest(inputArray);

        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    // Index 0: Additions; Index 1: Deletions;
    public static ArrayList<ArrayList<Path>> getDifferences(ArrayList<Path> ListA, ArrayList<Path> ListB) {

        ArrayList<Path> deletions = new ArrayList<>(ListA);
        ArrayList<Path> additions = new ArrayList<>(ListB);

        deletions.removeAll(ListB);
        additions.removeAll(ListA);

        logger.log(Level.FINEST, "Deletions: " + deletions);
        logger.log(Level.FINEST, "Additions: " + additions);

        var arr = new ArrayList<ArrayList<Path>>(2);
        arr.add(additions);
        arr.add(deletions);

        return arr;
    }

    public static ArrayList<Path> getModifiedFiles(HashMap<Path, String> originalHash, ArrayList<Path> originalList) {
        ArrayList<Path> modifiedFileList = new ArrayList<>();

        for (Path p : originalList) {
            if (!originalHash.get(p).equals(pathBindings.get(p))) {
                modifiedFileList.add(p);
            }
        }

        return modifiedFileList;

    }

    public static void writeArray(String filename, ArrayList<Path> arr) throws IOException {
        FileWriter writer = new FileWriter(filename);
//        int len = arr.size();
        for (Path path : arr) {
            writer.write(path + ":" + pathBindings.get(path) + "\n");
        }
        writer.close();
    }

    public static HashMap<Path, String> readArray(String filename) {
        String line;
        HashMap<Path, String> serializedData = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                // arr.add(Path.of(line));
                String pathGet = line.substring(0, line.indexOf(":"));
                String hashGet = line.substring(line.indexOf(":") + 1);
                serializedData.put(Path.of(pathGet), hashGet);
            }

            logger.log(Level.FINEST, "Read prevcom.file");

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error reading/loading changelog");
            ex.printStackTrace();
            System.exit(-1);
        }

        return serializedData;
    }

    public static void readJFTIgnore(String filename) {
        String line;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                JFTIgnore.add(Path.of(line));
            }

            logger.log(Level.FINEST, "Read .jftignore");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ".jftignore was not found. This file should've been initialized when the repository was initialized.");
            ex.printStackTrace();
        }

    }

    public static void initializeRepository() {

        getShaMDFileArray(allRelativeFilePathsInDirectory());
        try {
            Files.createDirectories(Path.of(jftDir));
            writeArray(jftDir + "prevcom.file", pathsInDirectoryList);

            Settings.createConfFile();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Was unable to create the required configuration files under .jft/");
            e.printStackTrace();
        }

    }

    public static String getInput(String prompt) {
        String text = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println(prompt);
            text = reader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Did not get any input. Exiting.");
            System.exit(-1);
        }

        return text;
    }

    public static void push() {

        Settings.readSettings();

        var changeLogArr = readArray(jftDir + File.separator + "prevcom.file");

        readJFTIgnore(jftDir + File.separator + ".jftignore");

        getShaMDFileArray(allRelativeFilePathsInDirectory());

        var temp = new ArrayList<Path>(changeLogArr.keySet());

        temp.retainAll(new ArrayList<Path>(pathBindings.keySet()));

        var modifiedFiles = getModifiedFiles(changeLogArr, temp);

        var changed = getDifferences(new ArrayList<Path>(changeLogArr.keySet()), new ArrayList<Path>(pathBindings.keySet()));

        int port = (!Settings.getProp("port").equals("UNSET")) ? Integer.parseInt(Settings.getProp("port")) : 5000;

        logger.log(Level.FINEST, "Sending through port " + port);

        String ipaddr = (!Settings.getProp("ipv4").equals("UNSET")) ? Settings.getProp("ipv4") : getInput("IP Address:");

        logger.log(Level.FINEST, "IP address: " + ipaddr);

        Console console = System.console();

        String passwd;

        if (console != null)
            passwd = String.valueOf(console.readPassword("password"));
        else
            passwd = (!Settings.getProp("password").equals("UNSET")) ? Settings.getProp("password") : getInput("Password: ");

        var cli = new Client(ipaddr, port, passwd);

        logger.log(Level.INFO, "Sending files now");
        cli.sendFiles(modifiedFiles, changed);
        logger.log(Level.INFO, "Finished sending files. Updating changelog now.");

        getShaMDFileArray(allRelativeFilePathsInDirectory());
        try {
            writeArray(jftDir + "prevcom.file", pathsInDirectoryList);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing to changelog. Report this error, the program is useless if this writing to changelog doesn't work.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void main(String[] args) throws IOException {
        push();
    }
}
