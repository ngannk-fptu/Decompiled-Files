/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt;

import java.awt.RenderingHints;

final class TranscodingHintKey
extends RenderingHints.Key {
    TranscodingHintKey(int number) {
        super(number);
    }

    @Override
    public boolean isCompatibleValue(Object val) {
        boolean isCompatible = true;
        if (val != null && !(val instanceof String)) {
            isCompatible = false;
        }
        return isCompatible;
    }
}

