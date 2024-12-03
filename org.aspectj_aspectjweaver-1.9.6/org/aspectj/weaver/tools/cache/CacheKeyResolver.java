/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import java.util.List;
import org.aspectj.weaver.tools.cache.CachedClassReference;

public interface CacheKeyResolver {
    public CachedClassReference generatedKey(String var1);

    public CachedClassReference weavedKey(String var1, byte[] var2);

    public String keyToClass(String var1);

    public String createClassLoaderScope(ClassLoader var1, List<String> var2);

    public String getGeneratedRegex();

    public String getWeavedRegex();
}

