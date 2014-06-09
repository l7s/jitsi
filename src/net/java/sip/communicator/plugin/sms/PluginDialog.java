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
        GridBagLayout layout = new GridBagLayout();
        this.mainPanel.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 5, 5, 0);
        //row 0
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridwidth = 1;
        c.gridy = 0;
        this.mainPanel.add(fromLabel, c);
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridwidth = 3;
        c.gridy = 0;
        this.mainPanel.add(fromField, c);
        //row 1
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridwidth = 1;
        c.gridy = 1;
        this.mainPanel.add(toLabel, c);   
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridwidth = 3;
        c.gridy = 1;
        this.mainPanel.add(toField, c);
        //row 2
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridwidth = 4;
        c.gridy = 2;
        this.mainPanel.add(textField, c);   
        //row 3
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 3;
        this.mainPanel.add(charactersLabel, c);
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 3;
        c.gridwidth = 1;
        c.gridy = 3;
        this.mainPanel.add(sendButton, c);
        /**/
        this.mainPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 5, 5, 10));
        
        this.fromField.setEditable(false);
        this.toLabel.setFont(toLabel.getFont().deriveFont(Font.BOLD));
        this.fromLabel.setFont(toLabel.getFont().deriveFont(Font.BOLD));
        
        this.textField.setColumns(18);
        this.textField.setLineWrap(true);
        this.textField.setRows(8);
        this.textField.setWrapStyleWord(true);
        this.textField.setDocument(doc);
        this.textField.setBorder( toField.getBorder() );     
        
        this.mainPanel.setLayout(layout);
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
        HttpPost httppost = new HttpPost("https://ssl7.net/%DOMAIN%/u/api");

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
