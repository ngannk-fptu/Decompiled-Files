/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.exception.SdkException
 *  software.amazon.awssdk.services.s3.S3Client
 */
package com.atlassian.dc.filestore.impl.s3;

import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;

@FunctionalInterface
public interface ClientOperation<T> {
    public T perform(S3Client var1) throws SdkException;
}

