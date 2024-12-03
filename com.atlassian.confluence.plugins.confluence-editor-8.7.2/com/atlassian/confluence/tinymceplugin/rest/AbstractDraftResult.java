/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.Immutable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.tinymceplugin.rest;

import com.google.errorprone.annotations.Immutable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Immutable
public abstract class AbstractDraftResult {
    @XmlElement
    private final Long draftId;
    @XmlElement
    private final String time;
    @XmlElement
    private final Long pageId;

    public AbstractDraftResult() {
        this.draftId = 0L;
        this.pageId = 0L;
        this.time = null;
    }

    public AbstractDraftResult(Long draftId, Long pageId, String time) {
        this.draftId = draftId;
        this.pageId = pageId;
        this.time = time;
    }
}

