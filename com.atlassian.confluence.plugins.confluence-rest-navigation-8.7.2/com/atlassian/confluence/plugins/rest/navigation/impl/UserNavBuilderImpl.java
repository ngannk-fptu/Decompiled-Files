/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.Navigation$UserNav
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.rest.navigation.impl;

import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.plugins.rest.navigation.impl.AbstractNav;
import com.atlassian.confluence.plugins.rest.navigation.impl.DelegatingPathBuilder;
import com.atlassian.sal.api.user.UserKey;

class UserNavBuilderImpl
extends DelegatingPathBuilder
implements Navigation.UserNav {
    public UserNavBuilderImpl(UserKey userKey, AbstractNav basePath) {
        super("/user", basePath);
        this.addParam("key", userKey);
    }

    public Navigation.Builder memberOf() {
        return new DelegatingPathBuilder("/memberof", this);
    }
}

