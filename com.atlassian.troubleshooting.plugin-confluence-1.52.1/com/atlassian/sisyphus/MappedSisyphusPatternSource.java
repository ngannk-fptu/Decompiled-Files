/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.SisyphusPattern;
import com.atlassian.sisyphus.SisyphusPatternSource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class MappedSisyphusPatternSource
implements SisyphusPatternSource {
    protected Map<String, SisyphusPattern> regexMap = new HashMap<String, SisyphusPattern>();

    @Override
    public SisyphusPattern getPattern(String patternID) {
        return this.regexMap.get(patternID);
    }

    @Override
    public Iterator<SisyphusPattern> iterator() {
        return this.regexMap.values().iterator();
    }

    public Map<String, SisyphusPattern> getRegexMap() {
        return this.regexMap;
    }

    @Override
    public int size() {
        return this.regexMap.size();
    }
}

