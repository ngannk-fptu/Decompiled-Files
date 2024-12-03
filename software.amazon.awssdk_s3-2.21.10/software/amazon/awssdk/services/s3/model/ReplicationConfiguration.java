/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.RequiredTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.io.Serializable;
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
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.ReplicationRule;
import software.amazon.awssdk.services.s3.model.ReplicationRulesCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ReplicationConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ReplicationConfiguration> {
    private static final SdkField<String> ROLE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Role").getter(ReplicationConfiguration.getter(ReplicationConfiguration::role)).setter(ReplicationConfiguration.setter(Builder::role)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Role").unmarshallLocationName("Role").build(), RequiredTrait.create()}).build();
    private static final SdkField<List<ReplicationRule>> RULES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Rules").getter(ReplicationConfiguration.getter(ReplicationConfiguration::rules)).setter(ReplicationConfiguration.setter(Builder::rules)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Rule").unmarshallLocationName("Rule").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(ReplicationRule::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ROLE_FIELD, RULES_FIELD));
    private static final long serialVersionUID = 1L;
    private final String role;
    private final List<ReplicationRule> rules;

    private ReplicationConfiguration(BuilderImpl builder) {
        this.role = builder.role;
        this.rules = builder.rules;
    }

    public final String role() {
        return this.role;
    }

    public final boolean hasRules() {
        return this.rules != null && !(this.rules instanceof SdkAutoConstructList);
    }

    public final List<ReplicationRule> rules() {
        return this.rules;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.role());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasRules() ? this.rules() : null);
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ReplicationConfiguration)) {
            return false;
        }
        ReplicationConfiguration other = (ReplicationConfiguration)obj;
        return Objects.equals(this.role(), other.role()) && this.hasRules() == other.hasRules() && Objects.equals(this.rules(), other.rules());
    }

    public final String toString() {
        return ToString.builder((String)"ReplicationConfiguration").add("Role", (Object)this.role()).add("Rules", this.hasRules() ? this.rules() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Role": {
                return Optional.ofNullable(clazz.cast(this.role()));
            }
            case "Rules": {
                return Optional.ofNullable(clazz.cast(this.rules()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ReplicationConfiguration, T> g) {
        return obj -> g.apply((ReplicationConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String role;
        private List<ReplicationRule> rules = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(ReplicationConfiguration model) {
            this.role(model.role);
            this.rules(model.rules);
        }

        public final String getRole() {
            return this.role;
        }

        public final void setRole(String role) {
            this.role = role;
        }

        @Override
        public final Builder role(String role) {
            this.role = role;
            return this;
        }

        public final List<ReplicationRule.Builder> getRules() {
            List<ReplicationRule.Builder> result = ReplicationRulesCopier.copyToBuilder(this.rules);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setRules(Collection<ReplicationRule.BuilderImpl> rules) {
            this.rules = ReplicationRulesCopier.copyFromBuilder(rules);
        }

        @Override
        public final Builder rules(Collection<ReplicationRule> rules) {
            this.rules = ReplicationRulesCopier.copy(rules);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder rules(ReplicationRule ... rules) {
            this.rules(Arrays.asList(rules));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder rules(Consumer<ReplicationRule.Builder> ... rules) {
            this.rules(Stream.of(rules).map(c -> (ReplicationRule)((ReplicationRule.Builder)ReplicationRule.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public ReplicationConfiguration build() {
            return new ReplicationConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ReplicationConfiguration> {
        public Builder role(String var1);

        public Builder rules(Collection<ReplicationRule> var1);

        public Builder rules(ReplicationRule ... var1);

        public Builder rules(Consumer<ReplicationRule.Builder> ... var1);
    }
}

