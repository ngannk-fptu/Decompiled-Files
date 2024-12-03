/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.stash;

import java.io.IOException;

@FunctionalInterface
public interface IOFriendlyFunction<T, R> {
    public R apply(T var1) throws IOException;
}

