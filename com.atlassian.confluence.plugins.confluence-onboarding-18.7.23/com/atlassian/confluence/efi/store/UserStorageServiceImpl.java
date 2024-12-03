/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.efi.store;

import com.atlassian.confluence.efi.store.UserStorageService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserStorageServiceImpl
implements UserStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserStorageServiceImpl.class);
    private final UserAccessor userAccessor;

    @Autowired
    public UserStorageServiceImpl(@ComponentImport UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    @Override
    public String get(String key, ConfluenceUser user) {
        return this.userAccessor.getPropertySet(user).getString("efi.store.onboarding." + key);
    }

    @Override
    public void set(String key, String value, ConfluenceUser user) {
        this.userAccessor.getPropertySet(user).setString("efi.store.onboarding." + key, value);
    }

    @Override
    public void remove(String key, ConfluenceUser user) {
        this.userAccessor.getPropertySet(user).remove("efi.store.onboarding." + key);
    }
}

