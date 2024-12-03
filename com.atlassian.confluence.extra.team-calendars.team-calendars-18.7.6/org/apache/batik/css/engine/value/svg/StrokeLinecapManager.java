/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.IdentifierManager;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;

public class StrokeLinecapManager
extends IdentifierManager {
    protected static final StringMap values = new StringMap();

    @Override
    public boolean isInheritedProperty() {
        return true;
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
    public int getPropertyType() {
        return 15;
    }

    @Override
    public String getPropertyName() {
        return "stroke-linecap";
    }

    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.BUTT_VALUE;
    }

    @Override
    public StringMap getIdentifiers() {
        return values;
    }

    static {
        values.put("butt", SVGValueConstants.BUTT_VALUE);
        values.put("round", SVGValueConstants.ROUND_VALUE);
        values.put("square", SVGValueConstants.SQUARE_VALUE);
    }
}

