/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.core;

import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.RequestOverrideConfiguration;

@Immutable
@SdkPublicApi
public final class SdkRequestOverrideConfiguration
extends RequestOverrideConfiguration {
    private SdkRequestOverrideConfiguration(Builder builder) {
        super(builder);
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    private static final class BuilderImpl
    extends RequestOverrideConfiguration.BuilderImpl<Builder>
    implements Builder {
        private BuilderImpl() {
        }

        private BuilderImpl(SdkRequestOverrideConfiguration sdkRequestOverrideConfig) {
            super(sdkRequestOverrideConfig);
        }

        @Override
        public SdkRequestOverrideConfiguration build() {
            return new SdkRequestOverrideConfiguration(this);
        }
    }

    public static interface Builder
    extends RequestOverrideConfiguration.Builder<Builder> {
        @Override
        public SdkRequestOverrideConfiguration build();
    }
}

