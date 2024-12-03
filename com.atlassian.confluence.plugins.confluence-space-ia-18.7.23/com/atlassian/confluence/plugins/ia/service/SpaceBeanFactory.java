/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.ia.service;

import com.atlassian.confluence.plugins.ia.model.SpaceBean;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;

public interface SpaceBeanFactory {
    public SpaceBean createSpaceBean(Space var1, User var2);
}

