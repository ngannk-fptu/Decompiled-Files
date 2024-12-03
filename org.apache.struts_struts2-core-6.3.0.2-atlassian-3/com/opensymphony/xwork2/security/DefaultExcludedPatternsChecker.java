/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.security;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.util.TextParseUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultExcludedPatternsChecker
implements ExcludedPatternsChecker {
    private static final Logger LOG = LogManager.getLogger(DefaultExcludedPatternsChecker.class);
    public static final String[] EXCLUDED_PATTERNS = new String[]{"(^|\\%\\{)((#?)(top(\\.|\\['|\\[\")|\\[\\d\\]\\.)?)(dojo|struts|session|request|response|application|servlet(Request|Response|Context)|parameters|context|_memberAccess)(\\.|\\[).*", ".*(^|\\.|\\[|\\'|\"|get)class(\\(\\.|\\[|\\'|\").*", "actionErrors|actionMessages|fieldErrors"};
    private Set<Pattern> excludedPatterns;

    public DefaultExcludedPatternsChecker() {
        this.setExcludedPatterns(EXCLUDED_PATTERNS);
    }

    @Inject(value="struts.override.excludedPatterns", required=false)
    protected void setOverrideExcludePatterns(String excludePatterns) {
        this.setExcludedPatterns(excludePatterns);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Inject(value="struts.additional.excludedPatterns", required=false)
    public void setAdditionalExcludePatterns(String excludePatterns) {
        LOG.debug("Adding additional global patterns [{}] to excluded patterns!", (Object)excludePatterns);
        this.excludedPatterns = new HashSet<Pattern>(this.excludedPatterns);
        try {
            for (String pattern : TextParseUtil.commaDelimitedStringToSet(excludePatterns)) {
                this.excludedPatterns.add(Pattern.compile(pattern, 2));
            }
        }
        finally {
            this.excludedPatterns = Collections.unmodifiableSet(this.excludedPatterns);
        }
    }

    @Inject(value="struts.enable.DynamicMethodInvocation")
    protected void setDynamicMethodInvocation(String dmiValue) {
        if (!BooleanUtils.toBoolean((String)dmiValue)) {
            LOG.debug("DMI is disabled, adding DMI related excluded patterns");
            this.setAdditionalExcludePatterns("^(action|method):.*");
        }
    }

    @Override
    public void setExcludedPatterns(String commaDelimitedPatterns) {
        this.setExcludedPatterns(TextParseUtil.commaDelimitedStringToSet(commaDelimitedPatterns));
    }

    @Override
    public void setExcludedPatterns(String[] patterns) {
        this.setExcludedPatterns(new HashSet<String>(Arrays.asList(patterns)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setExcludedPatterns(Set<String> patterns) {
        if (this.excludedPatterns != null && !this.excludedPatterns.isEmpty()) {
            LOG.warn("Replacing excluded patterns [{}] with [{}], be aware that this affects all instances and safety of your application!", this.excludedPatterns, patterns);
        } else {
            LOG.debug("Sets excluded patterns to [{}]", patterns);
        }
        this.excludedPatterns = new HashSet<Pattern>(patterns.size());
        try {
            for (String pattern : patterns) {
                this.excludedPatterns.add(Pattern.compile(pattern, 2));
            }
        }
        finally {
            this.excludedPatterns = Collections.unmodifiableSet(this.excludedPatterns);
        }
    }

    @Override
    public ExcludedPatternsChecker.IsExcluded isExcluded(String value) {
        for (Pattern excludedPattern : this.excludedPatterns) {
            if (!excludedPattern.matcher(value).matches()) continue;
            LOG.trace("[{}] matches excluded pattern [{}]", (Object)value, (Object)excludedPattern);
            return ExcludedPatternsChecker.IsExcluded.yes(excludedPattern);
        }
        return ExcludedPatternsChecker.IsExcluded.no(this.excludedPatterns);
    }

    @Override
    public Set<Pattern> getExcludedPatterns() {
        return this.excludedPatterns;
    }
}

