/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.transfer.UploadContext;

public interface ObjectTaggingProvider {
    public ObjectTagging provideObjectTags(UploadContext var1);
}

