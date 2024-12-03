/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.token.credentials;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.auth.token.credentials.SdkTokenProvider;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;

@FunctionalInterface
@SdkProtectedApi
public interface ChildProfileTokenProviderFactory {
    public SdkTokenProvider create(ProfileFile var1, Profile var2);
}

