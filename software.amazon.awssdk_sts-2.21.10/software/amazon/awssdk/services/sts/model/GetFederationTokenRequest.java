/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.sts.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.sts.model.PolicyDescriptorType;
import software.amazon.awssdk.services.sts.model.StsRequest;
import software.amazon.awssdk.services.sts.model.Tag;
import software.amazon.awssdk.services.sts.model._policyDescriptorListTypeCopier;
import software.amazon.awssdk.services.sts.model._tagListTypeCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetFederationTokenRequest
extends StsRequest
implements ToCopyableBuilder<Builder, GetFederationTokenRequest> {
    private static final SdkField<String> NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Name").getter(GetFederationTokenRequest.getter(GetFederationTokenRequest::name)).setter(GetFederationTokenRequest.setter(Builder::name)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Name").build()}).build();
    private static final SdkField<String> POLICY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Policy").getter(GetFederationTokenRequest.getter(GetFederationTokenRequest::policy)).setter(GetFederationTokenRequest.setter(Builder::policy)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Policy").build()}).build();
    private static final SdkField<List<PolicyDescriptorType>> POLICY_ARNS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("PolicyArns").getter(GetFederationTokenRequest.getter(GetFederationTokenRequest::policyArns)).setter(GetFederationTokenRequest.setter(Builder::policyArns)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PolicyArns").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(PolicyDescriptorType::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final SdkField<Integer> DURATION_SECONDS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("DurationSeconds").getter(GetFederationTokenRequest.getter(GetFederationTokenRequest::durationSeconds)).setter(GetFederationTokenRequest.setter(Builder::durationSeconds)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DurationSeconds").build()}).build();
    private static final SdkField<List<Tag>> TAGS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Tags").getter(GetFederationTokenRequest.getter(GetFederationTokenRequest::tags)).setter(GetFederationTokenRequest.setter(Builder::tags)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tags").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(Tag::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(NAME_FIELD, POLICY_FIELD, POLICY_ARNS_FIELD, DURATION_SECONDS_FIELD, TAGS_FIELD));
    private final String name;
    private final String policy;
    private final List<PolicyDescriptorType> policyArns;
    private final Integer durationSeconds;
    private final List<Tag> tags;

    private GetFederationTokenRequest(BuilderImpl builder) {
        super(builder);
        this.name = builder.name;
        this.policy = builder.policy;
        this.policyArns = builder.policyArns;
        this.durationSeconds = builder.durationSeconds;
        this.tags = builder.tags;
    }

    public final String name() {
        return this.name;
    }

    public final String policy() {
        return this.policy;
    }

    public final boolean hasPolicyArns() {
        return this.policyArns != null && !(this.policyArns instanceof SdkAutoConstructList);
    }

    public final List<PolicyDescriptorType> policyArns() {
        return this.policyArns;
    }

    public final Integer durationSeconds() {
        return this.durationSeconds;
    }

    public final boolean hasTags() {
        return this.tags != null && !(this.tags instanceof SdkAutoConstructList);
    }

    public final List<Tag> tags() {
        return this.tags;
    }

    @Override
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
        hashCode = 31 * hashCode + Objects.hashCode(this.name());
        hashCode = 31 * hashCode + Objects.hashCode(this.policy());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasPolicyArns() ? this.policyArns() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.durationSeconds());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasTags() ? this.tags() : null);
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
        if (!(obj instanceof GetFederationTokenRequest)) {
            return false;
        }
        GetFederationTokenRequest other = (GetFederationTokenRequest)((Object)obj);
        return Objects.equals(this.name(), other.name()) && Objects.equals(this.policy(), other.policy()) && this.hasPolicyArns() == other.hasPolicyArns() && Objects.equals(this.policyArns(), other.policyArns()) && Objects.equals(this.durationSeconds(), other.durationSeconds()) && this.hasTags() == other.hasTags() && Objects.equals(this.tags(), other.tags());
    }

    public final String toString() {
        return ToString.builder((String)"GetFederationTokenRequest").add("Name", (Object)this.name()).add("Policy", (Object)this.policy()).add("PolicyArns", this.hasPolicyArns() ? this.policyArns() : null).add("DurationSeconds", (Object)this.durationSeconds()).add("Tags", this.hasTags() ? this.tags() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Name": {
                return Optional.ofNullable(clazz.cast(this.name()));
            }
            case "Policy": {
                return Optional.ofNullable(clazz.cast(this.policy()));
            }
            case "PolicyArns": {
                return Optional.ofNullable(clazz.cast(this.policyArns()));
            }
            case "DurationSeconds": {
                return Optional.ofNullable(clazz.cast(this.durationSeconds()));
            }
            case "Tags": {
                return Optional.ofNullable(clazz.cast(this.tags()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetFederationTokenRequest, T> g) {
        return obj -> g.apply((GetFederationTokenRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends StsRequest.BuilderImpl
    implements Builder {
        private String name;
        private String policy;
        private List<PolicyDescriptorType> policyArns = DefaultSdkAutoConstructList.getInstance();
        private Integer durationSeconds;
        private List<Tag> tags = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(GetFederationTokenRequest model) {
            super(model);
            this.name(model.name);
            this.policy(model.policy);
            this.policyArns(model.policyArns);
            this.durationSeconds(model.durationSeconds);
            this.tags(model.tags);
        }

        public final String getName() {
            return this.name;
        }

        public final void setName(String name) {
            this.name = name;
        }

        @Override
        public final Builder name(String name) {
            this.name = name;
            return this;
        }

        public final String getPolicy() {
            return this.policy;
        }

        public final void setPolicy(String policy) {
            this.policy = policy;
        }

        @Override
        public final Builder policy(String policy) {
            this.policy = policy;
            return this;
        }

        public final List<PolicyDescriptorType.Builder> getPolicyArns() {
            List<PolicyDescriptorType.Builder> result = _policyDescriptorListTypeCopier.copyToBuilder(this.policyArns);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setPolicyArns(Collection<PolicyDescriptorType.BuilderImpl> policyArns) {
            this.policyArns = _policyDescriptorListTypeCopier.copyFromBuilder(policyArns);
        }

        @Override
        public final Builder policyArns(Collection<PolicyDescriptorType> policyArns) {
            this.policyArns = _policyDescriptorListTypeCopier.copy(policyArns);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder policyArns(PolicyDescriptorType ... policyArns) {
            this.policyArns(Arrays.asList(policyArns));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder policyArns(Consumer<PolicyDescriptorType.Builder> ... policyArns) {
            this.policyArns(Stream.of(policyArns).map(c -> (PolicyDescriptorType)((PolicyDescriptorType.Builder)PolicyDescriptorType.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final Integer getDurationSeconds() {
            return this.durationSeconds;
        }

        public final void setDurationSeconds(Integer durationSeconds) {
            this.durationSeconds = durationSeconds;
        }

        @Override
        public final Builder durationSeconds(Integer durationSeconds) {
            this.durationSeconds = durationSeconds;
            return this;
        }

        public final List<Tag.Builder> getTags() {
            List<Tag.Builder> result = _tagListTypeCopier.copyToBuilder(this.tags);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setTags(Collection<Tag.BuilderImpl> tags) {
            this.tags = _tagListTypeCopier.copyFromBuilder(tags);
        }

        @Override
        public final Builder tags(Collection<Tag> tags) {
            this.tags = _tagListTypeCopier.copy(tags);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder tags(Tag ... tags) {
            this.tags(Arrays.asList(tags));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder tags(Consumer<Tag.Builder> ... tags) {
            this.tags(Stream.of(tags).map(c -> (Tag)((Tag.Builder)Tag.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration overrideConfiguration) {
            super.overrideConfiguration(overrideConfiguration);
            return this;
        }

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> builderConsumer) {
            super.overrideConfiguration(builderConsumer);
            return this;
        }

        @Override
        public GetFederationTokenRequest build() {
            return new GetFederationTokenRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends StsRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetFederationTokenRequest> {
        public Builder name(String var1);

        public Builder policy(String var1);

        public Builder policyArns(Collection<PolicyDescriptorType> var1);

        public Builder policyArns(PolicyDescriptorType ... var1);

        public Builder policyArns(Consumer<PolicyDescriptorType.Builder> ... var1);

        public Builder durationSeconds(Integer var1);

        public Builder tags(Collection<Tag> var1);

        public Builder tags(Tag ... var1);

        public Builder tags(Consumer<Tag.Builder> ... var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

