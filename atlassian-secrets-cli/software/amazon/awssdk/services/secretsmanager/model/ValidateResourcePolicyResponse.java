/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.model;

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
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerResponse;
import software.amazon.awssdk.services.secretsmanager.model.ValidationErrorsEntry;
import software.amazon.awssdk.services.secretsmanager.model.ValidationErrorsTypeCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ValidateResourcePolicyResponse
extends SecretsManagerResponse
implements ToCopyableBuilder<Builder, ValidateResourcePolicyResponse> {
    private static final SdkField<Boolean> POLICY_VALIDATION_PASSED_FIELD = SdkField.builder(MarshallingType.BOOLEAN).memberName("PolicyValidationPassed").getter(ValidateResourcePolicyResponse.getter(ValidateResourcePolicyResponse::policyValidationPassed)).setter(ValidateResourcePolicyResponse.setter(Builder::policyValidationPassed)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PolicyValidationPassed").build()).build();
    private static final SdkField<List<ValidationErrorsEntry>> VALIDATION_ERRORS_FIELD = SdkField.builder(MarshallingType.LIST).memberName("ValidationErrors").getter(ValidateResourcePolicyResponse.getter(ValidateResourcePolicyResponse::validationErrors)).setter(ValidateResourcePolicyResponse.setter(Builder::validationErrors)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ValidationErrors").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder(MarshallingType.SDK_POJO).constructor(ValidationErrorsEntry::builder).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()).build()).build()).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(POLICY_VALIDATION_PASSED_FIELD, VALIDATION_ERRORS_FIELD));
    private final Boolean policyValidationPassed;
    private final List<ValidationErrorsEntry> validationErrors;

    private ValidateResourcePolicyResponse(BuilderImpl builder) {
        super(builder);
        this.policyValidationPassed = builder.policyValidationPassed;
        this.validationErrors = builder.validationErrors;
    }

    public final Boolean policyValidationPassed() {
        return this.policyValidationPassed;
    }

    public final boolean hasValidationErrors() {
        return this.validationErrors != null && !(this.validationErrors instanceof SdkAutoConstructList);
    }

    public final List<ValidationErrorsEntry> validationErrors() {
        return this.validationErrors;
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

    @Override
    public final int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.policyValidationPassed());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasValidationErrors() ? this.validationErrors() : null);
        return hashCode;
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj) && this.equalsBySdkFields(obj);
    }

    @Override
    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ValidateResourcePolicyResponse)) {
            return false;
        }
        ValidateResourcePolicyResponse other = (ValidateResourcePolicyResponse)obj;
        return Objects.equals(this.policyValidationPassed(), other.policyValidationPassed()) && this.hasValidationErrors() == other.hasValidationErrors() && Objects.equals(this.validationErrors(), other.validationErrors());
    }

    public final String toString() {
        return ToString.builder("ValidateResourcePolicyResponse").add("PolicyValidationPassed", this.policyValidationPassed()).add("ValidationErrors", this.hasValidationErrors() ? this.validationErrors() : null).build();
    }

    @Override
    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "PolicyValidationPassed": {
                return Optional.ofNullable(clazz.cast(this.policyValidationPassed()));
            }
            case "ValidationErrors": {
                return Optional.ofNullable(clazz.cast(this.validationErrors()));
            }
        }
        return Optional.empty();
    }

    @Override
    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ValidateResourcePolicyResponse, T> g) {
        return obj -> g.apply((ValidateResourcePolicyResponse)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerResponse.BuilderImpl
    implements Builder {
        private Boolean policyValidationPassed;
        private List<ValidationErrorsEntry> validationErrors = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(ValidateResourcePolicyResponse model) {
            super(model);
            this.policyValidationPassed(model.policyValidationPassed);
            this.validationErrors(model.validationErrors);
        }

        public final Boolean getPolicyValidationPassed() {
            return this.policyValidationPassed;
        }

        public final void setPolicyValidationPassed(Boolean policyValidationPassed) {
            this.policyValidationPassed = policyValidationPassed;
        }

        @Override
        public final Builder policyValidationPassed(Boolean policyValidationPassed) {
            this.policyValidationPassed = policyValidationPassed;
            return this;
        }

        public final List<ValidationErrorsEntry.Builder> getValidationErrors() {
            List<ValidationErrorsEntry.Builder> result = ValidationErrorsTypeCopier.copyToBuilder(this.validationErrors);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setValidationErrors(Collection<ValidationErrorsEntry.BuilderImpl> validationErrors) {
            this.validationErrors = ValidationErrorsTypeCopier.copyFromBuilder(validationErrors);
        }

        @Override
        public final Builder validationErrors(Collection<ValidationErrorsEntry> validationErrors) {
            this.validationErrors = ValidationErrorsTypeCopier.copy(validationErrors);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder validationErrors(ValidationErrorsEntry ... validationErrors) {
            this.validationErrors(Arrays.asList(validationErrors));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder validationErrors(Consumer<ValidationErrorsEntry.Builder> ... validationErrors) {
            this.validationErrors(Stream.of(validationErrors).map(c -> (ValidationErrorsEntry)((ValidationErrorsEntry.Builder)ValidationErrorsEntry.builder().applyMutation(c)).build()).collect(Collectors.toList()));
            return this;
        }

        @Override
        public ValidateResourcePolicyResponse build() {
            return new ValidateResourcePolicyResponse(this);
        }

        @Override
        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ValidateResourcePolicyResponse> {
        public Builder policyValidationPassed(Boolean var1);

        public Builder validationErrors(Collection<ValidationErrorsEntry> var1);

        public Builder validationErrors(ValidationErrorsEntry ... var1);

        public Builder validationErrors(Consumer<ValidationErrorsEntry.Builder> ... var1);
    }
}

