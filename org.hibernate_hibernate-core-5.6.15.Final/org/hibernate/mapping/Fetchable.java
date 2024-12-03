/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.FetchMode;

public interface Fetchable {
    public FetchMode getFetchMode();

    public void setFetchMode(FetchMode var1);

    public boolean isLazy();

    public void setLazy(boolean var1);
}

