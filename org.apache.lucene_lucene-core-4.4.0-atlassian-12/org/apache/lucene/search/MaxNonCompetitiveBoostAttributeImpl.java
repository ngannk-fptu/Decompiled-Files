/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.search.MaxNonCompetitiveBoostAttribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.BytesRef;

public final class MaxNonCompetitiveBoostAttributeImpl
extends AttributeImpl
implements MaxNonCompetitiveBoostAttribute {
    private float maxNonCompetitiveBoost = Float.NEGATIVE_INFINITY;
    private BytesRef competitiveTerm = null;

    @Override
    public void setMaxNonCompetitiveBoost(float maxNonCompetitiveBoost) {
        this.maxNonCompetitiveBoost = maxNonCompetitiveBoost;
    }

    @Override
    public float getMaxNonCompetitiveBoost() {
        return this.maxNonCompetitiveBoost;
    }

    @Override
    public void setCompetitiveTerm(BytesRef competitiveTerm) {
        this.competitiveTerm = competitiveTerm;
    }

    @Override
    public BytesRef getCompetitiveTerm() {
        return this.competitiveTerm;
    }

    @Override
    public void clear() {
        this.maxNonCompetitiveBoost = Float.NEGATIVE_INFINITY;
        this.competitiveTerm = null;
    }

    @Override
    public void copyTo(AttributeImpl target) {
        MaxNonCompetitiveBoostAttributeImpl t = (MaxNonCompetitiveBoostAttributeImpl)target;
        t.setMaxNonCompetitiveBoost(this.maxNonCompetitiveBoost);
        t.setCompetitiveTerm(this.competitiveTerm);
    }
}

