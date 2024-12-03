/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.annotations;

import java.util.HashMap;
import java.util.Map;

public @interface ApiKeyAuthDefinition {
    public String key();

    public String description() default "";

    public ApiKeyLocation in();

    public String name();

    public static enum ApiKeyLocation {
        HEADER,
        QUERY;

        private static Map<String, ApiKeyLocation> names;

        public static ApiKeyLocation forValue(String value) {
            return names.get(value.toLowerCase());
        }

        public String toValue() {
            for (Map.Entry<String, ApiKeyLocation> entry : names.entrySet()) {
                if (entry.getValue() != this) continue;
                return entry.getKey();
            }
            return null;
        }

        static {
            names = new HashMap<String, ApiKeyLocation>();
            names.put("header", HEADER);
            names.put("query", QUERY);
        }
    }
}

