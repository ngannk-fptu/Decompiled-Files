/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  com.google.common.base.Strings
 */
package com.atlassian.ratelimiting.internal.user;

import com.atlassian.ratelimiting.internal.user.CommonUserService;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;

public class AtlassianUserService
extends CommonUserService {
    private UserManager atlassianUserManager;

    public AtlassianUserService(com.atlassian.sal.api.user.UserManager salUserManager, UserManager atlassianUserManager) {
        super(salUserManager);
        this.atlassianUserManager = atlassianUserManager;
    }

    @Override
    public List<UserProfile> searchUsersForUserPicker(String criteria, int offset, int maxNumberOfResults) {
        String sanitisedCriteria = Strings.isNullOrEmpty((String)criteria) ? "" : criteria.toLowerCase();
        int sanitisedOffset = Math.max(offset, 0);
        ArrayList<UserProfile> searchResults = new ArrayList<UserProfile>();
        boolean resultsShouldReturnAnonymousUser = this.resultsShouldReturnAnonymousUser(sanitisedCriteria);
        if (resultsShouldReturnAnonymousUser) {
            searchResults.add(ANONYMOUS_REPRESENTATIVE_USER);
        }
        this.getAtlassianUsers().stream().filter(user -> this.filterByCriteria((User)user, sanitisedCriteria)).limit((long)this.determineResultSetSizeForSearch(resultsShouldReturnAnonymousUser, maxNumberOfResults) + (long)sanitisedOffset).map(this::atlassianUserToSalUser).forEach(searchResults::add);
        return searchResults.subList(Math.min(sanitisedOffset, searchResults.size()), searchResults.size());
    }

    private List<User> getAtlassianUsers() {
        try {
            return this.atlassianUserManager.getUsers().getCurrentPage();
        }
        catch (EntityException e) {
            throw new RuntimeException("Entity Exception when retrieving Atlassian User users.");
        }
    }

    private boolean filterByCriteria(User user, String criteria) {
        return user.getName().toLowerCase().contains(criteria) || user.getFullName().toLowerCase().contains(criteria) || user.getEmail().toLowerCase().contains(criteria);
    }

    private UserProfile atlassianUserToSalUser(User atlassianUser) {
        return this.userManager.getUserProfile(atlassianUser.getName());
    }
}

