/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;

@SdkInternalApi
public class SdkInternalTestAdvancedClientOption<T>
extends SdkAdvancedClientOption<T> {
    @SdkTestInternalApi
    public static final SdkInternalTestAdvancedClientOption<Boolean> ENDPOINT_OVERRIDDEN_OVERRIDE = new SdkInternalTestAdvancedClientOption<Boolean>(Boolean.class);

    protected SdkInternalTestAdvancedClientOption(Class<T> valueClass) {
        super(valueClass);
    }
}

