/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.extension;

import java.util.Properties;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.extension.CacheExtension;

public abstract class CacheExtensionFactory {
    public abstract CacheExtension createCacheExtension(Ehcache var1, Properties var2);
}

