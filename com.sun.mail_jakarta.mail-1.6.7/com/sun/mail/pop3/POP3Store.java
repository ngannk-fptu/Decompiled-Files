/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.pop3;

import com.sun.mail.pop3.DefaultFolder;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.Protocol;
import com.sun.mail.util.MailConnectException;
import com.sun.mail.util.MailLogger;
import com.sun.mail.util.PropUtil;
import com.sun.mail.util.SocketConnectException;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

public class POP3Store
extends Store {
    private String name = "pop3";
    private int defaultPort = 110;
    private boolean isSSL = false;
    private Protocol port = null;
    private POP3Folder portOwner = null;
    private String host = null;
    private int portNum = -1;
    private String user = null;
    private String passwd = null;
    private boolean useStartTLS = false;
    private boolean requireStartTLS = false;
    private boolean usingSSL = false;
    private Map<String, String> capabilities;
    private MailLogger logger;
    volatile Constructor<?> messageConstructor = null;
    volatile boolean rsetBeforeQuit = false;
    volatile boolean disableTop = false;
    volatile boolean forgetTopHeaders = false;
    volatile boolean supportsUidl = true;
    volatile boolean cacheWriteTo = false;
    volatile boolean useFileCache = false;
    volatile File fileCacheDir = null;
    volatile boolean keepMessageContent = false;
    volatile boolean finalizeCleanClose = false;

    public POP3Store(Session session, URLName url) {
        this(session, url, "pop3", false);
    }

    public POP3Store(Session session, URLName url, String name, boolean isSSL) {
        super(session, url);
        if (url != null) {
            name = url.getProtocol();
        }
        this.name = name;
        this.logger = new MailLogger(this.getClass(), "DEBUG POP3", session.getDebug(), session.getDebugOut());
        if (!isSSL) {
            isSSL = PropUtil.getBooleanProperty(session.getProperties(), "mail." + name + ".ssl.enable", false);
        }
        this.defaultPort = isSSL ? 995 : 110;
        this.isSSL = isSSL;
        this.rsetBeforeQuit = this.getBoolProp("rsetbeforequit");
        this.disableTop = this.getBoolProp("disabletop");
        this.forgetTopHeaders = this.getBoolProp("forgettopheaders");
        this.cacheWriteTo = this.getBoolProp("cachewriteto");
        this.useFileCache = this.getBoolProp("filecache.enable");
        String dir = session.getProperty("mail." + name + ".filecache.dir");
        if (dir != null && this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config("mail." + name + ".filecache.dir: " + dir);
        }
        if (dir != null) {
            this.fileCacheDir = new File(dir);
        }
        this.keepMessageContent = this.getBoolProp("keepmessagecontent");
        this.useStartTLS = this.getBoolProp("starttls.enable");
        this.requireStartTLS = this.getBoolProp("starttls.required");
        this.finalizeCleanClose = this.getBoolProp("finalizecleanclose");
        String s = session.getProperty("mail." + name + ".message.class");
        if (s != null) {
            this.logger.log(Level.CONFIG, "message class: {0}", (Object)s);
            try {
                ClassLoader cl = this.getClass().getClassLoader();
                Class<?> messageClass = null;
                try {
                    messageClass = Class.forName(s, false, cl);
                }
                catch (ClassNotFoundException ex1) {
                    messageClass = Class.forName(s);
                }
                Class[] c = new Class[]{Folder.class, Integer.TYPE};
                this.messageConstructor = messageClass.getConstructor(c);
            }
            catch (Exception ex) {
                this.logger.log(Level.CONFIG, "failed to load message class", ex);
            }
        }
    }

    private final synchronized boolean getBoolProp(String prop) {
        prop = "mail." + this.name + "." + prop;
        boolean val = PropUtil.getBooleanProperty(this.session.getProperties(), prop, false);
        if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config(prop + ": " + val);
        }
        return val;
    }

    synchronized Session getSession() {
        return this.session;
    }

    @Override
    protected synchronized boolean protocolConnect(String host, int portNum, String user, String passwd) throws MessagingException {
        if (host == null || passwd == null || user == null) {
            return false;
        }
        if (portNum == -1) {
            portNum = PropUtil.getIntProperty(this.session.getProperties(), "mail." + this.name + ".port", -1);
        }
        if (portNum == -1) {
            portNum = this.defaultPort;
        }
        this.host = host;
        this.portNum = portNum;
        this.user = user;
        this.passwd = passwd;
        try {
            this.port = this.getPort(null);
        }
        catch (EOFException eex) {
            throw new AuthenticationFailedException(eex.getMessage());
        }
        catch (SocketConnectException scex) {
            throw new MailConnectException(scex);
        }
        catch (IOException ioex) {
            throw new MessagingException("Connect failed", ioex);
        }
        return true;
    }

    @Override
    public synchronized boolean isConnected() {
        if (!super.isConnected()) {
            return false;
        }
        try {
            if (this.port == null) {
                this.port = this.getPort(null);
            } else if (!this.port.noop()) {
                throw new IOException("NOOP failed");
            }
            return true;
        }
        catch (IOException ioex) {
            try {
                super.close();
            }
            catch (MessagingException messagingException) {
                // empty catch block
            }
            return false;
        }
    }

    synchronized Protocol getPort(POP3Folder owner) throws IOException {
        if (this.port != null && this.portOwner == null) {
            this.portOwner = owner;
            return this.port;
        }
        Protocol p = new Protocol(this.host, this.portNum, this.logger, this.session.getProperties(), "mail." + this.name, this.isSSL);
        if (this.useStartTLS || this.requireStartTLS) {
            if (p.hasCapability("STLS")) {
                if (p.stls()) {
                    p.setCapabilities(p.capa());
                } else if (this.requireStartTLS) {
                    this.logger.fine("STLS required but failed");
                    throw POP3Store.cleanupAndThrow(p, new EOFException("STLS required but failed"));
                }
            } else if (this.requireStartTLS) {
                this.logger.fine("STLS required but not supported");
                throw POP3Store.cleanupAndThrow(p, new EOFException("STLS required but not supported"));
            }
        }
        this.capabilities = p.getCapabilities();
        this.usingSSL = p.isSSL();
        if (!this.disableTop && this.capabilities != null && !this.capabilities.containsKey("TOP")) {
            this.disableTop = true;
            this.logger.fine("server doesn't support TOP, disabling it");
        }
        this.supportsUidl = this.capabilities == null || this.capabilities.containsKey("UIDL");
        try {
            if (!this.authenticate(p, this.user, this.passwd)) {
                throw POP3Store.cleanupAndThrow(p, new EOFException("login failed"));
            }
        }
        catch (EOFException ex) {
            throw POP3Store.cleanupAndThrow(p, ex);
        }
        catch (Exception ex) {
            throw POP3Store.cleanupAndThrow(p, new EOFException(ex.getMessage()));
        }
        if (this.port == null && owner != null) {
            this.port = p;
            this.portOwner = owner;
        }
        if (this.portOwner == null) {
            this.portOwner = owner;
        }
        return p;
    }

    private static IOException cleanupAndThrow(Protocol p, IOException ife) {
        try {
            p.quit();
        }
        catch (Throwable thr) {
            if (POP3Store.isRecoverable(thr)) {
                ife.addSuppressed(thr);
            }
            thr.addSuppressed(ife);
            if (thr instanceof Error) {
                throw (Error)thr;
            }
            if (thr instanceof RuntimeException) {
                throw (RuntimeException)thr;
            }
            throw new RuntimeException("unexpected exception", thr);
        }
        return ife;
    }

    private boolean authenticate(Protocol p, String user, String passwd) throws MessagingException {
        String authzid;
        String mechs = this.session.getProperty("mail." + this.name + ".auth.mechanisms");
        boolean usingDefaultMechs = false;
        if (mechs == null) {
            mechs = p.getDefaultMechanisms();
            usingDefaultMechs = true;
        }
        if ((authzid = this.session.getProperty("mail." + this.name + ".sasl.authorizationid")) == null) {
            authzid = user;
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Attempt to authenticate using mechanisms: " + mechs);
        }
        StringTokenizer st = new StringTokenizer(mechs);
        while (st.hasMoreTokens()) {
            String m = st.nextToken();
            if (!p.supportsMechanism(m = m.toUpperCase(Locale.ENGLISH))) {
                this.logger.log(Level.FINE, "no authenticator for mechanism {0}", (Object)m);
                continue;
            }
            if (!p.supportsAuthentication(m)) {
                this.logger.log(Level.FINE, "mechanism {0} not supported by server", (Object)m);
                continue;
            }
            if (usingDefaultMechs) {
                String dprop = "mail." + this.name + ".auth." + m.toLowerCase(Locale.ENGLISH) + ".disable";
                boolean disabled = PropUtil.getBooleanProperty(this.session.getProperties(), dprop, !p.isMechanismEnabled(m));
                if (disabled) {
                    if (!this.logger.isLoggable(Level.FINE)) continue;
                    this.logger.fine("mechanism " + m + " disabled by property: " + dprop);
                    continue;
                }
            }
            this.logger.log(Level.FINE, "Using mechanism {0}", (Object)m);
            String msg = p.authenticate(m, this.host, authzid, user, passwd);
            if (msg != null) {
                throw new AuthenticationFailedException(msg);
            }
            return true;
        }
        throw new AuthenticationFailedException("No authentication mechanisms supported by both server and client");
    }

    private static boolean isRecoverable(Throwable t) {
        return t instanceof Exception || t instanceof LinkageError;
    }

    synchronized void closePort(POP3Folder owner) {
        if (this.portOwner == owner) {
            this.port = null;
            this.portOwner = null;
        }
    }

    @Override
    public synchronized void close() throws MessagingException {
        this.close(false);
    }

    synchronized void close(boolean force) throws MessagingException {
        try {
            if (this.port != null) {
                if (force) {
                    this.port.close();
                } else {
                    this.port.quit();
                }
            }
        }
        catch (IOException iOException) {
        }
        finally {
            this.port = null;
            super.close();
        }
    }

    @Override
    public Folder getDefaultFolder() throws MessagingException {
        this.checkConnected();
        return new DefaultFolder(this);
    }

    @Override
    public Folder getFolder(String name) throws MessagingException {
        this.checkConnected();
        return new POP3Folder(this, name);
    }

    @Override
    public Folder getFolder(URLName url) throws MessagingException {
        this.checkConnected();
        return new POP3Folder(this, url.getFile());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, String> capabilities() throws MessagingException {
        Map<String, String> c;
        POP3Store pOP3Store = this;
        synchronized (pOP3Store) {
            c = this.capabilities;
        }
        if (c != null) {
            return Collections.unmodifiableMap(c);
        }
        return Collections.emptyMap();
    }

    public synchronized boolean isSSL() {
        return this.usingSSL;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.port != null) {
                this.close(!this.finalizeCleanClose);
            }
        }
        finally {
            super.finalize();
        }
    }

    private void checkConnected() throws MessagingException {
        if (!super.isConnected()) {
            throw new MessagingException("Not connected");
        }
    }
}

