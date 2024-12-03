/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.security.servlet;

import java.security.Principal;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.security.servlet.ServletAuthenticatedUser;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class ServletSecurityProvider
implements SecurityProvider {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$security$servlet$ServletSecurityProvider == null ? (class$org$apache$axis$security$servlet$ServletSecurityProvider = ServletSecurityProvider.class$("org.apache.axis.security.servlet.ServletSecurityProvider")) : class$org$apache$axis$security$servlet$ServletSecurityProvider).getName());
    static HashMap users = null;
    static /* synthetic */ Class class$org$apache$axis$security$servlet$ServletSecurityProvider;

    public AuthenticatedUser authenticate(MessageContext msgContext) {
        HttpServletRequest req = (HttpServletRequest)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
        if (req == null) {
            return null;
        }
        log.debug((Object)Messages.getMessage("got00", "HttpServletRequest"));
        Principal principal = req.getUserPrincipal();
        if (principal == null) {
            log.debug((Object)Messages.getMessage("noPrincipal00"));
            return null;
        }
        log.debug((Object)Messages.getMessage("gotPrincipal00", principal.getName()));
        return new ServletAuthenticatedUser(req);
    }

    public boolean userMatches(AuthenticatedUser user, String principal) {
        if (user == null) {
            return principal == null;
        }
        if (user instanceof ServletAuthenticatedUser) {
            ServletAuthenticatedUser servletUser = (ServletAuthenticatedUser)user;
            return servletUser.getRequest().isUserInRole(principal);
        }
        return false;
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

