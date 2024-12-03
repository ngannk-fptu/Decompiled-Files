/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.security.simple.SimpleSecurityProvider;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class SimpleAuthenticationHandler
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$SimpleAuthenticationHandler == null ? (class$org$apache$axis$handlers$SimpleAuthenticationHandler = SimpleAuthenticationHandler.class$("org.apache.axis.handlers.SimpleAuthenticationHandler")) : class$org$apache$axis$handlers$SimpleAuthenticationHandler).getName());
    static /* synthetic */ Class class$org$apache$axis$handlers$SimpleAuthenticationHandler;

    public void invoke(MessageContext msgContext) throws AxisFault {
        SecurityProvider provider;
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: SimpleAuthenticationHandler::invoke");
        }
        if ((provider = (SecurityProvider)msgContext.getProperty("securityProvider")) == null) {
            provider = new SimpleSecurityProvider();
            msgContext.setProperty("securityProvider", provider);
        }
        if (provider != null) {
            AuthenticatedUser authUser;
            String userID = msgContext.getUsername();
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("user00", userID));
            }
            if (userID == null || userID.equals("")) {
                throw new AxisFault("Server.Unauthenticated", Messages.getMessage("cantAuth00", userID), null, null);
            }
            String passwd = msgContext.getPassword();
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("password00", passwd));
            }
            if ((authUser = provider.authenticate(msgContext)) == null) {
                throw new AxisFault("Server.Unauthenticated", Messages.getMessage("cantAuth01", userID), null, null);
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("auth00", userID));
            }
            msgContext.setProperty("authenticatedUser", authUser);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: SimpleAuthenticationHandler::invoke");
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

