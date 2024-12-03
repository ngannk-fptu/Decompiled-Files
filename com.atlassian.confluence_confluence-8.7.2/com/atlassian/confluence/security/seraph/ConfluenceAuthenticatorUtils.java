/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security.seraph;

import com.atlassian.confluence.security.seraph.ConfluenceUserPrincipal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceAuthenticatorUtils {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceAuthenticatorUtils.class);

    private ConfluenceAuthenticatorUtils() {
    }

    public static boolean isPrincipalAlreadyInSessionContext(HttpServletRequest httpServletRequest, Principal principal) {
        Principal currentPrincipal = (Principal)httpServletRequest.getSession().getAttribute("seraph_defaultauthenticator_user");
        ConfluenceUserPrincipal confluenceUserPrincipal = ConfluenceAuthenticatorUtils.asConfluenceUserPrincipal(currentPrincipal);
        return confluenceUserPrincipal != null && confluenceUserPrincipal.equals(ConfluenceUserPrincipal.of(principal));
    }

    public static ConfluenceUser refreshPrincipalObtainedFromSession(UserAccessor userAccessor, Principal principal) {
        ConfluenceUserPrincipal confluenceUserPrincipal = ConfluenceAuthenticatorUtils.asConfluenceUserPrincipal(principal);
        if (confluenceUserPrincipal == null) {
            return null;
        }
        return userAccessor.getExistingUserByKey(confluenceUserPrincipal.getUserKey());
    }

    private static ConfluenceUserPrincipal asConfluenceUserPrincipal(Principal principal) {
        ConfluenceUserPrincipal result = null;
        if (principal instanceof ConfluenceUserPrincipal) {
            result = (ConfluenceUserPrincipal)principal;
        } else if (principal instanceof ConfluenceUser) {
            result = new ConfluenceUserPrincipal((ConfluenceUser)((Object)principal));
        } else if (principal != null) {
            log.warn("Principal (name={}, class={} is not an instance of ConfluenceUserPrincipal and cannot be used for authentication", (Object)principal.getName(), principal.getClass());
        }
        return result;
    }
}

