/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Shape;
import org.apache.batik.ext.awt.image.renderable.Filter;

public interface ClipRable
extends Filter {
    public void setUseAntialiasedClip(boolean var1);

    public boolean getUseAntialiasedClip();

    public void setSource(Filter var1);

    public Filter getSource();

    public void setClipPath(Shape var1);

    public Shape getClipPath();
}

