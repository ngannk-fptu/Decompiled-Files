/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfBorderArray;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfColor;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PdfAnnotation
extends PdfDictionary {
    public static final PdfName HIGHLIGHT_NONE = PdfName.N;
    public static final PdfName HIGHLIGHT_INVERT = PdfName.I;
    public static final PdfName HIGHLIGHT_OUTLINE = PdfName.O;
    public static final PdfName HIGHLIGHT_PUSH = PdfName.P;
    public static final PdfName HIGHLIGHT_TOGGLE = PdfName.T;
    public static final int FLAGS_INVISIBLE = 1;
    public static final int FLAGS_HIDDEN = 2;
    public static final int FLAGS_PRINT = 4;
    public static final int FLAGS_NOZOOM = 8;
    public static final int FLAGS_NOROTATE = 16;
    public static final int FLAGS_NOVIEW = 32;
    public static final int FLAGS_READONLY = 64;
    public static final int FLAGS_LOCKED = 128;
    public static final int FLAGS_TOGGLENOVIEW = 256;
    public static final PdfName APPEARANCE_NORMAL = PdfName.N;
    public static final PdfName APPEARANCE_ROLLOVER = PdfName.R;
    public static final PdfName APPEARANCE_DOWN = PdfName.D;
    public static final PdfName AA_ENTER = PdfName.E;
    public static final PdfName AA_EXIT = PdfName.X;
    public static final PdfName AA_DOWN = PdfName.D;
    public static final PdfName AA_UP = PdfName.U;
    public static final PdfName AA_FOCUS = PdfName.FO;
    public static final PdfName AA_BLUR = PdfName.BL;
    public static final PdfName AA_JS_KEY = PdfName.K;
    public static final PdfName AA_JS_FORMAT = PdfName.F;
    public static final PdfName AA_JS_CHANGE = PdfName.V;
    public static final PdfName AA_JS_OTHER_CHANGE = PdfName.C;
    public static final int MARKUP_HIGHLIGHT = 0;
    public static final int MARKUP_UNDERLINE = 1;
    public static final int MARKUP_STRIKEOUT = 2;
    public static final int MARKUP_SQUIGGLY = 3;
    protected PdfWriter writer;
    protected PdfIndirectReference reference;
    protected Map<PdfTemplate, Object> templates;
    protected boolean form = false;
    protected boolean annotation = true;
    protected boolean used = false;
    private int placeInPage = -1;

    public PdfAnnotation(PdfWriter writer, Rectangle rect) {
        this.writer = writer;
        if (rect != null) {
            this.put(PdfName.RECT, new PdfRectangle(rect));
        }
    }

    public PdfAnnotation(PdfWriter writer, float llx, float lly, float urx, float ury, PdfString title, PdfString content) {
        this.writer = writer;
        this.put(PdfName.SUBTYPE, PdfName.TEXT);
        this.put(PdfName.T, title);
        this.put(PdfName.RECT, new PdfRectangle(llx, lly, urx, ury));
        this.put(PdfName.CONTENTS, content);
    }

    public PdfAnnotation(PdfWriter writer, float llx, float lly, float urx, float ury, PdfAction action) {
        this.writer = writer;
        this.put(PdfName.SUBTYPE, PdfName.LINK);
        this.put(PdfName.RECT, new PdfRectangle(llx, lly, urx, ury));
        this.put(PdfName.A, action);
        this.put(PdfName.BORDER, new PdfBorderArray(0.0f, 0.0f, 0.0f));
        this.put(PdfName.C, new PdfColor(0, 0, 255));
    }

    public static PdfAnnotation createScreen(PdfWriter writer, Rectangle rect, String clipTitle, PdfFileSpecification fs, String mimeType, boolean playOnDisplay) throws IOException {
        PdfAnnotation ann = new PdfAnnotation(writer, rect);
        ann.put(PdfName.SUBTYPE, PdfName.SCREEN);
        ann.put(PdfName.F, new PdfNumber(4));
        ann.put(PdfName.TYPE, PdfName.ANNOT);
        ann.setPage();
        PdfIndirectReference ref = ann.getIndirectReference();
        PdfAction action = PdfAction.rendition(clipTitle, fs, mimeType, ref);
        PdfIndirectReference actionRef = writer.addToBody(action).getIndirectReference();
        if (playOnDisplay) {
            PdfDictionary aa = new PdfDictionary();
            aa.put(new PdfName("PV"), actionRef);
            ann.put(PdfName.AA, aa);
        }
        ann.put(PdfName.A, actionRef);
        return ann;
    }

    public PdfIndirectReference getIndirectReference() {
        if (this.reference == null) {
            this.reference = this.writer.getPdfIndirectReference();
        }
        return this.reference;
    }

    public static PdfAnnotation createText(PdfWriter writer, Rectangle rect, String title, String contents, boolean open, String icon) {
        PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.TEXT);
        if (title != null) {
            annot.put(PdfName.T, new PdfString(title, "UnicodeBig"));
        }
        if (contents != null) {
            annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        }
        if (open) {
            annot.put(PdfName.OPEN, PdfBoolean.PDFTRUE);
        }
        if (icon != null) {
            annot.put(PdfName.NAME, new PdfName(icon));
        }
        return annot;
    }

    protected static PdfAnnotation createLink(PdfWriter writer, Rectangle rect, PdfName highlight) {
        PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.LINK);
        if (!highlight.equals(HIGHLIGHT_INVERT)) {
            annot.put(PdfName.H, highlight);
        }
        return annot;
    }

    public static PdfAnnotation createLink(PdfWriter writer, Rectangle rect, PdfName highlight, PdfAction action) {
        PdfAnnotation annot = PdfAnnotation.createLink(writer, rect, highlight);
        annot.putEx(PdfName.A, action);
        return annot;
    }

    public static PdfAnnotation createLink(PdfWriter writer, Rectangle rect, PdfName highlight, String namedDestination) {
        PdfAnnotation annot = PdfAnnotation.createLink(writer, rect, highlight);
        annot.put(PdfName.DEST, new PdfString(namedDestination));
        return annot;
    }

    public static PdfAnnotation createLink(PdfWriter writer, Rectangle rect, PdfName highlight, int page, PdfDestination dest) {
        PdfAnnotation annot = PdfAnnotation.createLink(writer, rect, highlight);
        PdfIndirectReference ref = writer.getPageReference(page);
        dest.addPage(ref);
        annot.put(PdfName.DEST, dest);
        return annot;
    }

    public static PdfAnnotation createFreeText(PdfWriter writer, Rectangle rect, String contents, PdfContentByte defaultAppearance) {
        PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.FREETEXT);
        annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        annot.setDefaultAppearanceString(defaultAppearance);
        return annot;
    }

    public static PdfAnnotation createLine(PdfWriter writer, Rectangle rect, String contents, float x1, float y1, float x2, float y2) {
        PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.LINE);
        annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        PdfArray array = new PdfArray(new PdfNumber(x1));
        array.add(new PdfNumber(y1));
        array.add(new PdfNumber(x2));
        array.add(new PdfNumber(y2));
        annot.put(PdfName.L, array);
        return annot;
    }

    public static PdfAnnotation createSquareCircle(PdfWriter writer, Rectangle rect, String contents, boolean square) {
        PdfAnnotation annot = new PdfAnnotation(writer, rect);
        if (square) {
            annot.put(PdfName.SUBTYPE, PdfName.SQUARE);
        } else {
            annot.put(PdfName.SUBTYPE, PdfName.CIRCLE);
        }
        annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        return annot;
    }

    public static PdfAnnotation createMarkup(PdfWriter writer, Rectangle rect, String contents, int type, float[] quadPoints) {
        PdfAnnotation annot = new PdfAnnotation(writer, rect);
        PdfName name = PdfName.HIGHLIGHT;
        switch (type) {
            case 1: {
                name = PdfName.UNDERLINE;
                break;
            }
            case 2: {
                name = PdfName.STRIKEOUT;
                break;
            }
            case 3: {
                name = PdfName.SQUIGGLY;
            }
        }
        annot.put(PdfName.SUBTYPE, name);
        annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        PdfArray array = new PdfArray();
        for (float quadPoint : quadPoints) {
            array.add(new PdfNumber(quadPoint));
        }
        annot.put(PdfName.QUADPOINTS, array);
        return annot;
    }

    public static PdfAnnotation createStamp(PdfWriter writer, Rectangle rect, String contents, String name) {
        PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.STAMP);
        annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        annot.put(PdfName.NAME, new PdfName(name));
        return annot;
    }

    public static PdfAnnotation createInk(PdfWriter writer, Rectangle rect, String contents, float[][] inkList) {
        PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.INK);
        annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        PdfArray outer = new PdfArray();
        for (float[] floats : inkList) {
            float[] deep;
            PdfArray inner = new PdfArray();
            for (float v : deep = floats) {
                inner.add(new PdfNumber(v));
            }
            outer.add(inner);
        }
        annot.put(PdfName.INKLIST, outer);
        return annot;
    }

    public static PdfAnnotation createFileAttachment(PdfWriter writer, Rectangle rect, String contents, byte[] fileStore, String file, String fileDisplay) throws IOException {
        return PdfAnnotation.createFileAttachment(writer, rect, contents, PdfFileSpecification.fileEmbedded(writer, file, fileDisplay, fileStore));
    }

    public static PdfAnnotation createFileAttachment(PdfWriter writer, Rectangle rect, String contents, PdfFileSpecification fs) throws IOException {
        PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.FILEATTACHMENT);
        if (contents != null) {
            annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        }
        annot.put(PdfName.FS, fs.getReference());
        return annot;
    }

    public static PdfAnnotation createPopup(PdfWriter writer, Rectangle rect, String contents, boolean open) {
        PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.POPUP);
        if (contents != null) {
            annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        }
        if (open) {
            annot.put(PdfName.OPEN, PdfBoolean.PDFTRUE);
        }
        return annot;
    }

    public void setDefaultAppearanceString(PdfContentByte cb) {
        byte[] b = cb.getInternalBuffer().toByteArray();
        int len = b.length;
        for (int k = 0; k < len; ++k) {
            if (b[k] != 10) continue;
            b[k] = 32;
        }
        this.put(PdfName.DA, new PdfString(b));
    }

    public void setFlags(int flags) {
        if (flags == 0) {
            this.remove(PdfName.F);
        } else {
            this.put(PdfName.F, new PdfNumber(flags));
        }
    }

    public void setBorder(PdfBorderArray border) {
        this.put(PdfName.BORDER, border);
    }

    public void setBorderStyle(PdfBorderDictionary border) {
        this.put(PdfName.BS, border);
    }

    public void setHighlighting(PdfName highlight) {
        if (highlight.equals(HIGHLIGHT_INVERT)) {
            this.remove(PdfName.H);
        } else {
            this.put(PdfName.H, highlight);
        }
    }

    public void setAppearance(PdfName ap, PdfTemplate template) {
        PdfDictionary dic = (PdfDictionary)this.get(PdfName.AP);
        if (dic == null) {
            dic = new PdfDictionary();
        }
        dic.put(ap, template.getIndirectReference());
        this.put(PdfName.AP, dic);
        if (!this.form) {
            return;
        }
        if (this.templates == null) {
            this.templates = new HashMap<PdfTemplate, Object>();
        }
        this.templates.put(template, null);
    }

    public void setAppearance(PdfName ap, String state, PdfTemplate template) {
        PdfObject obj;
        PdfDictionary dicAp = (PdfDictionary)this.get(PdfName.AP);
        if (dicAp == null) {
            dicAp = new PdfDictionary();
        }
        PdfDictionary dic = (obj = dicAp.get(ap)) != null && obj.isDictionary() ? (PdfDictionary)obj : new PdfDictionary();
        dic.put(new PdfName(state), template.getIndirectReference());
        dicAp.put(ap, dic);
        this.put(PdfName.AP, dicAp);
        if (!this.form) {
            return;
        }
        if (this.templates == null) {
            this.templates = new HashMap<PdfTemplate, Object>();
        }
        this.templates.put(template, null);
    }

    public void setAppearanceState(String state) {
        if (state == null) {
            this.remove(PdfName.AS);
            return;
        }
        this.put(PdfName.AS, new PdfName(state));
    }

    public void setColor(Color color) {
        this.put(PdfName.C, new PdfColor(color));
    }

    public void setTitle(String title) {
        if (title == null) {
            this.remove(PdfName.T);
            return;
        }
        this.put(PdfName.T, new PdfString(title, "UnicodeBig"));
    }

    public void setPopup(PdfAnnotation popup) {
        this.put(PdfName.POPUP, popup.getIndirectReference());
        popup.put(PdfName.PARENT, this.getIndirectReference());
    }

    public void setAction(PdfAction action) {
        this.put(PdfName.A, action);
    }

    public void setAdditionalActions(PdfName key, PdfAction action) {
        PdfObject obj = this.get(PdfName.AA);
        PdfDictionary dic = obj != null && obj.isDictionary() ? (PdfDictionary)obj : new PdfDictionary();
        dic.put(key, action);
        this.put(PdfName.AA, dic);
    }

    public boolean isUsed() {
        return this.used;
    }

    public void setUsed() {
        this.used = true;
    }

    public HashMap<PdfTemplate, Object> getTemplates() {
        return (HashMap)this.templates;
    }

    public boolean isForm() {
        return this.form;
    }

    public boolean isAnnotation() {
        return this.annotation;
    }

    public void setPage(int page) {
        this.put(PdfName.P, this.writer.getPageReference(page));
    }

    public void setPage() {
        this.put(PdfName.P, this.writer.getCurrentPage());
    }

    public int getPlaceInPage() {
        return this.placeInPage;
    }

    public void setPlaceInPage(int placeInPage) {
        this.placeInPage = placeInPage;
    }

    public void setRotate(int v) {
        this.put(PdfName.ROTATE, new PdfNumber(v));
    }

    PdfDictionary getMK() {
        PdfDictionary mk = (PdfDictionary)this.get(PdfName.MK);
        if (mk == null) {
            mk = new PdfDictionary();
            this.put(PdfName.MK, mk);
        }
        return mk;
    }

    public void setMKRotation(int rotation) {
        this.getMK().put(PdfName.R, new PdfNumber(rotation));
    }

    public static PdfArray getMKColor(Color color) {
        PdfArray array = new PdfArray();
        int type = ExtendedColor.getType(color);
        switch (type) {
            case 1: {
                array.add(new PdfNumber(((GrayColor)color).getGray()));
                break;
            }
            case 2: {
                CMYKColor cmyk = (CMYKColor)color;
                array.add(new PdfNumber(cmyk.getCyan()));
                array.add(new PdfNumber(cmyk.getMagenta()));
                array.add(new PdfNumber(cmyk.getYellow()));
                array.add(new PdfNumber(cmyk.getBlack()));
                break;
            }
            case 3: 
            case 4: 
            case 5: {
                throw new RuntimeException(MessageLocalization.getComposedMessage("separations.patterns.and.shadings.are.not.allowed.in.mk.dictionary"));
            }
            default: {
                array.add(new PdfNumber((float)color.getRed() / 255.0f));
                array.add(new PdfNumber((float)color.getGreen() / 255.0f));
                array.add(new PdfNumber((float)color.getBlue() / 255.0f));
            }
        }
        return array;
    }

    public void setMKBorderColor(Color color) {
        if (color == null) {
            this.getMK().remove(PdfName.BC);
        } else {
            this.getMK().put(PdfName.BC, PdfAnnotation.getMKColor(color));
        }
    }

    public void setMKBackgroundColor(Color color) {
        if (color == null) {
            this.getMK().remove(PdfName.BG);
        } else {
            this.getMK().put(PdfName.BG, PdfAnnotation.getMKColor(color));
        }
    }

    public void setMKNormalCaption(String caption) {
        this.getMK().put(PdfName.CA, new PdfString(caption, "UnicodeBig"));
    }

    public void setMKRolloverCaption(String caption) {
        this.getMK().put(PdfName.RC, new PdfString(caption, "UnicodeBig"));
    }

    public void setMKAlternateCaption(String caption) {
        this.getMK().put(PdfName.AC, new PdfString(caption, "UnicodeBig"));
    }

    public void setMKNormalIcon(PdfTemplate template) {
        this.getMK().put(PdfName.I, template.getIndirectReference());
    }

    public void setMKRolloverIcon(PdfTemplate template) {
        this.getMK().put(PdfName.RI, template.getIndirectReference());
    }

    public void setMKAlternateIcon(PdfTemplate template) {
        this.getMK().put(PdfName.IX, template.getIndirectReference());
    }

    public void setMKIconFit(PdfName scale, PdfName scalingType, float leftoverLeft, float leftoverBottom, boolean fitInBounds) {
        PdfDictionary dic = new PdfDictionary();
        if (!scale.equals(PdfName.A)) {
            dic.put(PdfName.SW, scale);
        }
        if (!scalingType.equals(PdfName.P)) {
            dic.put(PdfName.S, scalingType);
        }
        if (leftoverLeft != 0.5f || leftoverBottom != 0.5f) {
            PdfArray array = new PdfArray(new PdfNumber(leftoverLeft));
            array.add(new PdfNumber(leftoverBottom));
            dic.put(PdfName.A, array);
        }
        if (fitInBounds) {
            dic.put(PdfName.FB, PdfBoolean.PDFTRUE);
        }
        this.getMK().put(PdfName.IF, dic);
    }

    public void setMKTextPosition(int tp) {
        this.getMK().put(PdfName.TP, new PdfNumber(tp));
    }

    public void setLayer(PdfOCG layer) {
        this.put(PdfName.OC, layer.getRef());
    }

    public void setName(String name) {
        this.put(PdfName.NM, new PdfString(name));
    }

    public static class PdfImportedLink {
        float llx;
        float lly;
        float urx;
        float ury;
        HashMap<PdfName, PdfObject> parameters = new HashMap();
        PdfArray destination;
        int newPage = 0;

        PdfImportedLink(PdfDictionary annotation) {
            this.parameters.putAll(annotation.hashMap);
            try {
                this.destination = (PdfArray)this.parameters.remove(PdfName.DEST);
            }
            catch (ClassCastException ex) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("you.have.to.consolidate.the.named.destinations.of.your.reader"));
            }
            if (this.destination != null) {
                this.destination = new PdfArray(this.destination);
            }
            PdfArray rc = (PdfArray)this.parameters.remove(PdfName.RECT);
            this.llx = rc.getAsNumber(0).floatValue();
            this.lly = rc.getAsNumber(1).floatValue();
            this.urx = rc.getAsNumber(2).floatValue();
            this.ury = rc.getAsNumber(3).floatValue();
        }

        public boolean isInternal() {
            return this.destination != null;
        }

        public int getDestinationPage() {
            if (!this.isInternal()) {
                return 0;
            }
            PdfIndirectReference ref = this.destination.getAsIndirectObject(0);
            PRIndirectReference pr = (PRIndirectReference)ref;
            PdfReader r = pr.getReader();
            for (int i = 1; i <= r.getNumberOfPages(); ++i) {
                PRIndirectReference pp = r.getPageOrigRef(i);
                if (pp.getGeneration() != pr.getGeneration() || pp.getNumber() != pr.getNumber()) continue;
                return i;
            }
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("page.not.found"));
        }

        public void setDestinationPage(int newPage) {
            if (!this.isInternal()) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("cannot.change.destination.of.external.link"));
            }
            this.newPage = newPage;
        }

        public void transformDestination(float a, float b, float c, float d, float e, float f) {
            if (!this.isInternal()) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("cannot.change.destination.of.external.link"));
            }
            if (this.destination.getAsName(1).equals(PdfName.XYZ)) {
                float x = this.destination.getAsNumber(2).floatValue();
                float y = this.destination.getAsNumber(3).floatValue();
                float xx = x * a + y * c + e;
                float yy = x * b + y * d + f;
                this.destination.set(2, new PdfNumber(xx));
                this.destination.set(3, new PdfNumber(yy));
            }
        }

        public void transformRect(float a, float b, float c, float d, float e, float f) {
            float x = this.llx * a + this.lly * c + e;
            float y = this.llx * b + this.lly * d + f;
            this.llx = x;
            this.lly = y;
            x = this.urx * a + this.ury * c + e;
            y = this.urx * b + this.ury * d + f;
            this.urx = x;
            this.ury = y;
        }

        public PdfAnnotation createAnnotation(PdfWriter writer) {
            PdfAnnotation annotation = new PdfAnnotation(writer, new Rectangle(this.llx, this.lly, this.urx, this.ury));
            if (this.newPage != 0) {
                PdfIndirectReference ref = writer.getPageReference(this.newPage);
                this.destination.set(0, ref);
            }
            if (this.destination != null) {
                annotation.put(PdfName.DEST, this.destination);
            }
            annotation.hashMap.putAll(this.parameters);
            return annotation;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder("Imported link: location [");
            buf.append(this.llx);
            buf.append(' ');
            buf.append(this.lly);
            buf.append(' ');
            buf.append(this.urx);
            buf.append(' ');
            buf.append(this.ury);
            buf.append("] destination ");
            buf.append(this.destination);
            buf.append(" parameters ");
            buf.append(this.parameters);
            return buf.toString();
        }
    }
}

