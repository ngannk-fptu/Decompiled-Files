/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.inlinecomments.service;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

public class UserStorageService {
    private final BandanaManager bandanaManager;
    private static final BandanaContext GLOBAL_DATA_CONTEXT = new ConfluenceBandanaContext(UserStorageService.class.getName());
    private final String SEPARATE_CHARACTER = ",";
    private final List<String> KEY_ACCEPTED = Arrays.asList("GROW-1642", "GROW-1507");

    public UserStorageService(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public boolean isKeyStoredForCurrentUser(String key) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null || StringUtils.isEmpty((CharSequence)key) || !this.KEY_ACCEPTED.contains(key)) {
            return false;
        }
        Object users = this.bandanaManager.getValue(GLOBAL_DATA_CONTEXT, key);
        return users != null && users.toString().contains(user.getName() + ",");
    }

    public void storeKeyForCurrentUser(String key) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null || StringUtils.isEmpty((CharSequence)key) || !this.KEY_ACCEPTED.contains(key)) {
            throw new BadRequestException(Response.Status.BAD_REQUEST.getReasonPhrase());
        }
        Object users = this.bandanaManager.getValue(GLOBAL_DATA_CONTEXT, key);
        if (users == null || !users.toString().contains(user.getName() + ",")) {
            this.bandanaManager.setValue(GLOBAL_DATA_CONTEXT, key, (Object)(users + user.getName() + ","));
        }
    }
}

