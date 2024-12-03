/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.credentials;

import java.util.Objects;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@Immutable
@SdkPublicApi
public final class AwsBasicCredentials
implements AwsCredentials {
    @SdkInternalApi
    static final AwsBasicCredentials ANONYMOUS_CREDENTIALS = new AwsBasicCredentials(null, null, false);
    private final String accessKeyId;
    private final String secretAccessKey;

    protected AwsBasicCredentials(String accessKeyId, String secretAccessKey) {
        this(accessKeyId, secretAccessKey, true);
    }

    private AwsBasicCredentials(String accessKeyId, String secretAccessKey, boolean validateCredentials) {
        this.accessKeyId = StringUtils.trimToNull(accessKeyId);
        this.secretAccessKey = StringUtils.trimToNull(secretAccessKey);
        if (validateCredentials) {
            Validate.notNull(this.accessKeyId, "Access key ID cannot be blank.", new Object[0]);
            Validate.notNull(this.secretAccessKey, "Secret access key cannot be blank.", new Object[0]);
        }
    }

    public static AwsBasicCredentials create(String accessKeyId, String secretAccessKey) {
        return new AwsBasicCredentials(accessKeyId, secretAccessKey);
    }

    @Override
    public String accessKeyId() {
        return this.accessKeyId;
    }

    @Override
    public String secretAccessKey() {
        return this.secretAccessKey;
    }

    public String toString() {
        return ToString.builder("AwsCredentials").add("accessKeyId", this.accessKeyId).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AwsBasicCredentials that = (AwsBasicCredentials)o;
        return Objects.equals(this.accessKeyId, that.accessKeyId) && Objects.equals(this.secretAccessKey, that.secretAccessKey);
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + Objects.hashCode(this.accessKeyId());
        hashCode = 31 * hashCode + Objects.hashCode(this.secretAccessKey());
        return hashCode;
    }
}

