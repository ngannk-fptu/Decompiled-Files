/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractUserQuery
implements SearchQuery {
    private final String username;
    private final UserKey userkey;

    public AbstractUserQuery(String username) {
        this.username = username;
        this.userkey = null;
    }

    public AbstractUserQuery(@NonNull UserKey userKey) {
        this.userkey = userKey;
        this.username = null;
    }

    public Optional<String> username() {
        return Optional.ofNullable(this.username);
    }

    public Optional<UserKey> userkey() {
        return Optional.ofNullable(this.userkey);
    }

    @Override
    public List getParameters() {
        return this.userkey != null ? Collections.singletonList(this.userkey) : Collections.singletonList(this.username);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractUserQuery that = (AbstractUserQuery)o;
        return Objects.equals(this.username, that.username) && Objects.equals(this.userkey, that.userkey);
    }

    public int hashCode() {
        return Objects.hash(this.username, this.userkey);
    }
}

