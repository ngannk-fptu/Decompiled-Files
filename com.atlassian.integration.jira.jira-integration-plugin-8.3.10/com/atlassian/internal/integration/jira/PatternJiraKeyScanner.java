/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.internal.integration.jira;

import com.atlassian.integration.jira.JiraKeyScanner;
import com.atlassian.internal.integration.jira.util.PatternIterable;
import com.atlassian.internal.integration.jira.util.RegexUtils;
import com.google.common.collect.Multimap;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternJiraKeyScanner
implements JiraKeyScanner {
    static final Pattern DEFAULT_PATTERN = Pattern.compile("(?<=^|[a-z]\\-|[\\s\\p{Punct}&&[^\\-]])([A-Z][A-Z0-9_]*-\\d+)(?![^\\W_])", 256);
    static final String ENV_KEY_PATTERN_STRING = "integration.jira.key.pattern";
    private static final Logger log = LoggerFactory.getLogger(PatternJiraKeyScanner.class);
    private final Pattern pattern = PatternJiraKeyScanner.getJiraKeyPattern();

    @Override
    @Nonnull
    public Iterable<String> findAll(@Nonnull CharSequence text) {
        return new PatternIterable<String>(this.pattern, text, RegexUtils.group(1));
    }

    @Override
    @Nonnull
    public Multimap<String, MatchResult> findMatches(@Nonnull CharSequence text) {
        return this.findMatches(text, null);
    }

    @Override
    @Nonnull
    public Multimap<String, MatchResult> findMatches(@Nonnull CharSequence text, Pattern excludePattern) {
        return RegexUtils.findMatches(text, this.pattern, excludePattern);
    }

    private static Pattern getJiraKeyPattern() {
        Pattern pattern = DEFAULT_PATTERN;
        String patternString = System.getProperty(ENV_KEY_PATTERN_STRING);
        if (patternString == null) {
            log.debug("Using default Jira key pattern: {}", (Object)DEFAULT_PATTERN.pattern());
        } else {
            try {
                pattern = Pattern.compile(patternString);
                log.info("Using custom Jira key pattern: {}", (Object)patternString);
            }
            catch (IllegalArgumentException e) {
                log.warn("Custom Jira key pattern " + patternString + " is not valid. The default will be used instead", (Throwable)e);
            }
        }
        return pattern;
    }
}

