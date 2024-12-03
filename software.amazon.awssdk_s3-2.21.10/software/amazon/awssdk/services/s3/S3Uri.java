/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@Immutable
@SdkPublicApi
public final class S3Uri
implements ToCopyableBuilder<Builder, S3Uri> {
    private final URI uri;
    private final String bucket;
    private final String key;
    private final Region region;
    private final boolean isPathStyle;
    private final Map<String, List<String>> queryParams;

    private S3Uri(Builder builder) {
        this.uri = (URI)Validate.notNull((Object)builder.uri, (String)"URI must not be null", (Object[])new Object[0]);
        this.bucket = builder.bucket;
        this.key = builder.key;
        this.region = builder.region;
        this.isPathStyle = (Boolean)Validate.notNull((Object)builder.isPathStyle, (String)"Path style flag must not be null", (Object[])new Object[0]);
        this.queryParams = builder.queryParams == null ? new HashMap() : CollectionUtils.deepCopyMap((Map)builder.queryParams);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public URI uri() {
        return this.uri;
    }

    public Optional<String> bucket() {
        return Optional.ofNullable(this.bucket);
    }

    public Optional<String> key() {
        return Optional.ofNullable(this.key);
    }

    public Optional<Region> region() {
        return Optional.ofNullable(this.region);
    }

    public boolean isPathStyle() {
        return this.isPathStyle;
    }

    public Map<String, List<String>> rawQueryParameters() {
        return this.queryParams;
    }

    public List<String> firstMatchingRawQueryParameters(String key) {
        List<String> queryValues = this.queryParams.get(key);
        if (queryValues == null) {
            return new ArrayList<String>();
        }
        List<String> queryValuesCopy = Arrays.asList(new String[queryValues.size()]);
        Collections.copy(queryValuesCopy, queryValues);
        return queryValuesCopy;
    }

    public Optional<String> firstMatchingRawQueryParameter(String key) {
        return Optional.ofNullable(this.queryParams.get(key)).map(q -> (String)q.get(0));
    }

    public String toString() {
        return ToString.builder((String)"S3Uri").add("uri", (Object)this.uri).add("bucket", (Object)this.bucket).add("key", (Object)this.key).add("region", (Object)this.region).add("isPathStyle", (Object)this.isPathStyle).add("queryParams", this.queryParams).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        S3Uri s3Uri = (S3Uri)o;
        return Objects.equals(this.uri, s3Uri.uri) && Objects.equals(this.bucket, s3Uri.bucket) && Objects.equals(this.key, s3Uri.key) && Objects.equals(this.region, s3Uri.region) && Objects.equals(this.isPathStyle, s3Uri.isPathStyle) && Objects.equals(this.queryParams, s3Uri.queryParams);
    }

    public int hashCode() {
        int result = this.uri != null ? this.uri.hashCode() : 0;
        result = 31 * result + (this.bucket != null ? this.bucket.hashCode() : 0);
        result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
        result = 31 * result + (this.region != null ? this.region.hashCode() : 0);
        result = 31 * result + Boolean.hashCode(this.isPathStyle);
        result = 31 * result + (this.queryParams != null ? this.queryParams.hashCode() : 0);
        return result;
    }

    public static final class Builder
    implements CopyableBuilder<Builder, S3Uri> {
        private URI uri;
        private String bucket;
        private String key;
        private Region region;
        private boolean isPathStyle;
        private Map<String, List<String>> queryParams;

        private Builder() {
        }

        private Builder(S3Uri s3Uri) {
            this.uri = s3Uri.uri;
            this.bucket = s3Uri.bucket;
            this.key = s3Uri.key;
            this.region = s3Uri.region;
            this.isPathStyle = s3Uri.isPathStyle;
            this.queryParams = s3Uri.queryParams;
        }

        public Builder uri(URI uri) {
            this.uri = uri;
            return this;
        }

        public Builder bucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder region(Region region) {
            this.region = region;
            return this;
        }

        public Builder isPathStyle(boolean isPathStyle) {
            this.isPathStyle = isPathStyle;
            return this;
        }

        public Builder queryParams(Map<String, List<String>> queryParams) {
            this.queryParams = queryParams;
            return this;
        }

        public S3Uri build() {
            return new S3Uri(this);
        }
    }
}

