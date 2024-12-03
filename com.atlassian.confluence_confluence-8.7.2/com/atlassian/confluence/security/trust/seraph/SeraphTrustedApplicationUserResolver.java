/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.ApplicationCertificate
 *  com.atlassian.security.auth.trustedapps.UserResolver
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security.trust.seraph;

import com.atlassian.security.auth.trustedapps.ApplicationCertificate;
import com.atlassian.security.auth.trustedapps.UserResolver;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeraphTrustedApplicationUserResolver
implements UserResolver {
    private UserManager userManager;
    private Logger log = LoggerFactory.getLogger(SeraphTrustedApplicationUserResolver.class);

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public Principal resolve(ApplicationCertificate applicationCertificate) {
        try {
            User user = this.userManager.getUser(applicationCertificate.getUserName());
            if (this.log.isDebugEnabled()) {
                this.log.debug("resolved user [ " + applicationCertificate.getUserName() + " ] to [ " + user + " ]");
            }
            return user;
        }
        catch (EntityException e) {
            this.log.error("Error looking up user from application certificate", (Throwable)e);
            return null;
        }
    }
}

