package net.java.sip.communicator.plugin.balance;

import java.util.*;

import net.java.sip.communicator.service.protocol.Call;
import net.java.sip.communicator.service.protocol.CallPeer;
import net.java.sip.communicator.service.protocol.CallPeerState;
import net.java.sip.communicator.service.protocol.OperationSetBasicTelephony;
import net.java.sip.communicator.service.protocol.ProtocolProviderService;
import net.java.sip.communicator.service.provisioning.*;
import net.java.sip.communicator.service.protocol.event.*;
import net.java.sip.communicator.service.callhistory.*;
import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.util.*;

import org.jitsi.service.configuration.ConfigurationService;
import org.osgi.framework.*;

public class BalancePluginActivator
    implements  BundleActivator,
                ServiceListener,
                CallListener
{
    Logger logger = Logger.getLogger(BalancePluginActivator.class);
    
    
    private static ProvisioningService provisoningService = null;
    private static CallHistoryService callHistoryService = null;
    private static BalancePluginMenuItem BalanceMenuItem= null;
    
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
                    BalanceMenuItem = new BalancePluginMenuItem(this);
                    return BalanceMenuItem;
                }
            },
            containerFilter);

        if (logger.isInfoEnabled())
            logger.info("BALANCE... [REGISTERED]");
        
        startInternal(bundleContext);
    }

    public void stop(BundleContext bc)
        throws Exception
    {
        stopInternal(bc);
    }
    
    private void startInternal(BundleContext bc)
    {
        // start listening for newly register or removed protocol providers
        bc.addServiceListener(this);

        ServiceReference[] protocolProviderRefs;
        try
        {
            protocolProviderRefs = bc.getServiceReferences(
                ProtocolProviderService.class.getName(),
                null);
        }
        catch (InvalidSyntaxException ex)
        {
            // this shouldn't happen since we're providing no parameter string
            // but let's log just in case.
            logger.error(
                "Error while retrieving service refs", ex);
            return;
        }

        // in case we found any
        if (protocolProviderRefs != null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Found "
                         + protocolProviderRefs.length
                         + " already installed providers.");
            for (ServiceReference protocolProviderRef : protocolProviderRefs)
            {
                ProtocolProviderService provider
                    = (ProtocolProviderService)
                        bc.getService(protocolProviderRef);

                this.handleProviderAdded(provider);
            }
        }
    }
    
    /**
     * When new protocol provider is registered we check
     * does it supports needed Op. Sets and if so add a listener to it
     *
     * @param serviceEvent ServiceEvent
     */
    public void serviceChanged(ServiceEvent serviceEvent)
    {
        Object sService
            = bundleContext.getService(serviceEvent.getServiceReference());

        if (logger.isTraceEnabled())
            logger.trace("Received a service event for: " +
            sService.getClass().getName());

        // we don't care if the source service is not a protocol provider
        if (!(sService instanceof ProtocolProviderService))
            return;

        if (logger.isDebugEnabled())
            logger.debug("Service is a protocol provider.");
        switch (serviceEvent.getType())
        {
        case ServiceEvent.REGISTERED:
            this.handleProviderAdded((ProtocolProviderService)sService);
            break;

        case ServiceEvent.UNREGISTERING:
            this.handleProviderRemoved( (ProtocolProviderService) sService);
            break;
        }
    }
    
    private void handleProviderAdded(ProtocolProviderService provider)
    {
        if (logger.isDebugEnabled())
            logger.debug("Adding protocol provider " + provider.getProtocolName());

        OperationSetBasicTelephony<?> basicTelephonyOpSet
            = provider.getOperationSet(OperationSetBasicTelephony.class);

        if (basicTelephonyOpSet != null)
        {
            basicTelephonyOpSet.addCallListener(this);
        }
    }
    
    private void handleProviderRemoved(ProtocolProviderService provider)
    {
        OperationSetBasicTelephony<?> basicTelephonyOpSet
            = provider.getOperationSet(OperationSetBasicTelephony.class);

        if (basicTelephonyOpSet != null)
        {
            basicTelephonyOpSet.removeCallListener(this);
        }
    }
    
    
    /**
     * Stops the impl and removes necessary listeners.
     * @param bc the current bundle context.
     */
    private void stopInternal(BundleContext bc)
    {
        // start listening for newly register or removed protocol providers
        bc.removeServiceListener(this);

        ServiceReference[] protocolProviderRefs;
        try
        {
            protocolProviderRefs = bc.getServiceReferences(
                ProtocolProviderService.class.getName(),
                null);
        }
        catch (InvalidSyntaxException ex)
        {
            // this shouldn't happen since we're providing no parameter string
            // but let's log just in case.
            logger.error(
                "Error while retrieving service refs", ex);
            return;
        }

        // in case we found any
        if (protocolProviderRefs != null)
        {
            for (ServiceReference protocolProviderRef : protocolProviderRefs)
            {
                ProtocolProviderService provider
                    = (ProtocolProviderService)
                        bc.getService(protocolProviderRef);

                this.handleProviderRemoved(provider);
            }
        }
    }
    
    /**
     * Not used.
     * @param event a CalldEvent instance describing the new outgoing call.
     */
    public void incomingCallReceived(CallEvent event)
    {}

    /**
     * Not used.
     * @param event a CalldEvent instance describing the new outgoing call.
     */
    public void outgoingCallCreated(CallEvent event)
    {}

    /**
     * Not used
     * @param event the <tt>CallEvent</tt> containing the source call.
     */
    public void callEnded(CallEvent event)
    {
        logger.info("Call ended, update Balance.");
        BalanceMenuItem.updateBalance();
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
    
    public static CallHistoryService getCallHistoryService()
    {
        if (callHistoryService == null)
        {
            ServiceReference confReference
                = bundleContext.getServiceReference(
                    CallHistoryService.class.getName());
            callHistoryService
                = (CallHistoryService)bundleContext.getService(confReference);
        }
        return callHistoryService;
    }
}