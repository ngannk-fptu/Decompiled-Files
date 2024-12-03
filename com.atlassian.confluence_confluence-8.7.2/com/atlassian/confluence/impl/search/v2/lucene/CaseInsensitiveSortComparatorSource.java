/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.FieldComparator
 *  org.apache.lucene.search.FieldComparatorSource
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import com.atlassian.confluence.impl.search.v2.lucene.CaseInsensitiveSortComparator;
import java.io.IOException;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;

public class CaseInsensitiveSortComparatorSource
extends FieldComparatorSource {
    public FieldComparator<?> newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException {
        return new CaseInsensitiveSortComparator(numHits, fieldname);
    }
}

