/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.site;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.PostExportAction;
import com.atlassian.confluence.impl.backuprestore.backup.models.AttachmentInfo;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.pages.Attachment;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ContentEntityPostExportAction
implements PostExportAction {
    private final DatabaseExporterHelper helper;
    private final boolean includeAttachments;

    public ContentEntityPostExportAction(DatabaseExporterHelper helper, boolean includeAttachments) {
        this.helper = helper;
        this.includeAttachments = includeAttachments;
    }

    @Override
    public void apply(List<EntityObjectReadyForExport> entities) throws BackupRestoreException {
        if (this.includeAttachments) {
            this.writeAttachments(entities);
        }
    }

    private void writeAttachments(Collection<EntityObjectReadyForExport> entities) throws BackupRestoreException {
        List<AttachmentInfo> attachmentInfos = entities.stream().filter(entity -> entity.getClazz().equals(Attachment.class)).map(entity -> new AttachmentInfo((EntityObjectReadyForExport)entity)).collect(Collectors.toList());
        this.helper.getContainerWriter().addAttachments(attachmentInfos);
    }
}

