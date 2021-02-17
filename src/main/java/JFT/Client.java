package JFT;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;

interface ClientInterface{
    void sendBytes(byte[] data);
}

public class Client implements ClientInterface {

    Socket socket = null;
    DataOutputStream out = null;
    AES encryptionEngine = null;

    public Client(String ipAddr, int port, String passwd) {

        encryptionEngine = new AES(passwd);

        try {
            socket = new Socket(ipAddr, port);
        } catch (Exception ex) {
            System.out.println("Issue when connecting: ");
            ex.printStackTrace();
        }

        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendBytes(byte[] data) {
        String best = new String(data);
        try {
            out.writeUTF(best);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        out.close();
    }

    public ArrayList<String> readArray(Path filename) {
        String line;
        ArrayList<String> arr = new ArrayList<String>();

        try {
            this.sendBytes(this.encryptionEngine.encrypt("$FILE$"+String.valueOf(filename)).getBytes(StandardCharsets.UTF_8));

            System.out.println("Sent Header");

            BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(filename)));
            while ((line = reader.readLine()) != null) {
//                arr.add(line);
                this.sendBytes(this.encryptionEngine.encrypt(line).getBytes(StandardCharsets.UTF_8));
            }

            this.sendBytes(this.encryptionEngine.encrypt("$END$").getBytes(StandardCharsets.UTF_8));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return arr;
    }

    public void sendFiles(ArrayList<Path> modifiedFileset, ArrayList<ArrayList<Path>> newChangesFileset){
        for (var z : modifiedFileset){
            System.out.println();
            readArray(z);
        }

        for (var z : newChangesFileset.get(0)){
            System.out.println();
            readArray(z);
        }

        for (var z : newChangesFileset.get(1)){
            System.out.println();
            this.sendBytes(this.encryptionEngine.encrypt("$DEL$").getBytes(StandardCharsets.UTF_8));
            System.out.println(String.valueOf(z));
            this.sendBytes(this.encryptionEngine.encrypt(String.valueOf(z)).getBytes(StandardCharsets.UTF_8));

        }

/*        for (var z : newChangesFileset.get(0)){
            System.out.println("on"+z);
            readArray(z);
        }*/


        this.sendBytes(this.encryptionEngine.encrypt("$ENDTRANSFER$").getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args){
        var z = new Client("192.168.86.110", 5000, "HIBEAT THIS IS A PASSWD");

//        var z = readArray("Trash-Recognizer.tflite");
//        System.out.println(z);

       /*while (true){
            try {
                z.sendBytes(z.encryptionEngine.encrypt("STARTFILEPATH".getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
                var f = z.readArray(Path.of("test.txt"));
                for (var l : f){
                    var ho = z.encryptionEngine.encrypt(l.getBytes(StandardCharsets.UTF_8));
                    System.out.println(ho);
                    z.sendBytes(ho.getBytes(StandardCharsets.UTF_8));

                }
                z.sendBytes(z.encryptionEngine.encrypt("ENDFILE".getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
                break;
            } catch (Exception e){
                e.printStackTrace();
                break;
            }
        }*/
    }
}