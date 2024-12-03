/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.client.config.ClientOption;

@SdkPublicApi
public class ServiceMetadataAdvancedOption<T>
extends ClientOption<T> {
    public static final ServiceMetadataAdvancedOption<String> DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT = new ServiceMetadataAdvancedOption<String>(String.class);

    protected ServiceMetadataAdvancedOption(Class<T> valueClass) {
        super(valueClass);
    }
}

