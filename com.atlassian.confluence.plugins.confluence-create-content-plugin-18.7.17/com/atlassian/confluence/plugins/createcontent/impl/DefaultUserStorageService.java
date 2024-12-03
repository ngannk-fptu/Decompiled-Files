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
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.plugins.createcontent.services.UserStorageService;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserStorageService
implements UserStorageService {
    private final BandanaManager bandanaManager;
    private static final BandanaContext GLOBAL_DATA_CONTEXT = new ConfluenceBandanaContext(DefaultUserStorageService.class.getName());
    private final String SEPARATE_CHARACTER = ",";
    private final List<String> KEY_ACCEPTED = Collections.singletonList("quick-create");

    @Autowired
    public DefaultUserStorageService(@ComponentImport BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    @Override
    public boolean isKeyStoredForCurrentUser(String key) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null || StringUtils.isEmpty((CharSequence)key) || !this.KEY_ACCEPTED.contains(key)) {
            return false;
        }
        Object users = this.bandanaManager.getValue(GLOBAL_DATA_CONTEXT, key);
        return users != null && users.toString().contains(user.getName() + ",");
    }

    @Override
    public void storeKeyForCurrentUser(String key) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null || user.getName() == null || StringUtils.isEmpty((CharSequence)key) || !this.KEY_ACCEPTED.contains(key)) {
            throw new BadRequestException(Response.Status.BAD_REQUEST.getReasonPhrase());
        }
        Object users = this.bandanaManager.getValue(GLOBAL_DATA_CONTEXT, key);
        if (users == null || !users.toString().contains(user.getName() + ",")) {
            this.bandanaManager.setValue(GLOBAL_DATA_CONTEXT, key, (Object)((users == null ? "" : users) + user.getName() + ","));
        }
    }

    @Override
    public void removeKeyForUser(String key, User user) {
        if (key == null || !this.KEY_ACCEPTED.contains(key)) {
            throw new UnsupportedOperationException("The key supplied is not valid.");
        }
        if (user != null && user.getName() != null && user.getName().length() != 0) {
            Object bandanaRecord = this.bandanaManager.getValue(GLOBAL_DATA_CONTEXT, key);
            if (bandanaRecord == null) {
                return;
            }
            String tokenToRemove = user.getName() + ",";
            String userlistString = bandanaRecord.toString();
            if (userlistString.contains(tokenToRemove)) {
                String amendedUserList = userlistString.replaceFirst(Matcher.quoteReplacement(tokenToRemove), "");
                if (!amendedUserList.isEmpty()) {
                    this.bandanaManager.setValue(GLOBAL_DATA_CONTEXT, key, (Object)amendedUserList);
                } else {
                    this.bandanaManager.removeValue(GLOBAL_DATA_CONTEXT, key);
                }
            }
        } else {
            throw new UnsupportedOperationException("The user supplied is not valid.");
        }
    }
}

