/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.vectorhighlight;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.lucene.search.vectorhighlight.FieldFragList;
import org.apache.lucene.search.vectorhighlight.FieldPhraseList;
import org.apache.lucene.search.vectorhighlight.FieldTermStack;

public class WeightedFieldFragList
extends FieldFragList {
    public WeightedFieldFragList(int fragCharSize) {
        super(fragCharSize);
    }

    @Override
    public void add(int startOffset, int endOffset, List<FieldPhraseList.WeightedPhraseInfo> phraseInfoList) {
        float totalBoost = 0.0f;
        ArrayList<FieldFragList.WeightedFragInfo.SubInfo> subInfos = new ArrayList<FieldFragList.WeightedFragInfo.SubInfo>();
        HashSet<String> distinctTerms = new HashSet<String>();
        int length = 0;
        for (FieldPhraseList.WeightedPhraseInfo phraseInfo : phraseInfoList) {
            subInfos.add(new FieldFragList.WeightedFragInfo.SubInfo(phraseInfo.getText(), phraseInfo.getTermsOffsets(), phraseInfo.getSeqnum()));
            for (FieldTermStack.TermInfo ti : phraseInfo.getTermsInfos()) {
                if (distinctTerms.add(ti.getText())) {
                    totalBoost += ti.getWeight() * phraseInfo.getBoost();
                }
                ++length;
            }
        }
        totalBoost = (float)((double)totalBoost * ((double)length * (1.0 / Math.sqrt(length))));
        this.getFragInfos().add(new FieldFragList.WeightedFragInfo(startOffset, endOffset, subInfos, totalBoost));
    }
}

