/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.rdbms.ConnectionCallback
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 */
package com.atlassian.sal.spi;

import com.atlassian.sal.api.rdbms.ConnectionCallback;
import io.atlassian.fugue.Option;
import javax.annotation.Nonnull;

public interface HostConnectionAccessor {
    public <A> A execute(boolean var1, boolean var2, @Nonnull ConnectionCallback<A> var3);

    @Nonnull
    public Option<String> getSchemaName();
}

