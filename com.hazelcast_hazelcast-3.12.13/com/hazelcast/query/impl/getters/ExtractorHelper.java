/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.logging.Logger;
import com.hazelcast.query.extractor.ValueExtractor;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.StringUtil;
import java.util.List;
import java.util.Map;

public final class ExtractorHelper {
    private ExtractorHelper() {
    }

    static Map<String, ValueExtractor> instantiateExtractors(List<MapAttributeConfig> mapAttributeConfigs, ClassLoader classLoader) {
        Map<String, ValueExtractor> extractors = MapUtil.createHashMap(mapAttributeConfigs.size());
        for (MapAttributeConfig config : mapAttributeConfigs) {
            if (extractors.containsKey(config.getName())) {
                throw new IllegalArgumentException("Could not add " + config + ". Extractor for this attribute name already added.");
            }
            extractors.put(config.getName(), ExtractorHelper.instantiateExtractor(config, classLoader));
        }
        return extractors;
    }

    static ValueExtractor instantiateExtractor(MapAttributeConfig config, ClassLoader classLoader) {
        ValueExtractor extractor = null;
        if (classLoader != null) {
            try {
                extractor = ExtractorHelper.instantiateExtractorWithConfigClassLoader(config, classLoader);
            }
            catch (IllegalArgumentException ex) {
                Logger.getLogger(ExtractorHelper.class).warning("Could not instantiate extractor with the config class loader", ex);
            }
        }
        if (extractor == null) {
            extractor = ExtractorHelper.instantiateExtractorWithClassForName(config);
        }
        return extractor;
    }

    private static ValueExtractor instantiateExtractorWithConfigClassLoader(MapAttributeConfig config, ClassLoader classLoader) {
        try {
            Class<?> clazz = classLoader.loadClass(config.getExtractor());
            Object extractor = clazz.newInstance();
            if (extractor instanceof ValueExtractor) {
                return (ValueExtractor)extractor;
            }
            throw new IllegalArgumentException("Extractor does not extend ValueExtractor class " + config);
        }
        catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("Could not initialize extractor " + config, ex);
        }
        catch (InstantiationException ex) {
            throw new IllegalArgumentException("Could not initialize extractor " + config, ex);
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Could not initialize extractor " + config, ex);
        }
    }

    private static ValueExtractor instantiateExtractorWithClassForName(MapAttributeConfig config) {
        try {
            Class<?> clazz = Class.forName(config.getExtractor());
            Object extractor = clazz.newInstance();
            if (extractor instanceof ValueExtractor) {
                return (ValueExtractor)extractor;
            }
            throw new IllegalArgumentException("Extractor does not extend ValueExtractor class " + config);
        }
        catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("Could not initialize extractor " + config, ex);
        }
        catch (InstantiationException ex) {
            throw new IllegalArgumentException("Could not initialize extractor " + config, ex);
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Could not initialize extractor " + config, ex);
        }
    }

    public static String extractAttributeNameNameWithoutArguments(String attributeNameWithArguments) {
        int start = StringUtil.lastIndexOf(attributeNameWithArguments, '[');
        int end = StringUtil.lastIndexOf(attributeNameWithArguments, ']');
        if (start > 0 && end > 0 && end > start) {
            return attributeNameWithArguments.substring(0, start);
        }
        if (start < 0 && end < 0) {
            return attributeNameWithArguments;
        }
        throw new IllegalArgumentException("Wrong argument input passed " + attributeNameWithArguments);
    }

    public static String extractArgumentsFromAttributeName(String attributeNameWithArguments) {
        int start = StringUtil.lastIndexOf(attributeNameWithArguments, '[');
        int end = StringUtil.lastIndexOf(attributeNameWithArguments, ']');
        if (start > 0 && end > 0 && end > start) {
            return attributeNameWithArguments.substring(start + 1, end);
        }
        if (start < 0 && end < 0) {
            return null;
        }
        throw new IllegalArgumentException("Wrong argument input passed " + attributeNameWithArguments);
    }
}

