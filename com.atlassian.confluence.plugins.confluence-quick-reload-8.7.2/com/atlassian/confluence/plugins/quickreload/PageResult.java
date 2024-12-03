/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.quickreload;

import com.atlassian.confluence.plugins.quickreload.Commenter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PageResult {
    @XmlElement
    private Commenter editor;

    private PageResult() {
    }

    public PageResult(Commenter editor) {
        this.editor = editor;
    }
}

