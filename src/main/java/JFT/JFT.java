package JFT;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Stream;
import java.util.logging.*;

import java.util.logging.Logger;

public class JFT {
    public static HashMap<Path, String> pathBindings = new HashMap<>();
    public static ArrayList<Path> pathsInDirectoryList;
    public static ArrayList<Path> JFTIgnore = new ArrayList<>(Collections.singletonList(Path.of(".jft")));

    public static Logger logger = Logger.getLogger(JFT.class.getName());

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
                System.out.println("ERROR IN " + z);
            }
        }

        // System.out.println(pathBindings);

    }

    public static String getShaMD(byte[] inputArray) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (Exception E) {
            System.out.println(Arrays.toString(E.getStackTrace()));
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

    /*
     * A: A B C B: A B D -> C
     *
     * A: A B D B: A B C -> D
     */

    // Index 0: Additions; Index 1: Deletions;
    public static ArrayList<ArrayList<Path>> getDifferences(ArrayList<Path> ListA, ArrayList<Path> ListB) {

        ArrayList<Path> deletions = new ArrayList<>(ListA);
        ArrayList<Path> additions = new ArrayList<>(ListB);

        deletions.removeAll(ListB);
        additions.removeAll(ListA);

        System.out.println("Deletions: " + deletions);
        System.out.println("Additions: " + additions);

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
//        ArrayList<Path> arr = new ArrayList<Path>();

        HashMap<Path, String> serializedData = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                // arr.add(Path.of(line));
                String pathGet = line.substring(0, line.indexOf(":"));
                String hashGet = line.substring(line.indexOf(":") + 1);
                serializedData.put(Path.of(pathGet), hashGet);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return serializedData;
    }

    public static void readJFTIgnore(String filename) {
        String line;
//        ArrayList<Path> arr = new ArrayList<Path>();

//        HashMap<Path, String> serializedData = new HashMap<Path, String>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                // arr.add(Path.of(line));
                JFTIgnore.add(Path.of(line));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    static String jftDir = System.getProperty("user.dir") + "/.jft/";

    public static void initializeRepository() {

        getShaMDFileArray(allRelativeFilePathsInDirectory());
        try {
            // readArray(System.getProperty("user.dir")+"/.jft/prevcom.file");
            Files.createDirectories(Path.of(jftDir));
            // System.out.println(pathBindings);
            writeArray(jftDir + "prevcom.file", pathsInDirectoryList);
            Settings.createConfFile();
        } catch (IOException e) {
            System.out.println("An error occurred:");
            e.printStackTrace();
        }

    }

    public static String getInput(String prompt){
        String text = null;
        try {
            // Enter data using BufferReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Reading data using readLine
            System.out.println(prompt);
            String name = reader.readLine();

            // Printing the read line
            System.out.println(name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    public static void push() {

        Settings.readSettings();

        var z = readArray(jftDir + "/prevcom.file");

        readJFTIgnore(jftDir + File.separator + ".jftignore");

        getShaMDFileArray(allRelativeFilePathsInDirectory());

        var temp = new ArrayList<Path>(z.keySet());

        var changedExistingFiles = temp.retainAll(new ArrayList<Path>(pathBindings.keySet()));

        var t = getModifiedFiles(z, temp);

        var changed = getDifferences(new ArrayList<Path>(z.keySet()), new ArrayList<Path>(pathBindings.keySet()));

        // initializeRepository();
//        Settings settings = new Settings();
//        int port = Integer.parseInt(settings.getProp().getProperty("port", "5000"));

        int port = (!Settings.getProp("port").equals("UNSET")) ? Integer.parseInt(Settings.getProp("port")) : 5000;
        String ipaddr = (!Settings.getProp("port").equals("UNSET")) ? Settings.getProp("ipv4") : getInput("IP Address: \n");

        var cli = new Client(ipaddr, port, "HIBEAT THIS IS A PASSWD");

        System.out.println(t);

        cli.sendFiles(t, changed);

//        System.out.println("Because of data integrity issues, we recommend you check that your files arrived safely." +
//                "\nIf they haven't, there will be a problem, because the repository will be reinitialized after this message." +
//                "\nPlease manually scp, ftp, sftp, the changed files and report any error messages that you receive");
//        System.out.println("\nPress enter, to signal your acceptance");
//        System.in.readAllBytes();

//        initializeRepository();
        getShaMDFileArray(allRelativeFilePathsInDirectory());
        try {
            writeArray(jftDir + "prevcom.file", pathsInDirectoryList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // initializeRepository();
        push();
    }
}
