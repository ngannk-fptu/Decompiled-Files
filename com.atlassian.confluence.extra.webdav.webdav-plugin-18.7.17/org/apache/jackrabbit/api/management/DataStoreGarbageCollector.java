/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api.management;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.management.MarkEventListener;

public interface DataStoreGarbageCollector {
    public void setSleepBetweenNodes(long var1);

    public long getSleepBetweenNodes();

    public void setMarkEventListener(MarkEventListener var1);

    public void setPersistenceManagerScan(boolean var1);

    public boolean isPersistenceManagerScan();

    public void mark() throws RepositoryException;

    public int sweep() throws RepositoryException;

    public void close();
}

