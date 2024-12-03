/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.macro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.macro.Macro;
import org.radeox.macro.MacroLoader;
import org.radeox.macro.PluginRepository;

public class MacroRepository
extends PluginRepository {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$MacroRepository == null ? (class$org$radeox$macro$MacroRepository = MacroRepository.class$("org.radeox.macro.MacroRepository")) : class$org$radeox$macro$MacroRepository));
    private InitialRenderContext context;
    protected static MacroRepository instance;
    protected List loaders = new ArrayList();
    static /* synthetic */ Class class$org$radeox$macro$MacroRepository;

    public static synchronized MacroRepository getInstance() {
        if (null == instance) {
            instance = new MacroRepository();
        }
        return instance;
    }

    private void initialize(InitialRenderContext context) {
        Iterator iterator = this.list.iterator();
        while (iterator.hasNext()) {
            Macro macro = (Macro)iterator.next();
            macro.setInitialContext(context);
        }
        this.init();
    }

    public void setInitialContext(InitialRenderContext context) {
        this.context = context;
        this.initialize(context);
    }

    private void init() {
        HashMap<String, Macro> newPlugins = new HashMap<String, Macro>();
        Iterator iterator = this.list.iterator();
        while (iterator.hasNext()) {
            Macro macro = (Macro)iterator.next();
            newPlugins.put(macro.getName(), macro);
        }
        this.plugins = newPlugins;
    }

    private void load() {
        Iterator iterator = this.loaders.iterator();
        while (iterator.hasNext()) {
            MacroLoader loader = (MacroLoader)iterator.next();
            loader.setRepository(this);
            log.debug((Object)("Loading from: " + loader.getClass()));
            loader.loadPlugins(this);
        }
    }

    public void addLoader(MacroLoader loader) {
        loader.setRepository(this);
        this.loaders.add(loader);
        this.plugins = new HashMap();
        this.list = new ArrayList();
        this.load();
    }

    private MacroRepository() {
        this.loaders.add(new MacroLoader());
        this.load();
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

