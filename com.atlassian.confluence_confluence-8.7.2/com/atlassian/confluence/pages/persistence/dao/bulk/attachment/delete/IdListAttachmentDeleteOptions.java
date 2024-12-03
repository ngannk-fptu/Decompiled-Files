/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete;

import com.atlassian.confluence.pages.persistence.dao.bulk.DefaultBulkOptions;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.AttachmentDeleteOptions;
import java.util.List;

public class IdListAttachmentDeleteOptions
extends AttachmentDeleteOptions {
    private List<Long> ids;

    protected IdListAttachmentDeleteOptions(DefaultBulkOptions options, List<Long> ids) {
        super(options);
        this.ids = ids;
    }

    public List<Long> getIds() {
        return this.ids;
    }
}

