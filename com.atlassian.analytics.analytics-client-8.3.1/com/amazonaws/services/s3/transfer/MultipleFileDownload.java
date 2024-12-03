/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.services.s3.transfer.Transfer;
import java.io.IOException;

public interface MultipleFileDownload
extends Transfer {
    public String getKeyPrefix();

    public String getBucketName();

    public void abort() throws IOException;
}

