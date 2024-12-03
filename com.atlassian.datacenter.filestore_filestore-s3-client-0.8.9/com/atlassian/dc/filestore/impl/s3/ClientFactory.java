/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.services.s3.S3Client
 */
package com.atlassian.dc.filestore.impl.s3;

import software.amazon.awssdk.services.s3.S3Client;

@FunctionalInterface
public interface ClientFactory {
    public S3Client getClient();
}

