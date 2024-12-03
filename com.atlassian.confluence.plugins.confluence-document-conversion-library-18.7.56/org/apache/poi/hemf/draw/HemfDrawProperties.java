/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.draw;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.poi.hemf.record.emfplus.HemfPlusBrush;
import org.apache.poi.hwmf.draw.HwmfDrawProperties;
import org.apache.poi.sl.draw.ImageRenderer;

public class HemfDrawProperties
extends HwmfDrawProperties {
    protected Path2D path = null;
    protected boolean usePathBracket = false;
    private HemfPlusBrush.EmfPlusHatchStyle emfPlusBrushHatch;
    private ImageRenderer emfPlusImage;
    private final List<AffineTransform> transXForm = new ArrayList<AffineTransform>();
    private final List<TransOperand> transOper = new ArrayList<TransOperand>();
    private Rectangle2D brushRect;
    private List<? extends Map.Entry<Float, Color>> brushColorsV;
    private List<? extends Map.Entry<Float, Color>> brushColorsH;

    public HemfDrawProperties() {
    }

    public HemfDrawProperties(HemfDrawProperties other) {
        super(other);
        this.path = other.path != null ? (Path2D)other.path.clone() : null;
        this.usePathBracket = other.usePathBracket;
        this.emfPlusBrushHatch = other.emfPlusBrushHatch;
        this.clip = other.clip;
        this.emfPlusImage = other.emfPlusImage;
        this.transXForm.addAll(other.transXForm);
        this.transOper.addAll(other.transOper);
        if (other.brushRect != null) {
            this.brushRect = (Rectangle2D)other.brushRect.clone();
        }
        if (other.brushColorsV != null) {
            this.brushColorsV = new ArrayList<Map.Entry<Float, Color>>(other.brushColorsV);
        }
        if (other.brushColorsH != null) {
            this.brushColorsH = new ArrayList<Map.Entry<Float, Color>>(other.brushColorsH);
        }
    }

    public Path2D getPath() {
        return this.path;
    }

    public void setPath(Path2D path) {
        this.path = path;
    }

    public boolean getUsePathBracket() {
        return this.usePathBracket;
    }

    public void setUsePathBracket(boolean usePathBracket) {
        this.usePathBracket = usePathBracket;
    }

    public HemfPlusBrush.EmfPlusHatchStyle getEmfPlusBrushHatch() {
        return this.emfPlusBrushHatch;
    }

    public void setEmfPlusBrushHatch(HemfPlusBrush.EmfPlusHatchStyle emfPlusBrushHatch) {
        this.emfPlusBrushHatch = emfPlusBrushHatch;
    }

    public ImageRenderer getEmfPlusImage() {
        return this.emfPlusImage;
    }

    public void setEmfPlusImage(ImageRenderer emfPlusImage) {
        this.emfPlusImage = emfPlusImage;
    }

    public void addLeftTransform(AffineTransform transform) {
        this.addLRTransform(transform, TransOperand.left);
    }

    public void addRightTransform(AffineTransform transform) {
        this.addLRTransform(transform, TransOperand.right);
    }

    private static <T> T last(List<T> list) {
        return list.isEmpty() ? null : (T)list.get(list.size() - 1);
    }

    private void addLRTransform(AffineTransform transform, TransOperand lr) {
        if (transform.isIdentity() || transform.equals(HemfDrawProperties.last(this.transXForm)) && lr.equals((Object)HemfDrawProperties.last(this.transOper))) {
            return;
        }
        this.transXForm.add(transform);
        this.transOper.add(lr);
    }

    public void clearTransform() {
        this.transXForm.clear();
        this.transOper.clear();
    }

    List<AffineTransform> getTransXForm() {
        return this.transXForm;
    }

    List<TransOperand> getTransOper() {
        return this.transOper;
    }

    public Rectangle2D getBrushRect() {
        return this.brushRect;
    }

    public void setBrushRect(Rectangle2D brushRect) {
        this.brushRect = brushRect;
    }

    public List<? extends Map.Entry<Float, Color>> getBrushColorsV() {
        return this.brushColorsV;
    }

    public void setBrushColorsV(List<? extends Map.Entry<Float, Color>> brushColorsV) {
        this.brushColorsV = brushColorsV;
    }

    public List<? extends Map.Entry<Float, Color>> getBrushColorsH() {
        return this.brushColorsH;
    }

    public void setBrushColorsH(List<? extends Map.Entry<Float, Color>> brushColorsH) {
        this.brushColorsH = brushColorsH;
    }

    static enum TransOperand {
        left(AffineTransform::concatenate),
        right(AffineTransform::preConcatenate);

        BiConsumer<AffineTransform, AffineTransform> fun;

        private TransOperand(BiConsumer<AffineTransform, AffineTransform> fun) {
            this.fun = fun;
        }
    }
}

