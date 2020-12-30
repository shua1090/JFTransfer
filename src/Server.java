import javax.swing.*;
import java.awt.*;
class GUI{
    JLabel selectedFile;
    
    GUI(){

    }

    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setLayout(null);
        
        JPanel panel = new JPanel();
        var gbl = new GridBagLayout();
        panel.setLayout(gbl);
        JFileChooser fc = new JFileChooser("Pick a file");
        panel.add(fc);
        frame.add (panel);
        int returnVal = fc.showDialog(panel, "Attach");

        System.out.println(returnVal);
        System.out.println(fc.getSelectedFile());

        frame.setVisible(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

    }
}



// public class Server{
// }