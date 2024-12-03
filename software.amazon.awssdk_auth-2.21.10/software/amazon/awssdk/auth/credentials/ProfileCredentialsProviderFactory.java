/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.auth.credentials;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileProviderCredentialsContext;

@FunctionalInterface
@SdkProtectedApi
public interface ProfileCredentialsProviderFactory {
    public AwsCredentialsProvider create(ProfileProviderCredentialsContext var1);
}

