/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.credentials;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.utils.ToString;

@SdkPublicApi
public final class AnonymousCredentialsProvider
implements AwsCredentialsProvider {
    private AnonymousCredentialsProvider() {
    }

    public static AnonymousCredentialsProvider create() {
        return new AnonymousCredentialsProvider();
    }

    @Override
    public AwsCredentials resolveCredentials() {
        return AwsBasicCredentials.ANONYMOUS_CREDENTIALS;
    }

    public String toString() {
        return ToString.create("AnonymousCredentialsProvider");
    }
}

