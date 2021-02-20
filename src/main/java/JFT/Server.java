package JFT;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import static JFT.JFT.logger;

interface ServerInterface {
    void getFiles(Socket sock);
}

class Server implements ServerInterface {
    ServerSocket serverSocket = null;
    AES encryptionEngine = null;

    Server(int port, String password) {
        boolean listeningSocket = true;
        encryptionEngine = new AES(password);
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not listen on port " + port);
            e.printStackTrace();
            System.exit(-1);
//            System.err.println("Could not listen on port: 2343");
        }

        while (listeningSocket) {
            try {
                Socket clientSocket = serverSocket.accept();
                getFiles(clientSocket);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Shutting down the Server");
                System.exit(0);
            }
//            MiniServer mini = new MiniServer(clientSocket);
//            mini.start();
        }
    }

    public static void main(String[] args) {
//        JFT.getInput("What is your password?");

//        JFT.logger.log();

        var z = new Server(5000, "HIBEAT THIS IS A PASSWD");
    }

    @Override
    public void getFiles(Socket sock) {
        DataInputStream input = null;
        try {
            input = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        while (true) {
            try {
//                System.out.println("Waiting:");
                String data = input.readUTF();
                data = new String(encryptionEngine.decrypt(data));

                if (data.length() > 6 && data.startsWith("$FILE$")) {

                    logger.log(Level.FINEST, data.substring(6));
                    File f = new File(data.substring(6));

                    FileWriter fw;
                    try {
                        fw = new FileWriter(f);
                    } catch (FileNotFoundException ex) {
                        logger.log(Level.WARNING, "File not found, creating");
                        f.getParentFile().mkdirs();
                        f.createNewFile();
                        fw = new FileWriter(f);
                    }

                    while (true) {
                        data = input.readUTF();
                        data = new String(encryptionEngine.decrypt(data));

                        if (data.equals("$END$")) {
                            logger.log(Level.FINEST, "Reached End of File");
                            break;
                        } else {
                            fw.write(data);
                            fw.write(String.format("%n"));
                        }
                    }
                    fw.flush();
                    fw.close();
                }

                if (data.length() == 5 && data.startsWith("$DEL$")) {
                    data = input.readUTF();
                    data = new String(encryptionEngine.decrypt(data));
                    data = data;

                    logger.log(Level.FINEST, "Received file to delete: " + data);

                    if (new File(data).delete()) {
                        logger.log(Level.FINEST, "Succesfully deleted file.");
                    } else {
                        logger.log(Level.SEVERE, "Failure deleting " + data);
                    }
                }

                if (data.equals("$ENDTRANSFER$")) {
                    logger.log(Level.INFO, "Finished Transfer");
                    break;
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Unknown error. Please report this incident to the developer: ");
                e.printStackTrace();
                break;
            }
        }
    }

    @Override
    protected void finalize() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.log(Level.INFO, "Server was unable to close the socket. Don't worry, this doesn't really mean anything.");
            e.printStackTrace();
        }
    }

}