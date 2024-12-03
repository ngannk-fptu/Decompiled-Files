/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.httpclient.ConnectionPoolTimeoutException;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.util.IdleConnectionHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MultiThreadedHttpConnectionManager
implements HttpConnectionManager {
    private static final Log LOG = LogFactory.getLog(MultiThreadedHttpConnectionManager.class);
    public static final int DEFAULT_MAX_HOST_CONNECTIONS = 2;
    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;
    private static final Map REFERENCE_TO_CONNECTION_SOURCE = new HashMap();
    private static final ReferenceQueue REFERENCE_QUEUE = new ReferenceQueue();
    private static ReferenceQueueThread REFERENCE_QUEUE_THREAD;
    private static WeakHashMap ALL_CONNECTION_MANAGERS;
    private HttpConnectionManagerParams params = new HttpConnectionManagerParams();
    private ConnectionPool connectionPool = new ConnectionPool();
    private volatile boolean shutdown = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void shutdownAll() {
        Map map = REFERENCE_TO_CONNECTION_SOURCE;
        synchronized (map) {
            WeakHashMap weakHashMap = ALL_CONNECTION_MANAGERS;
            synchronized (weakHashMap) {
                MultiThreadedHttpConnectionManager[] connManagers = ALL_CONNECTION_MANAGERS.keySet().toArray(new MultiThreadedHttpConnectionManager[ALL_CONNECTION_MANAGERS.size()]);
                for (int i = 0; i < connManagers.length; ++i) {
                    if (connManagers[i] == null) continue;
                    connManagers[i].shutdown();
                }
            }
            if (REFERENCE_QUEUE_THREAD != null) {
                REFERENCE_QUEUE_THREAD.shutdown();
                REFERENCE_QUEUE_THREAD = null;
            }
            REFERENCE_TO_CONNECTION_SOURCE.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void storeReferenceToConnection(HttpConnectionWithReference connection, HostConfiguration hostConfiguration, ConnectionPool connectionPool) {
        ConnectionSource source = new ConnectionSource();
        source.connectionPool = connectionPool;
        source.hostConfiguration = hostConfiguration;
        Map map = REFERENCE_TO_CONNECTION_SOURCE;
        synchronized (map) {
            if (REFERENCE_QUEUE_THREAD == null) {
                REFERENCE_QUEUE_THREAD = new ReferenceQueueThread();
                REFERENCE_QUEUE_THREAD.start();
            }
            REFERENCE_TO_CONNECTION_SOURCE.put(connection.reference, source);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void shutdownCheckedOutConnections(ConnectionPool connectionPool) {
        ArrayList<HttpConnection> connectionsToClose = new ArrayList<HttpConnection>();
        Map map = REFERENCE_TO_CONNECTION_SOURCE;
        synchronized (map) {
            Iterator referenceIter = REFERENCE_TO_CONNECTION_SOURCE.keySet().iterator();
            while (referenceIter.hasNext()) {
                Reference ref = (Reference)referenceIter.next();
                ConnectionSource source = (ConnectionSource)REFERENCE_TO_CONNECTION_SOURCE.get(ref);
                if (source.connectionPool != connectionPool) continue;
                referenceIter.remove();
                HttpConnection connection = (HttpConnection)ref.get();
                if (connection == null) continue;
                connectionsToClose.add(connection);
            }
        }
        for (HttpConnection connection : connectionsToClose) {
            connection.close();
            connection.setHttpConnectionManager(null);
            connection.releaseConnection();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void removeReferenceToConnection(HttpConnectionWithReference connection) {
        Map map = REFERENCE_TO_CONNECTION_SOURCE;
        synchronized (map) {
            REFERENCE_TO_CONNECTION_SOURCE.remove(connection.reference);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MultiThreadedHttpConnectionManager() {
        WeakHashMap weakHashMap = ALL_CONNECTION_MANAGERS;
        synchronized (weakHashMap) {
            ALL_CONNECTION_MANAGERS.put(this, null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void shutdown() {
        ConnectionPool connectionPool = this.connectionPool;
        synchronized (connectionPool) {
            if (!this.shutdown) {
                this.shutdown = true;
                this.connectionPool.shutdown();
            }
        }
    }

    public boolean isConnectionStaleCheckingEnabled() {
        return this.params.isStaleCheckingEnabled();
    }

    public void setConnectionStaleCheckingEnabled(boolean connectionStaleCheckingEnabled) {
        this.params.setStaleCheckingEnabled(connectionStaleCheckingEnabled);
    }

    public void setMaxConnectionsPerHost(int maxHostConnections) {
        this.params.setDefaultMaxConnectionsPerHost(maxHostConnections);
    }

    public int getMaxConnectionsPerHost() {
        return this.params.getDefaultMaxConnectionsPerHost();
    }

    public void setMaxTotalConnections(int maxTotalConnections) {
        this.params.setMaxTotalConnections(maxTotalConnections);
    }

    public int getMaxTotalConnections() {
        return this.params.getMaxTotalConnections();
    }

    @Override
    public HttpConnection getConnection(HostConfiguration hostConfiguration) {
        while (true) {
            try {
                return this.getConnectionWithTimeout(hostConfiguration, 0L);
            }
            catch (ConnectionPoolTimeoutException e) {
                LOG.debug((Object)"Unexpected exception while waiting for connection", (Throwable)e);
                continue;
            }
            break;
        }
    }

    @Override
    public HttpConnection getConnectionWithTimeout(HostConfiguration hostConfiguration, long timeout) throws ConnectionPoolTimeoutException {
        LOG.trace((Object)"enter HttpConnectionManager.getConnectionWithTimeout(HostConfiguration, long)");
        if (hostConfiguration == null) {
            throw new IllegalArgumentException("hostConfiguration is null");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("HttpConnectionManager.getConnection:  config = " + hostConfiguration + ", timeout = " + timeout));
        }
        HttpConnection conn = this.doGetConnection(hostConfiguration, timeout);
        return new HttpConnectionAdapter(conn);
    }

    @Override
    public HttpConnection getConnection(HostConfiguration hostConfiguration, long timeout) throws HttpException {
        LOG.trace((Object)"enter HttpConnectionManager.getConnection(HostConfiguration, long)");
        try {
            return this.getConnectionWithTimeout(hostConfiguration, timeout);
        }
        catch (ConnectionPoolTimeoutException e) {
            throw new HttpException(e.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private HttpConnection doGetConnection(HostConfiguration hostConfiguration, long timeout) throws ConnectionPoolTimeoutException {
        HttpConnection connection = null;
        int maxHostConnections = this.params.getMaxConnectionsPerHost(hostConfiguration);
        int maxTotalConnections = this.params.getMaxTotalConnections();
        ConnectionPool connectionPool = this.connectionPool;
        synchronized (connectionPool) {
            hostConfiguration = new HostConfiguration(hostConfiguration);
            HostConnectionPool hostPool = this.connectionPool.getHostPool(hostConfiguration, true);
            WaitingThread waitingThread = null;
            boolean useTimeout = timeout > 0L;
            long timeToWait = timeout;
            long startWait = 0L;
            long endWait = 0L;
            while (connection == null) {
                block20: {
                    if (this.shutdown) {
                        throw new IllegalStateException("Connection factory has been shutdown.");
                    }
                    if (hostPool.freeConnections.size() > 0) {
                        connection = this.connectionPool.getFreeConnection(hostConfiguration);
                        continue;
                    }
                    if (hostPool.numConnections < maxHostConnections && this.connectionPool.numConnections < maxTotalConnections) {
                        connection = this.connectionPool.createConnection(hostConfiguration);
                        continue;
                    }
                    if (hostPool.numConnections < maxHostConnections && this.connectionPool.freeConnections.size() > 0) {
                        this.connectionPool.deleteLeastUsedConnection();
                        connection = this.connectionPool.createConnection(hostConfiguration);
                        continue;
                    }
                    try {
                        if (useTimeout && timeToWait <= 0L) {
                            throw new ConnectionPoolTimeoutException("Timeout waiting for connection");
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug((Object)("Unable to get a connection, waiting..., hostConfig=" + hostConfiguration));
                        }
                        if (waitingThread == null) {
                            waitingThread = new WaitingThread();
                            waitingThread.hostConnectionPool = hostPool;
                            waitingThread.thread = Thread.currentThread();
                        } else {
                            waitingThread.interruptedByConnectionPool = false;
                        }
                        if (useTimeout) {
                            startWait = System.currentTimeMillis();
                        }
                        hostPool.waitingThreads.addLast(waitingThread);
                        this.connectionPool.waitingThreads.addLast(waitingThread);
                        this.connectionPool.wait(timeToWait);
                        if (waitingThread.interruptedByConnectionPool) break block20;
                        hostPool.waitingThreads.remove(waitingThread);
                    }
                    catch (InterruptedException e) {
                        block21: {
                            try {
                                if (!waitingThread.interruptedByConnectionPool) {
                                    LOG.debug((Object)"Interrupted while waiting for connection", (Throwable)e);
                                    throw new IllegalThreadStateException("Interrupted while waiting in MultiThreadedHttpConnectionManager");
                                }
                                if (waitingThread.interruptedByConnectionPool) break block21;
                                hostPool.waitingThreads.remove(waitingThread);
                            }
                            catch (Throwable throwable) {
                                if (!waitingThread.interruptedByConnectionPool) {
                                    hostPool.waitingThreads.remove(waitingThread);
                                    this.connectionPool.waitingThreads.remove(waitingThread);
                                }
                                if (useTimeout) {
                                    endWait = System.currentTimeMillis();
                                    timeToWait -= endWait - startWait;
                                }
                                throw throwable;
                            }
                            this.connectionPool.waitingThreads.remove(waitingThread);
                        }
                        if (!useTimeout) continue;
                        endWait = System.currentTimeMillis();
                        timeToWait -= endWait - startWait;
                        continue;
                    }
                    this.connectionPool.waitingThreads.remove(waitingThread);
                }
                if (!useTimeout) continue;
                endWait = System.currentTimeMillis();
                timeToWait -= endWait - startWait;
            }
        }
        return connection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getConnectionsInPool(HostConfiguration hostConfiguration) {
        ConnectionPool connectionPool = this.connectionPool;
        synchronized (connectionPool) {
            HostConnectionPool hostPool = this.connectionPool.getHostPool(hostConfiguration, false);
            return hostPool != null ? hostPool.numConnections : 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getConnectionsInPool() {
        ConnectionPool connectionPool = this.connectionPool;
        synchronized (connectionPool) {
            return this.connectionPool.numConnections;
        }
    }

    public int getConnectionsInUse(HostConfiguration hostConfiguration) {
        return this.getConnectionsInPool(hostConfiguration);
    }

    public int getConnectionsInUse() {
        return this.getConnectionsInPool();
    }

    public void deleteClosedConnections() {
        this.connectionPool.deleteClosedConnections();
    }

    @Override
    public void closeIdleConnections(long idleTimeout) {
        this.connectionPool.closeIdleConnections(idleTimeout);
        this.deleteClosedConnections();
    }

    @Override
    public void releaseConnection(HttpConnection conn) {
        LOG.trace((Object)"enter HttpConnectionManager.releaseConnection(HttpConnection)");
        if (conn instanceof HttpConnectionAdapter) {
            conn = ((HttpConnectionAdapter)conn).getWrappedConnection();
        }
        SimpleHttpConnectionManager.finishLastResponse(conn);
        this.connectionPool.freeConnection(conn);
    }

    private HostConfiguration configurationForConnection(HttpConnection conn) {
        HostConfiguration connectionConfiguration = new HostConfiguration();
        connectionConfiguration.setHost(conn.getHost(), conn.getPort(), conn.getProtocol());
        if (conn.getLocalAddress() != null) {
            connectionConfiguration.setLocalAddress(conn.getLocalAddress());
        }
        if (conn.getProxyHost() != null) {
            connectionConfiguration.setProxy(conn.getProxyHost(), conn.getProxyPort());
        }
        return connectionConfiguration;
    }

    @Override
    public HttpConnectionManagerParams getParams() {
        return this.params;
    }

    @Override
    public void setParams(HttpConnectionManagerParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        this.params = params;
    }

    static {
        ALL_CONNECTION_MANAGERS = new WeakHashMap();
    }

    private static class HttpConnectionAdapter
    extends HttpConnection {
        private HttpConnection wrappedConnection;

        public HttpConnectionAdapter(HttpConnection connection) {
            super(connection.getHost(), connection.getPort(), connection.getProtocol());
            this.wrappedConnection = connection;
        }

        protected boolean hasConnection() {
            return this.wrappedConnection != null;
        }

        HttpConnection getWrappedConnection() {
            return this.wrappedConnection;
        }

        @Override
        public void close() {
            if (this.hasConnection()) {
                this.wrappedConnection.close();
            }
        }

        @Override
        public InetAddress getLocalAddress() {
            if (this.hasConnection()) {
                return this.wrappedConnection.getLocalAddress();
            }
            return null;
        }

        @Override
        public boolean isStaleCheckingEnabled() {
            if (this.hasConnection()) {
                return this.wrappedConnection.isStaleCheckingEnabled();
            }
            return false;
        }

        @Override
        public void setLocalAddress(InetAddress localAddress) {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.setLocalAddress(localAddress);
        }

        @Override
        public void setStaleCheckingEnabled(boolean staleCheckEnabled) {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.setStaleCheckingEnabled(staleCheckEnabled);
        }

        @Override
        public String getHost() {
            if (this.hasConnection()) {
                return this.wrappedConnection.getHost();
            }
            return null;
        }

        @Override
        public HttpConnectionManager getHttpConnectionManager() {
            if (this.hasConnection()) {
                return this.wrappedConnection.getHttpConnectionManager();
            }
            return null;
        }

        @Override
        public InputStream getLastResponseInputStream() {
            if (this.hasConnection()) {
                return this.wrappedConnection.getLastResponseInputStream();
            }
            return null;
        }

        @Override
        public int getPort() {
            if (this.hasConnection()) {
                return this.wrappedConnection.getPort();
            }
            return -1;
        }

        @Override
        public Protocol getProtocol() {
            if (this.hasConnection()) {
                return this.wrappedConnection.getProtocol();
            }
            return null;
        }

        @Override
        public String getProxyHost() {
            if (this.hasConnection()) {
                return this.wrappedConnection.getProxyHost();
            }
            return null;
        }

        @Override
        public int getProxyPort() {
            if (this.hasConnection()) {
                return this.wrappedConnection.getProxyPort();
            }
            return -1;
        }

        @Override
        public OutputStream getRequestOutputStream() throws IOException, IllegalStateException {
            if (this.hasConnection()) {
                return this.wrappedConnection.getRequestOutputStream();
            }
            return null;
        }

        @Override
        public InputStream getResponseInputStream() throws IOException, IllegalStateException {
            if (this.hasConnection()) {
                return this.wrappedConnection.getResponseInputStream();
            }
            return null;
        }

        @Override
        public boolean isOpen() {
            if (this.hasConnection()) {
                return this.wrappedConnection.isOpen();
            }
            return false;
        }

        @Override
        public boolean closeIfStale() throws IOException {
            if (this.hasConnection()) {
                return this.wrappedConnection.closeIfStale();
            }
            return false;
        }

        @Override
        public boolean isProxied() {
            if (this.hasConnection()) {
                return this.wrappedConnection.isProxied();
            }
            return false;
        }

        @Override
        public boolean isResponseAvailable() throws IOException {
            if (this.hasConnection()) {
                return this.wrappedConnection.isResponseAvailable();
            }
            return false;
        }

        @Override
        public boolean isResponseAvailable(int timeout) throws IOException {
            if (this.hasConnection()) {
                return this.wrappedConnection.isResponseAvailable(timeout);
            }
            return false;
        }

        @Override
        public boolean isSecure() {
            if (this.hasConnection()) {
                return this.wrappedConnection.isSecure();
            }
            return false;
        }

        @Override
        public boolean isTransparent() {
            if (this.hasConnection()) {
                return this.wrappedConnection.isTransparent();
            }
            return false;
        }

        @Override
        public void open() throws IOException {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.open();
        }

        @Override
        public void print(String data) throws IOException, IllegalStateException {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.print(data);
        }

        @Override
        public void printLine() throws IOException, IllegalStateException {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.printLine();
        }

        @Override
        public void printLine(String data) throws IOException, IllegalStateException {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.printLine(data);
        }

        @Override
        public String readLine() throws IOException, IllegalStateException {
            if (this.hasConnection()) {
                return this.wrappedConnection.readLine();
            }
            throw new IllegalStateException("Connection has been released");
        }

        @Override
        public String readLine(String charset) throws IOException, IllegalStateException {
            if (this.hasConnection()) {
                return this.wrappedConnection.readLine(charset);
            }
            throw new IllegalStateException("Connection has been released");
        }

        @Override
        public void releaseConnection() {
            if (!this.isLocked() && this.hasConnection()) {
                HttpConnection wrappedConnection = this.wrappedConnection;
                this.wrappedConnection = null;
                wrappedConnection.releaseConnection();
            }
        }

        @Override
        public void setConnectionTimeout(int timeout) {
            if (this.hasConnection()) {
                this.wrappedConnection.setConnectionTimeout(timeout);
            }
        }

        @Override
        public void setHost(String host) throws IllegalStateException {
            if (this.hasConnection()) {
                this.wrappedConnection.setHost(host);
            }
        }

        @Override
        public void setHttpConnectionManager(HttpConnectionManager httpConnectionManager) {
            if (this.hasConnection()) {
                this.wrappedConnection.setHttpConnectionManager(httpConnectionManager);
            }
        }

        @Override
        public void setLastResponseInputStream(InputStream inStream) {
            if (this.hasConnection()) {
                this.wrappedConnection.setLastResponseInputStream(inStream);
            }
        }

        @Override
        public void setPort(int port) throws IllegalStateException {
            if (this.hasConnection()) {
                this.wrappedConnection.setPort(port);
            }
        }

        @Override
        public void setProtocol(Protocol protocol) {
            if (this.hasConnection()) {
                this.wrappedConnection.setProtocol(protocol);
            }
        }

        @Override
        public void setProxyHost(String host) throws IllegalStateException {
            if (this.hasConnection()) {
                this.wrappedConnection.setProxyHost(host);
            }
        }

        @Override
        public void setProxyPort(int port) throws IllegalStateException {
            if (this.hasConnection()) {
                this.wrappedConnection.setProxyPort(port);
            }
        }

        @Override
        public void setSoTimeout(int timeout) throws SocketException, IllegalStateException {
            if (this.hasConnection()) {
                this.wrappedConnection.setSoTimeout(timeout);
            }
        }

        @Override
        public void shutdownOutput() {
            if (this.hasConnection()) {
                this.wrappedConnection.shutdownOutput();
            }
        }

        @Override
        public void tunnelCreated() throws IllegalStateException, IOException {
            if (this.hasConnection()) {
                this.wrappedConnection.tunnelCreated();
            }
        }

        @Override
        public void write(byte[] data, int offset, int length) throws IOException, IllegalStateException {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.write(data, offset, length);
        }

        @Override
        public void write(byte[] data) throws IOException, IllegalStateException {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.write(data);
        }

        @Override
        public void writeLine() throws IOException, IllegalStateException {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.writeLine();
        }

        @Override
        public void writeLine(byte[] data) throws IOException, IllegalStateException {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.writeLine(data);
        }

        @Override
        public void flushRequestOutputStream() throws IOException {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.flushRequestOutputStream();
        }

        @Override
        public int getSoTimeout() throws SocketException {
            if (this.hasConnection()) {
                return this.wrappedConnection.getSoTimeout();
            }
            throw new IllegalStateException("Connection has been released");
        }

        @Override
        public String getVirtualHost() {
            if (this.hasConnection()) {
                return this.wrappedConnection.getVirtualHost();
            }
            throw new IllegalStateException("Connection has been released");
        }

        @Override
        public void setVirtualHost(String host) throws IllegalStateException {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.setVirtualHost(host);
        }

        @Override
        public int getSendBufferSize() throws SocketException {
            if (this.hasConnection()) {
                return this.wrappedConnection.getSendBufferSize();
            }
            throw new IllegalStateException("Connection has been released");
        }

        @Override
        public void setSendBufferSize(int sendBufferSize) throws SocketException {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.setSendBufferSize(sendBufferSize);
        }

        @Override
        public HttpConnectionParams getParams() {
            if (this.hasConnection()) {
                return this.wrappedConnection.getParams();
            }
            throw new IllegalStateException("Connection has been released");
        }

        @Override
        public void setParams(HttpConnectionParams params) {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.setParams(params);
        }

        @Override
        public void print(String data, String charset) throws IOException, IllegalStateException {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.print(data, charset);
        }

        @Override
        public void printLine(String data, String charset) throws IOException, IllegalStateException {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.printLine(data, charset);
        }

        @Override
        public void setSocketTimeout(int timeout) throws SocketException, IllegalStateException {
            if (!this.hasConnection()) {
                throw new IllegalStateException("Connection has been released");
            }
            this.wrappedConnection.setSocketTimeout(timeout);
        }
    }

    private static class HttpConnectionWithReference
    extends HttpConnection {
        public WeakReference reference = new WeakReference<HttpConnectionWithReference>(this, MultiThreadedHttpConnectionManager.access$1500());

        public HttpConnectionWithReference(HostConfiguration hostConfiguration) {
            super(hostConfiguration);
        }
    }

    private static class ReferenceQueueThread
    extends Thread {
        private volatile boolean shutdown = false;

        public ReferenceQueueThread() {
            this.setDaemon(true);
            this.setName("MultiThreadedHttpConnectionManager cleanup");
        }

        public void shutdown() {
            this.shutdown = true;
            this.interrupt();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void handleReference(Reference ref) {
            ConnectionSource source = null;
            Map map = REFERENCE_TO_CONNECTION_SOURCE;
            synchronized (map) {
                source = (ConnectionSource)REFERENCE_TO_CONNECTION_SOURCE.remove(ref);
            }
            if (source != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug((Object)("Connection reclaimed by garbage collector, hostConfig=" + source.hostConfiguration));
                }
                source.connectionPool.handleLostConnection(source.hostConfiguration);
            }
        }

        @Override
        public void run() {
            while (!this.shutdown) {
                try {
                    Reference ref = REFERENCE_QUEUE.remove();
                    if (ref == null) continue;
                    this.handleReference(ref);
                }
                catch (InterruptedException e) {
                    LOG.debug((Object)"ReferenceQueueThread interrupted", (Throwable)e);
                }
            }
        }
    }

    private static class WaitingThread {
        public Thread thread;
        public HostConnectionPool hostConnectionPool;
        public boolean interruptedByConnectionPool = false;

        private WaitingThread() {
        }
    }

    private static class HostConnectionPool {
        public HostConfiguration hostConfiguration;
        public LinkedList freeConnections = new LinkedList();
        public LinkedList waitingThreads = new LinkedList();
        public int numConnections = 0;

        private HostConnectionPool() {
        }
    }

    private static class ConnectionSource {
        public ConnectionPool connectionPool;
        public HostConfiguration hostConfiguration;

        private ConnectionSource() {
        }
    }

    private class ConnectionPool {
        private LinkedList freeConnections = new LinkedList();
        private LinkedList waitingThreads = new LinkedList();
        private final Map mapHosts = new HashMap();
        private IdleConnectionHandler idleConnectionHandler = new IdleConnectionHandler();
        private int numConnections = 0;

        private ConnectionPool() {
        }

        public synchronized void shutdown() {
            Iterator iter = this.freeConnections.iterator();
            while (iter.hasNext()) {
                HttpConnection conn = (HttpConnection)iter.next();
                iter.remove();
                conn.close();
            }
            MultiThreadedHttpConnectionManager.shutdownCheckedOutConnections(this);
            iter = this.waitingThreads.iterator();
            while (iter.hasNext()) {
                WaitingThread waiter = (WaitingThread)iter.next();
                iter.remove();
                waiter.interruptedByConnectionPool = true;
                waiter.thread.interrupt();
            }
            this.mapHosts.clear();
            this.idleConnectionHandler.removeAll();
        }

        public synchronized HttpConnection createConnection(HostConfiguration hostConfiguration) {
            HostConnectionPool hostPool = this.getHostPool(hostConfiguration, true);
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Allocating new connection, hostConfig=" + hostConfiguration));
            }
            HttpConnectionWithReference connection = new HttpConnectionWithReference(hostConfiguration);
            connection.getParams().setDefaults(MultiThreadedHttpConnectionManager.this.params);
            connection.setHttpConnectionManager(MultiThreadedHttpConnectionManager.this);
            ++this.numConnections;
            ++hostPool.numConnections;
            MultiThreadedHttpConnectionManager.storeReferenceToConnection(connection, hostConfiguration, this);
            return connection;
        }

        public synchronized void handleLostConnection(HostConfiguration config) {
            HostConnectionPool hostPool = this.getHostPool(config, true);
            --hostPool.numConnections;
            if (hostPool.numConnections == 0 && hostPool.waitingThreads.isEmpty()) {
                this.mapHosts.remove(config);
            }
            --this.numConnections;
            this.notifyWaitingThread(config);
        }

        public synchronized HostConnectionPool getHostPool(HostConfiguration hostConfiguration, boolean create) {
            LOG.trace((Object)"enter HttpConnectionManager.ConnectionPool.getHostPool(HostConfiguration)");
            HostConnectionPool listConnections = (HostConnectionPool)this.mapHosts.get(hostConfiguration);
            if (listConnections == null && create) {
                listConnections = new HostConnectionPool();
                listConnections.hostConfiguration = hostConfiguration;
                this.mapHosts.put(hostConfiguration, listConnections);
            }
            return listConnections;
        }

        public synchronized HttpConnection getFreeConnection(HostConfiguration hostConfiguration) {
            HttpConnectionWithReference connection = null;
            HostConnectionPool hostPool = this.getHostPool(hostConfiguration, false);
            if (hostPool != null && hostPool.freeConnections.size() > 0) {
                connection = (HttpConnectionWithReference)hostPool.freeConnections.removeLast();
                this.freeConnections.remove(connection);
                MultiThreadedHttpConnectionManager.storeReferenceToConnection(connection, hostConfiguration, this);
                if (LOG.isDebugEnabled()) {
                    LOG.debug((Object)("Getting free connection, hostConfig=" + hostConfiguration));
                }
                this.idleConnectionHandler.remove(connection);
            } else if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("There were no free connections to get, hostConfig=" + hostConfiguration));
            }
            return connection;
        }

        public synchronized void deleteClosedConnections() {
            Iterator iter = this.freeConnections.iterator();
            while (iter.hasNext()) {
                HttpConnection conn = (HttpConnection)iter.next();
                if (conn.isOpen()) continue;
                iter.remove();
                this.deleteConnection(conn);
            }
        }

        public synchronized void closeIdleConnections(long idleTimeout) {
            this.idleConnectionHandler.closeIdleConnections(idleTimeout);
        }

        private synchronized void deleteConnection(HttpConnection connection) {
            HostConfiguration connectionConfiguration = MultiThreadedHttpConnectionManager.this.configurationForConnection(connection);
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Reclaiming connection, hostConfig=" + connectionConfiguration));
            }
            connection.close();
            HostConnectionPool hostPool = this.getHostPool(connectionConfiguration, true);
            hostPool.freeConnections.remove(connection);
            --hostPool.numConnections;
            --this.numConnections;
            if (hostPool.numConnections == 0 && hostPool.waitingThreads.isEmpty()) {
                this.mapHosts.remove(connectionConfiguration);
            }
            this.idleConnectionHandler.remove(connection);
        }

        public synchronized void deleteLeastUsedConnection() {
            HttpConnection connection = (HttpConnection)this.freeConnections.removeFirst();
            if (connection != null) {
                this.deleteConnection(connection);
            } else if (LOG.isDebugEnabled()) {
                LOG.debug((Object)"Attempted to reclaim an unused connection but there were none.");
            }
        }

        public synchronized void notifyWaitingThread(HostConfiguration configuration) {
            this.notifyWaitingThread(this.getHostPool(configuration, true));
        }

        public synchronized void notifyWaitingThread(HostConnectionPool hostPool) {
            WaitingThread waitingThread = null;
            if (hostPool.waitingThreads.size() > 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug((Object)("Notifying thread waiting on host pool, hostConfig=" + hostPool.hostConfiguration));
                }
                waitingThread = (WaitingThread)hostPool.waitingThreads.removeFirst();
                this.waitingThreads.remove(waitingThread);
            } else if (this.waitingThreads.size() > 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug((Object)"No-one waiting on host pool, notifying next waiting thread.");
                }
                waitingThread = (WaitingThread)this.waitingThreads.removeFirst();
                waitingThread.hostConnectionPool.waitingThreads.remove(waitingThread);
            } else if (LOG.isDebugEnabled()) {
                LOG.debug((Object)"Notifying no-one, there are no waiting threads");
            }
            if (waitingThread != null) {
                waitingThread.interruptedByConnectionPool = true;
                waitingThread.thread.interrupt();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void freeConnection(HttpConnection conn) {
            HostConfiguration connectionConfiguration = MultiThreadedHttpConnectionManager.this.configurationForConnection(conn);
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Freeing connection, hostConfig=" + connectionConfiguration));
            }
            ConnectionPool connectionPool = this;
            synchronized (connectionPool) {
                if (MultiThreadedHttpConnectionManager.this.shutdown) {
                    conn.close();
                    return;
                }
                HostConnectionPool hostPool = this.getHostPool(connectionConfiguration, true);
                hostPool.freeConnections.add(conn);
                if (hostPool.numConnections == 0) {
                    LOG.error((Object)("Host connection pool not found, hostConfig=" + connectionConfiguration));
                    hostPool.numConnections = 1;
                }
                this.freeConnections.add(conn);
                MultiThreadedHttpConnectionManager.removeReferenceToConnection((HttpConnectionWithReference)conn);
                if (this.numConnections == 0) {
                    LOG.error((Object)("Host connection pool not found, hostConfig=" + connectionConfiguration));
                    this.numConnections = 1;
                }
                this.idleConnectionHandler.add(conn);
                this.notifyWaitingThread(hostPool);
            }
        }
    }
}

