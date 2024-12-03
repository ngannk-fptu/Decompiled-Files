/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.spi;

import javax.persistence.spi.LoadState;

public interface ProviderUtil {
    public LoadState isLoadedWithoutReference(Object var1, String var2);

    public LoadState isLoadedWithReference(Object var1, String var2);

    public LoadState isLoaded(Object var1);
}

