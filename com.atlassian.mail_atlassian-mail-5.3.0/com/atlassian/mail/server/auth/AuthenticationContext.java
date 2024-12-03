/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.MessagingException
 *  javax.mail.Service
 */
package com.atlassian.mail.server.auth;

import com.atlassian.mail.server.auth.Credentials;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Service;

public interface AuthenticationContext {
    public Credentials getCredentials();

    public boolean isAuthenticating();

    public Properties preparePropertiesForSession(Properties var1);

    public void connectService(Service var1) throws MessagingException;
}

