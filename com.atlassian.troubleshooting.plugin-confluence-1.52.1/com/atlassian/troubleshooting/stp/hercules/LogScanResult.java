/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Ordering
 */
package com.atlassian.troubleshooting.stp.hercules;

import com.atlassian.sisyphus.LogLine;
import com.atlassian.sisyphus.RestartSisyphusPattern;
import com.atlassian.sisyphus.SisyphusPattern;
import com.atlassian.troubleshooting.stp.hercules.LogScanMatch;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class LogScanResult
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ConcurrentMap<String, LogScanMatch> matches = Maps.newConcurrentMap();

    public void add(SisyphusPattern pattern, LogLine line) {
        LogScanMatch match = (LogScanMatch)this.matches.get(pattern.getId());
        if (pattern instanceof RestartSisyphusPattern) {
            match = new LogScanMatch(null, pattern.getId(), null, null);
            UUID patternUid = UUID.randomUUID();
            this.matches.put(patternUid.toString(), match);
        } else if (match == null) {
            match = pattern.getSourceID().equals("JAC") ? new LogScanMatch(pattern.getSourceID(), pattern.getId(), pattern.getPageName(), pattern.getURL(), pattern.getPriority(), pattern.getStatus(), pattern.getResolution(), pattern.getFixVersion()) : new LogScanMatch(pattern.getSourceID(), pattern.getId(), pattern.getPageName(), pattern.getURL());
            LogScanMatch other = this.matches.putIfAbsent(pattern.getId(), match);
            if (other != null) {
                match = other;
            }
        }
        match.add(line);
    }

    public List<LogScanMatch> getMatches() {
        List orderedMatches = Ordering.natural().sortedCopy(this.matches.values());
        if (orderedMatches.size() == 1 && ((LogScanMatch)orderedMatches.iterator().next()).getPatternId().equals("restart")) {
            orderedMatches.clear();
            return orderedMatches;
        }
        return this.filterRestartsInMatches(orderedMatches);
    }

    public boolean isEmpty() {
        return this.matches.isEmpty();
    }

    public int size() {
        return this.matches.size();
    }

    private List<LogScanMatch> filterRestartsInMatches(List<LogScanMatch> orderedMatches) {
        LogScanMatch previousMatch = null;
        ArrayList<LogScanMatch> filteredMatches = new ArrayList<LogScanMatch>();
        for (LogScanMatch currentMatch : orderedMatches) {
            if (previousMatch != null) {
                if (!currentMatch.getPatternId().equals("restart") || !previousMatch.getPatternId().equals("restart")) {
                    filteredMatches.add(currentMatch);
                }
            } else {
                filteredMatches.add(currentMatch);
            }
            previousMatch = currentMatch;
        }
        return filteredMatches;
    }
}

