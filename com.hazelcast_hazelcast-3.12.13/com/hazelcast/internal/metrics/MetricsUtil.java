/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.annotation.Nonnull
 */
package com.hazelcast.internal.metrics;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public final class MetricsUtil {
    private MetricsUtil() {
    }

    @Nonnull
    public static String escapeMetricNamePart(@Nonnull String namePart) {
        char ch;
        int i;
        int l = namePart.length();
        for (i = 0; i < l && (ch = namePart.charAt(i)) != ',' && ch != '=' && ch != '\\'; ++i) {
        }
        if (i == l) {
            return namePart;
        }
        StringBuilder sb = new StringBuilder(namePart.length() + 3);
        sb.append(namePart, 0, i);
        while (i < l) {
            char ch2;
            if ((ch2 = namePart.charAt(i++)) == ',' || ch2 == '=' || ch2 == '\\') {
                sb.append('\\');
            }
            sb.append(ch2);
        }
        return sb.toString();
    }

    @SuppressFBWarnings(value={"ES_COMPARING_PARAMETER_STRING_WITH_EQ"}, justification="it's intentional")
    public static boolean containsSpecialCharacters(String namePart) {
        return MetricsUtil.escapeMetricNamePart(namePart) == namePart;
    }

    @Nonnull
    public static List<Map.Entry<String, String>> parseMetricName(@Nonnull String metricName) {
        if (metricName.charAt(0) != '[' || metricName.charAt(metricName.length() - 1) != ']') {
            throw new IllegalArgumentException("key not enclosed in []: " + metricName);
        }
        StringBuilder sb = new StringBuilder();
        ArrayList<Map.Entry<String, String>> result = new ArrayList<Map.Entry<String, String>>();
        int l = metricName.length() - 1;
        String tag = null;
        boolean inTag = true;
        block4: for (int i = 1; i < l; ++i) {
            char ch = metricName.charAt(i);
            switch (ch) {
                case '=': {
                    tag = MetricsUtil.handleEqualsSign(metricName, sb, inTag);
                    inTag = false;
                    continue block4;
                }
                case ',': {
                    MetricsUtil.handleComma(metricName, sb, result, tag, inTag);
                    inTag = true;
                    tag = null;
                    continue block4;
                }
                default: {
                    if (ch == '\\') {
                        ch = metricName.charAt(++i);
                    }
                    if (i == l) {
                        throw new IllegalArgumentException("backslash at the end: " + metricName);
                    }
                    sb.append(ch);
                }
            }
        }
        if (tag != null) {
            result.add(new AbstractMap.SimpleImmutableEntry<Object, String>(tag, sb.toString()));
        } else if (sb.length() > 0) {
            throw new IllegalArgumentException("unfinished tag at the end: " + metricName);
        }
        return result;
    }

    private static void handleComma(@Nonnull String metricKey, StringBuilder sb, List<Map.Entry<String, String>> result, String tag, boolean inTag) {
        if (inTag) {
            throw new IllegalArgumentException("comma in tag: " + metricKey);
        }
        result.add(new AbstractMap.SimpleImmutableEntry<String, String>(tag, sb.toString()));
        sb.setLength(0);
    }

    private static String handleEqualsSign(@Nonnull String metricKey, StringBuilder sb, boolean inTag) {
        if (!inTag) {
            throw new IllegalArgumentException("equals sign not after tag: " + metricKey);
        }
        String tag = sb.toString();
        if (tag.length() == 0) {
            throw new IllegalArgumentException("empty tag name: " + metricKey);
        }
        sb.setLength(0);
        return tag;
    }
}

