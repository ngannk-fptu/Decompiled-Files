/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.contributors.comparators;

import com.atlassian.confluence.contributors.util.AuthorRanking;
import java.util.Comparator;

public class AlphabeticalAuthorRankingComparator
implements Comparator<AuthorRanking> {
    @Override
    public int compare(AuthorRanking ranking1, AuthorRanking ranking2) {
        String lhsFullName = ranking1.getFullNameString();
        String rhsFullName = ranking2.getFullNameString();
        if (lhsFullName != null && rhsFullName != null) {
            return ranking1.getFullNameString().compareToIgnoreCase(ranking2.getFullNameString());
        }
        if (lhsFullName == null && rhsFullName == null) {
            return 0;
        }
        if (lhsFullName == null) {
            return -1;
        }
        return 1;
    }
}

