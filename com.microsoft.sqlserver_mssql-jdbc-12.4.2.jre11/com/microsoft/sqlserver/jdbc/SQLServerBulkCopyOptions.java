/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.Serializable;
import java.text.MessageFormat;

public class SQLServerBulkCopyOptions
implements Serializable {
    private static final long serialVersionUID = 711570696894155194L;
    private int batchSize = 0;
    private int bulkCopyTimeout = 60;
    private boolean checkConstraints = false;
    private boolean fireTriggers = false;
    private boolean keepIdentity = false;
    private boolean keepNulls = false;
    private boolean tableLock = false;
    private boolean useInternalTransaction = false;
    private boolean allowEncryptedValueModifications = false;

    public int getBatchSize() {
        return this.batchSize;
    }

    public void setBatchSize(int batchSize) throws SQLServerException {
        if (batchSize >= 0) {
            this.batchSize = batchSize;
        } else {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidNegativeArg"));
            Object[] msgArgs = new Object[]{"batchSize"};
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, false);
        }
    }

    public int getBulkCopyTimeout() {
        return this.bulkCopyTimeout;
    }

    public void setBulkCopyTimeout(int timeout) throws SQLServerException {
        if (timeout >= 0) {
            this.bulkCopyTimeout = timeout;
        } else {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidNegativeArg"));
            Object[] msgArgs = new Object[]{"timeout"};
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, false);
        }
    }

    public boolean isKeepIdentity() {
        return this.keepIdentity;
    }

    public void setKeepIdentity(boolean keepIdentity) {
        this.keepIdentity = keepIdentity;
    }

    public boolean isKeepNulls() {
        return this.keepNulls;
    }

    public void setKeepNulls(boolean keepNulls) {
        this.keepNulls = keepNulls;
    }

    public boolean isTableLock() {
        return this.tableLock;
    }

    public void setTableLock(boolean tableLock) {
        this.tableLock = tableLock;
    }

    public boolean isUseInternalTransaction() {
        return this.useInternalTransaction;
    }

    public void setUseInternalTransaction(boolean useInternalTransaction) {
        this.useInternalTransaction = useInternalTransaction;
    }

    public boolean isCheckConstraints() {
        return this.checkConstraints;
    }

    public void setCheckConstraints(boolean checkConstraints) {
        this.checkConstraints = checkConstraints;
    }

    public boolean isFireTriggers() {
        return this.fireTriggers;
    }

    public void setFireTriggers(boolean fireTriggers) {
        this.fireTriggers = fireTriggers;
    }

    public boolean isAllowEncryptedValueModifications() {
        return this.allowEncryptedValueModifications;
    }

    public void setAllowEncryptedValueModifications(boolean allowEncryptedValueModifications) {
        this.allowEncryptedValueModifications = allowEncryptedValueModifications;
    }
}

