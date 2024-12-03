/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetRandomPasswordRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, GetRandomPasswordRequest> {
    private static final SdkField<Long> PASSWORD_LENGTH_FIELD = SdkField.builder((MarshallingType)MarshallingType.LONG).memberName("PasswordLength").getter(GetRandomPasswordRequest.getter(GetRandomPasswordRequest::passwordLength)).setter(GetRandomPasswordRequest.setter(Builder::passwordLength)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PasswordLength").build()}).build();
    private static final SdkField<String> EXCLUDE_CHARACTERS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExcludeCharacters").getter(GetRandomPasswordRequest.getter(GetRandomPasswordRequest::excludeCharacters)).setter(GetRandomPasswordRequest.setter(Builder::excludeCharacters)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ExcludeCharacters").build()}).build();
    private static final SdkField<Boolean> EXCLUDE_NUMBERS_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("ExcludeNumbers").getter(GetRandomPasswordRequest.getter(GetRandomPasswordRequest::excludeNumbers)).setter(GetRandomPasswordRequest.setter(Builder::excludeNumbers)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ExcludeNumbers").build()}).build();
    private static final SdkField<Boolean> EXCLUDE_PUNCTUATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("ExcludePunctuation").getter(GetRandomPasswordRequest.getter(GetRandomPasswordRequest::excludePunctuation)).setter(GetRandomPasswordRequest.setter(Builder::excludePunctuation)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ExcludePunctuation").build()}).build();
    private static final SdkField<Boolean> EXCLUDE_UPPERCASE_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("ExcludeUppercase").getter(GetRandomPasswordRequest.getter(GetRandomPasswordRequest::excludeUppercase)).setter(GetRandomPasswordRequest.setter(Builder::excludeUppercase)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ExcludeUppercase").build()}).build();
    private static final SdkField<Boolean> EXCLUDE_LOWERCASE_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("ExcludeLowercase").getter(GetRandomPasswordRequest.getter(GetRandomPasswordRequest::excludeLowercase)).setter(GetRandomPasswordRequest.setter(Builder::excludeLowercase)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ExcludeLowercase").build()}).build();
    private static final SdkField<Boolean> INCLUDE_SPACE_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IncludeSpace").getter(GetRandomPasswordRequest.getter(GetRandomPasswordRequest::includeSpace)).setter(GetRandomPasswordRequest.setter(Builder::includeSpace)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IncludeSpace").build()}).build();
    private static final SdkField<Boolean> REQUIRE_EACH_INCLUDED_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("RequireEachIncludedType").getter(GetRandomPasswordRequest.getter(GetRandomPasswordRequest::requireEachIncludedType)).setter(GetRandomPasswordRequest.setter(Builder::requireEachIncludedType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RequireEachIncludedType").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(PASSWORD_LENGTH_FIELD, EXCLUDE_CHARACTERS_FIELD, EXCLUDE_NUMBERS_FIELD, EXCLUDE_PUNCTUATION_FIELD, EXCLUDE_UPPERCASE_FIELD, EXCLUDE_LOWERCASE_FIELD, INCLUDE_SPACE_FIELD, REQUIRE_EACH_INCLUDED_TYPE_FIELD));
    private final Long passwordLength;
    private final String excludeCharacters;
    private final Boolean excludeNumbers;
    private final Boolean excludePunctuation;
    private final Boolean excludeUppercase;
    private final Boolean excludeLowercase;
    private final Boolean includeSpace;
    private final Boolean requireEachIncludedType;

    private GetRandomPasswordRequest(BuilderImpl builder) {
        super(builder);
        this.passwordLength = builder.passwordLength;
        this.excludeCharacters = builder.excludeCharacters;
        this.excludeNumbers = builder.excludeNumbers;
        this.excludePunctuation = builder.excludePunctuation;
        this.excludeUppercase = builder.excludeUppercase;
        this.excludeLowercase = builder.excludeLowercase;
        this.includeSpace = builder.includeSpace;
        this.requireEachIncludedType = builder.requireEachIncludedType;
    }

    public final Long passwordLength() {
        return this.passwordLength;
    }

    public final String excludeCharacters() {
        return this.excludeCharacters;
    }

    public final Boolean excludeNumbers() {
        return this.excludeNumbers;
    }

    public final Boolean excludePunctuation() {
        return this.excludePunctuation;
    }

    public final Boolean excludeUppercase() {
        return this.excludeUppercase;
    }

    public final Boolean excludeLowercase() {
        return this.excludeLowercase;
    }

    public final Boolean includeSpace() {
        return this.includeSpace;
    }

    public final Boolean requireEachIncludedType() {
        return this.requireEachIncludedType;
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

    public final int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.passwordLength());
        hashCode = 31 * hashCode + Objects.hashCode(this.excludeCharacters());
        hashCode = 31 * hashCode + Objects.hashCode(this.excludeNumbers());
        hashCode = 31 * hashCode + Objects.hashCode(this.excludePunctuation());
        hashCode = 31 * hashCode + Objects.hashCode(this.excludeUppercase());
        hashCode = 31 * hashCode + Objects.hashCode(this.excludeLowercase());
        hashCode = 31 * hashCode + Objects.hashCode(this.includeSpace());
        hashCode = 31 * hashCode + Objects.hashCode(this.requireEachIncludedType());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return super.equals(obj) && this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GetRandomPasswordRequest)) {
            return false;
        }
        GetRandomPasswordRequest other = (GetRandomPasswordRequest)((Object)obj);
        return Objects.equals(this.passwordLength(), other.passwordLength()) && Objects.equals(this.excludeCharacters(), other.excludeCharacters()) && Objects.equals(this.excludeNumbers(), other.excludeNumbers()) && Objects.equals(this.excludePunctuation(), other.excludePunctuation()) && Objects.equals(this.excludeUppercase(), other.excludeUppercase()) && Objects.equals(this.excludeLowercase(), other.excludeLowercase()) && Objects.equals(this.includeSpace(), other.includeSpace()) && Objects.equals(this.requireEachIncludedType(), other.requireEachIncludedType());
    }

    public final String toString() {
        return ToString.builder((String)"GetRandomPasswordRequest").add("PasswordLength", (Object)this.passwordLength()).add("ExcludeCharacters", (Object)this.excludeCharacters()).add("ExcludeNumbers", (Object)this.excludeNumbers()).add("ExcludePunctuation", (Object)this.excludePunctuation()).add("ExcludeUppercase", (Object)this.excludeUppercase()).add("ExcludeLowercase", (Object)this.excludeLowercase()).add("IncludeSpace", (Object)this.includeSpace()).add("RequireEachIncludedType", (Object)this.requireEachIncludedType()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "PasswordLength": {
                return Optional.ofNullable(clazz.cast(this.passwordLength()));
            }
            case "ExcludeCharacters": {
                return Optional.ofNullable(clazz.cast(this.excludeCharacters()));
            }
            case "ExcludeNumbers": {
                return Optional.ofNullable(clazz.cast(this.excludeNumbers()));
            }
            case "ExcludePunctuation": {
                return Optional.ofNullable(clazz.cast(this.excludePunctuation()));
            }
            case "ExcludeUppercase": {
                return Optional.ofNullable(clazz.cast(this.excludeUppercase()));
            }
            case "ExcludeLowercase": {
                return Optional.ofNullable(clazz.cast(this.excludeLowercase()));
            }
            case "IncludeSpace": {
                return Optional.ofNullable(clazz.cast(this.includeSpace()));
            }
            case "RequireEachIncludedType": {
                return Optional.ofNullable(clazz.cast(this.requireEachIncludedType()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetRandomPasswordRequest, T> g) {
        return obj -> g.apply((GetRandomPasswordRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private Long passwordLength;
        private String excludeCharacters;
        private Boolean excludeNumbers;
        private Boolean excludePunctuation;
        private Boolean excludeUppercase;
        private Boolean excludeLowercase;
        private Boolean includeSpace;
        private Boolean requireEachIncludedType;

        private BuilderImpl() {
        }

        private BuilderImpl(GetRandomPasswordRequest model) {
            super(model);
            this.passwordLength(model.passwordLength);
            this.excludeCharacters(model.excludeCharacters);
            this.excludeNumbers(model.excludeNumbers);
            this.excludePunctuation(model.excludePunctuation);
            this.excludeUppercase(model.excludeUppercase);
            this.excludeLowercase(model.excludeLowercase);
            this.includeSpace(model.includeSpace);
            this.requireEachIncludedType(model.requireEachIncludedType);
        }

        public final Long getPasswordLength() {
            return this.passwordLength;
        }

        public final void setPasswordLength(Long passwordLength) {
            this.passwordLength = passwordLength;
        }

        @Override
        public final Builder passwordLength(Long passwordLength) {
            this.passwordLength = passwordLength;
            return this;
        }

        public final String getExcludeCharacters() {
            return this.excludeCharacters;
        }

        public final void setExcludeCharacters(String excludeCharacters) {
            this.excludeCharacters = excludeCharacters;
        }

        @Override
        public final Builder excludeCharacters(String excludeCharacters) {
            this.excludeCharacters = excludeCharacters;
            return this;
        }

        public final Boolean getExcludeNumbers() {
            return this.excludeNumbers;
        }

        public final void setExcludeNumbers(Boolean excludeNumbers) {
            this.excludeNumbers = excludeNumbers;
        }

        @Override
        public final Builder excludeNumbers(Boolean excludeNumbers) {
            this.excludeNumbers = excludeNumbers;
            return this;
        }

        public final Boolean getExcludePunctuation() {
            return this.excludePunctuation;
        }

        public final void setExcludePunctuation(Boolean excludePunctuation) {
            this.excludePunctuation = excludePunctuation;
        }

        @Override
        public final Builder excludePunctuation(Boolean excludePunctuation) {
            this.excludePunctuation = excludePunctuation;
            return this;
        }

        public final Boolean getExcludeUppercase() {
            return this.excludeUppercase;
        }

        public final void setExcludeUppercase(Boolean excludeUppercase) {
            this.excludeUppercase = excludeUppercase;
        }

        @Override
        public final Builder excludeUppercase(Boolean excludeUppercase) {
            this.excludeUppercase = excludeUppercase;
            return this;
        }

        public final Boolean getExcludeLowercase() {
            return this.excludeLowercase;
        }

        public final void setExcludeLowercase(Boolean excludeLowercase) {
            this.excludeLowercase = excludeLowercase;
        }

        @Override
        public final Builder excludeLowercase(Boolean excludeLowercase) {
            this.excludeLowercase = excludeLowercase;
            return this;
        }

        public final Boolean getIncludeSpace() {
            return this.includeSpace;
        }

        public final void setIncludeSpace(Boolean includeSpace) {
            this.includeSpace = includeSpace;
        }

        @Override
        public final Builder includeSpace(Boolean includeSpace) {
            this.includeSpace = includeSpace;
            return this;
        }

        public final Boolean getRequireEachIncludedType() {
            return this.requireEachIncludedType;
        }

        public final void setRequireEachIncludedType(Boolean requireEachIncludedType) {
            this.requireEachIncludedType = requireEachIncludedType;
        }

        @Override
        public final Builder requireEachIncludedType(Boolean requireEachIncludedType) {
            this.requireEachIncludedType = requireEachIncludedType;
            return this;
        }

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration overrideConfiguration) {
            super.overrideConfiguration(overrideConfiguration);
            return this;
        }

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> builderConsumer) {
            super.overrideConfiguration(builderConsumer);
            return this;
        }

        @Override
        public GetRandomPasswordRequest build() {
            return new GetRandomPasswordRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetRandomPasswordRequest> {
        public Builder passwordLength(Long var1);

        public Builder excludeCharacters(String var1);

        public Builder excludeNumbers(Boolean var1);

        public Builder excludePunctuation(Boolean var1);

        public Builder excludeUppercase(Boolean var1);

        public Builder excludeLowercase(Boolean var1);

        public Builder includeSpace(Boolean var1);

        public Builder requireEachIncludedType(Boolean var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

