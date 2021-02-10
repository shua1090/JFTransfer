package JFT;

import java.io.*;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.*;

class JVT {
    public static HashMap<Path, String> pathBindings = new HashMap<Path, String>();
    public static ArrayList<Path> pathsInDirectoryList;
    public static ArrayList<Path> JFTIgnore = new ArrayList<Path>(Collections.singletonList(Path.of(".jft")));

    static boolean isChild(File maybeChild, File possibleParent)
    {
        return maybeChild.getAbsolutePath().startsWith( possibleParent.getAbsolutePath());
    }

    public static void diff(){
        ArrayList<Path> pastArr = null;
        try {
            pastArr = readArray(System.getProperty("user.dir") + "/.jft/initialCommit/OrigLog");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<ArrayList<Path>> differences = null;
        if (pastArr != null)
            differences = getDifferences(pastArr, allRelativeFilePathsInDirectory());

        assert (differences != null);

        if (differences.get(1).size() == 0){System.out.println("Unable to find any uncommitted added files.");}
        else {
            System.out.println("Uncommitted added files: ");
            for (var z : differences.get(0))
                System.out.println("+" + z.toString() + "+");
        }
        if (differences.get(1).size() == 0){System.out.println("Unable to find any uncommitted added files.");}
        else {
            System.out.println("Uncommitted deleted files: ");
            for (var z : differences.get(1))
                System.out.println("-" + z.toString() + "-");
        }

    }

    public static ArrayList<Path> allRelativeFilePathsInDirectory() {

        ArrayList<Path> arr = new ArrayList<>();
        Path path = Path.of(System.getProperty("user.dir"));
        try (Stream<Path> pathStream = Files.walk(path)
                .filter(Files::isRegularFile)
        ) {

            for (Path file : (Iterable<Path>) pathStream::iterator) {
                // something that throws IOException

                for (var z : JFTIgnore){
                    if(isChild(file.toFile(), z.toFile())){
                        break;
                    } else {
                        arr.add(path.relativize(file));
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        pathsInDirectoryList = arr;

        return arr;
    }

    public static void getShaMDFileArray(ArrayList<Path> fileList){

        for (var z : fileList){
            try {
                byte[] data = Files.readAllBytes(z);
                String hash = getShaMD(data);
                pathBindings.put(z, hash);
            } catch (Exception e){
                System.out.println("ERROR IN "+z);
            }
        }

        System.out.println(pathBindings);

    }

    public static String getShaMD(byte[] inputArray){
        MessageDigest md = null;
        try{
            md = MessageDigest.getInstance("SHA-256");
        } catch (Exception E){
            System.out.println(Arrays.toString(E.getStackTrace()));
        }

        assert md != null;

        var hash = md.digest(inputArray);

        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    /* A: A B C
     B: A B D -> C

     A: A B D
     B: A B C -> D
     */

    // Index 0: Additions; Index 1: Deletions;
    public static <T> ArrayList<ArrayList<T>> getDifferences(ArrayList<T> ListA, ArrayList<T> ListB){

        ArrayList<T> deletions = new ArrayList<T>(ListA);
        ArrayList<T> additions = new ArrayList<T>(ListB);

        deletions.removeAll(ListB);
        additions.removeAll(ListA);

        var arr = new ArrayList<ArrayList<T>>(2);
        arr.add(additions);
        arr.add(deletions);

        return arr;
    }

    public static void writeArray(String filename, ArrayList<Path> arr) throws IOException {
        FileWriter writer = new FileWriter(filename);
        int len = arr.size();
        for (Path path : arr) {
            writer.write(path + "\n");
        }
        writer.close();
    }

    public static ArrayList<Path> readArray(String filename) throws IOException {
        String line;
        ArrayList<Path> arr = new ArrayList<Path>();
        try {
            BufferedReader bufferreader = new BufferedReader(new FileReader(filename));
            while ((line = bufferreader.readLine()) != null) {
                arr.add(Path.of(line));
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return arr;
    }

    public static void getChanges(ArrayList<Path> arr){
        var z = allRelativeFilePathsInDirectory();
        System.out.println(
                getDifferences(arr, z)
        );
    }

    boolean ignoringPolicy(File maybeChild, File possibleParent)
    {
        return maybeChild.getAbsolutePath().startsWith( possibleParent.getAbsolutePath());
    }

    public static void initializeRepository(){
        String jftDir = System.getProperty("user.dir") + "/.jft/";
        Path path = Paths.get(
                System.getProperty("user.dir") + "/.jft/initialCommit/"
        );

        System.out.println(allRelativeFilePathsInDirectory());

        try {
            Files.createDirectories(path);
            writeArray(jftDir+"initialCommit/OrigLog", pathsInDirectoryList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
//        allPathsInDirectory();
//        System.out.println(getShaMD("FEST".getBytes(StandardCharsets.UTF_8)));
//        ArrayList<String> OriginList = new ArrayList<String>(
//                List.of("A", "B", "C")
//        );
//
//        ArrayList<String> SecondList = new ArrayList<String>(
//                List.of("A", "B", "D")
//        );
//
//        var z = getDifferences(OriginList, SecondList);
//        System.out.println(z.get(0));
//        System.out.println(z.get(1));
//
//
//        getShaMDFileArray(allPathsInDirectory());

//        initializeRepository();

//        try {
//            getChanges(
//                    readArray(".jft/initialCommit/OrigLog")
//            );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        initializeRepository();

    }
}

public class JFT {

    JFT(){

    }

    public static void main(String[] args){
        for (var val : args){
            System.out.println(val);
        }

        if (args.length == 0){
            return;
        }

        switch (args[0]){
            case "init": {
                JVT.initializeRepository();
                break;
            }
            case "commit": {
                break;
            }
            case "diff":{
                JVT.diff();
                break;
            }
            case "status":{
                JVT.diff();
            }
            default:
                break;
        }

    }

}
