/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.balance;

import java.awt.event.*;

import net.java.sip.communicator.service.contactlist.*;
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
    implements ActionListener
{
    private AccountBalancePanel panelItem;

    /**
     * Creates an instance of <tt>ExamplePluginMenuItem</tt>.
     */
    public  BalancePluginMenuItem(PluginComponentFactory parentFactory)
    {
        super(Container.CONTAINER_CONTACT_RIGHT_BUTTON_MENU, parentFactory);
    }

    /**
     * Listens for events triggered by user clicks on this menu item. Opens
     * the <tt>PluginDialog</tt>.
     */
    public void actionPerformed(ActionEvent e)
    {
    }

    /*
     * Implements PluginComponent#getComponent().
     */
    public Object getComponent()
    {
        if (panelItem == null)
        {
            panelItem = new AccountBalancePanel();
            panelItem.addActionListener(this);
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

    /**
     * Sets the current <tt>MetaContact</tt>. This in the case of the contact
     * right button menu container would be the underlying contact in the
     * contact list.
     *
     * @param metaContact the <tt>MetaContact</tt> to set.
     *
     * @see PluginComponent#setCurrentContact(MetaContact)
     */
    @Override
    public void setCurrentContact(MetaContact metaContact)
    {
    }
}
