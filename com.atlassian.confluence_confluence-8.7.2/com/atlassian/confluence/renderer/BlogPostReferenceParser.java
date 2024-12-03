/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.DateUtils
 */
package com.atlassian.confluence.renderer;

import com.atlassian.core.util.DateUtils;
import java.text.ParseException;
import java.util.Calendar;
import java.util.StringTokenizer;

public class BlogPostReferenceParser {
    private String postingYear;
    private String postingMonth;
    private String postingDay;
    private String entityName;

    public BlogPostReferenceParser(String reference) throws ParseException {
        this.parseBlogPostReference(reference);
    }

    protected void parseBlogPostReference(String reference) throws ParseException {
        StringTokenizer tok = new StringTokenizer(reference, "/");
        if (!tok.hasMoreTokens()) {
            throw new ParseException("Expected a blog-entry reference, but wasn't", 0);
        }
        this.postingYear = tok.nextToken();
        if (!this.numberTest(this.postingYear, 1000, 9999)) {
            throw new ParseException("Expected a blog-entry reference, but wasn't", 0);
        }
        if (!tok.hasMoreTokens()) {
            throw new ParseException("Expected a blog-entry reference, but wasn't", 0);
        }
        this.postingMonth = tok.nextToken();
        if (!this.numberTest(this.postingMonth, 1, 12)) {
            throw new ParseException("Expected a blog-entry reference, but wasn't", 0);
        }
        if (!tok.hasMoreTokens()) {
            this.entityName = null;
            return;
        }
        this.postingDay = tok.nextToken();
        if (tok.countTokens() == 0) {
            this.entityName = null;
            return;
        }
        if (tok.countTokens() != 1) {
            throw new ParseException("Expected a blog-entry link, but wasn't", 0);
        }
        this.entityName = tok.nextToken();
    }

    public Calendar getCalendarPostingDay() {
        return DateUtils.getCalendarDay((int)Integer.parseInt(this.postingYear), (int)(Integer.parseInt(this.postingMonth) - 1), (int)Integer.parseInt(this.postingDay));
    }

    private boolean numberTest(String s, int minimumValue, int maximumValue) {
        try {
            int i = Integer.parseInt(s);
            return i <= maximumValue && i >= minimumValue;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public String getPostingDay() {
        return this.postingDay;
    }

    public String getPostingYear() {
        return this.postingYear;
    }

    public String getPostingMonth() {
        return this.postingMonth;
    }

    public String getEntityName() {
        return this.entityName;
    }
}

