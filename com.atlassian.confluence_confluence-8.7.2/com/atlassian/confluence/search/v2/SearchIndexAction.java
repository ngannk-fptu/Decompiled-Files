/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.SearchIndexWriter;
import java.io.IOException;

@FunctionalInterface
public interface SearchIndexAction {
    public void accept(SearchIndexWriter var1) throws IOException;
}

