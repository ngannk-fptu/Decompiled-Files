/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.contributors.search;

import com.atlassian.annotations.Internal;
import java.util.Set;
import java.util.function.BiConsumer;

@Internal
public interface SpaceWatcherScanner {
    public void scan(Set<String> var1, BiConsumer<String, String> var2);
}

