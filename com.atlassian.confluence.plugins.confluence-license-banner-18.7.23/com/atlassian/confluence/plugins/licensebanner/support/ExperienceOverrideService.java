/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.licensebanner.support;

import com.atlassian.sal.api.user.UserKey;

public interface ExperienceOverrideService {
    public boolean isOverridden(UserKey var1, String var2);
}

