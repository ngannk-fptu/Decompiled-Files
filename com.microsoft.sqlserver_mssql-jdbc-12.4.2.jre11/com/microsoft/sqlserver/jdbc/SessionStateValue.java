/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

class SessionStateValue {
    private boolean isRecoverable;
    private int sequenceNumber;
    private int dataLength;
    private byte[] data;

    SessionStateValue() {
    }

    boolean isSequenceNumberGreater(int sequenceNumberToBeCompared) {
        boolean greater = true;
        if (sequenceNumberToBeCompared > this.sequenceNumber) {
            if (sequenceNumberToBeCompared >= 0 && this.sequenceNumber < 0) {
                greater = false;
            }
        } else if (sequenceNumberToBeCompared > 0 || this.sequenceNumber < 0) {
            greater = false;
        }
        return greater;
    }

    boolean isRecoverable() {
        return this.isRecoverable;
    }

    void setRecoverable(boolean isRecoverable) {
        this.isRecoverable = isRecoverable;
    }

    int getSequenceNumber() {
        return this.sequenceNumber;
    }

    void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    int getDataLength() {
        return this.dataLength;
    }

    void setDataLengh(int dataLength) {
        this.dataLength = dataLength;
    }

    byte[] getData() {
        return this.data;
    }

    void setData(byte[] data) {
        this.data = data;
    }
}

