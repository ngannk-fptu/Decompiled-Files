/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.vectorhighlight;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.lucene.search.vectorhighlight.BaseFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.BoundaryScanner;
import org.apache.lucene.search.vectorhighlight.FieldFragList;

public class ScoreOrderFragmentsBuilder
extends BaseFragmentsBuilder {
    public ScoreOrderFragmentsBuilder() {
    }

    public ScoreOrderFragmentsBuilder(String[] preTags, String[] postTags) {
        super(preTags, postTags);
    }

    public ScoreOrderFragmentsBuilder(BoundaryScanner bs) {
        super(bs);
    }

    public ScoreOrderFragmentsBuilder(String[] preTags, String[] postTags, BoundaryScanner bs) {
        super(preTags, postTags, bs);
    }

    @Override
    public List<FieldFragList.WeightedFragInfo> getWeightedFragInfoList(List<FieldFragList.WeightedFragInfo> src) {
        Collections.sort(src, new ScoreComparator());
        return src;
    }

    public static class ScoreComparator
    implements Comparator<FieldFragList.WeightedFragInfo> {
        @Override
        public int compare(FieldFragList.WeightedFragInfo o1, FieldFragList.WeightedFragInfo o2) {
            if (o1.getTotalBoost() > o2.getTotalBoost()) {
                return -1;
            }
            if (o1.getTotalBoost() < o2.getTotalBoost()) {
                return 1;
            }
            if (o1.getStartOffset() < o2.getStartOffset()) {
                return -1;
            }
            if (o1.getStartOffset() > o2.getStartOffset()) {
                return 1;
            }
            return 0;
        }
    }
}

