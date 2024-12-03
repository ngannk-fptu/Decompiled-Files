/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.services.s3.transfer.AbortableTransfer;
import java.net.URL;

public interface PresignedUrlDownload
extends AbortableTransfer {
    public URL getPresignedUrl();
}

