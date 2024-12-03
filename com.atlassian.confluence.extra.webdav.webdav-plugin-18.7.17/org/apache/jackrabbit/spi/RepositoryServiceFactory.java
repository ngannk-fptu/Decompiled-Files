/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import java.util.Map;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.RepositoryService;

public interface RepositoryServiceFactory {
    public RepositoryService createRepositoryService(Map<?, ?> var1) throws RepositoryException;
}

