/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.IPAddressPreference;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SocketConnector;
import com.microsoft.sqlserver.jdbc.Util;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.SocketFactory;

final class SocketFinder {
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 5L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    private static final int MIN_TIMEOUT_FOR_PARALLEL_CONNECTIONS = 1500;
    private final Lock socketFinderlock = new ReentrantLock();
    private final Lock parentThreadLock = new ReentrantLock();
    private final Condition parentCondition = this.parentThreadLock.newCondition();
    private volatile Result result = Result.UNKNOWN;
    private int noOfSpawnedThreads = 0;
    private int noOfThreadsThatNotified = 0;
    private volatile Socket selectedSocket = null;
    private volatile IOException selectedException = null;
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SocketFinder");
    private final String traceID;
    private static final int IP_ADDRESS_LIMIT = 64;
    private final SQLServerConnection conn;
    private SocketFactory socketFactory = null;

    SocketFinder(String callerTraceID, SQLServerConnection sqlServerConnection) {
        this.traceID = "SocketFinder(" + callerTraceID + ")";
        this.conn = sqlServerConnection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Socket findSocket(String hostName, int portNumber, int timeoutInMilliSeconds, boolean useParallel, boolean useTnir, boolean isTnirFirstAttempt, int timeoutInMilliSecondsForFullTimeout, String iPAddressPreference) throws SQLServerException {
        assert (timeoutInMilliSeconds != 0) : "The driver does not allow a time out of 0";
        try {
            InetAddress[] inetAddrs = null;
            if (!useParallel) {
                if (useTnir && isTnirFirstAttempt) {
                    return this.getSocketByIPPreference(hostName, portNumber, 500, iPAddressPreference);
                }
                if (!useTnir) {
                    return this.getSocketByIPPreference(hostName, portNumber, timeoutInMilliSeconds, iPAddressPreference);
                }
            }
            if (useParallel || useTnir) {
                inetAddrs = InetAddress.getAllByName(hostName);
                if (useTnir && inetAddrs.length > 64) {
                    useTnir = false;
                    timeoutInMilliSeconds = timeoutInMilliSecondsForFullTimeout;
                }
            }
            if (logger.isLoggable(Level.FINER)) {
                StringBuilder loggingString = new StringBuilder(this.toString());
                loggingString.append(" Total no of InetAddresses: ");
                loggingString.append(inetAddrs != null ? inetAddrs.length : 0);
                loggingString.append(". They are: ");
                for (InetAddress inetAddr : inetAddrs) {
                    loggingString.append(inetAddr.toString()).append(";");
                }
                logger.finer(loggingString.toString());
            }
            if (inetAddrs != null && inetAddrs.length > 64) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ipAddressLimitWithMultiSubnetFailover"));
                Object[] msgArgs = new Object[]{Integer.toString(64)};
                String errorStr = form.format(msgArgs);
                this.conn.terminate(6, errorStr);
            }
            if (inetAddrs != null && inetAddrs.length == 1) {
                return this.getConnectedSocket(inetAddrs[0], portNumber, timeoutInMilliSeconds);
            }
            timeoutInMilliSeconds = Math.max(timeoutInMilliSeconds, 1500);
            if (Util.isIBM()) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + "Using Java NIO with timeout:" + timeoutInMilliSeconds);
                }
                this.findSocketUsingJavaNIO(inetAddrs, portNumber, timeoutInMilliSeconds);
            } else {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + "Using Threading with timeout:" + timeoutInMilliSeconds);
                }
                this.findSocketUsingThreading(inetAddrs, portNumber, timeoutInMilliSeconds);
            }
            if (this.result.equals((Object)Result.UNKNOWN)) {
                this.socketFinderlock.lock();
                try {
                    if (this.result.equals((Object)Result.UNKNOWN)) {
                        this.result = Result.FAILURE;
                        if (logger.isLoggable(Level.FINER)) {
                            logger.finer(this.toString() + " The parent thread updated the result to failure");
                        }
                    }
                }
                finally {
                    this.socketFinderlock.unlock();
                }
            }
            if (this.result.equals((Object)Result.FAILURE)) {
                if (this.selectedException == null) {
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer(this.toString() + " There is no selectedException. The wait calls timed out before any connect call returned or timed out.");
                    }
                    String message = SQLServerException.getErrString("R_connectionTimedOut");
                    this.selectedException = new IOException(message);
                }
                throw this.selectedException;
            }
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            this.close(this.selectedSocket);
            SQLServerException.convertConnectExceptionToSQLServerException(hostName, portNumber, this.conn, ex);
        }
        catch (IOException ex) {
            this.close(this.selectedSocket);
            SQLServerException.convertConnectExceptionToSQLServerException(hostName, portNumber, this.conn, ex);
        }
        assert (this.result.equals((Object)Result.SUCCESS));
        assert (this.selectedSocket != null) : "Bug in code. Selected Socket cannot be null here.";
        return this.selectedSocket;
    }

    private void findSocketUsingJavaNIO(InetAddress[] inetAddrs, int portNumber, int timeoutInMilliSeconds) throws IOException {
        assert (timeoutInMilliSeconds != 0) : "The timeout cannot be zero";
        assert (inetAddrs.length != 0) : "Number of inetAddresses should not be zero in this function";
        Selector selector = null;
        LinkedList<SocketChannel> socketChannels = new LinkedList<SocketChannel>();
        AbstractSelectableChannel selectedChannel = null;
        try {
            long timeRemaining;
            selector = Selector.open();
            for (InetAddress inetAddr : inetAddrs) {
                SocketChannel sChannel = SocketChannel.open();
                socketChannels.add(sChannel);
                sChannel.configureBlocking(false);
                int ops = 8;
                SelectionKey key = sChannel.register(selector, ops);
                sChannel.connect(new InetSocketAddress(inetAddr, portNumber));
                if (!logger.isLoggable(Level.FINER)) continue;
                logger.finer(this.toString() + " initiated connection to address: " + inetAddr + ", portNumber: " + portNumber);
            }
            long timerNow = System.currentTimeMillis();
            long timerExpire = timerNow + (long)timeoutInMilliSeconds;
            int noOfOutstandingChannels = inetAddrs.length;
            while ((timeRemaining = timerExpire - timerNow) > 0L && selectedChannel == null) {
                if (noOfOutstandingChannels <= 0) {
                    break;
                }
                int readyChannels = selector.select(timeRemaining);
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + " no of channels ready: " + readyChannels);
                }
                if (readyChannels != 0) {
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        SocketChannel ch = (SocketChannel)key.channel();
                        if (logger.isLoggable(Level.FINER)) {
                            logger.finer(this.toString() + " processing the channel :" + ch);
                        }
                        boolean connected = false;
                        try {
                            connected = ch.finishConnect();
                            assert (connected) : "finishConnect on channel:" + ch + " cannot be false";
                            selectedChannel = ch;
                            if (!logger.isLoggable(Level.FINER)) break;
                            logger.finer(this.toString() + " selected the channel :" + (SocketChannel)selectedChannel);
                            break;
                        }
                        catch (IOException ex) {
                            if (logger.isLoggable(Level.FINER)) {
                                logger.finer(this.toString() + " the exception: " + ex.getClass() + " with message: " + ex.getMessage() + " occurred while processing the channel: " + ch);
                            }
                            this.updateSelectedException(ex, this.toString());
                            ch.close();
                            key.cancel();
                            keyIterator.remove();
                            --noOfOutstandingChannels;
                        }
                    }
                }
                timerNow = System.currentTimeMillis();
            }
        }
        catch (IOException ex) {
            this.close((SocketChannel)selectedChannel);
            throw ex;
        }
        finally {
            this.close(selector);
            for (SocketChannel s : socketChannels) {
                if (s == selectedChannel) continue;
                this.close(s);
            }
        }
        if (selectedChannel != null) {
            selectedChannel.configureBlocking(true);
            this.selectedSocket = ((SocketChannel)selectedChannel).socket();
            this.result = Result.SUCCESS;
        }
    }

    private SocketFactory getSocketFactory() throws IOException {
        if (this.socketFactory == null) {
            String socketFactoryClass = this.conn.getSocketFactoryClass();
            if (socketFactoryClass == null) {
                this.socketFactory = SocketFactory.getDefault();
            } else {
                String socketFactoryConstructorArg = this.conn.getSocketFactoryConstructorArg();
                try {
                    Object[] msgArgs = new Object[]{"socketFactoryClass", "javax.net.SocketFactory"};
                    this.socketFactory = (SocketFactory)Util.newInstance(SocketFactory.class, socketFactoryClass, socketFactoryConstructorArg, msgArgs);
                }
                catch (RuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new IOException(e);
                }
            }
        }
        return this.socketFactory;
    }

    private InetSocketAddress getInetAddressByIPPreference(InetAddress[] addresses, boolean ipv6first, String hostName, int portNumber) {
        InetSocketAddress addr = InetSocketAddress.createUnresolved(hostName, portNumber);
        for (InetAddress inetAddress : this.fillAddressList(addresses, ipv6first)) {
            addr = new InetSocketAddress(inetAddress, portNumber);
            if (addr.isUnresolved()) continue;
            return addr;
        }
        return addr;
    }

    private Socket getSocketByIPPreference(String hostName, int portNumber, int timeoutInMilliSeconds, String iPAddressPreference) throws IOException, SQLServerException {
        InetSocketAddress addr = null;
        InetAddress[] addresses = InetAddress.getAllByName(hostName);
        IPAddressPreference pref = IPAddressPreference.valueOfString(iPAddressPreference);
        switch (pref) {
            case IPV6_FIRST: {
                addr = this.getInetAddressByIPPreference(addresses, true, hostName, portNumber);
                if (addr.isUnresolved()) {
                    addr = this.getInetAddressByIPPreference(addresses, false, hostName, portNumber);
                }
                if (addr.isUnresolved()) break;
                return this.getConnectedSocket(addr, timeoutInMilliSeconds);
            }
            case IPV4_FIRST: {
                addr = this.getInetAddressByIPPreference(addresses, false, hostName, portNumber);
                if (addr.isUnresolved()) {
                    addr = this.getInetAddressByIPPreference(addresses, true, hostName, portNumber);
                }
                if (addr.isUnresolved()) break;
                return this.getConnectedSocket(addr, timeoutInMilliSeconds);
            }
            case USE_PLATFORM_DEFAULT: {
                for (InetAddress address : addresses) {
                    addr = new InetSocketAddress(address, portNumber);
                    if (addr.isUnresolved()) continue;
                    return this.getConnectedSocket(addr, timeoutInMilliSeconds);
                }
                break;
            }
        }
        if (addr != null && addr.isUnresolved()) {
            InetSocketAddress cacheEntry;
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(this.toString() + "Failed to resolve host name: " + hostName + ". Using IP address from DNS cache.");
            }
            addr = null != (cacheEntry = SQLServerConnection.getDNSEntry(hostName)) ? cacheEntry : addr;
        }
        return this.getConnectedSocket(addr, timeoutInMilliSeconds);
    }

    private List<InetAddress> fillAddressList(InetAddress[] addresses, boolean ipv6first) {
        ArrayList<InetAddress> addressList = new ArrayList<InetAddress>();
        if (ipv6first) {
            for (InetAddress addr : addresses) {
                if (!(addr instanceof Inet6Address)) continue;
                addressList.add(addr);
            }
        } else {
            for (InetAddress addr : addresses) {
                if (!(addr instanceof Inet4Address)) continue;
                addressList.add(addr);
            }
        }
        return addressList;
    }

    private Socket getConnectedSocket(InetAddress inetAddr, int portNumber, int timeoutInMilliSeconds) throws IOException {
        InetSocketAddress addr = new InetSocketAddress(inetAddr, portNumber);
        return this.getConnectedSocket(addr, timeoutInMilliSeconds);
    }

    private Socket getConnectedSocket(InetSocketAddress addr, int timeoutInMilliSeconds) throws IOException {
        assert (timeoutInMilliSeconds != 0) : "timeout cannot be zero";
        if (addr.isUnresolved()) {
            throw new UnknownHostException();
        }
        this.selectedSocket = this.getSocketFactory().createSocket();
        if (!this.selectedSocket.isConnected()) {
            this.selectedSocket.connect(addr, timeoutInMilliSeconds);
        }
        return this.selectedSocket;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void findSocketUsingThreading(InetAddress[] inetAddrs, int portNumber, int timeoutInMilliSeconds) throws IOException, InterruptedException {
        assert (timeoutInMilliSeconds != 0) : "The timeout cannot be zero";
        assert (inetAddrs.length != 0) : "Number of inetAddresses should not be zero in this function";
        LinkedList<Socket> sockets = new LinkedList<Socket>();
        LinkedList<SocketConnector> socketConnectors = new LinkedList<SocketConnector>();
        try {
            this.noOfSpawnedThreads = inetAddrs.length;
            for (InetAddress inetAddress : inetAddrs) {
                Socket s = this.getSocketFactory().createSocket();
                sockets.add(s);
                InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, portNumber);
                SocketConnector socketConnector = new SocketConnector(s, inetSocketAddress, timeoutInMilliSeconds, this);
                socketConnectors.add(socketConnector);
            }
            this.parentThreadLock.lock();
            try {
                for (SocketConnector sc : socketConnectors) {
                    threadPoolExecutor.execute(sc);
                }
                long timerNow = System.currentTimeMillis();
                long timerExpire = timerNow + (long)timeoutInMilliSeconds;
                while (true) {
                    long timeRemaining = timerExpire - timerNow;
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer(this.toString() + " TimeRemaining:" + timeRemaining + "; Result:" + this.result + "; Max. open thread count: " + threadPoolExecutor.getLargestPoolSize() + "; Current open thread count:" + threadPoolExecutor.getActiveCount());
                    }
                    if (timeRemaining <= 0L) break;
                    if (!this.result.equals((Object)Result.UNKNOWN)) {
                        break;
                    }
                    try {
                        this.parentCondition.await(timeRemaining, TimeUnit.MILLISECONDS);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer(this.toString() + " The parent thread wokeup.");
                    }
                    timerNow = System.currentTimeMillis();
                }
            }
            finally {
                this.parentThreadLock.unlock();
            }
        }
        finally {
            for (Socket s : sockets) {
                if (s == this.selectedSocket) continue;
                this.close(s);
            }
        }
        if (this.selectedSocket != null) {
            this.result = Result.SUCCESS;
        }
    }

    Result getResult() {
        return this.result;
    }

    void close(Selector selector) {
        block4: {
            if (null != selector) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + ": Closing Selector");
                }
                try {
                    selector.close();
                }
                catch (IOException e) {
                    if (!logger.isLoggable(Level.FINE)) break block4;
                    logger.log(Level.FINE, this.toString() + ": Ignored the following error while closing Selector", e);
                }
            }
        }
    }

    void close(Socket socket) {
        block4: {
            if (null != socket) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + ": Closing TCP socket:" + socket);
                }
                try {
                    socket.close();
                }
                catch (IOException e) {
                    if (!logger.isLoggable(Level.FINE)) break block4;
                    logger.log(Level.FINE, this.toString() + ": Ignored the following error while closing socket", e);
                }
            }
        }
    }

    void close(SocketChannel socketChannel) {
        block4: {
            if (null != socketChannel) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + ": Closing TCP socket channel:" + socketChannel);
                }
                try {
                    socketChannel.close();
                }
                catch (IOException e) {
                    if (!logger.isLoggable(Level.FINE)) break block4;
                    logger.log(Level.FINE, this.toString() + "Ignored the following error while closing socketChannel", e);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void updateResult(Socket socket, IOException exception, String threadId) {
        if (this.result.equals((Object)Result.UNKNOWN)) {
            block18: {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer("The following child thread is waiting for socketFinderLock:" + threadId);
                }
                this.socketFinderlock.lock();
                try {
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer("The following child thread acquired socketFinderLock:" + threadId);
                    }
                    if (this.result.equals((Object)Result.UNKNOWN)) {
                        if (exception == null && this.selectedSocket == null) {
                            this.selectedSocket = socket;
                            this.result = Result.SUCCESS;
                            if (logger.isLoggable(Level.FINER)) {
                                logger.finer("The socket of the following thread has been chosen:" + threadId);
                            }
                        }
                        if (exception != null) {
                            this.updateSelectedException(exception, threadId);
                        }
                    }
                    ++this.noOfThreadsThatNotified;
                    if (this.noOfThreadsThatNotified >= this.noOfSpawnedThreads && this.result.equals((Object)Result.UNKNOWN)) {
                        this.result = Result.FAILURE;
                    }
                    if (this.result.equals((Object)Result.UNKNOWN)) break block18;
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer("The following child thread is waiting for parentThreadLock:" + threadId);
                    }
                    this.parentThreadLock.lock();
                    try {
                        if (logger.isLoggable(Level.FINER)) {
                            logger.finer("The following child thread acquired parentThreadLock:" + threadId);
                        }
                        this.parentCondition.signalAll();
                    }
                    finally {
                        this.parentThreadLock.unlock();
                    }
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer("The following child thread released parentThreadLock and notified the parent thread:" + threadId);
                    }
                }
                finally {
                    this.socketFinderlock.unlock();
                }
            }
            if (logger.isLoggable(Level.FINER)) {
                logger.finer("The following child thread released socketFinderLock:" + threadId);
            }
        }
    }

    public void updateSelectedException(IOException ex, String traceId) {
        boolean updatedException = false;
        if (this.selectedException == null || !(ex instanceof SocketTimeoutException) && this.selectedException instanceof SocketTimeoutException) {
            this.selectedException = ex;
            updatedException = true;
        }
        if (updatedException && logger.isLoggable(Level.FINER)) {
            logger.finer("The selected exception is updated to the following: ExceptionType:" + ex.getClass() + "; ExceptionMessage:" + ex.getMessage() + "; by the following thread:" + traceId);
        }
    }

    public String toString() {
        return this.traceID;
    }

    static enum Result {
        UNKNOWN,
        SUCCESS,
        FAILURE;

    }
}

