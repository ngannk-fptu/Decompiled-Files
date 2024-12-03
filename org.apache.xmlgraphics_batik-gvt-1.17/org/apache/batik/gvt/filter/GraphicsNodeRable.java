/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.renderable.Filter
 */
package org.apache.batik.gvt.filter;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;

public interface GraphicsNodeRable
extends Filter {
    public GraphicsNode getGraphicsNode();

    public void setGraphicsNode(GraphicsNode var1);

    public boolean getUsePrimitivePaint();

    public void setUsePrimitivePaint(boolean var1);
}

