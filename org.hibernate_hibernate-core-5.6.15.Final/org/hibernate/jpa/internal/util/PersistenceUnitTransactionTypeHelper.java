/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 *  javax.persistence.spi.PersistenceUnitTransactionType
 */
package org.hibernate.jpa.internal.util;

import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitTransactionType;

public class PersistenceUnitTransactionTypeHelper {
    private PersistenceUnitTransactionTypeHelper() {
    }

    public static PersistenceUnitTransactionType interpretTransactionType(Object value) {
        if (value == null) {
            return null;
        }
        if (PersistenceUnitTransactionType.class.isInstance(value)) {
            return (PersistenceUnitTransactionType)value;
        }
        String stringValue = value.toString().trim();
        if (stringValue.isEmpty()) {
            return null;
        }
        if (stringValue.equalsIgnoreCase("JTA")) {
            return PersistenceUnitTransactionType.JTA;
        }
        if (stringValue.equalsIgnoreCase("RESOURCE_LOCAL")) {
            return PersistenceUnitTransactionType.RESOURCE_LOCAL;
        }
        throw new PersistenceException("Unknown TransactionType: '" + stringValue + '\'');
    }
}

