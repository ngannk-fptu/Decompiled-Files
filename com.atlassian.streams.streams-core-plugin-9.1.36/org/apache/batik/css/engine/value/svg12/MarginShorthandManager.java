/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg12;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueFactory;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class MarginShorthandManager
extends AbstractValueFactory
implements ShorthandManager {
    @Override
    public String getPropertyName() {
        return "margin";
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
        if (lu.getLexicalUnitType() == 12) {
            return;
        }
        LexicalUnit[] lus = new LexicalUnit[4];
        int cnt = 0;
        while (lu != null) {
            if (cnt == 4) {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
            lus[cnt++] = lu;
            lu = lu.getNextLexicalUnit();
        }
        switch (cnt) {
            case 1: {
                lus[2] = lus[1] = lus[0];
                lus[3] = lus[1];
                break;
            }
            case 2: {
                lus[2] = lus[0];
                lus[3] = lus[1];
                break;
            }
            case 3: {
                lus[3] = lus[1];
                break;
            }
        }
        ph.property("margin-top", lus[0], imp);
        ph.property("margin-right", lus[1], imp);
        ph.property("margin-bottom", lus[2], imp);
        ph.property("margin-left", lus[3], imp);
    }
}

