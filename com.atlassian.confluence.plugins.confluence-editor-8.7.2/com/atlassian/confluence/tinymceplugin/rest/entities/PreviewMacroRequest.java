/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.tinymceplugin.rest.entities;

import com.atlassian.confluence.tinymceplugin.rest.entities.Macro;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PreviewMacroRequest {
    private Long contentId;
    private Macro macro;

    public Long getContentId() {
        return this.contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Macro getMacro() {
        return this.macro;
    }

    public void setMacro(Macro macro) {
        this.macro = macro;
    }
}

