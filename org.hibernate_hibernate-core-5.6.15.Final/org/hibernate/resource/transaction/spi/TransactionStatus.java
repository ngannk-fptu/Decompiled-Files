/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.spi;

public enum TransactionStatus {
    NOT_ACTIVE,
    ACTIVE,
    COMMITTED,
    ROLLED_BACK,
    MARKED_ROLLBACK,
    FAILED_COMMIT,
    FAILED_ROLLBACK,
    COMMITTING,
    ROLLING_BACK;


    public boolean isOneOf(TransactionStatus ... statuses) {
        for (TransactionStatus status : statuses) {
            if (this != status) continue;
            return true;
        }
        return false;
    }

    public boolean isNotOneOf(TransactionStatus ... statuses) {
        return !this.isOneOf(statuses);
    }

    public boolean canRollback() {
        return this.isOneOf(ACTIVE, FAILED_COMMIT, MARKED_ROLLBACK);
    }
}

