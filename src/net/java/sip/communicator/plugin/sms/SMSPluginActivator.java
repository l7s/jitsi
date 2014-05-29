package net.java.sip.communicator.plugin.sms;

import java.util.*;

import net.java.sip.communicator.util.*;
import net.java.sip.communicator.service.gui.*;

import org.osgi.framework.*;

public class SMSPluginActivator
    implements  BundleActivator
{
    Logger logger = Logger.getLogger(SMSPluginActivator.class);

    static BundleContext bundleContext = null;
    
    private ServiceRegistration menuRegistration = null;

    public void start(BundleContext bc) throws Exception
    {     
        SMSPluginActivator.bundleContext = bc;

        Hashtable<String, String> toolsMenuFilter =
            new Hashtable<String, String>();
        toolsMenuFilter.put(Container.CONTAINER_ID,
                Container.CONTAINER_TOOLS_MENU.getID());

        menuRegistration = bc.registerService(
            PluginComponentFactory.class.getName(),
            new PluginComponentFactory(Container.CONTAINER_TOOLS_MENU)
            {
                @Override
                protected PluginComponent getPluginInstance()
                {
                    return new ToolsMenuItem();
                }
            },
            toolsMenuFilter);

        if (logger.isInfoEnabled())
            logger.info("SMS [REGISTERED]");
    }

    public void stop(BundleContext bc) throws Exception
    {
    }
}
