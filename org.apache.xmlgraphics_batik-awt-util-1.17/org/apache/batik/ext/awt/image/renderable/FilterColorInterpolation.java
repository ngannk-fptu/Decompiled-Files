/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.color.ColorSpace;
import org.apache.batik.ext.awt.image.renderable.Filter;

public interface FilterColorInterpolation
extends Filter {
    public boolean isColorSpaceLinear();

    public void setColorSpaceLinear(boolean var1);

    public ColorSpace getOperationColorSpace();
}

