/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt;

import java.awt.RenderingHints;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;

public class AvoidTilingHintKey
extends RenderingHints.Key {
    AvoidTilingHintKey(int number) {
        super(number);
    }

    @Override
    public boolean isCompatibleValue(Object v) {
        if (v == null) {
            return false;
        }
        return v == RenderingHintsKeyExt.VALUE_AVOID_TILE_PAINTING_ON || v == RenderingHintsKeyExt.VALUE_AVOID_TILE_PAINTING_OFF || v == RenderingHintsKeyExt.VALUE_AVOID_TILE_PAINTING_DEFAULT;
    }
}

