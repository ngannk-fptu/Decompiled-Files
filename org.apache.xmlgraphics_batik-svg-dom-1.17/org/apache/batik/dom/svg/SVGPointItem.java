/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.dom.svg.SVGMatrix
 *  org.w3c.dom.svg.SVGPoint
 */
package org.apache.batik.dom.svg;

import org.apache.batik.dom.svg.AbstractSVGItem;
import org.apache.batik.dom.svg.SVGOMPoint;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPoint;

public class SVGPointItem
extends AbstractSVGItem
implements SVGPoint {
    protected float x;
    protected float y;

    public SVGPointItem(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    protected String getStringValue() {
        return Float.toString(this.x) + ',' + Float.toString(this.y);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void setX(float x) {
        this.x = x;
        this.resetAttribute();
    }

    public void setY(float y) {
        this.y = y;
        this.resetAttribute();
    }

    public SVGPoint matrixTransform(SVGMatrix matrix) {
        return SVGOMPoint.matrixTransform(this, matrix);
    }
}

