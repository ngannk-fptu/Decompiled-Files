/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.digester;

import org.apache.commons.digester.RegexMatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleRegexMatcher
extends RegexMatcher {
    private static final Log baseLog = LogFactory.getLog((Class)(class$org$apache$commons$digester$SimpleRegexMatcher == null ? (class$org$apache$commons$digester$SimpleRegexMatcher = SimpleRegexMatcher.class$("org.apache.commons.digester.SimpleRegexMatcher")) : class$org$apache$commons$digester$SimpleRegexMatcher));
    private Log log = baseLog;
    static /* synthetic */ Class class$org$apache$commons$digester$SimpleRegexMatcher;

    public Log getLog() {
        return this.log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public boolean match(String basePattern, String regexPattern) {
        if (basePattern == null || regexPattern == null) {
            return false;
        }
        return this.match(basePattern, regexPattern, 0, 0);
    }

    private boolean match(String basePattern, String regexPattern, int baseAt, int regexAt) {
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("Base: " + basePattern));
            this.log.trace((Object)("Regex: " + regexPattern));
            this.log.trace((Object)("Base@" + baseAt));
            this.log.trace((Object)("Regex@" + regexAt));
        }
        if (regexAt >= regexPattern.length()) {
            return baseAt >= basePattern.length();
        }
        if (baseAt >= basePattern.length()) {
            return false;
        }
        char regexCurrent = regexPattern.charAt(regexAt);
        switch (regexCurrent) {
            case '*': {
                if (++regexAt >= regexPattern.length()) {
                    return true;
                }
                char nextRegex = regexPattern.charAt(regexAt);
                if (this.log.isTraceEnabled()) {
                    this.log.trace((Object)("Searching for next '" + nextRegex + "' char"));
                }
                int nextMatch = basePattern.indexOf(nextRegex, baseAt);
                while (nextMatch != -1) {
                    if (this.log.isTraceEnabled()) {
                        this.log.trace((Object)("Trying '*' match@" + nextMatch));
                    }
                    if (this.match(basePattern, regexPattern, nextMatch, regexAt)) {
                        return true;
                    }
                    nextMatch = basePattern.indexOf(nextRegex, nextMatch + 1);
                }
                this.log.trace((Object)"No matches found.");
                return false;
            }
            case '?': {
                return this.match(basePattern, regexPattern, ++baseAt, ++regexAt);
            }
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("Camparing " + regexCurrent + " to " + basePattern.charAt(baseAt)));
        }
        if (regexCurrent == basePattern.charAt(baseAt)) {
            return this.match(basePattern, regexPattern, ++baseAt, ++regexAt);
        }
        return false;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

