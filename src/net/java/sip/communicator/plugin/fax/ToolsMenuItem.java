package net.java.sip.communicator.plugin.fax;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.SwingWorker;

import net.java.sip.communicator.plugin.desktoputil.DesktopUtilActivator;
import net.java.sip.communicator.plugin.desktoputil.ErrorDialog;
import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.httputil.HttpUtils;

public class ToolsMenuItem
    extends AbstractPluginComponent
    implements ActionListener
{ 
    private String number[];
    
    private boolean isWorkerRunning = false;
    
    public static PluginDialog pluginDialog;
    
    private PopupDialog popupDialog;
    private PopupDialog popupDialog2;
    
    private JMenuItem faxMenu = new JMenuItem("Send FAX");
    /* Default Constructor */
    public ToolsMenuItem(PluginComponentFactory parentFactory)
    {
        super(Container.CONTAINER_TOOLS_MENU, parentFactory);
        this.faxMenu.setIcon( DesktopUtilActivator.getResources().getImage("plugin.fax.FAX_16"));
        this.faxMenu.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e)
    {
        if(pluginDialog!=null)
        {
            popupDialog.setVisible(false);
            pluginDialog.setVisible(true);
            pluginDialog.setState(java.awt.Frame.NORMAL);
        }
        else
        {
            this.popupDialog = new PopupDialog();
            this.popupDialog.setPopup(1);
    
            this.popupDialog.setLocation(
                Toolkit.getDefaultToolkit().getScreenSize().width/2
                    - this.popupDialog.getWidth()/2,
                    Toolkit.getDefaultToolkit().getScreenSize().height/2
                    - this.popupDialog.getHeight()/2
                );
    
            popupDialog.setVisible(true);
            setWorker();
        }
    }
    
    public String getNumber(String url)
    {
        System.out.println("\tGetting fax numbers array from server");
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
            
            Map<String,String> responseMap = new HashMap<String,String>();
            if(responseSplit.length==0)
            {
                System.out.println("\tResponse is null, connection error.");
                return "Response is null, connection error.";
            }
            
            for(int i=0 ; i<responseSplit.length; i++)
            {
                String response1[] = responseSplit[i].split("=");
                if(response1.length==0)
                {
                    System.out.println("\tError while parsing response.");
                    return "Error while parsing response.";
                }
                
                if(response1.length==1)
                {
                    responseMap.put(response1[0],"EMPTY_STRING");
                    System.out.println("\t"+response1[0]+" = "+"EMPTY_STRING");
                }
                else
                {
                    responseMap.put(response1[0],response1[1]);
                    System.out.println("\t"+response1[0]+" = "+response1[1]);
                }
                
            }
            System.out.println("\tMAP: \n."+ responseMap + "\n");
            if(responseMap.containsKey("user.FAX") )
            {
                if(responseMap.get("user.FAX").equalsIgnoreCase("EMPTY_STRING" ))
                {
                    return "NO_NUM";
                }
                number = responseMap.get("user.FAX").split(",");
                return "OK";
            }
            else
            {
                return "Could not retrive fax numbers. Sorry.";
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
                error = getNumber( FAXPluginActivator.getResources().getSettingsString(
                    "net.java.sip.communicator.l7s.USER_INFO_URL"));
                return null;
            }
            
            protected void done()
            {
                isWorkerRunning=false;
                
                if(error.equalsIgnoreCase("OK") )
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
                else if(error.equalsIgnoreCase("NO_NUM"))
                {   
                    popupDialog2 = new PopupDialog();
                    popupDialog2.setPopup(2);
                    
                    popupDialog2.setLocation(
                        Toolkit.getDefaultToolkit().getScreenSize().width/2
                            - popupDialog2.getWidth()/2,
                            Toolkit.getDefaultToolkit().getScreenSize().height/2
                            - popupDialog2.getHeight()/2);
                    
                    popupDialog.setVisible(false);
                    popupDialog2.setVisible(true);
                }
                else if(error.equalsIgnoreCase("ERROR_HANDLED")){}
                else
                {
                    System.out.println("\tFAX Plugin Response: "+ error );
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
    
    @Override
    public String getName()
    {
        return this.faxMenu.getText();
    }

    @Override
    public Object getComponent()
    {
        return faxMenu;
    }
    
    @Override
    public int getPositionIndex()
    {
        return 0;
    }
}