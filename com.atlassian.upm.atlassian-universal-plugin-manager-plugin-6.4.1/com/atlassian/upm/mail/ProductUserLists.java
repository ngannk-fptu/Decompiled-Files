/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.mail;

import com.atlassian.sal.api.user.UserKey;
import java.util.Set;

public interface ProductUserLists {
    public Set<UserKey> getSystemAdmins();

    public Set<UserKey> getAdminsAndSystemAdmins();
}

