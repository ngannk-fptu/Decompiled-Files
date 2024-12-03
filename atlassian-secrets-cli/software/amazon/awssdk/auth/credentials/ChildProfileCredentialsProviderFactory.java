/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.credentials;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.profiles.Profile;

@FunctionalInterface
@SdkProtectedApi
public interface ChildProfileCredentialsProviderFactory {
    public AwsCredentialsProvider create(AwsCredentialsProvider var1, Profile var2);
}

