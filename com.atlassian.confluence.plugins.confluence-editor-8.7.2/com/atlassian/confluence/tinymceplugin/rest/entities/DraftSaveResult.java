/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.Immutable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.tinymceplugin.rest.entities;

import com.google.errorprone.annotations.Immutable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Immutable
public class DraftSaveResult {
    @XmlElement
    private final String draftId;
    @XmlElement
    private final String time;
    @XmlElement
    private final String messageKey;

    public DraftSaveResult() {
        this.draftId = null;
        this.time = null;
        this.messageKey = null;
    }

    public DraftSaveResult(String draftId, String time) {
        this.draftId = draftId;
        this.time = time;
        this.messageKey = null;
    }

    public DraftSaveResult(String draftId, String time, String messageKey) {
        this.draftId = draftId;
        this.time = time;
        this.messageKey = messageKey;
    }
}

