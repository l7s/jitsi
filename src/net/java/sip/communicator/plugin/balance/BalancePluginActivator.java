package net.java.sip.communicator.plugin.balance;

import java.util.*;

import net.java.sip.communicator.service.provisioning.*;
import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.util.*;

import org.jitsi.service.configuration.ConfigurationService;
import org.osgi.framework.*;

public class BalancePluginActivator
    implements BundleActivator
{
    Logger logger = Logger.getLogger(BalancePluginActivator.class);
    
    
    private static ProvisioningService provisoningService = null;
    
    static BundleContext bundleContext = null;

    /**
     * Called when this bundle is started so the Framework can perform the
     * bundle-specific activities necessary to start this bundle.
     */
    public void start(BundleContext bundleContext)
        throws Exception
    {
        Thread.sleep(4000);
        BalancePluginActivator.bundleContext = bundleContext;
        
        Hashtable<String, String> containerFilter
            = new Hashtable<String, String>();
        containerFilter.put(
                Container.CONTAINER_ID,
                Container.CONTAINER_ACCOUNT_BALANCE.getID());

        bundleContext.registerService(
            PluginComponentFactory.class.getName(),
            new PluginComponentFactory(
                    Container.CONTAINER_ACCOUNT_BALANCE)
            {
                @Override
                protected PluginComponent getPluginInstance()
                {
                    System.out.println("\tAdding balance container");
                    return new BalancePluginMenuItem(this);
                }
            },
            containerFilter);

        if (logger.isInfoEnabled())
            logger.info("BALANCE... [REGISTERED]");
    }

    public void stop(BundleContext bc)
        throws Exception
    {
    }
    
    public static ProvisioningService getProvisioningService()
    {
        if (provisoningService == null)
        {
            ServiceReference confReference
                = bundleContext.getServiceReference(
                    ProvisioningService.class.getName());
            provisoningService
                = (ProvisioningService)bundleContext.getService(confReference);
        }
        return provisoningService;
    }
}