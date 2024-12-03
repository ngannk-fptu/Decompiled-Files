/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.BytesRef;

public interface MaxNonCompetitiveBoostAttribute
extends Attribute {
    public void setMaxNonCompetitiveBoost(float var1);

    public float getMaxNonCompetitiveBoost();

    public void setCompetitiveTerm(BytesRef var1);

    public BytesRef getCompetitiveTerm();
}

