/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.permissions.Target
 *  com.atlassian.fugue.Option
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.internal.permissions;

import com.atlassian.confluence.api.model.permissions.Target;
import com.atlassian.fugue.Option;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface TargetResolver {
    public <T> @NonNull T resolveModelObject(Target var1, Class<T> var2);

    public <T> @NonNull Option<T> resolveHibernateObject(Target var1, Class<T> var2);

    public <T> @NonNull Option<T> resolveContainerHibernateObject(Target var1, Class<T> var2);

    public boolean isContainerTarget(Target var1);
}

