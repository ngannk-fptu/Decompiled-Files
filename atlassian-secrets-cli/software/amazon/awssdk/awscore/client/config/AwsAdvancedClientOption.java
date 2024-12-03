/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.client.config;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;

@SdkPublicApi
public final class AwsAdvancedClientOption<T>
extends SdkAdvancedClientOption<T> {
    public static final AwsAdvancedClientOption<Boolean> ENABLE_DEFAULT_REGION_DETECTION = new AwsAdvancedClientOption<Boolean>(Boolean.class);

    private AwsAdvancedClientOption(Class<T> valueClass) {
        super(valueClass);
    }
}

