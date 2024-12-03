/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.legacyapi.service.Expansion
 *  com.atlassian.confluence.legacyapi.service.Expansions
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.ui.rest;

import com.atlassian.confluence.legacyapi.service.Expansion;
import com.atlassian.confluence.legacyapi.service.Expansions;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class LegacyExpansionsParser {
    public static Expansion[] parse(String expand) {
        if (StringUtils.isBlank((CharSequence)expand)) {
            return new Expansion[0];
        }
        return LegacyExpansionsParser.parse(expand.split(",")).toArray();
    }

    private static Expansions parse(String[] individualExpansions) {
        ArrayList<Expansion> expandyBits = new ArrayList<Expansion>();
        for (String individualExpansion : individualExpansions) {
            expandyBits.add(LegacyExpansionsParser.toExpansion(individualExpansion));
        }
        return new Expansions(expandyBits);
    }

    private static Expansion toExpansion(String individualExpansion) {
        String[] pathParts = individualExpansion.split("\\.");
        return LegacyExpansionsParser.toExpansion(pathParts);
    }

    private static Expansion toExpansion(String[] pathParts) {
        if (pathParts.length == 1) {
            return new Expansion(pathParts[0]);
        }
        return new Expansion(pathParts[0], new Expansions(new Expansion[]{LegacyExpansionsParser.toExpansion(LegacyExpansionsParser.rest(pathParts))}));
    }

    private static String[] rest(String[] pathParts) {
        return Arrays.copyOfRange(pathParts, 1, pathParts.length);
    }
}

