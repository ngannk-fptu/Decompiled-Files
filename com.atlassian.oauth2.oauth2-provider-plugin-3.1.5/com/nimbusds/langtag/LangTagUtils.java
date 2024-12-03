/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.langtag;

import com.nimbusds.langtag.LangTag;
import com.nimbusds.langtag.LangTagException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LangTagUtils {
    public static String strip(String s) {
        if (s == null) {
            return null;
        }
        int pos = s.indexOf(35);
        if (pos < 0) {
            return s;
        }
        return s.substring(0, pos);
    }

    public static Set<String> strip(Set<String> set) {
        if (set == null) {
            return null;
        }
        HashSet<String> out = new HashSet<String>();
        for (String s : set) {
            out.add(LangTagUtils.strip(s));
        }
        return out;
    }

    public static List<String> strip(List<String> list) {
        if (list == null) {
            return null;
        }
        ArrayList<String> out = new ArrayList<String>(list.size());
        for (String s : list) {
            out.add(LangTagUtils.strip(s));
        }
        return out;
    }

    public static LangTag extract(String s) throws LangTagException {
        if (s == null) {
            return null;
        }
        int pos = s.indexOf(35);
        if (pos < 0 || s.length() < pos + 1) {
            return null;
        }
        return LangTag.parse(s.substring(pos + 1));
    }

    public static <T> Map<LangTag, T> find(String baseName, Map<String, T> map) {
        HashMap<LangTag, T> result = new HashMap<LangTag, T>();
        for (Map.Entry<String, T> entry : map.entrySet()) {
            T value;
            try {
                value = entry.getValue();
            }
            catch (ClassCastException e) {
                continue;
            }
            if (entry.getKey().equals(baseName)) {
                result.put(null, value);
                continue;
            }
            if (!entry.getKey().startsWith(baseName + '#')) continue;
            String[] parts = entry.getKey().split("#", 2);
            LangTag langTag = null;
            if (parts.length == 2) {
                try {
                    langTag = LangTag.parse(parts[1]);
                }
                catch (LangTagException langTagException) {
                    // empty catch block
                }
            }
            result.put(langTag, value);
        }
        return result;
    }

    public static List<String> toStringList(Collection<LangTag> langTags) {
        if (langTags == null) {
            return null;
        }
        ArrayList<String> out = new ArrayList<String>(langTags.size());
        for (LangTag lt : langTags) {
            out.add(lt.toString());
        }
        return out;
    }

    public static String[] toStringArray(Collection<LangTag> langTags) {
        if (langTags == null) {
            return null;
        }
        String[] out = new String[langTags.size()];
        int i = 0;
        for (LangTag lt : langTags) {
            out[i++] = lt.toString();
        }
        return out;
    }

    public static List<LangTag> parseLangTagList(Collection<String> collection) throws LangTagException {
        if (collection == null) {
            return null;
        }
        ArrayList<LangTag> out = new ArrayList<LangTag>(collection.size());
        for (String s : collection) {
            out.add(LangTag.parse(s));
        }
        return out;
    }

    public static List<LangTag> parseLangTagList(String ... values) throws LangTagException {
        if (values == null) {
            return null;
        }
        ArrayList<LangTag> out = new ArrayList<LangTag>(values.length);
        for (String s : values) {
            out.add(LangTag.parse(s));
        }
        return out;
    }

    public static LangTag[] parseLangTagArray(String ... values) throws LangTagException {
        if (values == null) {
            return null;
        }
        LangTag[] out = new LangTag[values.length];
        for (int i = 0; i < values.length; ++i) {
            out[i] = LangTag.parse(values[i]);
        }
        return out;
    }

    public static Map.Entry<String, LangTag> split(String s) throws LangTagException {
        if (s == null) {
            return null;
        }
        if ("#".equals(s)) {
            return new AbstractMap.SimpleImmutableEntry<String, Object>("#", null);
        }
        int pos = s.indexOf(35);
        if (pos < 0 || s.length() < pos + 1) {
            return new AbstractMap.SimpleImmutableEntry<String, Object>(s, null);
        }
        return new AbstractMap.SimpleImmutableEntry<String, LangTag>(s.substring(0, pos), LangTag.parse(s.substring(pos + 1)));
    }

    private LangTagUtils() {
    }
}

