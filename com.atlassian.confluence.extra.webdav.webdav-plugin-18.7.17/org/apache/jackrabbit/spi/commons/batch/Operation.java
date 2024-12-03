/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.batch;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Batch;

public interface Operation {
    public void apply(Batch var1) throws RepositoryException;
}

