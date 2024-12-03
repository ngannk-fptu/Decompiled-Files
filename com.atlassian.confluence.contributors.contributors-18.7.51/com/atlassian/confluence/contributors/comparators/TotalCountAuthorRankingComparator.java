/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.contributors.comparators;

import com.atlassian.confluence.contributors.comparators.AlphabeticalAuthorRankingComparator;
import com.atlassian.confluence.contributors.util.AuthorRanking;
import java.util.Comparator;

public class TotalCountAuthorRankingComparator
implements Comparator<AuthorRanking> {
    @Override
    public int compare(AuthorRanking ranking1, AuthorRanking ranking2) {
        int total2;
        int total1 = ranking1.getTotalCount();
        if (total1 < (total2 = ranking2.getTotalCount())) {
            return 1;
        }
        if (total1 > total2) {
            return -1;
        }
        return new AlphabeticalAuthorRankingComparator().compare(ranking1, ranking2);
    }
}

