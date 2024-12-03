/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer.radeox.macros.junit.report;

import com.atlassian.confluence.util.GeneralUtil;

public class TestCaseFailureReport {
    private String message;
    private String type;
    private String content;

    public String getContent() {
        return GeneralUtil.escapeXml(this.content);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessage() {
        return GeneralUtil.escapeXml(this.message == null ? this.content : this.message);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

