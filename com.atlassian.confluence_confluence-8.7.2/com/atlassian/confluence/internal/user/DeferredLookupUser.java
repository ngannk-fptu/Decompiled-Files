/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 */
package com.atlassian.confluence.internal.user;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class DeferredLookupUser
implements ConfluenceUser {
    private static final long serialVersionUID = -3149578154174356690L;
    private final UserKey userKey;
    private final transient Supplier<ConfluenceUser> delegateRef;

    public DeferredLookupUser(UserKey userKey) {
        this.userKey = Objects.requireNonNull(userKey);
        this.delegateRef = Lazy.supplier(() -> FindUserHelper.getUserByUserKey(userKey));
    }

    @Override
    public UserKey getKey() {
        return this.userKey;
    }

    @Override
    public String getLowerName() {
        return this.lookupUser(ConfluenceUser::getLowerName).orElse(null);
    }

    public String getFullName() {
        return this.lookupUser(User::getFullName).orElse(null);
    }

    public String getEmail() {
        return this.lookupUser(User::getEmail).orElse(null);
    }

    public String getName() {
        return this.lookupUser(Principal::getName).orElse(null);
    }

    private <T> Optional<T> lookupUser(Function<ConfluenceUser, T> f) {
        return Optional.ofNullable((ConfluenceUser)this.delegateRef.get()).map(f);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof ConfluenceUser)) {
            return false;
        }
        ConfluenceUser that = (ConfluenceUser)o;
        return Objects.equals(this.getKey(), that.getKey());
    }

    public int hashCode() {
        return Objects.hash(this.userKey);
    }
}

