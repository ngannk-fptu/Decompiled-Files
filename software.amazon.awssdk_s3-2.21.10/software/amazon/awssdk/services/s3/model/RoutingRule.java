/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.RequiredTrait
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
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.Condition;
import software.amazon.awssdk.services.s3.model.Redirect;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class RoutingRule
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, RoutingRule> {
    private static final SdkField<Condition> CONDITION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Condition").getter(RoutingRule.getter(RoutingRule::condition)).setter(RoutingRule.setter(Builder::condition)).constructor(Condition::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Condition").unmarshallLocationName("Condition").build()}).build();
    private static final SdkField<Redirect> REDIRECT_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Redirect").getter(RoutingRule.getter(RoutingRule::redirect)).setter(RoutingRule.setter(Builder::redirect)).constructor(Redirect::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Redirect").unmarshallLocationName("Redirect").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(CONDITION_FIELD, REDIRECT_FIELD));
    private static final long serialVersionUID = 1L;
    private final Condition condition;
    private final Redirect redirect;

    private RoutingRule(BuilderImpl builder) {
        this.condition = builder.condition;
        this.redirect = builder.redirect;
    }

    public final Condition condition() {
        return this.condition;
    }

    public final Redirect redirect() {
        return this.redirect;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.condition());
        hashCode = 31 * hashCode + Objects.hashCode(this.redirect());
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
        if (!(obj instanceof RoutingRule)) {
            return false;
        }
        RoutingRule other = (RoutingRule)obj;
        return Objects.equals(this.condition(), other.condition()) && Objects.equals(this.redirect(), other.redirect());
    }

    public final String toString() {
        return ToString.builder((String)"RoutingRule").add("Condition", (Object)this.condition()).add("Redirect", (Object)this.redirect()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Condition": {
                return Optional.ofNullable(clazz.cast(this.condition()));
            }
            case "Redirect": {
                return Optional.ofNullable(clazz.cast(this.redirect()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<RoutingRule, T> g) {
        return obj -> g.apply((RoutingRule)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Condition condition;
        private Redirect redirect;

        private BuilderImpl() {
        }

        private BuilderImpl(RoutingRule model) {
            this.condition(model.condition);
            this.redirect(model.redirect);
        }

        public final Condition.Builder getCondition() {
            return this.condition != null ? this.condition.toBuilder() : null;
        }

        public final void setCondition(Condition.BuilderImpl condition) {
            this.condition = condition != null ? condition.build() : null;
        }

        @Override
        public final Builder condition(Condition condition) {
            this.condition = condition;
            return this;
        }

        public final Redirect.Builder getRedirect() {
            return this.redirect != null ? this.redirect.toBuilder() : null;
        }

        public final void setRedirect(Redirect.BuilderImpl redirect) {
            this.redirect = redirect != null ? redirect.build() : null;
        }

        @Override
        public final Builder redirect(Redirect redirect) {
            this.redirect = redirect;
            return this;
        }

        public RoutingRule build() {
            return new RoutingRule(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, RoutingRule> {
        public Builder condition(Condition var1);

        default public Builder condition(Consumer<Condition.Builder> condition) {
            return this.condition((Condition)((Condition.Builder)Condition.builder().applyMutation(condition)).build());
        }

        public Builder redirect(Redirect var1);

        default public Builder redirect(Consumer<Redirect.Builder> redirect) {
            return this.redirect((Redirect)((Redirect.Builder)Redirect.builder().applyMutation(redirect)).build());
        }
    }
}

