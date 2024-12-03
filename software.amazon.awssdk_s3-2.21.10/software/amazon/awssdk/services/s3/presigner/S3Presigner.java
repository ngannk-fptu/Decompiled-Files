/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.NotThreadSafe
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
 *  software.amazon.awssdk.awscore.presigner.SdkPresigner
 *  software.amazon.awssdk.awscore.presigner.SdkPresigner$Builder
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.regions.Region
 */
package software.amazon.awssdk.services.s3.presigner;

import java.net.URI;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.awscore.presigner.SdkPresigner;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.internal.signing.DefaultS3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.AbortMultipartUploadPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.CompleteMultipartUploadPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.CreateMultipartUploadPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.DeleteObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedAbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedCompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedCreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedDeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

@SdkPublicApi
@Immutable
@ThreadSafe
public interface S3Presigner
extends SdkPresigner {
    public static S3Presigner create() {
        return S3Presigner.builder().build();
    }

    public static Builder builder() {
        return DefaultS3Presigner.builder();
    }

    public PresignedGetObjectRequest presignGetObject(GetObjectPresignRequest var1);

    default public PresignedGetObjectRequest presignGetObject(Consumer<GetObjectPresignRequest.Builder> request) {
        GetObjectPresignRequest.Builder builder = GetObjectPresignRequest.builder();
        request.accept(builder);
        return this.presignGetObject(builder.build());
    }

    public PresignedPutObjectRequest presignPutObject(PutObjectPresignRequest var1);

    default public PresignedPutObjectRequest presignPutObject(Consumer<PutObjectPresignRequest.Builder> request) {
        PutObjectPresignRequest.Builder builder = PutObjectPresignRequest.builder();
        request.accept(builder);
        return this.presignPutObject(builder.build());
    }

    public PresignedDeleteObjectRequest presignDeleteObject(DeleteObjectPresignRequest var1);

    default public PresignedDeleteObjectRequest presignDeleteObject(Consumer<DeleteObjectPresignRequest.Builder> request) {
        DeleteObjectPresignRequest.Builder builder = DeleteObjectPresignRequest.builder();
        request.accept(builder);
        return this.presignDeleteObject(builder.build());
    }

    public PresignedCreateMultipartUploadRequest presignCreateMultipartUpload(CreateMultipartUploadPresignRequest var1);

    default public PresignedCreateMultipartUploadRequest presignCreateMultipartUpload(Consumer<CreateMultipartUploadPresignRequest.Builder> request) {
        CreateMultipartUploadPresignRequest.Builder builder = CreateMultipartUploadPresignRequest.builder();
        request.accept(builder);
        return this.presignCreateMultipartUpload(builder.build());
    }

    public PresignedUploadPartRequest presignUploadPart(UploadPartPresignRequest var1);

    default public PresignedUploadPartRequest presignUploadPart(Consumer<UploadPartPresignRequest.Builder> request) {
        UploadPartPresignRequest.Builder builder = UploadPartPresignRequest.builder();
        request.accept(builder);
        return this.presignUploadPart(builder.build());
    }

    public PresignedCompleteMultipartUploadRequest presignCompleteMultipartUpload(CompleteMultipartUploadPresignRequest var1);

    default public PresignedCompleteMultipartUploadRequest presignCompleteMultipartUpload(Consumer<CompleteMultipartUploadPresignRequest.Builder> request) {
        CompleteMultipartUploadPresignRequest.Builder builder = CompleteMultipartUploadPresignRequest.builder();
        request.accept(builder);
        return this.presignCompleteMultipartUpload(builder.build());
    }

    public PresignedAbortMultipartUploadRequest presignAbortMultipartUpload(AbortMultipartUploadPresignRequest var1);

    default public PresignedAbortMultipartUploadRequest presignAbortMultipartUpload(Consumer<AbortMultipartUploadPresignRequest.Builder> request) {
        AbortMultipartUploadPresignRequest.Builder builder = AbortMultipartUploadPresignRequest.builder();
        request.accept(builder);
        return this.presignAbortMultipartUpload(builder.build());
    }

    @SdkPublicApi
    @NotThreadSafe
    public static interface Builder
    extends SdkPresigner.Builder {
        public Builder serviceConfiguration(S3Configuration var1);

        public Builder region(Region var1);

        default public Builder credentialsProvider(AwsCredentialsProvider credentialsProvider) {
            return this.credentialsProvider((IdentityProvider)credentialsProvider);
        }

        public Builder credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> var1);

        public Builder dualstackEnabled(Boolean var1);

        public Builder fipsEnabled(Boolean var1);

        public Builder endpointOverride(URI var1);

        public S3Presigner build();
    }
}

