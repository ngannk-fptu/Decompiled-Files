/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.rendering;

import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.TilingPaint;
import org.apache.pdfbox.util.Matrix;

class TilingPaintFactory {
    private final PageDrawer drawer;
    private final Map<TilingPaintParameter, WeakReference<Paint>> weakCache = new WeakHashMap<TilingPaintParameter, WeakReference<Paint>>();

    TilingPaintFactory(PageDrawer drawer) {
        this.drawer = drawer;
    }

    Paint create(PDTilingPattern pattern, PDColorSpace colorSpace, PDColor color, AffineTransform xform) throws IOException {
        Paint paint = null;
        TilingPaintParameter tilingPaintParameter = new TilingPaintParameter(this.drawer.getInitialMatrix(), pattern.getCOSObject(), colorSpace, color, xform);
        WeakReference<Paint> weakRef = this.weakCache.get(tilingPaintParameter);
        if (weakRef != null) {
            paint = (Paint)weakRef.get();
        }
        if (paint == null) {
            paint = new TilingPaint(this.drawer, pattern, colorSpace, color, xform);
            this.weakCache.put(tilingPaintParameter, new WeakReference<Paint>(paint));
        }
        return paint;
    }

    private static class TilingPaintParameter {
        private final Matrix matrix;
        private final COSDictionary patternDict;
        private final PDColorSpace colorSpace;
        private final PDColor color;
        private final AffineTransform xform;

        private TilingPaintParameter(Matrix matrix, COSDictionary patternDict, PDColorSpace colorSpace, PDColor color, AffineTransform xform) {
            this.matrix = matrix.clone();
            this.patternDict = patternDict;
            this.colorSpace = colorSpace;
            this.color = color;
            this.xform = xform;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            TilingPaintParameter other = (TilingPaintParameter)obj;
            if (!(this.matrix == other.matrix || this.matrix != null && this.matrix.equals(other.matrix))) {
                return false;
            }
            if (!(this.patternDict == other.patternDict || this.patternDict != null && this.patternDict.equals(other.patternDict))) {
                return false;
            }
            if (!(this.colorSpace == other.colorSpace || this.colorSpace != null && this.colorSpace.equals(other.colorSpace))) {
                return false;
            }
            if (this.color == null && other.color != null) {
                return false;
            }
            if (this.color != null && other.color == null) {
                return false;
            }
            if (this.color != null && this.color.getColorSpace() != other.color.getColorSpace()) {
                return false;
            }
            try {
                if (this.color != null && other.color != null && this.color != other.color && this.color.toRGB() != other.color.toRGB()) {
                    return false;
                }
            }
            catch (IOException ex) {
                return false;
            }
            return this.xform == other.xform || this.xform != null && this.xform.equals(other.xform);
        }

        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + (this.matrix != null ? this.matrix.hashCode() : 0);
            hash = 23 * hash + (this.patternDict != null ? this.patternDict.hashCode() : 0);
            hash = 23 * hash + (this.colorSpace != null ? this.colorSpace.hashCode() : 0);
            hash = 23 * hash + (this.color != null ? this.color.hashCode() : 0);
            hash = 23 * hash + (this.xform != null ? this.xform.hashCode() : 0);
            return hash;
        }

        public String toString() {
            return "TilingPaintParameter{matrix=" + this.matrix + ", pattern=" + this.patternDict + ", colorSpace=" + this.colorSpace + ", color=" + this.color + ", xform=" + this.xform + '}';
        }
    }
}

