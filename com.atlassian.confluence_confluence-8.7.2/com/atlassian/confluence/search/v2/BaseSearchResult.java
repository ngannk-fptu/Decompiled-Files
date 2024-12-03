/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Handle
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.bonnie.Handle;
import java.util.Set;

public interface BaseSearchResult {
    public Handle getHandle();

    public long getHandleId();

    public Set<String> getFieldNames();

    public String getField(String var1);

    public Set<String> getFieldValues(String var1);
}

