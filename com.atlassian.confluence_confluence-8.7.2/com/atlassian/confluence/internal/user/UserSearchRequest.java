/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.internal.user;

import com.atlassian.confluence.internal.user.SearchRequest;
import com.atlassian.confluence.internal.user.UserSearchRequestBuilder;
import org.apache.commons.lang3.StringUtils;

public class UserSearchRequest
extends SearchRequest {
    private final String searchTerm;
    private final String usernameTerm;
    private final String emailTerm;
    private final String fullnameTerm;
    private final boolean showEmail;

    UserSearchRequest(String searchTerm, String usernameTerm, String emailTerm, String fullnameTerm, boolean showUnlicensedUsers, boolean showEmail) {
        super(showUnlicensedUsers);
        this.searchTerm = searchTerm;
        this.usernameTerm = usernameTerm;
        this.emailTerm = emailTerm;
        this.fullnameTerm = fullnameTerm;
        this.showEmail = showEmail;
    }

    public boolean isSimpleSearch() {
        return StringUtils.isNotEmpty((CharSequence)this.searchTerm);
    }

    public String getSearchTerm() {
        return this.searchTerm;
    }

    public String getUsernameTerm() {
        return this.usernameTerm;
    }

    public String getEmailTerm() {
        return this.emailTerm;
    }

    public String getFullnameTerm() {
        return this.fullnameTerm;
    }

    public boolean isShowEmail() {
        return this.showEmail;
    }

    public static UserSearchRequestBuilder builder() {
        return new UserSearchRequestBuilder();
    }
}

