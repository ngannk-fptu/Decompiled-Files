/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.backend.jta.internal;

import org.hibernate.TransactionException;
import org.hibernate.resource.transaction.spi.TransactionStatus;

public class StatusTranslator {
    public static TransactionStatus translate(int status) {
        TransactionStatus transactionStatus = null;
        switch (status) {
            case 0: {
                transactionStatus = TransactionStatus.ACTIVE;
                break;
            }
            case 2: {
                transactionStatus = TransactionStatus.ACTIVE;
                break;
            }
            case 7: {
                transactionStatus = TransactionStatus.ACTIVE;
                break;
            }
            case 8: {
                transactionStatus = TransactionStatus.COMMITTING;
                break;
            }
            case 9: {
                transactionStatus = TransactionStatus.ROLLING_BACK;
                break;
            }
            case 6: {
                transactionStatus = TransactionStatus.NOT_ACTIVE;
                break;
            }
            case 3: {
                transactionStatus = TransactionStatus.COMMITTED;
                break;
            }
            case 4: {
                transactionStatus = TransactionStatus.ROLLED_BACK;
                break;
            }
            case 1: {
                transactionStatus = TransactionStatus.MARKED_ROLLBACK;
                break;
            }
        }
        if (transactionStatus == null) {
            throw new TransactionException("TransactionManager reported transaction status as unknwon");
        }
        return transactionStatus;
    }
}

