/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.TermFrequency;
import cz.vutbr.web.csskit.TermFloatValueImpl;

public class TermFrequencyImpl
extends TermFloatValueImpl
implements TermFrequency {
    protected TermFrequencyImpl() {
    }

    public TermFrequency setValue(Float value) {
        if (value == null || new Float(0.0f).compareTo(value) > 0) {
            throw new IllegalArgumentException("Null or negative value for CSS time");
        }
        this.value = value;
        return this;
    }
}

