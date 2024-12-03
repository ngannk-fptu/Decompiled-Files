/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  javax.annotation.Nullable
 *  org.springframework.util.unit.DataSize
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem;

import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFile;
import com.atlassian.confluence.impl.pages.attachments.filesystem.model.AttachmentRef;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import java.util.Optional;
import javax.annotation.Nullable;
import org.springframework.util.unit.DataSize;

public interface AttachmentDataFileSystem {
    public boolean dataExistsForAttachment(AttachmentRef var1);

    @Deprecated
    public void moveAttachment(AttachmentRef var1, AttachmentRef var2, AttachmentRef.Container var3);

    public boolean saveAttachmentData(AttachmentRef var1, AttachmentDataStream var2, boolean var3, DataSize var4);

    public void deleteAllAttachmentVersions(AttachmentRef var1, AttachmentRef.Container var2);

    @Deprecated
    public void moveDataForAttachmentVersion(AttachmentRef var1, AttachmentRef var2);

    public void deleteSingleAttachmentVersion(AttachmentRef var1, AttachmentRef.Container var2);

    public void deleteSingleAttachmentVersion(AttachmentRef var1, AttachmentRef.Container var2, AttachmentDataStreamType var3);

    public AttachmentDataStream getAttachmentData(AttachmentRef var1, AttachmentDataStreamType var2);

    public AttachmentDataStream getAttachmentData(AttachmentRef var1, AttachmentDataStreamType var2, Optional<RangeRequest> var3);

    @Deprecated
    public void moveAttachments(AttachmentRef.Container var1, AttachmentRef.Space var2, AttachmentRef.Space var3);

    @Deprecated
    public void prepareForMigrationTo();

    @Deprecated(since="8.3.0")
    public AttachmentDataFile<FilesystemPath> getAttachmentDataFile(long var1, @Nullable Long var3, @Nullable Long var4, Integer var5, AttachmentDataStreamType var6);
}

