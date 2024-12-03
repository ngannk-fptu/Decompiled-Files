/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.IdentifierManager;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;

public class TextRenderingManager
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
        return "text-rendering";
    }

    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.AUTO_VALUE;
    }

    @Override
    public StringMap getIdentifiers() {
        return values;
    }

    static {
        values.put("auto", SVGValueConstants.AUTO_VALUE);
        values.put("optimizespeed", SVGValueConstants.OPTIMIZESPEED_VALUE);
        values.put("geometricprecision", SVGValueConstants.GEOMETRICPRECISION_VALUE);
        values.put("optimizelegibility", SVGValueConstants.OPTIMIZELEGIBILITY_VALUE);
    }
}

