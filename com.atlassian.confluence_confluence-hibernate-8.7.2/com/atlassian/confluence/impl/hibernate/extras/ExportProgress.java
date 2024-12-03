/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.hibernate.extras;

@Deprecated
public interface ExportProgress {
    public void setStatus(String var1);

    public int increment();

    public void setTotal(int var1);

    public void incrementTotal();
}

