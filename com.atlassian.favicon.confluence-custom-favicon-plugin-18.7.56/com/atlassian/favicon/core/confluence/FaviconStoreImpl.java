/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.AttachmentResource
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.InputStreamAttachmentResource
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.FileUploadManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.core.util.thumbnail.ThumbnailDimension
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.favicon.core.Constants
 *  com.atlassian.favicon.core.Favicon
 *  com.atlassian.favicon.core.FaviconStore
 *  com.atlassian.favicon.core.ImageType
 *  com.atlassian.favicon.core.StoredFavicon
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.favicon.core.confluence;

import com.atlassian.confluence.core.AttachmentResource;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.InputStreamAttachmentResource;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.core.util.thumbnail.ThumbnailDimension;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.favicon.core.Constants;
import com.atlassian.favicon.core.Favicon;
import com.atlassian.favicon.core.FaviconStore;
import com.atlassian.favicon.core.ImageType;
import com.atlassian.favicon.core.StoredFavicon;
import com.atlassian.favicon.core.confluence.FaviconChangedEvent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={FaviconStore.class})
public class FaviconStoreImpl
implements FaviconStore {
    private static final Logger log = LoggerFactory.getLogger(FaviconStoreImpl.class);
    private final SettingsManager settingsManager;
    private final AttachmentManager attachmentManager;
    private final FileUploadManager fileUploadManager;
    private final EventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public FaviconStoreImpl(@ComponentImport SettingsManager settingsManager, @ComponentImport AttachmentManager attachmentManager, @ComponentImport FileUploadManager fileUploadManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport TransactionTemplate transactionTemplate) {
        this.settingsManager = settingsManager;
        this.attachmentManager = attachmentManager;
        this.fileUploadManager = fileUploadManager;
        this.eventPublisher = eventPublisher;
        this.transactionTemplate = transactionTemplate;
    }

    public Optional<StoredFavicon> getFavicon(final ImageType anImageType, final ThumbnailDimension aDesiredSize) {
        try {
            return (Optional)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Optional<StoredFavicon>>(){

                public Optional<StoredFavicon> doInTransaction() {
                    Attachment attachment = FaviconStoreImpl.this.getAttachmentFor(FaviconStoreImpl.this.getAttachmentFilenameFor(anImageType, aDesiredSize));
                    if (attachment == null && anImageType == ImageType.PNG) {
                        attachment = FaviconStoreImpl.this.getAttachmentFor(FaviconStoreImpl.this.getAttachmentFilenameFor(anImageType, Constants.MAX_DIMENSION));
                    }
                    return Optional.of(FaviconStoreImpl.this.getFaviconDataFor(attachment));
                }
            });
        }
        catch (RuntimeException e) {
            log.error("Expected custom favicon data to exist but it doesn't. Maybe attachments were accidentally removed? Re-setting the favicon should fix this. Requested image of type {} and dimensions {}", (Object)anImageType, (Object)aDesiredSize);
            return Optional.empty();
        }
    }

    public void saveFavicon(Favicon aFaviconToSave) throws IOException {
        byte[] imageData = aFaviconToSave.getImageData();
        try (ByteArrayInputStream is = new ByteArrayInputStream(imageData);){
            InputStreamAttachmentResource ar = new InputStreamAttachmentResource((InputStream)is, aFaviconToSave.getFilename(), aFaviconToSave.getContentType(), (long)imageData.length);
            this.fileUploadManager.storeResource((AttachmentResource)ar, this.getContentToAttachImagesTo());
        }
    }

    public void notifyChangedFavicon() {
        this.eventPublisher.publish((Object)new FaviconChangedEvent(this));
    }

    private ContentEntityObject getContentToAttachImagesTo() {
        return this.settingsManager.getGlobalDescription();
    }

    private String getAttachmentFilenameFor(ImageType anImageType, ThumbnailDimension aSize) {
        return Favicon.generateFilename((ImageType)anImageType, (ThumbnailDimension)aSize);
    }

    private Attachment getAttachmentFor(String aFilename) {
        return this.attachmentManager.getAttachment(this.getContentToAttachImagesTo(), aFilename);
    }

    private StoredFavicon getFaviconDataFor(Attachment anAttachment) {
        InputStream is = this.attachmentManager.getAttachmentData(anAttachment);
        return new StoredFavicon(is, anAttachment.getContentType(), anAttachment.getFileSize());
    }
}

