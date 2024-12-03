/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.plugins.conversion.api.ConversionManager
 *  com.atlassian.confluence.plugins.conversion.api.ConversionType
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.files.manager;

import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugins.conversion.api.ConversionManager;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.confluence.plugins.files.api.SavableContent;
import com.atlassian.confluence.plugins.files.entities.FileContentEntity;
import com.atlassian.confluence.plugins.files.entities.FileVersionSummaryEntity;
import com.atlassian.confluence.plugins.files.manager.ConfluenceFileManager;
import com.atlassian.confluence.plugins.files.manager.QueryHelper;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@ExportAsService(value={ConfluenceFileManager.class})
@Component
public class DefaultConfluenceFileManager
implements ConfluenceFileManager {
    private final AttachmentManager attachmentManager;
    private final ContentEntityManager contentEntityManager;
    private final CustomContentManager customContentManager;
    private final TransactionTemplate transactionTemplate;
    private final ConversionManager conversionManager;

    @Autowired
    public DefaultConfluenceFileManager(@ComponentImport AttachmentManager attachmentManager, @ComponentImport(value="contentEntityManager") @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport CustomContentManager customContentManager, @ComponentImport TransactionTemplate transactionTemplate, @ComponentImport ConversionManager conversionManager) {
        this.attachmentManager = attachmentManager;
        this.contentEntityManager = contentEntityManager;
        this.customContentManager = customContentManager;
        this.transactionTemplate = transactionTemplate;
        this.conversionManager = conversionManager;
    }

    @Override
    @Nonnull
    public Option<Attachment> getPreviewContent(@Nonnull Attachment attachment, @Nonnull ConversionType conversionType) {
        List previewAttachments = attachment.getAttachments();
        for (Attachment preview : previewAttachments) {
            if (!preview.getFileName().equals(conversionType.name() + "-" + attachment.getFileName())) continue;
            return Option.some((Object)preview);
        }
        return Option.none();
    }

    @Override
    @Nonnull
    public Map<ConversionType, FileContentEntity> getPreviewMap(@Nonnull Attachment attachment) {
        ImmutableMap.Builder previews = ImmutableMap.builder();
        if (this.conversionManager.isConvertible(this.conversionManager.getFileFormat(attachment))) {
            for (ConversionType conversionType : ConversionType.values()) {
                previews.put((Object)conversionType, (Object)new FileContentEntity(attachment.getId(), conversionType, null, this.conversionManager.getConversionUrl(attachment.getId(), attachment.getVersion(), conversionType)));
            }
        }
        return previews.build();
    }

    @Override
    @Nonnull
    public PageResponse<FileVersionSummaryEntity> getVersionSummaries(long attachmentId, @Nonnull PageRequest request) {
        final HashMap commentCounts = Maps.newHashMap();
        Iterator rawCommentCounts = QueryHelper.doQueryById(this.customContentManager, "files.findUnresolvedCommentCountOnFileVersions", Functions.identity(), request.getStart(), request.getLimit(), attachmentId);
        while (rawCommentCounts.hasNext()) {
            Object[] data = (Object[])rawCommentCounts.next();
            Integer attachmentVersion = (Integer)data[0];
            Integer commentCount = ((Long)data[1]).intValue();
            commentCounts.put(attachmentVersion, commentCount);
        }
        Function<Object[], FileVersionSummaryEntity> queryToVersionSummary = new Function<Object[], FileVersionSummaryEntity>(){

            public FileVersionSummaryEntity apply(@Nonnull Object[] data) {
                Long attachmentId = (Long)data[0];
                Long latestVersionId = (Long)data[1];
                Integer fileVersion = (Integer)data[2];
                String title = (String)data[3];
                Timestamp lastModificationDate = (Timestamp)data[4];
                String versionComment = (String)data[5];
                int commentCount = commentCounts.containsKey(fileVersion) ? (Integer)commentCounts.get(fileVersion) : 0;
                return new FileVersionSummaryEntity(attachmentId, latestVersionId, fileVersion, title, lastModificationDate, versionComment, commentCount);
            }
        };
        return QueryHelper.doQueryById(this.customContentManager, "files.findFileVersions", request, queryToVersionSummary, attachmentId);
    }

    @Override
    public int getUnresolvedCommentCountByAttachmentId(long attachmentId) {
        Iterator it = QueryHelper.doQueryById(this.customContentManager, "files.findUnresolvedCommentCount", Functions.identity(), 0, 1, attachmentId);
        return ((Long)it.next()).intValue();
    }

    @Override
    public int getUnresolvedCommentCountByAttachmentId(long attachmentId, int attachmentVersion) {
        ContentEntityObject attachment = this.contentEntityManager.getById(attachmentId);
        if (attachment != null && attachmentVersion > 0) {
            attachment = this.contentEntityManager.getOtherVersion(attachment, attachmentVersion);
        }
        return this.getUnresolvedCommentCountByAttachmentId(attachment == null ? attachmentId : attachment.getId());
    }

    @Override
    @Nonnull
    public PageResponse<Attachment> getFilesForContent(long contentId, @Nonnull PageRequest request) {
        ContentEntityObject content = this.contentEntityManager.getById(contentId);
        PageResponse attachments = this.attachmentManager.getAttachmentDao().getLatestVersionsOfAttachments(content, LimitedRequestImpl.create((PageRequest)request, (int)request.getLimit()), null);
        return attachments;
    }

    @Override
    @Nonnull
    public PageResponse<Attachment> getFilesMinusAttachmentId(long contentId, List<Long> attachmentIds, @Nonnull PageRequest request) {
        return QueryHelper.doQueryById(this.customContentManager, "files.findAttachmentsNotInList", request, Functions.identity(), contentId, attachmentIds);
    }

    @Override
    public void savePreview(final @Nonnull Attachment attachment, final @Nonnull SavableContent savableContent, final @Nonnull ConversionType conversionType) {
        this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Object>(){

            public Object doInTransaction() {
                Attachment preview = new Attachment(conversionType.name() + "-" + attachment.getFileName(), savableContent.getMimeType(), savableContent.getFileSize(), "", true);
                try {
                    DefaultConfluenceFileManager.this.attachmentManager.saveAttachment(preview, null, savableContent.getContentStream());
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        });
    }
}

