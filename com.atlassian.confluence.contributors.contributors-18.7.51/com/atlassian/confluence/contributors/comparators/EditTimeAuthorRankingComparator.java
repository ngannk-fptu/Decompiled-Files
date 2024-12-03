/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.contributors.comparators;

import com.atlassian.confluence.contributors.comparators.AlphabeticalAuthorRankingComparator;
import com.atlassian.confluence.contributors.util.AuthorRanking;
import java.util.Comparator;

public class EditTimeAuthorRankingComparator
implements Comparator<AuthorRanking> {
    @Override
    public int compare(AuthorRanking ranking1, AuthorRanking ranking2) {
        if (ranking1.getLastEditTime() < ranking2.getLastEditTime()) {
            return 1;
        }
        if (ranking1.getLastEditTime() > ranking2.getLastEditTime()) {
            return -1;
        }
        return new AlphabeticalAuthorRankingComparator().compare(ranking1, ranking2);
    }
}

