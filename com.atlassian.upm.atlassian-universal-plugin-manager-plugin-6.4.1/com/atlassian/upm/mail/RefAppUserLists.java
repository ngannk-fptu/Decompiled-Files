/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.mail;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.mail.ProductUserLists;
import java.util.Collections;
import java.util.Set;

public class RefAppUserLists
implements ProductUserLists {
    @Override
    public Set<UserKey> getSystemAdmins() {
        return Collections.emptySet();
    }

    @Override
    public Set<UserKey> getAdminsAndSystemAdmins() {
        return Collections.emptySet();
    }
}

