/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.client.builder.AwsAsyncClientBuilder
 */
package software.amazon.awssdk.services.s3;

import java.util.function.Consumer;
import software.amazon.awssdk.awscore.client.builder.AwsAsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3BaseClientBuilder;
import software.amazon.awssdk.services.s3.multipart.MultipartConfiguration;

public interface S3AsyncClientBuilder
extends AwsAsyncClientBuilder<S3AsyncClientBuilder, S3AsyncClient>,
S3BaseClientBuilder<S3AsyncClientBuilder, S3AsyncClient> {
    default public S3AsyncClientBuilder multipartEnabled(Boolean enabled) {
        throw new UnsupportedOperationException();
    }

    default public S3AsyncClientBuilder multipartConfiguration(MultipartConfiguration multipartConfiguration) {
        throw new UnsupportedOperationException();
    }

    default public S3AsyncClientBuilder multipartConfiguration(Consumer<MultipartConfiguration.Builder> multipartConfiguration) {
        MultipartConfiguration.Builder builder = MultipartConfiguration.builder();
        multipartConfiguration.accept(builder);
        return this.multipartConfiguration((MultipartConfiguration)builder.build());
    }
}

