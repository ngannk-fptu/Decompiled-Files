/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.dom.svg.SVGMatrix
 *  org.w3c.dom.svg.SVGTransform
 */
package org.apache.batik.dom.svg;

import java.awt.geom.AffineTransform;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGTransform;

public abstract class AbstractSVGTransform
implements SVGTransform {
    protected short type = 0;
    protected AffineTransform affineTransform;
    protected float angle;
    protected float x;
    protected float y;

    protected abstract SVGMatrix createMatrix();

    public void setType(short type) {
        this.type = type;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void assign(AbstractSVGTransform t) {
        this.type = t.type;
        this.affineTransform = t.affineTransform;
        this.angle = t.angle;
        this.x = t.x;
        this.y = t.y;
    }

    public short getType() {
        return this.type;
    }

    public SVGMatrix getMatrix() {
        return this.createMatrix();
    }

    public float getAngle() {
        return this.angle;
    }

    public void setMatrix(SVGMatrix matrix) {
        this.type = 1;
        this.affineTransform = new AffineTransform(matrix.getA(), matrix.getB(), matrix.getC(), matrix.getD(), matrix.getE(), matrix.getF());
    }

    public void setTranslate(float tx, float ty) {
        this.type = (short)2;
        this.affineTransform = AffineTransform.getTranslateInstance(tx, ty);
    }

    public void setScale(float sx, float sy) {
        this.type = (short)3;
        this.affineTransform = AffineTransform.getScaleInstance(sx, sy);
    }

    public void setRotate(float angle, float cx, float cy) {
        this.type = (short)4;
        this.affineTransform = AffineTransform.getRotateInstance(Math.toRadians(angle), cx, cy);
        this.angle = angle;
        this.x = cx;
        this.y = cy;
    }

    public void setSkewX(float angle) {
        this.type = (short)5;
        this.affineTransform = AffineTransform.getShearInstance(Math.tan(Math.toRadians(angle)), 0.0);
        this.angle = angle;
    }

    public void setSkewY(float angle) {
        this.type = (short)6;
        this.affineTransform = AffineTransform.getShearInstance(0.0, Math.tan(Math.toRadians(angle)));
        this.angle = angle;
    }
}

