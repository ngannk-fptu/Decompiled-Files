/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.services.s3.internal.endpoints;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public final class S3EndpointUtils {
    private static final List<Class<?>> ACCELERATE_DISABLED_OPERATIONS = Arrays.asList(ListBucketsRequest.class, CreateBucketRequest.class, DeleteBucketRequest.class);

    private S3EndpointUtils() {
    }

    public static String removeFipsIfNeeded(String region) {
        if (region.startsWith("fips-")) {
            return StringUtils.replace((String)region, (String)"fips-", (String)"");
        }
        if (region.endsWith("-fips")) {
            return StringUtils.replace((String)region, (String)"-fips", (String)"");
        }
        return region;
    }

    public static boolean isFipsRegion(String region) {
        return !StringUtils.isEmpty((CharSequence)region) && (region.startsWith("fips-") || region.endsWith("-fips"));
    }

    public static boolean isAccelerateEnabled(S3Configuration serviceConfiguration) {
        return serviceConfiguration != null && serviceConfiguration.accelerateModeEnabled();
    }

    public static boolean isAccelerateSupported(SdkRequest originalRequest) {
        return !ACCELERATE_DISABLED_OPERATIONS.contains(originalRequest.getClass());
    }

    public static URI accelerateEndpoint(String domain, String protocol) {
        return S3EndpointUtils.toUri(protocol, "s3-accelerate." + domain);
    }

    public static URI accelerateDualstackEndpoint(String domain, String protocol) {
        return S3EndpointUtils.toUri(protocol, "s3-accelerate.dualstack." + domain);
    }

    public static boolean isDualstackEnabled(S3Configuration serviceConfiguration) {
        return serviceConfiguration != null && serviceConfiguration.dualstackEnabled();
    }

    public static URI dualstackEndpoint(String id, String domain, String protocol) {
        String serviceEndpoint = String.format("%s.%s.%s.%s", "s3", "dualstack", id, domain);
        return S3EndpointUtils.toUri(protocol, serviceEndpoint);
    }

    public static URI fipsEndpoint(String id, String domain, String protocol) {
        String serviceEndpoint = String.format("%s.%s.%s", "s3-fips", id, domain);
        return S3EndpointUtils.toUri(protocol, serviceEndpoint);
    }

    public static URI fipsDualstackEndpoint(String id, String domain, String protocol) {
        String serviceEndpoint = String.format("%s.%s.%s.%s", "s3-fips", "dualstack", id, domain);
        return S3EndpointUtils.toUri(protocol, serviceEndpoint);
    }

    public static boolean isPathStyleAccessEnabled(S3Configuration serviceConfiguration) {
        return serviceConfiguration != null && serviceConfiguration.pathStyleAccessEnabled();
    }

    public static boolean isArnRegionEnabled(S3Configuration serviceConfiguration) {
        return serviceConfiguration != null && serviceConfiguration.useArnRegionEnabled();
    }

    public static void changeToDnsEndpoint(SdkHttpRequest.Builder mutableRequest, String bucketName) {
        if (mutableRequest.host().startsWith("s3")) {
            String newHost = mutableRequest.host().replaceFirst("s3", bucketName + ".s3");
            String newPath = mutableRequest.encodedPath().replaceFirst("/" + bucketName, "");
            mutableRequest.host(newHost).encodedPath(newPath);
        }
    }

    public static boolean isArn(String s) {
        return s.startsWith("arn:");
    }

    private static URI toUri(String protocol, String endpoint) {
        try {
            return new URI(String.format("%s://%s", protocol, endpoint));
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

