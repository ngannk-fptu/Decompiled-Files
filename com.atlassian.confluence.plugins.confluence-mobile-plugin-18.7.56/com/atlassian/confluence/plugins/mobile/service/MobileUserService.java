/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Person
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.mobile.service;

import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.plugins.mobile.dto.UserDto;
import java.util.List;
import javax.annotation.Nonnull;

public interface MobileUserService {
    @Nonnull
    public UserDto getCurrentUser();

    @Nonnull
    public List<Person> getConcurrentEditingUser(@Nonnull Long var1);
}

