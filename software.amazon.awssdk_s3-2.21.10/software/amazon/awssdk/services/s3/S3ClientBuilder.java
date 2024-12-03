/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.client.builder.AwsSyncClientBuilder
 */
package software.amazon.awssdk.services.s3;

import software.amazon.awssdk.awscore.client.builder.AwsSyncClientBuilder;
import software.amazon.awssdk.services.s3.S3BaseClientBuilder;
import software.amazon.awssdk.services.s3.S3Client;

public interface S3ClientBuilder
extends AwsSyncClientBuilder<S3ClientBuilder, S3Client>,
S3BaseClientBuilder<S3ClientBuilder, S3Client> {
}

