/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;

public interface FilterChainRable
extends Filter {
    public int getFilterResolutionX();

    public void setFilterResolutionX(int var1);

    public int getFilterResolutionY();

    public void setFilterResolutionY(int var1);

    public void setFilterRegion(Rectangle2D var1);

    public Rectangle2D getFilterRegion();

    public void setSource(Filter var1);

    public Filter getSource();
}

