/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.GlyphOrientationManager;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;

public class GlyphOrientationHorizontalManager
extends GlyphOrientationManager {
    @Override
    public String getPropertyName() {
        return "glyph-orientation-horizontal";
    }

    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.ZERO_DEGREE;
    }
}

