/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.s3.internal.multipart;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.multipart.MultipartConfiguration;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class MultipartConfigurationResolver {
    private static final long DEFAULT_MIN_PART_SIZE = 0x800000L;
    private final long minimalPartSizeInBytes;
    private final long apiCallBufferSize;
    private final long thresholdInBytes;

    public MultipartConfigurationResolver(MultipartConfiguration multipartConfiguration) {
        Validate.notNull((Object)multipartConfiguration, (String)"multipartConfiguration", (Object[])new Object[0]);
        this.minimalPartSizeInBytes = (Long)Validate.getOrDefault((Object)multipartConfiguration.minimumPartSizeInBytes(), () -> 0x800000L);
        this.apiCallBufferSize = (Long)Validate.getOrDefault((Object)multipartConfiguration.apiCallBufferSizeInBytes(), () -> this.minimalPartSizeInBytes * 4L);
        this.thresholdInBytes = (Long)Validate.getOrDefault((Object)multipartConfiguration.thresholdInBytes(), () -> this.minimalPartSizeInBytes);
    }

    public long minimalPartSizeInBytes() {
        return this.minimalPartSizeInBytes;
    }

    public long thresholdInBytes() {
        return this.thresholdInBytes;
    }

    public long apiCallBufferSize() {
        return this.apiCallBufferSize;
    }
}

