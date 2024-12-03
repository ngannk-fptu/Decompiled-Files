/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers;

import java.util.StringTokenizer;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class SimpleAuthorizationHandler
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$SimpleAuthorizationHandler == null ? (class$org$apache$axis$handlers$SimpleAuthorizationHandler = SimpleAuthorizationHandler.class$("org.apache.axis.handlers.SimpleAuthorizationHandler")) : class$org$apache$axis$handlers$SimpleAuthorizationHandler).getName());
    static /* synthetic */ Class class$org$apache$axis$handlers$SimpleAuthorizationHandler;

    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: SimpleAuthorizationHandler::invoke");
        }
        boolean allowByDefault = JavaUtils.isTrueExplicitly(this.getOption("allowByDefault"));
        AuthenticatedUser user = (AuthenticatedUser)msgContext.getProperty("authenticatedUser");
        if (user == null) {
            throw new AxisFault("Server.NoUser", Messages.getMessage("needUser00"), null, null);
        }
        String userID = user.getName();
        SOAPService serviceHandler = msgContext.getService();
        if (serviceHandler == null) {
            throw new AxisFault(Messages.getMessage("needService00"));
        }
        String serviceName = serviceHandler.getName();
        String allowedRoles = (String)serviceHandler.getOption("allowedRoles");
        if (allowedRoles == null) {
            if (allowByDefault) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)Messages.getMessage("noRoles00"));
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug((Object)Messages.getMessage("noRoles01"));
                }
                throw new AxisFault("Server.Unauthorized", Messages.getMessage("notAuth00", userID, serviceName), null, null);
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)"Exit: SimpleAuthorizationHandler::invoke");
            }
            return;
        }
        SecurityProvider provider = (SecurityProvider)msgContext.getProperty("securityProvider");
        if (provider == null) {
            throw new AxisFault(Messages.getMessage("noSecurity00"));
        }
        StringTokenizer st = new StringTokenizer(allowedRoles, ",");
        while (st.hasMoreTokens()) {
            String thisRole = st.nextToken();
            if (!provider.userMatches(user, thisRole)) continue;
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("auth01", userID, serviceName));
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)"Exit: SimpleAuthorizationHandler::invoke");
            }
            return;
        }
        throw new AxisFault("Server.Unauthorized", Messages.getMessage("cantAuth02", userID, serviceName), null, null);
    }

    public void onFault(MessageContext msgContext) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: SimpleAuthorizationHandler::onFault");
            log.debug((Object)"Exit: SimpleAuthorizationHandler::onFault");
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

