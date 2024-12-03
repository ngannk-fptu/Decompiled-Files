/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.client.config.ClientOption
 */
package software.amazon.awssdk.services.sts.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.client.config.ClientOption;

@SdkInternalApi
public class StsClientOption<T>
extends ClientOption<T> {
    private StsClientOption(Class<T> valueClass) {
        super(valueClass);
    }
}

