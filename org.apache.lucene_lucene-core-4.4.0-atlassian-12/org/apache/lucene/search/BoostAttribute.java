/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.util.Attribute;

public interface BoostAttribute
extends Attribute {
    public void setBoost(float var1);

    public float getBoost();
}

