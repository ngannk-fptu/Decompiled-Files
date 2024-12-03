/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.GradientPaint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.util.PublicCloneable;

public class StandardGradientPaintTransformer
implements GradientPaintTransformer,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -8155025776964678320L;
    private GradientPaintTransformType type;

    public StandardGradientPaintTransformer() {
        this(GradientPaintTransformType.VERTICAL);
    }

    public StandardGradientPaintTransformer(GradientPaintTransformType type) {
        if (type == null) {
            throw new IllegalArgumentException("Null 'type' argument.");
        }
        this.type = type;
    }

    public GradientPaintTransformType getType() {
        return this.type;
    }

    public GradientPaint transform(GradientPaint paint, Shape target) {
        GradientPaint result = paint;
        Rectangle2D bounds = target.getBounds2D();
        if (this.type.equals(GradientPaintTransformType.VERTICAL)) {
            result = new GradientPaint((float)bounds.getCenterX(), (float)bounds.getMinY(), paint.getColor1(), (float)bounds.getCenterX(), (float)bounds.getMaxY(), paint.getColor2());
        } else if (this.type.equals(GradientPaintTransformType.HORIZONTAL)) {
            result = new GradientPaint((float)bounds.getMinX(), (float)bounds.getCenterY(), paint.getColor1(), (float)bounds.getMaxX(), (float)bounds.getCenterY(), paint.getColor2());
        } else if (this.type.equals(GradientPaintTransformType.CENTER_HORIZONTAL)) {
            result = new GradientPaint((float)bounds.getCenterX(), (float)bounds.getCenterY(), paint.getColor2(), (float)bounds.getMaxX(), (float)bounds.getCenterY(), paint.getColor1(), true);
        } else if (this.type.equals(GradientPaintTransformType.CENTER_VERTICAL)) {
            result = new GradientPaint((float)bounds.getCenterX(), (float)bounds.getMinY(), paint.getColor1(), (float)bounds.getCenterX(), (float)bounds.getCenterY(), paint.getColor2(), true);
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardGradientPaintTransformer)) {
            return false;
        }
        StandardGradientPaintTransformer that = (StandardGradientPaintTransformer)obj;
        return this.type == that.type;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int hashCode() {
        return this.type != null ? this.type.hashCode() : 0;
    }
}

