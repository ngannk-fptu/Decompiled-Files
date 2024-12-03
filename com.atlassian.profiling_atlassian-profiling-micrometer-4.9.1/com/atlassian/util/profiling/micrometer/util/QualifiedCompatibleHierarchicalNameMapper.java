/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  io.micrometer.core.instrument.Meter$Id
 *  io.micrometer.core.instrument.config.NamingConvention
 *  io.micrometer.core.instrument.util.HierarchicalNameMapper
 *  javax.annotation.Nonnull
 */
package com.atlassian.util.profiling.micrometer.util;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.util.profiling.micrometer.util.TagComparator;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class QualifiedCompatibleHierarchicalNameMapper
implements HierarchicalNameMapper {
    @VisibleForTesting
    static final String NAME_KEY = "name";
    @VisibleForTesting
    static final String TYPE_KEY = "type";
    @VisibleForTesting
    static final String TAG_KEY_PREFIX = "tag.";
    @VisibleForTesting
    static final String METRICS_PROPERTY = "metrics";
    @VisibleForTesting
    static final String KEY_PROPERTY_PAIR_DELIMITER = ",";
    @VisibleForTesting
    static final String NAME_GROUPING_DELIMITER = ".";
    private static final String CATEGORY_BASE_KEY = "category";
    private static final String KEY_PROPERTY_SEPARATOR = "=";
    @VisibleForTesting
    static final int STARTING_COUNT = 0;
    private static final Pattern SPECIAL_CHARACTERS = Pattern.compile("[" + Stream.of(" ", "\\", "\"", "*", "?", ":", "\n", "=", ",").map(Pattern::quote).collect(Collectors.joining()) + "]");

    @Nonnull
    public String toHierarchicalName(@Nonnull Meter.Id meterId, @Nonnull NamingConvention namingConvention) {
        Objects.requireNonNull(meterId, "meterId");
        Objects.requireNonNull(namingConvention, "namingConvention");
        LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();
        properties.put(TYPE_KEY, METRICS_PROPERTY);
        QualifiedCompatibleHierarchicalNameMapper.putCategoriesAndName(meterId, properties);
        QualifiedCompatibleHierarchicalNameMapper.putTags(meterId, namingConvention, properties);
        return this.buildHierarchicalName(properties);
    }

    private String buildHierarchicalName(Map<String, String> properties) {
        return properties.entrySet().stream().map(e -> QualifiedCompatibleHierarchicalNameMapper.buildKeyProperty((String)e.getKey(), (String)e.getValue())).collect(Collectors.joining(KEY_PROPERTY_PAIR_DELIMITER));
    }

    private static String sanitize(String hierarchicalName) {
        return SPECIAL_CHARACTERS.matcher(hierarchicalName).replaceAll("_");
    }

    private static void putCategoriesAndName(Meter.Id id, Map<String, String> properties) {
        String metricName = id.getConventionName(NamingConvention.identity);
        List categories = Arrays.stream(metricName.split("\\.")).collect(Collectors.toList());
        String jmxName = (String)categories.remove(categories.size() - 1);
        int categoriesCount = 0;
        for (String category : categories) {
            properties.put(QualifiedCompatibleHierarchicalNameMapper.buildNumberedCategory(categoriesCount), category);
            ++categoriesCount;
        }
        properties.put(NAME_KEY, jmxName);
    }

    private static void putTags(Meter.Id id, NamingConvention namingConvention, Map<String, String> properties) {
        id.getConventionTags(namingConvention).stream().sorted(TagComparator.tagComparator).forEach(tag -> properties.put(TAG_KEY_PREFIX + tag.getKey(), tag.getValue()));
    }

    private static String twoDigitMinimumLeftPad(int integerToPad) {
        return String.format("%02d", integerToPad);
    }

    @VisibleForTesting
    static String buildNumberedCategory(int number) {
        return CATEGORY_BASE_KEY + QualifiedCompatibleHierarchicalNameMapper.twoDigitMinimumLeftPad(number);
    }

    @VisibleForTesting
    static String buildKeyProperty(String key, String value) {
        return QualifiedCompatibleHierarchicalNameMapper.sanitize(key) + KEY_PROPERTY_SEPARATOR + QualifiedCompatibleHierarchicalNameMapper.sanitize(value);
    }
}

