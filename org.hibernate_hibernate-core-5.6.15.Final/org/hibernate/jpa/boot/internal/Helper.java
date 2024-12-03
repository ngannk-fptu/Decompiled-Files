/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 */
package org.hibernate.jpa.boot.internal;

import javax.persistence.PersistenceException;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;

public class Helper {
    public static PersistenceException persistenceException(PersistenceUnitDescriptor persistenceUnit, String message) {
        return Helper.persistenceException(persistenceUnit, message, null);
    }

    public static PersistenceException persistenceException(PersistenceUnitDescriptor persistenceUnit, String message, Exception cause) {
        return new PersistenceException(Helper.getExceptionHeader(persistenceUnit) + message, (Throwable)cause);
    }

    private static String getExceptionHeader(PersistenceUnitDescriptor persistenceUnit) {
        return "[PersistenceUnit: " + persistenceUnit.getName() + "] ";
    }
}

