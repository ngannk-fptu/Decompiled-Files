/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.hibernate.bulk;

public interface BulkTransaction {
    public boolean shouldStartTransaction();

    public <T> boolean beginTransaction(T ... var1);

    public <T> boolean commitTransaciton();

    public <T> boolean rollbackTransaciton();
}

