/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.templates;

import java.util.HashMap;
import java.util.Map;
import org.apache.abdera.i18n.templates.AbstractContext;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class CachingContext
extends AbstractContext {
    private Map<String, Object> cache = new HashMap<String, Object>();

    @Override
    public <T> T resolve(String var) {
        Object t = this.cache.get(var);
        if (t == null && (t = this.resolveActual(var)) != null) {
            this.cache.put(var, t);
        }
        return (T)t;
    }

    protected abstract <T> T resolveActual(String var1);

    @Override
    public void clear() {
        this.cache.clear();
    }
}

