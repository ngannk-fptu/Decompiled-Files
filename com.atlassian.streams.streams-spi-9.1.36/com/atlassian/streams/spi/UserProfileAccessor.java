/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.streams.api.UserProfile
 */
package com.atlassian.streams.spi;

import com.atlassian.annotations.PublicApi;
import com.atlassian.streams.api.UserProfile;
import java.net.URI;

@PublicApi
public interface UserProfileAccessor {
    public UserProfile getUserProfile(String var1);

    public UserProfile getAnonymousUserProfile();

    public UserProfile getUserProfile(URI var1, String var2);

    public UserProfile getAnonymousUserProfile(URI var1);
}

