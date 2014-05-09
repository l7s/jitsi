/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.balance;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.java.sip.communicator.plugin.desktoputil.*;
import net.java.sip.communicator.util.skin.*;

@SuppressWarnings("serial")
public class AccountBalancePanel
    extends SIPCommTextButton
    implements Skinnable
{
    /**
     * The tool tip shown by default over the balance button.
     */
    /*private final static String accountBalanceToolTip
        = GuiActivator.getResources().getI18NString(
            "service.gui.ACCOUNT_BALANCE_TOOL_TIP");*/

    /**
     * Creates a <tt>CallHistoryButton</tt>.
     */
    public AccountBalancePanel()
    {
        super("");

        // All items are now instantiated and could safely load the skin.
        loadSkin();

        this.setForeground(Color.BLACK);
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setFont(getFont().deriveFont(Font.BOLD, 10f));
        this.setToolTipText("tooltip");
        this.setBackground(new Color(255, 255, 255, 100));
        this.setRolloverEnabled(false);

        this.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setBalanceView();
                setText(BalancePluginActivator.getProvisioningService().getProvisioningPassword() );
                repaint();
            }
        });
    }

    /**
     * Sets the balance view.
     */
    private void setBalanceView()
    {
        setToolTipText("tooltip");
        setText("test");
    }


    /**
     * Loads images and sets balance view.
     */
    public void loadSkin()
    {

        this.setPreferredSize(new Dimension(90,
                                           30));
        setBalanceView();
    }
    
    private String getBalance()
    {
        String balance;
        return balance;
    }
}
