/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.persistence;

import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.persistence.CachePersistenceException;
import java.util.Set;

public interface PersistenceListener {
    public boolean isStored(String var1) throws CachePersistenceException;

    public boolean isGroupStored(String var1) throws CachePersistenceException;

    public void clear() throws CachePersistenceException;

    public PersistenceListener configure(Config var1);

    public void remove(String var1) throws CachePersistenceException;

    public void removeGroup(String var1) throws CachePersistenceException;

    public Object retrieve(String var1) throws CachePersistenceException;

    public void store(String var1, Object var2) throws CachePersistenceException;

    public void storeGroup(String var1, Set var2) throws CachePersistenceException;

    public Set retrieveGroup(String var1) throws CachePersistenceException;
}

