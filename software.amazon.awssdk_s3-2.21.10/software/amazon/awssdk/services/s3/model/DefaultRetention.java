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
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.ObjectLockRetentionMode;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class DefaultRetention
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, DefaultRetention> {
    private static final SdkField<String> MODE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Mode").getter(DefaultRetention.getter(DefaultRetention::modeAsString)).setter(DefaultRetention.setter(Builder::mode)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Mode").unmarshallLocationName("Mode").build()}).build();
    private static final SdkField<Integer> DAYS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("Days").getter(DefaultRetention.getter(DefaultRetention::days)).setter(DefaultRetention.setter(Builder::days)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Days").unmarshallLocationName("Days").build()}).build();
    private static final SdkField<Integer> YEARS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("Years").getter(DefaultRetention.getter(DefaultRetention::years)).setter(DefaultRetention.setter(Builder::years)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Years").unmarshallLocationName("Years").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(MODE_FIELD, DAYS_FIELD, YEARS_FIELD));
    private static final long serialVersionUID = 1L;
    private final String mode;
    private final Integer days;
    private final Integer years;

    private DefaultRetention(BuilderImpl builder) {
        this.mode = builder.mode;
        this.days = builder.days;
        this.years = builder.years;
    }

    public final ObjectLockRetentionMode mode() {
        return ObjectLockRetentionMode.fromValue(this.mode);
    }

    public final String modeAsString() {
        return this.mode;
    }

    public final Integer days() {
        return this.days;
    }

    public final Integer years() {
        return this.years;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.modeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.days());
        hashCode = 31 * hashCode + Objects.hashCode(this.years());
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
        if (!(obj instanceof DefaultRetention)) {
            return false;
        }
        DefaultRetention other = (DefaultRetention)obj;
        return Objects.equals(this.modeAsString(), other.modeAsString()) && Objects.equals(this.days(), other.days()) && Objects.equals(this.years(), other.years());
    }

    public final String toString() {
        return ToString.builder((String)"DefaultRetention").add("Mode", (Object)this.modeAsString()).add("Days", (Object)this.days()).add("Years", (Object)this.years()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Mode": {
                return Optional.ofNullable(clazz.cast(this.modeAsString()));
            }
            case "Days": {
                return Optional.ofNullable(clazz.cast(this.days()));
            }
            case "Years": {
                return Optional.ofNullable(clazz.cast(this.years()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<DefaultRetention, T> g) {
        return obj -> g.apply((DefaultRetention)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String mode;
        private Integer days;
        private Integer years;

        private BuilderImpl() {
        }

        private BuilderImpl(DefaultRetention model) {
            this.mode(model.mode);
            this.days(model.days);
            this.years(model.years);
        }

        public final String getMode() {
            return this.mode;
        }

        public final void setMode(String mode) {
            this.mode = mode;
        }

        @Override
        public final Builder mode(String mode) {
            this.mode = mode;
            return this;
        }

        @Override
        public final Builder mode(ObjectLockRetentionMode mode) {
            this.mode(mode == null ? null : mode.toString());
            return this;
        }

        public final Integer getDays() {
            return this.days;
        }

        public final void setDays(Integer days) {
            this.days = days;
        }

        @Override
        public final Builder days(Integer days) {
            this.days = days;
            return this;
        }

        public final Integer getYears() {
            return this.years;
        }

        public final void setYears(Integer years) {
            this.years = years;
        }

        @Override
        public final Builder years(Integer years) {
            this.years = years;
            return this;
        }

        public DefaultRetention build() {
            return new DefaultRetention(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, DefaultRetention> {
        public Builder mode(String var1);

        public Builder mode(ObjectLockRetentionMode var1);

        public Builder days(Integer var1);

        public Builder years(Integer var1);
    }
}

