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
import software.amazon.awssdk.services.sts.model.ProvidedContext;
import software.amazon.awssdk.services.sts.model.ProvidedContextsListTypeCopier;
import software.amazon.awssdk.services.sts.model.StsRequest;
import software.amazon.awssdk.services.sts.model.Tag;
import software.amazon.awssdk.services.sts.model._policyDescriptorListTypeCopier;
import software.amazon.awssdk.services.sts.model._tagKeyListTypeCopier;
import software.amazon.awssdk.services.sts.model._tagListTypeCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class AssumeRoleRequest
extends StsRequest
implements ToCopyableBuilder<Builder, AssumeRoleRequest> {
    private static final SdkField<String> ROLE_ARN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RoleArn").getter(AssumeRoleRequest.getter(AssumeRoleRequest::roleArn)).setter(AssumeRoleRequest.setter(Builder::roleArn)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RoleArn").build()}).build();
    private static final SdkField<String> ROLE_SESSION_NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RoleSessionName").getter(AssumeRoleRequest.getter(AssumeRoleRequest::roleSessionName)).setter(AssumeRoleRequest.setter(Builder::roleSessionName)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RoleSessionName").build()}).build();
    private static final SdkField<List<PolicyDescriptorType>> POLICY_ARNS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("PolicyArns").getter(AssumeRoleRequest.getter(AssumeRoleRequest::policyArns)).setter(AssumeRoleRequest.setter(Builder::policyArns)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PolicyArns").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(PolicyDescriptorType::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final SdkField<String> POLICY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Policy").getter(AssumeRoleRequest.getter(AssumeRoleRequest::policy)).setter(AssumeRoleRequest.setter(Builder::policy)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Policy").build()}).build();
    private static final SdkField<Integer> DURATION_SECONDS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("DurationSeconds").getter(AssumeRoleRequest.getter(AssumeRoleRequest::durationSeconds)).setter(AssumeRoleRequest.setter(Builder::durationSeconds)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DurationSeconds").build()}).build();
    private static final SdkField<List<Tag>> TAGS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Tags").getter(AssumeRoleRequest.getter(AssumeRoleRequest::tags)).setter(AssumeRoleRequest.setter(Builder::tags)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tags").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(Tag::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final SdkField<List<String>> TRANSITIVE_TAG_KEYS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("TransitiveTagKeys").getter(AssumeRoleRequest.getter(AssumeRoleRequest::transitiveTagKeys)).setter(AssumeRoleRequest.setter(Builder::transitiveTagKeys)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("TransitiveTagKeys").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final SdkField<String> EXTERNAL_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExternalId").getter(AssumeRoleRequest.getter(AssumeRoleRequest::externalId)).setter(AssumeRoleRequest.setter(Builder::externalId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ExternalId").build()}).build();
    private static final SdkField<String> SERIAL_NUMBER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SerialNumber").getter(AssumeRoleRequest.getter(AssumeRoleRequest::serialNumber)).setter(AssumeRoleRequest.setter(Builder::serialNumber)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SerialNumber").build()}).build();
    private static final SdkField<String> TOKEN_CODE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("TokenCode").getter(AssumeRoleRequest.getter(AssumeRoleRequest::tokenCode)).setter(AssumeRoleRequest.setter(Builder::tokenCode)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("TokenCode").build()}).build();
    private static final SdkField<String> SOURCE_IDENTITY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SourceIdentity").getter(AssumeRoleRequest.getter(AssumeRoleRequest::sourceIdentity)).setter(AssumeRoleRequest.setter(Builder::sourceIdentity)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SourceIdentity").build()}).build();
    private static final SdkField<List<ProvidedContext>> PROVIDED_CONTEXTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("ProvidedContexts").getter(AssumeRoleRequest.getter(AssumeRoleRequest::providedContexts)).setter(AssumeRoleRequest.setter(Builder::providedContexts)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ProvidedContexts").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(ProvidedContext::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ROLE_ARN_FIELD, ROLE_SESSION_NAME_FIELD, POLICY_ARNS_FIELD, POLICY_FIELD, DURATION_SECONDS_FIELD, TAGS_FIELD, TRANSITIVE_TAG_KEYS_FIELD, EXTERNAL_ID_FIELD, SERIAL_NUMBER_FIELD, TOKEN_CODE_FIELD, SOURCE_IDENTITY_FIELD, PROVIDED_CONTEXTS_FIELD));
    private final String roleArn;
    private final String roleSessionName;
    private final List<PolicyDescriptorType> policyArns;
    private final String policy;
    private final Integer durationSeconds;
    private final List<Tag> tags;
    private final List<String> transitiveTagKeys;
    private final String externalId;
    private final String serialNumber;
    private final String tokenCode;
    private final String sourceIdentity;
    private final List<ProvidedContext> providedContexts;

    private AssumeRoleRequest(BuilderImpl builder) {
        super(builder);
        this.roleArn = builder.roleArn;
        this.roleSessionName = builder.roleSessionName;
        this.policyArns = builder.policyArns;
        this.policy = builder.policy;
        this.durationSeconds = builder.durationSeconds;
        this.tags = builder.tags;
        this.transitiveTagKeys = builder.transitiveTagKeys;
        this.externalId = builder.externalId;
        this.serialNumber = builder.serialNumber;
        this.tokenCode = builder.tokenCode;
        this.sourceIdentity = builder.sourceIdentity;
        this.providedContexts = builder.providedContexts;
    }

    public final String roleArn() {
        return this.roleArn;
    }

    public final String roleSessionName() {
        return this.roleSessionName;
    }

    public final boolean hasPolicyArns() {
        return this.policyArns != null && !(this.policyArns instanceof SdkAutoConstructList);
    }

    public final List<PolicyDescriptorType> policyArns() {
        return this.policyArns;
    }

    public final String policy() {
        return this.policy;
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

    public final boolean hasTransitiveTagKeys() {
        return this.transitiveTagKeys != null && !(this.transitiveTagKeys instanceof SdkAutoConstructList);
    }

    public final List<String> transitiveTagKeys() {
        return this.transitiveTagKeys;
    }

    public final String externalId() {
        return this.externalId;
    }

    public final String serialNumber() {
        return this.serialNumber;
    }

    public final String tokenCode() {
        return this.tokenCode;
    }

    public final String sourceIdentity() {
        return this.sourceIdentity;
    }

    public final boolean hasProvidedContexts() {
        return this.providedContexts != null && !(this.providedContexts instanceof SdkAutoConstructList);
    }

    public final List<ProvidedContext> providedContexts() {
        return this.providedContexts;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.roleArn());
        hashCode = 31 * hashCode + Objects.hashCode(this.roleSessionName());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasPolicyArns() ? this.policyArns() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.policy());
        hashCode = 31 * hashCode + Objects.hashCode(this.durationSeconds());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasTags() ? this.tags() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.hasTransitiveTagKeys() ? this.transitiveTagKeys() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.externalId());
        hashCode = 31 * hashCode + Objects.hashCode(this.serialNumber());
        hashCode = 31 * hashCode + Objects.hashCode(this.tokenCode());
        hashCode = 31 * hashCode + Objects.hashCode(this.sourceIdentity());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasProvidedContexts() ? this.providedContexts() : null);
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
        if (!(obj instanceof AssumeRoleRequest)) {
            return false;
        }
        AssumeRoleRequest other = (AssumeRoleRequest)((Object)obj);
        return Objects.equals(this.roleArn(), other.roleArn()) && Objects.equals(this.roleSessionName(), other.roleSessionName()) && this.hasPolicyArns() == other.hasPolicyArns() && Objects.equals(this.policyArns(), other.policyArns()) && Objects.equals(this.policy(), other.policy()) && Objects.equals(this.durationSeconds(), other.durationSeconds()) && this.hasTags() == other.hasTags() && Objects.equals(this.tags(), other.tags()) && this.hasTransitiveTagKeys() == other.hasTransitiveTagKeys() && Objects.equals(this.transitiveTagKeys(), other.transitiveTagKeys()) && Objects.equals(this.externalId(), other.externalId()) && Objects.equals(this.serialNumber(), other.serialNumber()) && Objects.equals(this.tokenCode(), other.tokenCode()) && Objects.equals(this.sourceIdentity(), other.sourceIdentity()) && this.hasProvidedContexts() == other.hasProvidedContexts() && Objects.equals(this.providedContexts(), other.providedContexts());
    }

    public final String toString() {
        return ToString.builder((String)"AssumeRoleRequest").add("RoleArn", (Object)this.roleArn()).add("RoleSessionName", (Object)this.roleSessionName()).add("PolicyArns", this.hasPolicyArns() ? this.policyArns() : null).add("Policy", (Object)this.policy()).add("DurationSeconds", (Object)this.durationSeconds()).add("Tags", this.hasTags() ? this.tags() : null).add("TransitiveTagKeys", this.hasTransitiveTagKeys() ? this.transitiveTagKeys() : null).add("ExternalId", (Object)this.externalId()).add("SerialNumber", (Object)this.serialNumber()).add("TokenCode", (Object)this.tokenCode()).add("SourceIdentity", (Object)this.sourceIdentity()).add("ProvidedContexts", this.hasProvidedContexts() ? this.providedContexts() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "RoleArn": {
                return Optional.ofNullable(clazz.cast(this.roleArn()));
            }
            case "RoleSessionName": {
                return Optional.ofNullable(clazz.cast(this.roleSessionName()));
            }
            case "PolicyArns": {
                return Optional.ofNullable(clazz.cast(this.policyArns()));
            }
            case "Policy": {
                return Optional.ofNullable(clazz.cast(this.policy()));
            }
            case "DurationSeconds": {
                return Optional.ofNullable(clazz.cast(this.durationSeconds()));
            }
            case "Tags": {
                return Optional.ofNullable(clazz.cast(this.tags()));
            }
            case "TransitiveTagKeys": {
                return Optional.ofNullable(clazz.cast(this.transitiveTagKeys()));
            }
            case "ExternalId": {
                return Optional.ofNullable(clazz.cast(this.externalId()));
            }
            case "SerialNumber": {
                return Optional.ofNullable(clazz.cast(this.serialNumber()));
            }
            case "TokenCode": {
                return Optional.ofNullable(clazz.cast(this.tokenCode()));
            }
            case "SourceIdentity": {
                return Optional.ofNullable(clazz.cast(this.sourceIdentity()));
            }
            case "ProvidedContexts": {
                return Optional.ofNullable(clazz.cast(this.providedContexts()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<AssumeRoleRequest, T> g) {
        return obj -> g.apply((AssumeRoleRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends StsRequest.BuilderImpl
    implements Builder {
        private String roleArn;
        private String roleSessionName;
        private List<PolicyDescriptorType> policyArns = DefaultSdkAutoConstructList.getInstance();
        private String policy;
        private Integer durationSeconds;
        private List<Tag> tags = DefaultSdkAutoConstructList.getInstance();
        private List<String> transitiveTagKeys = DefaultSdkAutoConstructList.getInstance();
        private String externalId;
        private String serialNumber;
        private String tokenCode;
        private String sourceIdentity;
        private List<ProvidedContext> providedContexts = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(AssumeRoleRequest model) {
            super(model);
            this.roleArn(model.roleArn);
            this.roleSessionName(model.roleSessionName);
            this.policyArns(model.policyArns);
            this.policy(model.policy);
            this.durationSeconds(model.durationSeconds);
            this.tags(model.tags);
            this.transitiveTagKeys(model.transitiveTagKeys);
            this.externalId(model.externalId);
            this.serialNumber(model.serialNumber);
            this.tokenCode(model.tokenCode);
            this.sourceIdentity(model.sourceIdentity);
            this.providedContexts(model.providedContexts);
        }

        public final String getRoleArn() {
            return this.roleArn;
        }

        public final void setRoleArn(String roleArn) {
            this.roleArn = roleArn;
        }

        @Override
        public final Builder roleArn(String roleArn) {
            this.roleArn = roleArn;
            return this;
        }

        public final String getRoleSessionName() {
            return this.roleSessionName;
        }

        public final void setRoleSessionName(String roleSessionName) {
            this.roleSessionName = roleSessionName;
        }

        @Override
        public final Builder roleSessionName(String roleSessionName) {
            this.roleSessionName = roleSessionName;
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

        public final Collection<String> getTransitiveTagKeys() {
            if (this.transitiveTagKeys instanceof SdkAutoConstructList) {
                return null;
            }
            return this.transitiveTagKeys;
        }

        public final void setTransitiveTagKeys(Collection<String> transitiveTagKeys) {
            this.transitiveTagKeys = _tagKeyListTypeCopier.copy(transitiveTagKeys);
        }

        @Override
        public final Builder transitiveTagKeys(Collection<String> transitiveTagKeys) {
            this.transitiveTagKeys = _tagKeyListTypeCopier.copy(transitiveTagKeys);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder transitiveTagKeys(String ... transitiveTagKeys) {
            this.transitiveTagKeys(Arrays.asList(transitiveTagKeys));
            return this;
        }

        public final String getExternalId() {
            return this.externalId;
        }

        public final void setExternalId(String externalId) {
            this.externalId = externalId;
        }

        @Override
        public final Builder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public final String getSerialNumber() {
            return this.serialNumber;
        }

        public final void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        @Override
        public final Builder serialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

        public final String getTokenCode() {
            return this.tokenCode;
        }

        public final void setTokenCode(String tokenCode) {
            this.tokenCode = tokenCode;
        }

        @Override
        public final Builder tokenCode(String tokenCode) {
            this.tokenCode = tokenCode;
            return this;
        }

        public final String getSourceIdentity() {
            return this.sourceIdentity;
        }

        public final void setSourceIdentity(String sourceIdentity) {
            this.sourceIdentity = sourceIdentity;
        }

        @Override
        public final Builder sourceIdentity(String sourceIdentity) {
            this.sourceIdentity = sourceIdentity;
            return this;
        }

        public final List<ProvidedContext.Builder> getProvidedContexts() {
            List<ProvidedContext.Builder> result = ProvidedContextsListTypeCopier.copyToBuilder(this.providedContexts);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setProvidedContexts(Collection<ProvidedContext.BuilderImpl> providedContexts) {
            this.providedContexts = ProvidedContextsListTypeCopier.copyFromBuilder(providedContexts);
        }

        @Override
        public final Builder providedContexts(Collection<ProvidedContext> providedContexts) {
            this.providedContexts = ProvidedContextsListTypeCopier.copy(providedContexts);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder providedContexts(ProvidedContext ... providedContexts) {
            this.providedContexts(Arrays.asList(providedContexts));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder providedContexts(Consumer<ProvidedContext.Builder> ... providedContexts) {
            this.providedContexts(Stream.of(providedContexts).map(c -> (ProvidedContext)((ProvidedContext.Builder)ProvidedContext.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
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
        public AssumeRoleRequest build() {
            return new AssumeRoleRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends StsRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, AssumeRoleRequest> {
        public Builder roleArn(String var1);

        public Builder roleSessionName(String var1);

        public Builder policyArns(Collection<PolicyDescriptorType> var1);

        public Builder policyArns(PolicyDescriptorType ... var1);

        public Builder policyArns(Consumer<PolicyDescriptorType.Builder> ... var1);

        public Builder policy(String var1);

        public Builder durationSeconds(Integer var1);

        public Builder tags(Collection<Tag> var1);

        public Builder tags(Tag ... var1);

        public Builder tags(Consumer<Tag.Builder> ... var1);

        public Builder transitiveTagKeys(Collection<String> var1);

        public Builder transitiveTagKeys(String ... var1);

        public Builder externalId(String var1);

        public Builder serialNumber(String var1);

        public Builder tokenCode(String var1);

        public Builder sourceIdentity(String var1);

        public Builder providedContexts(Collection<ProvidedContext> var1);

        public Builder providedContexts(ProvidedContext ... var1);

        public Builder providedContexts(Consumer<ProvidedContext.Builder> ... var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

