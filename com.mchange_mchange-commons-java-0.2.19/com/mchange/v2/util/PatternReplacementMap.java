/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.util;

import com.mchange.v1.util.WrapperIterator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PatternReplacementMap {
    List mappings = new LinkedList();

    public synchronized void addMapping(Pattern pattern, String string) {
        this.mappings.add(new Mapping(pattern, string));
    }

    public synchronized void removeMapping(Pattern pattern) {
        int n = this.mappings.size();
        for (int i = 0; i < n; ++i) {
            if (!((Mapping)this.mappings.get(i)).getPattern().equals(pattern)) continue;
            this.mappings.remove(i);
        }
    }

    public synchronized Iterator patterns() {
        return new WrapperIterator(this.mappings.iterator(), true){

            @Override
            protected Object transformObject(Object object) {
                Mapping mapping = (Mapping)object;
                return mapping.getPattern();
            }
        };
    }

    public synchronized int size() {
        return this.mappings.size();
    }

    public synchronized String attemptReplace(String string) {
        String string2 = null;
        for (Mapping mapping : this.mappings) {
            Matcher matcher = mapping.getPattern().matcher(string);
            if (!matcher.matches()) continue;
            string2 = matcher.replaceAll(mapping.getReplacement());
            break;
        }
        return string2;
    }

    private static final class Mapping {
        Pattern pattern;
        String replacement;

        public Pattern getPattern() {
            return this.pattern;
        }

        public String getReplacement() {
            return this.replacement;
        }

        public Mapping(Pattern pattern, String string) {
            this.pattern = pattern;
            this.replacement = string;
        }
    }
}

