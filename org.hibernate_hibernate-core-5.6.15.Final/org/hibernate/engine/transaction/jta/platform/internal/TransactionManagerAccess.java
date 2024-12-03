/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import java.io.Serializable;
import javax.transaction.TransactionManager;

public interface TransactionManagerAccess
extends Serializable {
    public TransactionManager getTransactionManager();
}

