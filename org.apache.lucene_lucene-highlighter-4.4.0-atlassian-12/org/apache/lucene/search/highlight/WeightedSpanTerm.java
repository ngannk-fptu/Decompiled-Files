/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.highlight;

import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.search.highlight.PositionSpan;
import org.apache.lucene.search.highlight.WeightedTerm;

public class WeightedSpanTerm
extends WeightedTerm {
    boolean positionSensitive;
    private List<PositionSpan> positionSpans = new ArrayList<PositionSpan>();

    public WeightedSpanTerm(float weight, String term) {
        super(weight, term);
        this.positionSpans = new ArrayList<PositionSpan>();
    }

    public WeightedSpanTerm(float weight, String term, boolean positionSensitive) {
        super(weight, term);
        this.positionSensitive = positionSensitive;
    }

    public boolean checkPosition(int position) {
        for (PositionSpan posSpan : this.positionSpans) {
            if (position < posSpan.start || position > posSpan.end) continue;
            return true;
        }
        return false;
    }

    public void addPositionSpans(List<PositionSpan> positionSpans) {
        this.positionSpans.addAll(positionSpans);
    }

    public boolean isPositionSensitive() {
        return this.positionSensitive;
    }

    public void setPositionSensitive(boolean positionSensitive) {
        this.positionSensitive = positionSensitive;
    }

    public List<PositionSpan> getPositionSpans() {
        return this.positionSpans;
    }
}

