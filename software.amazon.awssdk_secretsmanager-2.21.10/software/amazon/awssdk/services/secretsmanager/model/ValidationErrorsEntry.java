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

public final class ValidationErrorsEntry
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ValidationErrorsEntry> {
    private static final SdkField<String> CHECK_NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CheckName").getter(ValidationErrorsEntry.getter(ValidationErrorsEntry::checkName)).setter(ValidationErrorsEntry.setter(Builder::checkName)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CheckName").build()}).build();
    private static final SdkField<String> ERROR_MESSAGE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ErrorMessage").getter(ValidationErrorsEntry.getter(ValidationErrorsEntry::errorMessage)).setter(ValidationErrorsEntry.setter(Builder::errorMessage)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ErrorMessage").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(CHECK_NAME_FIELD, ERROR_MESSAGE_FIELD));
    private static final long serialVersionUID = 1L;
    private final String checkName;
    private final String errorMessage;

    private ValidationErrorsEntry(BuilderImpl builder) {
        this.checkName = builder.checkName;
        this.errorMessage = builder.errorMessage;
    }

    public final String checkName() {
        return this.checkName;
    }

    public final String errorMessage() {
        return this.errorMessage;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.checkName());
        hashCode = 31 * hashCode + Objects.hashCode(this.errorMessage());
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
        if (!(obj instanceof ValidationErrorsEntry)) {
            return false;
        }
        ValidationErrorsEntry other = (ValidationErrorsEntry)obj;
        return Objects.equals(this.checkName(), other.checkName()) && Objects.equals(this.errorMessage(), other.errorMessage());
    }

    public final String toString() {
        return ToString.builder((String)"ValidationErrorsEntry").add("CheckName", (Object)this.checkName()).add("ErrorMessage", (Object)this.errorMessage()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "CheckName": {
                return Optional.ofNullable(clazz.cast(this.checkName()));
            }
            case "ErrorMessage": {
                return Optional.ofNullable(clazz.cast(this.errorMessage()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ValidationErrorsEntry, T> g) {
        return obj -> g.apply((ValidationErrorsEntry)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String checkName;
        private String errorMessage;

        private BuilderImpl() {
        }

        private BuilderImpl(ValidationErrorsEntry model) {
            this.checkName(model.checkName);
            this.errorMessage(model.errorMessage);
        }

        public final String getCheckName() {
            return this.checkName;
        }

        public final void setCheckName(String checkName) {
            this.checkName = checkName;
        }

        @Override
        public final Builder checkName(String checkName) {
            this.checkName = checkName;
            return this;
        }

        public final String getErrorMessage() {
            return this.errorMessage;
        }

        public final void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public final Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public ValidationErrorsEntry build() {
            return new ValidationErrorsEntry(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ValidationErrorsEntry> {
        public Builder checkName(String var1);

        public Builder errorMessage(String var1);
    }
}

