/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.renderable.Filter
 */
package org.apache.batik.gvt.filter;

import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;

public interface Mask
extends Filter {
    public Rectangle2D getFilterRegion();

    public void setFilterRegion(Rectangle2D var1);

    public void setSource(Filter var1);

    public Filter getSource();

    public void setMaskNode(GraphicsNode var1);

    public GraphicsNode getMaskNode();
}

