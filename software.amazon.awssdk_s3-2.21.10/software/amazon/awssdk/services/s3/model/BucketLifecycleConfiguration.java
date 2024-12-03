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
import software.amazon.awssdk.services.s3.model.LifecycleRule;
import software.amazon.awssdk.services.s3.model.LifecycleRulesCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class BucketLifecycleConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, BucketLifecycleConfiguration> {
    private static final SdkField<List<LifecycleRule>> RULES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Rules").getter(BucketLifecycleConfiguration.getter(BucketLifecycleConfiguration::rules)).setter(BucketLifecycleConfiguration.setter(Builder::rules)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Rule").unmarshallLocationName("Rule").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(LifecycleRule::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(RULES_FIELD));
    private static final long serialVersionUID = 1L;
    private final List<LifecycleRule> rules;

    private BucketLifecycleConfiguration(BuilderImpl builder) {
        this.rules = builder.rules;
    }

    public final boolean hasRules() {
        return this.rules != null && !(this.rules instanceof SdkAutoConstructList);
    }

    public final List<LifecycleRule> rules() {
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
        if (!(obj instanceof BucketLifecycleConfiguration)) {
            return false;
        }
        BucketLifecycleConfiguration other = (BucketLifecycleConfiguration)obj;
        return this.hasRules() == other.hasRules() && Objects.equals(this.rules(), other.rules());
    }

    public final String toString() {
        return ToString.builder((String)"BucketLifecycleConfiguration").add("Rules", this.hasRules() ? this.rules() : null).build();
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

    private static <T> Function<Object, T> getter(Function<BucketLifecycleConfiguration, T> g) {
        return obj -> g.apply((BucketLifecycleConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private List<LifecycleRule> rules = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(BucketLifecycleConfiguration model) {
            this.rules(model.rules);
        }

        public final List<LifecycleRule.Builder> getRules() {
            List<LifecycleRule.Builder> result = LifecycleRulesCopier.copyToBuilder(this.rules);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setRules(Collection<LifecycleRule.BuilderImpl> rules) {
            this.rules = LifecycleRulesCopier.copyFromBuilder(rules);
        }

        @Override
        public final Builder rules(Collection<LifecycleRule> rules) {
            this.rules = LifecycleRulesCopier.copy(rules);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder rules(LifecycleRule ... rules) {
            this.rules(Arrays.asList(rules));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder rules(Consumer<LifecycleRule.Builder> ... rules) {
            this.rules(Stream.of(rules).map(c -> (LifecycleRule)((LifecycleRule.Builder)LifecycleRule.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public BucketLifecycleConfiguration build() {
            return new BucketLifecycleConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, BucketLifecycleConfiguration> {
        public Builder rules(Collection<LifecycleRule> var1);

        public Builder rules(LifecycleRule ... var1);

        public Builder rules(Consumer<LifecycleRule.Builder> ... var1);
    }
}

