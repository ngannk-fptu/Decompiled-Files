/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.thumbnail;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.thumbnail.CannotGenerateThumbnailException;
import com.atlassian.confluence.pages.thumbnail.ThumbnailInfo;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.renderer.embedded.ImagePathHelper;
import java.util.ArrayList;
import java.util.List;

public class Thumbnails {
    private List<ThumbnailInfo> thumbnails = new ArrayList<ThumbnailInfo>();
    private List<List<Object>> rows;
    private int maxHeight;
    private int maxWidth;
    private int maxImageHeight;
    private int maxImageWidth;
    private int configuredColumns;

    @Deprecated
    public Thumbnails(List<Attachment> attachments, String attachmentsRoot, int columns, AttachmentManager attachmentManager, ThumbnailManager thumbnailManager) {
        this(attachments, new StaticImagePathHelper(attachmentsRoot), columns, thumbnailManager);
    }

    @Deprecated
    public Thumbnails(List<Attachment> attachments, ImagePathHelper pathHelper, int columns, AttachmentManager attachmentManager, ThumbnailManager thumbnailManager) {
        this(attachments, pathHelper, columns, thumbnailManager);
    }

    public Thumbnails(List<Attachment> attachments, int columns, ThumbnailManager thumbnailManager) {
        this(attachments, null, columns, thumbnailManager);
    }

    public Thumbnails(List<Attachment> attachments, ImagePathHelper pathHelper, int columns, ThumbnailManager thumbnailManager) {
        this.configuredColumns = columns;
        for (Attachment attachment : attachments) {
            ThumbnailInfo thumbnail;
            if (!thumbnailManager.isThumbnailable(attachment)) continue;
            try {
                thumbnail = pathHelper != null ? thumbnailManager.getThumbnailInfo(attachment, pathHelper.getImagePath(attachment, true)) : thumbnailManager.getThumbnailInfo(attachment);
            }
            catch (CannotGenerateThumbnailException e) {
                continue;
            }
            if (!thumbnailManager.isThumbnailable(thumbnail)) continue;
            if (thumbnail.getThumbnailHeight() > this.maxHeight) {
                this.maxHeight = thumbnail.getThumbnailHeight();
            }
            if (thumbnail.getThumbnailWidth() > this.maxWidth) {
                this.maxWidth = thumbnail.getThumbnailWidth();
            }
            if (thumbnail.getOriginalHeight() > this.maxImageHeight) {
                this.maxImageHeight = thumbnail.getOriginalHeight();
            }
            if (thumbnail.getOriginalWidth() > this.maxImageWidth) {
                this.maxImageWidth = thumbnail.getOriginalWidth();
            }
            this.thumbnails.add(thumbnail);
        }
    }

    public int getSize() {
        return this.thumbnails.size();
    }

    public List<ThumbnailInfo> getThumbnails() {
        return this.thumbnails;
    }

    public List<List<Object>> getRows() {
        if (this.rows == null) {
            this.rows = new ArrayList<List<Object>>(this.getSize() / this.configuredColumns + 1);
            for (int idx = 0; idx < this.getSize(); idx += this.configuredColumns) {
                ArrayList<Object> row = new ArrayList<Object>(this.configuredColumns);
                for (int i = 0; i < this.configuredColumns; ++i) {
                    if (idx + i < this.thumbnails.size()) {
                        row.add(this.thumbnails.get(idx + i));
                        continue;
                    }
                    if (this.rows.size() <= 0) continue;
                    row.add("BLANK");
                }
                this.rows.add(row);
            }
        }
        return this.rows;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public int getMaxImageHeight() {
        return this.maxImageHeight;
    }

    public int getMaxImageWidth() {
        return this.maxImageWidth;
    }

    public int getColumns() {
        if (this.getRows().size() == 0) {
            return 1;
        }
        return this.getRows().get(0).size();
    }

    private static class StaticImagePathHelper
    implements ImagePathHelper {
        final String path;

        private StaticImagePathHelper(String str) {
            this.path = str;
        }

        @Override
        public String getImagePath(Attachment attachment, boolean isThumbnail) {
            return this.path;
        }
    }
}

