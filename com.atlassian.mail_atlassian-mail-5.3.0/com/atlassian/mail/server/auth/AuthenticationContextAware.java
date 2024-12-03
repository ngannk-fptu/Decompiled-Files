/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.MessagingException
 *  javax.mail.Service
 */
package com.atlassian.mail.server.auth;

import com.atlassian.mail.server.auth.AuthenticationContext;
import javax.mail.MessagingException;
import javax.mail.Service;

public interface AuthenticationContextAware {
    public void setAuthenticationContext(AuthenticationContext var1);

    public AuthenticationContext getAuthenticationContext();

    public void smartConnect(Service var1) throws MessagingException;
}

