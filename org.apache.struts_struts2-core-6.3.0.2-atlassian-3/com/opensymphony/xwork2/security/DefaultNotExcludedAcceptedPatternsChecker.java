/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.security;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.security.NotExcludedAcceptedPatternsChecker;
import java.util.Set;
import java.util.regex.Pattern;

public class DefaultNotExcludedAcceptedPatternsChecker
implements NotExcludedAcceptedPatternsChecker {
    private ExcludedPatternsChecker excludedPatterns;
    private AcceptedPatternsChecker acceptedPatterns;

    @Inject
    public void setExcludedPatterns(ExcludedPatternsChecker excludedPatterns) {
        this.excludedPatterns = excludedPatterns;
    }

    @Inject
    public void setAcceptedPatterns(AcceptedPatternsChecker acceptedPatterns) {
        this.acceptedPatterns = acceptedPatterns;
    }

    @Override
    public NotExcludedAcceptedPatternsChecker.IsAllowed isAllowed(String value) {
        ExcludedPatternsChecker.IsExcluded isExcluded = this.isExcluded(value);
        if (isExcluded.isExcluded()) {
            return NotExcludedAcceptedPatternsChecker.IsAllowed.no(isExcluded.getExcludedPattern());
        }
        AcceptedPatternsChecker.IsAccepted isAccepted = this.isAccepted(value);
        if (!isAccepted.isAccepted()) {
            return NotExcludedAcceptedPatternsChecker.IsAllowed.no(isAccepted.getAcceptedPattern());
        }
        return NotExcludedAcceptedPatternsChecker.IsAllowed.yes(isAccepted.getAcceptedPattern());
    }

    @Override
    public AcceptedPatternsChecker.IsAccepted isAccepted(String value) {
        return this.acceptedPatterns.isAccepted(value);
    }

    @Override
    public void setAcceptedPatterns(String commaDelimitedPatterns) {
        this.acceptedPatterns.setAcceptedPatterns(commaDelimitedPatterns);
    }

    @Override
    public void setAcceptedPatterns(String[] patterns) {
        this.acceptedPatterns.setAcceptedPatterns(patterns);
    }

    @Override
    public void setAcceptedPatterns(Set<String> patterns) {
        this.acceptedPatterns.setAcceptedPatterns(patterns);
    }

    @Override
    public Set<Pattern> getAcceptedPatterns() {
        return this.acceptedPatterns.getAcceptedPatterns();
    }

    @Override
    public ExcludedPatternsChecker.IsExcluded isExcluded(String value) {
        return this.excludedPatterns.isExcluded(value);
    }

    @Override
    public void setExcludedPatterns(String commaDelimitedPatterns) {
        this.excludedPatterns.setExcludedPatterns(commaDelimitedPatterns);
    }

    @Override
    public void setExcludedPatterns(String[] patterns) {
        this.excludedPatterns.setExcludedPatterns(patterns);
    }

    @Override
    public void setExcludedPatterns(Set<String> patterns) {
        this.excludedPatterns.setExcludedPatterns(patterns);
    }

    @Override
    public Set<Pattern> getExcludedPatterns() {
        return this.excludedPatterns.getExcludedPatterns();
    }
}

