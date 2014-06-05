package net.java.sip.communicator.plugin.sms;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.text.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;
import org.apache.http.util.EntityUtils;

import net.java.sip.communicator.plugin.desktoputil.ErrorDialog;

@SuppressWarnings("serial")
public class PluginDialog
    extends JFrame
{
    
    SwingWorker<Void, Void> worker;
    
    private DefaultStyledDocument doc;
    
    private String number;
    
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
        this.number = number;
        this.setIconImage( SMSPluginActivator.getResources().getImage("service.gui.SIP_COMMUNICATOR_LOGO_64x64").getImage());
        initialize(this.number);
        
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
        this.setAlwaysOnTop(false);
        
        this.fromLabel.setText("From: ");
        this.fromField.setText(number);
        this.toLabel.setText("To: ");
        
        this.sendButton.setText("Send");
        this.sendButton.setEnabled(false);

        this.charactersLabel.setText("0/160 characters.");
        
        this.mainPanel.add(fromLabel);
        this.mainPanel.add(fromField);
        
        this.mainPanel.add(toLabel);
        this.mainPanel.add(toField);
    
        this.mainPanel.add(textField);
        
        this.mainPanel.add(charactersLabel);
        this.mainPanel.add(sendButton);

        this.getContentPane().add(mainPanel);

        this.setCharacterCount();
        this.setStyles();
        
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
        this.textField.setDocument(doc);
        this.textField.setBorder( toField.getBorder() );     
        
        this.mainPanel.setLayout(layout);
        this.mainPanel.setPreferredSize(new Dimension(225, 210) );
        layout.minimumLayoutSize(mainPanel);
    }
    
    private void setCharacterCount()
    {
        /*
         * Document filter used to limit max character count of textField to 160
         */
        doc = new DefaultStyledDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(160));
        
        /*
         * Update label when typing
         */
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e)
            {
                
                int lenght = textField.getText().length();
                charactersLabel.setText( lenght + "/160 characters.");
                if(lenght==0)
                    sendButton.setEnabled(false);
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
        int lenght = textField.getText().length();
        charactersLabel.setText( lenght + "/160 characters.");
        if(lenght>160)
        {
            return;
        }
        
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://ssl7.net/%WEB_DOMAIN%/u/api");

        // Get provisioning username and password
        String username = SMSPluginActivator.getProvisioningService().getProvisioningUsername();
        String password = SMSPluginActivator.getProvisioningService().getProvisioningPassword();
        
        if(username == null || password == null)
        {
            ErrorDialog errorDialog = new ErrorDialog( (Frame)SwingUtilities.getWindowAncestor(this), 
                            "No connection","Could not resolve your username and password.\n"+
                                    "Please verify that your internet connection is working.", ErrorDialog.WARNING);
            errorDialog.showDialog();      
            return;
        }
        
        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        
        params.add(new BasicNameValuePair("api_email", username));
        params.add(new BasicNameValuePair("api_password", password));
        params.add(new BasicNameValuePair("o", "sms"));
        params.add(new BasicNameValuePair("a", "send"));
        params.add(new BasicNameValuePair("text", textField.getText() ));
        params.add(new BasicNameValuePair("to_no", toField.getText() ));
        
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // writing error to Log
            e.printStackTrace();
        }

        //Execute and get the response.
        try {
            HttpResponse response = httpClient.execute(httppost);
            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                // Check if there was an error
                String content =  EntityUtils.toString(respEntity);
                System.out.println("\tSMS API response: " + content);
                
                if(content.split(",")[0].contains("false") )
                {
                  String error_msg = null;
                  if(content.contains("Failed to parse To number.") )
                  {
                      error_msg="Please input proper number";
                  }
                  else if(content.contains("Invalid format of To field.") )
                  {
                      error_msg="Invalid format of \"To\" field.";
                  }
                  else
                  {
                      error_msg= "\nUnknown error:\n" + content;
                  }
                  
                  ErrorDialog errorDialog = new ErrorDialog( (Frame)SwingUtilities.getWindowAncestor(this)
                      , "No connection","There was an error:\n" + error_msg
                      , ErrorDialog.WARNING);
                  errorDialog.showDialog();
                  return;
                }
            }
        } catch (ClientProtocolException e) {
            // writing exception to log
            e.printStackTrace();
        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();
            
            toField.setText(null);
            textField.setText(null);
        }
    }
    
    /*
     * 
     */
    private class DocumentSizeFilter extends DocumentFilter
    {
        int maxCharacters;

        public DocumentSizeFilter(int maxChars) {
            maxCharacters = maxChars;
        }

        public void insertString(FilterBypass fb, int offs,
                                 String str, AttributeSet a)
            throws BadLocationException
            {

            if ((fb.getDocument().getLength() + str.length()) <= maxCharacters)
                super.insertString(fb, offs, str, a);
            else
                Toolkit.getDefaultToolkit().beep();
            }
        
        public void replace(FilterBypass fb, int offs,
                            int length, 
                            String str, AttributeSet a)
            throws BadLocationException 
            {
            if ((fb.getDocument().getLength() + str.length()
                 - length) <= maxCharacters)
                super.replace(fb, offs, length, str, a);
            else
                Toolkit.getDefaultToolkit().beep();
            }
    }
}
