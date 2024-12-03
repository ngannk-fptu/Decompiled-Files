/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.extend;

import java.awt.Point;
import org.xhtmlrenderer.layout.LayoutContext;

public interface ReplacedElement {
    public int getIntrinsicWidth();

    public int getIntrinsicHeight();

    public Point getLocation();

    public void setLocation(int var1, int var2);

    public void detach(LayoutContext var1);

    public boolean isRequiresInteractivePaint();

    public boolean hasBaseline();

    public int getBaseline();
}

