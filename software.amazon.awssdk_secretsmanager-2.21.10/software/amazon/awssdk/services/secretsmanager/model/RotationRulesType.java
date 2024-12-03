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
package software.amazon.awssdk.services.secretsmanager.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class RotationRulesType
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, RotationRulesType> {
    private static final SdkField<Long> AUTOMATICALLY_AFTER_DAYS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LONG).memberName("AutomaticallyAfterDays").getter(RotationRulesType.getter(RotationRulesType::automaticallyAfterDays)).setter(RotationRulesType.setter(Builder::automaticallyAfterDays)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AutomaticallyAfterDays").build()}).build();
    private static final SdkField<String> DURATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Duration").getter(RotationRulesType.getter(RotationRulesType::duration)).setter(RotationRulesType.setter(Builder::duration)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Duration").build()}).build();
    private static final SdkField<String> SCHEDULE_EXPRESSION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ScheduleExpression").getter(RotationRulesType.getter(RotationRulesType::scheduleExpression)).setter(RotationRulesType.setter(Builder::scheduleExpression)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ScheduleExpression").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(AUTOMATICALLY_AFTER_DAYS_FIELD, DURATION_FIELD, SCHEDULE_EXPRESSION_FIELD));
    private static final long serialVersionUID = 1L;
    private final Long automaticallyAfterDays;
    private final String duration;
    private final String scheduleExpression;

    private RotationRulesType(BuilderImpl builder) {
        this.automaticallyAfterDays = builder.automaticallyAfterDays;
        this.duration = builder.duration;
        this.scheduleExpression = builder.scheduleExpression;
    }

    public final Long automaticallyAfterDays() {
        return this.automaticallyAfterDays;
    }

    public final String duration() {
        return this.duration;
    }

    public final String scheduleExpression() {
        return this.scheduleExpression;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.automaticallyAfterDays());
        hashCode = 31 * hashCode + Objects.hashCode(this.duration());
        hashCode = 31 * hashCode + Objects.hashCode(this.scheduleExpression());
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
        if (!(obj instanceof RotationRulesType)) {
            return false;
        }
        RotationRulesType other = (RotationRulesType)obj;
        return Objects.equals(this.automaticallyAfterDays(), other.automaticallyAfterDays()) && Objects.equals(this.duration(), other.duration()) && Objects.equals(this.scheduleExpression(), other.scheduleExpression());
    }

    public final String toString() {
        return ToString.builder((String)"RotationRulesType").add("AutomaticallyAfterDays", (Object)this.automaticallyAfterDays()).add("Duration", (Object)this.duration()).add("ScheduleExpression", (Object)this.scheduleExpression()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "AutomaticallyAfterDays": {
                return Optional.ofNullable(clazz.cast(this.automaticallyAfterDays()));
            }
            case "Duration": {
                return Optional.ofNullable(clazz.cast(this.duration()));
            }
            case "ScheduleExpression": {
                return Optional.ofNullable(clazz.cast(this.scheduleExpression()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<RotationRulesType, T> g) {
        return obj -> g.apply((RotationRulesType)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Long automaticallyAfterDays;
        private String duration;
        private String scheduleExpression;

        private BuilderImpl() {
        }

        private BuilderImpl(RotationRulesType model) {
            this.automaticallyAfterDays(model.automaticallyAfterDays);
            this.duration(model.duration);
            this.scheduleExpression(model.scheduleExpression);
        }

        public final Long getAutomaticallyAfterDays() {
            return this.automaticallyAfterDays;
        }

        public final void setAutomaticallyAfterDays(Long automaticallyAfterDays) {
            this.automaticallyAfterDays = automaticallyAfterDays;
        }

        @Override
        public final Builder automaticallyAfterDays(Long automaticallyAfterDays) {
            this.automaticallyAfterDays = automaticallyAfterDays;
            return this;
        }

        public final String getDuration() {
            return this.duration;
        }

        public final void setDuration(String duration) {
            this.duration = duration;
        }

        @Override
        public final Builder duration(String duration) {
            this.duration = duration;
            return this;
        }

        public final String getScheduleExpression() {
            return this.scheduleExpression;
        }

        public final void setScheduleExpression(String scheduleExpression) {
            this.scheduleExpression = scheduleExpression;
        }

        @Override
        public final Builder scheduleExpression(String scheduleExpression) {
            this.scheduleExpression = scheduleExpression;
            return this;
        }

        public RotationRulesType build() {
            return new RotationRulesType(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, RotationRulesType> {
        public Builder automaticallyAfterDays(Long var1);

        public Builder duration(String var1);

        public Builder scheduleExpression(String var1);
    }
}

