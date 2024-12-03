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
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.util.TextParseUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultAcceptedPatternsChecker
implements AcceptedPatternsChecker {
    private static final Logger LOG = LogManager.getLogger(DefaultAcceptedPatternsChecker.class);
    public static final String[] ACCEPTED_PATTERNS = new String[]{"\\w+((\\.\\w+)|(\\[\\d+])|(\\(\\d+\\))|(\\['(\\w-?|[\\u4e00-\\u9fa5]-?)+'])|(\\('(\\w-?|[\\u4e00-\\u9fa5]-?)+'\\)))*"};
    public static final String[] DMI_AWARE_ACCEPTED_PATTERNS = new String[]{"\\w+([:]?\\w+)?((\\.\\w+)|(\\[\\d+])|(\\(\\d+\\))|(\\['(\\w-?|[\\u4e00-\\u9fa5]-?)+'])|(\\('(\\w-?|[\\u4e00-\\u9fa5]-?)+'\\)))*([!]?\\w+)?"};
    protected Set<Pattern> acceptedPatterns;

    public DefaultAcceptedPatternsChecker() {
        this.setAcceptedPatterns(ACCEPTED_PATTERNS);
    }

    public DefaultAcceptedPatternsChecker(@Inject(value="struts.enable.DynamicMethodInvocation", required=false) String dmiValue) {
        if (BooleanUtils.toBoolean((String)dmiValue)) {
            LOG.debug("DMI is enabled, adding DMI related accepted patterns");
            this.setAcceptedPatterns(DMI_AWARE_ACCEPTED_PATTERNS);
        } else {
            this.setAcceptedPatterns(ACCEPTED_PATTERNS);
        }
    }

    @Inject(value="struts.override.acceptedPatterns", required=false)
    protected void setOverrideAcceptedPatterns(String acceptablePatterns) {
        this.setAcceptedPatterns(acceptablePatterns);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Inject(value="struts.additional.acceptedPatterns", required=false)
    protected void setAdditionalAcceptedPatterns(String acceptablePatterns) {
        LOG.warn("Adding additional global patterns [{}] to accepted patterns!", (Object)acceptablePatterns);
        HashSet<Pattern> newAcceptedPatterns = new HashSet<Pattern>(this.acceptedPatterns);
        try {
            for (String pattern : TextParseUtil.commaDelimitedStringToSet(acceptablePatterns)) {
                newAcceptedPatterns.add(Pattern.compile(pattern, 2));
            }
        }
        finally {
            this.acceptedPatterns = Collections.unmodifiableSet(newAcceptedPatterns);
        }
    }

    @Override
    public void setAcceptedPatterns(String commaDelimitedPatterns) {
        this.setAcceptedPatterns(TextParseUtil.commaDelimitedStringToSet(commaDelimitedPatterns));
    }

    @Override
    public void setAcceptedPatterns(String[] additionalPatterns) {
        this.setAcceptedPatterns(new HashSet<String>(Arrays.asList(additionalPatterns)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAcceptedPatterns(Set<String> patterns) {
        this.logPatternChange(patterns);
        HashSet<Pattern> newAcceptedPatterns = new HashSet<Pattern>(patterns.size());
        try {
            for (String pattern : patterns) {
                newAcceptedPatterns.add(Pattern.compile(pattern, 2));
            }
        }
        finally {
            this.acceptedPatterns = Collections.unmodifiableSet(newAcceptedPatterns);
        }
    }

    protected void logPatternChange(Set<String> newPatterns) {
        if (this.acceptedPatterns == null) {
            LOG.debug("Sets accepted patterns to [{}], note this impacts the safety of your application!", newPatterns);
        } else {
            LOG.warn("Replacing accepted patterns [{}] with [{}], be aware that this affects all instances and safety of your application!", this.acceptedPatterns, newPatterns);
        }
    }

    @Override
    public AcceptedPatternsChecker.IsAccepted isAccepted(String value) {
        for (Pattern acceptedPattern : this.acceptedPatterns) {
            if (!acceptedPattern.matcher(value).matches()) continue;
            LOG.trace("[{}] matches accepted pattern [{}]", (Object)value, (Object)acceptedPattern);
            return AcceptedPatternsChecker.IsAccepted.yes(acceptedPattern.toString());
        }
        return AcceptedPatternsChecker.IsAccepted.no(this.acceptedPatterns.toString());
    }

    @Override
    public Set<Pattern> getAcceptedPatterns() {
        return this.acceptedPatterns;
    }
}

