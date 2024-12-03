/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.Immutable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.tinymceplugin.rest;

import com.atlassian.confluence.tinymceplugin.rest.AbstractDraftResult;
import com.google.errorprone.annotations.Immutable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Immutable
public class DraftChangeResult
extends AbstractDraftResult {
    @XmlElement
    private final String messageKey;

    public DraftChangeResult() {
        this.messageKey = null;
    }

    public DraftChangeResult(Long draftId, Long pageId, String time) {
        super(draftId, pageId, time);
        this.messageKey = null;
    }

    public DraftChangeResult(Long draftId, Long pageId, String time, String messageKey) {
        super(draftId, pageId, time);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return this.messageKey;
    }
}

