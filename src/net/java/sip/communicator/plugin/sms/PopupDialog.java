package net.java.sip.communicator.plugin.sms;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

import net.java.sip.communicator.plugin.desktoputil.TransparentPanel;

public class PopupDialog
extends JDialog
{
    private JTextArea nonumberPanel = new JTextArea();
    
    private TransparentPanel buttonsPanel
    = new TransparentPanel(new FlowLayout(FlowLayout.CENTER));
    
    private JButton registerButton = new JButton();
    
    private JPanel mainPanel = new JPanel();
    
    private JLabel pleaseWait = new JLabel();
    
    private Timer timer;
    private int loading_prog;
    
    public PopupDialog()
    {
        initialize();
    }
    
    private void initialize()
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

    private void setStyles()
    {
        BoxLayout layout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        
        this.mainPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10));  
        
        this.mainPanel.setLayout(layout);
        layout.minimumLayoutSize(mainPanel);
    }
    
    public void setPopup(int i)
    {
        this.mainPanel.removeAll();
        timer.stop();
        
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
        else if(i==2)
        {
            this.setTitle(null);
            this.nonumberPanel.setText("Before sending SMS message you need to register your own mobile number.\n"
                        +"Please click button below to login into your Customer Portal to complete registration.");
            this.nonumberPanel.setEditable(false);
            this.nonumberPanel.setColumns(25);
            this.nonumberPanel.setOpaque(false);
            
            this.registerButton.setText("Register");
            this.registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    SMSPluginActivator.getBrowserService().openURL("http://%DOMAIN%/en/login");
                    PopupDialog.this.dispose();
                }            
            });
            
            this.buttonsPanel.add(registerButton);
            
            this.mainPanel.add(nonumberPanel);
            this.mainPanel.add(buttonsPanel);
            this.setStyles();
            
            this.setResizable(false);
            this.pack();
        }
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
