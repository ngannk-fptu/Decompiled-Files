/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.tinymceplugin.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UnmarshalMacroRequest {
    private String macroHtml;
    private Long contentId;

    public void setMacroHtml(String macroHtml) {
        this.macroHtml = macroHtml;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getMacroHtml() {
        return this.macroHtml;
    }

    public Long getContentId() {
        return this.contentId;
    }
}

