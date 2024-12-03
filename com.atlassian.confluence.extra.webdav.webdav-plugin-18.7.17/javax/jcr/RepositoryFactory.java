/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import java.util.Map;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;

public interface RepositoryFactory {
    public Repository getRepository(Map var1) throws RepositoryException;
}

