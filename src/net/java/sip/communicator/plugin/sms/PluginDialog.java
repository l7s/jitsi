package net.java.sip.communicator.plugin.sms;

import java.awt.*;

import javax.swing.*;

public class PluginDialog
    extends JDialog
{
    private JButton sendButton = new JButton(); 
    
    private JPanel mainPanel = new JPanel();

    private JTextField toField = new JFormattedTextField();
    private JTextField fromField = new JTextField();
    private JTextArea textField = new JTextArea();
    
    private JLabel fromLabel = new JLabel();
    private JLabel toLabel = new JLabel();

    public PluginDialog()
    {
        this.setTitle("Send SMS");
        
        this.fromLabel.setText("From: ");
        this.fromField.setText("+44 123 456 789");

        this.toLabel.setText("To: ");

        this.mainPanel.add(fromLabel);
        this.mainPanel.add(fromField);
        
        this.mainPanel.add(toLabel);
        this.mainPanel.add(toField);
    
        this.mainPanel.add(textField);


        this.getContentPane().add(mainPanel);

        this.setStyles();

        this.setResizable(true);
        this.pack();
    }

    private void setStyles()
    {
        SpringLayout layout = new SpringLayout();
        
        layout.putConstraint(SpringLayout.NORTH, fromLabel, 0, SpringLayout.NORTH, fromField);
        layout.putConstraint(SpringLayout.WEST, fromLabel, 5, SpringLayout.WEST, mainPanel);
        
        layout.putConstraint(SpringLayout.NORTH, fromField, 5, SpringLayout.NORTH, mainPanel);
        layout.putConstraint(SpringLayout.WEST, fromField, 5, SpringLayout.EAST, fromLabel);
        layout.putConstraint(SpringLayout.EAST, fromField, 5, SpringLayout.EAST, mainPanel);
        
        layout.putConstraint(SpringLayout.NORTH, toLabel, 0, SpringLayout.NORTH, toField);
        layout.putConstraint(SpringLayout.WEST, toLabel, 5, SpringLayout.WEST, mainPanel);
        
        layout.putConstraint(SpringLayout.NORTH, toField, 5, SpringLayout.SOUTH, fromField);
        layout.putConstraint(SpringLayout.WEST, toField, 0, SpringLayout.WEST, fromField);
        layout.putConstraint(SpringLayout.EAST, toField, 5, SpringLayout.EAST, mainPanel);
        
        layout.putConstraint(SpringLayout.NORTH, textField, 5, SpringLayout.SOUTH, toField);
        layout.putConstraint(SpringLayout.SOUTH, textField, 5, SpringLayout.SOUTH, mainPanel);
        layout.putConstraint(SpringLayout.WEST, textField, 5, SpringLayout.WEST, mainPanel);
        layout.putConstraint(SpringLayout.EAST, textField, 5, SpringLayout.EAST, mainPanel);
        
        this.mainPanel.setLayout(layout);

        this.mainPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        this.fromField.setEditable(false);
        this.toLabel.setFont(toLabel.getFont().deriveFont(Font.BOLD));
        this.fromLabel.setFont(toLabel.getFont().deriveFont(Font.BOLD));
        
        this.textField.setColumns(20);
        this.textField.setLineWrap(true);
        this.textField.setRows(5);
        this.textField.setWrapStyleWord(true);
        this.textField.setBorder( toField.getBorder() );
        
        /*this.infoTextArea.setOpaque(false);
        this.infoTextArea.setWrapStyleWord(true);
        this.infoTextArea.setLineWrap(true);
        this.infoTextArea.setFont(infoTextArea.getFont().deriveFont(Font.BOLD));
        this.infoTextArea.setAlignmentX(JTextArea.CENTER_ALIGNMENT);*/
        

        /*this.contactLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        this.contactLabel.setAlignmentY(JLabel.TOP_ALIGNMENT);
        this.contactLabel.setFont(contactLabel.getFont().deriveFont(Font.BOLD)); */       
    }
}
