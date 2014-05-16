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
    /**
     * The tool tip shown by default over the balance button.
     */
    /*private final static String accountBalanceToolTip
        = GuiActivator.getResources().getI18NString(
            "service.gui.ACCOUNT_BALANCE_TOOL_TIP");*/

    /**
     * Creates a <tt>CallHistoryButton</tt>.
     */
    public AccountBalancePanel()
    {
        super("");

        // All items are now instantiated and could safely load the skin.
        loadSkin();

        this.setForeground(Color.BLACK);
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setFont(getFont().deriveFont(Font.BOLD, 10f));
        this.setToolTipText("tooltip");
        this.setBackground(new Color(255, 255, 255, 100));
        this.setRolloverEnabled(false);

        setText(getBalance("https://ssl7.net/oss/j/info?username=${username}&password=${password}") );
        this.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setBalanceView();
                setText(getBalance("https://ssl7.net/oss/j/info?username=${username}&password=${password}") );
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
        setText("0.00");
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
        
        url = url.replace("${username}", provUsername);
        url = url.replace("${password}", provPassword);
        
        try
        {
            res =
                HttpUtils.openURLConnection(url);
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        String[] responseSplit;
        String response;
        String amount;
        try
        {
            response = res.getContentString();
        }
        catch (IOException e)
        {
            response="ERROR\n";
            e.printStackTrace();
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
}
