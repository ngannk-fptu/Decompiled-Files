/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.ResourceAware
 *  org.apache.commons.lang.StringUtils
 *  org.jivesoftware.smack.SmackException
 *  org.jivesoftware.smack.SmackException$NoResponseException
 *  org.jivesoftware.smack.SmackException$NotConnectedException
 *  org.jivesoftware.smack.SmackException$NotLoggedInException
 *  org.jivesoftware.smack.XMPPConnection
 *  org.jivesoftware.smack.XMPPException
 *  org.jivesoftware.smack.packet.Presence
 *  org.jivesoftware.smack.packet.Presence$Mode
 *  org.jivesoftware.smack.packet.Presence$Type
 *  org.jivesoftware.smack.roster.Roster
 *  org.jivesoftware.smack.roster.RosterEntry
 *  org.jivesoftware.smack.tcp.XMPPTCPConnection
 *  org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
 *  org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration$Builder
 *  org.jxmpp.jid.BareJid
 *  org.jxmpp.jid.impl.JidCreate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.impresence2.reporter;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.impresence2.reporter.LoginPresenceReporter;
import com.atlassian.confluence.extra.impresence2.reporter.PresenceException;
import com.atlassian.confluence.extra.impresence2.reporter.RosterFactory;
import com.atlassian.confluence.extra.impresence2.util.LocaleSupport;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.ResourceAware;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JabberPresenceReporter
extends LoginPresenceReporter
implements ResourceAware {
    private static final String DOMAIN_PREFIX = "extra.im.domain.";
    private static final String PORT_PREFIX = "extra.im.port.";
    private static final Logger logger = LoggerFactory.getLogger(JabberPresenceReporter.class);
    private static final Map<Presence.Mode, String> STATUS_MAP = Collections.unmodifiableMap(new HashMap<Presence.Mode, String>(){
        {
            this.put(Presence.Mode.available, "im_available");
            this.put(Presence.Mode.away, "im_away");
            this.put(Presence.Mode.available, "im_free_chat");
            this.put(Presence.Mode.dnd, "im_dnd");
            this.put(Presence.Mode.xa, "im_away");
        }
    });
    public static final String KEY = "jabber";
    public static final String DEFAULT_JABBER_DOMAIN = "chat.example.com";
    public static final int DEFAULT_JABBER_PORT = 5222;
    private final RosterFactory rosterFactory;
    private String resourcePath;
    private XMPPTCPConnection xmppConnection;

    public JabberPresenceReporter(LocaleSupport localeSupport, BandanaManager bandanaManager, BootstrapManager bootstrapManager, RosterFactory rosterFactory) {
        super(localeSupport, bandanaManager, bootstrapManager);
        this.rosterFactory = rosterFactory;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getName() {
        return this.getText("presencereporter." + this.getKey() + ".name");
    }

    @Override
    public String getServiceHomepage() {
        return this.getText("presencereporter." + this.getKey() + ".servicehomepage");
    }

    @Override
    public String getPresenceXHTML(String id, boolean outputId) throws IOException, PresenceException {
        XMPPTCPConnection xmppConnection = this.checkCreateConnection();
        if (xmppConnection.isAuthenticated()) {
            BareJid bjID;
            Roster roster = this.rosterFactory.createRoster((XMPPConnection)xmppConnection);
            if (!roster.contains(bjID = JidCreate.bareFrom((String)id))) {
                try {
                    roster.createEntry(bjID, id, null);
                    return RenderUtils.error((String)this.getText("presencereporter." + this.getKey() + ".message.waitinbuddyaccept", new Object[]{id}));
                }
                catch (InterruptedException | SmackException.NoResponseException | SmackException.NotConnectedException | SmackException.NotLoggedInException | XMPPException e) {
                    logger.error("Unable to add " + id + " to contact list of " + this.getId(), e);
                    throw new PresenceException(this.getText("presencereporter." + this.getKey() + ".error.addbuddy", new Object[]{id, this.getId()}), e);
                }
            }
            RosterEntry entry = roster.getEntry(bjID);
            if (entry.isSubscriptionPending()) {
                return this.getPresenceLink(id, "im_invisible", this.getText("presence.link.waitingunblock"), outputId);
            }
            Presence presence = this.getPresence(bjID, roster);
            if (null != presence) {
                Presence.Mode presenceMode = presence.getMode();
                if (null == presenceMode && presence.getType().equals((Object)Presence.Type.available)) {
                    presenceMode = Presence.Mode.available;
                }
                return this.getPresenceLink(id, this.getStatusImage(presenceMode), String.valueOf(presenceMode), outputId);
            }
            return this.getPresenceLink(id, "im_invisible", this.getText("presence.link.intermediate"), outputId);
        }
        if (xmppConnection.isConnected()) {
            xmppConnection.disconnect();
        }
        return RenderUtils.error((String)this.getText("presencereporter." + this.getKey() + ".error.login"));
    }

    protected XMPPTCPConnection checkCreateConnection() {
        if (null == this.xmppConnection || !this.xmppConnection.isConnected()) {
            if (null != this.xmppConnection) {
                this.xmppConnection.disconnect();
            }
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Creating a new XMPPConnection for: " + this.getKey());
                }
                String trimmedId = this.getTrimmedId(this.getId());
                String password = this.getPassword();
                int lastIndexOfAlias = this.getId().lastIndexOf("@");
                String userDomainName = -1 != lastIndexOfAlias && lastIndexOfAlias < this.getId().length() - 1 ? this.getId().substring(lastIndexOfAlias + 1) : this.getDomain();
                this.xmppConnection = (XMPPTCPConnection)this.createXmppConnection(userDomainName);
                try {
                    this.xmppConnection.connect();
                    this.xmppConnection.login((CharSequence)trimmedId, password);
                }
                catch (IOException | InterruptedException | SmackException e) {
                    logger.error("Unable to establish connection to " + this.getKey(), e);
                }
            }
            catch (XMPPException xmppe) {
                logger.error("Unable to establish connection to " + this.getKey(), (Throwable)xmppe);
            }
        }
        return this.xmppConnection;
    }

    XMPPConnection createXmppConnection(String usernameDomain) {
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setHost((CharSequence)this.getDomain());
        config.setPort(this.getPort().intValue());
        config.setHost((CharSequence)usernameDomain);
        return new XMPPTCPConnection(config.build());
    }

    private String getStatusImage(Presence.Mode mode) {
        String img = null != mode && STATUS_MAP.containsKey(mode) ? STATUS_MAP.get(mode) : "im_invisible";
        if (null == img) {
            logger.info("Unrecognised " + this.getKey() + " status: " + mode);
        }
        return img;
    }

    private String getTrimmedId(String id) {
        if (id.contains("@")) {
            id = id.substring(0, id.indexOf("@"));
        }
        return id;
    }

    private Presence getPresence(BareJid targetAddress, Roster roster) {
        if (logger.isDebugEnabled()) {
            logger.debug("Roster for " + this.getId() + " is: " + roster);
        }
        if (null != roster) {
            return roster.getPresence(targetAddress);
        }
        return null;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    protected String getPresenceURL(String id) {
        return "jabber:" + id;
    }

    public String getDomain() {
        return StringUtils.defaultIfEmpty((String)((String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, DOMAIN_PREFIX + this.getKey())), (String)DEFAULT_JABBER_DOMAIN);
    }

    public void setDomain(String domain) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, DOMAIN_PREFIX + this.getKey(), (Object)StringUtils.defaultString((String)StringUtils.trim((String)domain)));
    }

    public Integer getPort() {
        Object portObj = this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, PORT_PREFIX + this.getKey());
        return null == portObj ? 5222 : new Integer(portObj.toString());
    }

    public void setPort(Integer port) {
        if (null == port) {
            this.bandanaManager.removeValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, PORT_PREFIX + this.getKey());
        } else {
            this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, PORT_PREFIX + this.getKey(), (Object)port.toString());
        }
    }
}

