/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.balance;

import java.awt.event.*;

import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.gui.Container;

/**
 * The <tt>ExamplePluginMenuItem</tt> is a <tt>JMenuItem</tt> that implements
 * the <tt>PluginComponent</tt> interface. The <tt>PluginComponent</tt>
 * interface allows us to add this menu item in the user interface bundle by
 * registering it through the the bundle context
 * (see {@link ExamplePluginActivator#start(org.osgi.framework.BundleContext)}).
 *
 * @author Yana Stamcheva
 */
public class BalancePluginMenuItem
    extends AbstractPluginComponent
{
    private AccountBalancePanel panelItem;

    /**
     * Creates an instance of <tt>ExamplePluginMenuItem</tt>.
     */
    public  BalancePluginMenuItem(PluginComponentFactory parentFactory)
    {
        super(Container.CONTAINER_ACCOUNT_BALANCE, parentFactory);
    }

    /*
     * Implements PluginComponent#getComponent().
     */
    public Object getComponent()
    {
        if (panelItem == null)
        {
            panelItem = new AccountBalancePanel();
        }
        return panelItem ;
    }

    /*
     * Implements PluginComponent#getName().
     */
    public String getName()
    {
        return "Example plugin";
    }
    
    @Override
    public int getPositionIndex()
    {
        return -1;
    }

}
