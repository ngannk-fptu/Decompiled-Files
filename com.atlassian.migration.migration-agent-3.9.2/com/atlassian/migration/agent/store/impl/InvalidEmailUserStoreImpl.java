/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.InvalidEmailUser;
import com.atlassian.migration.agent.store.InvalidEmailUserStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class InvalidEmailUserStoreImpl
implements InvalidEmailUserStore {
    private final EntityManagerTemplate tmpl;

    public InvalidEmailUserStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public void saveInvalidEmailUserOrIgnoreIfExists(InvalidEmailUser invalidEmailUser) {
        this.tmpl.persist(invalidEmailUser);
    }

    @Override
    public Optional<InvalidEmailUser> findByUserName(@Nonnull String userName) {
        return this.tmpl.query(InvalidEmailUser.class, "select ieu from InvalidEmailUser ieu where ieu.userName=:userName").param("userName", (Object)userName).first();
    }

    @Override
    public Set<String> findAllUserNamesOfInvalidEmailUsers() {
        return new HashSet<String>(this.tmpl.query(String.class, "select distinct ieu.userName from InvalidEmailUser ieu ").list());
    }

    @Override
    public void deleteAll() {
        this.tmpl.query("delete from InvalidEmailUser ieu").update();
    }
}

