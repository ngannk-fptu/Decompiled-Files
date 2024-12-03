/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk;

public interface PageNameConflictResolver {
    public boolean couldProvideNewName();

    public int getMaxRetryNumber();

    public String resolveConflict(int var1, String var2);
}

