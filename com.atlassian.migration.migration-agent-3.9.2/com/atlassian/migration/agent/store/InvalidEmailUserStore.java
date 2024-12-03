/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.InvalidEmailUser;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;

public interface InvalidEmailUserStore {
    public Optional<InvalidEmailUser> findByUserName(@Nonnull String var1);

    public void saveInvalidEmailUserOrIgnoreIfExists(@Nonnull InvalidEmailUser var1);

    public Set<String> findAllUserNamesOfInvalidEmailUsers();

    public void deleteAll();
}

