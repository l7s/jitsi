package net.java.sip.communicator.plugin.sms;

import java.util.*;

import net.java.sip.communicator.util.*;
import net.java.sip.communicator.plugin.balance.BalancePluginActivator;
import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.browserlauncher.*;
import net.java.sip.communicator.service.provisioning.ProvisioningService;
import net.java.sip.communicator.service.resources.ResourceManagementServiceUtils;

import org.jitsi.service.configuration.ConfigurationService;
import org.jitsi.service.resources.ResourceManagementService;
import org.osgi.framework.*;

public class SMSPluginActivator
    implements  BundleActivator
{
    Logger logger = Logger.getLogger(SMSPluginActivator.class);
    
    private static final String DISABLED_PROP
    = "net.java.sip.communicator.plugin.sms.DISABLED";

    static BundleContext bundleContext = null;
    
    private ServiceRegistration menuRegistration = null;
    
    private static ProvisioningService provisoningService = null;
    
    private static BrowserLauncherService browserService = null;
    
    private static ResourceManagementService resourcesService = null;
    
    private static ConfigurationService configurationService = null;

    private static UIService uiService = null;

    public void start(BundleContext bc) throws Exception
    {
        Thread.sleep(4000);
        SMSPluginActivator.bundleContext = bc;
        
        if(getConfigurationService().getBoolean(DISABLED_PROP, true) )
        {
            System.out.println("\nSMS Plugin Disabled, exiting.\n");
            return;
        }
        
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
    
    /*
     * Service getters
     */
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
    
    public static BrowserLauncherService getBrowserService()
    {
        if (browserService == null)
        {
            ServiceReference confReference
                = bundleContext.getServiceReference(
                    BrowserLauncherService.class.getName());
            browserService
                = (BrowserLauncherService)bundleContext.getService(confReference);
        }
        return browserService;
    }    
    
    public static ResourceManagementService getResources()
    {
        if (resourcesService == null)
            resourcesService =
                ResourceManagementServiceUtils
                    .getService(SMSPluginActivator.bundleContext);
        return resourcesService;
    }
    
    public static ConfigurationService getConfigurationService()
    {
        if (configurationService == null)
        {
            ServiceReference confReference
                = bundleContext.getServiceReference(
                    ConfigurationService.class.getName());
            configurationService
                = (ConfigurationService)bundleContext.getService(confReference);
        }
        return configurationService;
    }
    
    public static UIService getUIService()
    {
        if (uiService == null)
        {
            ServiceReference confReference
                = bundleContext.getServiceReference(
                    UIService.class.getName());
            uiService
                = (UIService)bundleContext.getService(confReference);
        }
        return uiService;
    }
}
