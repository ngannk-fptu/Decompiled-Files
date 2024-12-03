/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.model;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

@ExperimentalApi
public class Expansions {
    private static final Pattern KEY_MATCH_PATTERN = Pattern.compile("^(.+?)(\\[.*?\\])?$");
    public static final Expansions EMPTY = new Expansions(Collections.emptyMap());
    private final Map<String, Expansions> expansionsMap;

    public Expansions(Expansion ... expansions) {
        this(Expansions.merge(new HashMap<String, Expansions>(), expansions));
    }

    public Expansions(Iterable<Expansion> expansions) {
        this(Expansions.toArray(expansions));
    }

    private static Expansion[] toArray(Iterable<Expansion> expansions) {
        return (Expansion[])StreamSupport.stream(expansions.spliterator(), false).toArray(Expansion[]::new);
    }

    public static Expansions of(String ... expansionStr) {
        Expansion[] array = (Expansion[])Arrays.stream(expansionStr).map(Expansion::new).toArray(Expansion[]::new);
        return new Expansions(array);
    }

    public Expansions merge(Expansions expansions) {
        return new Expansions(Expansions.merge(this.expansionsMap, expansions.toArray()));
    }

    public Expansion[] merge(Expansion[] expansions) {
        return new Expansions(Expansions.merge(this.expansionsMap, expansions)).toArray();
    }

    private Expansions(Map<String, Expansions> expansionsMap) {
        this.expansionsMap = Collections.unmodifiableMap(expansionsMap);
    }

    private static Map<String, Expansions> merge(Map<String, Expansions> existingMap, Expansion ... incomingExpansions) {
        HashMap<String, Expansions> mutableExistingMap = new HashMap<String, Expansions>(existingMap);
        for (Expansion incomingExpansion : incomingExpansions) {
            Expansions incomingSubExpansions;
            Expansions mergedSubExpansions = incomingSubExpansions = incomingExpansion.getSubExpansions();
            String name = incomingExpansion.getPropertyName();
            Expansions existingSubExpansions = (Expansions)mutableExistingMap.get(name);
            if (existingSubExpansions != null) {
                Map<String, Expansions> mergedMap = Expansions.merge(existingSubExpansions.expansionsMap, incomingSubExpansions.toArray());
                mergedSubExpansions = new Expansions(mergedMap);
                mutableExistingMap.put(Expansions.encode(name), mergedSubExpansions);
            }
            mutableExistingMap.put(Expansions.encode(name), mergedSubExpansions);
        }
        return mutableExistingMap;
    }

    public boolean canExpand(String propertyName) {
        if (this.expansionsMap.containsKey(propertyName)) {
            return true;
        }
        return this.expansionsMap.containsKey(Expansions.encode(propertyName));
    }

    public Expansions getSubExpansions(String propertyName) {
        if (this.canExpand(propertyName)) {
            if (this.expansionsMap.containsKey(propertyName)) {
                return this.expansionsMap.get(propertyName);
            }
            return this.expansionsMap.get(Expansions.encode(propertyName));
        }
        return EMPTY;
    }

    public Expansions prepend(String propertyName) {
        return new Expansions(new Expansion(propertyName, this));
    }

    public Expansion[] toArray() {
        Expansion[] array = new Expansion[this.expansionsMap.size()];
        int i = 0;
        for (Map.Entry<String, Expansions> stringExpansionsEntry : this.expansionsMap.entrySet()) {
            array[i++] = new Expansion(stringExpansionsEntry.getKey(), stringExpansionsEntry.getValue());
        }
        return array;
    }

    public boolean isEmpty() {
        return this.expansionsMap.isEmpty();
    }

    public String toString() {
        return "Expansions{expansionsMap=" + this.expansionsMap + '}';
    }

    public void checkRecursiveExpansion(String expansion) {
        if (this.canExpand(expansion)) {
            throw new IllegalArgumentException("Cannot recursively expand : " + expansion);
        }
        for (Expansions exp : this.expansionsMap.values()) {
            exp.checkRecursiveExpansion(expansion);
        }
    }

    public static String encode(String expand) {
        if (expand == null) {
            return null;
        }
        Matcher matcher = KEY_MATCH_PATTERN.matcher(expand);
        if (matcher.matches()) {
            String expandName = matcher.group(1);
            String indexes = matcher.group(2);
            if (indexes == null) {
                return Expansions.encodeExpandName(expandName);
            }
            return Expansions.encodeExpandName(expandName) + indexes;
        }
        return expand;
    }

    private static String encodeExpandName(String expandName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expandName.length(); ++i) {
            char c = expandName.charAt(i);
            if ('a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || '0' <= c && c <= '9' || c == '_' || c == '*') {
                sb.append(c);
                continue;
            }
            if (c == ':') {
                sb.append("__");
                continue;
            }
            sb.append("_");
        }
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Expansions that = (Expansions)o;
        return Objects.equals(this.expansionsMap, that.expansionsMap);
    }

    public int hashCode() {
        return Objects.hash(this.expansionsMap);
    }
}

