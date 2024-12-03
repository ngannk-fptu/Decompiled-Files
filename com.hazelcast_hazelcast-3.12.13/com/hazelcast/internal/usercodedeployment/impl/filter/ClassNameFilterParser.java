/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment.impl.filter;

import com.hazelcast.config.UserCodeDeploymentConfig;
import com.hazelcast.internal.usercodedeployment.impl.filter.ClassBlacklistFilter;
import com.hazelcast.internal.usercodedeployment.impl.filter.ClassWhitelistFilter;
import com.hazelcast.internal.util.filter.AndFilter;
import com.hazelcast.internal.util.filter.Filter;
import com.hazelcast.util.SetUtil;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ClassNameFilterParser {
    private static final String[] BUILTIN_BLACKLIST_PREFIXES = new String[]{"javax.", "java.", "sun.", "com.hazelcast."};

    private ClassNameFilterParser() {
    }

    public static Filter<String> parseClassNameFilters(UserCodeDeploymentConfig config) {
        Filter<String> classFilter = ClassNameFilterParser.parseBlackList(config);
        String whitelistedPrefixes = config.getWhitelistedPrefixes();
        Set<String> whitelistSet = ClassNameFilterParser.parsePrefixes(whitelistedPrefixes);
        if (!whitelistSet.isEmpty()) {
            ClassWhitelistFilter whitelistFilter = new ClassWhitelistFilter(whitelistSet.toArray(new String[0]));
            classFilter = new AndFilter<String>(classFilter, whitelistFilter);
        }
        return classFilter;
    }

    private static Filter<String> parseBlackList(UserCodeDeploymentConfig config) {
        String blacklistedPrefixes = config.getBlacklistedPrefixes();
        Set<String> blacklistSet = ClassNameFilterParser.parsePrefixes(blacklistedPrefixes);
        blacklistSet.addAll(Arrays.asList(BUILTIN_BLACKLIST_PREFIXES));
        return new ClassBlacklistFilter(blacklistSet.toArray(new String[0]));
    }

    private static Set<String> parsePrefixes(String prefixes) {
        if (prefixes == null) {
            return new HashSet<String>();
        }
        prefixes = prefixes.trim();
        String[] prefixArray = prefixes.split(",");
        Set<String> blacklistSet = SetUtil.createHashSet(prefixArray.length + BUILTIN_BLACKLIST_PREFIXES.length);
        for (String prefix : prefixArray) {
            blacklistSet.add(prefix.trim());
        }
        return blacklistSet;
    }
}

