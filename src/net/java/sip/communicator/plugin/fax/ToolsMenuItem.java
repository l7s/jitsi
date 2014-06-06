package net.java.sip.communicator.plugin.fax;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.java.sip.communicator.plugin.desktoputil.ErrorDialog;
import net.java.sip.communicator.service.contactlist.MetaContact;
import net.java.sip.communicator.service.contactlist.MetaContactGroup;
import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.httputil.HttpUtils;
import net.java.sip.communicator.service.protocol.AccountID;
import net.java.sip.communicator.service.protocol.Contact;

public class ToolsMenuItem
    extends JMenuItem
    implements  PluginComponent,
                ActionListener
{ 
    private String number = null;
    
    private boolean isWorkerRunning = false;
    
    private PopupDialog popupDialog;
    /* Default Constructor */
    public ToolsMenuItem()
    {
        super("FAX");
        this.addActionListener(this);
    }

    public void setCurrentContactGroup(MetaContactGroup metaGroup)
    {}

    public void actionPerformed(ActionEvent e)
    {
        this.popupDialog = new PopupDialog();

        this.popupDialog.setLocation(
            Toolkit.getDefaultToolkit().getScreenSize().width/2
                - this.popupDialog.getWidth()/2,
                Toolkit.getDefaultToolkit().getScreenSize().height/2
                - this.popupDialog.getHeight()/2
            );

        popupDialog.setVisible(true);
        setWorker();
    }

    public String getConstraints()
    {
        return null;
    }

    public Container getContainer()
    {
        return Container.CONTAINER_TOOLS_MENU;
    }

    public int getPositionIndex()
    {
        return 1;
    }

    public boolean isNativeComponent()
    {
        return false;
    }
    
    public String getNumber(String url)
    {
        System.out.println("\tGetting number from server");
        HttpUtils.HTTPResponseResult res = null;
        
        String provUsername=FAXPluginActivator.getProvisioningService().getProvisioningUsername();
        String provPassword=FAXPluginActivator.getProvisioningService().getProvisioningPassword();
        
        if(provUsername==null || provPassword==null)
        {
            System.out.println("\tERROR: provisioning password: "+ provPassword +
                                            "\t\tprovisioning username: " + provUsername);
            ErrorDialog errorDialog = new ErrorDialog( null, 
                "No connection","Could not resolve your username and password.\n"+
                        "Please verify that your internet connection is working.", ErrorDialog.WARNING);
            errorDialog.showDialog();      
            return "ERROR_HANDLED";
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
            return "Open connection error!";
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
                System.out.println("\tOpen connection error!");
                return "Open connection error!";
            }
            
            responseSplit = response.split("\n");
            
            if(responseSplit[2].length()< 13 )
            {
                System.out.println("\tNo number.\n");
                return "NO_NUM";
            }
            else
            {
                System.out.println("\tThere is a number.\n");
                number = responseSplit[2].split("=")[1];
                return "OK";
            }
        }
        else
        {
            System.out.println("\tResponse is null, connection error.");
            return "Response is null, connection error.";
        }
    }
    
    private String error = null;
    public void setWorker()
    {
        
        if(isWorkerRunning==true)
        {
            System.out.println("Number getter is already running. Aborting.");
            return;
        }
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            protected Void doInBackground()
            {
                isWorkerRunning=true;
                error = getNumber("https://ssl7.net/oss/j/info?username=${username}&password=${password}");
                return null;
            }
            
            protected void done()
            {
                isWorkerRunning=false;
                
                PluginDialog pluginDialog;
                if(error=="OK")
                {
                    pluginDialog = new PluginDialog(number);

                    pluginDialog.setLocation(
                        Toolkit.getDefaultToolkit().getScreenSize().width/2
                            - pluginDialog.getWidth()/2,
                            Toolkit.getDefaultToolkit().getScreenSize().height/2
                            - pluginDialog.getHeight()/2);
                    
                    popupDialog.setVisible(false);
                    pluginDialog.setVisible(true);
                }
                else if(error=="NO_NUM")
                {   
                    popupDialog.setPopup(2);;
                    
                    popupDialog.setLocation(
                        Toolkit.getDefaultToolkit().getScreenSize().width/2
                            - popupDialog.getWidth()/2,
                            Toolkit.getDefaultToolkit().getScreenSize().height/2
                            - popupDialog.getHeight()/2);
                    
                    popupDialog.setVisible(true);
                }
                else if(error=="ERROR_HANDLED"){}
                else
                {
                    System.out.println("\tSMS Plugin Response: "+ error );
                    ErrorDialog errorDialog = new ErrorDialog( null, 
                        "ERROR","There was an unknown error:\n"+
                                error);
                    
                    errorDialog.showDialog();      
                    popupDialog.setVisible(false);
                }
            }
        };
        worker.execute();
    }
    /* Unused classes */
    @Override
    public PluginComponentFactory getParentFactory()
    {
        return null;
    }

    @Override
    public void setCurrentContact(Contact contact)
    {   
    }

    @Override
    public void setCurrentContact(Contact contact, String resourceName)
    {   
    }

    @Override
    public void setCurrentAccountID(AccountID accountID)
    {   
    }

    @Override
    public void setCurrentContact(MetaContact metaContact)
    {
    }
}