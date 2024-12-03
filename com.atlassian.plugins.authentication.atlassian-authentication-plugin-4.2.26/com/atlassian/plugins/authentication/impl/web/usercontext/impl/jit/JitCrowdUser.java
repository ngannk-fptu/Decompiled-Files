/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserWithAttributes
 *  com.atlassian.crowd.embedded.impl.ImmutableUser
 *  com.google.common.base.Strings
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserWithAttributes;
import com.atlassian.crowd.embedded.impl.ImmutableUser;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.JitUserData;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;

public class JitCrowdUser
implements UserWithAttributes {
    public static final String IDENTITY_PROVIDER_ID_ATTRIBUTE_KEY = "jit_idp_id";
    private final String identityProviderId;
    private final User user;

    public JitCrowdUser(String identityProviderId, User user) {
        this.identityProviderId = Strings.nullToEmpty((String)identityProviderId);
        this.user = user;
    }

    public JitCrowdUser(JitUserData userData, long directoryId) {
        this.identityProviderId = Strings.nullToEmpty((String)userData.getIdentityProviderId());
        this.user = new ImmutableUser(directoryId, userData.getUsername(), userData.getDisplayName(), userData.getEmail(), true);
    }

    public long getDirectoryId() {
        return this.user.getDirectoryId();
    }

    public boolean isActive() {
        return this.user.isActive();
    }

    public String getEmailAddress() {
        return this.user.getEmailAddress();
    }

    public String getDisplayName() {
        return this.user.getDisplayName();
    }

    public int compareTo(User user) {
        return this.user.compareTo(user);
    }

    public String getName() {
        return this.user.getName();
    }

    @Nullable
    public Set<String> getValues(String key) {
        return IDENTITY_PROVIDER_ID_ATTRIBUTE_KEY.equals(key) ? Sets.newHashSet((Object[])new String[]{this.identityProviderId}) : Collections.emptySet();
    }

    @Nullable
    public String getValue(String key) {
        return IDENTITY_PROVIDER_ID_ATTRIBUTE_KEY.equals(key) ? this.identityProviderId : null;
    }

    public Set<String> getKeys() {
        return Sets.newHashSet((Object[])new String[]{IDENTITY_PROVIDER_ID_ATTRIBUTE_KEY});
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JitCrowdUser that = (JitCrowdUser)o;
        return Objects.equals(this.identityProviderId, that.identityProviderId) && Objects.equals(this.user, that.user);
    }

    public int hashCode() {
        return Objects.hash(this.identityProviderId, this.user);
    }
}

