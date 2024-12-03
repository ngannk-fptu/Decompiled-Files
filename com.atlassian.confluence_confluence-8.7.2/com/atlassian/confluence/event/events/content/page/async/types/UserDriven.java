/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.event.events.content.page.async.types;

import com.atlassian.sal.api.user.UserKey;

public interface UserDriven {
    public UserKey getOriginatingUserKey();
}

