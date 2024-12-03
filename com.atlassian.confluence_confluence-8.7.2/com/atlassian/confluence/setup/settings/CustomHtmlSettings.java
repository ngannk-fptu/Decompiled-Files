/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import java.io.Serializable;

public class CustomHtmlSettings
implements Serializable {
    private static final long serialVersionUID = -7156924092264809386L;
    private String beforeHeadEnd;
    private String afterBodyStart;
    private String beforeBodyEnd;

    public CustomHtmlSettings() {
        this("", "", "");
    }

    public CustomHtmlSettings(String beforeHeadEnd, String afterBodyStart, String beforeBodyEnd) {
        this.beforeHeadEnd = beforeHeadEnd;
        this.afterBodyStart = afterBodyStart;
        this.beforeBodyEnd = beforeBodyEnd;
    }

    public CustomHtmlSettings(CustomHtmlSettings other) {
        this(other.beforeHeadEnd, other.afterBodyStart, other.beforeBodyEnd);
    }

    @HtmlSafe
    public String getBeforeHeadEnd() {
        return this.beforeHeadEnd;
    }

    @HtmlSafe
    public String getAfterBodyStart() {
        return this.afterBodyStart;
    }

    @HtmlSafe
    public String getBeforeBodyEnd() {
        return this.beforeBodyEnd;
    }

    public void setBeforeHeadEnd(String beforeHeadEnd) {
        this.beforeHeadEnd = beforeHeadEnd;
    }

    public void setAfterBodyStart(String afterBodyStart) {
        this.afterBodyStart = afterBodyStart;
    }

    public void setBeforeBodyEnd(String beforeBodyEnd) {
        this.beforeBodyEnd = beforeBodyEnd;
    }
}

