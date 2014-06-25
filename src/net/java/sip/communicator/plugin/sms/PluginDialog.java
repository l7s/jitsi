package net.java.sip.communicator.plugin.sms;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.text.*;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.java.sip.communicator.service.gui.PopupDialog;
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
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                ToolsMenuItem.pluginDialog = null;
                PluginDialog.this.dispose();
            }         
        });
        this.number = number;
        this.setIconImage( SMSPluginActivator.getResources().getImage("service.gui.SIP_COMMUNICATOR_LOGO_64x64").getImage());
        initialize(this.number);
        
        this.sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    sendSMS();
                }
                catch(NoSuchAlgorithmException ex)
                {
                    System.out.println(ex);
                }
                catch(KeyManagementException ex)
                {
                    System.out.println(ex);
                }
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

        this.charactersLabel.setText("000/160 characters.");
        
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
        this.charactersLabel.setText("0/160 characters.");
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
        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridwidth = 4;
        c.gridy = 2;
        this.mainPanel.add(textField, c);   
        //row 3
        c.weighty = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 3;
        this.mainPanel.add(charactersLabel, c);
        c.weightx = 0.5;
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
    
    private void sendSMS() throws NoSuchAlgorithmException, KeyManagementException
    {
        int lenght = textField.getText().length();
        charactersLabel.setText( lenght + "/160 characters.");
        if(lenght>160)
        {
            return;
        }
        
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost( SMSPluginActivator.getResources().getSettingsString(
                                                                                "net.java.sip.communicator.l7s.API_URL"));

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
        
        if(SMSPluginActivator.getResources().getSettingsString(
                                    "net.java.sip.communicator.service.gui.ALWAYS_TRUST_MODE_ENABLED").contentEquals("true"))
        {
            System.out.println("SSL cert checking disabled.");
            httpClient = WebClientDevWrapper.wrapClient(httpClient);
        }
        else
        {
            httpClient = new DefaultHttpClient();
        }
        
        //Execute and get the response.
        try {
            HttpResponse response = httpClient.execute(httppost);
            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                // Check if there was an error
                String content =  EntityUtils.toString(respEntity);
                System.out.println("\tSMS API response: " + content);
                
                JSONParser parser = new JSONParser();
                Object obj;
                try
                {
                  obj = parser.parse(content);
                }
                catch(ParseException pe)
                {
                  System.out.println("Response parsing error at position: " + pe.getPosition());
                  System.out.println(pe);
                  
                  ErrorDialog errorDialog = new ErrorDialog( (Frame)SwingUtilities.getWindowAncestor(this)
                      , "Error", "Unexpected server response."
                      , ErrorDialog.WARNING);
                  errorDialog.showDialog();
                  return;
                }
                
                JSONObject jsonObject = (JSONObject)obj;

                if( jsonObject.get("success").toString() != "true")
                {
                  jsonObject = (JSONObject)jsonObject.get("errors");
                  String error_msg= "";
                  for(Object key : jsonObject.keySet() )
                  {
                      error_msg+= jsonObject.get(key).toString()+"\n";
                  }
                  
                  ErrorDialog errorDialog = new ErrorDialog( (Frame)SwingUtilities.getWindowAncestor(this)
                      , "Error","There was an error:\n" + error_msg
                      , ErrorDialog.WARNING);
                  errorDialog.showDialog();
                  return;
                }
                else
                {
                    SMSPluginActivator.getUIService().getPopupDialog().showMessagePopupDialog("Message sent successfully.",
                        "SMS", PopupDialog.INFORMATION_MESSAGE);
                    ToolsMenuItem.pluginDialog = null;
                    this.dispose();
                    return;
                }
            }
        } catch (ClientProtocolException e) {
            // writing exception to log
            e.printStackTrace();
        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();
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
    
    public static class WebClientDevWrapper {
        
        public static HttpClient wrapClient(HttpClient base) {
            try {
                SSLContext ctx = SSLContext.getInstance("TLS");
                X509TrustManager tm = new X509TrustManager() {
     
                    public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                    }
     
                    public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                    }
     
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };
                X509HostnameVerifier verifier = new X509HostnameVerifier() {
     
                    @Override
                    public void verify(String string, SSLSocket ssls) throws IOException {
                    }
     
                    @Override
                    public void verify(String string, X509Certificate xc) throws SSLException {
                    }
     
                    @Override
                    public void verify(String string, String[] strings, String[] strings1) throws SSLException {
                    }
     
                    @Override
                    public boolean verify(String string, SSLSession ssls) {
                        return true;
                    }
                };
                ctx.init(null, new TrustManager[]{tm}, null);
                SSLSocketFactory ssf = new SSLSocketFactory(ctx);
                ssf.setHostnameVerifier(verifier);
                ClientConnectionManager ccm = base.getConnectionManager();
                SchemeRegistry sr = ccm.getSchemeRegistry();
                sr.register(new Scheme("https", ssf, 443));
                return new DefaultHttpClient(ccm, base.getParams());
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}
