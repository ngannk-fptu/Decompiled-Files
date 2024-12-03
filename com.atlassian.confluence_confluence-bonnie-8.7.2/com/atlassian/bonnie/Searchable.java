/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.bonnie;

import java.util.Collection;

public interface Searchable {
    public long getId();

    public Collection getSearchableDependants();

    public boolean isIndexable();
}

