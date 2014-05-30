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
import javax.swing.Timer;

public class PopupDialog
extends JDialog
{
    private JPanel mainPanel = new JPanel();
    
    private JLabel pleaseWait = new JLabel();
    
    private Timer timer;
    private int loading_prog;
    
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
            
            this.pleaseWait.setText("\tPlease wait...");
            this.loading_prog = 3;
            timer = new Timer(875, new TimerUpdater() );
            timer.setRepeats(true);
            timer.start();
            
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
    
    private class TimerUpdater implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
           switch(loading_prog)
           {
           case 3:
               pleaseWait.setText("\tPlease wait");
               loading_prog = 0;
               break;
           case 2:
               pleaseWait.setText("\tPlease wait...");
               loading_prog = 3;
               break;
           case 1:
               pleaseWait.setText("\tPlease wait..");
               loading_prog = 2;
               break;
           case 0:
               pleaseWait.setText("\tPlease wait.");
               loading_prog = 1;
               break;
           }
            
        }
    }
}
