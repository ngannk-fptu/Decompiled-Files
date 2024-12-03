/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDLineAppearanceHandler;

public class PDAnnotationLine
extends PDAnnotationMarkup {
    private PDAppearanceHandler customAppearanceHandler;
    public static final String IT_LINE_ARROW = "LineArrow";
    public static final String IT_LINE_DIMENSION = "LineDimension";
    public static final String LE_SQUARE = "Square";
    public static final String LE_CIRCLE = "Circle";
    public static final String LE_DIAMOND = "Diamond";
    public static final String LE_OPEN_ARROW = "OpenArrow";
    public static final String LE_CLOSED_ARROW = "ClosedArrow";
    public static final String LE_NONE = "None";
    public static final String LE_BUTT = "Butt";
    public static final String LE_R_OPEN_ARROW = "ROpenArrow";
    public static final String LE_R_CLOSED_ARROW = "RClosedArrow";
    public static final String LE_SLASH = "Slash";
    public static final String SUB_TYPE = "Line";

    public PDAnnotationLine() {
        this.getCOSObject().setName(COSName.SUBTYPE, SUB_TYPE);
        this.setLine(new float[]{0.0f, 0.0f, 0.0f, 0.0f});
    }

    public PDAnnotationLine(COSDictionary field) {
        super(field);
    }

    public void setLine(float[] l) {
        COSArray newL = new COSArray();
        newL.setFloatArray(l);
        this.getCOSObject().setItem(COSName.L, (COSBase)newL);
    }

    public float[] getLine() {
        COSArray l = (COSArray)this.getCOSObject().getDictionaryObject(COSName.L);
        return l.toFloatArray();
    }

    @Override
    public void setStartPointEndingStyle(String style) {
        COSBase base;
        if (style == null) {
            style = LE_NONE;
        }
        if (!((base = this.getCOSObject().getDictionaryObject(COSName.LE)) instanceof COSArray) || ((COSArray)base).size() == 0) {
            COSArray array = new COSArray();
            array.add(COSName.getPDFName(style));
            array.add(COSName.getPDFName(LE_NONE));
            this.getCOSObject().setItem(COSName.LE, (COSBase)array);
        } else {
            COSArray array = (COSArray)base;
            array.setName(0, style);
        }
    }

    @Override
    public String getStartPointEndingStyle() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.LE);
        if (base instanceof COSArray && ((COSArray)base).size() >= 2) {
            return ((COSArray)base).getName(0);
        }
        return LE_NONE;
    }

    @Override
    public void setEndPointEndingStyle(String style) {
        COSBase base;
        if (style == null) {
            style = LE_NONE;
        }
        if (!((base = this.getCOSObject().getDictionaryObject(COSName.LE)) instanceof COSArray) || ((COSArray)base).size() < 2) {
            COSArray array = new COSArray();
            array.add(COSName.getPDFName(LE_NONE));
            array.add(COSName.getPDFName(style));
            this.getCOSObject().setItem(COSName.LE, (COSBase)array);
        } else {
            COSArray array = (COSArray)base;
            array.setName(1, style);
        }
    }

    @Override
    public String getEndPointEndingStyle() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.LE);
        if (base instanceof COSArray && ((COSArray)base).size() >= 2) {
            return ((COSArray)base).getName(1);
        }
        return LE_NONE;
    }

    @Override
    public void setInteriorColor(PDColor ic) {
        this.getCOSObject().setItem(COSName.IC, (COSBase)ic.toCOSArray());
    }

    @Override
    public PDColor getInteriorColor() {
        return this.getColor(COSName.IC);
    }

    public void setCaption(boolean cap) {
        this.getCOSObject().setBoolean(COSName.CAP, cap);
    }

    public boolean getCaption() {
        return this.getCOSObject().getBoolean(COSName.CAP, false);
    }

    @Override
    public void setBorderStyle(PDBorderStyleDictionary bs) {
        this.getCOSObject().setItem(COSName.BS, (COSObjectable)bs);
    }

    @Override
    public PDBorderStyleDictionary getBorderStyle() {
        COSBase bs = this.getCOSObject().getDictionaryObject(COSName.BS);
        if (bs instanceof COSDictionary) {
            return new PDBorderStyleDictionary((COSDictionary)bs);
        }
        return null;
    }

    public float getLeaderLineLength() {
        return this.getCOSObject().getFloat(COSName.LL, 0.0f);
    }

    public void setLeaderLineLength(float leaderLineLength) {
        this.getCOSObject().setFloat(COSName.LL, leaderLineLength);
    }

    public float getLeaderLineExtensionLength() {
        return this.getCOSObject().getFloat(COSName.LLE, 0.0f);
    }

    public void setLeaderLineExtensionLength(float leaderLineExtensionLength) {
        this.getCOSObject().setFloat(COSName.LLE, leaderLineExtensionLength);
    }

    public float getLeaderLineOffsetLength() {
        return this.getCOSObject().getFloat(COSName.LLO, 0.0f);
    }

    public void setLeaderLineOffsetLength(float leaderLineOffsetLength) {
        this.getCOSObject().setFloat(COSName.LLO, leaderLineOffsetLength);
    }

    public String getCaptionPositioning() {
        return this.getCOSObject().getNameAsString(COSName.CP);
    }

    public void setCaptionPositioning(String captionPositioning) {
        this.getCOSObject().setName(COSName.CP, captionPositioning);
    }

    public void setCaptionHorizontalOffset(float offset) {
        COSArray array = (COSArray)this.getCOSObject().getDictionaryObject(COSName.CO);
        if (array == null) {
            array = new COSArray();
            array.setFloatArray(new float[]{offset, 0.0f});
            this.getCOSObject().setItem(COSName.CO, (COSBase)array);
        } else {
            array.set(0, new COSFloat(offset));
        }
    }

    public float getCaptionHorizontalOffset() {
        float retval = 0.0f;
        COSArray array = (COSArray)this.getCOSObject().getDictionaryObject(COSName.CO);
        if (array != null) {
            retval = array.toFloatArray()[0];
        }
        return retval;
    }

    public void setCaptionVerticalOffset(float offset) {
        COSArray array = (COSArray)this.getCOSObject().getDictionaryObject(COSName.CO);
        if (array == null) {
            array = new COSArray();
            array.setFloatArray(new float[]{0.0f, offset});
            this.getCOSObject().setItem(COSName.CO, (COSBase)array);
        } else {
            array.set(1, new COSFloat(offset));
        }
    }

    public float getCaptionVerticalOffset() {
        float retval = 0.0f;
        COSArray array = (COSArray)this.getCOSObject().getDictionaryObject(COSName.CO);
        if (array != null) {
            retval = array.toFloatArray()[1];
        }
        return retval;
    }

    @Override
    public void setCustomAppearanceHandler(PDAppearanceHandler appearanceHandler) {
        this.customAppearanceHandler = appearanceHandler;
    }

    @Override
    public void constructAppearances() {
        this.constructAppearances(null);
    }

    @Override
    public void constructAppearances(PDDocument document) {
        if (this.customAppearanceHandler == null) {
            PDLineAppearanceHandler appearanceHandler = new PDLineAppearanceHandler(this, document);
            appearanceHandler.generateAppearanceStreams();
        } else {
            this.customAppearanceHandler.generateAppearanceStreams();
        }
    }
}

