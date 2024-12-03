/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.csv;

import com.atlassian.migration.agent.service.check.csv.CheckResultCSVContainer;
import com.atlassian.migration.agent.service.check.csv.MissingAttachmentCSVBean;
import java.util.Collection;
import java.util.Collections;

public class MissingAttachmentCSVContainer
implements CheckResultCSVContainer<MissingAttachmentCSVBean> {
    private final Collection<MissingAttachmentCSVBean> beans;

    public MissingAttachmentCSVContainer() {
        this.beans = Collections.emptyList();
    }

    public MissingAttachmentCSVContainer(Collection<MissingAttachmentCSVBean> beans) {
        this.beans = beans;
    }

    @Override
    public Collection<MissingAttachmentCSVBean> beans() {
        return this.beans;
    }

    @Override
    public String[] headers() {
        return new String[]{"Space Key", "Page Id", "Attachment Id", "Attachment Name", "URL", "File path"};
    }

    @Override
    public String[] fieldMappings() {
        return new String[]{"spaceKey", "pageId", "attachmentId", "name", "url", "path"};
    }
}

