/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.MessagingException
 *  javax.mail.Service
 */
package com.atlassian.mail.server;

import com.atlassian.mail.server.auth.AuthenticationContext;
import com.atlassian.mail.server.auth.Credentials;
import com.atlassian.mail.server.auth.UserPasswordCredentials;
import java.io.Serializable;
import java.util.Objects;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Service;

final class InternalAuthenticationContext
implements AuthenticationContext,
Serializable {
    private MutableUserPasswordCredentials credentials;

    public InternalAuthenticationContext(MutableUserPasswordCredentials credentials) {
        Objects.requireNonNull(credentials, "Not null credentials required");
        this.credentials = credentials;
    }

    @Override
    public void connectService(Service service) throws MessagingException {
        service.connect(this.credentials.getUserName(), this.credentials.getPassword());
    }

    @Override
    public Credentials getCredentials() {
        return this.credentials;
    }

    @Override
    public boolean isAuthenticating() {
        return this.credentials.getUserName() != null;
    }

    MutableUserPasswordCredentials getUserPasswordCredentials() {
        return this.credentials;
    }

    @Override
    public Properties preparePropertiesForSession(Properties properties) {
        String protocol = properties.getProperty("mail.transport.protocol");
        properties.put("mail." + protocol + ".auth", Boolean.toString(this.isAuthenticating()));
        return properties;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        InternalAuthenticationContext that = (InternalAuthenticationContext)o;
        return this.credentials.equals(that.credentials);
    }

    public int hashCode() {
        return Objects.hash(this.credentials);
    }

    static interface MutableUserPasswordCredentials
    extends UserPasswordCredentials {
        public void setPassword(String var1);

        public void setUserName(String var1);
    }
}

