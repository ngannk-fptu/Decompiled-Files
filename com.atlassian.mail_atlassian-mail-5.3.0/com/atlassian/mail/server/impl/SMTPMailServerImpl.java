/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.cache.RemovalListener
 *  javax.annotation.Nonnull
 *  javax.mail.Authenticator
 *  javax.mail.Message
 *  javax.mail.MessagingException
 *  javax.mail.NoSuchProviderException
 *  javax.mail.PasswordAuthentication
 *  javax.mail.Service
 *  javax.mail.Session
 *  javax.mail.Transport
 *  javax.mail.URLName
 *  javax.mail.internet.MimeMessage
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.mail.server.impl;

import com.atlassian.mail.Email;
import com.atlassian.mail.MailConstants;
import com.atlassian.mail.MailException;
import com.atlassian.mail.MailProtocol;
import com.atlassian.mail.server.AbstractMailServer;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.mail.server.auth.AuthenticationContext;
import com.atlassian.mail.server.impl.ExtendedMimeMessage;
import com.atlassian.mail.server.impl.util.MessageCreator;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SMTPMailServerImpl
extends AbstractMailServer
implements SMTPMailServer {
    private static final long serialVersionUID = -1443984302060710065L;
    private static final String JNDI_JAVA_SCHEME = "java:";
    private static final Duration CONNECTION_TRANSPORT_VALIDITY_DURATION = Duration.of(10L, ChronoUnit.MINUTES);
    private boolean isSessionServer;
    private String defaultFrom;
    private String prefix;
    private String jndiLocation;
    private boolean removePrecedence;
    private boolean tlsHostnameCheckRequired;
    private transient Session session;
    private transient LoadingCache<Session, Transport> transportCache;

    public SMTPMailServerImpl() {
        super(10000L);
    }

    public SMTPMailServerImpl(Long id, String name, String description, String from, String prefix, boolean isSession, String location, String username, String password) {
        this(id, name, description, from, prefix, isSession, MailConstants.DEFAULT_SMTP_PROTOCOL, location, "25", false, username, password);
    }

    public SMTPMailServerImpl(Long id, String name, String description, String from, String prefix, boolean isSession, MailProtocol protocol, String location, String smtpPort, boolean tlsRequired, String username, String password) {
        this(id, name, description, from, prefix, isSession, false, protocol, location, smtpPort, tlsRequired, username, password, 10000L);
    }

    public SMTPMailServerImpl(Long id, String name, String description, String from, String prefix, boolean isSession, MailProtocol protocol, String location, String smtpPort, boolean tlsRequired, String username, String password, long timeout) {
        this(id, name, description, from, prefix, isSession, false, protocol, location, smtpPort, tlsRequired, username, password, timeout);
    }

    public SMTPMailServerImpl(Long id, String name, String description, String from, String prefix, boolean isSession, boolean removePrecedence, MailProtocol protocol, String location, String smtpPort, boolean tlsRequired, String username, String password, long timeout) {
        this(id, name, description, from, prefix, isSession, removePrecedence, protocol, location, smtpPort, tlsRequired, username, password, timeout, null, null);
    }

    public SMTPMailServerImpl(Long id, String name, String description, String from, String prefix, boolean isSession, MailProtocol protocol, String location, String smtpPort, boolean tlsRequired, String username, String password, long timeout, String socksHost, String socksPort) {
        this(id, name, description, from, prefix, isSession, false, protocol, location, smtpPort, tlsRequired, username, password, timeout, socksHost, socksPort);
    }

    public SMTPMailServerImpl(Long id, String name, String description, String from, String prefix, boolean isSession, boolean removePrecedence, MailProtocol protocol, String location, String smtpPort, boolean tlsRequired, String username, String password, long timeout, String socksHost, String socksPort) {
        super(id, name, description, protocol, location, smtpPort, username, password, timeout, socksHost, socksPort);
        this.setDefaultFrom(from);
        this.setPrefix(prefix);
        this.setSessionServer(isSession);
        this.setRemovePrecedence(removePrecedence);
        this.setTlsRequired(tlsRequired);
        if (isSession) {
            this.setJndiLocation(location);
            this.setHostname(null);
        }
        this.initTransportCache();
    }

    public SMTPMailServerImpl(Long id, String name, String description, String from, String prefix, boolean isSession, String location, AuthenticationContext authenticationContext) {
        this(id, name, description, from, prefix, isSession, MailConstants.DEFAULT_SMTP_PROTOCOL, location, "25", false, authenticationContext);
    }

    public SMTPMailServerImpl(Long id, String name, String description, String from, String prefix, boolean isSession, MailProtocol protocol, String location, String smtpPort, boolean tlsRequired, AuthenticationContext authenticationContext) {
        this(id, name, description, from, prefix, isSession, false, protocol, location, smtpPort, tlsRequired, authenticationContext, 10000L);
    }

    public SMTPMailServerImpl(Long id, String name, String description, String from, String prefix, boolean isSession, MailProtocol protocol, String location, String smtpPort, boolean tlsRequired, AuthenticationContext authenticationContext, long timeout) {
        this(id, name, description, from, prefix, isSession, false, protocol, location, smtpPort, tlsRequired, authenticationContext, timeout);
    }

    public SMTPMailServerImpl(Long id, String name, String description, String from, String prefix, boolean isSession, boolean removePrecedence, MailProtocol protocol, String location, String smtpPort, boolean tlsRequired, AuthenticationContext authenticationContext, long timeout) {
        this(id, name, description, from, prefix, isSession, removePrecedence, protocol, location, smtpPort, tlsRequired, authenticationContext, timeout, null, null);
    }

    public SMTPMailServerImpl(Long id, String name, String description, String from, String prefix, boolean isSession, MailProtocol protocol, String location, String smtpPort, boolean tlsRequired, AuthenticationContext authenticationContext, long timeout, String socksHost, String socksPort) {
        this(id, name, description, from, prefix, isSession, false, protocol, location, smtpPort, tlsRequired, authenticationContext, timeout, socksHost, socksPort);
    }

    public SMTPMailServerImpl(Long id, String name, String description, String from, String prefix, boolean isSession, boolean removePrecedence, MailProtocol protocol, String location, String smtpPort, boolean tlsRequired, AuthenticationContext authenticationContext, long timeout, String socksHost, String socksPort) {
        super(id, name, description, protocol, location, smtpPort, authenticationContext, timeout, socksHost, socksPort);
        this.setDefaultFrom(from);
        this.setPrefix(prefix);
        this.setSessionServer(isSession);
        this.setRemovePrecedence(removePrecedence);
        this.setTlsRequired(tlsRequired);
        if (isSession) {
            this.setJndiLocation(location);
            this.setHostname(null);
        }
        this.initTransportCache();
    }

    @Override
    public String getJndiLocation() {
        return this.jndiLocation;
    }

    @Override
    public void setJndiLocation(String jndiLocation) {
        if (StringUtils.isNotBlank((CharSequence)jndiLocation) && !StringUtils.startsWithIgnoreCase((CharSequence)jndiLocation, (CharSequence)JNDI_JAVA_SCHEME)) {
            throw new IllegalArgumentException("Only the java URL scheme(java:) is allowed for the jndiLocation");
        }
        this.jndiLocation = jndiLocation;
        this.propertyChanged();
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public Session getSession() throws NamingException, MailException {
        if (this.session != null) return this.session;
        if (this.isSessionServer()) {
            this.log.debug((Object)"Getting session from JNDI");
            Object jndiSession = this.getJndiSession();
            if (jndiSession instanceof Session) {
                this.session = (Session)jndiSession;
                return this.session;
            }
            this.log.error((Object)("Mail server at location [" + this.getJndiLocation() + "] is not of required type javax.mail.Session, or is in different classloader. It is of type '" + (jndiSession != null ? jndiSession.getClass().getName() : null) + "' in classloader '" + jndiSession.getClass().getClassLoader() + "' instead"));
            throw new IllegalArgumentException("Mail server at location [" + this.getJndiLocation() + "] is not of required type javax.mail.Session. ");
        }
        Properties props = this.loadSystemProperties(this.getProperties());
        if (this.isTlsRequired() && this.isTlsHostnameCheckRequired()) {
            props.put(this.getMailProtocol() == MailProtocol.SECURE_SMTP ? "mail.smtps.ssl.checkserveridentity" : "mail.smtp.ssl.checkserveridentity", "true");
        }
        Authenticator auth = this.isAuthenticating ? this.getAuthenticator() : null;
        this.session = this.getSessionFromServerManager(props, auth);
        if (auth == null) return this.session;
        this.session.setPasswordAuthentication(new URLName(this.getMailProtocol().getProtocol(), this.getHostname(), Integer.parseInt(this.getPort()), null, null, null), new PasswordAuthentication(this.getUsername(), this.getPassword()));
        return this.session;
    }

    protected Object getJndiSession() throws NamingException {
        InitialContext ctx = new InitialContext();
        return ctx.lookup(this.getJndiLocation());
    }

    @Override
    public void send(Email email) throws MailException {
        this.sendWithMessageId(email, null);
    }

    @Override
    public void sendWithMessageId(Email email, String messageId) throws MailException {
        try {
            Session thisSession = this.getSession();
            ExtendedMimeMessage message = new ExtendedMimeMessage(thisSession, messageId);
            MessageCreator messageCreator = new MessageCreator();
            messageCreator.updateMimeMessage(email, this.getDefaultFrom(), this.prefix, message);
            this.log.debug((Object)("Getting transport for protocol [" + this.getMailProtocol().getProtocol() + "]"));
            Transport transport = this.getTransport(thisSession);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Got transport: [" + transport + "]. Connecting"));
            }
            if (!transport.isConnected()) {
                this.getAuthenticationContext().connectService((Service)transport);
            }
            this.log.debug((Object)"Sending message");
            this.sendMimeMessage(message, transport);
            Object[] messageHeaders = message.getHeader("Message-Id");
            if (!ArrayUtils.isEmpty((Object[])messageHeaders)) {
                Object actualMessageId = messageHeaders[0];
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("Message was sent with Message-Id " + (String)actualMessageId));
                }
                email.setMessageId((String)actualMessageId);
            }
        }
        catch (MessagingException | NamingException e) {
            throw new MailException(e);
        }
        catch (UnsupportedEncodingException e) {
            this.log.error((Object)"Error setting the 'from' address with an email and the user's fullname", (Throwable)e);
        }
    }

    private Transport getTransport(Session thisSession) throws MailException {
        try {
            if (this.isTransportCachingEnabled()) {
                this.log.debug((Object)"Obtaining transport object with cached approach.");
                return (Transport)this.transportCache.get((Object)thisSession);
            }
            this.log.debug((Object)"Obtaining transport object directly (no caching).");
            return thisSession.getTransport(this.getMailProtocol().getProtocol());
        }
        catch (ExecutionException | NoSuchProviderException e) {
            throw new MailException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void sendMimeMessage(MimeMessage message, Transport transport) throws MessagingException {
        Thread currentThread = Thread.currentThread();
        ClassLoader originalClassLoader = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(SMTPMailServerImpl.class.getClassLoader());
            transport.sendMessage((Message)message, message.getAllRecipients());
        }
        finally {
            currentThread.setContextClassLoader(originalClassLoader);
        }
    }

    @Override
    public void quietSend(Email email) throws MailException {
        try {
            this.send(email);
        }
        catch (Exception e) {
            this.log.error((Object)("Error sending mail. to:" + email.getTo() + ", cc:" + email.getCc() + ", bcc:" + email.getBcc() + ", subject:" + email.getSubject() + ", body:" + email.getBody() + ", mimeType:" + email.getMimeType() + ", encoding:" + email.getEncoding() + ", multipart:" + email.getMultipart() + ", error:" + e), (Throwable)e);
        }
    }

    @Override
    public String getType() {
        return MailServerManager.SERVER_TYPES[1];
    }

    @Override
    public String getDefaultFrom() {
        return this.defaultFrom;
    }

    @Override
    public void setDefaultFrom(String defaultFrom) {
        this.defaultFrom = defaultFrom;
        this.propertyChanged();
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean isRemovePrecedence() {
        return this.removePrecedence;
    }

    @Override
    public void setRemovePrecedence(boolean precedence) {
        this.removePrecedence = precedence;
        this.propertyChanged();
    }

    @Override
    public boolean isSessionServer() {
        return this.isSessionServer;
    }

    @Override
    public void setSessionServer(boolean sessionServer) {
        this.isSessionServer = sessionServer;
        this.propertyChanged();
    }

    @Override
    public boolean isTlsHostnameCheckRequired() {
        return this.tlsHostnameCheckRequired;
    }

    @Override
    public void setTlsHostnameCheckRequired(boolean tlsHostnameCheckRequired) {
        this.tlsHostnameCheckRequired = tlsHostnameCheckRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SMTPMailServerImpl)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        SMTPMailServerImpl smtpMailServer = (SMTPMailServerImpl)o;
        if (this.isSessionServer != smtpMailServer.isSessionServer) {
            return false;
        }
        if (this.defaultFrom != null ? !this.defaultFrom.equals(smtpMailServer.defaultFrom) : smtpMailServer.defaultFrom != null) {
            return false;
        }
        if (this.prefix != null ? !this.prefix.equals(smtpMailServer.prefix) : smtpMailServer.prefix != null) {
            return false;
        }
        return this.removePrecedence == smtpMailServer.removePrecedence;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.isSessionServer ? 1 : 0);
        result = 29 * result + (this.defaultFrom != null ? this.defaultFrom.hashCode() : 0);
        result = 29 * result + (this.prefix != null ? this.prefix.hashCode() : 0);
        result = 29 * result + (this.removePrecedence ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder((Object)this).append("id", (Object)this.getId()).append("name", (Object)this.getName()).append("description", (Object)this.getDescription()).append("server name", (Object)this.getHostname()).append("username", (Object)this.getUsername()).append("password", (Object)this.getPassword()).append("isSessionServer", this.isSessionServer).append("defaultFrom", (Object)this.defaultFrom).append("prefix", (Object)this.prefix).append("smtpPort", (Object)this.getPort()).toString();
    }

    @Override
    protected void propertyChanged() {
        super.propertyChanged();
        this.session = null;
    }

    private void initTransportCache() {
        this.transportCache = CacheBuilder.newBuilder().expireAfterWrite(CONNECTION_TRANSPORT_VALIDITY_DURATION).removalListener(this.closeTransport()).build((CacheLoader)new CacheLoader<Session, Transport>(){

            public Transport load(@Nonnull Session session) throws NoSuchProviderException {
                SMTPMailServerImpl.this.log.debug((Object)"Obtaining a new connection (transport) to mail server.");
                return session.getTransport((String)Optional.ofNullable(SMTPMailServerImpl.this.getMailProtocol()).map(MailProtocol::getProtocol).orElse(null));
            }
        });
    }

    private RemovalListener<Object, Object> closeTransport() {
        return notification -> {
            try {
                Transport transport = (Transport)notification.getValue();
                transport.close();
                this.log.debug((Object)"Completed closing the connection (transport) to mail server.");
            }
            catch (RuntimeException | MessagingException ex) {
                this.log.warn((Object)("An error has occurred whilst closing the connection to the outgoing mail server: " + this.getName() + ". This could be caused by a time-out on closing this connection, increase the timeout in smtp server configuration. Alternatively, set the 'mail.smtp.quitwait' (or 'mail.smtps.quitwait' for ssl) system property to false."), ex);
            }
        };
    }

    private class MyAuthenticator
    extends Authenticator {
        private MyAuthenticator() {
        }

        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(SMTPMailServerImpl.this.getUsername(), SMTPMailServerImpl.this.getPassword());
        }
    }
}

