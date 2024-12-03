/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.AttributeMap$Key
 */
package software.amazon.awssdk.services.s3.endpoints;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.AttributeMap;

@SdkInternalApi
public final class S3ClientContextParams<T>
extends AttributeMap.Key<T> {
    public static final S3ClientContextParams<Boolean> ACCELERATE = new S3ClientContextParams<Boolean>(Boolean.class);
    public static final S3ClientContextParams<Boolean> DISABLE_MULTI_REGION_ACCESS_POINTS = new S3ClientContextParams<Boolean>(Boolean.class);
    public static final S3ClientContextParams<Boolean> FORCE_PATH_STYLE = new S3ClientContextParams<Boolean>(Boolean.class);
    public static final S3ClientContextParams<Boolean> USE_ARN_REGION = new S3ClientContextParams<Boolean>(Boolean.class);
    public static final S3ClientContextParams<Boolean> CROSS_REGION_ACCESS_ENABLED = new S3ClientContextParams<Boolean>(Boolean.class);

    private S3ClientContextParams(Class<T> valueClass) {
        super(valueClass);
    }
}

