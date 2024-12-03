/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.plugins.rest.dto;

import com.atlassian.confluence.plugins.rest.dto.UserDto;
import com.atlassian.confluence.user.ConfluenceUser;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface UserDtoFactory {
    public UserDto getUserDto(@Nullable ConfluenceUser var1);
}

