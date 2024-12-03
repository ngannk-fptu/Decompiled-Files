/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.spi.PersistenceUnitInfo
 */
package org.springframework.orm.jpa.persistenceunit;

import java.util.List;
import javax.persistence.spi.PersistenceUnitInfo;

public interface SmartPersistenceUnitInfo
extends PersistenceUnitInfo {
    public List<String> getManagedPackages();

    public void setPersistenceProviderPackageName(String var1);
}

