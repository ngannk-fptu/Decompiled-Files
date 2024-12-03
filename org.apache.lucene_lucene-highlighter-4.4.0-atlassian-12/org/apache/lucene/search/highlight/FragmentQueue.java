/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.PriorityQueue
 */
package org.apache.lucene.search.highlight;

import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.util.PriorityQueue;

class FragmentQueue
extends PriorityQueue<TextFragment> {
    public FragmentQueue(int size) {
        super(size);
    }

    public final boolean lessThan(TextFragment fragA, TextFragment fragB) {
        if (fragA.getScore() == fragB.getScore()) {
            return fragA.fragNum > fragB.fragNum;
        }
        return fragA.getScore() < fragB.getScore();
    }
}

