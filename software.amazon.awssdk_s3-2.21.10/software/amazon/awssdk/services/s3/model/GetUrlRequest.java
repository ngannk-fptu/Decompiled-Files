/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class GetUrlRequest
implements SdkPojo,
ToCopyableBuilder<Builder, GetUrlRequest> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).getter(GetUrlRequest.getter(GetUrlRequest::bucket)).setter(GetUrlRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build()}).build();
    private static final SdkField<String> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).getter(GetUrlRequest.getter(GetUrlRequest::key)).setter(GetUrlRequest.setter(Builder::key)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.GREEDY_PATH).locationName("Key").unmarshallLocationName("Key").build()}).build();
    private static final SdkField<String> VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("VersionId").getter(GetUrlRequest.getter(GetUrlRequest::versionId)).setter(GetUrlRequest.setter(Builder::versionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("versionId").unmarshallLocationName("versionId").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, KEY_FIELD, VERSION_ID_FIELD));
    private final String bucket;
    private final String key;
    private final Region region;
    private final URI endpoint;
    private final String versionId;

    private GetUrlRequest(BuilderImpl builder) {
        this.bucket = (String)Validate.paramNotBlank((CharSequence)builder.bucket, (String)"Bucket");
        this.key = (String)Validate.paramNotBlank((CharSequence)builder.key, (String)"Key");
        this.region = builder.region;
        this.endpoint = builder.endpoint;
        this.versionId = builder.versionId;
    }

    public String bucket() {
        return this.bucket;
    }

    public String key() {
        return this.key;
    }

    public Region region() {
        return this.region;
    }

    public URI endpoint() {
        return this.endpoint;
    }

    public String versionId() {
        return this.versionId;
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
            case "VersionId": {
                return Optional.ofNullable(clazz.cast(this.versionId()));
            }
        }
        return Optional.empty();
    }

    public List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetUrlRequest, T> g) {
        return obj -> g.apply((GetUrlRequest)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    private static final class BuilderImpl
    implements Builder {
        private String bucket;
        private String key;
        private Region region;
        private URI endpoint;
        private String versionId;

        private BuilderImpl() {
        }

        private BuilderImpl(GetUrlRequest getUrlRequest) {
            this.bucket(getUrlRequest.bucket);
            this.key(getUrlRequest.key);
            this.region(getUrlRequest.region);
            this.endpoint(getUrlRequest.endpoint);
        }

        @Override
        public Builder bucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        @Override
        public Builder key(String key) {
            this.key = key;
            return this;
        }

        @Override
        public Builder versionId(String versionId) {
            this.versionId = versionId;
            return this;
        }

        @Override
        public Builder region(Region region) {
            this.region = region;
            return this;
        }

        @Override
        public Builder endpoint(URI endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public GetUrlRequest build() {
            return new GetUrlRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, GetUrlRequest> {
        public Builder bucket(String var1);

        public Builder key(String var1);

        public Builder versionId(String var1);

        public Builder region(Region var1);

        public Builder endpoint(URI var1);
    }
}

