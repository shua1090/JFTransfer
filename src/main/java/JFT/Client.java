package JFT;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;

import static JFT.JFT.logger;

interface ClientInterface {
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
//            System.out.println("Issue when connecting: ");
            logger.log(Level.SEVERE, "Was unable to connect to Server. Please make sure the server is listening, and the input ip address is correct.");
            ex.printStackTrace();
        }

        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        var z = new Client("192.168.86.110", 5000, "HIBEAT THIS IS A PASSWD");

//        var z = sendFile("Trash-Recognizer.tflite");
//        System.out.println(z);

       /*while (true){
            try {
                z.sendBytes(z.encryptionEngine.encrypt("STARTFILEPATH".getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
                var f = z.sendFile(Path.of("test.txt"));
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

    @Override
    public void sendBytes(byte[] data) {
        String best = new String(data);
        try {
            out.writeUTF(best);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to send data " + new String(data));
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        out.close();
    }

    public void sendFile(Path filename) {
        String line;

        try {
            this.sendBytes(this.encryptionEngine.encrypt("$FILE$" + filename).getBytes(StandardCharsets.UTF_8));

            BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(filename)));
            while ((line = reader.readLine()) != null) {
                this.sendBytes(this.encryptionEngine.encrypt(line).getBytes(StandardCharsets.UTF_8));
            }

            this.sendBytes(this.encryptionEngine.encrypt("$END$").getBytes(StandardCharsets.UTF_8));

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error sending File " + filename.toString());
            ex.printStackTrace();
        }

    }

    public void sendFiles(ArrayList<Path> modifiedFileset, ArrayList<ArrayList<Path>> newChangesFileset) {
        for (var z : modifiedFileset) {
            sendFile(z);
            logger.log(Level.INFO, "Sending modified file " + z.toString() + " to Server");
        }

        for (var z : newChangesFileset.get(0)) {
            sendFile(z);
            logger.log(Level.INFO, "Sending New file " + z.toString() + " to Server");
        }

        for (var z : newChangesFileset.get(1)) {
            this.sendBytes(this.encryptionEngine.encrypt("$DEL$").getBytes(StandardCharsets.UTF_8));
            logger.log(Level.INFO, "Deleint file " + z.toString() + " at the Server");
            this.sendBytes(this.encryptionEngine.encrypt(String.valueOf(z)).getBytes(StandardCharsets.UTF_8));

        }

        this.sendBytes(this.encryptionEngine.encrypt("$ENDTRANSFER$").getBytes(StandardCharsets.UTF_8));
        logger.log(Level.FINEST, "Completed Transfer");
    }
}