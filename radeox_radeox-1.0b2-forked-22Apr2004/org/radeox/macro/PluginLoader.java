/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.macro;

import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.macro.Repository;
import org.radeox.util.Service;

public abstract class PluginLoader {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$PluginLoader == null ? (class$org$radeox$macro$PluginLoader = PluginLoader.class$("org.radeox.macro.PluginLoader")) : class$org$radeox$macro$PluginLoader));
    protected Repository repository;
    static /* synthetic */ Class class$org$radeox$macro$PluginLoader;

    public Repository loadPlugins(Repository repository) {
        return this.loadPlugins(repository, this.getLoadClass());
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public Iterator getPlugins(Class klass) {
        return Service.providers(klass);
    }

    public Repository loadPlugins(Repository repository, Class klass) {
        if (null != repository) {
            Iterator iterator = this.getPlugins(klass);
            while (iterator.hasNext()) {
                try {
                    Object plugin = iterator.next();
                    this.add(repository, plugin);
                    log.debug((Object)("PluginLoader: Loaded plugin: " + plugin.getClass()));
                }
                catch (Exception e) {
                    log.warn((Object)"PluginLoader: unable to load plugin", (Throwable)e);
                }
            }
        }
        return repository;
    }

    public abstract void add(Repository var1, Object var2);

    public abstract Class getLoadClass();

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

