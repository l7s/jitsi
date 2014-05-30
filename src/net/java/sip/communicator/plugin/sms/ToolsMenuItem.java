package net.java.sip.communicator.plugin.sms;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.SwingWorker;

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
        super("SMS");
        this.addActionListener(this);
    }

    public void setCurrentContactGroup(MetaContactGroup metaGroup)
    {}

    public void actionPerformed(ActionEvent e)
    {
        this.popupDialog = new PopupDialog(1);

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
        return 0;
    }

    public boolean isNativeComponent()
    {
        return false;
    }
    
    public String getNumber(String url)
    {
        System.out.println("\tGetting number from server");
        HttpUtils.HTTPResponseResult res = null;
        
        String provUsername=SMSPluginActivator.getProvisioningService().getProvisioningUsername();
        String provPassword=SMSPluginActivator.getProvisioningService().getProvisioningPassword();
        
        if(provUsername==null || provPassword==null)
        {
            System.out.println("\tERROR: provisioning password: "+ provPassword +
                                            "\t\tprovisioning username: " + provUsername);
            return "\nERROR";
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
            return "\nERROR";
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
                return "\nERROR";
            }
            responseSplit = response.split("\n");
            if(responseSplit[2].split("=")[1] == null )
            {
                System.out.println("\tNo number.\n");
                return "\nNO_NUM";
            }
            else
            {
                System.out.println("\tThere is a number.\n");
                number = responseSplit[2].split("=")[1];
                return "\nOK";
            }
        }
        else
        {
            System.out.println("\tResponse is null, connection error.");
            return "\nERROR";
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
                PluginDialog pluginDialog;
                if(error=="\nOK")
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
                else
                {
                    System.out.println("\tTEST "+ error );
                    popupDialog.setVisible(false);
                }
                isWorkerRunning=false;
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