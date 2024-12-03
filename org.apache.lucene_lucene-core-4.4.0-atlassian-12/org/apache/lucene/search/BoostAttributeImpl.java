/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.search.BoostAttribute;
import org.apache.lucene.util.AttributeImpl;

public final class BoostAttributeImpl
extends AttributeImpl
implements BoostAttribute {
    private float boost = 1.0f;

    @Override
    public void setBoost(float boost) {
        this.boost = boost;
    }

    @Override
    public float getBoost() {
        return this.boost;
    }

    @Override
    public void clear() {
        this.boost = 1.0f;
    }

    @Override
    public void copyTo(AttributeImpl target) {
        ((BoostAttribute)((Object)target)).setBoost(this.boost);
    }
}

