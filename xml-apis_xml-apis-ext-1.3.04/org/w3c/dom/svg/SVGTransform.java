/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGMatrix;

public interface SVGTransform {
    public static final short SVG_TRANSFORM_UNKNOWN = 0;
    public static final short SVG_TRANSFORM_MATRIX = 1;
    public static final short SVG_TRANSFORM_TRANSLATE = 2;
    public static final short SVG_TRANSFORM_SCALE = 3;
    public static final short SVG_TRANSFORM_ROTATE = 4;
    public static final short SVG_TRANSFORM_SKEWX = 5;
    public static final short SVG_TRANSFORM_SKEWY = 6;

    public short getType();

    public SVGMatrix getMatrix();

    public float getAngle();

    public void setMatrix(SVGMatrix var1);

    public void setTranslate(float var1, float var2);

    public void setScale(float var1, float var2);

    public void setRotate(float var1, float var2, float var3);

    public void setSkewX(float var1);

    public void setSkewY(float var1);
}

