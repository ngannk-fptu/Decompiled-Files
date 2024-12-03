/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.macro;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.macro.Macro;
import org.radeox.macro.PluginLoader;
import org.radeox.macro.Repository;

public class MacroLoader
extends PluginLoader {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$MacroLoader == null ? (class$org$radeox$macro$MacroLoader = MacroLoader.class$("org.radeox.macro.MacroLoader")) : class$org$radeox$macro$MacroLoader));
    static /* synthetic */ Class class$org$radeox$macro$MacroLoader;
    static /* synthetic */ Class class$org$radeox$macro$Macro;

    public Class getLoadClass() {
        return class$org$radeox$macro$Macro == null ? (class$org$radeox$macro$Macro = MacroLoader.class$("org.radeox.macro.Macro")) : class$org$radeox$macro$Macro;
    }

    public void add(Repository repository, Object plugin) {
        if (plugin instanceof Macro) {
            repository.put(((Macro)plugin).getName(), plugin);
        } else {
            log.debug((Object)("MacroLoader: " + plugin.getClass() + " not of Type " + this.getLoadClass()));
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

