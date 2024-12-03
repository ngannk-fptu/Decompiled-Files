/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.vault.core.util;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.util.Assert;
import org.springframework.vault.core.util.PropertyTransformer;

public abstract class PropertyTransformers {
    public static PropertyTransformer noop() {
        return NoOpPropertyTransformer.instance();
    }

    public static PropertyTransformer removeNullProperties() {
        return RemoveNullProperties.instance();
    }

    public static PropertyTransformer propertyNamePrefix(String propertyNamePrefix) {
        return KeyPrefixPropertyTransformer.forPrefix(propertyNamePrefix);
    }

    static class KeyPrefixPropertyTransformer
    implements PropertyTransformer {
        private final String propertyNamePrefix;

        private KeyPrefixPropertyTransformer(String propertyNamePrefix) {
            Assert.notNull((Object)propertyNamePrefix, (String)"Property name prefix must not be null");
            this.propertyNamePrefix = propertyNamePrefix;
        }

        public static PropertyTransformer forPrefix(String propertyNamePrefix) {
            return new KeyPrefixPropertyTransformer(propertyNamePrefix);
        }

        @Override
        public Map<String, Object> transformProperties(Map<String, ? extends Object> input) {
            LinkedHashMap<String, Object> target = new LinkedHashMap<String, Object>(input.size(), 1.0f);
            for (Map.Entry<String, ? extends Object> entry : input.entrySet()) {
                target.put(this.propertyNamePrefix + entry.getKey(), entry.getValue());
            }
            return target;
        }
    }

    static class RemoveNullProperties
    implements PropertyTransformer {
        static RemoveNullProperties INSTANCE = new RemoveNullProperties();

        private RemoveNullProperties() {
        }

        public static PropertyTransformer instance() {
            return INSTANCE;
        }

        @Override
        public Map<String, Object> transformProperties(Map<String, ? extends Object> input) {
            LinkedHashMap<String, Object> target = new LinkedHashMap<String, Object>(input.size(), 1.0f);
            for (Map.Entry<String, ? extends Object> entry : input.entrySet()) {
                if (entry.getValue() == null) continue;
                target.put(entry.getKey(), entry.getValue());
            }
            return target;
        }
    }

    static class NoOpPropertyTransformer
    implements PropertyTransformer {
        static NoOpPropertyTransformer INSTANCE = new NoOpPropertyTransformer();

        private NoOpPropertyTransformer() {
        }

        public static PropertyTransformer instance() {
            return INSTANCE;
        }

        @Override
        public Map<String, Object> transformProperties(Map<String, ? extends Object> input) {
            return input;
        }
    }
}

