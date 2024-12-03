/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.transcoder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TranscodingHints
extends HashMap {
    public TranscodingHints() {
        this((Map)null);
    }

    public TranscodingHints(Map init) {
        super(7);
        if (init != null) {
            this.putAll(init);
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    @Override
    public Object get(Object key) {
        return super.get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        if (!((Key)key).isCompatibleValue(value)) {
            throw new IllegalArgumentException(value + " incompatible with " + key);
        }
        return super.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return super.remove(key);
    }

    public void putAll(TranscodingHints hints) {
        super.putAll(hints);
    }

    @Override
    public void putAll(Map m) {
        if (m instanceof TranscodingHints) {
            this.putAll((TranscodingHints)m);
        } else {
            Iterator iterator = m.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry o;
                Map.Entry entry = o = iterator.next();
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public static abstract class Key {
        protected Key() {
        }

        public abstract boolean isCompatibleValue(Object var1);
    }
}

