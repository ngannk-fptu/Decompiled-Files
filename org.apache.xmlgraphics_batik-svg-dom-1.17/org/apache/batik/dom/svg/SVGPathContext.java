/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.svg;

import java.awt.geom.Point2D;
import org.apache.batik.dom.svg.SVGContext;

public interface SVGPathContext
extends SVGContext {
    public float getTotalLength();

    public Point2D getPointAtLength(float var1);

    public int getPathSegAtLength(float var1);
}

