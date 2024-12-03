/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.spi;

import java.util.List;
import javax.persistence.spi.PersistenceProvider;

public interface PersistenceProviderResolver {
    public List<PersistenceProvider> getPersistenceProviders();

    public void clearCachedProviders();
}

