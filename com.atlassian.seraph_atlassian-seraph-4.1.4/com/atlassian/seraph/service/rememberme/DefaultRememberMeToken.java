/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.service.rememberme;

import com.atlassian.seraph.service.rememberme.RememberMeToken;

public class DefaultRememberMeToken
implements RememberMeToken {
    private final Long id;
    private final String randomString;
    private final String userName;
    private final long createdTime;

    private DefaultRememberMeToken(Long id, String randomString, String userName, long createdTime) {
        this.id = id;
        this.randomString = randomString;
        this.userName = userName;
        this.createdTime = createdTime;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getRandomString() {
        return this.randomString;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public long getCreatedTime() {
        return this.createdTime;
    }

    public static Builder builder(Long id, String randomString) {
        return new Builder(id, randomString);
    }

    public static Builder builder(String randomString) {
        return new Builder(randomString);
    }

    public static Builder builder(RememberMeToken token) {
        return new Builder(token);
    }

    public static class Builder {
        private Long id;
        private String randomString;
        private String userName;
        private long createdTime;

        public Builder(RememberMeToken token) {
            this.id = token.getId();
            this.randomString = token.getRandomString();
            this.userName = token.getUserName();
            this.createdTime = token.getCreatedTime();
        }

        public Builder(Long id, String randomString) {
            this.id = id;
            this.randomString = randomString;
        }

        public Builder(String randomString) {
            this.randomString = randomString;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setRandomString(String randomString) {
            this.randomString = randomString;
            return this;
        }

        public Builder setCreatedTime(long createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public RememberMeToken build() {
            return new DefaultRememberMeToken(this.id, this.randomString, this.userName, this.createdTime);
        }
    }
}

