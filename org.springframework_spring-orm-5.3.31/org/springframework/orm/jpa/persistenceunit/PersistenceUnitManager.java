/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.spi.PersistenceUnitInfo
 */
package org.springframework.orm.jpa.persistenceunit;

import javax.persistence.spi.PersistenceUnitInfo;

public interface PersistenceUnitManager {
    public PersistenceUnitInfo obtainDefaultPersistenceUnitInfo() throws IllegalStateException;

    public PersistenceUnitInfo obtainPersistenceUnitInfo(String var1) throws IllegalArgumentException, IllegalStateException;
}

