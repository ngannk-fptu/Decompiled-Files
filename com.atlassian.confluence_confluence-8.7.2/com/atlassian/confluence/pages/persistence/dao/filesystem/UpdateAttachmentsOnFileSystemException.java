/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.filesystem;

import com.atlassian.confluence.event.events.exception.ConfluenceEventPropagatingException;

public class UpdateAttachmentsOnFileSystemException
extends ConfluenceEventPropagatingException {
    public UpdateAttachmentsOnFileSystemException(Throwable cause) {
        super("Failed to move the attachment data", cause);
    }
}

