package net.java.sip.communicator.plugin.fax;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;
import java.nio.charset.Charset;

import javax.swing.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;

import net.java.sip.communicator.plugin.desktoputil.ErrorDialog;
import net.java.sip.communicator.plugin.desktoputil.GenericFileDialog;
import net.java.sip.communicator.plugin.desktoputil.SipCommFileChooser;

@SuppressWarnings("serial")
public class PluginDialog
    extends JFrame
{
    private SipCommFileChooser fc = null;
    
    SwingWorker<Void, Void> worker;
    
    private String number[];
    
    private JButton sendButton = new JButton(); 
    private JButton fileChooserButton = new JButton(); 

    
    private JPanel mainPanel = new JPanel();
    /* Text Fields */
    private JTextField toField = new JFormattedTextField();
    private JComboBox<String> fromField = new JComboBox<String>();
    /* Labels */
    private JLabel fromLabel = new JLabel();
    private JLabel fileLabel = new JLabel();
    private JLabel toLabel = new JLabel();
    private JTextField textField = new JTextField("", 20);
    

    public PluginDialog(String number[])
    {
        this.number = number;
        this.setIconImage( FAXPluginActivator.getResources().getImage("service.gui.SIP_COMMUNICATOR_LOGO_64x64").getImage());
        initialize(this.number);
        
        this.sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    sendFAX();
                }
                catch (UnsupportedEncodingException e1)
                {
                    System.out.println("\tHttp Post encoding error");
                    e1.printStackTrace();
                }
            }            
        });
        
        this.fileChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                
                File file = fc.getFileFromDialog(); 
                
                if ( file.exists() )
                { 
                    textField.setText(file.getAbsolutePath() );
                    sendButton.setEnabled(true);
                } 
                else
                { 
                    sendButton.setEnabled(false);
                    System.out.println("\tOpen command cancelled by user or file doesnt exist."); 
                }
            }            
        });
    }
    
    private void initialize(String number[])
    {
        this.setTitle("Send FAX");
        this.setAlwaysOnTop(false);
        
        this.fromLabel.setText("From: ");
        this.fromField.setModel(new DefaultComboBoxModel<String>(number) );
        this.toLabel.setText("To: ");
        
        this.sendButton.setText("Send");

        this.fileLabel.setText("Path:");
        
        this.fileChooserButton.setText("Browse...");
        this.textField.setEnabled(false);
        
        this.sendButton.setEnabled(false);
        
        this.getContentPane().add(mainPanel);


        this.setStyles();
        
        this.setResizable(false);
        this.pack();
        
        new GenericFileDialog();
        fc = GenericFileDialog.create( (Frame)SwingUtilities.getWindowAncestor(mainPanel), "Choose file"
                                                                        ,SipCommFileChooser.LOAD_FILE_OPERATION );
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
        c.gridwidth = 2;
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
        c.gridwidth = 2;
        c.gridy = 1;
        this.mainPanel.add(toField, c);
        //row 2
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridwidth = 1;
        c.gridy = 2;
        this.mainPanel.add(fileLabel, c);
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridwidth = 2;
        c.gridy = 2;
        this.mainPanel.add(textField, c);   
        //row 3
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridwidth = 1;
        c.gridy = 3;
        this.mainPanel.add(fileChooserButton, c);
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridwidth = 1;
        c.gridy = 3;
        this.mainPanel.add(sendButton, c);


        this.mainPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 5, 5, 10));
        
        this.fromField.setEditable(false);
        this.toLabel.setFont(toLabel.getFont().deriveFont(Font.BOLD));
        this.fromLabel.setFont(toLabel.getFont().deriveFont(Font.BOLD));
        this.fileLabel.setFont(fileLabel.getFont().deriveFont(Font.BOLD));    
    }
    
    private void sendFAX() throws UnsupportedEncodingException
    {
        File file = fc.getApprovedFile();
        
        if( !fc.getApprovedFile().exists() )
        {
            ErrorDialog errorDialog = new ErrorDialog( (Frame)SwingUtilities.getWindowAncestor(this), 
                "File error","Could not open file.\n"+
                        "Please verify that file exists and you have read rights.", ErrorDialog.ERROR);
            errorDialog.showDialog();
            sendButton.setEnabled(false);
            return;
        }
        
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://ssl7.net/%WEB_DOMAIN%/u/api");
        //HttpPost httppost = new HttpPost("https://ssl7.net/voipdito.com/u/api");

        // Get provisioning username and password
        String username = FAXPluginActivator.getProvisioningService().getProvisioningUsername();
        String password = FAXPluginActivator.getProvisioningService().getProvisioningPassword();
        
        if(username == null || password == null)
        {
            ErrorDialog errorDialog = new ErrorDialog( (Frame)SwingUtilities.getWindowAncestor(this), 
                            "No connection","Could not resolve your username and password.\n"+
                                    "Please verify that your internet connection is working.", ErrorDialog.WARNING);
            errorDialog.showDialog();      
            return;
        }
        
        // Request parameters and other properties.
        Charset chars = Charset.forName("UTF-8");
        MultipartEntity params = new MultipartEntity();
        
        params.addPart("api_email", new StringBody(username, chars));
        params.addPart("api_password", new StringBody(password, chars));
        params.addPart("o", new StringBody("fax", chars));
        params.addPart("a", new StringBody("send", chars));
        params.addPart("from_no", new StringBody( (String) fromField.getSelectedItem(), chars));
        params.addPart("to_no", new StringBody( toField.getText(), chars));
        params.addPart("fax_file", new FileBody(file));
        
        httppost.setEntity(params);


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
                  if(content.contains("To number field is required") )
                  {
                      error_msg="You need to ";
                  }
                  else if(content.contains("Enter Fax number in format:") )
                  {
                      error_msg="Enter Fax number in format:<br/>+&lt;Country Code&gt; &lt;Area Code&gt; &lt;Number&gt;";
                  }
                  else
                  {
                      error_msg= "\nUnknown error:\n" + content;
                  }
                  
                  ErrorDialog errorDialog = new ErrorDialog( (Frame)SwingUtilities.getWindowAncestor(this)
                      , "Error", error_msg
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
            sendButton.setEnabled(false);
        }
    }
}
