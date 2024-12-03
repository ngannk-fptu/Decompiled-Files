/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.engine.context;

import java.util.HashMap;
import java.util.Map;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.RenderContext;

public class BaseRenderContext
implements RenderContext {
    private boolean cacheable = true;
    private boolean tempCacheable = false;
    private RenderEngine engine;
    private Map params;
    private Map values = new HashMap();

    public Object get(String key) {
        return this.values.get(key);
    }

    public void set(String key, Object value) {
        this.values.put(key, value);
    }

    public Map getParameters() {
        return this.params;
    }

    public void setParameters(Map parameters) {
        this.params = parameters;
    }

    public RenderEngine getRenderEngine() {
        return this.engine;
    }

    public void setRenderEngine(RenderEngine engine) {
        this.engine = engine;
    }

    public void setCacheable(boolean cacheable) {
        this.tempCacheable = cacheable;
    }

    public void commitCache() {
        this.cacheable = this.cacheable && this.tempCacheable;
        this.tempCacheable = false;
    }

    public boolean isCacheable() {
        return this.cacheable;
    }
}

