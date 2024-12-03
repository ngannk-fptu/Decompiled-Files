/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Group
 *  javax.annotation.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.user;

import com.atlassian.user.Group;
import javax.annotation.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface GroupResolver {
    @Nullable
    public Group getGroup(String var1);
}

