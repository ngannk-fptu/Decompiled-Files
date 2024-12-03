/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.bandana;

import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import java.util.HashMap;
import java.util.Map;

public class ConfluenceBandanaConfigMap {
    private ConfluenceBandanaContext context;
    private Map values = new HashMap();

    public ConfluenceBandanaContext getContext() {
        return this.context;
    }

    public void setContext(ConfluenceBandanaContext context) {
        this.context = context;
    }

    public void put(String key, Object configuration) {
        this.values.put(key, configuration);
    }

    public Object get(String key) {
        return this.values.get(key);
    }

    public Map getValues() {
        return this.values;
    }
}

