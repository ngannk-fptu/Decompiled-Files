/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.contributors.util;

import com.atlassian.confluence.contributors.comparators.AlphabeticalAuthorRankingComparator;
import com.atlassian.confluence.contributors.comparators.EditCountAuthorRankingComparator;
import com.atlassian.confluence.contributors.comparators.EditTimeAuthorRankingComparator;
import com.atlassian.confluence.contributors.comparators.LastActiveTimeRankingComparator;
import com.atlassian.confluence.contributors.comparators.TotalCountAuthorRankingComparator;
import com.atlassian.confluence.contributors.util.AuthorRanking;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorRankingSystem {
    private Map<String, AuthorRanking> authorRankMap = new HashMap<String, AuthorRanking>();
    private final RankType rankingType;

    public AuthorRankingSystem(RankType rankingType) {
        this.rankingType = rankingType;
    }

    public boolean hasRanking(String authorId) {
        return this.authorRankMap.containsKey(authorId);
    }

    public AuthorRanking computeAuthorRanking(String authorId, String fullName) {
        return this.authorRankMap.computeIfAbsent(authorId, x -> new AuthorRanking(authorId, fullName));
    }

    public AuthorRanking createAuthorRanking(String authorId, String fullName, long lastEditTime) {
        AuthorRanking ranking = new AuthorRanking(authorId, fullName, lastEditTime);
        this.authorRankMap.put(authorId, ranking);
        return ranking;
    }

    public AuthorRanking createAuthorRanking(String authorId, String fullName) {
        AuthorRanking ranking = new AuthorRanking(authorId, fullName);
        this.authorRankMap.put(authorId, ranking);
        return ranking;
    }

    public AuthorRanking getAuthorRanking(String authorId) {
        return this.authorRankMap.get(authorId);
    }

    public List<AuthorRanking> getRankedAuthors() {
        return this.getRankedAuthors(false);
    }

    public List<AuthorRanking> getRankedAuthors(boolean reverse) {
        ArrayList<AuthorRanking> authorRankings = new ArrayList<AuthorRanking>(this.authorRankMap.values());
        Comparator originalComparator = this.rankingType.getComparator();
        Comparator comparator = reverse ? (object, object1) -> -originalComparator.compare(object, object1) : originalComparator;
        Collections.sort(authorRankings, comparator);
        return authorRankings;
    }

    public List<AuthorRanking> getRankedAuthors(boolean reverse, boolean keepEditors, boolean keepCommentors, boolean keepLabelers, boolean keepWatches) {
        List<AuthorRanking> sortedList = this.getRankedAuthors(reverse);
        sortedList.removeIf(authorRanking -> !(keepEditors && authorRanking.getEdits() > 0 || keepCommentors && authorRanking.getComments() > 0 || keepLabelers && authorRanking.getLabels() > 0 || keepWatches && authorRanking.getWatches() > 0));
        return sortedList;
    }

    public List<AuthorRanking> getAuthors() {
        return new ArrayList<AuthorRanking>(this.authorRankMap.values());
    }

    public int size() {
        return this.authorRankMap.size();
    }

    public static enum RankType {
        FULL_NAME(new AlphabeticalAuthorRankingComparator()),
        EDIT_COUNT(new EditCountAuthorRankingComparator()),
        TOTAL_COUNT(new TotalCountAuthorRankingComparator()),
        EDIT_TIME(new EditTimeAuthorRankingComparator()),
        LAST_ACTIVE_TIME(new LastActiveTimeRankingComparator());

        private final Comparator<AuthorRanking> comparator;

        private RankType(Comparator<AuthorRanking> comparator) {
            this.comparator = comparator;
        }

        public Comparator<AuthorRanking> getComparator() {
            return this.comparator;
        }
    }
}

