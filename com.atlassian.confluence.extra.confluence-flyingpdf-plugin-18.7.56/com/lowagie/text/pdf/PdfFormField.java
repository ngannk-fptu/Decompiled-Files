/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfSignature;
import com.lowagie.text.pdf.PdfStamperImp;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.util.ArrayList;
import java.util.List;

public class PdfFormField
extends PdfAnnotation {
    public static final int FF_READ_ONLY = 1;
    public static final int FF_REQUIRED = 2;
    public static final int FF_NO_EXPORT = 4;
    public static final int FF_NO_TOGGLE_TO_OFF = 16384;
    public static final int FF_RADIO = 32768;
    public static final int FF_PUSHBUTTON = 65536;
    public static final int FF_MULTILINE = 4096;
    public static final int FF_PASSWORD = 8192;
    public static final int FF_COMBO = 131072;
    public static final int FF_EDIT = 262144;
    public static final int FF_FILESELECT = 0x100000;
    public static final int FF_MULTISELECT = 0x200000;
    public static final int FF_DONOTSPELLCHECK = 0x400000;
    public static final int FF_DONOTSCROLL = 0x800000;
    public static final int FF_COMB = 0x1000000;
    public static final int FF_RADIOSINUNISON = 0x2000000;
    public static final int Q_LEFT = 0;
    public static final int Q_CENTER = 1;
    public static final int Q_RIGHT = 2;
    public static final int MK_NO_ICON = 0;
    public static final int MK_NO_CAPTION = 1;
    public static final int MK_CAPTION_BELOW = 2;
    public static final int MK_CAPTION_ABOVE = 3;
    public static final int MK_CAPTION_RIGHT = 4;
    public static final int MK_CAPTION_LEFT = 5;
    public static final int MK_CAPTION_OVERLAID = 6;
    public static final PdfName IF_SCALE_ALWAYS = PdfName.A;
    public static final PdfName IF_SCALE_BIGGER = PdfName.B;
    public static final PdfName IF_SCALE_SMALLER = PdfName.S;
    public static final PdfName IF_SCALE_NEVER = PdfName.N;
    public static final PdfName IF_SCALE_ANAMORPHIC = PdfName.A;
    public static final PdfName IF_SCALE_PROPORTIONAL = PdfName.P;
    public static final boolean MULTILINE = true;
    public static final boolean SINGLELINE = false;
    public static final boolean PLAINTEXT = false;
    public static final boolean PASSWORD = true;
    static PdfName[] mergeTarget = new PdfName[]{PdfName.FONT, PdfName.XOBJECT, PdfName.COLORSPACE, PdfName.PATTERN};
    protected PdfFormField parent;
    protected List<PdfFormField> kids;

    public PdfFormField(PdfWriter writer, float llx, float lly, float urx, float ury, PdfAction action) {
        super(writer, llx, lly, urx, ury, action);
        this.put(PdfName.TYPE, PdfName.ANNOT);
        this.put(PdfName.SUBTYPE, PdfName.WIDGET);
        this.annotation = true;
    }

    protected PdfFormField(PdfWriter writer) {
        super(writer, null);
        this.form = true;
        this.annotation = false;
    }

    public void setWidget(Rectangle rect, PdfName highlight) {
        this.put(PdfName.TYPE, PdfName.ANNOT);
        this.put(PdfName.SUBTYPE, PdfName.WIDGET);
        this.put(PdfName.RECT, new PdfRectangle(rect));
        this.annotation = true;
        if (highlight != null && !highlight.equals(HIGHLIGHT_INVERT)) {
            this.put(PdfName.H, highlight);
        }
    }

    public static PdfFormField createEmpty(PdfWriter writer) {
        PdfFormField field = new PdfFormField(writer);
        return field;
    }

    public void setButton(int flags) {
        this.put(PdfName.FT, PdfName.BTN);
        if (flags != 0) {
            this.put(PdfName.FF, new PdfNumber(flags));
        }
    }

    protected static PdfFormField createButton(PdfWriter writer, int flags) {
        PdfFormField field = new PdfFormField(writer);
        field.setButton(flags);
        return field;
    }

    public static PdfFormField createPushButton(PdfWriter writer) {
        return PdfFormField.createButton(writer, 65536);
    }

    public static PdfFormField createCheckBox(PdfWriter writer) {
        return PdfFormField.createButton(writer, 0);
    }

    public static PdfFormField createRadioButton(PdfWriter writer, boolean noToggleToOff) {
        return PdfFormField.createButton(writer, 32768 + (noToggleToOff ? 16384 : 0));
    }

    public static PdfFormField createTextField(PdfWriter writer, boolean multiline, boolean password, int maxLen) {
        PdfFormField field = new PdfFormField(writer);
        field.put(PdfName.FT, PdfName.TX);
        int flags = multiline ? 4096 : 0;
        field.put(PdfName.FF, new PdfNumber(flags += password ? 8192 : 0));
        if (maxLen > 0) {
            field.put(PdfName.MAXLEN, new PdfNumber(maxLen));
        }
        return field;
    }

    protected static PdfFormField createChoice(PdfWriter writer, int flags, PdfArray options, int topIndex) {
        PdfFormField field = new PdfFormField(writer);
        field.put(PdfName.FT, PdfName.CH);
        field.put(PdfName.FF, new PdfNumber(flags));
        field.put(PdfName.OPT, options);
        if (topIndex > 0) {
            field.put(PdfName.TI, new PdfNumber(topIndex));
        }
        return field;
    }

    public static PdfFormField createList(PdfWriter writer, String[] options, int topIndex) {
        return PdfFormField.createChoice(writer, 0, PdfFormField.processOptions(options), topIndex);
    }

    public static PdfFormField createList(PdfWriter writer, String[][] options, int topIndex) {
        return PdfFormField.createChoice(writer, 0, PdfFormField.processOptions(options), topIndex);
    }

    public static PdfFormField createCombo(PdfWriter writer, boolean edit, String[] options, int topIndex) {
        return PdfFormField.createChoice(writer, 131072 + (edit ? 262144 : 0), PdfFormField.processOptions(options), topIndex);
    }

    public static PdfFormField createCombo(PdfWriter writer, boolean edit, String[][] options, int topIndex) {
        return PdfFormField.createChoice(writer, 131072 + (edit ? 262144 : 0), PdfFormField.processOptions(options), topIndex);
    }

    protected static PdfArray processOptions(String[] options) {
        PdfArray array = new PdfArray();
        for (String option : options) {
            array.add(new PdfString(option, "UnicodeBig"));
        }
        return array;
    }

    protected static PdfArray processOptions(String[][] options) {
        PdfArray array = new PdfArray();
        for (String[] subOption : options) {
            PdfArray ar2 = new PdfArray(new PdfString(subOption[0], "UnicodeBig"));
            ar2.add(new PdfString(subOption[1], "UnicodeBig"));
            array.add(ar2);
        }
        return array;
    }

    public static PdfFormField createSignature(PdfWriter writer) {
        PdfFormField field = new PdfFormField(writer);
        field.put(PdfName.FT, PdfName.SIG);
        return field;
    }

    public PdfFormField getParent() {
        return this.parent;
    }

    public void addKid(PdfFormField field) {
        field.parent = this;
        if (this.kids == null) {
            this.kids = new ArrayList<PdfFormField>();
        }
        this.kids.add(field);
    }

    @Deprecated
    public ArrayList getKids() {
        return (ArrayList)this.kids;
    }

    public List<PdfFormField> getKidFields() {
        return this.kids;
    }

    public int setFieldFlags(int flags) {
        PdfNumber obj = (PdfNumber)this.get(PdfName.FF);
        int old = obj == null ? 0 : obj.intValue();
        int v = old | flags;
        this.put(PdfName.FF, new PdfNumber(v));
        return old;
    }

    public void setValueAsString(String s) {
        this.put(PdfName.V, new PdfString(s, "UnicodeBig"));
    }

    public void setValueAsName(String s) {
        this.put(PdfName.V, new PdfName(s));
    }

    public void setValue(PdfSignature sig) {
        this.put(PdfName.V, sig);
    }

    public void setDefaultValueAsString(String s) {
        this.put(PdfName.DV, new PdfString(s, "UnicodeBig"));
    }

    public void setDefaultValueAsName(String s) {
        this.put(PdfName.DV, new PdfName(s));
    }

    public void setFieldName(String s) {
        if (s != null) {
            this.put(PdfName.T, new PdfString(s, "UnicodeBig"));
        }
    }

    public void setUserName(String s) {
        this.put(PdfName.TU, new PdfString(s, "UnicodeBig"));
    }

    public void setMappingName(String s) {
        this.put(PdfName.TM, new PdfString(s, "UnicodeBig"));
    }

    public void setQuadding(int v) {
        this.put(PdfName.Q, new PdfNumber(v));
    }

    static void mergeResources(PdfDictionary result, PdfDictionary source, PdfStamperImp writer) {
        PdfDictionary dic = null;
        PdfDictionary res = null;
        PdfName target = null;
        for (PdfName pdfName : mergeTarget) {
            target = pdfName;
            PdfDictionary pdfDict = source.getAsDict(target);
            dic = pdfDict;
            if (dic == null) continue;
            res = (PdfDictionary)PdfReader.getPdfObject(result.get(target), result);
            if (res == null) {
                res = new PdfDictionary();
            }
            res.mergeDifferent(dic);
            result.put(target, res);
            if (writer == null) continue;
            writer.markUsed(res);
        }
    }

    static void mergeResources(PdfDictionary result, PdfDictionary source) {
        PdfFormField.mergeResources(result, source, null);
    }

    @Override
    public void setUsed() {
        this.used = true;
        if (this.parent != null) {
            this.put(PdfName.PARENT, this.parent.getIndirectReference());
        }
        if (this.kids != null) {
            PdfArray array = new PdfArray();
            for (PdfFormField kid : this.kids) {
                array.add(kid.getIndirectReference());
            }
            this.put(PdfName.KIDS, array);
        }
        if (this.templates == null) {
            return;
        }
        PdfDictionary dic = new PdfDictionary();
        for (PdfTemplate template : this.templates.keySet()) {
            PdfFormField.mergeResources(dic, (PdfDictionary)template.getResources());
        }
        this.put(PdfName.DR, dic);
    }

    public static PdfAnnotation shallowDuplicate(PdfAnnotation annot) {
        PdfAnnotation dup;
        if (annot.isForm()) {
            dup = new PdfFormField(annot.writer);
            PdfFormField dupField = dup;
            PdfFormField srcField = (PdfFormField)annot;
            dupField.parent = srcField.parent;
            dupField.kids = srcField.kids;
        } else {
            dup = new PdfAnnotation(annot.writer, null);
        }
        dup.merge(annot);
        dup.form = annot.form;
        dup.annotation = annot.annotation;
        dup.templates = annot.templates;
        return dup;
    }
}

