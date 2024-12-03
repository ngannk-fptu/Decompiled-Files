/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.ToString;

@SdkProtectedApi
public abstract class AwsResponseMetadata {
    private static final String UNKNOWN = "UNKNOWN";
    private final Map<String, String> metadata;

    protected AwsResponseMetadata(Map<String, String> metadata) {
        this.metadata = Collections.unmodifiableMap(metadata);
    }

    protected AwsResponseMetadata(AwsResponseMetadata responseMetadata) {
        this(responseMetadata == null ? new HashMap() : responseMetadata.metadata);
    }

    public String requestId() {
        return this.getValue("AWS_REQUEST_ID");
    }

    public final String toString() {
        return ToString.builder("AwsResponseMetadata").add("metadata", this.metadata.keySet()).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AwsResponseMetadata that = (AwsResponseMetadata)o;
        return Objects.equals(this.metadata, that.metadata);
    }

    public int hashCode() {
        return Objects.hashCode(this.metadata);
    }

    protected final String getValue(String key) {
        return Optional.ofNullable(this.metadata.get(key)).orElse(UNKNOWN);
    }
}

