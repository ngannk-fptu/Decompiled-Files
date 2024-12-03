/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.draw;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.poi.hemf.draw.HemfDrawProperties;
import org.apache.poi.hemf.record.emf.HemfComment;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecord;
import org.apache.poi.hwmf.draw.HwmfDrawProperties;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfBrushStyle;
import org.apache.poi.hwmf.record.HwmfColorRef;
import org.apache.poi.hwmf.record.HwmfMisc;
import org.apache.poi.hwmf.record.HwmfObjectTableEntry;
import org.apache.poi.hwmf.record.HwmfPenStyle;
import org.apache.poi.util.Internal;

public class HemfGraphics
extends HwmfGraphics {
    private static final HwmfColorRef WHITE = new HwmfColorRef(Color.WHITE);
    private static final HwmfColorRef LTGRAY = new HwmfColorRef(new Color(0xC0C0C0));
    private static final HwmfColorRef GRAY = new HwmfColorRef(new Color(0x808080));
    private static final HwmfColorRef DKGRAY = new HwmfColorRef(new Color(0x404040));
    private static final HwmfColorRef BLACK = new HwmfColorRef(Color.BLACK);
    private EmfRenderState renderState = EmfRenderState.INITIAL;
    private final Map<Integer, HwmfObjectTableEntry> plusObjectTable = new HashMap<Integer, HwmfObjectTableEntry>();
    private final Map<Integer, HemfDrawProperties> plusPropStack = new HashMap<Integer, HemfDrawProperties>();

    public HemfGraphics(Graphics2D graphicsCtx, Rectangle2D bbox) {
        super(graphicsCtx, bbox);
        this.objectIndexes.set(0);
        this.getProperties().setBkMode(HwmfMisc.WmfSetBkMode.HwmfBkMode.TRANSPARENT);
    }

    @Override
    public HemfDrawProperties getProperties() {
        return (HemfDrawProperties)super.getProperties();
    }

    @Override
    protected HemfDrawProperties newProperties(HwmfDrawProperties oldProps) {
        return oldProps == null ? new HemfDrawProperties() : new HemfDrawProperties((HemfDrawProperties)oldProps);
    }

    public EmfRenderState getRenderState() {
        return this.renderState;
    }

    public void setRenderState(EmfRenderState renderState) {
        this.renderState = renderState;
    }

    public void draw(HemfRecord r) {
        switch (this.getRenderState()) {
            default: {
                r.draw(this);
                break;
            }
            case EMF_ONLY: {
                if (r instanceof HemfComment.EmfComment) break;
                r.draw(this);
                break;
            }
            case EMFPLUS_ONLY: {
                if (!(r instanceof HemfComment.EmfComment)) break;
                r.draw(this);
            }
        }
    }

    public void draw(HemfPlusRecord r) {
        r.draw(this);
    }

    @Internal
    public void draw(Consumer<Path2D> pathConsumer, HwmfGraphics.FillDrawStyle fillDraw) {
        Path2D path;
        HemfDrawProperties prop = this.getProperties();
        boolean useBracket = prop.getUsePathBracket();
        if (useBracket) {
            path = prop.getPath();
        } else {
            path = new Path2D.Double();
            path.setWindingRule(prop.getWindingRule());
        }
        if (path.getCurrentPoint() == null) {
            Point2D pnt = prop.getLocation();
            path.moveTo(pnt.getX(), pnt.getY());
        }
        try {
            pathConsumer.accept(path);
        }
        catch (Exception e) {
            Point2D loc = prop.getLocation();
            path.moveTo(loc.getX(), loc.getY());
            pathConsumer.accept(path);
        }
        Point2D curPnt = path.getCurrentPoint();
        if (curPnt == null) {
            return;
        }
        prop.setLocation(curPnt);
        if (!useBracket) {
            switch (fillDraw) {
                case FILL: {
                    super.fill(path);
                    break;
                }
                case DRAW: {
                    super.draw(path);
                    break;
                }
                case FILL_DRAW: {
                    super.fill(path);
                    super.draw(path);
                }
            }
        }
    }

    public void addObjectTableEntry(HwmfObjectTableEntry entry, int index) {
        if (index < 1) {
            throw new IndexOutOfBoundsException("Object table entry index in EMF must be > 0 - invalid index: " + index);
        }
        this.objectIndexes.set(index);
        this.objectTable.put(index, entry);
    }

    public void addPlusObjectTableEntry(HwmfObjectTableEntry entry, int index) {
        if (index < 0 || index > 63) {
            throw new IndexOutOfBoundsException("Object table entry index in EMF+ must be [0..63] - invalid index: " + index);
        }
        this.plusObjectTable.put(index, entry);
    }

    public HwmfObjectTableEntry getObjectTableEntry(int index) {
        if (index < 1) {
            throw new IndexOutOfBoundsException("Object table entry index in EMF must be > 0 - invalid index: " + index);
        }
        return (HwmfObjectTableEntry)this.objectTable.get(index);
    }

    public HwmfObjectTableEntry getPlusObjectTableEntry(int index) {
        if (index < 0 || index > 63) {
            throw new IndexOutOfBoundsException("Object table entry index in EMF+ must be [0..63] - invalid index: " + index);
        }
        return this.plusObjectTable.get(index);
    }

    @Override
    public void applyObjectTableEntry(int index) {
        if ((index & Integer.MIN_VALUE) != 0) {
            this.selectStockObject(index);
        } else {
            HwmfObjectTableEntry ote = (HwmfObjectTableEntry)this.objectTable.get(index);
            if (ote == null) {
                throw new NoSuchElementException("EMF reference exception - object table entry on index " + index + " was deleted before.");
            }
            ote.applyObject(this);
        }
    }

    public void applyPlusObjectTableEntry(int index) {
        if ((index & Integer.MIN_VALUE) != 0) {
            this.selectStockObject(index);
        } else {
            HwmfObjectTableEntry ote = this.plusObjectTable.get(index);
            if (ote == null) {
                throw new NoSuchElementException("EMF+ reference exception - plus object table entry on index " + index + " was deleted before.");
            }
            ote.applyObject(this);
        }
    }

    private void selectStockObject(int objectIndex) {
        HemfDrawProperties prop = this.getProperties();
        switch (objectIndex) {
            case -2147483648: {
                prop.setBrushColor(WHITE);
                prop.setBrushStyle(HwmfBrushStyle.BS_SOLID);
                break;
            }
            case -2147483647: {
                prop.setBrushColor(LTGRAY);
                prop.setBrushStyle(HwmfBrushStyle.BS_SOLID);
                break;
            }
            case -2147483646: {
                prop.setBrushColor(GRAY);
                prop.setBrushStyle(HwmfBrushStyle.BS_SOLID);
                break;
            }
            case -2147483645: {
                prop.setBrushColor(DKGRAY);
                prop.setBrushStyle(HwmfBrushStyle.BS_SOLID);
                break;
            }
            case -2147483644: {
                prop.setBrushColor(BLACK);
                prop.setBrushStyle(HwmfBrushStyle.BS_SOLID);
                break;
            }
            case -2147483643: {
                prop.setBrushStyle(HwmfBrushStyle.BS_NULL);
                break;
            }
            case -2147483642: {
                prop.setPenStyle(HwmfPenStyle.valueOf(0));
                prop.setPenWidth(1.0);
                prop.setPenColor(WHITE);
                break;
            }
            case -2147483641: {
                prop.setPenStyle(HwmfPenStyle.valueOf(0));
                prop.setPenWidth(1.0);
                prop.setPenColor(BLACK);
                break;
            }
            case -2147483640: {
                prop.setPenStyle(HwmfPenStyle.valueOf(HwmfPenStyle.HwmfLineDash.NULL.wmfFlag));
                break;
            }
            case -2147483638: {
                break;
            }
            case -2147483637: {
                break;
            }
            case -2147483636: {
                break;
            }
            case -2147483635: {
                break;
            }
            case -2147483634: {
                break;
            }
            case -2147483633: {
                break;
            }
            case -2147483632: {
                break;
            }
            case -2147483631: {
                break;
            }
            case -2147483630: {
                break;
            }
        }
    }

    @Override
    protected Paint getHatchedFill() {
        return super.getHatchedFill();
    }

    @Override
    public void updateWindowMapMode() {
        super.updateWindowMapMode();
        HemfDrawProperties prop = this.getProperties();
        List<AffineTransform> transXform = prop.getTransXForm();
        List<HemfDrawProperties.TransOperand> transOper = prop.getTransOper();
        assert (transXform.size() == transOper.size());
        AffineTransform tx = this.graphicsCtx.getTransform();
        Iterator<AffineTransform> iter = transXform.iterator();
        transOper.forEach(to -> to.fun.accept(tx, (AffineTransform)iter.next()));
        this.graphicsCtx.setTransform(tx);
    }

    @Override
    public void fill(Shape shape) {
        HemfDrawProperties prop = this.getProperties();
        if (prop.getBrushStyle() == HwmfBrushStyle.BS_NULL) {
            return;
        }
        Composite old = this.graphicsCtx.getComposite();
        this.graphicsCtx.setComposite(AlphaComposite.getInstance(3));
        this.graphicsCtx.setPaint(this.getFill());
        this.graphicsCtx.fill(shape);
        this.graphicsCtx.setComposite(old);
    }

    @Override
    protected Paint getLinearGradient() {
        HemfDrawProperties prop = this.getProperties();
        Rectangle2D rect = prop.getBrushRect();
        List<? extends Map.Entry<Float, Color>> colorsH = prop.getBrushColorsH();
        assert (rect != null && colorsH != null);
        return new LinearGradientPaint(new Point2D.Double(rect.getMinX(), rect.getCenterY()), new Point2D.Double(rect.getMaxX(), rect.getCenterY()), HemfGraphics.toArray(colorsH.stream().map(Map.Entry::getKey), colorsH.size()), (Color[])colorsH.stream().map(Map.Entry::getValue).toArray(Color[]::new), MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, prop.getBrushTransform());
    }

    private static float[] toArray(Stream<? extends Number> numbers, int size) {
        float[] arr = new float[size];
        int[] i = new int[]{0};
        numbers.forEach(n -> {
            int n2 = i[0];
            i[0] = n2 + 1;
            arr[n2] = n.floatValue();
        });
        return arr;
    }

    public void savePlusProperties(int index) {
        HemfDrawProperties p = this.getProperties();
        assert (p != null);
        p.setTransform(this.graphicsCtx.getTransform());
        p.setClip(this.graphicsCtx.getClip());
        this.plusPropStack.put(index, p);
        this.prop = this.newProperties(p);
    }

    public void restorePlusProperties(int index) {
        if (!this.plusPropStack.containsKey(index)) {
            return;
        }
        this.prop = new HemfDrawProperties(this.plusPropStack.get(index));
        this.graphicsCtx.setTransform(this.prop.getTransform());
        this.graphicsCtx.setClip(this.prop.getClip());
    }

    public static enum EmfRenderState {
        INITIAL,
        EMF_ONLY,
        EMFPLUS_ONLY,
        EMF_DCONTEXT;

    }
}

