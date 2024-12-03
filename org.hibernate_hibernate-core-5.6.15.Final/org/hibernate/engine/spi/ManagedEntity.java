/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.Managed;

public interface ManagedEntity
extends Managed {
    public Object $$_hibernate_getEntityInstance();

    public EntityEntry $$_hibernate_getEntityEntry();

    public void $$_hibernate_setEntityEntry(EntityEntry var1);

    public ManagedEntity $$_hibernate_getPreviousManagedEntity();

    public void $$_hibernate_setPreviousManagedEntity(ManagedEntity var1);

    public ManagedEntity $$_hibernate_getNextManagedEntity();

    public void $$_hibernate_setNextManagedEntity(ManagedEntity var1);
}

