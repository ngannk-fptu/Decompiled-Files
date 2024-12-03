/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 */
package com.atlassian.plugins.whitelist.ui;

import com.atlassian.plugins.whitelist.WhitelistType;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;

public class WhitelistTypeMapper {
    private static final Map<String, WhitelistType> whitelistTypeMap = Maps.uniqueIndex(Arrays.asList(WhitelistType.values()), (Function)new Function<WhitelistType, String>(){

        public String apply(WhitelistType whitelistType) {
            return WhitelistTypeMapper.asString(whitelistType);
        }
    });

    public static String asString(WhitelistType whitelistType) {
        Preconditions.checkNotNull((Object)whitelistType, (Object)"whitelistType");
        return "whitelist." + whitelistType.name().toLowerCase().replace('_', '.');
    }

    public static WhitelistType asType(String value) {
        Preconditions.checkNotNull((Object)value, (Object)"value");
        WhitelistType whitelistType = whitelistTypeMap.get(value);
        Preconditions.checkArgument((whitelistType != null ? 1 : 0) != 0, (Object)("Failed to find a matching whitelist type mapping for the given string '" + value + "'; known types are: " + whitelistTypeMap.keySet()));
        return whitelistType;
    }
}

