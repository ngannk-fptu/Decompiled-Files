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
import software.amazon.awssdk.services.s3.model.ServerSideEncryptionRule;
import software.amazon.awssdk.services.s3.model.ServerSideEncryptionRulesCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ServerSideEncryptionConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ServerSideEncryptionConfiguration> {
    private static final SdkField<List<ServerSideEncryptionRule>> RULES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Rules").getter(ServerSideEncryptionConfiguration.getter(ServerSideEncryptionConfiguration::rules)).setter(ServerSideEncryptionConfiguration.setter(Builder::rules)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Rule").unmarshallLocationName("Rule").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(ServerSideEncryptionRule::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(RULES_FIELD));
    private static final long serialVersionUID = 1L;
    private final List<ServerSideEncryptionRule> rules;

    private ServerSideEncryptionConfiguration(BuilderImpl builder) {
        this.rules = builder.rules;
    }

    public final boolean hasRules() {
        return this.rules != null && !(this.rules instanceof SdkAutoConstructList);
    }

    public final List<ServerSideEncryptionRule> rules() {
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
        if (!(obj instanceof ServerSideEncryptionConfiguration)) {
            return false;
        }
        ServerSideEncryptionConfiguration other = (ServerSideEncryptionConfiguration)obj;
        return this.hasRules() == other.hasRules() && Objects.equals(this.rules(), other.rules());
    }

    public final String toString() {
        return ToString.builder((String)"ServerSideEncryptionConfiguration").add("Rules", this.hasRules() ? this.rules() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Rules": {
                return Optional.ofNullable(clazz.cast(this.rules()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ServerSideEncryptionConfiguration, T> g) {
        return obj -> g.apply((ServerSideEncryptionConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private List<ServerSideEncryptionRule> rules = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(ServerSideEncryptionConfiguration model) {
            this.rules(model.rules);
        }

        public final List<ServerSideEncryptionRule.Builder> getRules() {
            List<ServerSideEncryptionRule.Builder> result = ServerSideEncryptionRulesCopier.copyToBuilder(this.rules);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setRules(Collection<ServerSideEncryptionRule.BuilderImpl> rules) {
            this.rules = ServerSideEncryptionRulesCopier.copyFromBuilder(rules);
        }

        @Override
        public final Builder rules(Collection<ServerSideEncryptionRule> rules) {
            this.rules = ServerSideEncryptionRulesCopier.copy(rules);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder rules(ServerSideEncryptionRule ... rules) {
            this.rules(Arrays.asList(rules));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder rules(Consumer<ServerSideEncryptionRule.Builder> ... rules) {
            this.rules(Stream.of(rules).map(c -> (ServerSideEncryptionRule)((ServerSideEncryptionRule.Builder)ServerSideEncryptionRule.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public ServerSideEncryptionConfiguration build() {
            return new ServerSideEncryptionConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ServerSideEncryptionConfiguration> {
        public Builder rules(Collection<ServerSideEncryptionRule> var1);

        public Builder rules(ServerSideEncryptionRule ... var1);

        public Builder rules(Consumer<ServerSideEncryptionRule.Builder> ... var1);
    }
}

