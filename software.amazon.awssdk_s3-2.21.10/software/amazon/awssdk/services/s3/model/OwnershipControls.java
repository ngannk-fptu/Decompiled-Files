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
import software.amazon.awssdk.services.s3.model.OwnershipControlsRule;
import software.amazon.awssdk.services.s3.model.OwnershipControlsRulesCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class OwnershipControls
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, OwnershipControls> {
    private static final SdkField<List<OwnershipControlsRule>> RULES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Rules").getter(OwnershipControls.getter(OwnershipControls::rules)).setter(OwnershipControls.setter(Builder::rules)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Rule").unmarshallLocationName("Rule").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(OwnershipControlsRule::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(RULES_FIELD));
    private static final long serialVersionUID = 1L;
    private final List<OwnershipControlsRule> rules;

    private OwnershipControls(BuilderImpl builder) {
        this.rules = builder.rules;
    }

    public final boolean hasRules() {
        return this.rules != null && !(this.rules instanceof SdkAutoConstructList);
    }

    public final List<OwnershipControlsRule> rules() {
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
        if (!(obj instanceof OwnershipControls)) {
            return false;
        }
        OwnershipControls other = (OwnershipControls)obj;
        return this.hasRules() == other.hasRules() && Objects.equals(this.rules(), other.rules());
    }

    public final String toString() {
        return ToString.builder((String)"OwnershipControls").add("Rules", this.hasRules() ? this.rules() : null).build();
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

    private static <T> Function<Object, T> getter(Function<OwnershipControls, T> g) {
        return obj -> g.apply((OwnershipControls)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private List<OwnershipControlsRule> rules = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(OwnershipControls model) {
            this.rules(model.rules);
        }

        public final List<OwnershipControlsRule.Builder> getRules() {
            List<OwnershipControlsRule.Builder> result = OwnershipControlsRulesCopier.copyToBuilder(this.rules);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setRules(Collection<OwnershipControlsRule.BuilderImpl> rules) {
            this.rules = OwnershipControlsRulesCopier.copyFromBuilder(rules);
        }

        @Override
        public final Builder rules(Collection<OwnershipControlsRule> rules) {
            this.rules = OwnershipControlsRulesCopier.copy(rules);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder rules(OwnershipControlsRule ... rules) {
            this.rules(Arrays.asList(rules));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder rules(Consumer<OwnershipControlsRule.Builder> ... rules) {
            this.rules(Stream.of(rules).map(c -> (OwnershipControlsRule)((OwnershipControlsRule.Builder)OwnershipControlsRule.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public OwnershipControls build() {
            return new OwnershipControls(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, OwnershipControls> {
        public Builder rules(Collection<OwnershipControlsRule> var1);

        public Builder rules(OwnershipControlsRule ... var1);

        public Builder rules(Consumer<OwnershipControlsRule.Builder> ... var1);
    }
}

