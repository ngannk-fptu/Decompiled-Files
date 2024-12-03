/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.radeox.macro.PluginRepository;
import org.radeox.macro.Repository;
import org.radeox.macro.table.FunctionLoader;

public class FunctionRepository
extends PluginRepository {
    protected static Repository instance;
    protected List loaders = new ArrayList();

    public static synchronized Repository getInstance() {
        if (null == instance) {
            instance = new FunctionRepository();
        }
        return instance;
    }

    private void load() {
        Iterator iterator = this.loaders.iterator();
        while (iterator.hasNext()) {
            FunctionLoader loader = (FunctionLoader)iterator.next();
            loader.loadPlugins(this);
        }
    }

    private FunctionRepository() {
        this.loaders.add(new FunctionLoader());
        this.load();
    }
}

