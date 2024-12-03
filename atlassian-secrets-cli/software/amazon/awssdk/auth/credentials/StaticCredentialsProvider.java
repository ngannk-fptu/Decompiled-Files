/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.credentials;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class StaticCredentialsProvider
implements AwsCredentialsProvider {
    private final AwsCredentials credentials;

    private StaticCredentialsProvider(AwsCredentials credentials) {
        this.credentials = Validate.notNull(credentials, "Credentials must not be null.", new Object[0]);
    }

    public static StaticCredentialsProvider create(AwsCredentials credentials) {
        return new StaticCredentialsProvider(credentials);
    }

    @Override
    public AwsCredentials resolveCredentials() {
        return this.credentials;
    }

    public String toString() {
        return ToString.builder("StaticCredentialsProvider").add("credentials", this.credentials).build();
    }
}

