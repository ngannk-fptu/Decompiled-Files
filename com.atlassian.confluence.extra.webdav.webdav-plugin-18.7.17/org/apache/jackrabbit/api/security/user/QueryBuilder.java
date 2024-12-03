/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.apache.jackrabbit.api.security.user;

import javax.jcr.Value;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface QueryBuilder<T> {
    public void setSelector(@NotNull Class<? extends Authorizable> var1);

    public void setScope(@NotNull String var1, boolean var2);

    public void setCondition(@NotNull T var1);

    public void setSortOrder(@NotNull String var1, @NotNull Direction var2, boolean var3);

    public void setSortOrder(@NotNull String var1, @NotNull Direction var2);

    public void setLimit(@Nullable Value var1, long var2);

    public void setLimit(long var1, long var3);

    @NotNull
    public T nameMatches(@NotNull String var1);

    @NotNull
    public T neq(@NotNull String var1, @NotNull Value var2);

    @NotNull
    public T eq(@NotNull String var1, @NotNull Value var2);

    @NotNull
    public T lt(@NotNull String var1, @NotNull Value var2);

    @NotNull
    public T le(@NotNull String var1, @NotNull Value var2);

    @NotNull
    public T gt(@NotNull String var1, @NotNull Value var2);

    @NotNull
    public T ge(@NotNull String var1, @NotNull Value var2);

    @NotNull
    public T exists(@NotNull String var1);

    @NotNull
    public T like(@NotNull String var1, @NotNull String var2);

    @NotNull
    public T contains(@NotNull String var1, @NotNull String var2);

    @NotNull
    public T impersonates(@NotNull String var1);

    @NotNull
    public T not(@NotNull T var1);

    @NotNull
    public T and(@NotNull T var1, @NotNull T var2);

    @NotNull
    public T or(@NotNull T var1, @NotNull T var2);

    public static enum Direction {
        ASCENDING("ascending"),
        DESCENDING("descending");

        private final String direction;

        private Direction(String direction) {
            this.direction = direction;
        }

        public String getDirection() {
            return this.direction;
        }
    }
}

