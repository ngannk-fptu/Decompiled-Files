/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.StringUtils
 */
package io.micrometer.core.instrument.config;

import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.StringUtils;
import io.micrometer.core.instrument.Meter;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public interface NamingConvention {
    public static final NamingConvention identity;
    public static final NamingConvention dot;
    public static final NamingConvention snakeCase;
    public static final NamingConvention camelCase;
    public static final NamingConvention upperCamelCase;
    public static final NamingConvention slashes;

    default public String name(String name, Meter.Type type) {
        return this.name(name, type, null);
    }

    public String name(String var1, Meter.Type var2, @Nullable String var3);

    default public String tagKey(String key) {
        return key;
    }

    default public String tagValue(String value) {
        return value;
    }

    static {
        dot = identity = (name, type, baseUnit) -> name;
        snakeCase = new NamingConvention(){

            @Override
            public String name(String name, Meter.Type type, @Nullable String baseUnit) {
                return this.toSnakeCase(name);
            }

            @Override
            public String tagKey(String key) {
                return this.toSnakeCase(key);
            }

            private String toSnakeCase(String value) {
                return Arrays.stream(value.split("\\.")).filter(Objects::nonNull).collect(Collectors.joining("_"));
            }
        };
        camelCase = new NamingConvention(){

            @Override
            public String name(String name, Meter.Type type, @Nullable String baseUnit) {
                return this.toCamelCase(name);
            }

            @Override
            public String tagKey(String key) {
                return this.toCamelCase(key);
            }

            private String toCamelCase(String value) {
                String[] parts = value.split("\\.");
                StringBuilder conventionName = new StringBuilder(value.length());
                for (int i = 0; i < parts.length; ++i) {
                    String str = parts[i];
                    if (StringUtils.isEmpty((String)str)) continue;
                    if (i == 0) {
                        conventionName.append(str);
                        continue;
                    }
                    char firstChar = str.charAt(0);
                    if (Character.isUpperCase(firstChar)) {
                        conventionName.append(str);
                        continue;
                    }
                    conventionName.append(Character.toUpperCase(firstChar)).append(str, 1, str.length());
                }
                return conventionName.toString();
            }
        };
        upperCamelCase = new NamingConvention(){

            @Override
            public String name(String name, Meter.Type type, @Nullable String baseUnit) {
                return this.capitalize(camelCase.name(name, type, baseUnit));
            }

            @Override
            public String tagKey(String key) {
                return this.capitalize(camelCase.tagKey(key));
            }

            private String capitalize(String name) {
                if (name.length() == 0 || Character.isUpperCase(name.charAt(0))) {
                    return name;
                }
                char[] chars = name.toCharArray();
                chars[0] = Character.toUpperCase(chars[0]);
                return new String(chars);
            }
        };
        slashes = new NamingConvention(){

            @Override
            public String name(String name, Meter.Type type, @Nullable String baseUnit) {
                return this.toSlashes(name);
            }

            @Override
            public String tagKey(String key) {
                return this.toSlashes(key);
            }

            private String toSlashes(String value) {
                return Arrays.stream(value.split("\\.")).filter(Objects::nonNull).collect(Collectors.joining("/"));
            }
        };
    }
}

