/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLCollation;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SessionStateValue;
import com.microsoft.sqlserver.jdbc.TDSReader;
import java.util.concurrent.atomic.AtomicInteger;

class SessionStateTable {
    static final int SESSION_STATE_ID_MAX = 256;
    static final long MASTER_RECOVERY_DISABLE_SEQ_NUMBER = -1L;
    private boolean masterRecoveryDisabled;
    private byte[][] sessionStateInitial;
    private SessionStateValue[] sessionStateDelta;
    private AtomicInteger unRecoverableSessionStateCount = new AtomicInteger(0);
    private String originalCatalog;
    private String originalLanguage;
    private SQLCollation originalCollation;
    private byte originalNegotiatedEncryptionLevel = (byte)-1;
    private boolean resetCalled = false;

    SessionStateTable() {
        this.sessionStateDelta = new SessionStateValue[256];
        this.sessionStateInitial = new byte[256][];
    }

    void updateSessionState(TDSReader tdsReader, short sessionStateId, int sessionStateLength, int sequenceNumber, boolean fRecoverable) throws SQLServerException {
        this.sessionStateDelta[sessionStateId].setSequenceNumber(sequenceNumber);
        this.sessionStateDelta[sessionStateId].setDataLengh(sessionStateLength);
        if (this.sessionStateDelta[sessionStateId].getData() == null || this.sessionStateDelta[sessionStateId].getData().length < sessionStateLength) {
            this.sessionStateDelta[sessionStateId].setData(new byte[sessionStateLength]);
            if (!fRecoverable) {
                this.unRecoverableSessionStateCount.incrementAndGet();
            }
        } else if (fRecoverable != this.sessionStateDelta[sessionStateId].isRecoverable()) {
            if (fRecoverable) {
                this.unRecoverableSessionStateCount.decrementAndGet();
            } else {
                this.unRecoverableSessionStateCount.incrementAndGet();
            }
        }
        tdsReader.readBytes(this.sessionStateDelta[sessionStateId].getData(), 0, sessionStateLength);
        this.sessionStateDelta[sessionStateId].setRecoverable(fRecoverable);
    }

    int getInitialLength() {
        int length = 0;
        for (int i = 0; i < 256; ++i) {
            if (this.sessionStateInitial[i] == null) continue;
            length += 1 + (this.sessionStateInitial[i].length < 255 ? 1 : 5) + this.sessionStateInitial[i].length;
        }
        return length;
    }

    int getDeltaLength() {
        int length = 0;
        for (int i = 0; i < 256; ++i) {
            if (this.sessionStateDelta[i] == null || this.sessionStateDelta[i].getData() == null) continue;
            length += 1 + (this.sessionStateDelta[i].getDataLength() < 255 ? 1 : 5) + this.sessionStateDelta[i].getDataLength();
        }
        return length;
    }

    boolean isSessionRecoverable() {
        return !this.isMasterRecoveryDisabled() && 0 == this.unRecoverableSessionStateCount.get();
    }

    boolean isMasterRecoveryDisabled() {
        return this.masterRecoveryDisabled;
    }

    void setMasterRecoveryDisabled(boolean masterRecoveryDisabled) {
        this.masterRecoveryDisabled = masterRecoveryDisabled;
    }

    byte[][] getSessionStateInitial() {
        return this.sessionStateInitial;
    }

    void setSessionStateInitial(byte[][] sessionStateInitial) {
        this.sessionStateInitial = sessionStateInitial;
    }

    SessionStateValue[] getSessionStateDelta() {
        return this.sessionStateDelta;
    }

    void setSessionStateDelta(SessionStateValue[] sessionStateDelta) {
        this.sessionStateDelta = sessionStateDelta;
    }

    String getOriginalCatalog() {
        return this.originalCatalog;
    }

    void setOriginalCatalog(String catalog) {
        this.originalCatalog = catalog;
    }

    String getOriginalLanguage() {
        return this.originalLanguage;
    }

    void setOriginalLanguage(String language) {
        this.originalLanguage = language;
    }

    SQLCollation getOriginalCollation() {
        return this.originalCollation;
    }

    void setOriginalCollation(SQLCollation collation) {
        this.originalCollation = collation;
    }

    byte getOriginalNegotiatedEncryptionLevel() {
        return this.originalNegotiatedEncryptionLevel;
    }

    void setOriginalNegotiatedEncryptionLevel(byte originalNegotiatedEncryptionLevel) {
        this.originalNegotiatedEncryptionLevel = originalNegotiatedEncryptionLevel;
    }

    boolean spResetCalled() {
        return this.resetCalled;
    }

    void setspResetCalled(boolean status) {
        this.resetCalled = status;
    }

    public void reset() {
        this.resetCalled = true;
        this.sessionStateDelta = new SessionStateValue[256];
        this.unRecoverableSessionStateCount = new AtomicInteger(0);
    }
}

