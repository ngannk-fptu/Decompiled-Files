/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg12;

import org.apache.batik.css.engine.value.IdentifierManager;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.svg12.SVG12ValueConstants;

public class TextAlignManager
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
        return "text-align";
    }

    @Override
    public Value getDefaultValue() {
        return ValueConstants.INHERIT_VALUE;
    }

    @Override
    public StringMap getIdentifiers() {
        return values;
    }

    static {
        values.put("start", SVG12ValueConstants.START_VALUE);
        values.put("middle", SVG12ValueConstants.MIDDLE_VALUE);
        values.put("end", SVG12ValueConstants.END_VALUE);
        values.put("full", SVG12ValueConstants.FULL_VALUE);
    }
}

