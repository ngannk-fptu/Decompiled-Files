/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.contributors.util;

import com.atlassian.confluence.contributors.search.Doc;
import com.atlassian.confluence.contributors.util.AuthorRankingSystem;

public interface PageProcessor {
    public AuthorRankingSystem process(Iterable<Doc> var1, AuthorRankingSystem.RankType var2, GroupBy var3);

    public AuthorRankingSystem process(Iterable<Doc> var1, AuthorRankingSystem.RankType var2, GroupBy var3, boolean var4, boolean var5, boolean var6, boolean var7);

    public static enum GroupBy {
        CONTRIBUTORS,
        PAGES;

    }
}

