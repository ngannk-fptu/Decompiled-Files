/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.svg;

import java.awt.geom.AffineTransform;
import org.apache.batik.dom.svg.AbstractSVGMatrix;

public class SVGOMMatrix
extends AbstractSVGMatrix {
    protected AffineTransform affineTransform;

    public SVGOMMatrix(AffineTransform at) {
        this.affineTransform = at;
    }

    @Override
    protected AffineTransform getAffineTransform() {
        return this.affineTransform;
    }
}

