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
import software.amazon.awssdk.services.sts.model._policyDescriptorListTypeCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class AssumeRoleWithSamlRequest
extends StsRequest
implements ToCopyableBuilder<Builder, AssumeRoleWithSamlRequest> {
    private static final SdkField<String> ROLE_ARN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RoleArn").getter(AssumeRoleWithSamlRequest.getter(AssumeRoleWithSamlRequest::roleArn)).setter(AssumeRoleWithSamlRequest.setter(Builder::roleArn)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RoleArn").build()}).build();
    private static final SdkField<String> PRINCIPAL_ARN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("PrincipalArn").getter(AssumeRoleWithSamlRequest.getter(AssumeRoleWithSamlRequest::principalArn)).setter(AssumeRoleWithSamlRequest.setter(Builder::principalArn)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PrincipalArn").build()}).build();
    private static final SdkField<String> SAML_ASSERTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SAMLAssertion").getter(AssumeRoleWithSamlRequest.getter(AssumeRoleWithSamlRequest::samlAssertion)).setter(AssumeRoleWithSamlRequest.setter(Builder::samlAssertion)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SAMLAssertion").build()}).build();
    private static final SdkField<List<PolicyDescriptorType>> POLICY_ARNS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("PolicyArns").getter(AssumeRoleWithSamlRequest.getter(AssumeRoleWithSamlRequest::policyArns)).setter(AssumeRoleWithSamlRequest.setter(Builder::policyArns)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PolicyArns").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(PolicyDescriptorType::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final SdkField<String> POLICY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Policy").getter(AssumeRoleWithSamlRequest.getter(AssumeRoleWithSamlRequest::policy)).setter(AssumeRoleWithSamlRequest.setter(Builder::policy)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Policy").build()}).build();
    private static final SdkField<Integer> DURATION_SECONDS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("DurationSeconds").getter(AssumeRoleWithSamlRequest.getter(AssumeRoleWithSamlRequest::durationSeconds)).setter(AssumeRoleWithSamlRequest.setter(Builder::durationSeconds)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DurationSeconds").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ROLE_ARN_FIELD, PRINCIPAL_ARN_FIELD, SAML_ASSERTION_FIELD, POLICY_ARNS_FIELD, POLICY_FIELD, DURATION_SECONDS_FIELD));
    private final String roleArn;
    private final String principalArn;
    private final String samlAssertion;
    private final List<PolicyDescriptorType> policyArns;
    private final String policy;
    private final Integer durationSeconds;

    private AssumeRoleWithSamlRequest(BuilderImpl builder) {
        super(builder);
        this.roleArn = builder.roleArn;
        this.principalArn = builder.principalArn;
        this.samlAssertion = builder.samlAssertion;
        this.policyArns = builder.policyArns;
        this.policy = builder.policy;
        this.durationSeconds = builder.durationSeconds;
    }

    public final String roleArn() {
        return this.roleArn;
    }

    public final String principalArn() {
        return this.principalArn;
    }

    public final String samlAssertion() {
        return this.samlAssertion;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.principalArn());
        hashCode = 31 * hashCode + Objects.hashCode(this.samlAssertion());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasPolicyArns() ? this.policyArns() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.policy());
        hashCode = 31 * hashCode + Objects.hashCode(this.durationSeconds());
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
        if (!(obj instanceof AssumeRoleWithSamlRequest)) {
            return false;
        }
        AssumeRoleWithSamlRequest other = (AssumeRoleWithSamlRequest)((Object)obj);
        return Objects.equals(this.roleArn(), other.roleArn()) && Objects.equals(this.principalArn(), other.principalArn()) && Objects.equals(this.samlAssertion(), other.samlAssertion()) && this.hasPolicyArns() == other.hasPolicyArns() && Objects.equals(this.policyArns(), other.policyArns()) && Objects.equals(this.policy(), other.policy()) && Objects.equals(this.durationSeconds(), other.durationSeconds());
    }

    public final String toString() {
        return ToString.builder((String)"AssumeRoleWithSamlRequest").add("RoleArn", (Object)this.roleArn()).add("PrincipalArn", (Object)this.principalArn()).add("SAMLAssertion", (Object)(this.samlAssertion() == null ? null : "*** Sensitive Data Redacted ***")).add("PolicyArns", this.hasPolicyArns() ? this.policyArns() : null).add("Policy", (Object)this.policy()).add("DurationSeconds", (Object)this.durationSeconds()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "RoleArn": {
                return Optional.ofNullable(clazz.cast(this.roleArn()));
            }
            case "PrincipalArn": {
                return Optional.ofNullable(clazz.cast(this.principalArn()));
            }
            case "SAMLAssertion": {
                return Optional.ofNullable(clazz.cast(this.samlAssertion()));
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
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<AssumeRoleWithSamlRequest, T> g) {
        return obj -> g.apply((AssumeRoleWithSamlRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends StsRequest.BuilderImpl
    implements Builder {
        private String roleArn;
        private String principalArn;
        private String samlAssertion;
        private List<PolicyDescriptorType> policyArns = DefaultSdkAutoConstructList.getInstance();
        private String policy;
        private Integer durationSeconds;

        private BuilderImpl() {
        }

        private BuilderImpl(AssumeRoleWithSamlRequest model) {
            super(model);
            this.roleArn(model.roleArn);
            this.principalArn(model.principalArn);
            this.samlAssertion(model.samlAssertion);
            this.policyArns(model.policyArns);
            this.policy(model.policy);
            this.durationSeconds(model.durationSeconds);
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

        public final String getPrincipalArn() {
            return this.principalArn;
        }

        public final void setPrincipalArn(String principalArn) {
            this.principalArn = principalArn;
        }

        @Override
        public final Builder principalArn(String principalArn) {
            this.principalArn = principalArn;
            return this;
        }

        public final String getSamlAssertion() {
            return this.samlAssertion;
        }

        public final void setSamlAssertion(String samlAssertion) {
            this.samlAssertion = samlAssertion;
        }

        @Override
        public final Builder samlAssertion(String samlAssertion) {
            this.samlAssertion = samlAssertion;
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
        public AssumeRoleWithSamlRequest build() {
            return new AssumeRoleWithSamlRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends StsRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, AssumeRoleWithSamlRequest> {
        public Builder roleArn(String var1);

        public Builder principalArn(String var1);

        public Builder samlAssertion(String var1);

        public Builder policyArns(Collection<PolicyDescriptorType> var1);

        public Builder policyArns(PolicyDescriptorType ... var1);

        public Builder policyArns(Consumer<PolicyDescriptorType.Builder> ... var1);

        public Builder policy(String var1);

        public Builder durationSeconds(Integer var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

