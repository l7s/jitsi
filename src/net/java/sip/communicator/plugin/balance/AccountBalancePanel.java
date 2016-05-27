/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.balance;

import java.awt.*;
import java.awt.event.*;
import java.net.URLEncoder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.*;
import javax.swing.SwingWorker;

import net.java.sip.communicator.plugin.desktoputil.*;
import net.java.sip.communicator.service.httputil.*;
import net.java.sip.communicator.util.skin.*;

@SuppressWarnings("serial")
public class AccountBalancePanel
    extends SIPCommTextButton
    implements Skinnable
{

    private String amount="";
    private Timer timer;
    private UpdateBalance listener;
    private int balanceLength = 9;
    private boolean isWorkerRunning=false;
    
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
        this.setBackground(new Color(255, 255, 255, 65));
        listener= new UpdateBalance();
        this.addActionListener(listener);
        
        timer = new Timer(300000, listener);
        timer.setRepeats(true);
        timer.start();
    }
    
    /**
     * Updates Balance amount and Sets it.
     */
    private class UpdateBalance implements ActionListener
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
        if(isWorkerRunning==true)
        {
            System.out.println("Balance getter is already running. Aborting.");
            return;
        }
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            protected Void doInBackground()
            {
                isWorkerRunning=true;
				try
				{
					getBalance(BalancePluginActivator.userInfoUrl );
				}
				catch (UnsupportedEncodingException ex)
                {
                    System.out.println("\tBalance: Http Post encoding error");
                    ex.printStackTrace();
                }	
                return null;
            }
            
            protected void done()
            {
             // If balance is too long show it in tooltip message, else there should be no tooltip.
                if(amount.length() > balanceLength)
                {
                   String temp = amount.substring(0,4) + "...";
                   setText(temp);
                   setToolTipText(amount);
                }
                else
                {
                    setText(amount);
                    setToolTipText(null);   
                }
                /*
                 *  Get font metric and change button size according to text inside
                 */
                FontMetrics metrics = getFontMetrics( getFont() ); 
                int width = metrics.stringWidth( getText() );
                int height = metrics.getHeight();
                Dimension newDimension =  new Dimension(width+10 ,height+10);
                setPreferredSize(newDimension);
                setBounds(new Rectangle(
                               getLocation(), getPreferredSize()));
                isWorkerRunning=false;
            }
        };
        worker.execute();
    }


    /**
     * Loads images and sets balance view.
     */
    public void loadSkin()
    {
        setBalanceView();
    }
    
    private String getBalance(String url) throws UnsupportedEncodingException
    {
        System.out.println("\tGetting balance from server");
        
        HttpUtils.HTTPResponseResult res = null;
        
        String provUsername = URLEncoder.encode(BalancePluginActivator.getProvisioningService().getProvisioningUsername(), "UTF-8");
        String provPassword = URLEncoder.encode(BalancePluginActivator.getProvisioningService().getProvisioningPassword(), "UTF-8");
        
        if(provUsername==null || provPassword==null)
        {
            System.out.println("\tERROR: provisioning password: "+ provPassword +
                                            "\t\tprovisioning username: " + provUsername);
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
             amount = "\u00a3 " + amount;
             balanceLength = 9;
            }
            else if(responseSplit[1].split("=")[1].contains("USD") )
            {
             amount = "\u0024 " + amount;
             balanceLength = 9;
            }
            else if(responseSplit[1].split("=")[1].contains("EUR") )
            {
             amount = "\u20ac " + amount;
             balanceLength = 9;
            }
            else if(responseSplit[1].split("=")[1].contains("PLN") )
            {
             amount = amount + " z\u0142";
             balanceLength = 10;
            }
            else
            {
                amount = amount + responseSplit[1].split("=")[1];
                balanceLength = 8 + responseSplit[1].split("=")[1].length();
            }
            
            System.out.println("\tDone.\n");
            return amount;
        }
        else
        {
            System.out.println("\tResponse is null, connection error.");
            return amount;
        }
    }

}
