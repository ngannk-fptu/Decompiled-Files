/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer;

import java.util.Properties;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.writer.CacheWriter;

public abstract class CacheWriterFactory {
    public abstract CacheWriter createCacheWriter(Ehcache var1, Properties var2);
}

