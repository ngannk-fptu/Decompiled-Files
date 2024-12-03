/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.vectorhighlight;

import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.search.vectorhighlight.FieldFragList;
import org.apache.lucene.search.vectorhighlight.FieldPhraseList;

public class SimpleFieldFragList
extends FieldFragList {
    public SimpleFieldFragList(int fragCharSize) {
        super(fragCharSize);
    }

    @Override
    public void add(int startOffset, int endOffset, List<FieldPhraseList.WeightedPhraseInfo> phraseInfoList) {
        float totalBoost = 0.0f;
        ArrayList<FieldFragList.WeightedFragInfo.SubInfo> subInfos = new ArrayList<FieldFragList.WeightedFragInfo.SubInfo>();
        for (FieldPhraseList.WeightedPhraseInfo phraseInfo : phraseInfoList) {
            subInfos.add(new FieldFragList.WeightedFragInfo.SubInfo(phraseInfo.getText(), phraseInfo.getTermsOffsets(), phraseInfo.getSeqnum()));
            totalBoost += phraseInfo.getBoost();
        }
        this.getFragInfos().add(new FieldFragList.WeightedFragInfo(startOffset, endOffset, subInfos, totalBoost));
    }
}

