/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.HostNameOverrideX509TrustManager;
import com.microsoft.sqlserver.jdbc.PermissiveX509TrustManager;
import com.microsoft.sqlserver.jdbc.SQLServerCertificateUtils;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.microsoft.sqlserver.jdbc.SQLServerDriverBooleanProperty;
import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SSLProtocol;
import com.microsoft.sqlserver.jdbc.SecureStringUtil;
import com.microsoft.sqlserver.jdbc.ServerCertificateX509TrustManager;
import com.microsoft.sqlserver.jdbc.SocketFinder;
import com.microsoft.sqlserver.jdbc.StringUtils;
import com.microsoft.sqlserver.jdbc.TDSCommand;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSWriter;
import com.microsoft.sqlserver.jdbc.Util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

final class TDSChannel
implements Serializable {
    private static final long serialVersionUID = -866497813437384090L;
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.TDS.Channel");
    private final String traceID;
    private final SQLServerConnection con;
    private final transient TDSWriter tdsWriter;
    private transient Socket tcpSocket;
    private transient SSLSocket sslSocket;
    private transient Socket channelSocket;
    private transient ProxySocket proxySocket = null;
    private transient ProxyInputStream tcpInputStream;
    private transient OutputStream tcpOutputStream;
    private transient ProxyInputStream inputStream;
    private final transient Lock inputStreamLock = new ReentrantLock();
    private transient OutputStream outputStream;
    private final transient Lock outputStreamLock = new ReentrantLock();
    private static Logger packetLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.TDS.DATA");
    private final boolean isLoggingPackets = packetLogger.isLoggable(Level.FINEST);
    int numMsgsSent = 0;
    int numMsgsRcvd = 0;
    private final transient Lock lock = new ReentrantLock();
    private int spid = 0;
    private static final String SEPARATOR = System.getProperty("file.separator");
    private static final String JAVA_HOME = System.getProperty("java.home");
    private static final String JAVA_SECURITY = JAVA_HOME + SEPARATOR + "lib" + SEPARATOR + "security";
    private static final String JSSECACERTS = JAVA_SECURITY + SEPARATOR + "jssecacerts";
    private static final String CACERTS = JAVA_SECURITY + SEPARATOR + "cacerts";

    final Logger getLogger() {
        return logger;
    }

    public final String toString() {
        return this.traceID;
    }

    final TDSWriter getWriter() {
        return this.tdsWriter;
    }

    final TDSReader getReader(TDSCommand command) {
        return new TDSReader(this, this.con, command);
    }

    final boolean isLoggingPackets() {
        return this.isLoggingPackets;
    }

    void setSPID(int spid) {
        this.spid = spid;
    }

    int getSPID() {
        return this.spid;
    }

    void resetPooledConnection() {
        this.tdsWriter.resetPooledConnection();
    }

    TDSChannel(SQLServerConnection con) {
        this.con = con;
        this.traceID = "TDSChannel (" + con.toString() + ")";
        this.tcpSocket = null;
        this.sslSocket = null;
        this.channelSocket = null;
        this.tcpInputStream = null;
        this.tcpOutputStream = null;
        this.inputStream = null;
        this.outputStream = null;
        this.tdsWriter = new TDSWriter(this, con);
    }

    final InetSocketAddress open(String host, int port, int timeoutMillis, boolean useParallel, boolean useTnir, boolean isTnirFirstAttempt, int timeoutMillisForFullTimeout, String iPAddressPreference) throws SQLServerException {
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + ": Opening TCP socket...");
        }
        SocketFinder socketFinder = new SocketFinder(this.traceID, this.con);
        this.channelSocket = this.tcpSocket = socketFinder.findSocket(host, port, timeoutMillis, useParallel, useTnir, isTnirFirstAttempt, timeoutMillisForFullTimeout, iPAddressPreference);
        try {
            this.tcpSocket.setTcpNoDelay(true);
            this.tcpSocket.setKeepAlive(true);
            this.setSocketOptions(this.tcpSocket, this);
            int socketTimeout = this.con.getSocketTimeoutMilliseconds();
            this.tcpSocket.setSoTimeout(socketTimeout);
            this.inputStream = this.tcpInputStream = new ProxyInputStream(this.tcpSocket.getInputStream());
            this.outputStream = this.tcpOutputStream = this.tcpSocket.getOutputStream();
        }
        catch (IOException ex) {
            SQLServerException.convertConnectExceptionToSQLServerException(host, port, this.con, ex);
        }
        return (InetSocketAddress)this.channelSocket.getRemoteSocketAddress();
    }

    private void setSocketOptions(Socket tcpSocket, TDSChannel channel) {
        block4: {
            try {
                if (SQLServerDriver.socketSetOptionMethod != null && SQLServerDriver.socketKeepIdleOption != null && SQLServerDriver.socketKeepIntervalOption != null) {
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer(channel.toString() + ": Setting KeepAlive extended socket options.");
                    }
                    SQLServerDriver.socketSetOptionMethod.invoke((Object)tcpSocket, SQLServerDriver.socketKeepIdleOption, 30);
                    SQLServerDriver.socketSetOptionMethod.invoke((Object)tcpSocket, SQLServerDriver.socketKeepIntervalOption, 1);
                }
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                if (!logger.isLoggable(Level.FINER)) break block4;
                logger.finer(channel.toString() + ": KeepAlive extended socket options not supported on this platform. " + e.getMessage());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void disableSSL() {
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + " Disabling SSL...");
        }
        this.lock.lock();
        try {
            if (this.proxySocket == null) {
                if (logger.isLoggable(Level.INFO)) {
                    logger.finer(this.toString() + " proxySocket is null, exit early");
                }
                return;
            }
            ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);
            try {
                ((InputStream)is).close();
            }
            catch (IOException e) {
                logger.fine("Ignored error closing InputStream: " + e.getMessage());
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ((OutputStream)os).close();
            }
            catch (IOException e) {
                logger.fine("Ignored error closing OutputStream: " + e.getMessage());
            }
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this.toString() + " Rewiring proxy streams for SSL socket close");
            }
            this.proxySocket.setStreams(is, os);
            try {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + " Closing SSL socket");
                }
                this.sslSocket.close();
            }
            catch (IOException e) {
                logger.fine("Ignored error closing SSLSocket: " + e.getMessage());
            }
            this.proxySocket = null;
            this.inputStream = this.tcpInputStream;
            this.outputStream = this.tcpOutputStream;
            this.channelSocket = this.tcpSocket;
            this.sslSocket = null;
        }
        finally {
            this.lock.unlock();
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + " SSL disabled");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void enableSSL(String host, int port, String clientCertificate, String clientKey, String clientKeyPassword, boolean isTDS8) throws SQLServerException {
        Provider tmfProvider = null;
        Provider sslContextProvider = null;
        Provider ksProvider = null;
        String tmfDefaultAlgorithm = null;
        SSLHandhsakeState handshakeState = SSLHandhsakeState.SSL_HANDHSAKE_NOT_STARTED;
        boolean isFips = false;
        String trustStoreType = null;
        String sslProtocol = null;
        try {
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(this.toString() + " Enabling SSL...");
            }
            String trustStoreFileName = this.con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.TRUST_STORE.toString());
            String hostNameInCertificate = this.con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.toString());
            trustStoreType = this.con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.TRUST_STORE_TYPE.toString());
            if (StringUtils.isEmpty(trustStoreType)) {
                trustStoreType = SQLServerDriverStringProperty.TRUST_STORE_TYPE.getDefaultValue();
            }
            String serverCert = this.con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.SERVER_CERTIFICATE.toString());
            isFips = Boolean.parseBoolean(this.con.activeConnectionProperties.getProperty(SQLServerDriverBooleanProperty.FIPS.toString()));
            if (isFips) {
                this.validateFips(trustStoreType, trustStoreFileName);
            }
            sslProtocol = this.con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.SSL_PROTOCOL.toString());
            byte requestedEncryptLevel = this.con.getRequestedEncryptionLevel();
            assert (0 == requestedEncryptLevel || 1 == requestedEncryptLevel || 3 == requestedEncryptLevel || isTDS8 && 2 == requestedEncryptLevel);
            TrustManager[] tm = null;
            if (0 == this.con.getNegotiatedEncryptionLevel() || this.con.getTrustServerCertificate()) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + " SSL handshake will trust any certificate");
                }
                tm = new TrustManager[]{new PermissiveX509TrustManager(this)};
            } else if (this.con.getTrustManagerClass() != null) {
                Object[] msgArgs = new Object[]{"trustManagerClass", "javax.net.ssl.TrustManager"};
                tm = new TrustManager[]{(TrustManager)Util.newInstance(TrustManager.class, this.con.getTrustManagerClass(), this.con.getTrustManagerConstructorArg(), msgArgs)};
            } else if (isTDS8 && serverCert != null) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(this.toString() + " Verify server certificate for TDS 8");
                }
                tm = null != hostNameInCertificate ? new TrustManager[]{new ServerCertificateX509TrustManager(this, serverCert, hostNameInCertificate)} : new TrustManager[]{new ServerCertificateX509TrustManager(this, serverCert, host)};
            } else {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + " SSL handshake will validate server certificate");
                }
                KeyStore ks = null;
                if (null == trustStoreFileName && null == this.con.encryptedTrustStorePassword && !isTDS8) {
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer(this.toString() + " Using system default trust store and password");
                    }
                } else {
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.finest(this.toString() + " Finding key store interface");
                    }
                    ks = KeyStore.getInstance(trustStoreType);
                    ksProvider = ks.getProvider();
                    InputStream is = this.loadTrustStore(trustStoreFileName);
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.finest(this.toString() + " Loading key store");
                    }
                    char[] trustStorePassword = SecureStringUtil.getInstance().getDecryptedChars(this.con.encryptedTrustStorePassword);
                    try {
                        ks.load(is, null == trustStorePassword ? null : trustStorePassword);
                    }
                    finally {
                        block53: {
                            if (trustStorePassword != null) {
                                Arrays.fill(trustStorePassword, ' ');
                            }
                            if (null != is) {
                                try {
                                    is.close();
                                }
                                catch (IOException e) {
                                    if (!logger.isLoggable(Level.FINE)) break block53;
                                    logger.fine(this.toString() + " Ignoring error closing trust material InputStream...");
                                }
                            }
                        }
                    }
                }
                TrustManagerFactory tmf = null;
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(this.toString() + " Locating X.509 trust manager factory");
                }
                tmfDefaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                tmf = TrustManagerFactory.getInstance(tmfDefaultAlgorithm);
                tmfProvider = tmf.getProvider();
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(this.toString() + " Getting trust manager");
                }
                tmf.init(ks);
                tm = tmf.getTrustManagers();
                if (!isFips) {
                    tm = null != hostNameInCertificate ? new TrustManager[]{new HostNameOverrideX509TrustManager(this, (X509TrustManager)tm[0], hostNameInCertificate)} : new TrustManager[]{new HostNameOverrideX509TrustManager(this, (X509TrustManager)tm[0], host)};
                }
            }
            SSLContext sslContext = null;
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this.toString() + " Getting TLS or better SSL context");
            }
            KeyManager[] km = null != clientCertificate && !clientCertificate.isEmpty() ? SQLServerCertificateUtils.getKeyManagerFromFile(clientCertificate, clientKey, clientKeyPassword) : null;
            sslContext = SSLContext.getInstance(sslProtocol);
            sslContextProvider = sslContext.getProvider();
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this.toString() + " Initializing SSL context");
            }
            sslContext.init(km, tm, null);
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this.toString() + " Creating SSL socket");
            }
            this.proxySocket = new ProxySocket(this);
            if (isTDS8) {
                this.sslSocket = (SSLSocket)sslContext.getSocketFactory().createSocket(this.channelSocket, host, port, true);
                SSLParameters sslParam = this.sslSocket.getSSLParameters();
                sslParam.setApplicationProtocols(new String[]{"tds/8.0"});
                this.sslSocket.setSSLParameters(sslParam);
            } else {
                this.sslSocket = (SSLSocket)sslContext.getSocketFactory().createSocket(this.proxySocket, host, port, false);
            }
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(this.toString() + " Starting SSL handshake");
            }
            handshakeState = SSLHandhsakeState.SSL_HANDHSAKE_STARTED;
            this.sslSocket.startHandshake();
            if (isTDS8) {
                String negotiatedProtocol = this.sslSocket.getApplicationProtocol();
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(this.toString() + " Application Protocol negotiated: " + (negotiatedProtocol == null ? "null" : negotiatedProtocol));
                }
                if (null != negotiatedProtocol && !negotiatedProtocol.isEmpty() && negotiatedProtocol.compareToIgnoreCase("tds/8.0") != 0) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ALPNFailed"));
                    Object[] msgArgs = new Object[]{"tds/8.0", negotiatedProtocol};
                    this.con.terminate(5, form.format(msgArgs));
                }
            }
            handshakeState = SSLHandhsakeState.SSL_HANDHSAKE_COMPLETE;
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this.toString() + " Rewiring proxy streams after handshake");
            }
            this.proxySocket.setStreams(this.inputStream, this.outputStream);
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this.toString() + " Getting SSL InputStream");
            }
            this.inputStream = new ProxyInputStream(this.sslSocket.getInputStream());
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this.toString() + " Getting SSL OutputStream");
            }
            this.outputStream = this.sslSocket.getOutputStream();
            this.channelSocket = this.sslSocket;
            String tlsProtocol = this.sslSocket.getSession().getProtocol();
            if (SSLProtocol.TLS_V10.toString().equalsIgnoreCase(tlsProtocol) || SSLProtocol.TLS_V11.toString().equalsIgnoreCase(tlsProtocol)) {
                String warningMsg = tlsProtocol + " was negotiated. Please update server and client to use TLSv1.2 at minimum.";
                logger.warning(warningMsg);
                this.con.addWarning(warningMsg);
            }
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(this.toString() + " SSL enabled");
            }
        }
        catch (Exception e) {
            String localizedMessage;
            if (logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, e.getMessage(), e);
            }
            if (logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "java.security path: " + JAVA_SECURITY + "\nSecurity providers: " + Arrays.asList(Security.getProviders()) + "\n" + (String)(null != sslContextProvider ? "SSLContext provider info: " + sslContextProvider.getInfo() + "\nSSLContext provider services:\n" + sslContextProvider.getServices() + "\n" : "") + (String)(null != tmfProvider ? "TrustManagerFactory provider info: " + tmfProvider.getInfo() + "\n" : "") + (String)(null != tmfDefaultAlgorithm ? "TrustManagerFactory default algorithm: " + tmfDefaultAlgorithm + "\n" : "") + (String)(null != ksProvider ? "KeyStore provider info: " + ksProvider.getInfo() + "\n" : "") + "java.ext.dirs: " + System.getProperty("java.ext.dirs"));
            }
            String errMsg = (localizedMessage = e.getLocalizedMessage()) != null ? localizedMessage : e.getMessage();
            String causeErrMsg = null;
            Throwable cause = e.getCause();
            if (cause != null) {
                String causeLocalizedMessage = cause.getLocalizedMessage();
                causeErrMsg = causeLocalizedMessage != null ? causeLocalizedMessage : cause.getMessage();
            }
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_sslFailed"));
            Object[] msgArgs = new Object[]{this.con.getEncrypt().toLowerCase(), this.con.getTrustServerCertificate(), errMsg};
            if (errMsg != null && errMsg.contains(" ClientConnectionId:")) {
                errMsg = errMsg.substring(0, errMsg.indexOf(" ClientConnectionId:"));
            }
            if (causeErrMsg != null && causeErrMsg.contains(" ClientConnectionId:")) {
                causeErrMsg = causeErrMsg.substring(0, causeErrMsg.indexOf(" ClientConnectionId:"));
            }
            if (e instanceof IOException && SSLHandhsakeState.SSL_HANDHSAKE_STARTED == handshakeState && (SQLServerException.getErrString("R_truncatedServerResponse").equals(errMsg) || SQLServerException.getErrString("R_truncatedServerResponse").equals(causeErrMsg))) {
                this.con.terminate(7, form.format(msgArgs), e);
            }
            this.con.terminate(5, form.format(msgArgs), e);
        }
    }

    private void validateFips(String trustStoreType, String trustStoreFileName) throws SQLServerException {
        boolean isValid = false;
        String strError = SQLServerException.getErrString("R_invalidFipsConfig");
        boolean isEncryptOn = 1 == this.con.getRequestedEncryptionLevel();
        boolean isValidTrustStoreType = !StringUtils.isEmpty(trustStoreType);
        boolean isValidTrustStore = !StringUtils.isEmpty(trustStoreFileName);
        boolean isTrustServerCertificate = this.con.getTrustServerCertificate();
        if (isEncryptOn && !isTrustServerCertificate) {
            isValid = true;
            if (isValidTrustStore && !isValidTrustStoreType) {
                isValid = false;
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + "TrustStoreType is required alongside with TrustStore.");
                }
            }
        }
        if (!isValid) {
            throw new SQLServerException(strError, null, 0, null);
        }
    }

    final InputStream loadTrustStore(String trustStoreFileName) {
        FileInputStream is;
        block17: {
            block18: {
                is = null;
                if (null != trustStoreFileName) {
                    try {
                        if (logger.isLoggable(Level.FINEST)) {
                            logger.finest(this.toString() + " Opening specified trust store: " + trustStoreFileName);
                        }
                        is = new FileInputStream(trustStoreFileName);
                    }
                    catch (FileNotFoundException e) {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.fine(this.toString() + " Trust store not found: " + e.getMessage());
                        }
                        break block17;
                    }
                }
                trustStoreFileName = System.getProperty("javax.net.ssl.trustStore");
                if (null != trustStoreFileName) {
                    try {
                        if (logger.isLoggable(Level.FINEST)) {
                            logger.finest(this.toString() + " Opening default trust store (from javax.net.ssl.trustStore): " + trustStoreFileName);
                        }
                        is = new FileInputStream(trustStoreFileName);
                    }
                    catch (FileNotFoundException e) {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.fine(this.toString() + " Trust store not found: " + e.getMessage());
                        }
                        break block17;
                    }
                }
                try {
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.finest(this.toString() + " Opening default trust store: " + JSSECACERTS);
                    }
                    is = new FileInputStream(JSSECACERTS);
                }
                catch (FileNotFoundException e) {
                    if (!logger.isLoggable(Level.FINE)) break block18;
                    logger.fine(this.toString() + " Trust store not found: " + e.getMessage());
                }
            }
            if (null == is) {
                try {
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.finest(this.toString() + " Opening default trust store: " + CACERTS);
                    }
                    is = new FileInputStream(CACERTS);
                }
                catch (FileNotFoundException e) {
                    if (!logger.isLoggable(Level.FINE)) break block17;
                    logger.fine(this.toString() + " Trust store not found: " + e.getMessage());
                }
            }
        }
        return is;
    }

    /*
     * Exception decompiling
     */
    final Boolean networkSocketStillConnected() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final int read(byte[] data, int offset, int length) throws SQLServerException {
        this.inputStreamLock.lock();
        try {
            this.con.idleNetworkTracker.markNetworkActivity();
            int n = this.inputStream.read(data, offset, length);
            this.inputStreamLock.unlock();
            return n;
        }
        catch (Throwable throwable) {
            try {
                this.inputStreamLock.unlock();
                throw throwable;
            }
            catch (IOException e) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(this.toString() + " read failed:" + e.getMessage());
                }
                if (e instanceof SocketTimeoutException) {
                    this.con.terminate(8, e.getMessage(), e);
                } else {
                    this.con.terminate(3, e.getMessage(), e);
                }
                return 0;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void write(byte[] data, int offset, int length) throws SQLServerException {
        try {
            this.outputStreamLock.lock();
            try {
                this.con.idleNetworkTracker.markNetworkActivity();
                this.outputStream.write(data, offset, length);
            }
            finally {
                this.outputStreamLock.unlock();
            }
        }
        catch (IOException e) {
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(this.toString() + " write failed:" + e.getMessage());
            }
            this.con.terminate(3, e.getMessage(), e);
        }
    }

    final void flush() throws SQLServerException {
        try {
            this.con.idleNetworkTracker.markNetworkActivity();
            this.outputStream.flush();
        }
        catch (IOException e) {
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(this.toString() + " flush failed:" + e.getMessage());
            }
            this.con.terminate(3, e.getMessage(), e);
        }
    }

    final void close() {
        block15: {
            block14: {
                block13: {
                    if (null != this.sslSocket) {
                        this.disableSSL();
                    }
                    if (null != this.inputStream) {
                        if (logger.isLoggable(Level.FINEST)) {
                            logger.finest(this.toString() + ": Closing inputStream...");
                        }
                        try {
                            this.inputStream.close();
                        }
                        catch (IOException e) {
                            if (!logger.isLoggable(Level.FINE)) break block13;
                            logger.log(Level.FINE, this.toString() + ": Ignored error closing inputStream", e);
                        }
                    }
                }
                if (null != this.outputStream) {
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.finest(this.toString() + ": Closing outputStream...");
                    }
                    try {
                        this.outputStream.close();
                    }
                    catch (IOException e) {
                        if (!logger.isLoggable(Level.FINE)) break block14;
                        logger.log(Level.FINE, this.toString() + ": Ignored error closing outputStream", e);
                    }
                }
            }
            if (null != this.tcpSocket) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + ": Closing TCP socket...");
                }
                try {
                    this.tcpSocket.close();
                }
                catch (IOException e) {
                    if (!logger.isLoggable(Level.FINE)) break block15;
                    logger.log(Level.FINE, this.toString() + ": Ignored error closing socket", e);
                }
            }
        }
    }

    void logPacket(byte[] data, int nStartOffset, int nLength, String messageDetail) {
        assert (0 <= nLength && nLength <= data.length);
        assert (0 <= nStartOffset && nStartOffset <= data.length);
        char[] printableChars = new char[]{'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'};
        char[] lineTemplate = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'};
        char[] logLine = new char[lineTemplate.length];
        System.arraycopy(lineTemplate, 0, logLine, 0, lineTemplate.length);
        StringBuilder logMsg = new StringBuilder(messageDetail.length() + 4 * nLength + 4 * (1 + nLength / 16) + 80);
        logMsg.append(this.tcpSocket.getLocalAddress().toString()).append(":").append(this.tcpSocket.getLocalPort()).append(" SPID:").append(this.spid).append(" ").append(messageDetail).append("\r\n");
        int nBytesLogged = 0;
        while (true) {
            int nBytesThisLine;
            for (nBytesThisLine = 0; nBytesThisLine < 16 && nBytesLogged < nLength; ++nBytesThisLine, ++nBytesLogged) {
                int nUnsignedByteVal = (data[nStartOffset + nBytesLogged] + 256) % 256;
                logLine[3 * nBytesThisLine] = Util.HEXCHARS[nUnsignedByteVal / 16];
                logLine[3 * nBytesThisLine + 1] = Util.HEXCHARS[nUnsignedByteVal % 16];
                logLine[50 + nBytesThisLine] = printableChars[nUnsignedByteVal];
            }
            for (int nBytesJustified = nBytesThisLine; nBytesJustified < 16; ++nBytesJustified) {
                logLine[3 * nBytesJustified] = 32;
                logLine[3 * nBytesJustified + 1] = 32;
            }
            logMsg.append(logLine, 0, 50 + nBytesThisLine);
            if (nBytesLogged == nLength) break;
            logMsg.append("\r\n");
        }
        if (packetLogger.isLoggable(Level.FINEST)) {
            packetLogger.finest(logMsg.toString());
        }
    }

    final int getNetworkTimeout() throws IOException {
        return this.tcpSocket.getSoTimeout();
    }

    final void setNetworkTimeout(int timeout) throws IOException {
        this.tcpSocket.setSoTimeout(timeout);
    }

    static enum SSLHandhsakeState {
        SSL_HANDHSAKE_NOT_STARTED,
        SSL_HANDHSAKE_STARTED,
        SSL_HANDHSAKE_COMPLETE;

    }

    private class ProxySocket
    extends Socket {
        private final TDSChannel tdsChannel;
        private final Logger logger;
        private final String logContext;
        private final ProxyInputStream proxyInputStream;
        private final ProxyOutputStream proxyOutputStream;

        ProxySocket(TDSChannel tdsChannel) {
            this.tdsChannel = tdsChannel;
            this.logger = tdsChannel.getLogger();
            this.logContext = tdsChannel.toString() + " (ProxySocket):";
            SSLHandshakeOutputStream sslHandshakeOutputStream = new SSLHandshakeOutputStream(tdsChannel);
            SSLHandshakeInputStream sslHandshakeInputStream = new SSLHandshakeInputStream(tdsChannel, sslHandshakeOutputStream);
            this.proxyOutputStream = new ProxyOutputStream(sslHandshakeOutputStream);
            this.proxyInputStream = new ProxyInputStream(sslHandshakeInputStream);
        }

        void setStreams(InputStream is, OutputStream os) {
            this.proxyInputStream.setFilteredStream(is);
            this.proxyOutputStream.setFilteredStream(os);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " Getting input stream");
            }
            return this.proxyInputStream;
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " Getting output stream");
            }
            return this.proxyOutputStream;
        }

        @Override
        public InetAddress getInetAddress() {
            return this.tdsChannel.tcpSocket.getInetAddress();
        }

        @Override
        public boolean getKeepAlive() throws SocketException {
            return this.tdsChannel.tcpSocket.getKeepAlive();
        }

        @Override
        public InetAddress getLocalAddress() {
            return this.tdsChannel.tcpSocket.getLocalAddress();
        }

        @Override
        public int getLocalPort() {
            return this.tdsChannel.tcpSocket.getLocalPort();
        }

        @Override
        public SocketAddress getLocalSocketAddress() {
            return this.tdsChannel.tcpSocket.getLocalSocketAddress();
        }

        @Override
        public boolean getOOBInline() throws SocketException {
            return this.tdsChannel.tcpSocket.getOOBInline();
        }

        @Override
        public int getPort() {
            return this.tdsChannel.tcpSocket.getPort();
        }

        @Override
        public synchronized int getReceiveBufferSize() throws SocketException {
            return this.tdsChannel.tcpSocket.getReceiveBufferSize();
        }

        @Override
        public SocketAddress getRemoteSocketAddress() {
            return this.tdsChannel.tcpSocket.getRemoteSocketAddress();
        }

        @Override
        public boolean getReuseAddress() throws SocketException {
            return this.tdsChannel.tcpSocket.getReuseAddress();
        }

        @Override
        public synchronized int getSendBufferSize() throws SocketException {
            return this.tdsChannel.tcpSocket.getSendBufferSize();
        }

        @Override
        public int getSoLinger() throws SocketException {
            return this.tdsChannel.tcpSocket.getSoLinger();
        }

        @Override
        public synchronized int getSoTimeout() throws SocketException {
            return this.tdsChannel.tcpSocket.getSoTimeout();
        }

        @Override
        public boolean getTcpNoDelay() throws SocketException {
            return this.tdsChannel.tcpSocket.getTcpNoDelay();
        }

        @Override
        public int getTrafficClass() throws SocketException {
            return this.tdsChannel.tcpSocket.getTrafficClass();
        }

        @Override
        public boolean isBound() {
            return true;
        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public boolean isConnected() {
            return true;
        }

        @Override
        public boolean isInputShutdown() {
            return false;
        }

        @Override
        public boolean isOutputShutdown() {
            return false;
        }

        @Override
        public String toString() {
            return this.tdsChannel.tcpSocket.toString();
        }

        @Override
        public SocketChannel getChannel() {
            return null;
        }

        @Override
        public void bind(SocketAddress bindPoint) throws IOException {
            this.logger.finer(this.logContext + " Disallowed call to bind.  Throwing IOException.");
            throw new IOException();
        }

        @Override
        public void connect(SocketAddress endpoint) throws IOException {
            this.logger.finer(this.logContext + " Disallowed call to connect (without timeout).  Throwing IOException.");
            throw new IOException();
        }

        @Override
        public void connect(SocketAddress endpoint, int timeout) throws IOException {
            this.logger.finer(this.logContext + " Disallowed call to connect (with timeout).  Throwing IOException.");
            throw new IOException();
        }

        @Override
        public synchronized void close() throws IOException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.logContext + " Ignoring close");
            }
        }

        @Override
        public synchronized void setReceiveBufferSize(int size) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setReceiveBufferSize size:" + size);
            }
        }

        @Override
        public synchronized void setSendBufferSize(int size) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setSendBufferSize size:" + size);
            }
        }

        @Override
        public void setReuseAddress(boolean on) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setReuseAddress");
            }
        }

        @Override
        public void setSoLinger(boolean on, int linger) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setSoLinger");
            }
        }

        @Override
        public synchronized void setSoTimeout(int timeout) throws SocketException {
            this.tdsChannel.tcpSocket.setSoTimeout(timeout);
        }

        @Override
        public void setTcpNoDelay(boolean on) throws SocketException {
            this.tdsChannel.tcpSocket.setTcpNoDelay(on);
        }

        @Override
        public void setTrafficClass(int tc) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setTrafficClass");
            }
        }

        @Override
        public void shutdownInput() throws IOException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring shutdownInput");
            }
        }

        @Override
        public void shutdownOutput() throws IOException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring shutdownOutput");
            }
        }

        @Override
        public void sendUrgentData(int data) throws IOException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring sendUrgentData");
            }
        }

        @Override
        public void setKeepAlive(boolean on) throws SocketException {
            this.tdsChannel.tcpSocket.setKeepAlive(on);
        }

        @Override
        public void setOOBInline(boolean on) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setOOBInline");
            }
        }
    }

    final class ProxyOutputStream
    extends OutputStream {
        private OutputStream filteredStream;
        private final byte[] singleByte = new byte[1];

        ProxyOutputStream(OutputStream os) {
            this.filteredStream = os;
        }

        final void setFilteredStream(OutputStream os) {
            this.filteredStream = os;
        }

        @Override
        public void close() throws IOException {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(super.toString() + " Closing");
            }
            this.filteredStream.close();
        }

        @Override
        public void flush() throws IOException {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(super.toString() + " Flushing");
            }
            this.filteredStream.flush();
        }

        @Override
        public void write(int b) throws IOException {
            this.singleByte[0] = (byte)(b & 0xFF);
            this.writeInternal(this.singleByte, 0, this.singleByte.length);
        }

        @Override
        public void write(byte[] b) throws IOException {
            this.writeInternal(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this.writeInternal(b, off, len);
        }

        private void writeInternal(byte[] b, int off, int len) throws IOException {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(super.toString() + " Writing " + len + " bytes");
            }
            this.filteredStream.write(b, off, len);
        }
    }

    private final class ProxyInputStream
    extends InputStream {
        private InputStream filteredStream;
        private int[] cachedBytes = new int[10];
        private int cachedLength = 0;
        private final byte[] oneByte = new byte[1];

        ProxyInputStream(InputStream is) {
            this.filteredStream = is;
        }

        final void setFilteredStream(InputStream is) {
            this.filteredStream = is;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Loose catch block
         */
        public boolean poll() {
            TDSChannel.this.lock.lock();
            try {
                int b;
                try {
                    b = this.filteredStream.read();
                }
                catch (SocketTimeoutException e) {
                    boolean bl = true;
                    TDSChannel.this.lock.unlock();
                    return bl;
                }
                catch (IOException e) {
                    boolean bl = false;
                    TDSChannel.this.lock.unlock();
                    return bl;
                }
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(super.toString() + "poll() - read() returned " + b);
                }
                if (b == -1) {
                    boolean e = false;
                    return e;
                }
                if (this.cachedBytes.length <= this.cachedLength) {
                    int[] temp = new int[this.cachedBytes.length + 10];
                    for (int i = 0; i < this.cachedBytes.length; ++i) {
                        temp[i] = this.cachedBytes[i];
                    }
                    this.cachedBytes = temp;
                }
                this.cachedBytes[this.cachedLength] = b;
                ++this.cachedLength;
                boolean bl = true;
                return bl;
                {
                    catch (Throwable throwable) {
                        throw throwable;
                    }
                }
            }
            finally {
                TDSChannel.this.lock.unlock();
            }
        }

        private int getOneFromCache() {
            int result = this.cachedBytes[0];
            for (int i = 0; i < this.cachedLength; ++i) {
                this.cachedBytes[i] = this.cachedBytes[i + 1];
            }
            --this.cachedLength;
            return result;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long skip(long n) throws IOException {
            TDSChannel.this.lock.lock();
            try {
                long bytesSkipped = 0L;
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(super.toString() + " Skipping " + n + " bytes");
                }
                while (this.cachedLength > 0 && bytesSkipped < n) {
                    ++bytesSkipped;
                    this.getOneFromCache();
                }
                if (bytesSkipped < n) {
                    bytesSkipped += this.filteredStream.skip(n - bytesSkipped);
                }
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(super.toString() + " Skipped " + n + " bytes");
                }
                long l = bytesSkipped;
                return l;
            }
            finally {
                TDSChannel.this.lock.unlock();
            }
        }

        @Override
        public int available() throws IOException {
            int bytesAvailable = this.filteredStream.available() + this.cachedLength;
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(super.toString() + " " + bytesAvailable + " bytes available");
            }
            return bytesAvailable;
        }

        @Override
        public int read() throws IOException {
            int bytesRead;
            while (0 == (bytesRead = this.readInternal(this.oneByte, 0, this.oneByte.length))) {
            }
            assert (1 == bytesRead || -1 == bytesRead);
            return 1 == bytesRead ? this.oneByte[0] : -1;
        }

        @Override
        public int read(byte[] b) throws IOException {
            return this.readInternal(b, 0, b.length);
        }

        @Override
        public int read(byte[] b, int offset, int maxBytes) throws IOException {
            return this.readInternal(b, offset, maxBytes);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private int readInternal(byte[] b, int offset, int maxBytes) throws IOException {
            TDSChannel.this.lock.lock();
            try {
                int bytesRead;
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(super.toString() + " Reading " + maxBytes + " bytes");
                }
                if (this.cachedLength == 0) {
                    try {
                        bytesRead = this.filteredStream.read(b, offset, maxBytes);
                    }
                    catch (IOException e) {
                        if (logger.isLoggable(Level.FINER)) {
                            logger.finer(super.toString() + " Reading bytes threw exception:" + e.getMessage());
                        }
                        throw e;
                    }
                }
                int offsetBytesToSkipInCache = Math.min(offset, this.cachedLength);
                for (int i = 0; i < offsetBytesToSkipInCache; ++i) {
                    this.getOneFromCache();
                }
                byte[] bytesFromCache = new byte[Math.min(maxBytes, this.cachedLength)];
                for (int i = 0; i < bytesFromCache.length; ++i) {
                    bytesFromCache[i] = (byte)this.getOneFromCache();
                }
                try {
                    byte[] bytesFromStream = new byte[maxBytes - bytesFromCache.length];
                    int bytesReadFromStream = this.filteredStream.read(bytesFromStream, offset - offsetBytesToSkipInCache, maxBytes - bytesFromCache.length);
                    bytesRead = bytesFromCache.length + bytesReadFromStream;
                    System.arraycopy(bytesFromCache, 0, b, 0, bytesFromCache.length);
                    if (bytesReadFromStream >= 0) {
                        System.arraycopy(bytesFromStream, 0, b, bytesFromCache.length, bytesReadFromStream);
                    }
                }
                catch (IOException e) {
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer(super.toString() + " " + e.getMessage());
                    }
                    logger.finer(super.toString() + " Reading bytes threw exception:" + e.getMessage());
                    throw e;
                }
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(super.toString() + " Read " + bytesRead + " bytes");
                }
                int n = bytesRead;
                return n;
            }
            finally {
                TDSChannel.this.lock.unlock();
            }
        }

        @Override
        public boolean markSupported() {
            boolean markSupported = this.filteredStream.markSupported();
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(super.toString() + " Returning markSupported: " + markSupported);
            }
            return markSupported;
        }

        @Override
        public void mark(int readLimit) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(super.toString() + " Marking next " + readLimit + " bytes");
            }
            TDSChannel.this.lock.lock();
            try {
                this.filteredStream.mark(readLimit);
            }
            finally {
                TDSChannel.this.lock.unlock();
            }
        }

        @Override
        public void reset() throws IOException {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(super.toString() + " Resetting to previous mark");
            }
            TDSChannel.this.lock.lock();
            try {
                this.filteredStream.reset();
            }
            finally {
                TDSChannel.this.lock.unlock();
            }
        }

        @Override
        public void close() throws IOException {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(super.toString() + " Closing");
            }
            this.filteredStream.close();
        }
    }

    private class SSLHandshakeOutputStream
    extends OutputStream {
        private final TDSWriter tdsWriter;
        private boolean messageStarted;
        private final Logger logger;
        private final String logContext;
        private final byte[] singleByte = new byte[1];

        SSLHandshakeOutputStream(TDSChannel tdsChannel) {
            this.tdsWriter = tdsChannel.getWriter();
            this.messageStarted = false;
            this.logger = tdsChannel.getLogger();
            this.logContext = tdsChannel.toString() + " (SSLHandshakeOutputStream):";
        }

        @Override
        public void flush() throws IOException {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " Ignored a request to flush the stream");
            }
        }

        void endMessage() throws SQLServerException {
            assert (this.messageStarted);
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " Finishing TDS message");
            }
            this.tdsWriter.endMessage();
            this.messageStarted = false;
        }

        @Override
        public void write(int b) throws IOException {
            this.singleByte[0] = (byte)(b & 0xFF);
            this.writeInternal(this.singleByte, 0, this.singleByte.length);
        }

        @Override
        public void write(byte[] b) throws IOException {
            this.writeInternal(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this.writeInternal(b, off, len);
        }

        private void writeInternal(byte[] b, int off, int len) throws IOException {
            try {
                if (!this.messageStarted) {
                    if (this.logger.isLoggable(Level.FINEST)) {
                        this.logger.finest(this.logContext + " Starting new TDS packet...");
                    }
                    this.tdsWriter.startMessage(null, (byte)18);
                    this.messageStarted = true;
                }
                if (this.logger.isLoggable(Level.FINEST)) {
                    this.logger.finest(this.logContext + " Writing " + len + " bytes...");
                }
                this.tdsWriter.writeBytes(b, off, len);
            }
            catch (SQLServerException e) {
                this.logger.finer(this.logContext + " Writing bytes threw exception:" + e.getMessage());
                throw new IOException(e.getMessage());
            }
        }
    }

    private class SSLHandshakeInputStream
    extends InputStream {
        private final TDSReader tdsReader;
        private final SSLHandshakeOutputStream sslHandshakeOutputStream;
        private final Logger logger;
        private final String logContext;
        private final byte[] oneByte = new byte[1];

        SSLHandshakeInputStream(TDSChannel tdsChannel, SSLHandshakeOutputStream sslHandshakeOutputStream) {
            this.tdsReader = tdsChannel.getReader(null);
            this.sslHandshakeOutputStream = sslHandshakeOutputStream;
            this.logger = tdsChannel.getLogger();
            this.logContext = tdsChannel.toString() + " (SSLHandshakeInputStream):";
        }

        private void ensureSSLPayload() throws IOException {
            if (0 == this.tdsReader.available()) {
                if (this.logger.isLoggable(Level.FINEST)) {
                    this.logger.finest(this.logContext + " No handshake response bytes available. Flushing SSL handshake output stream.");
                }
                try {
                    this.sslHandshakeOutputStream.endMessage();
                }
                catch (SQLServerException e) {
                    this.logger.finer(this.logContext + " Ending TDS message threw exception:" + e.getMessage());
                    throw new IOException(e.getMessage());
                }
                if (this.logger.isLoggable(Level.FINEST)) {
                    this.logger.finest(this.logContext + " Reading first packet of SSL handshake response");
                }
                try {
                    this.tdsReader.readPacket();
                }
                catch (SQLServerException e) {
                    this.logger.finer(this.logContext + " Reading response packet threw exception:" + e.getMessage());
                    throw new IOException(e.getMessage());
                }
            }
        }

        @Override
        public long skip(long n) throws IOException {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " Skipping " + n + " bytes...");
            }
            if (n <= 0L) {
                return 0L;
            }
            if (n > Integer.MAX_VALUE) {
                n = Integer.MAX_VALUE;
            }
            this.ensureSSLPayload();
            try {
                this.tdsReader.skip((int)n);
            }
            catch (SQLServerException e) {
                this.logger.finer(this.logContext + " Skipping bytes threw exception:" + e.getMessage());
                throw new IOException(e.getMessage());
            }
            return n;
        }

        @Override
        public int read() throws IOException {
            int bytesRead;
            while (0 == (bytesRead = this.readInternal(this.oneByte, 0, this.oneByte.length))) {
            }
            assert (1 == bytesRead || -1 == bytesRead);
            return 1 == bytesRead ? this.oneByte[0] : -1;
        }

        @Override
        public int read(byte[] b) throws IOException {
            return this.readInternal(b, 0, b.length);
        }

        @Override
        public int read(byte[] b, int offset, int maxBytes) throws IOException {
            return this.readInternal(b, offset, maxBytes);
        }

        private int readInternal(byte[] b, int offset, int maxBytes) throws IOException {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " Reading " + maxBytes + " bytes...");
            }
            this.ensureSSLPayload();
            try {
                this.tdsReader.readBytes(b, offset, maxBytes);
            }
            catch (SQLServerException e) {
                this.logger.finer(this.logContext + " Reading bytes threw exception:" + e.getMessage());
                throw new IOException(e.getMessage());
            }
            return maxBytes;
        }
    }
}

