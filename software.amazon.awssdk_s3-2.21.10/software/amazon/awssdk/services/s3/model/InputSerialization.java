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
import software.amazon.awssdk.services.s3.model.CSVInput;
import software.amazon.awssdk.services.s3.model.CompressionType;
import software.amazon.awssdk.services.s3.model.JSONInput;
import software.amazon.awssdk.services.s3.model.ParquetInput;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class InputSerialization
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, InputSerialization> {
    private static final SdkField<CSVInput> CSV_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("CSV").getter(InputSerialization.getter(InputSerialization::csv)).setter(InputSerialization.setter(Builder::csv)).constructor(CSVInput::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CSV").unmarshallLocationName("CSV").build()}).build();
    private static final SdkField<String> COMPRESSION_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CompressionType").getter(InputSerialization.getter(InputSerialization::compressionTypeAsString)).setter(InputSerialization.setter(Builder::compressionType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CompressionType").unmarshallLocationName("CompressionType").build()}).build();
    private static final SdkField<JSONInput> JSON_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("JSON").getter(InputSerialization.getter(InputSerialization::json)).setter(InputSerialization.setter(Builder::json)).constructor(JSONInput::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("JSON").unmarshallLocationName("JSON").build()}).build();
    private static final SdkField<ParquetInput> PARQUET_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Parquet").getter(InputSerialization.getter(InputSerialization::parquet)).setter(InputSerialization.setter(Builder::parquet)).constructor(ParquetInput::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Parquet").unmarshallLocationName("Parquet").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(CSV_FIELD, COMPRESSION_TYPE_FIELD, JSON_FIELD, PARQUET_FIELD));
    private static final long serialVersionUID = 1L;
    private final CSVInput csv;
    private final String compressionType;
    private final JSONInput json;
    private final ParquetInput parquet;

    private InputSerialization(BuilderImpl builder) {
        this.csv = builder.csv;
        this.compressionType = builder.compressionType;
        this.json = builder.json;
        this.parquet = builder.parquet;
    }

    public final CSVInput csv() {
        return this.csv;
    }

    public final CompressionType compressionType() {
        return CompressionType.fromValue(this.compressionType);
    }

    public final String compressionTypeAsString() {
        return this.compressionType;
    }

    public final JSONInput json() {
        return this.json;
    }

    public final ParquetInput parquet() {
        return this.parquet;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.csv());
        hashCode = 31 * hashCode + Objects.hashCode(this.compressionTypeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.json());
        hashCode = 31 * hashCode + Objects.hashCode(this.parquet());
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
        if (!(obj instanceof InputSerialization)) {
            return false;
        }
        InputSerialization other = (InputSerialization)obj;
        return Objects.equals(this.csv(), other.csv()) && Objects.equals(this.compressionTypeAsString(), other.compressionTypeAsString()) && Objects.equals(this.json(), other.json()) && Objects.equals(this.parquet(), other.parquet());
    }

    public final String toString() {
        return ToString.builder((String)"InputSerialization").add("CSV", (Object)this.csv()).add("CompressionType", (Object)this.compressionTypeAsString()).add("JSON", (Object)this.json()).add("Parquet", (Object)this.parquet()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "CSV": {
                return Optional.ofNullable(clazz.cast(this.csv()));
            }
            case "CompressionType": {
                return Optional.ofNullable(clazz.cast(this.compressionTypeAsString()));
            }
            case "JSON": {
                return Optional.ofNullable(clazz.cast(this.json()));
            }
            case "Parquet": {
                return Optional.ofNullable(clazz.cast(this.parquet()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<InputSerialization, T> g) {
        return obj -> g.apply((InputSerialization)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private CSVInput csv;
        private String compressionType;
        private JSONInput json;
        private ParquetInput parquet;

        private BuilderImpl() {
        }

        private BuilderImpl(InputSerialization model) {
            this.csv(model.csv);
            this.compressionType(model.compressionType);
            this.json(model.json);
            this.parquet(model.parquet);
        }

        public final CSVInput.Builder getCsv() {
            return this.csv != null ? this.csv.toBuilder() : null;
        }

        public final void setCsv(CSVInput.BuilderImpl csv) {
            this.csv = csv != null ? csv.build() : null;
        }

        @Override
        public final Builder csv(CSVInput csv) {
            this.csv = csv;
            return this;
        }

        public final String getCompressionType() {
            return this.compressionType;
        }

        public final void setCompressionType(String compressionType) {
            this.compressionType = compressionType;
        }

        @Override
        public final Builder compressionType(String compressionType) {
            this.compressionType = compressionType;
            return this;
        }

        @Override
        public final Builder compressionType(CompressionType compressionType) {
            this.compressionType(compressionType == null ? null : compressionType.toString());
            return this;
        }

        public final JSONInput.Builder getJson() {
            return this.json != null ? this.json.toBuilder() : null;
        }

        public final void setJson(JSONInput.BuilderImpl json) {
            this.json = json != null ? json.build() : null;
        }

        @Override
        public final Builder json(JSONInput json) {
            this.json = json;
            return this;
        }

        public final ParquetInput.Builder getParquet() {
            return this.parquet != null ? this.parquet.toBuilder() : null;
        }

        public final void setParquet(ParquetInput.BuilderImpl parquet) {
            this.parquet = parquet != null ? parquet.build() : null;
        }

        @Override
        public final Builder parquet(ParquetInput parquet) {
            this.parquet = parquet;
            return this;
        }

        public InputSerialization build() {
            return new InputSerialization(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, InputSerialization> {
        public Builder csv(CSVInput var1);

        default public Builder csv(Consumer<CSVInput.Builder> csv) {
            return this.csv((CSVInput)((CSVInput.Builder)CSVInput.builder().applyMutation(csv)).build());
        }

        public Builder compressionType(String var1);

        public Builder compressionType(CompressionType var1);

        public Builder json(JSONInput var1);

        default public Builder json(Consumer<JSONInput.Builder> json) {
            return this.json((JSONInput)((JSONInput.Builder)JSONInput.builder().applyMutation(json)).build());
        }

        public Builder parquet(ParquetInput var1);

        default public Builder parquet(Consumer<ParquetInput.Builder> parquet) {
            return this.parquet((ParquetInput)((ParquetInput.Builder)ParquetInput.builder().applyMutation(parquet)).build());
        }
    }
}

