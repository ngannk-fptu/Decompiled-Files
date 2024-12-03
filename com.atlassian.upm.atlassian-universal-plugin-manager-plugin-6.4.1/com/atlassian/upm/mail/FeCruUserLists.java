/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.cenqua.fisheye.AppConfig
 */
package com.atlassian.upm.mail;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.mail.ProductUserLists;
import com.cenqua.fisheye.AppConfig;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class FeCruUserLists
implements ProductUserLists {
    @Override
    public Set<UserKey> getSystemAdmins() {
        return this.getAdminsAndSystemAdmins();
    }

    @Override
    public Set<UserKey> getAdminsAndSystemAdmins() {
        String[] sysadmins = AppConfig.getsConfig().getConfig().getSecurity().getAdmins().getSystemAdmins().getUserArray();
        return Collections.unmodifiableSet(Arrays.stream(sysadmins).map(UserKey::new).collect(Collectors.toSet()));
    }
}

