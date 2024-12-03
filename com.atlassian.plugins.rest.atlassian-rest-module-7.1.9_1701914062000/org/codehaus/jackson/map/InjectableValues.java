/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map;

import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationContext;

public abstract class InjectableValues {
    public abstract Object findInjectableValue(Object var1, DeserializationContext var2, BeanProperty var3, Object var4);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Std
    extends InjectableValues {
        protected final Map<String, Object> _values;

        public Std() {
            this(new HashMap<String, Object>());
        }

        public Std(Map<String, Object> values) {
            this._values = values;
        }

        public Std addValue(String key, Object value) {
            this._values.put(key, value);
            return this;
        }

        public Std addValue(Class<?> classKey, Object value) {
            this._values.put(classKey.getName(), value);
            return this;
        }

        @Override
        public Object findInjectableValue(Object valueId, DeserializationContext ctxt, BeanProperty forProperty, Object beanInstance) {
            if (!(valueId instanceof String)) {
                String type = valueId == null ? "[null]" : valueId.getClass().getName();
                throw new IllegalArgumentException("Unrecognized inject value id type (" + type + "), expecting String");
            }
            String key = (String)valueId;
            Object ob = this._values.get(key);
            if (ob == null && !this._values.containsKey(key)) {
                throw new IllegalArgumentException("No injectable id with value '" + key + "' found (for property '" + forProperty.getName() + "')");
            }
            return ob;
        }
    }
}

