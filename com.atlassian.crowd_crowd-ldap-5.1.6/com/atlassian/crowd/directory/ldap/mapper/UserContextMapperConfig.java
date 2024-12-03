/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.ldap.mapper;

public class UserContextMapperConfig {
    private final boolean includeAll;
    private final boolean includeMemberOf;

    private UserContextMapperConfig(boolean includeAll, boolean includeMemberOf) {
        this.includeAll = includeAll;
        this.includeMemberOf = includeMemberOf;
    }

    public boolean includeAll() {
        return this.includeAll;
    }

    public boolean includeMemberOf() {
        return this.includeMemberOf;
    }

    public static class Builder {
        private boolean includeAll = false;
        private boolean includeMemberOf = false;

        private Builder() {
        }

        public static Builder withRequiredAttributes() {
            return new Builder();
        }

        public static Builder withCustomAttributes() {
            Builder builder = new Builder();
            builder.includeAll = true;
            return builder;
        }

        public Builder withMemberOfAttribute() {
            this.includeMemberOf = true;
            return this;
        }

        public UserContextMapperConfig build() {
            return new UserContextMapperConfig(this.includeAll, this.includeMemberOf);
        }
    }
}

