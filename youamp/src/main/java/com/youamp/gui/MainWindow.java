package com.youamp.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow {

    public void create() {
        Font defaultFont = new Font("Helvetica", Font.PLAIN, 14);

        JFrame frame = new JFrame("YouAMP");
        JButton btClickMe = new JButton();

        btClickMe.setFont(defaultFont);
        btClickMe.setPreferredSize(new Dimension(100, 40));
        btClickMe.setText("Click me!");
        btClickMe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });

        frame.setPreferredSize(new Dimension(640, 480));
        frame.getContentPane().add(btClickMe, BorderLayout.LINE_START);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

}
