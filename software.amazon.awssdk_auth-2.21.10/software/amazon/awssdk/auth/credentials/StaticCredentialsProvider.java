/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
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
        this.credentials = (AwsCredentials)Validate.notNull((Object)credentials, (String)"Credentials must not be null.", (Object[])new Object[0]);
    }

    public static StaticCredentialsProvider create(AwsCredentials credentials) {
        return new StaticCredentialsProvider(credentials);
    }

    @Override
    public AwsCredentials resolveCredentials() {
        return this.credentials;
    }

    public String toString() {
        return ToString.builder((String)"StaticCredentialsProvider").add("credentials", (Object)this.credentials).build();
    }
}

