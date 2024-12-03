/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt;

import java.awt.RenderingHints;
import java.awt.Shape;

final class AreaOfInterestHintKey
extends RenderingHints.Key {
    AreaOfInterestHintKey(int number) {
        super(number);
    }

    @Override
    public boolean isCompatibleValue(Object val) {
        boolean isCompatible = true;
        if (val != null && !(val instanceof Shape)) {
            isCompatible = false;
        }
        return isCompatible;
    }
}

