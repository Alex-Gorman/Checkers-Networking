package GameMVC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;

public class MainMenu extends JPanel {

    public MainMenu(JFrame frame, JPanel HostPanel, JPanel AwayPanel) {

//        this.setBackground(Color.BLUE);

        /* Button to enter screen where user can host a game */
        JButton hostGameButton = new JButton("Host A Game");

        /* Button to enter screen where user can join a game */
        JButton AwayGameButton = new JButton("Enter A Game");

        /* Label to display user IP address */
        JLabel myHostAddr;


        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        hostGameButton.setPreferredSize(new Dimension(350,150));
        hostGameButton.setFont(new Font("",Font.PLAIN,20));
        AwayGameButton.setPreferredSize(new Dimension(350,150));
        AwayGameButton.setFont(new Font("",Font.PLAIN,20));

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10,10,10,10);
        this.add(hostGameButton,gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(AwayGameButton,gbc);

        setBackground(new Color(159,235,237)); // Set the background color to red

        hostGameButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(HostPanel);
            frame.revalidate();
            frame.repaint();

        });
        AwayGameButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(AwayPanel);
            frame.revalidate();
            frame.repaint();


        });
        try {
            myHostAddr = new JLabel("My IP Address: \n"+String.valueOf(InetAddress.getLocalHost()).split("/")[1], SwingConstants.CENTER);

            /* make the label opaque so that its background colour is visible */
            myHostAddr.setOpaque(true);

            /* set the background colour of the label to blue */
            myHostAddr.setBackground(new Color(51, 255, 249 ));

            /* Set a black border with a thickness of 2 pixels */
            myHostAddr.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth=2;
            myHostAddr.setFont(new Font("",Font.BOLD,20));
            this.add(myHostAddr,gbc);
        } catch (Exception ignored){
        }
    }
}
