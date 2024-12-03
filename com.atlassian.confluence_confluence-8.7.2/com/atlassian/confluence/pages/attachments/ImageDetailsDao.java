/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.attachments;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.attachments.ImageDetails;

public interface ImageDetailsDao {
    public ImageDetails getImageDetails(Attachment var1);

    public void save(ImageDetails var1);

    public void removeDetailsFor(Attachment var1);
}

