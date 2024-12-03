/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.ColorInterpolationManager;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;

public class ColorInterpolationFiltersManager
extends ColorInterpolationManager {
    @Override
    public String getPropertyName() {
        return "color-interpolation-filters";
    }

    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.LINEARRGB_VALUE;
    }
}

