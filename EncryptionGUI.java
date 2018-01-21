package encryption;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.swing.event.*;
import java.io.*;
//MOST RECENT FILE as of January 20th, 2017
public class EncryptionGUI extends JPanel implements ActionListener{

  JButton ENCRYPT = new JButton("Encrypt");
  JButton DECRYPT = new JButton("Decrypt");

  JButton CHOOSE_FILE;
  JFileChooser FILE_FINDER;
  private JTextField FILE_NAME = new JTextField();
  private JTextField dir = new JTextField();
  private JTextField fil = new JTextField();
  private int gridLayoutRows = 5;
  private int gridLayoutCol =2;
  private int X_DIMENSION = 500;
  private int Y_DIMENSION = 200;

  File SELECTED_FILE;

  JPanel BUTTON_PANEL = new JPanel();
  JPanel FILE_PANEL = new JPanel();
  JPanel PASS_PANEL = new JPanel();
  JPanel CONTROL_PANEL;
  String chooserFile;
  //private final static int DIMENSIONS = 300;
  JLabel INSTRUCTIONS;

  final int READ_BLOCK_SIZE  = 104;
  InputStream in = System.in;//default input stream
  OutputStream out = System.out; //default output stream
  Scanner scanner = new Scanner(in);
  private JPasswordField PASS_KEY;
  public EncryptionGUI() {

    FILE_NAME.setEditable(false);

    PASS_KEY = new JPasswordField(10);
    JLabel keyLabel = new JLabel("Enter the key: ");
    keyLabel.setLabelFor(PASS_KEY);
    INSTRUCTIONS = new JLabel("Select File, enter password, then select"
        + "whether to encrypt or decrypt");

    JLabel fileLabel = new JLabel("Selected File Name: ");
    fileLabel.setLabelFor(FILE_NAME);

    CHOOSE_FILE = new JButton("Choose File");

    CHOOSE_FILE.addActionListener(this);

    CONTROL_PANEL = new JPanel();
    CONTROL_PANEL.setLayout(new GridLayout(gridLayoutRows, 1));

    BUTTON_PANEL.setLayout(new GridLayout(1, gridLayoutCol));
    BUTTON_PANEL.add(ENCRYPT);
    BUTTON_PANEL.add(DECRYPT);
    ENCRYPT.addActionListener(this);
    DECRYPT.addActionListener(this);

    FILE_PANEL.setLayout(new GridLayout(1, gridLayoutCol));
    FILE_PANEL.add(fileLabel);
    FILE_PANEL.add(FILE_NAME);

    PASS_PANEL.setLayout(new GridLayout(1, gridLayoutCol));
    PASS_PANEL.add(keyLabel);
    PASS_PANEL.add(PASS_KEY);

    CONTROL_PANEL.add(INSTRUCTIONS);
    CONTROL_PANEL.add(CHOOSE_FILE);
    CONTROL_PANEL.add(FILE_PANEL);
    //CONTROL_PANEL.add(PASS_PANEL);
    CONTROL_PANEL.add(BUTTON_PANEL);


    this.add(CONTROL_PANEL);
    this.validate();

  }
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();

    if(cmd.equals("Choose File")){
      String userDir = System.getProperty("user.home");
      FILE_FINDER = new JFileChooser();
      FILE_FINDER.setCurrentDirectory(new File(userDir));
      //FILE_FINDER.setDialogTitle(choosertitle);

      if (FILE_FINDER.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        System.out.println("getCurrentDirectory(): "
           +  FILE_FINDER.getCurrentDirectory());
        System.out.println("getSelectedFile() : "
           +  FILE_FINDER.getSelectedFile());

        FILE_NAME.setText(FILE_FINDER.getSelectedFile().getName());

        SELECTED_FILE = new File(FILE_FINDER.getSelectedFile().getName());

        chooserFile = new String(FILE_FINDER.getSelectedFile().getName());
        fil.setText(FILE_FINDER.getSelectedFile().toString());
        dir.setText(FILE_FINDER.getCurrentDirectory().toString());

      }
      else {
        System.out.println("No Selection ");
      }
    }
    else {
      //System.out.println("WORK");
      chooserFile = new String(fil.getText());
      if(cmd.equals("Encrypt")){

        if (chooserFile.equals("")){
          //System.out.println("lol");
        }
        else {
          try {
            in =  new FileInputStream(chooserFile);
            out = new FileOutputStream(dir.getText()+"/Encrypted " + SELECTED_FILE);
            //Writer wr = new FileWriter(dir.getText()+"/Encoded.txt");

            int bytesRead = 0;
            byte[] block = new byte[READ_BLOCK_SIZE];
            int byteLength = 0;

            while((byteLength = in.read(block)) != -1) {
              TEACipher cipher = new TEACipher();
              //FIXME: Bug9a
              byte[] actual_block=Arrays.copyOf(block, byteLength);
              //FIXME: Bug6 
              //byte[] encrypted = cipher.encode(block, byteLength);                           
              //byte[] encrypted = cipher.alvin_encode(block);
              // Bug6+Bug9a
              byte[] encrypted = cipher.alvin_encode(actual_block);
              
              System.out.format("encrypted.length=%d\n", encrypted.length);
              out.write(encrypted);
              bytesRead += byteLength;
              //System.out.println("lol");
            }
            System.out.print("enciphered");

          } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
          }
        }

      }
      else{
        if (chooserFile.equals("")){
          //System.out.println("lol");
        }
        else {
          try {
            in =  new FileInputStream(chooserFile);
            out = new FileOutputStream(dir.getText()+"/Decoded "+SELECTED_FILE);

            int bytesRead = 0;
            byte[] block = new byte[READ_BLOCK_SIZE];
            int byteLength = 0;

            while((byteLength = in.read(block)) != -1) {
              TEACipher cipher = new TEACipher();
              //fz: byte[] decipherd = cipher.decode(block, byteLength);
              //byte[] decipherd1 = cipher.decode(block, byteLength);
              System.out.println("\n---------------Alvin----------------\n");
              //FIXME Bug9b
              byte[] actual_block=Arrays.copyOf(block, byteLength);
              byte[] decipherd = cipher.alvin_decode(actual_block);
              out.write(decipherd);
              bytesRead += byteLength;
              //System.out.println("lol");
            }

            System.out.print("deciphered");
            out.close();
          } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
          }
        }
      }
    }

  }

  public Dimension getPreferredSize(){
    return new Dimension(X_DIMENSION, Y_DIMENSION);
  }

  public static void main(String args[]) {
    JFrame frame = new JFrame("");
    EncryptionGUI panel = new EncryptionGUI();
    frame.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            System.exit(0);
            }
          }
        );
      frame.getContentPane().add(panel,"Center");
      frame.setSize(panel.getPreferredSize());
      frame.setVisible(true);

  }
}
