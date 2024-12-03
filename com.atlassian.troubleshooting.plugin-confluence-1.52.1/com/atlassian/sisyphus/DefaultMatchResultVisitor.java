/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.LogLine;
import com.atlassian.sisyphus.MatchResultVisitor;
import com.atlassian.sisyphus.PatternMatchSet;
import com.atlassian.sisyphus.SisyphusPattern;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultMatchResultVisitor
implements MatchResultVisitor {
    private final Map<String, PatternMatchSet> matches = new ConcurrentHashMap<String, PatternMatchSet>();
    private boolean isCancelled;

    @Override
    public void patternMatched(String line, LogLine datedLine, SisyphusPattern pattern) {
        PatternMatchSet match = this.matches.get(pattern.getId());
        if (match == null) {
            match = new PatternMatchSet(pattern);
        }
        match.lineMatched(datedLine);
        this.matches.put(pattern.getId(), match);
    }

    public Map<String, PatternMatchSet> getResults() {
        return this.matches;
    }

    public void setCancelled() {
        this.isCancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }
}

