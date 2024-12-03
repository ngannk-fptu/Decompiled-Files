/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SocketFinder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

final class SocketConnector
implements Runnable {
    private final Socket socket;
    private final SocketFinder socketFinder;
    private final InetSocketAddress inetSocketAddress;
    private final int timeoutInMilliseconds;
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SocketConnector");
    private final String traceID;
    private final String threadID;
    private static final AtomicLong lastThreadID = new AtomicLong();

    SocketConnector(Socket socket, InetSocketAddress inetSocketAddress, int timeOutInMilliSeconds, SocketFinder socketFinder) {
        this.socket = socket;
        this.inetSocketAddress = inetSocketAddress;
        this.timeoutInMilliseconds = timeOutInMilliSeconds;
        this.socketFinder = socketFinder;
        this.threadID = Long.toString(SocketConnector.nextThreadID());
        this.traceID = "SocketConnector:" + this.threadID + "(" + socketFinder.toString() + ")";
    }

    @Override
    public void run() {
        IOException exception = null;
        SocketFinder.Result result = this.socketFinder.getResult();
        if (result.equals((Object)SocketFinder.Result.UNKNOWN)) {
            try {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + " connecting to InetSocketAddress:" + this.inetSocketAddress + " with timeout:" + this.timeoutInMilliseconds);
                }
                this.socket.connect(this.inetSocketAddress, this.timeoutInMilliseconds);
            }
            catch (IOException ex) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + " exception:" + ex.getClass() + " with message:" + ex.getMessage() + " occurred while connecting to InetSocketAddress:" + this.inetSocketAddress);
                }
                exception = ex;
            }
            this.socketFinder.updateResult(this.socket, exception, this.toString());
        }
    }

    public String toString() {
        return this.traceID;
    }

    private static long nextThreadID() {
        return lastThreadID.updateAndGet(threadId -> {
            if (threadId == Long.MAX_VALUE) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer("Resetting the Id count");
                }
                return 1L;
            }
            return threadId + 1L;
        });
    }
}

