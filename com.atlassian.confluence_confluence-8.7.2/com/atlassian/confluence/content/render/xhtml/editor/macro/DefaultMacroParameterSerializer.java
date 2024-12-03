/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableSortedMap
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterSerializer;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultMacroParameterSerializer
implements MacroParameterSerializer {
    @Override
    public String serialize(Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }
        ImmutableSortedMap sortedParameters = ImmutableSortedMap.copyOf((Map)Maps.filterValues(parameters, (Predicate)Predicates.notNull()));
        StringBuilder result = new StringBuilder();
        for (Map.Entry entry : sortedParameters.entrySet()) {
            if (result.length() > 0) {
                result.append('|');
            }
            result.append(DefaultMacroParameterSerializer.escape((String)entry.getKey()));
            result.append('=');
            result.append(DefaultMacroParameterSerializer.escape((String)entry.getValue()));
        }
        return result.toString();
    }

    @Override
    public Map<String, String> deserialize(String encodedParameters) {
        HashMap parameters = Maps.newHashMap();
        for (String macroParameter : DefaultMacroParameterSerializer.split(encodedParameters, '|')) {
            List<String> macroParameterSplit = DefaultMacroParameterSerializer.split(macroParameter, '=');
            if (macroParameterSplit.size() != 2) continue;
            parameters.put(DefaultMacroParameterSerializer.unescape(macroParameterSplit.get(0)), DefaultMacroParameterSerializer.unescape(macroParameterSplit.get(1)));
        }
        return parameters;
    }

    private static List<String> split(String str, char splitChar) {
        ArrayList<String> result = new ArrayList<String>();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c == '\\') {
                buffer.append(c);
                if (i + 1 == str.length()) continue;
                buffer.append(str.charAt(++i));
                continue;
            }
            if (c == splitChar) {
                result.add(buffer.toString());
                buffer = new StringBuilder();
                continue;
            }
            buffer.append(c);
        }
        result.add(buffer.toString());
        return result;
    }

    private static String escape(String string) {
        return string.replaceAll("\\\\", "\\\\\\\\").replaceAll("=", "\\\\=").replaceAll("[|]", "\\\\|");
    }

    private static String unescape(String string) {
        return string.replaceAll("\\\\(.)", "$1");
    }
}

