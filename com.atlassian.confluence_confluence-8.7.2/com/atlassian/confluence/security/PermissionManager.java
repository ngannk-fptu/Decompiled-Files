/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.security.Permission;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PermissionManager {
    public static final Object TARGET_APPLICATION = "APPLICATION";
    public static final Object TARGET_SYSTEM = "SYSTEM";
    public static final Object TARGET_PEOPLE_DIRECTORY = "PEOPLEDIRECTORY";

    @Transactional(readOnly=true)
    public boolean hasPermission(@Nullable User var1, Permission var2, @Nullable Object var3);

    @Transactional(readOnly=true)
    public boolean hasPermission(User var1, Permission var2, Class var3);

    @Transactional(readOnly=true)
    public boolean hasPermissionNoExemptions(User var1, Permission var2, Object var3);

    @Transactional(readOnly=true)
    public boolean hasCreatePermission(User var1, Object var2, Class<?> var3);

    @Transactional(readOnly=true)
    public boolean hasCreatePermission(User var1, Object var2, Object var3);

    @Transactional(readOnly=true)
    public <X> List<X> getPermittedEntities(User var1, Permission var2, List<? extends X> var3);

    @Transactional(readOnly=true)
    public <X> List<X> getPermittedEntities(User var1, Permission var2, Iterator<? extends X> var3, int var4);

    @Transactional(readOnly=true)
    public <X> List<X> getPermittedEntities(User var1, Permission var2, Iterator<X> var3, int var4, Collection<? extends Criterion> var5);

    @Transactional(readOnly=true)
    public <X> List<X> getPermittedEntitiesNoExemptions(User var1, Permission var2, List<? extends X> var3);

    @Transactional(readOnly=true)
    public <X> List<X> getPermittedEntitiesNoExemptions(User var1, Permission var2, Iterator<? extends X> var3, int var4);

    @Transactional(readOnly=true)
    public <X> List<X> getPermittedEntitiesNoExemptions(User var1, Permission var2, Iterator<X> var3, int var4, Collection<? extends Criterion> var5);

    @Transactional(readOnly=true)
    public boolean isConfluenceAdministrator(User var1);

    @Transactional(readOnly=true)
    public boolean isSystemAdministrator(@Nullable User var1);

    public void withExemption(Runnable var1);

    @Transactional(readOnly=true)
    public boolean hasMovePermission(User var1, Object var2, Object var3, String var4);

    @Transactional(readOnly=true)
    public boolean hasRemoveHierarchyPermission(User var1, Object var2);

    public static interface Criterion {
        public boolean test(List<?> var1, Object var2);
    }
}

