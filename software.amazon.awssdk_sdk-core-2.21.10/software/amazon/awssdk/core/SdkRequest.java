/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.core;

import java.util.Optional;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkPojo;

@Immutable
@SdkPublicApi
public abstract class SdkRequest
implements SdkPojo {
    public abstract Optional<? extends RequestOverrideConfiguration> overrideConfiguration();

    public <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        return Optional.empty();
    }

    public abstract Builder toBuilder();

    public static interface Builder {
        public RequestOverrideConfiguration overrideConfiguration();

        public SdkRequest build();
    }
}

