/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.codec.wmf;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.codec.wmf.MetaBrush;
import com.lowagie.text.pdf.codec.wmf.MetaFont;
import com.lowagie.text.pdf.codec.wmf.MetaObject;
import com.lowagie.text.pdf.codec.wmf.MetaPen;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MetaState {
    public static final int TA_NOUPDATECP = 0;
    public static final int TA_UPDATECP = 1;
    public static final int TA_LEFT = 0;
    public static final int TA_RIGHT = 2;
    public static final int TA_CENTER = 6;
    public static final int TA_TOP = 0;
    public static final int TA_BOTTOM = 8;
    public static final int TA_BASELINE = 24;
    public static final int TRANSPARENT = 1;
    public static final int OPAQUE = 2;
    public static final int ALTERNATE = 1;
    public static final int WINDING = 2;
    public Stack<MetaState> savedStates;
    public List<Object> MetaObjects;
    public Point currentPoint;
    public MetaPen currentPen;
    public MetaBrush currentBrush;
    public MetaFont currentFont;
    public Color currentBackgroundColor = Color.white;
    public Color currentTextColor = Color.black;
    public int backgroundMode = 2;
    public int polyFillMode = 1;
    public int lineJoin = 1;
    public int textAlign;
    public int offsetWx;
    public int offsetWy;
    public int extentWx;
    public int extentWy;
    public float scalingX;
    public float scalingY;

    public MetaState() {
        this.savedStates = new Stack();
        this.MetaObjects = new ArrayList<Object>();
        this.currentPoint = new Point(0, 0);
        this.currentPen = new MetaPen();
        this.currentBrush = new MetaBrush();
        this.currentFont = new MetaFont();
    }

    public MetaState(MetaState state) {
        this.setMetaState(state);
    }

    public void setMetaState(MetaState state) {
        this.savedStates = state.savedStates;
        this.MetaObjects = state.MetaObjects;
        this.currentPoint = state.currentPoint;
        this.currentPen = state.currentPen;
        this.currentBrush = state.currentBrush;
        this.currentFont = state.currentFont;
        this.currentBackgroundColor = state.currentBackgroundColor;
        this.currentTextColor = state.currentTextColor;
        this.backgroundMode = state.backgroundMode;
        this.polyFillMode = state.polyFillMode;
        this.textAlign = state.textAlign;
        this.lineJoin = state.lineJoin;
        this.offsetWx = state.offsetWx;
        this.offsetWy = state.offsetWy;
        this.extentWx = state.extentWx;
        this.extentWy = state.extentWy;
        this.scalingX = state.scalingX;
        this.scalingY = state.scalingY;
    }

    public void addMetaObject(MetaObject object) {
        for (int k = 0; k < this.MetaObjects.size(); ++k) {
            if (this.MetaObjects.get(k) != null) continue;
            this.MetaObjects.set(k, object);
            return;
        }
        this.MetaObjects.add(object);
    }

    public void selectMetaObject(int index, PdfContentByte cb) {
        MetaObject obj = (MetaObject)this.MetaObjects.get(index);
        if (obj == null) {
            return;
        }
        block0 : switch (obj.getType()) {
            case 2: {
                this.currentBrush = (MetaBrush)obj;
                int style = this.currentBrush.getStyle();
                if (style == 0) {
                    Color color = this.currentBrush.getColor();
                    cb.setColorFill(color);
                    break;
                }
                if (style != 2) break;
                Color color = this.currentBackgroundColor;
                cb.setColorFill(color);
                break;
            }
            case 1: {
                this.currentPen = (MetaPen)obj;
                int style = this.currentPen.getStyle();
                if (style == 5) break;
                Color color = this.currentPen.getColor();
                cb.setColorStroke(color);
                cb.setLineWidth(Math.abs((float)this.currentPen.getPenWidth() * this.scalingX / (float)this.extentWx));
                switch (style) {
                    case 1: {
                        cb.setLineDash(18.0f, 6.0f, 0.0f);
                        break block0;
                    }
                    case 3: {
                        cb.setLiteral("[9 6 3 6]0 d\n");
                        break block0;
                    }
                    case 4: {
                        cb.setLiteral("[9 3 3 3 3 3]0 d\n");
                        break block0;
                    }
                    case 2: {
                        cb.setLineDash(3.0f, 0.0f);
                        break block0;
                    }
                }
                cb.setLineDash(0.0f);
                break;
            }
            case 3: {
                this.currentFont = (MetaFont)obj;
            }
        }
    }

    public void deleteMetaObject(int index) {
        this.MetaObjects.set(index, null);
    }

    public void saveState(PdfContentByte cb) {
        cb.saveState();
        MetaState state = new MetaState(this);
        this.savedStates.push(state);
    }

    public void restoreState(int index, PdfContentByte cb) {
        int pops = index < 0 ? Math.min(-index, this.savedStates.size()) : Math.max(this.savedStates.size() - index, 0);
        if (pops == 0) {
            return;
        }
        MetaState state = null;
        while (pops-- != 0) {
            cb.restoreState();
            state = this.savedStates.pop();
        }
        this.setMetaState(state);
    }

    public void cleanup(PdfContentByte cb) {
        int k = this.savedStates.size();
        while (k-- > 0) {
            cb.restoreState();
        }
    }

    public float transformX(int x) {
        return ((float)x - (float)this.offsetWx) * this.scalingX / (float)this.extentWx;
    }

    public float transformY(int y) {
        return (1.0f - ((float)y - (float)this.offsetWy) / (float)this.extentWy) * this.scalingY;
    }

    public void setScalingX(float scalingX) {
        this.scalingX = scalingX;
    }

    public void setScalingY(float scalingY) {
        this.scalingY = scalingY;
    }

    public void setOffsetWx(int offsetWx) {
        this.offsetWx = offsetWx;
    }

    public void setOffsetWy(int offsetWy) {
        this.offsetWy = offsetWy;
    }

    public void setExtentWx(int extentWx) {
        this.extentWx = extentWx;
    }

    public void setExtentWy(int extentWy) {
        this.extentWy = extentWy;
    }

    public float transformAngle(float angle) {
        float ta = this.scalingY < 0.0f ? -angle : angle;
        return (float)(this.scalingX < 0.0f ? Math.PI - (double)ta : (double)ta);
    }

    public void setCurrentPoint(Point p) {
        this.currentPoint = p;
    }

    public Point getCurrentPoint() {
        return this.currentPoint;
    }

    public MetaBrush getCurrentBrush() {
        return this.currentBrush;
    }

    public MetaPen getCurrentPen() {
        return this.currentPen;
    }

    public MetaFont getCurrentFont() {
        return this.currentFont;
    }

    public Color getCurrentBackgroundColor() {
        return this.currentBackgroundColor;
    }

    public void setCurrentBackgroundColor(Color currentBackgroundColor) {
        this.currentBackgroundColor = currentBackgroundColor;
    }

    public Color getCurrentTextColor() {
        return this.currentTextColor;
    }

    public void setCurrentTextColor(Color currentTextColor) {
        this.currentTextColor = currentTextColor;
    }

    public int getBackgroundMode() {
        return this.backgroundMode;
    }

    public void setBackgroundMode(int backgroundMode) {
        this.backgroundMode = backgroundMode;
    }

    public int getTextAlign() {
        return this.textAlign;
    }

    public void setTextAlign(int textAlign) {
        this.textAlign = textAlign;
    }

    public int getPolyFillMode() {
        return this.polyFillMode;
    }

    public void setPolyFillMode(int polyFillMode) {
        this.polyFillMode = polyFillMode;
    }

    public void setLineJoinRectangle(PdfContentByte cb) {
        if (this.lineJoin != 0) {
            this.lineJoin = 0;
            cb.setLineJoin(0);
        }
    }

    public void setLineJoinPolygon(PdfContentByte cb) {
        if (this.lineJoin == 0) {
            this.lineJoin = 1;
            cb.setLineJoin(1);
        }
    }

    public boolean getLineNeutral() {
        return this.lineJoin == 0;
    }
}

