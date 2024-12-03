/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.arns.Arn
 *  software.amazon.awssdk.arns.ArnResource
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.services.s3.internal.resource;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.arns.Arn;
import software.amazon.awssdk.arns.ArnResource;
import software.amazon.awssdk.services.s3.internal.resource.IntermediateOutpostResource;
import software.amazon.awssdk.services.s3.internal.resource.S3AccessPointResource;
import software.amazon.awssdk.services.s3.internal.resource.S3ResourceType;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public class S3ArnUtils {
    private static final int OUTPOST_ID_START_INDEX = "outpost".length() + 1;

    private S3ArnUtils() {
    }

    public static S3AccessPointResource parseS3AccessPointArn(Arn arn) {
        return S3AccessPointResource.builder().partition(arn.partition()).region(arn.region().orElse(null)).accountId(arn.accountId().orElse(null)).accessPointName(arn.resource().resource()).build();
    }

    public static IntermediateOutpostResource parseOutpostArn(Arn arn) {
        String resource = arn.resourceAsString();
        Integer outpostIdEndIndex = null;
        for (int i = OUTPOST_ID_START_INDEX; i < resource.length(); ++i) {
            char ch = resource.charAt(i);
            if (ch != ':' && ch != '/') continue;
            outpostIdEndIndex = i;
            break;
        }
        if (outpostIdEndIndex == null) {
            throw new IllegalArgumentException("Invalid format for S3 outpost ARN, missing outpostId");
        }
        String outpostId = resource.substring(OUTPOST_ID_START_INDEX, outpostIdEndIndex);
        if (StringUtils.isEmpty((CharSequence)outpostId)) {
            throw new IllegalArgumentException("Invalid format for S3 outpost ARN, missing outpostId");
        }
        String subresource = resource.substring(outpostIdEndIndex + 1);
        if (StringUtils.isEmpty((CharSequence)subresource)) {
            throw new IllegalArgumentException("Invalid format for S3 outpost ARN");
        }
        return IntermediateOutpostResource.builder().outpostId(outpostId).outpostSubresource(ArnResource.fromString((String)subresource)).build();
    }

    public static Optional<S3ResourceType> getArnType(String arnString) {
        try {
            Arn arn = Arn.fromString((String)arnString);
            String resourceType = (String)arn.resource().resourceType().get();
            S3ResourceType s3ResourceType = S3ResourceType.fromValue(resourceType);
            return Optional.of(s3ResourceType);
        }
        catch (Exception ignored) {
            return Optional.empty();
        }
    }
}

