/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.service;

public class UserSearchRequest {
    private int startIndex;
    private int maxResult;
    private String searchTerms;

    private UserSearchRequest(int startIndex, int maxResult, String searchTerms) {
        this.startIndex = startIndex;
        this.maxResult = maxResult;
        this.searchTerms = searchTerms;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public int getMaxResult() {
        return this.maxResult;
    }

    public String getSearchTerms() {
        return this.searchTerms;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private int startIndex;
        private int maxResult;
        private String searchTerms;

        public Builder withStartIndex(int startIndex) {
            this.startIndex = startIndex;
            return this;
        }

        public Builder withMaxResult(int maxResult) {
            this.maxResult = maxResult;
            return this;
        }

        public Builder withSearchTerms(String searchTerms) {
            this.searchTerms = searchTerms;
            return this;
        }

        public UserSearchRequest build() {
            return new UserSearchRequest(this.startIndex, this.maxResult, this.searchTerms);
        }
    }
}

