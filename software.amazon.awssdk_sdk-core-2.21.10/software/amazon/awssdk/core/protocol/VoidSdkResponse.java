/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.builder.SdkBuilder
 */
package software.amazon.awssdk.core.protocol;

import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.utils.builder.SdkBuilder;

@SdkProtectedApi
public final class VoidSdkResponse
extends SdkResponse {
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Collections.emptyList());

    private VoidSdkResponse(Builder builder) {
        super(builder);
    }

    @Override
    public Builder toBuilder() {
        return VoidSdkResponse.builder();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    public static final class Builder
    extends SdkResponse.BuilderImpl
    implements SdkPojo,
    SdkBuilder<Builder, SdkResponse> {
        private Builder() {
        }

        @Override
        public SdkResponse build() {
            return new VoidSdkResponse(this);
        }

        @Override
        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }
}

