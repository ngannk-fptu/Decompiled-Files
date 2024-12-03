/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 */
package com.atlassian.confluence.plugins.attachment.reconciliation.marshalling;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;

@EventName(value="confluence.unknown.attachment.unmarshal")
public class UnknownAttachmentUnmarshalEvent
extends ConfluenceEvent {
    private final UnmarshalType unmarshalType;
    private final UnmarshalCase description;
    private final long ceoId;

    public UnknownAttachmentUnmarshalEvent(Object src, UnmarshalType unmarshalType, UnmarshalCase description, long ceoId) {
        super(src);
        this.unmarshalType = unmarshalType;
        this.description = description;
        this.ceoId = ceoId;
    }

    public UnmarshalType getUnmarshalType() {
        return this.unmarshalType;
    }

    public UnmarshalCase getDescription() {
        return this.description;
    }

    public long getCeoId() {
        return this.ceoId;
    }

    public static enum UnmarshalType {
        EDIT,
        STORAGE;

    }

    public static enum UnmarshalCase {
        UNMARSHAL_CASE_NO_TITLE,
        UNMARSHAL_CASE_SUCCESS,
        UNMARSHAL_CASE_SUCCESS_SINGLE_ATTACHMENT,
        UNMARSHAL_CASE_NO_MATCHING_ATTACHMENT;

    }
}

