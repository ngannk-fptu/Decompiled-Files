/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import java.beans.PropertyChangeListener;
import net.java.ao.EntityManager;
import net.java.ao.schema.Ignore;

public interface RawEntity<T> {
    @Ignore
    public void init();

    @Ignore
    public void save();

    @Ignore
    public EntityManager getEntityManager();

    @Ignore
    public <X extends RawEntity<T>> Class<X> getEntityType();

    @Ignore
    public void addPropertyChangeListener(PropertyChangeListener var1);

    @Ignore
    public void removePropertyChangeListener(PropertyChangeListener var1);
}

