/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.themes.persistence;

import com.atlassian.confluence.core.PersistentDecorator;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PersistentDecoratorDao {
    public void saveOrUpdate(PersistentDecorator var1);

    @Transactional(readOnly=true)
    public @Nullable PersistentDecorator get(@Nullable String var1, String var2);

    public void remove(PersistentDecorator var1);
}

