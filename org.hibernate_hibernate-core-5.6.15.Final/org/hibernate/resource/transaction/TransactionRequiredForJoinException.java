/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction;

import org.hibernate.HibernateException;

public class TransactionRequiredForJoinException
extends HibernateException {
    public TransactionRequiredForJoinException(String message) {
        super(message);
    }

    public TransactionRequiredForJoinException(String message, Throwable cause) {
        super(message, cause);
    }
}

