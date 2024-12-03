/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.client.config.ClientOption
 */
package software.amazon.awssdk.services.s3.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.client.config.ClientOption;

@SdkInternalApi
public class S3ClientOption<T>
extends ClientOption<T> {
    private S3ClientOption(Class<T> valueClass) {
        super(valueClass);
    }
}

