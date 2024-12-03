/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nullable
 */
package com.atlassian.gadgets;

import com.atlassian.gadgets.view.View;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import io.atlassian.fugue.Option;
import java.util.Locale;
import javax.annotation.Nullable;

public final class GadgetRequestContext {
    public static final GadgetRequestContext NO_CURRENT_REQUEST = Builder.gadgetRequestContext().locale(new Locale("")).ignoreCache(false).debug(false).build();
    private final Locale locale;
    private final boolean ignoreCache;
    private final boolean debug;
    private final Option<User> user;
    private final View view;

    private GadgetRequestContext(Builder builder) {
        this.locale = builder.locale;
        this.ignoreCache = builder.ignoreCache;
        this.view = builder.view;
        this.debug = builder.debug;
        this.user = builder.user;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public boolean getIgnoreCache() {
        return this.ignoreCache;
    }

    public String getViewer() {
        return (String)this.getUser().map(input -> input.getUsername()).getOrNull();
    }

    public View getView() {
        return this.view;
    }

    public boolean isDebuggingEnabled() {
        return this.debug;
    }

    public Option<User> getUser() {
        return this.user;
    }

    public static class Builder {
        private Locale locale = Locale.US;
        private boolean ignoreCache = false;
        private View view = View.DEFAULT;
        private boolean debug = false;
        private Option<User> user = Option.none();

        public static Builder gadgetRequestContext() {
            return new Builder();
        }

        public GadgetRequestContext build() {
            return new GadgetRequestContext(this);
        }

        public Builder locale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Builder ignoreCache(boolean ignoreCache) {
            this.ignoreCache = ignoreCache;
            return this;
        }

        public Builder view(View view) {
            this.view = view;
            return this;
        }

        public Builder user(@Nullable User user) {
            this.user = Option.option((Object)user);
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }
    }

    public static final class User {
        private final String userKey;
        private final String username;

        public User(String userKey, String username) {
            this.userKey = (String)Preconditions.checkNotNull((Object)userKey);
            this.username = (String)Preconditions.checkNotNull((Object)username);
        }

        public String getUserKey() {
            return this.userKey;
        }

        public String getUsername() {
            return this.username;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            User that = (User)o;
            return Objects.equal((Object)this.userKey, (Object)that.userKey) && Objects.equal((Object)this.username, (Object)that.username);
        }

        public int hashCode() {
            return Objects.hashCode((Object[])new Object[]{this.userKey, this.username});
        }

        public String toString() {
            return String.format("User{userKey=%s, username=%s}", this.userKey, this.username);
        }
    }
}

