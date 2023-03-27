import javax.swing.*;
import  java.net.*;
public class SwingFrame {

    public static void main(String[] args) throws UnknownHostException {
        /* Frame to hold the GUI in */
        JFrame frame = new JFrame("Main UI");

        /* Close the frame on close */
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* Set frame size */
        frame.setSize(1000,1000);

        /* Client Menu Panel */
        ClientMenu clientMenu = new ClientMenu(frame);

        /* Host Menu Panel */
        HostMenu hostMenu = new HostMenu(frame);

        /* Main Menu Panel */
        MainMenu mainMenu = new MainMenu(frame,hostMenu, clientMenu);

        /* Send Main Menu Panel to Client Menu object to allow user to return to Main Menu */
        clientMenu.addMainPanel(mainMenu);

        /* Send Main Menu Panel to Host Menu object to allow user to return to Main Menu */
        hostMenu.addMainPanel(mainMenu);

        /* Set the frame to the Main Menu to begin */
        frame.getContentPane().add(mainMenu);

        /* Make the frame appear on the screen */
        frame.setVisible(true);
    }
}