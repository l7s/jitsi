package net.java.sip.communicator.plugin.sms;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class PluginDialog
    extends JDialog
{
    SwingWorker<Void, Void> worker;
    
    private JButton sendButton = new JButton(); 
    
    private JPanel mainPanel = new JPanel();
    /* Text Fields */
    private JTextField toField = new JFormattedTextField();
    private JTextField fromField = new JTextField();
    private JTextArea textField = new JTextArea();
    /* Labels */
    private JLabel fromLabel = new JLabel();
    private JLabel toLabel = new JLabel();
    private JLabel charactersLabel = new JLabel();

    public PluginDialog(String number)
    {
        initialize(number);
        
        this.sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                sendSMS();
            }            
        });
    }
    
    private void initialize(String number)
    {
        this.setTitle("Send SMS");
        this.setAlwaysOnTop(true);
        
        this.fromLabel.setText("From: ");
        this.fromField.setText(number);
        this.toLabel.setText("To: ");
        
        this.sendButton.setText("Send");

        this.charactersLabel.setText("0/160 characters.");
        
        this.mainPanel.add(fromLabel);
        this.mainPanel.add(fromField);
        
        this.mainPanel.add(toLabel);
        this.mainPanel.add(toField);
    
        this.mainPanel.add(textField);
        
        this.mainPanel.add(charactersLabel);
        this.mainPanel.add(sendButton);

        this.getContentPane().add(mainPanel);

        this.setStyles();
        this.setCharacterCount();
        
        this.setResizable(false);
        this.pack();       
    }

    private void setStyles()
    {
        SpringLayout layout = new SpringLayout();
        /* From Label */
        layout.putConstraint(SpringLayout.NORTH, fromLabel, 0, SpringLayout.NORTH, fromField);
        layout.putConstraint(SpringLayout.WEST, fromLabel, 5, SpringLayout.WEST, mainPanel);
        /* From Field */
        layout.putConstraint(SpringLayout.NORTH, fromField, 5, SpringLayout.NORTH, mainPanel);
        layout.putConstraint(SpringLayout.WEST, fromField, 5, SpringLayout.EAST, fromLabel);
        layout.putConstraint(SpringLayout.EAST, fromField, 5, SpringLayout.EAST, mainPanel);
        /* To Label */
        layout.putConstraint(SpringLayout.NORTH, toLabel, 0, SpringLayout.NORTH, toField);
        layout.putConstraint(SpringLayout.WEST, toLabel, 5, SpringLayout.WEST, mainPanel);
        /* To Field */
        layout.putConstraint(SpringLayout.NORTH, toField, 5, SpringLayout.SOUTH, fromField);
        layout.putConstraint(SpringLayout.WEST, toField, 0, SpringLayout.WEST, fromField);
        layout.putConstraint(SpringLayout.EAST, toField, 5, SpringLayout.EAST, mainPanel);
        /* Text Field */
        layout.putConstraint(SpringLayout.NORTH, textField, 5, SpringLayout.SOUTH, toField);
        layout.putConstraint(SpringLayout.SOUTH, textField, -5, SpringLayout.NORTH, sendButton);
        layout.putConstraint(SpringLayout.WEST, textField, 5, SpringLayout.WEST, mainPanel);
        layout.putConstraint(SpringLayout.EAST, textField, 5, SpringLayout.EAST, mainPanel);
        /* Send Button */
        layout.putConstraint(SpringLayout.EAST, sendButton, 0, SpringLayout.EAST, textField);
        layout.putConstraint(SpringLayout.SOUTH, sendButton, 5, SpringLayout.SOUTH, mainPanel);
        /* Character label */
        layout.putConstraint(SpringLayout.WEST, charactersLabel, 5, SpringLayout.WEST, textField);
        layout.putConstraint(SpringLayout.NORTH, charactersLabel, 5, SpringLayout.SOUTH, textField);
        
        this.mainPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        this.fromField.setEditable(false);
        this.toLabel.setFont(toLabel.getFont().deriveFont(Font.BOLD));
        this.fromLabel.setFont(toLabel.getFont().deriveFont(Font.BOLD));
        
        this.textField.setColumns(45);
        this.textField.setLineWrap(true);
        this.textField.setRows(5);
        this.textField.setWrapStyleWord(true);
        this.textField.setBorder( toField.getBorder() );     
        
        this.mainPanel.setLayout(layout);
        this.mainPanel.setPreferredSize(new Dimension(225, 210) );
        layout.minimumLayoutSize(mainPanel);
    }
    
    private void setCharacterCount()
    {
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e)
            {
                int lenght = textField.getText().length();
                charactersLabel.setText( lenght + "/160 characters.");
                if(lenght>160)
                {
                    sendButton.setEnabled(false);
                }
                else
                    sendButton.setEnabled(true);
            }
            /* Unused classes */
            @Override
            public void keyPressed(KeyEvent e)
            {   
            }
            @Override
            public void keyTyped(KeyEvent e)
            {
            }
        });
    }
    
    private void sendSMS()
    {
        System.out.println("\tS*it ain't workin yet ;)");
    }
}
