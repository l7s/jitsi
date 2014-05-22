/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.balance;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;

import net.java.sip.communicator.plugin.desktoputil.*;
import net.java.sip.communicator.service.httputil.*;
import net.java.sip.communicator.util.skin.*;
import net.java.sip.communicator.service.protocol.event.CallEvent;
import net.java.sip.communicator.service.protocol.event.CallListener;

@SuppressWarnings("serial")
public class AccountBalancePanel
    extends SIPCommTextButton
    implements Skinnable, CallListener
{

    private String amount="";
    private Timer timer;
    private UpdateBalance listener;
    /**
     * Creates a Balance panel Button.
     */
    public AccountBalancePanel()
    {
        super("");

        // All items are now instantiated and could safely load the skin.
        loadSkin();

        this.setForeground(Color.BLACK);
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setFont(getFont().deriveFont(Font.BOLD, 10f));
        this.setBackground(new Color(255, 255, 255, 100));
        this.setRolloverEnabled(false);

        listener= new UpdateBalance();
        this.addActionListener(listener);
        
        timer = new Timer(300000, listener);
        timer.setRepeats(true);
        timer.start();
    }

    public class UpdateBalance implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            setBalanceView();
            repaint();
        }
    }
    
    /**
     * Sets the balance view.
     */
    public void setBalanceView()
    {
        SwingUtilities.invokeLater(new Runnable (){
            public void run(){
                setText(getBalance("https://ssl7.net/oss/j/info?username=${username}&password=${password}") );
                if(amount.length()>11)
                    // If balance is too long show it in tooltip message, else there should be no tooltip.
                    setToolTipText(amount);
                else
                    setToolTipText(null);
            }
        });
    }


    /**
     * Loads images and sets balance view.
     */
    public void loadSkin()
    {

        this.setPreferredSize(new Dimension(60,
                                           30));
        setBalanceView();
    }
    
    private String getBalance(String url)
    {
        System.out.println("\tGetting balance from server");
        HttpUtils.HTTPResponseResult res = null;
        
        String provUsername=BalancePluginActivator.getProvisioningService().getProvisioningUsername();
        String provPassword=BalancePluginActivator.getProvisioningService().getProvisioningPassword();
        if(provUsername==null || provPassword==null)
        {
            System.out.println("\tERROR: provisioning password: "+ provPassword +
                                            "\n\t\tprovisioning username: " + provUsername);
            return amount;
        }
        
        url = url.replace("${username}", provUsername);
        url = url.replace("${password}", provPassword);
        
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
        String[] responseSplit;
        String response;   

        if(res!=null)
        {
            try
            {
                response = res.getContentString();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return amount;
            }
            
            responseSplit = response.split("\n");
            amount=responseSplit[0].split("=")[1];
            
            if(responseSplit[1].split("=")[1].contains("GBP") )
            {
             amount += " GBP ";
            }
            if(responseSplit[1].split("=")[1].contains("USD") )
            {
             amount += " USD ";
            }
            System.out.println("\tDone.");
            return amount;
        }
        else
        {
            System.out.println("\tResponse is null, connection error.");
            return amount;
        }
    }

    public void incomingCallReceived(CallEvent event)
    {
        System.out.println("test");
        
    }


   
    public void outgoingCallCreated(CallEvent event)
    {
        // TODO Auto-generated method stub
        
    }


    
    public void callEnded(CallEvent event)
    {
        setBalanceView();
    }
}
