/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.blend;

import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;

public abstract class SeparableBlendMode
extends BlendMode {
    SeparableBlendMode() {
    }

    public abstract float blendChannel(float var1, float var2);
}

