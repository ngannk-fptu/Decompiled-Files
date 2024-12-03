/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.radeox.macro.Repository;

public class PluginRepository
implements Repository {
    protected Map plugins = new HashMap();
    protected List list = new ArrayList();
    protected static Repository instance;

    public boolean containsKey(String key) {
        return this.plugins.containsKey(key);
    }

    public Object get(String key) {
        return this.plugins.get(key);
    }

    public List getPlugins() {
        return new ArrayList(this.plugins.values());
    }

    public void put(String key, Object value) {
        this.plugins.put(key, value);
        this.list.add(value);
    }
}

