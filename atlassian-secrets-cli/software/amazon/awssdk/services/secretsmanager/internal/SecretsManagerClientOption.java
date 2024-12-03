/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.client.config.ClientOption;

@SdkInternalApi
public class SecretsManagerClientOption<T>
extends ClientOption<T> {
    private SecretsManagerClientOption(Class<T> valueClass) {
        super(valueClass);
    }
}

