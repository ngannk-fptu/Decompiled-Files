/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.PayloadTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.PayloadTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.PolicyStatus;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetBucketPolicyStatusResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetBucketPolicyStatusResponse> {
    private static final SdkField<PolicyStatus> POLICY_STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("PolicyStatus").getter(GetBucketPolicyStatusResponse.getter(GetBucketPolicyStatusResponse::policyStatus)).setter(GetBucketPolicyStatusResponse.setter(Builder::policyStatus)).constructor(PolicyStatus::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PolicyStatus").unmarshallLocationName("PolicyStatus").build(), PayloadTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(POLICY_STATUS_FIELD));
    private final PolicyStatus policyStatus;

    private GetBucketPolicyStatusResponse(BuilderImpl builder) {
        super(builder);
        this.policyStatus = builder.policyStatus;
    }

    public final PolicyStatus policyStatus() {
        return this.policyStatus;
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static Class<? extends Builder> serializableBuilderClass() {
        return BuilderImpl.class;
    }

    public final int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.policyStatus());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return super.equals(obj) && this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GetBucketPolicyStatusResponse)) {
            return false;
        }
        GetBucketPolicyStatusResponse other = (GetBucketPolicyStatusResponse)((Object)obj);
        return Objects.equals(this.policyStatus(), other.policyStatus());
    }

    public final String toString() {
        return ToString.builder((String)"GetBucketPolicyStatusResponse").add("PolicyStatus", (Object)this.policyStatus()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "PolicyStatus": {
                return Optional.ofNullable(clazz.cast(this.policyStatus()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetBucketPolicyStatusResponse, T> g) {
        return obj -> g.apply((GetBucketPolicyStatusResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private PolicyStatus policyStatus;

        private BuilderImpl() {
        }

        private BuilderImpl(GetBucketPolicyStatusResponse model) {
            super(model);
            this.policyStatus(model.policyStatus);
        }

        public final PolicyStatus.Builder getPolicyStatus() {
            return this.policyStatus != null ? this.policyStatus.toBuilder() : null;
        }

        public final void setPolicyStatus(PolicyStatus.BuilderImpl policyStatus) {
            this.policyStatus = policyStatus != null ? policyStatus.build() : null;
        }

        @Override
        public final Builder policyStatus(PolicyStatus policyStatus) {
            this.policyStatus = policyStatus;
            return this;
        }

        @Override
        public GetBucketPolicyStatusResponse build() {
            return new GetBucketPolicyStatusResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetBucketPolicyStatusResponse> {
        public Builder policyStatus(PolicyStatus var1);

        default public Builder policyStatus(Consumer<PolicyStatus.Builder> policyStatus) {
            return this.policyStatus((PolicyStatus)((PolicyStatus.Builder)PolicyStatus.builder().applyMutation(policyStatus)).build());
        }
    }
}

