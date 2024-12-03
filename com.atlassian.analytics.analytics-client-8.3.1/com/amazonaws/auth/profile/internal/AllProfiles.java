/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.profile.internal;

import com.amazonaws.annotation.Immutable;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.auth.profile.internal.BasicProfile;
import java.util.Collections;
import java.util.Map;

@Immutable
@SdkInternalApi
public class AllProfiles {
    private final Map<String, BasicProfile> profiles;

    public AllProfiles(Map<String, BasicProfile> profiles) {
        this.profiles = profiles;
    }

    public Map<String, BasicProfile> getProfiles() {
        return Collections.unmodifiableMap(this.profiles);
    }

    public BasicProfile getProfile(String profileName) {
        BasicProfile profile = this.profiles.get(profileName);
        if (profile != null) {
            return profile;
        }
        return this.profiles.get("profile " + profileName);
    }
}

