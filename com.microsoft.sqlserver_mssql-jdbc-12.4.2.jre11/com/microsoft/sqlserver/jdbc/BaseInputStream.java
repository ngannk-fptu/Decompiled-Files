/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.ServerDTVImpl;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSReaderMark;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class BaseInputStream
extends InputStream {
    final boolean isAdaptive;
    final boolean isStreaming;
    int payloadLength;
    private static final AtomicInteger lastLoggingID = new AtomicInteger(0);
    static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.InputStream");
    private String traceID;
    int streamPos = 0;
    int markedStreamPos = 0;
    TDSReaderMark currentMark;
    private ServerDTVImpl dtv;
    TDSReader tdsReader;
    int readLimit = 0;
    boolean isReadLimitSet = false;

    abstract byte[] getBytes() throws SQLServerException;

    private static int nextLoggingID() {
        return lastLoggingID.incrementAndGet();
    }

    public final String toString() {
        if (this.traceID == null) {
            this.traceID = this.getClass().getName() + "ID:" + BaseInputStream.nextLoggingID();
        }
        return this.traceID;
    }

    final void setLoggingInfo(String info) {
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString());
        }
    }

    BaseInputStream(TDSReader tdsReader, boolean isAdaptive, boolean isStreaming, ServerDTVImpl dtv) {
        this.tdsReader = tdsReader;
        this.isAdaptive = isAdaptive;
        this.isStreaming = isStreaming;
        if (isAdaptive) {
            this.clearCurrentMark();
        } else {
            this.currentMark = tdsReader.mark();
        }
        this.dtv = dtv;
    }

    final void clearCurrentMark() {
        this.currentMark = null;
        this.isReadLimitSet = false;
        if (this.isAdaptive && this.isStreaming) {
            this.tdsReader.stream();
        }
    }

    void closeHelper() {
        if (this.isAdaptive && null != this.dtv) {
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(this.toString() + " closing the adaptive stream.");
            }
            this.dtv.setPositionAfterStreamed(this.tdsReader);
        }
        this.currentMark = null;
        this.tdsReader = null;
        this.dtv = null;
    }

    final void checkClosed() throws IOException {
        if (null == this.tdsReader) {
            throw new IOException(SQLServerException.getErrString("R_streamIsClosed"));
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    void setReadLimit(int readLimit) {
        if (this.isAdaptive && readLimit > 0) {
            this.readLimit = readLimit;
            this.isReadLimitSet = true;
        }
    }

    void resetHelper() throws IOException {
        this.checkClosed();
        if (null == this.currentMark) {
            throw new IOException(SQLServerException.getErrString("R_streamWasNotMarkedBefore"));
        }
        this.tdsReader.reset(this.currentMark);
    }
}

