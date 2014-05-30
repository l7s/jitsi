package net.java.sip.communicator.plugin.sms;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class PopupDialog
extends JDialog
{
    private JPanel mainPanel = new JPanel();
    
    private JLabel pleaseWait = new JLabel();
    
    public PopupDialog(int i)
    {
        initialize(i);
    }
    
    private void initialize(int i)
    {
        if(i==1)
        {
            this.setTitle("Loading");
            this.setAlwaysOnTop(true);
            
            this.pleaseWait.setText("Please wait...");
            
            this.mainPanel.add(pleaseWait);
    
            this.getContentPane().add(mainPanel);
    
            this.setStyles();
            
            this.setResizable(false);
            this.pack();
        }
    }

    private void setStyles()
    {
        BoxLayout layout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        
        this.mainPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10));  
        
        this.mainPanel.setLayout(layout);
        //this.mainPanel.setPreferredSize(new Dimension(225, 210) );
        layout.minimumLayoutSize(mainPanel);
    }
}
