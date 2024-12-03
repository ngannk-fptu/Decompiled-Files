/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.vault.support;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class JsonMapFlattener {
    private JsonMapFlattener() {
    }

    public static Map<String, Object> flatten(Map<String, ? extends Object> inputMap) {
        Assert.notNull(inputMap, (String)"Input Map must not be null");
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>();
        JsonMapFlattener.doFlatten("", inputMap.entrySet().iterator(), resultMap, UnaryOperator.identity());
        return resultMap;
    }

    public static Map<String, String> flattenToStringMap(Map<String, ? extends Object> inputMap) {
        Assert.notNull(inputMap, (String)"Input Map must not be null");
        LinkedHashMap<String, String> resultMap = new LinkedHashMap<String, String>();
        JsonMapFlattener.doFlatten("", inputMap.entrySet().iterator(), resultMap, it -> it == null ? null : it.toString());
        return resultMap;
    }

    private static void doFlatten(String propertyPrefix, Iterator<? extends Map.Entry<String, ?>> inputMap, Map<String, ? extends Object> resultMap, Function<Object, Object> valueTransformer) {
        if (StringUtils.hasText((String)propertyPrefix)) {
            propertyPrefix = propertyPrefix + ".";
        }
        while (inputMap.hasNext()) {
            Map.Entry<String, ?> entry = inputMap.next();
            JsonMapFlattener.flattenElement(propertyPrefix.concat(entry.getKey()), entry.getValue(), resultMap, valueTransformer);
        }
    }

    private static void flattenElement(String propertyPrefix, @Nullable Object source, Map<String, ?> resultMap, Function<Object, Object> valueTransformer) {
        if (source instanceof Iterable) {
            JsonMapFlattener.flattenCollection(propertyPrefix, (Iterable)source, resultMap, valueTransformer);
            return;
        }
        if (source instanceof Map) {
            JsonMapFlattener.doFlatten(propertyPrefix, ((Map)source).entrySet().iterator(), resultMap, valueTransformer);
            return;
        }
        resultMap.put(propertyPrefix, valueTransformer.apply(source));
    }

    private static void flattenCollection(String propertyPrefix, Iterable<Object> iterable, Map<String, ?> resultMap, Function<Object, Object> valueTransformer) {
        int counter = 0;
        for (Object element : iterable) {
            JsonMapFlattener.flattenElement(propertyPrefix + "[" + counter + "]", element, resultMap, valueTransformer);
            ++counter;
        }
    }
}

