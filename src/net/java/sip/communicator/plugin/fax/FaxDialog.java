package net.java.sip.communicator.plugin.fax;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import net.java.sip.communicator.plugin.balance.BalancePluginActivator;
import net.java.sip.communicator.plugin.desktoputil.*;
import net.java.sip.communicator.service.httputil.HttpUtils;


public class FaxDialog
extends JFrame
{
    private Timer timer = null;
    
    private int sec = 0;
    private int min = 0;
    private int millis = 0;
    
    private String timeStr = String.format("%02d:%02d", 0, 0);
    
    private JLabel timerLabel = new JLabel("00:00\n\nFinished");
    private JLabel cloudImage = new JLabel(DesktopUtilActivator.getResources().getImage("plugin.fax.CLOUD_FAX") ); 
    private JLabel hwImage = new JLabel(DesktopUtilActivator.getResources().getImage("plugin.fax.HW_FAX") );
    private JButton closeButton = new JButton("Close");
    
    private JPanel mainPanel = new JPanel();
    
    private String username = null;
    private String pass = null;
    
    FaxDialog(String username, String pass)
    {
        this.username = username;
        this.pass = pass;

         this.setTitle("Sending FAX");
         this.setIconImage( FAXPluginActivator.getResources().getImage("service.gui.SIP_COMMUNICATOR_LOGO_64x64").getImage());
         this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
         
         this.mainPanel.setBorder(
             BorderFactory.createEmptyBorder(10, 10, 5, 10));
         this.getContentPane().add(mainPanel);
         /* Set Layout      */
         GridBagLayout layout = new GridBagLayout();
         this.mainPanel.setLayout(layout);
         GridBagConstraints c = new GridBagConstraints();
         c.fill = GridBagConstraints.HORIZONTAL;
         c.insets = new Insets(0, 5, 5, 0);
         //row 0
         c.weightx = 0;
         c.fill = GridBagConstraints.NONE;
         c.gridx = 0;
         c.gridwidth = 1;
         c.gridy = 0;
         this.mainPanel.add(cloudImage, c);
         c.weightx = 0.5;
         c.fill = GridBagConstraints.HORIZONTAL;
         c.gridx = 1;
         c.gridwidth = 1;
         c.gridy = 0;
         c.insets = new Insets(0, 27, 0, 27);
         this.mainPanel.add(timerLabel, c);
         c.weightx = 0;
         c.fill = GridBagConstraints.NONE;
         c.gridx = 2;
         c.gridwidth = 1;
         c.gridy = 0;
         c.insets = new Insets(0, 0, 0, 2);
         this.mainPanel.add(hwImage, c);
         //row 1
         c.weightx = 0;
         c.fill = GridBagConstraints.NONE;
         c.gridx = 1;
         c.gridwidth = 1;
         c.gridy = 1;
         c.insets = new Insets(5, 0, 0, 0);
         this.mainPanel.add(closeButton, c);
         this.closeButton.setEnabled(false);
         /* End of layout setting   */
         
         this.closeButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e)
             {
                 FaxDialog.this.dispose();
             }            
         });
         
         this.setResizable(false);
         this.pack();
         
         timer = new Timer(1000, new UpdateFAX());
         timer.setRepeats(true);
         timer.start();
         
    }
    
    private class UpdateFAX implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            updateFAXStatus(username, pass);
        }
    }
    
    void updateFAXStatus(String username, String password)
    {
        HttpUtils.HTTPResponseResult res = null;
        
        String url = "https://ssl7.net/voipdito.com/u/api?o=fax&a=send&api_email="
            +"${username}&api_password=${password}";
        //String url = "https://ssl7.net/%web_domain/u/fax/send?api_email="
          //  +"${username}&api_password=${password}";
        
        url = url.replace("${username}", username);
        url = url.replace("${password}", password);

        try
        {
            res =
                HttpUtils.openURLConnection(url);
        }
        catch(Throwable t)
        {
            System.out.println("\tOpen connection error!");
            t.printStackTrace();
        }
        
        String response;   

        if(res!=null)
        {
            try
            {
                response = res.getContentString();
                System.out.println("FAX: Response:\n."+response);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.out.println("FAX: No connection error.");
                return;
            }
        }
        else
        {
            System.out.println("FAX: No response error.");
        }
        
        millis+= 1000;
        sec  = (int)(millis/ 1000) % 60 ;
        min  = (int)((millis/ (1000) / 60));
        timeStr = String.format("\t%02d:%02d", min, sec);
        timerLabel.setText(timeStr);
        
        /**/
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.closeButton.setEnabled(true);
    }
}
