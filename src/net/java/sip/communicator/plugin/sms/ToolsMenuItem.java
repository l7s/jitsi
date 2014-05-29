package net.java.sip.communicator.plugin.sms;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import net.java.sip.communicator.service.contactlist.MetaContact;
import net.java.sip.communicator.service.contactlist.MetaContactGroup;
import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.protocol.AccountID;
import net.java.sip.communicator.service.protocol.Contact;

public class ToolsMenuItem
    extends JMenuItem
    implements  PluginComponent,
                ActionListener
{
    private MetaContact metaContact;

    public ToolsMenuItem()
    {
        super("SMS");

        this.addActionListener(this);
    }

    public void setCurrentContact(MetaContact metaContact)
    {   
        this.metaContact = metaContact;
    }

    public void setCurrentContactGroup(MetaContactGroup metaGroup)
    {}

    public void actionPerformed(ActionEvent e)
    {
        PluginDialog pluginDialog = new PluginDialog();

        pluginDialog.setLocation(
            Toolkit.getDefaultToolkit().getScreenSize().width/2
                - pluginDialog.getWidth()/2,
                Toolkit.getDefaultToolkit().getScreenSize().height/2
                - pluginDialog.getHeight()/2
            );

        pluginDialog.setVisible(true);
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
}