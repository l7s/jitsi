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

@SuppressWarnings("serial")
public class AccountBalancePanel
    extends SIPCommTextButton
    implements Skinnable
{

    private String amount="";
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

        setText(getBalance("https://ssl7.net/oss/j/info?username=${username}&password=${password}"));
        this.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setText(getBalance("https://ssl7.net/oss/j/info?username=${username}&password=${password}") );
                if(amount.length()>7)
                    // If balance is too long show it in tooltip message, else there should be no tooltip.
                    setToolTipText(amount);
                else
                    setToolTipText(null);
                repaint();
            }
        });
    }

    /**
     * Sets the balance view.
     */
    private void setBalanceView()
    {
        setToolTipText("tooltip");
        setText("");
    }


    /**
     * Loads images and sets balance view.
     */
    public void loadSkin()
    {

        this.setPreferredSize(new Dimension(50,
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
}
