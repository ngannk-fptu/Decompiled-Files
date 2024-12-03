/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpParser;
import org.apache.commons.httpclient.Wire;
import org.apache.commons.httpclient.WireLogOutputStream;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.httpclient.util.ExceptionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpConnection {
    private static final byte[] CRLF = new byte[]{13, 10};
    private static final Log LOG = LogFactory.getLog(HttpConnection.class);
    private String hostName = null;
    private int portNumber = -1;
    private String proxyHostName = null;
    private int proxyPortNumber = -1;
    private Socket socket = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private InputStream lastResponseInputStream = null;
    protected boolean isOpen = false;
    private Protocol protocolInUse;
    private HttpConnectionParams params = new HttpConnectionParams();
    private boolean locked = false;
    private boolean usingSecureSocket = false;
    private boolean tunnelEstablished = false;
    private HttpConnectionManager httpConnectionManager;
    private InetAddress localAddress;

    public HttpConnection(String host, int port) {
        this(null, -1, host, null, port, Protocol.getProtocol("http"));
    }

    public HttpConnection(String host, int port, Protocol protocol) {
        this(null, -1, host, null, port, protocol);
    }

    public HttpConnection(String host, String virtualHost, int port, Protocol protocol) {
        this(null, -1, host, virtualHost, port, protocol);
    }

    public HttpConnection(String proxyHost, int proxyPort, String host, int port) {
        this(proxyHost, proxyPort, host, null, port, Protocol.getProtocol("http"));
    }

    public HttpConnection(HostConfiguration hostConfiguration) {
        this(hostConfiguration.getProxyHost(), hostConfiguration.getProxyPort(), hostConfiguration.getHost(), hostConfiguration.getPort(), hostConfiguration.getProtocol());
        this.localAddress = hostConfiguration.getLocalAddress();
    }

    public HttpConnection(String proxyHost, int proxyPort, String host, String virtualHost, int port, Protocol protocol) {
        this(proxyHost, proxyPort, host, port, protocol);
    }

    public HttpConnection(String proxyHost, int proxyPort, String host, int port, Protocol protocol) {
        if (host == null) {
            throw new IllegalArgumentException("host parameter is null");
        }
        if (protocol == null) {
            throw new IllegalArgumentException("protocol is null");
        }
        this.proxyHostName = proxyHost;
        this.proxyPortNumber = proxyPort;
        this.hostName = host;
        this.portNumber = protocol.resolvePort(port);
        this.protocolInUse = protocol;
    }

    protected Socket getSocket() {
        return this.socket;
    }

    public String getHost() {
        return this.hostName;
    }

    public void setHost(String host) throws IllegalStateException {
        if (host == null) {
            throw new IllegalArgumentException("host parameter is null");
        }
        this.assertNotOpen();
        this.hostName = host;
    }

    public String getVirtualHost() {
        return this.hostName;
    }

    public void setVirtualHost(String host) throws IllegalStateException {
        this.assertNotOpen();
    }

    public int getPort() {
        if (this.portNumber < 0) {
            return this.isSecure() ? 443 : 80;
        }
        return this.portNumber;
    }

    public void setPort(int port) throws IllegalStateException {
        this.assertNotOpen();
        this.portNumber = port;
    }

    public String getProxyHost() {
        return this.proxyHostName;
    }

    public void setProxyHost(String host) throws IllegalStateException {
        this.assertNotOpen();
        this.proxyHostName = host;
    }

    public int getProxyPort() {
        return this.proxyPortNumber;
    }

    public void setProxyPort(int port) throws IllegalStateException {
        this.assertNotOpen();
        this.proxyPortNumber = port;
    }

    public boolean isSecure() {
        return this.protocolInUse.isSecure();
    }

    public Protocol getProtocol() {
        return this.protocolInUse;
    }

    public void setProtocol(Protocol protocol) {
        this.assertNotOpen();
        if (protocol == null) {
            throw new IllegalArgumentException("protocol is null");
        }
        this.protocolInUse = protocol;
    }

    public InetAddress getLocalAddress() {
        return this.localAddress;
    }

    public void setLocalAddress(InetAddress localAddress) {
        this.assertNotOpen();
        this.localAddress = localAddress;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public boolean closeIfStale() throws IOException {
        if (this.isOpen && this.isStale()) {
            LOG.debug((Object)"Connection is stale, closing...");
            this.close();
            return true;
        }
        return false;
    }

    public boolean isStaleCheckingEnabled() {
        return this.params.isStaleCheckingEnabled();
    }

    public void setStaleCheckingEnabled(boolean staleCheckEnabled) {
        this.params.setStaleCheckingEnabled(staleCheckEnabled);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean isStale() throws IOException {
        boolean isStale;
        block10: {
            isStale = true;
            if (this.isOpen) {
                isStale = false;
                try {
                    if (this.inputStream.available() > 0) break block10;
                    try {
                        this.socket.setSoTimeout(1);
                        this.inputStream.mark(1);
                        int byteRead = this.inputStream.read();
                        if (byteRead == -1) {
                            isStale = true;
                        } else {
                            this.inputStream.reset();
                        }
                    }
                    finally {
                        this.socket.setSoTimeout(this.params.getSoTimeout());
                    }
                }
                catch (InterruptedIOException e) {
                    if (!ExceptionUtil.isSocketTimeoutException(e)) {
                        throw e;
                    }
                }
                catch (IOException e) {
                    LOG.debug((Object)"An error occurred while reading from the socket, is appears to be stale", (Throwable)e);
                    isStale = true;
                }
            }
        }
        return isStale;
    }

    public boolean isProxied() {
        return null != this.proxyHostName && 0 < this.proxyPortNumber;
    }

    public void setLastResponseInputStream(InputStream inStream) {
        this.lastResponseInputStream = inStream;
    }

    public InputStream getLastResponseInputStream() {
        return this.lastResponseInputStream;
    }

    public HttpConnectionParams getParams() {
        return this.params;
    }

    public void setParams(HttpConnectionParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        this.params = params;
    }

    public void setSoTimeout(int timeout) throws SocketException, IllegalStateException {
        this.params.setSoTimeout(timeout);
        if (this.socket != null) {
            this.socket.setSoTimeout(timeout);
        }
    }

    public void setSocketTimeout(int timeout) throws SocketException, IllegalStateException {
        this.assertOpen();
        if (this.socket != null) {
            this.socket.setSoTimeout(timeout);
        }
    }

    public int getSoTimeout() throws SocketException {
        return this.params.getSoTimeout();
    }

    public void setConnectionTimeout(int timeout) {
        this.params.setConnectionTimeout(timeout);
    }

    public void open() throws IOException {
        LOG.trace((Object)"enter HttpConnection.open()");
        String host = this.proxyHostName == null ? this.hostName : this.proxyHostName;
        int port = this.proxyHostName == null ? this.portNumber : this.proxyPortNumber;
        this.assertNotOpen();
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Open connection to " + host + ":" + port));
        }
        try {
            int inbuffersize;
            int outbuffersize;
            int rcvBufSize;
            int sndBufSize;
            if (this.socket == null) {
                this.usingSecureSocket = this.isSecure() && !this.isProxied();
                ProtocolSocketFactory socketFactory = null;
                if (this.isSecure() && this.isProxied()) {
                    Protocol defaultprotocol = Protocol.getProtocol("http");
                    socketFactory = defaultprotocol.getSocketFactory();
                } else {
                    socketFactory = this.protocolInUse.getSocketFactory();
                }
                this.socket = socketFactory.createSocket(host, port, this.localAddress, 0, this.params);
            }
            this.socket.setTcpNoDelay(this.params.getTcpNoDelay());
            this.socket.setSoTimeout(this.params.getSoTimeout());
            int linger = this.params.getLinger();
            if (linger >= 0) {
                this.socket.setSoLinger(linger > 0, linger);
            }
            if ((sndBufSize = this.params.getSendBufferSize()) >= 0) {
                this.socket.setSendBufferSize(sndBufSize);
            }
            if ((rcvBufSize = this.params.getReceiveBufferSize()) >= 0) {
                this.socket.setReceiveBufferSize(rcvBufSize);
            }
            if ((outbuffersize = this.socket.getSendBufferSize()) > 2048 || outbuffersize <= 0) {
                outbuffersize = 2048;
            }
            if ((inbuffersize = this.socket.getReceiveBufferSize()) > 2048 || inbuffersize <= 0) {
                inbuffersize = 2048;
            }
            this.inputStream = new BufferedInputStream(this.socket.getInputStream(), inbuffersize);
            this.outputStream = new BufferedOutputStream(this.socket.getOutputStream(), outbuffersize);
            this.isOpen = true;
        }
        catch (IOException e) {
            this.closeSocketAndStreams();
            throw e;
        }
    }

    public void tunnelCreated() throws IllegalStateException, IOException {
        int inbuffersize;
        int outbuffersize;
        int rcvBufSize;
        LOG.trace((Object)"enter HttpConnection.tunnelCreated()");
        if (!this.isSecure() || !this.isProxied()) {
            throw new IllegalStateException("Connection must be secure and proxied to use this feature");
        }
        if (this.usingSecureSocket) {
            throw new IllegalStateException("Already using a secure socket");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Secure tunnel to " + this.hostName + ":" + this.portNumber));
        }
        SecureProtocolSocketFactory socketFactory = (SecureProtocolSocketFactory)this.protocolInUse.getSocketFactory();
        this.socket = socketFactory.createSocket(this.socket, this.hostName, this.portNumber, true);
        int sndBufSize = this.params.getSendBufferSize();
        if (sndBufSize >= 0) {
            this.socket.setSendBufferSize(sndBufSize);
        }
        if ((rcvBufSize = this.params.getReceiveBufferSize()) >= 0) {
            this.socket.setReceiveBufferSize(rcvBufSize);
        }
        if ((outbuffersize = this.socket.getSendBufferSize()) > 2048) {
            outbuffersize = 2048;
        }
        if ((inbuffersize = this.socket.getReceiveBufferSize()) > 2048) {
            inbuffersize = 2048;
        }
        this.inputStream = new BufferedInputStream(this.socket.getInputStream(), inbuffersize);
        this.outputStream = new BufferedOutputStream(this.socket.getOutputStream(), outbuffersize);
        this.usingSecureSocket = true;
        this.tunnelEstablished = true;
    }

    public boolean isTransparent() {
        return !this.isProxied() || this.tunnelEstablished;
    }

    public void flushRequestOutputStream() throws IOException {
        LOG.trace((Object)"enter HttpConnection.flushRequestOutputStream()");
        this.assertOpen();
        this.outputStream.flush();
    }

    public OutputStream getRequestOutputStream() throws IOException, IllegalStateException {
        LOG.trace((Object)"enter HttpConnection.getRequestOutputStream()");
        this.assertOpen();
        OutputStream out = this.outputStream;
        if (Wire.CONTENT_WIRE.enabled()) {
            out = new WireLogOutputStream(out, Wire.CONTENT_WIRE);
        }
        return out;
    }

    public InputStream getResponseInputStream() throws IOException, IllegalStateException {
        LOG.trace((Object)"enter HttpConnection.getResponseInputStream()");
        this.assertOpen();
        return this.inputStream;
    }

    public boolean isResponseAvailable() throws IOException {
        LOG.trace((Object)"enter HttpConnection.isResponseAvailable()");
        if (this.isOpen) {
            return this.inputStream.available() > 0;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isResponseAvailable(int timeout) throws IOException {
        LOG.trace((Object)"enter HttpConnection.isResponseAvailable(int)");
        if (!this.isOpen) {
            return false;
        }
        boolean result = false;
        if (this.inputStream.available() > 0) {
            result = true;
        } else {
            try {
                this.socket.setSoTimeout(timeout);
                this.inputStream.mark(1);
                int byteRead = this.inputStream.read();
                if (byteRead != -1) {
                    this.inputStream.reset();
                    LOG.debug((Object)"Input data available");
                    result = true;
                } else {
                    LOG.debug((Object)"Input data not available");
                }
            }
            catch (InterruptedIOException e) {
                if (!ExceptionUtil.isSocketTimeoutException(e)) {
                    throw e;
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug((Object)("Input data not available after " + timeout + " ms"));
                }
            }
            finally {
                try {
                    this.socket.setSoTimeout(this.params.getSoTimeout());
                }
                catch (IOException ioe) {
                    LOG.debug((Object)"An error ocurred while resetting soTimeout, we will assume that no response is available.", (Throwable)ioe);
                    result = false;
                }
            }
        }
        return result;
    }

    public void write(byte[] data) throws IOException, IllegalStateException {
        LOG.trace((Object)"enter HttpConnection.write(byte[])");
        this.write(data, 0, data.length);
    }

    public void write(byte[] data, int offset, int length) throws IOException, IllegalStateException {
        LOG.trace((Object)"enter HttpConnection.write(byte[], int, int)");
        if (offset < 0) {
            throw new IllegalArgumentException("Array offset may not be negative");
        }
        if (length < 0) {
            throw new IllegalArgumentException("Array length may not be negative");
        }
        if (offset + length > data.length) {
            throw new IllegalArgumentException("Given offset and length exceed the array length");
        }
        this.assertOpen();
        this.outputStream.write(data, offset, length);
    }

    public void writeLine(byte[] data) throws IOException, IllegalStateException {
        LOG.trace((Object)"enter HttpConnection.writeLine(byte[])");
        this.write(data);
        this.writeLine();
    }

    public void writeLine() throws IOException, IllegalStateException {
        LOG.trace((Object)"enter HttpConnection.writeLine()");
        this.write(CRLF);
    }

    public void print(String data) throws IOException, IllegalStateException {
        LOG.trace((Object)"enter HttpConnection.print(String)");
        this.write(EncodingUtil.getBytes(data, "ISO-8859-1"));
    }

    public void print(String data, String charset) throws IOException, IllegalStateException {
        LOG.trace((Object)"enter HttpConnection.print(String)");
        this.write(EncodingUtil.getBytes(data, charset));
    }

    public void printLine(String data) throws IOException, IllegalStateException {
        LOG.trace((Object)"enter HttpConnection.printLine(String)");
        this.writeLine(EncodingUtil.getBytes(data, "ISO-8859-1"));
    }

    public void printLine(String data, String charset) throws IOException, IllegalStateException {
        LOG.trace((Object)"enter HttpConnection.printLine(String)");
        this.writeLine(EncodingUtil.getBytes(data, charset));
    }

    public void printLine() throws IOException, IllegalStateException {
        LOG.trace((Object)"enter HttpConnection.printLine()");
        this.writeLine();
    }

    public String readLine() throws IOException, IllegalStateException {
        LOG.trace((Object)"enter HttpConnection.readLine()");
        this.assertOpen();
        return HttpParser.readLine(this.inputStream);
    }

    public String readLine(String charset) throws IOException, IllegalStateException {
        LOG.trace((Object)"enter HttpConnection.readLine()");
        this.assertOpen();
        return HttpParser.readLine(this.inputStream, charset);
    }

    public void shutdownOutput() {
        LOG.trace((Object)"enter HttpConnection.shutdownOutput()");
        try {
            Class[] paramsClasses = new Class[]{};
            Method shutdownOutput = this.socket.getClass().getMethod("shutdownOutput", paramsClasses);
            Object[] params = new Object[]{};
            shutdownOutput.invoke((Object)this.socket, params);
        }
        catch (Exception ex) {
            LOG.debug((Object)"Unexpected Exception caught", (Throwable)ex);
        }
    }

    public void close() {
        LOG.trace((Object)"enter HttpConnection.close()");
        this.closeSocketAndStreams();
    }

    public HttpConnectionManager getHttpConnectionManager() {
        return this.httpConnectionManager;
    }

    public void setHttpConnectionManager(HttpConnectionManager httpConnectionManager) {
        this.httpConnectionManager = httpConnectionManager;
    }

    public void releaseConnection() {
        LOG.trace((Object)"enter HttpConnection.releaseConnection()");
        if (this.locked) {
            LOG.debug((Object)"Connection is locked.  Call to releaseConnection() ignored.");
        } else if (this.httpConnectionManager != null) {
            LOG.debug((Object)"Releasing connection back to connection manager.");
            this.httpConnectionManager.releaseConnection(this);
        } else {
            LOG.warn((Object)"HttpConnectionManager is null.  Connection cannot be released.");
        }
    }

    protected boolean isLocked() {
        return this.locked;
    }

    protected void setLocked(boolean locked) {
        this.locked = locked;
    }

    protected void closeSocketAndStreams() {
        Closeable temp;
        LOG.trace((Object)"enter HttpConnection.closeSockedAndStreams()");
        this.isOpen = false;
        this.lastResponseInputStream = null;
        if (null != this.outputStream) {
            temp = this.outputStream;
            this.outputStream = null;
            try {
                ((OutputStream)temp).close();
            }
            catch (Exception ex) {
                LOG.debug((Object)"Exception caught when closing output", (Throwable)ex);
            }
        }
        if (null != this.inputStream) {
            temp = this.inputStream;
            this.inputStream = null;
            try {
                ((InputStream)temp).close();
            }
            catch (Exception ex) {
                LOG.debug((Object)"Exception caught when closing input", (Throwable)ex);
            }
        }
        if (null != this.socket) {
            temp = this.socket;
            this.socket = null;
            try {
                ((Socket)temp).close();
            }
            catch (Exception ex) {
                LOG.debug((Object)"Exception caught when closing socket", (Throwable)ex);
            }
        }
        this.tunnelEstablished = false;
        this.usingSecureSocket = false;
    }

    protected void assertNotOpen() throws IllegalStateException {
        if (this.isOpen) {
            throw new IllegalStateException("Connection is open");
        }
    }

    protected void assertOpen() throws IllegalStateException {
        if (!this.isOpen) {
            throw new IllegalStateException("Connection is not open");
        }
    }

    public int getSendBufferSize() throws SocketException {
        if (this.socket == null) {
            return -1;
        }
        return this.socket.getSendBufferSize();
    }

    public void setSendBufferSize(int sendBufferSize) throws SocketException {
        this.params.setSendBufferSize(sendBufferSize);
    }
}

