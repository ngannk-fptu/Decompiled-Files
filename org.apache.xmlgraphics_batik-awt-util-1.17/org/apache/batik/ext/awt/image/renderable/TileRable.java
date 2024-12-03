/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.FilterColorInterpolation;

public interface TileRable
extends FilterColorInterpolation {
    public Rectangle2D getTileRegion();

    public void setTileRegion(Rectangle2D var1);

    public Rectangle2D getTiledRegion();

    public void setTiledRegion(Rectangle2D var1);

    public boolean isOverflow();

    public void setOverflow(boolean var1);

    public void setSource(Filter var1);

    public Filter getSource();
}

