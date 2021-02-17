package JFT;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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
            System.err.println("Could not listen on port: 2343");
        }

        while (listeningSocket) {
            try {
                Socket clientSocket = serverSocket.accept();
                getFiles(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            MiniServer mini = new MiniServer(clientSocket);
//            mini.start();
        }
    }

    public static void main(String[] args) {
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

                if (data.length() > 6 && data.substring(0, 6).equals("$FILE$")) {
                    System.out.println("FILE Path: " + data.substring(6));

                    File f = new File("FileTestingDir" +File.separator +data.substring(6));

                    FileWriter fw;
                    try {
                            fw = new FileWriter(f);
                    } catch (FileNotFoundException ex){
                        System.out.println("File not found, creating");
                        f.getParentFile().mkdirs();
                        f.createNewFile();
                        fw = new FileWriter(f);
                    }

                    while (true) {
                        data = input.readUTF();
                        data = new String(encryptionEngine.decrypt(data));

                        if (data.equals("$END$")) {
                            System.out.println("END FILE");
                            break;
                        } else {
                            fw.write(data);
                            fw.write(String.format("%n"));
//                            System.out.println(data);
                        }
                    }
                    fw.flush();
                    fw.close();
                    System.out.println("broke");
//                    break;
                }

                if (data.length() == 5 && data.substring(0, 5).equals("$DEL$")) {
                    data = input.readUTF();
                    data = new String(encryptionEngine.decrypt(data));
                    data = new String("FileTestingDir" +File.separator + data);

                    if (new File(data).delete()){
                        System.out.println("Succesfully deleted file");
                    } else {
                        System.out.println("Failure deleting "+data);
                    };
                    System.out.println("broke");
                }

                if (data.equals("$ENDTRANSFER$")) break;

            } catch (Exception e) {
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
            e.printStackTrace();
        }
    }

}