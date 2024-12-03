/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface UserProfilePictureAccessor {
    @Transactional(propagation=Propagation.SUPPORTS)
    public ProfilePictureInfo getUserProfilePicture(@Nullable User var1);
}

