/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool;

import java.util.ServiceLoader;
import net.sf.ehcache.pool.SizeOfEngine;
import net.sf.ehcache.pool.SizeOfEngineFactory;
import net.sf.ehcache.pool.impl.DefaultSizeOfEngine;

public final class SizeOfEngineLoader
implements SizeOfEngineFactory {
    public static final SizeOfEngineLoader INSTANCE = new SizeOfEngineLoader();
    private final ServiceLoader<SizeOfEngineFactory> loader;
    private volatile SizeOfEngineFactory factory;

    SizeOfEngineLoader(ClassLoader classLoader) {
        this.loader = ServiceLoader.load(SizeOfEngineFactory.class, classLoader);
        this.load(SizeOfEngineFactory.class, false);
    }

    private SizeOfEngineLoader() {
        this(SizeOfEngineLoader.class.getClassLoader());
    }

    public static SizeOfEngine newSizeOfEngine(int maxObjectCount, boolean abort, boolean silent) {
        return INSTANCE.createSizeOfEngine(maxObjectCount, abort, silent);
    }

    @Override
    public SizeOfEngine createSizeOfEngine(int maxObjectCount, boolean abort, boolean silent) {
        SizeOfEngineFactory currentFactory = this.factory;
        if (currentFactory != null) {
            return currentFactory.createSizeOfEngine(maxObjectCount, abort, silent);
        }
        return new DefaultSizeOfEngine(maxObjectCount, abort, silent);
    }

    public void reload() {
        this.load(SizeOfEngineFactory.class, true);
    }

    public synchronized boolean load(Class<? extends SizeOfEngineFactory> clazz, boolean reload) {
        if (reload) {
            this.loader.reload();
        }
        for (SizeOfEngineFactory sizeOfEngineFactory : this.loader) {
            if (!clazz.isAssignableFrom(sizeOfEngineFactory.getClass())) continue;
            this.factory = sizeOfEngineFactory;
            return true;
        }
        this.factory = null;
        return false;
    }
}

