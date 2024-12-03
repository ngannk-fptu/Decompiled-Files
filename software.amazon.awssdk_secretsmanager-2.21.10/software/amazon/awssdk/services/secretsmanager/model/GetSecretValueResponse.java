/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkBytes
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.secretsmanager.model.SecretVersionStagesTypeCopier;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerResponse;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetSecretValueResponse
extends SecretsManagerResponse
implements ToCopyableBuilder<Builder, GetSecretValueResponse> {
    private static final SdkField<String> ARN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ARN").getter(GetSecretValueResponse.getter(GetSecretValueResponse::arn)).setter(GetSecretValueResponse.setter(Builder::arn)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ARN").build()}).build();
    private static final SdkField<String> NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Name").getter(GetSecretValueResponse.getter(GetSecretValueResponse::name)).setter(GetSecretValueResponse.setter(Builder::name)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Name").build()}).build();
    private static final SdkField<String> VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("VersionId").getter(GetSecretValueResponse.getter(GetSecretValueResponse::versionId)).setter(GetSecretValueResponse.setter(Builder::versionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("VersionId").build()}).build();
    private static final SdkField<SdkBytes> SECRET_BINARY_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_BYTES).memberName("SecretBinary").getter(GetSecretValueResponse.getter(GetSecretValueResponse::secretBinary)).setter(GetSecretValueResponse.setter(Builder::secretBinary)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretBinary").build()}).build();
    private static final SdkField<String> SECRET_STRING_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SecretString").getter(GetSecretValueResponse.getter(GetSecretValueResponse::secretString)).setter(GetSecretValueResponse.setter(Builder::secretString)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretString").build()}).build();
    private static final SdkField<List<String>> VERSION_STAGES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("VersionStages").getter(GetSecretValueResponse.getter(GetSecretValueResponse::versionStages)).setter(GetSecretValueResponse.setter(Builder::versionStages)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("VersionStages").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final SdkField<Instant> CREATED_DATE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("CreatedDate").getter(GetSecretValueResponse.getter(GetSecretValueResponse::createdDate)).setter(GetSecretValueResponse.setter(Builder::createdDate)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CreatedDate").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ARN_FIELD, NAME_FIELD, VERSION_ID_FIELD, SECRET_BINARY_FIELD, SECRET_STRING_FIELD, VERSION_STAGES_FIELD, CREATED_DATE_FIELD));
    private final String arn;
    private final String name;
    private final String versionId;
    private final SdkBytes secretBinary;
    private final String secretString;
    private final List<String> versionStages;
    private final Instant createdDate;

    private GetSecretValueResponse(BuilderImpl builder) {
        super(builder);
        this.arn = builder.arn;
        this.name = builder.name;
        this.versionId = builder.versionId;
        this.secretBinary = builder.secretBinary;
        this.secretString = builder.secretString;
        this.versionStages = builder.versionStages;
        this.createdDate = builder.createdDate;
    }

    public final String arn() {
        return this.arn;
    }

    public final String name() {
        return this.name;
    }

    public final String versionId() {
        return this.versionId;
    }

    public final SdkBytes secretBinary() {
        return this.secretBinary;
    }

    public final String secretString() {
        return this.secretString;
    }

    public final boolean hasVersionStages() {
        return this.versionStages != null && !(this.versionStages instanceof SdkAutoConstructList);
    }

    public final List<String> versionStages() {
        return this.versionStages;
    }

    public final Instant createdDate() {
        return this.createdDate;
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
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.arn());
        hashCode = 31 * hashCode + Objects.hashCode(this.name());
        hashCode = 31 * hashCode + Objects.hashCode(this.versionId());
        hashCode = 31 * hashCode + Objects.hashCode(this.secretBinary());
        hashCode = 31 * hashCode + Objects.hashCode(this.secretString());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasVersionStages() ? this.versionStages() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.createdDate());
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
        if (!(obj instanceof GetSecretValueResponse)) {
            return false;
        }
        GetSecretValueResponse other = (GetSecretValueResponse)((Object)obj);
        return Objects.equals(this.arn(), other.arn()) && Objects.equals(this.name(), other.name()) && Objects.equals(this.versionId(), other.versionId()) && Objects.equals(this.secretBinary(), other.secretBinary()) && Objects.equals(this.secretString(), other.secretString()) && this.hasVersionStages() == other.hasVersionStages() && Objects.equals(this.versionStages(), other.versionStages()) && Objects.equals(this.createdDate(), other.createdDate());
    }

    public final String toString() {
        return ToString.builder((String)"GetSecretValueResponse").add("ARN", (Object)this.arn()).add("Name", (Object)this.name()).add("VersionId", (Object)this.versionId()).add("SecretBinary", (Object)(this.secretBinary() == null ? null : "*** Sensitive Data Redacted ***")).add("SecretString", (Object)(this.secretString() == null ? null : "*** Sensitive Data Redacted ***")).add("VersionStages", this.hasVersionStages() ? this.versionStages() : null).add("CreatedDate", (Object)this.createdDate()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ARN": {
                return Optional.ofNullable(clazz.cast(this.arn()));
            }
            case "Name": {
                return Optional.ofNullable(clazz.cast(this.name()));
            }
            case "VersionId": {
                return Optional.ofNullable(clazz.cast(this.versionId()));
            }
            case "SecretBinary": {
                return Optional.ofNullable(clazz.cast(this.secretBinary()));
            }
            case "SecretString": {
                return Optional.ofNullable(clazz.cast(this.secretString()));
            }
            case "VersionStages": {
                return Optional.ofNullable(clazz.cast(this.versionStages()));
            }
            case "CreatedDate": {
                return Optional.ofNullable(clazz.cast(this.createdDate()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetSecretValueResponse, T> g) {
        return obj -> g.apply((GetSecretValueResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerResponse.BuilderImpl
    implements Builder {
        private String arn;
        private String name;
        private String versionId;
        private SdkBytes secretBinary;
        private String secretString;
        private List<String> versionStages = DefaultSdkAutoConstructList.getInstance();
        private Instant createdDate;

        private BuilderImpl() {
        }

        private BuilderImpl(GetSecretValueResponse model) {
            super(model);
            this.arn(model.arn);
            this.name(model.name);
            this.versionId(model.versionId);
            this.secretBinary(model.secretBinary);
            this.secretString(model.secretString);
            this.versionStages(model.versionStages);
            this.createdDate(model.createdDate);
        }

        public final String getArn() {
            return this.arn;
        }

        public final void setArn(String arn) {
            this.arn = arn;
        }

        @Override
        public final Builder arn(String arn) {
            this.arn = arn;
            return this;
        }

        public final String getName() {
            return this.name;
        }

        public final void setName(String name) {
            this.name = name;
        }

        @Override
        public final Builder name(String name) {
            this.name = name;
            return this;
        }

        public final String getVersionId() {
            return this.versionId;
        }

        public final void setVersionId(String versionId) {
            this.versionId = versionId;
        }

        @Override
        public final Builder versionId(String versionId) {
            this.versionId = versionId;
            return this;
        }

        public final ByteBuffer getSecretBinary() {
            return this.secretBinary == null ? null : this.secretBinary.asByteBuffer();
        }

        public final void setSecretBinary(ByteBuffer secretBinary) {
            this.secretBinary(secretBinary == null ? null : SdkBytes.fromByteBuffer((ByteBuffer)secretBinary));
        }

        @Override
        public final Builder secretBinary(SdkBytes secretBinary) {
            this.secretBinary = secretBinary;
            return this;
        }

        public final String getSecretString() {
            return this.secretString;
        }

        public final void setSecretString(String secretString) {
            this.secretString = secretString;
        }

        @Override
        public final Builder secretString(String secretString) {
            this.secretString = secretString;
            return this;
        }

        public final Collection<String> getVersionStages() {
            if (this.versionStages instanceof SdkAutoConstructList) {
                return null;
            }
            return this.versionStages;
        }

        public final void setVersionStages(Collection<String> versionStages) {
            this.versionStages = SecretVersionStagesTypeCopier.copy(versionStages);
        }

        @Override
        public final Builder versionStages(Collection<String> versionStages) {
            this.versionStages = SecretVersionStagesTypeCopier.copy(versionStages);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder versionStages(String ... versionStages) {
            this.versionStages(Arrays.asList(versionStages));
            return this;
        }

        public final Instant getCreatedDate() {
            return this.createdDate;
        }

        public final void setCreatedDate(Instant createdDate) {
            this.createdDate = createdDate;
        }

        @Override
        public final Builder createdDate(Instant createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        @Override
        public GetSecretValueResponse build() {
            return new GetSecretValueResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetSecretValueResponse> {
        public Builder arn(String var1);

        public Builder name(String var1);

        public Builder versionId(String var1);

        public Builder secretBinary(SdkBytes var1);

        public Builder secretString(String var1);

        public Builder versionStages(Collection<String> var1);

        public Builder versionStages(String ... var1);

        public Builder createdDate(Instant var1);
    }
}

