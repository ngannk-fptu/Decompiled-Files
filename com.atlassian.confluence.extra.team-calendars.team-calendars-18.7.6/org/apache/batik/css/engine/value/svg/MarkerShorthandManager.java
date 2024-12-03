/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.LexicalUnit
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueFactory;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class MarkerShorthandManager
extends AbstractValueFactory
implements ShorthandManager {
    @Override
    public String getPropertyName() {
        return "marker";
    }

    @Override
    public boolean isAnimatableProperty() {
        return true;
    }

    @Override
    public boolean isAdditiveProperty() {
        return false;
    }

    @Override
    public void setValues(CSSEngine eng, ShorthandManager.PropertyHandler ph, LexicalUnit lu, boolean imp) throws DOMException {
        ph.property("marker-end", lu, imp);
        ph.property("marker-mid", lu, imp);
        ph.property("marker-start", lu, imp);
    }
}

