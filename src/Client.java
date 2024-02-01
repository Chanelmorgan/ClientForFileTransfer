import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {

    public static void main(String[] args) {
        // Have to have the variable as final because it is accessed within an inner class
        final File[] fileToSend = new File[1];

        // Creating the UI
        JFrame jFrame = new JFrame("Client");
        jFrame.setSize(450, 450);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creating the titles of the application
        JLabel jlTitle = new JLabel("File Sender");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jLFileName = new JLabel("Choose a file to send");
        jLFileName.setFont(new Font("Arial", Font.BOLD, 20));
        jLFileName.setBorder(new EmptyBorder(50, 0, 0,0 ));
        jLFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Creating the panel to allow us to put multiple things in it
        JPanel jpButton = new JPanel();
        jpButton.setBorder(new EmptyBorder(75, 0, 10,0));

        // Creating the buttons
        JButton sendFileButton = new JButton("Send File");
        sendFileButton.setPreferredSize(new Dimension(150, 75));
        sendFileButton.setFont(new Font("Arial", Font.BOLD, 20));

        JButton chooseFileButton = new JButton("Choose File");
        chooseFileButton.setPreferredSize(new Dimension(150, 75));
        chooseFileButton.setFont(new Font("Arial", Font.BOLD, 20));

        // Adding the buttons to the panel
        jpButton.add(sendFileButton);
        jpButton.add(chooseFileButton);

        // Adding the action listener to the buttons
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Choose a file to send");

                // Getting the users file
                if(jFileChooser.showOpenDialog(null) == jFileChooser.APPROVE_OPTION){
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    jLFileName.setText("The file you want to send is: " + fileToSend[0].getName());

                }

            }
        });

        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileToSend[0] == null) {
                    jLFileName.setText("Please choose a file first");
                } else {
                    try {
                        // Creating the input streams and output streams
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
                        // Creating the tcp socket
                        Socket socket = new Socket("localhost", 1234);

                        // Converting the content into bytes and storing in an arrray to send over
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        String fileName = fileToSend[0].getName();
                        byte[] fileNameBytes = fileName.getBytes();
                        byte[] contentBytes = new byte[(int) fileToSend[0].length()];
                        fileInputStream.read(contentBytes);

                        // Send an integer to the sever which is the length of the data that it will be receiving.
                        // The server then knows when it should stop expecting data from the client
                        dataOutputStream.writeInt(fileNameBytes.length);
                        dataOutputStream.write(fileNameBytes);

                        // Doing the same thing for the content now
                        dataOutputStream.writeInt(contentBytes.length);
                        dataOutputStream.write(contentBytes);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });

        // Adding the buttons to the frame
        jFrame.add(jlTitle);
        jFrame.add(jLFileName);
        jFrame.add(jpButton);
        jFrame.setVisible(true);

    }
}
