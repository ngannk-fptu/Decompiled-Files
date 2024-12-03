/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sisyphus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SisyphusPattern {
    private static final Logger log = LoggerFactory.getLogger(SisyphusPattern.class);
    private String pageName;
    private String regex;
    private String URL;
    private String Id;
    private String sourceID;
    private String priority;
    private String status;
    private String resolution;
    private String fixVersion;
    private transient Pattern pattern = null;
    private transient boolean isBrokenPattern = false;
    private transient ThreadLocal<Matcher> matcher;

    public SisyphusPattern() {
    }

    public SisyphusPattern(String id) {
        this.Id = id;
    }

    public SisyphusPattern(SisyphusPattern other) {
        this.setId(other.getId());
        this.setPageName(other.getPageName());
        this.setURL(other.getURL());
        this.setRegex(other.getRegex());
    }

    public String getPageName() {
        return this.pageName;
    }

    public String getRegex() {
        return this.regex;
    }

    public String getURL() {
        return this.URL;
    }

    public String getId() {
        return this.Id;
    }

    public String getPriority() {
        return this.priority;
    }

    public String getStatus() {
        return this.status;
    }

    public String getResolution() {
        return this.resolution;
    }

    public String getFixVersion() {
        return this.fixVersion;
    }

    public void setPageName(String pn) {
        this.pageName = pn;
    }

    public void setRegex(String r) {
        this.regex = r;
        this.compile();
    }

    private void compile() {
        if (StringUtils.isEmpty((CharSequence)this.regex)) {
            this.isBrokenPattern = true;
            return;
        }
        try {
            this.pattern = Pattern.compile(this.regex);
            if (this.matcher == null) {
                this.matcher = new ThreadLocal();
            }
            this.matcher.set(this.pattern.matcher(""));
            this.isBrokenPattern = this.regex.equals("") || this.regex.equals("$body");
        }
        catch (PatternSyntaxException e) {
            this.isBrokenPattern = true;
            log.error("Failed to compile pattern '" + this.getPageName() + "' at " + this.getURL(), (Throwable)e);
        }
    }

    public Pattern getPattern() {
        if (this.isBrokenPattern) {
            return null;
        }
        if (this.pattern == null) {
            this.compile();
        }
        return this.pattern;
    }

    public Matcher getMatcher() {
        if (this.matcher != null && this.matcher.get() != null) {
            return this.matcher.get();
        }
        this.compile();
        return this.matcher.get();
    }

    public boolean isBrokenPattern() {
        return this.isBrokenPattern;
    }

    public void setURL(String u) {
        this.URL = u;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setFixVersion(String fixVersion) {
        this.fixVersion = fixVersion;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public String getSourceID() {
        return this.sourceID;
    }

    public boolean equals(Object obj) {
        SisyphusPattern other = (SisyphusPattern)obj;
        if (!other.Id.equals(this.Id)) {
            return false;
        }
        if (!other.pageName.equals(this.pageName)) {
            return false;
        }
        if (!other.URL.equals(this.URL)) {
            return false;
        }
        return other.regex.equals(this.regex);
    }
}

