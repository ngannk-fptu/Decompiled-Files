/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.ref.Reference;

final class BufferedImageHintKey
extends RenderingHints.Key {
    BufferedImageHintKey(int number) {
        super(number);
    }

    @Override
    public boolean isCompatibleValue(Object val) {
        if (val == null) {
            return true;
        }
        if (!(val instanceof Reference)) {
            return false;
        }
        Reference ref = (Reference)val;
        if ((val = ref.get()) == null) {
            return true;
        }
        return val instanceof BufferedImage;
    }
}

