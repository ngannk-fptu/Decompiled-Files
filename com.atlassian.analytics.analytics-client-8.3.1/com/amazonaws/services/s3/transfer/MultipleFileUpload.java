/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.Upload;
import java.util.Collection;

public interface MultipleFileUpload
extends Transfer {
    public String getKeyPrefix();

    public String getBucketName();

    public Collection<? extends Upload> getSubTransfers();
}

