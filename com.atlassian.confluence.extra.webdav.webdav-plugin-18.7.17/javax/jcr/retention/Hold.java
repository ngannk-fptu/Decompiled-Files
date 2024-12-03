/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.retention;

import javax.jcr.RepositoryException;

public interface Hold {
    public boolean isDeep() throws RepositoryException;

    public String getName() throws RepositoryException;
}

