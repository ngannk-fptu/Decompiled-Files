/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.IdentifierManager;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;

public class PointerEventsManager
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
        return "pointer-events";
    }

    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.VISIBLEPAINTED_VALUE;
    }

    @Override
    public StringMap getIdentifiers() {
        return values;
    }

    static {
        values.put("all", SVGValueConstants.ALL_VALUE);
        values.put("fill", SVGValueConstants.FILL_VALUE);
        values.put("fillstroke", SVGValueConstants.FILLSTROKE_VALUE);
        values.put("none", SVGValueConstants.NONE_VALUE);
        values.put("painted", SVGValueConstants.PAINTED_VALUE);
        values.put("stroke", SVGValueConstants.STROKE_VALUE);
        values.put("visible", SVGValueConstants.VISIBLE_VALUE);
        values.put("visiblefill", SVGValueConstants.VISIBLEFILL_VALUE);
        values.put("visiblefillstroke", SVGValueConstants.VISIBLEFILLSTROKE_VALUE);
        values.put("visiblepainted", SVGValueConstants.VISIBLEPAINTED_VALUE);
        values.put("visiblestroke", SVGValueConstants.VISIBLESTROKE_VALUE);
    }
}

