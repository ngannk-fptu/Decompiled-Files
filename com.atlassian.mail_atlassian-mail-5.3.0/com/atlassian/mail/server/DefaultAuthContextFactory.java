/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.mail.server;

import com.atlassian.mail.server.InternalAuthenticationContext;
import com.atlassian.mail.server.auth.AuthenticationContext;
import com.atlassian.mail.server.auth.AuthenticationContextFactory;
import com.atlassian.mail.server.auth.Credentials;
import com.atlassian.mail.server.auth.UserPasswordCredentials;
import java.util.Optional;
import java.util.Properties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DefaultAuthContextFactory
implements AuthenticationContextFactory {
    private static volatile DefaultAuthContextFactory instance;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static DefaultAuthContextFactory getInstance() {
        if (instance != null) return instance;
        Class<DefaultAuthContextFactory> clazz = DefaultAuthContextFactory.class;
        synchronized (DefaultAuthContextFactory.class) {
            if (instance != null) return instance;
            instance = new DefaultAuthContextFactory();
            // ** MonitorExit[var0] (shouldn't be in output)
            return instance;
        }
    }

    @Override
    public AuthenticationContext createAuthenticationContext(Credentials credentials) {
        if (credentials instanceof UserPasswordCredentials) {
            UserPasswordCredentials userPwdCredentials = (UserPasswordCredentials)credentials;
            return new InternalAuthenticationContext(new FactoryUserPasswordCredentials(userPwdCredentials.getUserName(), userPwdCredentials.getPassword(), userPwdCredentials.getProperties().orElse(null)));
        }
        return null;
    }

    AuthenticationContext createAuthenticationContext(InternalAuthenticationContext.MutableUserPasswordCredentials credentials) {
        return new InternalAuthenticationContext(credentials);
    }

    private static final class FactoryUserPasswordCredentials
    implements InternalAuthenticationContext.MutableUserPasswordCredentials {
        private String userName;
        private String password;
        private final Properties properties;

        public FactoryUserPasswordCredentials(String userName, String password, Properties properties) {
            this.userName = userName;
            this.password = password;
            this.properties = properties;
        }

        @Override
        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public void setUserName(String userName) {
            this.userName = userName;
        }

        @Override
        public String getUserName() {
            return this.userName;
        }

        @Override
        public String getPassword() {
            return this.password;
        }

        @Override
        public Optional<Properties> getProperties() {
            return Optional.ofNullable(this.properties);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof UserPasswordCredentials)) {
                return false;
            }
            UserPasswordCredentials credentials = (UserPasswordCredentials)o;
            return new EqualsBuilder().append((Object)this.getUserName(), (Object)credentials.getUserName()).append((Object)this.getPassword(), (Object)credentials.getPassword()).append(this.getProperties(), credentials.getProperties()).isEquals();
        }

        public int hashCode() {
            return new HashCodeBuilder().append((Object)this.getUserName()).append((Object)this.getPassword()).append(this.getProperties()).toHashCode();
        }
    }
}

