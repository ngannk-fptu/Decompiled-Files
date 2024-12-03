/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Authenticator
 *  javax.mail.MessagingException
 *  javax.mail.PasswordAuthentication
 *  javax.mail.Service
 *  javax.mail.Session
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.log4j.Logger
 */
package com.atlassian.mail.server;

import com.atlassian.mail.MailException;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.MailProtocol;
import com.atlassian.mail.server.DefaultAuthContextFactory;
import com.atlassian.mail.server.InternalAuthenticationContext;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.auth.AuthenticationContext;
import com.atlassian.mail.server.auth.AuthenticationContextAware;
import com.atlassian.mail.server.auth.Credentials;
import com.atlassian.mail.server.auth.UserPasswordCredentials;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Service;
import javax.mail.Session;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;

public abstract class AbstractMailServer
implements MailServer,
AuthenticationContextAware,
Serializable {
    private static final long serialVersionUID = 8520787806355214860L;
    protected transient Logger log = Logger.getLogger(this.getClass());
    private Long id;
    private String name;
    private String description;
    private String hostname;
    private String username = null;
    private String password = null;
    private MailProtocol mailProtocol = null;
    private String port = null;
    private long timeout;
    private long connectionTimeout;
    private boolean debug;
    private boolean tlsRequired;
    private transient PrintStream debugStream;
    private Properties props = new Properties();
    protected boolean isAuthenticating;
    private String socksHost;
    private String socksPort;
    private transient AuthenticationContext authenticationContext = null;

    public AbstractMailServer() {
        this.authenticationContext = DefaultAuthContextFactory.getInstance().createAuthenticationContext(new InternalMutableUserPasswordCredentials());
        this.setInitialProperties();
    }

    protected AbstractMailServer(long timeout) {
        this.authenticationContext = DefaultAuthContextFactory.getInstance().createAuthenticationContext(new InternalMutableUserPasswordCredentials());
        this.timeout = timeout;
        this.connectionTimeout = timeout;
        this.setInitialProperties();
    }

    public AbstractMailServer(Long id, String name, String description, MailProtocol protocol, String hostName, String port, String username, String password, long timeout, String socksHost, String socksPort) {
        InternalMutableUserPasswordCredentials mutableCredentials = new InternalMutableUserPasswordCredentials();
        mutableCredentials.setUserName(username);
        mutableCredentials.setPassword(password);
        this.authenticationContext = DefaultAuthContextFactory.getInstance().createAuthenticationContext(mutableCredentials);
        this.id = id;
        this.name = name;
        this.description = description;
        this.hostname = hostName;
        this.mailProtocol = protocol;
        this.port = port;
        this.timeout = timeout;
        this.connectionTimeout = timeout;
        this.socksHost = socksHost;
        this.socksPort = socksPort;
        this.setInitialProperties();
        this.props = this.loadSystemProperties(this.props);
    }

    public AbstractMailServer(Long id, String name, String description, MailProtocol protocol, String hostName, String port, AuthenticationContext authenticationContext, long timeout, String socksHost, String socksPort) {
        Objects.requireNonNull(authenticationContext, "not null authentication context required");
        this.authenticationContext = authenticationContext;
        this.id = id;
        this.name = name;
        this.description = description;
        this.hostname = hostName;
        this.mailProtocol = protocol;
        this.port = port;
        this.timeout = timeout;
        this.connectionTimeout = timeout;
        this.socksHost = socksHost;
        this.socksPort = socksPort;
        this.setInitialProperties();
        this.props = this.loadSystemProperties(this.props);
    }

    private void setInitialProperties() {
        MailProtocol mailProtocol = this.getMailProtocol();
        if (mailProtocol != null) {
            String protocol = mailProtocol.getProtocol();
            this.props.put("mail." + protocol + ".host", "" + this.getHostname());
            this.props.put("mail." + protocol + ".port", "" + this.getPort());
            this.props.put("mail." + protocol + ".timeout", "" + this.getTimeout());
            this.props.put("mail." + protocol + ".connectiontimeout", "" + this.getConnectionTimeout());
            this.props.put("mail.transport.protocol", "" + protocol);
            if (mailProtocol == MailProtocol.SMTP || mailProtocol == MailProtocol.SECURE_SMTP) {
                String mailSmtpQuitWaitPropertyName = "mail." + protocol + ".quitwait";
                this.props.put(mailSmtpQuitWaitPropertyName, Boolean.toString(Boolean.getBoolean(mailSmtpQuitWaitPropertyName)));
            }
            if (this.isTlsRequired()) {
                this.props.put("mail." + protocol + ".starttls.enable", "true");
            }
            this.isAuthenticating = this.getAuthenticationContext().isAuthenticating();
            if (StringUtils.isNotBlank((CharSequence)this.getSocksHost())) {
                this.props.put("mail." + protocol + ".socks.host", this.getSocksHost());
            }
            if (StringUtils.isNotBlank((CharSequence)this.getSocksPort())) {
                this.props.put("mail." + protocol + ".socks.port", this.getSocksPort());
            }
        }
        this.props.put("mail.debug", "" + this.getDebug());
        if (Boolean.getBoolean("mail.debug")) {
            this.props.put("mail.debug", "true");
        }
        this.props = this.getAuthenticationContext().preparePropertiesForSession(this.props);
    }

    protected Authenticator getAuthenticator() {
        Credentials credentials = this.getAuthenticationContext().getCredentials();
        if (credentials instanceof UserPasswordCredentials) {
            final UserPasswordCredentials userPwdCred = (UserPasswordCredentials)credentials;
            return new Authenticator(){

                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userPwdCred.getUserName(), userPwdCred.getPassword());
                }
            };
        }
        return null;
    }

    @Override
    public void smartConnect(Service service) throws MessagingException {
        this.getAuthenticationContext().connectService(service);
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.propertyChanged();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        this.propertyChanged();
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
        this.propertyChanged();
    }

    @Override
    public String getHostname() {
        return this.hostname;
    }

    @Override
    public void setHostname(String serverName) {
        this.hostname = serverName;
        this.propertyChanged();
    }

    @Override
    public AuthenticationContext getAuthenticationContext() {
        return this.authenticationContext;
    }

    @Override
    public void setAuthenticationContext(AuthenticationContext context) {
        this.authenticationContext = context;
    }

    @Override
    public String getUsername() {
        return this.getInternalContext().map(InternalAuthenticationContext::getUserPasswordCredentials).map(UserPasswordCredentials::getUserName).orElse(null);
    }

    @Override
    public void setUsername(String username) {
        this.getInternalContext().ifPresent(ctx -> {
            ctx.getUserPasswordCredentials().setUserName(username);
            this.propertyChanged();
        });
    }

    @Override
    public String getPassword() {
        return this.getInternalContext().map(InternalAuthenticationContext::getUserPasswordCredentials).map(UserPasswordCredentials::getPassword).orElse(null);
    }

    @Override
    public void setPassword(String password) {
        this.getInternalContext().ifPresent(ctx -> {
            ctx.getUserPasswordCredentials().setPassword(password);
            this.propertyChanged();
        });
    }

    private Optional<InternalAuthenticationContext> getInternalContext() {
        return Optional.ofNullable(this.getAuthenticationContext()).filter(c -> c instanceof InternalAuthenticationContext).map(c -> (InternalAuthenticationContext)c);
    }

    @Override
    public MailProtocol getMailProtocol() {
        return this.mailProtocol;
    }

    @Override
    public void setMailProtocol(MailProtocol protocol) {
        this.mailProtocol = protocol;
        this.propertyChanged();
    }

    @Override
    public String getPort() {
        return this.port;
    }

    @Override
    public void setPort(String port) {
        this.port = port;
        this.propertyChanged();
    }

    @Override
    public long getTimeout() {
        return this.timeout;
    }

    public long getConnectionTimeout() {
        return this.connectionTimeout;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
        this.propertyChanged();
    }

    public void setConnectionTimeout(long timeout) {
        this.connectionTimeout = timeout;
    }

    @Override
    public String getSocksHost() {
        return this.socksHost;
    }

    @Override
    public void setSocksHost(String socksHost) {
        this.socksHost = socksHost;
        this.propertyChanged();
    }

    @Override
    public String getSocksPort() {
        return this.socksPort;
    }

    @Override
    public void setSocksPort(String socksPort) {
        this.socksPort = socksPort;
        this.propertyChanged();
    }

    public boolean isTlsRequired() {
        return this.tlsRequired;
    }

    public void setTlsRequired(boolean tlsRequired) {
        this.tlsRequired = tlsRequired;
        this.propertyChanged();
    }

    @Override
    public Properties getProperties() {
        return this.props;
    }

    @Override
    public void setProperties(Properties props) {
        this.props = props;
        this.propertyChanged();
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
        this.propertyChanged();
    }

    @Override
    public void setDebugStream(PrintStream debugStream) {
        this.debugStream = debugStream;
        this.propertyChanged();
    }

    @Override
    public boolean getDebug() {
        return this.debug;
    }

    public PrintStream getDebugStream() {
        return this.debugStream;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractMailServer)) {
            return false;
        }
        AbstractMailServer abstractMailServer = (AbstractMailServer)o;
        return new EqualsBuilder().append((Object)this.id, (Object)abstractMailServer.id).append((Object)this.name, (Object)abstractMailServer.name).append((Object)this.description, (Object)abstractMailServer.description).append((Object)this.hostname, (Object)abstractMailServer.hostname).append((Object)this.getAuthenticationContext(), (Object)abstractMailServer.getAuthenticationContext()).append((Object)this.mailProtocol, (Object)abstractMailServer.mailProtocol).append((Object)this.port, (Object)abstractMailServer.port).append((Object)this.socksHost, (Object)abstractMailServer.socksHost).append((Object)this.socksPort, (Object)abstractMailServer.socksPort).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.id).append((Object)this.name).append((Object)this.description).append((Object)this.hostname).append((Object)this.getAuthenticationContext()).append((Object)this.mailProtocol).append((Object)this.port).append((Object)this.socksHost).append((Object)this.socksPort).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("id", (Object)this.id).append("name", (Object)this.name).append("description", (Object)this.description).append("server name", (Object)this.hostname).append("username", (Object)this.getUsername()).append("password", (Object)(this.getPassword() != null ? "***" : "<unset>")).append("authenticationContext", (Object)this.getAuthenticationContext()).append("socks host", (Object)this.socksHost).append("socks port", (Object)this.socksPort).toString();
    }

    protected void propertyChanged() {
        this.setInitialProperties();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.log = Logger.getLogger(this.getClass());
        this.authenticationContext = DefaultAuthContextFactory.getInstance().createAuthenticationContext(new InternalMutableUserPasswordCredentials());
    }

    protected synchronized Properties loadSystemProperties(Properties p) {
        Properties props = new Properties();
        props.putAll((Map<?, ?>)p);
        props.putAll((Map<?, ?>)System.getProperties());
        if (this.props != null) {
            props.putAll((Map<?, ?>)this.props);
        }
        return props;
    }

    @Override
    public void setLogger(Logger logger) {
        this.log = logger;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void getMoreDebugInfoAboutCreatedSession(Session session) {
        this.log.debug((Object)("Session providers: [" + Arrays.toString(session.getProviders()) + "]"));
        try {
            Field addressMapField = Session.class.getDeclaredField("addressMap");
            boolean originalAccessibility = addressMapField.isAccessible();
            addressMapField.setAccessible(true);
            try {
                this.log.debug((Object)("Session addressMap: [" + addressMapField.get(session) + "]"));
            }
            finally {
                addressMapField.setAccessible(originalAccessibility);
            }
        }
        catch (Exception e) {
            this.log.debug((Object)("Cannot retrieve Session details via reflections: " + e.getMessage()), (Throwable)e);
        }
    }

    protected Session getSessionFromServerManager(Properties props, Authenticator authenticator) throws MailException {
        this.log.debug((Object)"Getting session");
        if (this.getDebug()) {
            this.log.debug((Object)"Debug messages from JavaMail session initialization will not appear in this log. These messages are sent to standard out.");
        }
        props = this.getAuthenticationContext().preparePropertiesForSession(props);
        Session session = this.getSessionFromServerManagerInternal(props, authenticator);
        if (this.log.isDebugEnabled()) {
            this.getMoreDebugInfoAboutCreatedSession(session);
        }
        if (this.getDebugStream() != null) {
            try {
                session.setDebugOut(this.getDebugStream());
            }
            catch (NoSuchMethodError nsme) {
                this.log.error((Object)"Warning: An old (pre-1.3.2) version of the JavaMail library (javamail.jar or mail.jar) bundled with your app server, is in use. Some functions such as IMAPS/POPS/SMTPS will not work. Consider upgrading the app server's javamail jar to the version JIRA provides.");
            }
        }
        return session;
    }

    protected Session getSessionFromServerManagerInternal(Properties props, Authenticator authenticator) throws MailException {
        return MailFactory.getServerManager().getSession(props, authenticator);
    }

    private final class InternalMutableUserPasswordCredentials
    implements InternalAuthenticationContext.MutableUserPasswordCredentials {
        private InternalMutableUserPasswordCredentials() {
        }

        @Override
        public String getUserName() {
            return AbstractMailServer.this.username;
        }

        @Override
        public String getPassword() {
            return AbstractMailServer.this.password;
        }

        @Override
        public void setPassword(String password) {
            if (StringUtils.isNotBlank((CharSequence)password)) {
                AbstractMailServer.this.password = password;
            } else {
                AbstractMailServer.this.password = null;
            }
        }

        @Override
        public void setUserName(String userName) {
            if (StringUtils.isNotBlank((CharSequence)userName)) {
                AbstractMailServer.this.username = userName;
            } else {
                AbstractMailServer.this.username = null;
            }
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

        @Override
        public Optional<Properties> getProperties() {
            return Optional.ofNullable(null);
        }
    }
}

