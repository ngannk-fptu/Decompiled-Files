/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Constants
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package org.springframework.jdbc.datasource.lookup;

import org.springframework.core.Constants;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class IsolationLevelDataSourceRouter
extends AbstractRoutingDataSource {
    private static final Constants constants = new Constants(TransactionDefinition.class);

    @Override
    protected Object resolveSpecifiedLookupKey(Object lookupKey) {
        if (lookupKey instanceof Integer) {
            return lookupKey;
        }
        if (lookupKey instanceof String) {
            String constantName = (String)lookupKey;
            if (!constantName.startsWith("ISOLATION_")) {
                throw new IllegalArgumentException("Only isolation constants allowed");
            }
            return constants.asNumber(constantName);
        }
        throw new IllegalArgumentException("Invalid lookup key - needs to be isolation level Integer or isolation level name String: " + lookupKey);
    }

    @Override
    @Nullable
    protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
    }
}

