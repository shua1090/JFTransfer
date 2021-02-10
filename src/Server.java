import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.io.DataOutputStream;
import java.io.File;
import java.io.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
class Ser_GUI{

    JLabel selectedFile;                                       // 
    public static JPanel mainPanel;                            // Main Entry point
    GridBagLayout gbl = new GridBagLayout();                   // For use with the Layout Manager
    GridBagConstraints gbc = new GridBagConstraints();         // For use with Constraining the Layout
    boolean connectionEstablished = false;                     // Only true when a two-way connection is established
    
    ServerSocket serversocket;
    Socket socket;

    public void send_file(File f) throws IOException{
        FileInputStream k = new FileInputStream(f);
        // serversocket = new ServerSocket(5000);
        // socket = serversocket.accept();
        // ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        // out.writeObject( f ); // Writing File Object to Client
        // socket.close();
        // f.renameTo(new File("C:\\Users\\Shynn\\Downloads\\Photos.zip"));
        System.out.println("Finished");
        // File.createTempFile(prefix, suffix);
        byte[] z = k.readAllBytes();
        FileOutputStream stream = new FileOutputStream("C:\\Users\\Shynn\\Downloads\\Photos.zip");
        stream.write(z);
        stream.close();
        k.close();
    }



    public void run(){ // For Receiving files

    }

    Ser_GUI(){
        mainPanel = new JPanel();
        mainPanel.setLayout(gbl);
        setup();
    }

    private void setup(){
        gbc.gridx = 5;
        gbc.gridy = 5;
        JButton button = new JButton("Load File");
        mainPanel.add(button, gbc);

        class file_action implements ActionListener{
            public void actionPerformed(ActionEvent e){
                if (e.getActionCommand().equals("Load File")){

                    var fc = new JFileChooser("Pick a File");
                    fc.setDialogTitle("Choose the file to send to the client device");
                    fc.setApproveButtonText("Load");
                    int returnVal = fc.showOpenDialog(mainPanel);

                    if (returnVal == JFileChooser.APPROVE_OPTION){
                        System.out.println("Approved");
                        var file = fc.getSelectedFile();
                        try{
                            send_file(file);
                        } catch (Exception l){
                            System.out.print(l);
                        }

                    } else {
                        System.out.println("Transaction has not been approved");
                    }

                }
            }
        }

        button.addActionListener(new file_action());
        JFrame frame = new JFrame();
        frame.add(Ser_GUI.mainPanel);
        this.mainPanel.setVisible(true);
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args){
        var z = new Ser_GUI();

        // JPanel panel = new JPanel();
        // var gbl = new GridBagLayout();
        // panel.setLayout(gbl);
        // JFileChooser fc = new JFileChooser("Pick a file");
        // panel.add(fc);
        // frame.add (panel);
        // int returnVal = fc.showDialog(panel, "Attach");

        // System.out.println(returnVal);
        // System.out.println(fc.getSelectedFile());

    }
}



// public class Server{
// }