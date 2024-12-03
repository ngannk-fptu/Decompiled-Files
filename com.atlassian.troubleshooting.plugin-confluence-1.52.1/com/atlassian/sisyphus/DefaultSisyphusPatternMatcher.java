/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.DefaultMatchResultVisitor;
import com.atlassian.sisyphus.LogLine;
import com.atlassian.sisyphus.MatchResultVisitor;
import com.atlassian.sisyphus.PatternMatchSet;
import com.atlassian.sisyphus.RestartSisyphusPattern;
import com.atlassian.sisyphus.SisyphusDateMatcher;
import com.atlassian.sisyphus.SisyphusLogLevelMatcher;
import com.atlassian.sisyphus.SisyphusPattern;
import com.atlassian.sisyphus.SisyphusPatternMatcher;
import com.atlassian.sisyphus.SisyphusPatternSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSisyphusPatternMatcher
implements SisyphusPatternMatcher {
    private static final Logger log = LoggerFactory.getLogger(DefaultSisyphusPatternMatcher.class);
    private static final int MAX_LINE_LENGTH = 5000;
    private final SisyphusPatternSource patternSource;
    private final SisyphusDateMatcher sisyphusDateMatcher;

    public DefaultSisyphusPatternMatcher(SisyphusPatternSource patternSource, SisyphusDateMatcher sisyphusDateMatcher) {
        this.patternSource = patternSource;
        this.sisyphusDateMatcher = sisyphusDateMatcher;
    }

    @Override
    public Map<String, PatternMatchSet> match(BufferedReader reader) throws IOException, InterruptedException {
        DefaultMatchResultVisitor visitor = new DefaultMatchResultVisitor();
        this.match(reader, visitor, null);
        return visitor.getResults();
    }

    @Override
    public void match(BufferedReader reader, MatchResultVisitor visitor, Pattern restartPattern) throws IOException, InterruptedException {
        String thisLine;
        Date latestDate = null;
        int lineCount = 1;
        int truncatedLineCount = 0;
        int maxLength = 0;
        String logLevel = "";
        while ((thisLine = reader.readLine()) != null) {
            String currentLogLevel;
            Date currentDate;
            if (thisLine.length() > 5000) {
                if (maxLength < thisLine.length()) {
                    maxLength = thisLine.length();
                }
                if (log.isDebugEnabled()) {
                    log.debug("Truncating a long line of length: " + thisLine.length() + " to " + 5000 + ". Data:\n" + thisLine + "\n");
                }
                ++truncatedLineCount;
                thisLine = thisLine.substring(0, 5000);
            }
            if ((currentDate = this.sisyphusDateMatcher.extractDate(thisLine)) != null) {
                latestDate = currentDate;
            }
            if (!(currentLogLevel = SisyphusLogLevelMatcher.extractLogLevel(thisLine)).isEmpty()) {
                logLevel = currentLogLevel;
            }
            this.matchLine(thisLine, new LogLine(lineCount, latestDate, logLevel), visitor, restartPattern);
            ++lineCount;
        }
        if (truncatedLineCount > 0) {
            log.info("Truncated " + truncatedLineCount + " lines to " + 5000 + ". Max length: " + maxLength);
        }
    }

    protected void matchLine(String thisLine, LogLine datedLine, MatchResultVisitor visitor, Pattern restartPattern) throws InterruptedException {
        Matcher restartMatcher;
        long lineTime = System.currentTimeMillis();
        for (SisyphusPattern sPattern : this.patternSource) {
            if (Thread.currentThread().isInterrupted() || visitor.isCancelled()) {
                throw new InterruptedException();
            }
            if (sPattern.isBrokenPattern()) continue;
            Pattern pat = sPattern.getPattern();
            if (pat != null) {
                long matchDuration;
                if (log.isDebugEnabled()) {
                    log.debug("Current Pattern being evaluated: " + sPattern.toString());
                }
                long matchtime = System.currentTimeMillis();
                Matcher matcher = sPattern.getMatcher();
                matcher.reset(thisLine);
                if (matcher.find()) {
                    visitor.patternMatched(thisLine, datedLine, sPattern);
                }
                if (!log.isDebugEnabled() || (matchDuration = System.currentTimeMillis() - matchtime) < 5L) continue;
                log.debug("Slow match. Time from find() method: " + matchDuration + " milliseconds. Regex being used is: '" + sPattern.getRegex() + "' and 'thisLine' is " + thisLine.length() + " characters long.");
                continue;
            }
            if (!log.isDebugEnabled()) continue;
            log.debug("Regexp would not compile and was skipped: " + sPattern.getRegex());
        }
        if (restartPattern != null && (restartMatcher = restartPattern.matcher(thisLine)).find()) {
            visitor.patternMatched(thisLine, datedLine, new RestartSisyphusPattern());
        }
        if (log.isDebugEnabled()) {
            log.debug("Time from line scan (all regexs): " + (System.currentTimeMillis() - lineTime) + " milliseconds.");
        }
    }
}

