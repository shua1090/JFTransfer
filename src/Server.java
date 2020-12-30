import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.crypto.*;
class GUI{
    /*
    <summary>
    Test
    </summary>
    */
    JLabel selectedFile;
    public static JPanel mainPanel;
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    
    GUI(){
        mainPanel = new JPanel();
        mainPanel.setLayout(gbl);
        setup();
    }

    private void setup(){
        gbc.gridx = 5;
        gbc.gridy = 5;
        JButton button = new JButton("Load File");
        mainPanel.add(button, gbc);

        class act implements ActionListener{
            public void actionPerformed(ActionEvent e){
                if (e.getActionCommand().equals("Load File")){
                    var fc = new JFileChooser("Pick a File");
                    fc.setDialogTitle("Choose the file to send over");
                    fc.setApproveButtonText("Load");
                    int returnVal = fc.showOpenDialog(mainPanel);

                    if (returnVal == fc.APPROVE_OPTION){
                        System.out.println("Approved");
                        var file = fc.getSelectedFile();
                        System.out.println(file.getAbsolutePath());
                        System.out.println(file.getClass().toString());
                        System.out.println(file.getName());
                    } else {
                        System.out.println("Transaction has not been approved");
                    }

                }
            }
        }

        button.addActionListener(new act());
    }

    public static void main(String[] args){
        var z = new GUI();
        JFrame frame = new JFrame();

        frame.add(z.mainPanel);
        // JPanel panel = new JPanel();
        // var gbl = new GridBagLayout();
        // panel.setLayout(gbl);
        // JFileChooser fc = new JFileChooser("Pick a file");
        // panel.add(fc);
        // frame.add (panel);
        // int returnVal = fc.showDialog(panel, "Attach");

        // System.out.println(returnVal);
        // System.out.println(fc.getSelectedFile());
        z.mainPanel.setVisible(true);
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

    }
}



// public class Server{
// }