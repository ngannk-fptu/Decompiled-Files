/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.vectorhighlight;

import java.util.List;
import org.apache.lucene.search.vectorhighlight.BaseFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.BoundaryScanner;
import org.apache.lucene.search.vectorhighlight.FieldFragList;

public class SimpleFragmentsBuilder
extends BaseFragmentsBuilder {
    public SimpleFragmentsBuilder() {
    }

    public SimpleFragmentsBuilder(String[] preTags, String[] postTags) {
        super(preTags, postTags);
    }

    public SimpleFragmentsBuilder(BoundaryScanner bs) {
        super(bs);
    }

    public SimpleFragmentsBuilder(String[] preTags, String[] postTags, BoundaryScanner bs) {
        super(preTags, postTags, bs);
    }

    @Override
    public List<FieldFragList.WeightedFragInfo> getWeightedFragInfoList(List<FieldFragList.WeightedFragInfo> src) {
        return src;
    }
}

