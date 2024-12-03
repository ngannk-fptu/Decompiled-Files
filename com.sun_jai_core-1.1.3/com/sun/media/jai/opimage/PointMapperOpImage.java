/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.util.Map;
import javax.media.jai.NullOpImage;
import javax.media.jai.PlanarImage;

public class PointMapperOpImage
extends NullOpImage {
    private AffineTransform transform;
    private AffineTransform inverseTransform;

    public PointMapperOpImage(PlanarImage source, Map configuration, AffineTransform transform) throws NoninvertibleTransformException {
        super((RenderedImage)source, null, configuration, 1);
        if (transform == null) {
            throw new IllegalArgumentException("transform == null!");
        }
        this.transform = transform;
        this.inverseTransform = transform.createInverse();
    }

    public Point2D mapDestPoint(Point2D destPt, int sourceIndex) {
        if (sourceIndex != 0) {
            throw new IndexOutOfBoundsException("sourceIndex != 0!");
        }
        return this.inverseTransform.transform(destPt, null);
    }

    public Point2D mapSourcePoint(Point2D sourcePt, int sourceIndex) {
        if (sourceIndex != 0) {
            throw new IndexOutOfBoundsException("sourceIndex != 0!");
        }
        return this.inverseTransform.transform(sourcePt, null);
    }

    public synchronized void dispose() {
        this.getSourceImage(0).dispose();
        super.dispose();
    }
}

