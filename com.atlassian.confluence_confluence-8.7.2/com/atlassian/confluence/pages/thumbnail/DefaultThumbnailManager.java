/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.core.util.thumbnail.Thumbnail
 *  com.atlassian.core.util.thumbnail.ThumbnailUtil
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.dc.filestore.impl.filesystem.FilesystemFileStore
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  com.atlassian.renderer.util.FileTypeUtil
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Throwables
 *  io.atlassian.util.concurrent.ConcurrentOperationMap
 *  io.atlassian.util.concurrent.ConcurrentOperationMapImpl
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.thumbnail;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.pages.thumbnail.ThumbnailManagerInternal;
import com.atlassian.confluence.impl.pages.thumbnail.renderer.DelegatingThumbnailRenderer;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.attachments.ImageDetails;
import com.atlassian.confluence.pages.attachments.ImageDetailsManager;
import com.atlassian.confluence.pages.persistence.dao.filesystem.HierarchicalContentFileSystemHelper;
import com.atlassian.confluence.pages.thumbnail.CannotGenerateThumbnailException;
import com.atlassian.confluence.pages.thumbnail.ThumbnailInfo;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.ConfluenceRenderUtils;
import com.atlassian.core.util.thumbnail.Thumbnail;
import com.atlassian.core.util.thumbnail.ThumbnailUtil;
import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.dc.filestore.impl.filesystem.FilesystemFileStore;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.atlassian.renderer.util.FileTypeUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import io.atlassian.util.concurrent.ConcurrentOperationMap;
import io.atlassian.util.concurrent.ConcurrentOperationMapImpl;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultThumbnailManager
implements ThumbnailManagerInternal {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultThumbnailManager.class);
    private static final int DELTA_SIZE = 50;
    private static final String THUMBNAIL_FILE_PREFIX = "thumb_";
    private final ConcurrentOperationMap<Pair<Attachment, Option<ImageDimensions>>, Thumbnail> operationMap = new ConcurrentOperationMapImpl();
    private GlobalSettingsManager settingsManager;
    private BootstrapManager bootstrapManager;
    private FilesystemPath confluenceHome;
    private ImageDetailsManager imageDetailsManager;
    private DelegatingThumbnailRenderer thumbnailRenderer;
    private final HierarchicalContentFileSystemHelper fileSystemHelper;

    public DefaultThumbnailManager() {
        this(new HierarchicalContentFileSystemHelper());
    }

    @Internal
    @VisibleForTesting
    DefaultThumbnailManager(HierarchicalContentFileSystemHelper fileSystemHelper) {
        this.fileSystemHelper = fileSystemHelper;
    }

    @Override
    public boolean isThumbnailable(@Nullable Attachment attachment) {
        if (attachment != null) {
            String contentType = FileTypeUtil.getContentType((String)attachment.getFileName());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Attachment {}. Content type {} is thumbnailable: {}. Media type {} is thumbnailable: {}. Attachment is thumbnailable: {}.", new Object[]{attachment.getDisplayTitle(), contentType, DefaultThumbnailManager.isThumbnailable(contentType), attachment.getMediaType(), DefaultThumbnailManager.isThumbnailable(attachment.getMediaType()), DefaultThumbnailManager.isThumbnailable(contentType) || DefaultThumbnailManager.isThumbnailable(attachment.getMediaType())});
            }
            return DefaultThumbnailManager.isThumbnailable(contentType) || DefaultThumbnailManager.isThumbnailable(attachment.getMediaType());
        }
        LOG.debug("Cannot create thumbnail. Attachment is null.");
        return false;
    }

    public static boolean isThumbnailable(String mimeType) {
        LOG.debug("Mime type is {}", (Object)mimeType);
        if (DefaultThumbnailManager.isImageFormatNotSupported(mimeType)) {
            LOG.debug("Mime type matched. {} is NOT supported for thumbnails.", (Object)mimeType);
            return false;
        }
        if (ThumbnailUtil.isMimeTypeSupported((String)mimeType)) {
            LOG.debug("Mime type matched. {} is thumbnailable.", (Object)mimeType);
            return true;
        }
        LOG.debug("Mime type {} not matched. Will be thumbnailable only if mime type equals image/pjpeg: {}", (Object)mimeType, (Object)StringUtils.equalsIgnoreCase((CharSequence)mimeType, (CharSequence)"image/pjpeg"));
        return StringUtils.equalsIgnoreCase((CharSequence)mimeType, (CharSequence)"image/pjpeg");
    }

    private static boolean isImageFormatNotSupported(String mimeType) {
        FileFormat fileFormat = FileFormat.fromMimeType(mimeType);
        return fileFormat == FileFormat.PSD && !DefaultThumbnailManager.isPsdEnabled() || fileFormat == FileFormat.TIF && !DefaultThumbnailManager.isTifEnabled();
    }

    private static boolean isTifEnabled() {
        return Boolean.getBoolean("confluence.document.conversion.imaging.enabled.tif");
    }

    private static boolean isPsdEnabled() {
        return Boolean.getBoolean("confluence.document.conversion.imaging.enabled.psd");
    }

    @Override
    public boolean isThumbnailable(@NonNull ThumbnailInfo info) {
        return DefaultThumbnailManager.isThumbnailable(info.getImageMimeType());
    }

    @Override
    public InputStream getThumbnailData(@NonNull Attachment attachment) throws FileNotFoundException {
        return this.getThumbnailData(attachment, null);
    }

    @Override
    public InputStream getThumbnailData(@NonNull Attachment attachment, @Nullable ImageDimensions imageDimensions) throws FileNotFoundException {
        FilesystemPath thumbnailFile = this.getThumbnailPath(attachment, imageDimensions);
        try {
            if (thumbnailFile.fileExists()) {
                return thumbnailFile.fileReader().openInputStream();
            }
            throw new FileNotFoundException("Thumbnail file for attachment " + attachment.getFileName() + " is '" + thumbnailFile + "'. But this file does not exist.");
        }
        catch (IOException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override
    public Thumbnail getThumbnail(@NonNull Attachment attachment) throws IllegalArgumentException {
        return this.getThumbnail(attachment, null);
    }

    @Override
    public Thumbnail getThumbnail(@NonNull Attachment attachment, @Nullable ImageDimensions imageDimensions) throws IllegalArgumentException {
        int maxHeight;
        int maxWidth;
        Preconditions.checkArgument((attachment != null ? 1 : 0) != 0, (Object)"A null attachment was passed. Cannot get the thumbnail file of a null attachment.");
        Preconditions.checkArgument((boolean)this.isThumbnailable(attachment), (String)"Unable to create thumbnail image for attachment: %s", (Object)attachment);
        ImageDimensions maxThumbDimensions = this.getMaxThumbDimensions(attachment, imageDimensions);
        if (maxThumbDimensions == null) {
            maxWidth = this.settingsManager.getGlobalSettings().getMaxThumbWidth();
            maxHeight = this.settingsManager.getGlobalSettings().getMaxThumbHeight();
        } else {
            maxWidth = maxThumbDimensions.getWidth();
            maxHeight = maxThumbDimensions.getHeight();
        }
        try {
            return (Thumbnail)this.operationMap.runOperation((Object)Pair.pair((Object)attachment, (Object)Option.option((Object)imageDimensions)), () -> this.thumbnailRenderer.retrieveOrCreateThumbNail(attachment, this.getThumbnailPath(attachment, imageDimensions), maxWidth, maxHeight));
        }
        catch (ExecutionException ex) {
            Throwables.throwIfUnchecked((Throwable)ex.getCause());
            throw new RuntimeException(ex.getCause());
        }
    }

    @Override
    public ThumbnailInfo getThumbnailInfo(@NonNull Attachment attachment) throws CannotGenerateThumbnailException {
        return this.getThumbnailInfo(attachment, (ImageDimensions)null);
    }

    @Override
    public ThumbnailInfo getThumbnailInfo(@NonNull Attachment attachment, @Nullable ImageDimensions imageDimensions) throws CannotGenerateThumbnailException {
        Objects.requireNonNull(attachment, "Cannot get the thumbnail file of a null attachment.");
        ContentEntityObject container = attachment.getContainer();
        Objects.requireNonNull(container, "Cannot get the thumbnail of an attachment without a container.");
        String contextPath = this.bootstrapManager.getWebAppContextPath();
        String remotePath = contextPath + ConfluenceRenderUtils.getAttachmentsPathForContent(container);
        return this.getThumbnailInfo(attachment, remotePath, imageDimensions);
    }

    @Override
    public ThumbnailInfo getThumbnailInfo(@NonNull Attachment attachment, @Nullable String remoteAttachmentPath) throws CannotGenerateThumbnailException {
        return this.getThumbnailInfo(attachment, remoteAttachmentPath, null);
    }

    @Override
    public ThumbnailInfo getThumbnailInfo(@NonNull Attachment attachment, @Nullable String remoteAttachmentPath, @Nullable ImageDimensions imageDimensions) throws CannotGenerateThumbnailException {
        if (attachment == null) {
            throw new IllegalArgumentException("A null attachment was passed. Cannot get the thumbnail file of a null attachment.");
        }
        ImageDetails details = this.imageDetailsManager.getImageDetails(attachment);
        if (details == null) {
            throw new CannotGenerateThumbnailException(attachment);
        }
        ImageDimensions maxThumbnailDimensions = this.settingsManager.getGlobalSettings().getMaxThumbnailDimensions();
        ImageDimensions maxThumbDimensions = this.getMaxThumbDimensions(attachment, imageDimensions);
        if (maxThumbDimensions != null) {
            maxThumbnailDimensions = new ImageDimensions(maxThumbDimensions.getWidth(), maxThumbDimensions.getHeight());
        }
        return new ThumbnailInfo(details, maxThumbnailDimensions, remoteAttachmentPath);
    }

    public void setBootstrapManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public void setConfluenceHome(FilesystemPath confluenceHome) {
        this.confluenceHome = confluenceHome;
    }

    @Deprecated
    protected File getThumbnailsFolder(Attachment attachment) {
        return this.getThumbnailsPath(attachment).asJavaFile();
    }

    private FilesystemPath getThumbnailsPath(Attachment attachment) {
        ContentEntityObject container = Objects.requireNonNull(attachment.getContainer());
        return this.getThumbnailsFolder(container.getId());
    }

    private FilesystemPath getThumbnailsFolder(long contentId) {
        return this.fileSystemHelper.getDirectory(this.getRootThumbnailsPath(), contentId);
    }

    FilesystemPath getRootThumbnailsPath() {
        return this.getConfluenceHome().path(new String[]{"thumbnails"});
    }

    private FilesystemPath getConfluenceHome() {
        if (this.confluenceHome != null) {
            return this.confluenceHome;
        }
        return FilesystemFileStore.forFile((File)new File(this.bootstrapManager.getConfluenceHome()));
    }

    @Override
    public FilesystemPath getThumbnailPath(@NonNull Attachment attachment, @Nullable ImageDimensions imageDimensions) {
        if (attachment == null) {
            throw new IllegalArgumentException("A null attachment was passed. Cannot get the thumbnail file of a null attachment.");
        }
        String thumbnailFileName = this.getThumbnailFileName(attachment, imageDimensions);
        return this.getThumbnailsPath(attachment).path(new String[]{thumbnailFileName});
    }

    @Override
    public FilesystemPath getThumbnailPath(long attachmentId, int version, long contentId) {
        return this.getThumbnailsFolder(contentId).path(new String[]{THUMBNAIL_FILE_PREFIX + attachmentId + "_" + version});
    }

    @Override
    public boolean removeThumbnail(@NonNull Attachment attachment) {
        if (attachment == null) {
            throw new IllegalArgumentException("A null attachment was passed. Cannot remove the thumbnail file of a null attachment.");
        }
        FilesystemPath thumbnailsFolder = this.getThumbnailsPath(attachment);
        String thumbnailFileNamePrefix = this.getThumbnailFileNamePrefix(attachment);
        try {
            List<FilesystemPath> thumbnailFiles = thumbnailsFolder.getFileDescendents().filter(path -> path.getLeafName().filter(name -> name.startsWith(thumbnailFileNamePrefix)).isPresent()).collect(Collectors.toList());
            thumbnailFiles.forEach(FileStore.Path::tryDeleteFile);
            thumbnailsFolder.deleteFileAndPrune().untilReach(this.getRootThumbnailsPath());
            return !thumbnailFiles.isEmpty();
        }
        catch (IOException ex) {
            return false;
        }
    }

    public void setThumbnailRenderer(DelegatingThumbnailRenderer thumbnailRenderer) {
        this.thumbnailRenderer = thumbnailRenderer;
    }

    @Deprecated
    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setSettingsManager(GlobalSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setImageDetailsManager(ImageDetailsManager imageDetailsManager) {
        this.imageDetailsManager = imageDetailsManager;
    }

    private ImageDimensions getMaxThumbDimensions(Attachment attachment, ImageDimensions imageDimensions) {
        if (imageDimensions == null) {
            return null;
        }
        Integer maxWidth = null;
        Integer maxHeight = null;
        if (imageDimensions.getWidth() > 0) {
            Integer originWidth = this.imageDetailsManager.getImageDetails(attachment).getWidth();
            maxWidth = this.ceilWithUpperBound(imageDimensions.getWidth(), originWidth);
            maxHeight = Integer.MAX_VALUE;
        } else if (imageDimensions.getHeight() > 0) {
            Integer originHeight = this.imageDetailsManager.getImageDetails(attachment).getHeight();
            maxHeight = this.ceilWithUpperBound(imageDimensions.getHeight(), originHeight);
            maxWidth = Integer.MAX_VALUE;
        }
        if (maxWidth != null && maxHeight != null) {
            return new ImageDimensions(maxWidth, maxHeight);
        }
        return null;
    }

    private Integer ceilWithUpperBound(Integer size, Integer maxSize) {
        Integer ceil = 50 * (int)Math.ceil((double)size.intValue() / 50.0);
        return Math.min(ceil, maxSize);
    }

    private String getThumbnailFileName(Attachment attachment, ImageDimensions imageDimensions) {
        String thumbnailFileName = THUMBNAIL_FILE_PREFIX + attachment.getId() + "_" + attachment.getVersion();
        ImageDimensions maxThumbDimensions = this.getMaxThumbDimensions(attachment, imageDimensions);
        if (maxThumbDimensions != null) {
            int width = maxThumbDimensions.getWidth();
            if (width < Integer.MAX_VALUE) {
                thumbnailFileName = thumbnailFileName + "_w" + width;
            } else {
                int height = maxThumbDimensions.getHeight();
                if (height < Integer.MAX_VALUE) {
                    thumbnailFileName = thumbnailFileName + "_h" + height;
                }
            }
        }
        return thumbnailFileName;
    }

    private String getThumbnailFileNamePrefix(Attachment attachment) {
        return THUMBNAIL_FILE_PREFIX + attachment.getId() + "_";
    }

    static enum FileFormat {
        PSD(new String[]{"image/photoshop", "image/x-photoshop", "image/psd", "application/photoshop", "application/psd", "zz-application/zz-winassoc-psd"}),
        TIF(new String[]{"image/tif", "image/x-tif", "image/tiff", "image/x-tiff", "application/tif", "application/x-tif", "application/tiff", "application/x-tiff"});

        private final String[] mimeTypes;

        private FileFormat(String[] mimeTypes) {
            this.mimeTypes = mimeTypes;
        }

        public static FileFormat fromMimeType(String mimeType) {
            if (mimeType == null) {
                return null;
            }
            String newMimeType = mimeType.trim().toLowerCase();
            for (FileFormat fileFormat : FileFormat.values()) {
                String[] mimeTypes;
                for (String entry : mimeTypes = fileFormat.mimeTypes) {
                    if (!entry.equals(newMimeType)) continue;
                    return fileFormat;
                }
            }
            return null;
        }
    }
}

