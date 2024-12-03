/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.tuckey.web.filters.urlrewrite.substitution;

import javax.servlet.http.HttpServletRequest;
import org.tuckey.web.filters.urlrewrite.ConditionMatch;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingMatcher;

public class SubstitutionContext {
    private HttpServletRequest hsRequest;
    private StringMatchingMatcher matcher;
    private ConditionMatch lastConditionMatch;
    private String replacePattern;

    public SubstitutionContext(HttpServletRequest hsRequest, StringMatchingMatcher matcher, ConditionMatch lastConditionMatch, String replacePattern) {
        this.hsRequest = hsRequest;
        this.matcher = matcher;
        this.lastConditionMatch = lastConditionMatch;
        this.replacePattern = replacePattern;
    }

    public HttpServletRequest getHsRequest() {
        return this.hsRequest;
    }

    public StringMatchingMatcher getMatcher() {
        return this.matcher;
    }

    public ConditionMatch getLastConditionMatch() {
        return this.lastConditionMatch;
    }

    public String getReplacePattern() {
        return this.replacePattern;
    }
}

