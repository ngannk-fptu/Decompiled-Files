/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.FileStore
 *  com.atlassian.dc.filestore.api.FileStore$Path
 */
package com.atlassian.dc.filestore.impl.s3;

import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.dc.filestore.impl.s3.S3Config;
import com.atlassian.dc.filestore.impl.s3.S3Path;
import java.util.LinkedList;

public final class S3FileStore
implements FileStore {
    private final S3Config s3Config;

    public S3FileStore(S3Config s3Config) {
        this.s3Config = s3Config;
    }

    public FileStore.Path root() {
        return new S3Path(this.s3Config.getBucketName(), new LinkedList<String>(), this.s3Config.getOperationExecutor());
    }
}

