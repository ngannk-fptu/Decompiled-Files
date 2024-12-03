/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.PutInstructionFileRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.UploadObjectRequest;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface AmazonS3EncryptionV2
extends AmazonS3 {
    public PutObjectResult putInstructionFile(PutInstructionFileRequest var1);

    public CompleteMultipartUploadResult uploadObject(UploadObjectRequest var1) throws IOException, InterruptedException, ExecutionException;
}

