/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.blend;

import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;

public abstract class NonSeparableBlendMode
extends BlendMode {
    NonSeparableBlendMode() {
    }

    public abstract void blend(float[] var1, float[] var2, float[] var3);
}

