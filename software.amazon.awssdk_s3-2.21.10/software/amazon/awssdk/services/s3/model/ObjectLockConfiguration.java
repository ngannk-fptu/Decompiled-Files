/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.io.Serializable;
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
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.ObjectLockEnabled;
import software.amazon.awssdk.services.s3.model.ObjectLockRule;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ObjectLockConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ObjectLockConfiguration> {
    private static final SdkField<String> OBJECT_LOCK_ENABLED_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ObjectLockEnabled").getter(ObjectLockConfiguration.getter(ObjectLockConfiguration::objectLockEnabledAsString)).setter(ObjectLockConfiguration.setter(Builder::objectLockEnabled)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ObjectLockEnabled").unmarshallLocationName("ObjectLockEnabled").build()}).build();
    private static final SdkField<ObjectLockRule> RULE_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Rule").getter(ObjectLockConfiguration.getter(ObjectLockConfiguration::rule)).setter(ObjectLockConfiguration.setter(Builder::rule)).constructor(ObjectLockRule::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Rule").unmarshallLocationName("Rule").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(OBJECT_LOCK_ENABLED_FIELD, RULE_FIELD));
    private static final long serialVersionUID = 1L;
    private final String objectLockEnabled;
    private final ObjectLockRule rule;

    private ObjectLockConfiguration(BuilderImpl builder) {
        this.objectLockEnabled = builder.objectLockEnabled;
        this.rule = builder.rule;
    }

    public final ObjectLockEnabled objectLockEnabled() {
        return ObjectLockEnabled.fromValue(this.objectLockEnabled);
    }

    public final String objectLockEnabledAsString() {
        return this.objectLockEnabled;
    }

    public final ObjectLockRule rule() {
        return this.rule;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.objectLockEnabledAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.rule());
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
        if (!(obj instanceof ObjectLockConfiguration)) {
            return false;
        }
        ObjectLockConfiguration other = (ObjectLockConfiguration)obj;
        return Objects.equals(this.objectLockEnabledAsString(), other.objectLockEnabledAsString()) && Objects.equals(this.rule(), other.rule());
    }

    public final String toString() {
        return ToString.builder((String)"ObjectLockConfiguration").add("ObjectLockEnabled", (Object)this.objectLockEnabledAsString()).add("Rule", (Object)this.rule()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ObjectLockEnabled": {
                return Optional.ofNullable(clazz.cast(this.objectLockEnabledAsString()));
            }
            case "Rule": {
                return Optional.ofNullable(clazz.cast(this.rule()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ObjectLockConfiguration, T> g) {
        return obj -> g.apply((ObjectLockConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String objectLockEnabled;
        private ObjectLockRule rule;

        private BuilderImpl() {
        }

        private BuilderImpl(ObjectLockConfiguration model) {
            this.objectLockEnabled(model.objectLockEnabled);
            this.rule(model.rule);
        }

        public final String getObjectLockEnabled() {
            return this.objectLockEnabled;
        }

        public final void setObjectLockEnabled(String objectLockEnabled) {
            this.objectLockEnabled = objectLockEnabled;
        }

        @Override
        public final Builder objectLockEnabled(String objectLockEnabled) {
            this.objectLockEnabled = objectLockEnabled;
            return this;
        }

        @Override
        public final Builder objectLockEnabled(ObjectLockEnabled objectLockEnabled) {
            this.objectLockEnabled(objectLockEnabled == null ? null : objectLockEnabled.toString());
            return this;
        }

        public final ObjectLockRule.Builder getRule() {
            return this.rule != null ? this.rule.toBuilder() : null;
        }

        public final void setRule(ObjectLockRule.BuilderImpl rule) {
            this.rule = rule != null ? rule.build() : null;
        }

        @Override
        public final Builder rule(ObjectLockRule rule) {
            this.rule = rule;
            return this;
        }

        public ObjectLockConfiguration build() {
            return new ObjectLockConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ObjectLockConfiguration> {
        public Builder objectLockEnabled(String var1);

        public Builder objectLockEnabled(ObjectLockEnabled var1);

        public Builder rule(ObjectLockRule var1);

        default public Builder rule(Consumer<ObjectLockRule.Builder> rule) {
            return this.rule((ObjectLockRule)((ObjectLockRule.Builder)ObjectLockRule.builder().applyMutation(rule)).build());
        }
    }
}

