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
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ServerSideEncryptionByDefault
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ServerSideEncryptionByDefault> {
    private static final SdkField<String> SSE_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSEAlgorithm").getter(ServerSideEncryptionByDefault.getter(ServerSideEncryptionByDefault::sseAlgorithmAsString)).setter(ServerSideEncryptionByDefault.setter(Builder::sseAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SSEAlgorithm").unmarshallLocationName("SSEAlgorithm").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> KMS_MASTER_KEY_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("KMSMasterKeyID").getter(ServerSideEncryptionByDefault.getter(ServerSideEncryptionByDefault::kmsMasterKeyID)).setter(ServerSideEncryptionByDefault.setter(Builder::kmsMasterKeyID)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("KMSMasterKeyID").unmarshallLocationName("KMSMasterKeyID").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SSE_ALGORITHM_FIELD, KMS_MASTER_KEY_ID_FIELD));
    private static final long serialVersionUID = 1L;
    private final String sseAlgorithm;
    private final String kmsMasterKeyID;

    private ServerSideEncryptionByDefault(BuilderImpl builder) {
        this.sseAlgorithm = builder.sseAlgorithm;
        this.kmsMasterKeyID = builder.kmsMasterKeyID;
    }

    public final ServerSideEncryption sseAlgorithm() {
        return ServerSideEncryption.fromValue(this.sseAlgorithm);
    }

    public final String sseAlgorithmAsString() {
        return this.sseAlgorithm;
    }

    public final String kmsMasterKeyID() {
        return this.kmsMasterKeyID;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.sseAlgorithmAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.kmsMasterKeyID());
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
        if (!(obj instanceof ServerSideEncryptionByDefault)) {
            return false;
        }
        ServerSideEncryptionByDefault other = (ServerSideEncryptionByDefault)obj;
        return Objects.equals(this.sseAlgorithmAsString(), other.sseAlgorithmAsString()) && Objects.equals(this.kmsMasterKeyID(), other.kmsMasterKeyID());
    }

    public final String toString() {
        return ToString.builder((String)"ServerSideEncryptionByDefault").add("SSEAlgorithm", (Object)this.sseAlgorithmAsString()).add("KMSMasterKeyID", (Object)(this.kmsMasterKeyID() == null ? null : "*** Sensitive Data Redacted ***")).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SSEAlgorithm": {
                return Optional.ofNullable(clazz.cast(this.sseAlgorithmAsString()));
            }
            case "KMSMasterKeyID": {
                return Optional.ofNullable(clazz.cast(this.kmsMasterKeyID()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ServerSideEncryptionByDefault, T> g) {
        return obj -> g.apply((ServerSideEncryptionByDefault)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String sseAlgorithm;
        private String kmsMasterKeyID;

        private BuilderImpl() {
        }

        private BuilderImpl(ServerSideEncryptionByDefault model) {
            this.sseAlgorithm(model.sseAlgorithm);
            this.kmsMasterKeyID(model.kmsMasterKeyID);
        }

        public final String getSseAlgorithm() {
            return this.sseAlgorithm;
        }

        public final void setSseAlgorithm(String sseAlgorithm) {
            this.sseAlgorithm = sseAlgorithm;
        }

        @Override
        public final Builder sseAlgorithm(String sseAlgorithm) {
            this.sseAlgorithm = sseAlgorithm;
            return this;
        }

        @Override
        public final Builder sseAlgorithm(ServerSideEncryption sseAlgorithm) {
            this.sseAlgorithm(sseAlgorithm == null ? null : sseAlgorithm.toString());
            return this;
        }

        public final String getKmsMasterKeyID() {
            return this.kmsMasterKeyID;
        }

        public final void setKmsMasterKeyID(String kmsMasterKeyID) {
            this.kmsMasterKeyID = kmsMasterKeyID;
        }

        @Override
        public final Builder kmsMasterKeyID(String kmsMasterKeyID) {
            this.kmsMasterKeyID = kmsMasterKeyID;
            return this;
        }

        public ServerSideEncryptionByDefault build() {
            return new ServerSideEncryptionByDefault(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ServerSideEncryptionByDefault> {
        public Builder sseAlgorithm(String var1);

        public Builder sseAlgorithm(ServerSideEncryption var1);

        public Builder kmsMasterKeyID(String var1);
    }
}

