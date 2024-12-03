/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.macro.table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.macro.PluginLoader;
import org.radeox.macro.Repository;
import org.radeox.macro.table.Function;

public class FunctionLoader
extends PluginLoader {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$table$FunctionLoader == null ? (class$org$radeox$macro$table$FunctionLoader = FunctionLoader.class$("org.radeox.macro.table.FunctionLoader")) : class$org$radeox$macro$table$FunctionLoader));
    protected static FunctionLoader instance;
    static /* synthetic */ Class class$org$radeox$macro$table$FunctionLoader;
    static /* synthetic */ Class class$org$radeox$macro$table$Function;

    public static synchronized PluginLoader getInstance() {
        if (null == instance) {
            instance = new FunctionLoader();
        }
        return instance;
    }

    public Class getLoadClass() {
        return class$org$radeox$macro$table$Function == null ? (class$org$radeox$macro$table$Function = FunctionLoader.class$("org.radeox.macro.table.Function")) : class$org$radeox$macro$table$Function;
    }

    public void add(Repository repository, Object plugin) {
        if (plugin instanceof Function) {
            repository.put(((Function)plugin).getName().toLowerCase(), plugin);
        } else {
            log.debug((Object)("FunctionLoader: " + plugin.getClass() + " not of Type " + this.getLoadClass()));
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

