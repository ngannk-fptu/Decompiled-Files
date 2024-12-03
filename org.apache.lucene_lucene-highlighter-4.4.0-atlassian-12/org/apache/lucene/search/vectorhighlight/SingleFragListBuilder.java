/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.vectorhighlight;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.lucene.search.vectorhighlight.FieldFragList;
import org.apache.lucene.search.vectorhighlight.FieldPhraseList;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFieldFragList;

public class SingleFragListBuilder
implements FragListBuilder {
    @Override
    public FieldFragList createFieldFragList(FieldPhraseList fieldPhraseList, int fragCharSize) {
        SimpleFieldFragList ffl = new SimpleFieldFragList(fragCharSize);
        ArrayList<FieldPhraseList.WeightedPhraseInfo> wpil = new ArrayList<FieldPhraseList.WeightedPhraseInfo>();
        Iterator ite = fieldPhraseList.phraseList.iterator();
        FieldPhraseList.WeightedPhraseInfo phraseInfo = null;
        while (ite.hasNext() && (phraseInfo = (FieldPhraseList.WeightedPhraseInfo)ite.next()) != null) {
            wpil.add(phraseInfo);
        }
        if (wpil.size() > 0) {
            ((FieldFragList)ffl).add(0, Integer.MAX_VALUE, wpil);
        }
        return ffl;
    }
}

