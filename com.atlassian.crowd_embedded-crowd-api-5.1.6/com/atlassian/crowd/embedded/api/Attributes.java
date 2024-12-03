/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.embedded.api;

import java.util.Set;
import javax.annotation.Nullable;

public interface Attributes {
    @Nullable
    public Set<String> getValues(String var1);

    @Nullable
    public String getValue(String var1);

    public Set<String> getKeys();

    public boolean isEmpty();
}

