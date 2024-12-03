/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation;

import java.io.IOException;
import java.util.Calendar;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationPopup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderEffectDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDExternalDataDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAbstractAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDCaretAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDFileAttachmentAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDFreeTextAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDInkAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDPolygonAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDPolylineAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDSoundAppearanceHandler;

public class PDAnnotationMarkup
extends PDAnnotation {
    private PDAppearanceHandler customAppearanceHandler;
    public static final String SUB_TYPE_FREETEXT = "FreeText";
    public static final String SUB_TYPE_POLYGON = "Polygon";
    public static final String SUB_TYPE_POLYLINE = "PolyLine";
    public static final String SUB_TYPE_CARET = "Caret";
    public static final String SUB_TYPE_INK = "Ink";
    public static final String SUB_TYPE_SOUND = "Sound";
    public static final String IT_FREE_TEXT = "FreeText";
    public static final String IT_FREE_TEXT_CALLOUT = "FreeTextCallout";
    public static final String IT_FREE_TEXT_TYPE_WRITER = "FreeTextTypeWriter";
    public static final String RT_REPLY = "R";
    public static final String RT_GROUP = "Group";

    public PDAnnotationMarkup() {
    }

    public PDAnnotationMarkup(COSDictionary dict) {
        super(dict);
    }

    public String getTitlePopup() {
        return this.getCOSObject().getString(COSName.T);
    }

    public void setTitlePopup(String t) {
        this.getCOSObject().setString(COSName.T, t);
    }

    public PDAnnotationPopup getPopup() {
        COSDictionary popup = (COSDictionary)this.getCOSObject().getDictionaryObject("Popup");
        if (popup != null) {
            return new PDAnnotationPopup(popup);
        }
        return null;
    }

    public void setPopup(PDAnnotationPopup popup) {
        this.getCOSObject().setItem("Popup", (COSObjectable)popup);
    }

    public float getConstantOpacity() {
        return this.getCOSObject().getFloat(COSName.CA, 1.0f);
    }

    public void setConstantOpacity(float ca) {
        this.getCOSObject().setFloat(COSName.CA, ca);
    }

    public String getRichContents() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.RC);
        if (base instanceof COSString) {
            return ((COSString)base).getString();
        }
        if (base instanceof COSStream) {
            return ((COSStream)base).toTextString();
        }
        return null;
    }

    public void setRichContents(String rc) {
        this.getCOSObject().setItem(COSName.RC, (COSBase)new COSString(rc));
    }

    public Calendar getCreationDate() throws IOException {
        return this.getCOSObject().getDate(COSName.CREATION_DATE);
    }

    public void setCreationDate(Calendar creationDate) {
        this.getCOSObject().setDate(COSName.CREATION_DATE, creationDate);
    }

    public PDAnnotation getInReplyTo() throws IOException {
        COSBase base = this.getCOSObject().getDictionaryObject("IRT");
        if (base instanceof COSDictionary) {
            return PDAnnotation.createAnnotation(base);
        }
        return null;
    }

    public void setInReplyTo(PDAnnotation irt) {
        this.getCOSObject().setItem("IRT", (COSObjectable)irt);
    }

    public String getSubject() {
        return this.getCOSObject().getString(COSName.SUBJ);
    }

    public void setSubject(String subj) {
        this.getCOSObject().setString(COSName.SUBJ, subj);
    }

    public String getReplyType() {
        return this.getCOSObject().getNameAsString("RT", RT_REPLY);
    }

    public void setReplyType(String rt) {
        this.getCOSObject().setName("RT", rt);
    }

    public String getIntent() {
        return this.getCOSObject().getNameAsString(COSName.IT);
    }

    public void setIntent(String it) {
        this.getCOSObject().setName(COSName.IT, it);
    }

    public PDExternalDataDictionary getExternalData() {
        COSBase exData = this.getCOSObject().getDictionaryObject("ExData");
        if (exData instanceof COSDictionary) {
            return new PDExternalDataDictionary((COSDictionary)exData);
        }
        return null;
    }

    public void setExternalData(PDExternalDataDictionary externalData) {
        this.getCOSObject().setItem("ExData", (COSObjectable)externalData);
    }

    public void setBorderStyle(PDBorderStyleDictionary bs) {
        this.getCOSObject().setItem(COSName.BS, (COSObjectable)bs);
    }

    public PDBorderStyleDictionary getBorderStyle() {
        COSBase bs = this.getCOSObject().getDictionaryObject(COSName.BS);
        if (bs instanceof COSDictionary) {
            return new PDBorderStyleDictionary((COSDictionary)bs);
        }
        return null;
    }

    public final void setLineEndingStyle(String style) {
        this.getCOSObject().setName(COSName.LE, style);
    }

    public String getLineEndingStyle() {
        return this.getCOSObject().getNameAsString(COSName.LE, "None");
    }

    public void setInteriorColor(PDColor ic) {
        this.getCOSObject().setItem(COSName.IC, (COSBase)ic.toCOSArray());
    }

    public PDColor getInteriorColor() {
        return this.getColor(COSName.IC);
    }

    public void setBorderEffect(PDBorderEffectDictionary be) {
        this.getCOSObject().setItem(COSName.BE, (COSObjectable)be);
    }

    public PDBorderEffectDictionary getBorderEffect() {
        COSDictionary be = (COSDictionary)this.getCOSObject().getDictionaryObject(COSName.BE);
        if (be != null) {
            return new PDBorderEffectDictionary(be);
        }
        return null;
    }

    public void setInkList(float[][] inkList) {
        if (inkList == null) {
            this.getCOSObject().removeItem(COSName.INKLIST);
            return;
        }
        COSArray array = new COSArray();
        for (float[] path : inkList) {
            COSArray innerArray = new COSArray();
            innerArray.setFloatArray(path);
            array.add(innerArray);
        }
        this.getCOSObject().setItem(COSName.INKLIST, (COSBase)array);
    }

    public float[][] getInkList() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.INKLIST);
        if (base instanceof COSArray) {
            COSArray array = (COSArray)base;
            float[][] inkList = new float[array.size()][];
            for (int i = 0; i < array.size(); ++i) {
                COSBase base2 = array.getObject(i);
                inkList[i] = base2 instanceof COSArray ? ((COSArray)base2).toFloatArray() : new float[0];
            }
            return inkList;
        }
        return new float[0][0];
    }

    public String getDefaultAppearance() {
        return this.getCOSObject().getString(COSName.DA);
    }

    public void setDefaultAppearance(String daValue) {
        this.getCOSObject().setString(COSName.DA, daValue);
    }

    public String getDefaultStyleString() {
        return this.getCOSObject().getString(COSName.DS);
    }

    public void setDefaultStyleString(String defaultStyleString) {
        this.getCOSObject().setString(COSName.DS, defaultStyleString);
    }

    public int getQ() {
        return this.getCOSObject().getInt(COSName.Q, 0);
    }

    public void setQ(int q) {
        this.getCOSObject().setInt(COSName.Q, q);
    }

    public void setRectDifference(PDRectangle rd) {
        this.getCOSObject().setItem(COSName.RD, (COSObjectable)rd);
    }

    public PDRectangle getRectDifference() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.RD);
        if (base instanceof COSArray) {
            return new PDRectangle((COSArray)base);
        }
        return null;
    }

    public void setRectDifferences(float difference) {
        this.setRectDifferences(difference, difference, difference, difference);
    }

    public void setRectDifferences(float differenceLeft, float differenceTop, float differenceRight, float differenceBottom) {
        COSArray margins = new COSArray();
        margins.add(new COSFloat(differenceLeft));
        margins.add(new COSFloat(differenceTop));
        margins.add(new COSFloat(differenceRight));
        margins.add(new COSFloat(differenceBottom));
        this.getCOSObject().setItem(COSName.RD, (COSBase)margins);
    }

    public float[] getRectDifferences() {
        COSBase margin = this.getCOSObject().getItem(COSName.RD);
        if (margin instanceof COSArray) {
            return ((COSArray)margin).toFloatArray();
        }
        return new float[0];
    }

    public final void setCallout(float[] callout) {
        COSArray newCallout = new COSArray();
        newCallout.setFloatArray(callout);
        this.getCOSObject().setItem(COSName.CL, (COSBase)newCallout);
    }

    public float[] getCallout() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.CL);
        if (base instanceof COSArray) {
            return ((COSArray)base).toFloatArray();
        }
        return null;
    }

    public void setStartPointEndingStyle(String style) {
        String actualStyle = style == null ? "None" : style;
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.LE);
        if (!(base instanceof COSArray) || ((COSArray)base).size() == 0) {
            COSArray array = new COSArray();
            array.add(COSName.getPDFName(actualStyle));
            array.add(COSName.getPDFName("None"));
            this.getCOSObject().setItem(COSName.LE, (COSBase)array);
        } else {
            COSArray array = (COSArray)base;
            array.setName(0, actualStyle);
        }
    }

    public String getStartPointEndingStyle() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.LE);
        if (base instanceof COSArray && ((COSArray)base).size() >= 2) {
            return ((COSArray)base).getName(0, "None");
        }
        return "None";
    }

    public void setEndPointEndingStyle(String style) {
        String actualStyle = style == null ? "None" : style;
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.LE);
        if (!(base instanceof COSArray) || ((COSArray)base).size() < 2) {
            COSArray array = new COSArray();
            array.add(COSName.getPDFName("None"));
            array.add(COSName.getPDFName(actualStyle));
            this.getCOSObject().setItem(COSName.LE, (COSBase)array);
        } else {
            COSArray array = (COSArray)base;
            array.setName(1, actualStyle);
        }
    }

    public String getEndPointEndingStyle() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.LE);
        if (base instanceof COSArray && ((COSArray)base).size() >= 2) {
            return ((COSArray)base).getName(1, "None");
        }
        return "None";
    }

    public float[] getVertices() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.VERTICES);
        if (base instanceof COSArray) {
            return ((COSArray)base).toFloatArray();
        }
        return null;
    }

    public void setVertices(float[] points) {
        COSArray ar = new COSArray();
        ar.setFloatArray(points);
        this.getCOSObject().setItem(COSName.VERTICES, (COSBase)ar);
    }

    public float[][] getPath() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.PATH);
        if (base instanceof COSArray) {
            COSArray array = (COSArray)base;
            float[][] pathArray = new float[array.size()][];
            for (int i = 0; i < array.size(); ++i) {
                COSBase base2 = array.getObject(i);
                pathArray[i] = base2 instanceof COSArray ? ((COSArray)base2).toFloatArray() : new float[0];
            }
            return pathArray;
        }
        return null;
    }

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
            PDAbstractAppearanceHandler appearanceHandler = null;
            if (SUB_TYPE_CARET.equals(this.getSubtype())) {
                appearanceHandler = new PDCaretAppearanceHandler(this, document);
            } else if ("FreeText".equals(this.getSubtype())) {
                appearanceHandler = new PDFreeTextAppearanceHandler(this, document);
            } else if (SUB_TYPE_INK.equals(this.getSubtype())) {
                appearanceHandler = new PDInkAppearanceHandler(this, document);
            } else if (SUB_TYPE_POLYGON.equals(this.getSubtype())) {
                appearanceHandler = new PDPolygonAppearanceHandler(this, document);
            } else if (SUB_TYPE_POLYLINE.equals(this.getSubtype())) {
                appearanceHandler = new PDPolylineAppearanceHandler(this, document);
            } else if (SUB_TYPE_SOUND.equals(this.getSubtype())) {
                appearanceHandler = new PDSoundAppearanceHandler(this, document);
            } else if ("FileAttachment".equals(this.getSubtype())) {
                appearanceHandler = new PDFileAttachmentAppearanceHandler(this, document);
            }
            if (appearanceHandler != null) {
                appearanceHandler.generateAppearanceStreams();
            }
        } else {
            this.customAppearanceHandler.generateAppearanceStreams();
        }
    }
}

