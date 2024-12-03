/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.image.renderable.Filter;

public interface AffineRable
extends Filter {
    public Filter getSource();

    public void setSource(Filter var1);

    public void setAffine(AffineTransform var1);

    public AffineTransform getAffine();
}

