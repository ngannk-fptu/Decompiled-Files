/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api.management;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.management.DataStoreGarbageCollector;

public interface RepositoryManager {
    public void stop();

    public DataStoreGarbageCollector createDataStoreGarbageCollector() throws RepositoryException;
}

