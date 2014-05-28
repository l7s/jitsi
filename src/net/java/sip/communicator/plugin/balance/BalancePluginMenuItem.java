package net.java.sip.communicator.plugin.balance;

import java.awt.event.*;

import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.gui.Container;


public class BalancePluginMenuItem
    extends AbstractPluginComponent
{
    private AccountBalancePanel panelItem;

    /**
     * Creates an instance of <tt>BalancePluginMenuItem</tt>.
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
        return "Balance Plugin";
    }
    
    @Override
    public int getPositionIndex()
    {
        return -1;
    }
    
    public void updateBalance()
    {
        panelItem.setBalanceView();
    }

}
