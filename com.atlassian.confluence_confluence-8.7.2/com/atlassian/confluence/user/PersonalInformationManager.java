/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.user;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;

@ParametersAreNonnullByDefault
@Transactional
public interface PersonalInformationManager {
    public @NonNull PersonalInformation getOrCreatePersonalInformation(User var1);

    public void savePersonalInformation(PersonalInformation var1, @Nullable PersonalInformation var2);

    public void savePersonalInformation(User var1, String var2, String var3);

    public void removePersonalInformation(@Nullable ConfluenceUser var1);

    public PersonalInformation createPersonalInformation(User var1);

    public boolean hasPersonalInformation(@Nullable String var1);

    public boolean hasPersonalInformation(@Nullable UserKey var1);
}

