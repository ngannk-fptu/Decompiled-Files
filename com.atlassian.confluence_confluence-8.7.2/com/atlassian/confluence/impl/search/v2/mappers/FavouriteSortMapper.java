/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.collect.Iterators
 *  org.apache.lucene.search.FieldComparator
 *  org.apache.lucene.search.FieldComparatorSource
 *  org.apache.lucene.search.Sort
 *  org.apache.lucene.search.SortField
 *  org.apache.lucene.util.BytesRef
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneMultiTermFieldComparator;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSortMapper;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.FavouriteSort;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.collect.Iterators;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.BytesRef;

@Internal
public class FavouriteSortMapper
implements LuceneSortMapper<FavouriteSort> {
    @Override
    public Sort convertToLuceneSort(FavouriteSort searchSort) {
        boolean reverse = SearchSort.Order.DESCENDING.equals((Object)searchSort.getOrder());
        return new Sort(new SortField(SearchFieldNames.LABEL, FavouriteSortMapper.getFieldComparator(this.getComparator(searchSort.getOrder())), reverse));
    }

    private static FieldComparatorSource getFieldComparator(final Comparator<String> comparator) {
        return new FieldComparatorSource(){

            public FieldComparator<String> newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException {
                ConfluenceUser user = AuthenticatedUserThreadLocal.get();
                String favouriteLabel = LabelParser.render(new ParsedLabelName("favourite", user.getName()), true);
                return new LuceneMultiTermFieldComparator<String>(fieldname, (Function<Iterable<BytesRef>, String>)((Function<Iterable, String>)labels -> FavouriteSortMapper.getFavouriteLabel(labels, favouriteLabel)), (Comparator<String>)comparator, numHits);
            }
        };
    }

    private Comparator<String> getComparator(SearchSort.Order order) {
        return order == SearchSort.Order.ASCENDING ? Comparator.nullsLast(String::compareTo) : Comparator.nullsFirst(String::compareTo);
    }

    private static String getFavouriteLabel(Iterable<BytesRef> labelBytes, String favouriteLabel) {
        Iterator labels = Iterators.transform(labelBytes.iterator(), BytesRef::utf8ToString);
        return (String)Iterators.tryFind((Iterator)labels, label -> FavouriteSortMapper.isFavouriteLabel(label, favouriteLabel)).orNull();
    }

    private static boolean isFavouriteLabel(String label, String favouriteLabel) {
        return label.equals(favouriteLabel) || label.startsWith(favouriteLabel + ":");
    }
}

