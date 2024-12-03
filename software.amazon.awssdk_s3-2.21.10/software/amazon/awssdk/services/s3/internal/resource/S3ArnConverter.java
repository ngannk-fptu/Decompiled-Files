/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.arns.Arn
 *  software.amazon.awssdk.arns.ArnResource
 */
package software.amazon.awssdk.services.s3.internal.resource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.arns.Arn;
import software.amazon.awssdk.arns.ArnResource;
import software.amazon.awssdk.services.s3.internal.resource.ArnConverter;
import software.amazon.awssdk.services.s3.internal.resource.IntermediateOutpostResource;
import software.amazon.awssdk.services.s3.internal.resource.OutpostResourceType;
import software.amazon.awssdk.services.s3.internal.resource.S3AccessPointResource;
import software.amazon.awssdk.services.s3.internal.resource.S3ArnUtils;
import software.amazon.awssdk.services.s3.internal.resource.S3BucketResource;
import software.amazon.awssdk.services.s3.internal.resource.S3ObjectLambdaResource;
import software.amazon.awssdk.services.s3.internal.resource.S3ObjectResource;
import software.amazon.awssdk.services.s3.internal.resource.S3OutpostResource;
import software.amazon.awssdk.services.s3.internal.resource.S3Resource;
import software.amazon.awssdk.services.s3.internal.resource.S3ResourceType;

@SdkInternalApi
public final class S3ArnConverter
implements ArnConverter<S3Resource> {
    private static final S3ArnConverter INSTANCE = new S3ArnConverter();
    private static final Pattern OBJECT_AP_PATTERN = Pattern.compile("^([0-9a-zA-Z-]+)/object/(.*)$");
    private static final String OBJECT_LAMBDA_SERVICE = "s3-object-lambda";

    private S3ArnConverter() {
    }

    public static S3ArnConverter create() {
        return INSTANCE;
    }

    @Override
    public S3Resource convertArn(Arn arn) {
        S3ResourceType s3ResourceType;
        if (this.isV1Arn(arn)) {
            return this.convertV1Arn(arn);
        }
        String resourceType = (String)arn.resource().resourceType().orElseThrow(() -> new IllegalArgumentException("Unknown ARN type"));
        try {
            s3ResourceType = S3ResourceType.fromValue(resourceType);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown ARN type '" + (String)arn.resource().resourceType().get() + "'");
        }
        switch (s3ResourceType) {
            case ACCESS_POINT: {
                return this.parseS3AccessPointArn(arn);
            }
            case BUCKET: {
                return this.parseS3BucketArn(arn);
            }
            case OUTPOST: {
                return this.parseS3OutpostAccessPointArn(arn);
            }
        }
        throw new IllegalArgumentException("Unknown ARN type '" + (Object)((Object)s3ResourceType) + "'");
    }

    private S3Resource convertV1Arn(Arn arn) {
        String resource = arn.resourceAsString();
        String[] splitResource = resource.split("/", 2);
        if (splitResource.length > 1) {
            S3BucketResource parentBucket = S3BucketResource.builder().partition(arn.partition()).bucketName(splitResource[0]).build();
            return S3ObjectResource.builder().parentS3Resource(parentBucket).key(splitResource[1]).build();
        }
        return S3BucketResource.builder().partition(arn.partition()).bucketName(resource).build();
    }

    private S3BucketResource parseS3BucketArn(Arn arn) {
        return S3BucketResource.builder().partition(arn.partition()).region(arn.region().orElse(null)).accountId(arn.accountId().orElse(null)).bucketName(arn.resource().resource()).build();
    }

    private S3Resource parseS3AccessPointArn(Arn arn) {
        Matcher objectMatcher = OBJECT_AP_PATTERN.matcher(arn.resource().resource());
        if (objectMatcher.matches()) {
            String accessPointName = objectMatcher.group(1);
            String objectKey = objectMatcher.group(2);
            S3AccessPointResource parentResource = S3AccessPointResource.builder().partition(arn.partition()).region(arn.region().orElse(null)).accountId(arn.accountId().orElse(null)).accessPointName(accessPointName).build();
            return S3ObjectResource.builder().parentS3Resource(parentResource).key(objectKey).build();
        }
        if (OBJECT_LAMBDA_SERVICE.equals(arn.service())) {
            return this.parseS3ObjectLambdaAccessPointArn(arn);
        }
        return S3AccessPointResource.builder().partition(arn.partition()).region(arn.region().orElse(null)).accountId(arn.accountId().orElse(null)).accessPointName(arn.resource().resource()).build();
    }

    private S3Resource parseS3OutpostAccessPointArn(Arn arn) {
        IntermediateOutpostResource intermediateOutpostResource = S3ArnUtils.parseOutpostArn(arn);
        ArnResource outpostSubResource = intermediateOutpostResource.outpostSubresource();
        String resourceType = (String)outpostSubResource.resourceType().orElseThrow(() -> new IllegalArgumentException("Unknown ARN type"));
        if (!OutpostResourceType.OUTPOST_ACCESS_POINT.toString().equals(resourceType)) {
            throw new IllegalArgumentException("Unknown outpost ARN type '" + outpostSubResource.resourceType() + "'");
        }
        return S3AccessPointResource.builder().accessPointName(outpostSubResource.resource()).parentS3Resource(S3OutpostResource.builder().partition(arn.partition()).region(arn.region().orElse(null)).accountId(arn.accountId().orElse(null)).outpostId(intermediateOutpostResource.outpostId()).build()).build();
    }

    private S3Resource parseS3ObjectLambdaAccessPointArn(Arn arn) {
        if (arn.resource().qualifier().isPresent()) {
            throw new IllegalArgumentException("S3 object lambda access point arn shouldn't contain any sub resources.");
        }
        S3ObjectLambdaResource objectLambdaResource = S3ObjectLambdaResource.builder().accountId(arn.accountId().orElse(null)).region(arn.region().orElse(null)).partition(arn.partition()).accessPointName(arn.resource().resource()).build();
        return S3AccessPointResource.builder().accessPointName(objectLambdaResource.accessPointName()).parentS3Resource(objectLambdaResource).build();
    }

    private boolean isV1Arn(Arn arn) {
        return !arn.accountId().isPresent() && !arn.region().isPresent();
    }
}

