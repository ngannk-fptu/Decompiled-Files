/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.DriverError;
import com.microsoft.sqlserver.jdbc.ICounter;
import com.microsoft.sqlserver.jdbc.MaxResultBufferCounter;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLState;
import com.microsoft.sqlserver.jdbc.TDSParser;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSTimeoutTask;
import com.microsoft.sqlserver.jdbc.TDSWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class TDSCommand
implements Serializable {
    private static final long serialVersionUID = 5485075546328951857L;
    static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.TDS.Command");
    private final String logContext;
    private String traceID;
    private volatile transient TDSWriter tdsWriter;
    private volatile TDSReader tdsReader;
    private final transient Lock interruptLock = new ReentrantLock();
    private volatile boolean interruptsEnabled = false;
    private volatile boolean wasInterrupted = false;
    private volatile String interruptReason = null;
    private volatile boolean requestComplete;
    private volatile boolean attentionPending = false;
    private volatile boolean processedResponse;
    private volatile boolean readingResponse;
    private int queryTimeoutSeconds;
    private int cancelQueryTimeoutSeconds;
    private transient ScheduledFuture<?> timeout;
    private boolean isExecuted = false;
    protected ArrayList<byte[]> enclaveCEKs;
    private transient ICounter counter;
    private boolean interruptChecked = false;
    private transient Thread correspondingThread = null;

    abstract boolean doExecute() throws SQLServerException;

    final String getLogContext() {
        return this.logContext;
    }

    public final String toString() {
        if (this.traceID == null) {
            this.traceID = "TDSCommand@" + Integer.toHexString(this.hashCode()) + " (" + this.logContext + ")";
        }
        return this.traceID;
    }

    final void log(Level level, String message) {
        logger.log(level, this.toString() + ": " + message);
    }

    protected TDSWriter getTDSWriter() {
        return this.tdsWriter;
    }

    protected boolean getInterruptsEnabled() {
        return this.interruptsEnabled;
    }

    protected void setInterruptsEnabled(boolean interruptsEnabled) {
        this.interruptLock.lock();
        try {
            this.interruptsEnabled = interruptsEnabled;
        }
        finally {
            this.interruptLock.unlock();
        }
    }

    boolean wasInterrupted() {
        return this.wasInterrupted;
    }

    protected boolean getRequestComplete() {
        return this.requestComplete;
    }

    protected void setRequestComplete(boolean requestComplete) {
        this.interruptLock.lock();
        try {
            this.requestComplete = requestComplete;
        }
        finally {
            this.interruptLock.unlock();
        }
    }

    boolean attentionPending() {
        return this.attentionPending;
    }

    protected boolean getProcessedResponse() {
        return this.processedResponse;
    }

    protected void setProcessedResponse(boolean processedResponse) {
        this.interruptLock.lock();
        try {
            this.processedResponse = processedResponse;
        }
        finally {
            this.interruptLock.unlock();
        }
    }

    protected int getQueryTimeoutSeconds() {
        return this.queryTimeoutSeconds;
    }

    protected int getCancelQueryTimeoutSeconds() {
        return this.cancelQueryTimeoutSeconds;
    }

    final boolean readingResponse() {
        return this.readingResponse;
    }

    ICounter getCounter() {
        return this.counter;
    }

    void createCounter(ICounter previousCounter, Properties activeConnectionProperties) {
        if (null == previousCounter) {
            String maxResultBuffer = activeConnectionProperties.getProperty(SQLServerDriverStringProperty.MAX_RESULT_BUFFER.toString());
            this.counter = new MaxResultBufferCounter(Long.parseLong(maxResultBuffer));
        } else {
            this.counter = previousCounter;
        }
    }

    boolean wasExecuted() {
        return this.isExecuted;
    }

    TDSCommand(String logContext, int queryTimeoutSeconds, int cancelQueryTimeoutSeconds) {
        this.logContext = logContext;
        this.queryTimeoutSeconds = queryTimeoutSeconds;
        this.cancelQueryTimeoutSeconds = cancelQueryTimeoutSeconds;
    }

    boolean execute(TDSWriter tdsWriter, TDSReader tdsReader) throws SQLServerException {
        this.isExecuted = true;
        this.tdsWriter = tdsWriter;
        this.tdsReader = tdsReader;
        assert (null != tdsReader);
        try {
            return this.doExecute();
        }
        catch (SQLServerException e) {
            block6: {
                try {
                    if (!this.requestComplete && !tdsReader.getConnection().isClosed()) {
                        this.interrupt(e.getMessage());
                        this.onRequestComplete();
                        this.close();
                    }
                }
                catch (SQLServerException interruptException) {
                    if (!logger.isLoggable(Level.FINE)) break block6;
                    logger.fine(this.toString() + ": Ignoring error in sending attention: " + interruptException.getMessage());
                }
            }
            throw e;
        }
    }

    void processResponse(TDSReader tdsReader) throws SQLServerException {
        block4: {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this.toString() + ": Processing response");
            }
            try {
                TDSParser.parse(tdsReader, this.getLogContext());
            }
            catch (SQLServerException e) {
                if (2 != e.getDriverErrorCode()) {
                    throw e;
                }
                if (!logger.isLoggable(Level.FINEST)) break block4;
                logger.finest(this.toString() + ": Ignoring error from database: " + e.getMessage());
            }
        }
    }

    final void detach() throws SQLServerException {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this + ": detaching...");
        }
        while (this.tdsReader.readPacket()) {
        }
        assert (!this.readingResponse);
    }

    final void close() {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this + ": closing...");
        }
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this + ": processing response...");
        }
        while (!this.processedResponse) {
            try {
                this.processResponse(this.tdsReader);
            }
            catch (SQLServerException e) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(this + ": close ignoring error processing response: " + e.getMessage());
                }
                if (!this.tdsReader.getConnection().isSessionUnAvailable()) continue;
                this.processedResponse = true;
                this.attentionPending = false;
            }
        }
        if (this.attentionPending) {
            block19: {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(this + ": processing attention ack...");
                }
                try {
                    TDSParser.parse(this.tdsReader, "attention ack");
                }
                catch (SQLServerException e) {
                    if (this.tdsReader.getConnection().isSessionUnAvailable()) {
                        if (logger.isLoggable(Level.FINEST)) {
                            logger.finest(this + ": giving up on attention ack after connection closed by exception: " + e);
                        }
                        this.attentionPending = false;
                    }
                    if (!logger.isLoggable(Level.FINEST)) break block19;
                    logger.finest(this + ": ignored exception: " + e);
                }
            }
            if (this.attentionPending) {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe(this.toString() + ": expected attn ack missing or not processed; terminating connection...");
                }
                try {
                    this.tdsReader.throwInvalidTDS();
                }
                catch (SQLServerException e) {
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.finest(this + ": ignored expected invalid TDS exception: " + e);
                    }
                    assert (this.tdsReader.getConnection().isSessionUnAvailable());
                    this.attentionPending = false;
                }
            }
        }
        assert (this.processedResponse && !this.attentionPending);
    }

    void interrupt(String reason) throws SQLServerException {
        this.interruptLock.lock();
        try {
            if (this.interruptsEnabled && !this.wasInterrupted()) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(this + ": Raising interrupt for reason:" + reason);
                }
                this.wasInterrupted = true;
                this.interruptReason = reason;
                if (this.requestComplete) {
                    this.attentionPending = this.tdsWriter.sendAttention();
                }
                if (this.correspondingThread != null) {
                    this.correspondingThread.interrupt();
                    this.correspondingThread = null;
                }
            }
        }
        finally {
            this.interruptLock.unlock();
        }
    }

    final void checkForInterrupt() throws SQLServerException {
        if (this.wasInterrupted() && !this.interruptChecked) {
            this.interruptChecked = true;
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this + ": throwing interrupt exception, reason: " + this.interruptReason);
            }
            throw new SQLServerException(this.interruptReason, SQLState.STATEMENT_CANCELED, DriverError.NOT_SET, null);
        }
    }

    final void onRequestComplete() throws SQLServerException {
        this.interruptLock.lock();
        try {
            assert (!this.requestComplete);
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this + ": request complete");
            }
            this.requestComplete = true;
            if (!this.interruptsEnabled) {
                assert (!this.attentionPending);
                assert (!this.processedResponse);
                assert (!this.readingResponse);
                this.processedResponse = true;
            } else if (this.wasInterrupted()) {
                if (this.tdsWriter.isEOMSent()) {
                    this.readingResponse = this.attentionPending = this.tdsWriter.sendAttention();
                } else {
                    assert (!this.attentionPending);
                    this.readingResponse = this.tdsWriter.ignoreMessage();
                }
                this.processedResponse = !this.readingResponse;
            } else {
                assert (!this.attentionPending);
                assert (!this.processedResponse);
                this.readingResponse = true;
            }
        }
        finally {
            this.interruptLock.unlock();
        }
    }

    final void onResponseEOM() throws SQLServerException {
        boolean readAttentionAck = false;
        this.interruptLock.lock();
        try {
            if (this.interruptsEnabled) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(this + ": disabling interrupts");
                }
                readAttentionAck = this.attentionPending;
                this.interruptsEnabled = false;
            }
        }
        finally {
            this.interruptLock.unlock();
        }
        if (readAttentionAck) {
            this.tdsReader.readPacket();
        }
        this.readingResponse = false;
    }

    final void onTokenEOF() {
        this.processedResponse = true;
    }

    final void onAttentionAck() {
        this.attentionPending = false;
    }

    final TDSWriter startRequest(byte tdsMessageType) throws SQLServerException {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this + ": starting request...");
        }
        try {
            this.tdsWriter.startMessage(this, tdsMessageType);
        }
        catch (SQLServerException e) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this + ": starting request: exception: " + e.getMessage());
            }
            throw e;
        }
        this.interruptLock.lock();
        try {
            this.requestComplete = false;
            this.readingResponse = false;
            this.processedResponse = false;
            this.attentionPending = false;
            this.wasInterrupted = false;
            this.interruptReason = null;
            this.interruptsEnabled = true;
        }
        finally {
            this.interruptLock.unlock();
        }
        return this.tdsWriter;
    }

    final TDSReader startResponse() throws SQLServerException {
        return this.startResponse(false);
    }

    final TDSReader startResponse(boolean isAdaptive) throws SQLServerException {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this + ": finishing request");
        }
        try {
            this.tdsWriter.endMessage();
        }
        catch (SQLServerException e) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this + ": finishing request: endMessage threw exception: " + e.getMessage());
            }
            throw e;
        }
        if (this.queryTimeoutSeconds > 0) {
            SQLServerConnection conn = this.tdsReader != null ? this.tdsReader.getConnection() : null;
            this.timeout = this.tdsWriter.getSharedTimer().schedule(new TDSTimeoutTask(this, conn), this.queryTimeoutSeconds);
        }
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this.toString() + ": Reading response...");
        }
        try {
            if (isAdaptive) {
                this.tdsReader.readPacket();
            } else {
                while (this.tdsReader.readPacket()) {
                }
            }
        }
        catch (SQLServerException e) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this.toString() + ": Exception reading response: " + e.getMessage());
            }
            throw e;
        }
        finally {
            if (this.timeout != null) {
                this.timeout.cancel(false);
                this.timeout = null;
            }
        }
        if (!(this instanceof SQLServerConnection.FedAuthTokenCommand)) {
            this.tdsReader.getConnection().getSessionRecovery().incrementUnprocessedResponseCount();
        }
        return this.tdsReader;
    }

    void attachThread(Thread reconnectThread) {
        this.correspondingThread = reconnectThread;
    }
}

