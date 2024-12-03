/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.contributors.search;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.contributors.search.Doc;
import java.util.Set;
import java.util.function.Consumer;

@Internal
public interface CommentScanner {
    public void scan(Set<Long> var1, Consumer<Doc> var2);
}

