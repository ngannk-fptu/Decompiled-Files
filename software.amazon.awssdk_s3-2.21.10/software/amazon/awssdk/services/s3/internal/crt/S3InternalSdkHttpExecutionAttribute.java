/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.interceptor.trait.HttpChecksum
 *  software.amazon.awssdk.crt.s3.ResumeToken
 *  software.amazon.awssdk.http.SdkHttpExecutionAttribute
 *  software.amazon.awssdk.regions.Region
 */
package software.amazon.awssdk.services.s3.internal.crt;

import java.nio.file.Path;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.interceptor.trait.HttpChecksum;
import software.amazon.awssdk.crt.s3.ResumeToken;
import software.amazon.awssdk.http.SdkHttpExecutionAttribute;
import software.amazon.awssdk.regions.Region;

@SdkInternalApi
public final class S3InternalSdkHttpExecutionAttribute<T>
extends SdkHttpExecutionAttribute<T> {
    public static final S3InternalSdkHttpExecutionAttribute<String> OPERATION_NAME = new S3InternalSdkHttpExecutionAttribute<String>(String.class);
    public static final S3InternalSdkHttpExecutionAttribute<HttpChecksum> HTTP_CHECKSUM = new S3InternalSdkHttpExecutionAttribute<HttpChecksum>(HttpChecksum.class);
    public static final S3InternalSdkHttpExecutionAttribute<ResumeToken> CRT_PAUSE_RESUME_TOKEN = new S3InternalSdkHttpExecutionAttribute<ResumeToken>(ResumeToken.class);
    public static final S3InternalSdkHttpExecutionAttribute<Region> SIGNING_REGION = new S3InternalSdkHttpExecutionAttribute<Region>(Region.class);
    public static final S3InternalSdkHttpExecutionAttribute<Path> OBJECT_FILE_PATH = new S3InternalSdkHttpExecutionAttribute<Path>(Path.class);

    private S3InternalSdkHttpExecutionAttribute(Class<T> valueClass) {
        super(valueClass);
    }
}

