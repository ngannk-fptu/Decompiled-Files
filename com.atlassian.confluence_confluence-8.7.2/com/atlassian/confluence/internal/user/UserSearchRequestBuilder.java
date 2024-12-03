/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.user;

import com.atlassian.confluence.internal.user.AbstractSearchRequestBuilder;
import com.atlassian.confluence.internal.user.UserSearchRequest;

public class UserSearchRequestBuilder
extends AbstractSearchRequestBuilder<UserSearchRequest> {
    private String searchTerm;
    private String usernameTerm;
    private String emailTerm;
    private String fullnameTerm;
    private boolean showEmail;

    public UserSearchRequestBuilder searchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        return this;
    }

    public UserSearchRequestBuilder usernameTerm(String usernameTerm) {
        this.usernameTerm = usernameTerm;
        return this;
    }

    public UserSearchRequestBuilder emailTerm(String emailTerm) {
        this.emailTerm = emailTerm;
        return this;
    }

    public UserSearchRequestBuilder fullnameTerm(String fullnameTerm) {
        this.fullnameTerm = fullnameTerm;
        return this;
    }

    public UserSearchRequestBuilder showEmail(boolean showEmail) {
        this.showEmail = showEmail;
        return this;
    }

    @Override
    public UserSearchRequest build() {
        return new UserSearchRequest(this.searchTerm, this.usernameTerm, this.emailTerm, this.fullnameTerm, this.showUnlicensedUsers, this.showEmail);
    }
}

