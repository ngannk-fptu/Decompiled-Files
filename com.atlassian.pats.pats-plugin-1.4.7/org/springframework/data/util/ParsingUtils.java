/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class ParsingUtils {
    private static final String UPPER = "\\p{Lu}|\\P{InBASIC_LATIN}";
    private static final String LOWER = "\\p{Ll}";
    private static final String CAMEL_CASE_REGEX = "(?<!(^|[%u_$]))(?=[%u])|(?<!^)(?=[%u][%l])".replace("%u", "\\p{Lu}|\\P{InBASIC_LATIN}").replace("%l", "\\p{Ll}");
    private static final Pattern CAMEL_CASE = Pattern.compile(CAMEL_CASE_REGEX);

    private ParsingUtils() {
    }

    public static List<String> splitCamelCase(String source) {
        return ParsingUtils.split(source, false);
    }

    public static List<String> splitCamelCaseToLower(String source) {
        return ParsingUtils.split(source, true);
    }

    public static String reconcatenateCamelCase(String source, String delimiter) {
        Assert.notNull((Object)source, (String)"Source string must not be null!");
        Assert.notNull((Object)delimiter, (String)"Delimiter must not be null!");
        return StringUtils.collectionToDelimitedString(ParsingUtils.splitCamelCaseToLower(source), (String)delimiter);
    }

    private static List<String> split(String source, boolean toLower) {
        Assert.notNull((Object)source, (String)"Source string must not be null!");
        String[] parts = CAMEL_CASE.split(source);
        ArrayList<String> result = new ArrayList<String>(parts.length);
        for (String part : parts) {
            result.add(toLower ? part.toLowerCase() : part);
        }
        return Collections.unmodifiableList(result);
    }
}

