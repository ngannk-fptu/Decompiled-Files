/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.Filter;

public interface PadRable
extends Filter {
    public Filter getSource();

    public void setSource(Filter var1);

    public void setPadRect(Rectangle2D var1);

    public Rectangle2D getPadRect();

    public void setPadMode(PadMode var1);

    public PadMode getPadMode();
}

