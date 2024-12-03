/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.FileTypeMap
 *  javax.mail.Address
 *  javax.mail.AuthenticationFailedException
 *  javax.mail.Message
 *  javax.mail.MessagingException
 *  javax.mail.NoSuchProviderException
 *  javax.mail.Session
 *  javax.mail.Transport
 *  javax.mail.internet.MimeMessage
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.mail.javamail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Properties;
import javax.activation.FileTypeMap;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import org.springframework.lang.Nullable;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.mail.javamail.SmartMimeMessage;
import org.springframework.util.Assert;

public class JavaMailSenderImpl
implements JavaMailSender {
    public static final String DEFAULT_PROTOCOL = "smtp";
    public static final int DEFAULT_PORT = -1;
    private static final String HEADER_MESSAGE_ID = "Message-ID";
    private Properties javaMailProperties = new Properties();
    @Nullable
    private Session session;
    @Nullable
    private String protocol;
    @Nullable
    private String host;
    private int port = -1;
    @Nullable
    private String username;
    @Nullable
    private String password;
    @Nullable
    private String defaultEncoding;
    @Nullable
    private FileTypeMap defaultFileTypeMap;

    public JavaMailSenderImpl() {
        ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();
        fileTypeMap.afterPropertiesSet();
        this.defaultFileTypeMap = fileTypeMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setJavaMailProperties(Properties javaMailProperties) {
        this.javaMailProperties = javaMailProperties;
        JavaMailSenderImpl javaMailSenderImpl = this;
        synchronized (javaMailSenderImpl) {
            this.session = null;
        }
    }

    public Properties getJavaMailProperties() {
        return this.javaMailProperties;
    }

    public synchronized void setSession(Session session) {
        Assert.notNull((Object)session, (String)"Session must not be null");
        this.session = session;
    }

    public synchronized Session getSession() {
        if (this.session == null) {
            this.session = Session.getInstance((Properties)this.javaMailProperties);
        }
        return this.session;
    }

    public void setProtocol(@Nullable String protocol) {
        this.protocol = protocol;
    }

    @Nullable
    public String getProtocol() {
        return this.protocol;
    }

    public void setHost(@Nullable String host) {
        this.host = host;
    }

    @Nullable
    public String getHost() {
        return this.host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    @Nullable
    public String getUsername() {
        return this.username;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    @Nullable
    public String getPassword() {
        return this.password;
    }

    public void setDefaultEncoding(@Nullable String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    @Nullable
    public String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    public void setDefaultFileTypeMap(@Nullable FileTypeMap defaultFileTypeMap) {
        this.defaultFileTypeMap = defaultFileTypeMap;
    }

    @Nullable
    public FileTypeMap getDefaultFileTypeMap() {
        return this.defaultFileTypeMap;
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        this.send(new SimpleMailMessage[]{simpleMessage});
    }

    @Override
    public void send(SimpleMailMessage ... simpleMessages) throws MailException {
        ArrayList<MimeMessage> mimeMessages = new ArrayList<MimeMessage>(simpleMessages.length);
        for (SimpleMailMessage simpleMessage : simpleMessages) {
            MimeMailMessage message = new MimeMailMessage(this.createMimeMessage());
            simpleMessage.copyTo(message);
            mimeMessages.add(message.getMimeMessage());
        }
        this.doSend(mimeMessages.toArray(new MimeMessage[0]), simpleMessages);
    }

    @Override
    public MimeMessage createMimeMessage() {
        return new SmartMimeMessage(this.getSession(), this.getDefaultEncoding(), this.getDefaultFileTypeMap());
    }

    @Override
    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
        try {
            return new MimeMessage(this.getSession(), contentStream);
        }
        catch (Exception ex) {
            throw new MailParseException("Could not parse raw MIME content", ex);
        }
    }

    @Override
    public void send(MimeMessage mimeMessage) throws MailException {
        this.send(new MimeMessage[]{mimeMessage});
    }

    @Override
    public void send(MimeMessage ... mimeMessages) throws MailException {
        this.doSend(mimeMessages, null);
    }

    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
        this.send(new MimeMessagePreparator[]{mimeMessagePreparator});
    }

    @Override
    public void send(MimeMessagePreparator ... mimeMessagePreparators) throws MailException {
        try {
            ArrayList<MimeMessage> mimeMessages = new ArrayList<MimeMessage>(mimeMessagePreparators.length);
            for (MimeMessagePreparator preparator : mimeMessagePreparators) {
                MimeMessage mimeMessage = this.createMimeMessage();
                preparator.prepare(mimeMessage);
                mimeMessages.add(mimeMessage);
            }
            this.send(mimeMessages.toArray(new MimeMessage[0]));
        }
        catch (MailException ex) {
            throw ex;
        }
        catch (MessagingException ex) {
            throw new MailParseException(ex);
        }
        catch (Exception ex) {
            throw new MailPreparationException(ex);
        }
    }

    public void testConnection() throws MessagingException {
        try (Transport transport = null;){
            transport = this.connectTransport();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doSend(MimeMessage[] mimeMessages, @Nullable Object[] originalMessages) throws MailException {
        LinkedHashMap<Object, Exception> failedMessages = new LinkedHashMap<Object, Exception>();
        Transport transport = null;
        try {
            for (int i = 0; i < mimeMessages.length; ++i) {
                Object original;
                if (transport == null || !transport.isConnected()) {
                    if (transport != null) {
                        try {
                            transport.close();
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        transport = null;
                    }
                    try {
                        transport = this.connectTransport();
                    }
                    catch (AuthenticationFailedException ex) {
                        throw new MailAuthenticationException(ex);
                    }
                    catch (Exception ex) {
                        for (int j = i; j < mimeMessages.length; ++j) {
                            original = originalMessages != null ? originalMessages[j] : mimeMessages[j];
                            failedMessages.put(original, ex);
                        }
                        throw new MailSendException("Mail server connection failed", ex, failedMessages);
                    }
                }
                MimeMessage mimeMessage = mimeMessages[i];
                try {
                    Address[] addresses;
                    if (mimeMessage.getSentDate() == null) {
                        mimeMessage.setSentDate(new Date());
                    }
                    String messageId = mimeMessage.getMessageID();
                    mimeMessage.saveChanges();
                    if (messageId != null) {
                        mimeMessage.setHeader(HEADER_MESSAGE_ID, messageId);
                    }
                    transport.sendMessage((Message)mimeMessage, (addresses = mimeMessage.getAllRecipients()) != null ? addresses : new Address[]{});
                    continue;
                }
                catch (Exception ex) {
                    original = originalMessages != null ? originalMessages[i] : mimeMessage;
                    failedMessages.put(original, ex);
                }
            }
        }
        finally {
            try {
                if (transport != null) {
                    transport.close();
                }
            }
            catch (Exception ex) {
                if (!failedMessages.isEmpty()) {
                    throw new MailSendException("Failed to close server connection after message failures", ex, failedMessages);
                }
                throw new MailSendException("Failed to close server connection after message sending", ex);
            }
        }
        if (!failedMessages.isEmpty()) {
            throw new MailSendException(failedMessages);
        }
    }

    protected Transport connectTransport() throws MessagingException {
        String username = this.getUsername();
        String password = this.getPassword();
        if ("".equals(username)) {
            username = null;
            if ("".equals(password)) {
                password = null;
            }
        }
        Transport transport = this.getTransport(this.getSession());
        transport.connect(this.getHost(), this.getPort(), username, password);
        return transport;
    }

    protected Transport getTransport(Session session) throws NoSuchProviderException {
        String protocol = this.getProtocol();
        if (protocol == null && (protocol = session.getProperty("mail.transport.protocol")) == null) {
            protocol = DEFAULT_PROTOCOL;
        }
        return session.getTransport(protocol);
    }
}

