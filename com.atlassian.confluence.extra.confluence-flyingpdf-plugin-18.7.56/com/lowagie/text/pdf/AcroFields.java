/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BaseField;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.DocumentFont;
import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.FdfReader;
import com.lowagie.text.pdf.FdfWriter;
import com.lowagie.text.pdf.FieldReader;
import com.lowagie.text.pdf.FontDetails;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamperImp;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PushbuttonField;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.TextField;
import com.lowagie.text.pdf.XfaForm;
import com.lowagie.text.pdf.XfdfReader;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Node;

public class AcroFields {
    PdfReader reader;
    PdfWriter writer;
    private Map<String, Item> fields;
    private int topFirst;
    private Map<String, int[]> sigNames;
    private boolean append;
    public static final int DA_FONT = 0;
    public static final int DA_SIZE = 1;
    public static final int DA_COLOR = 2;
    private final Map<Integer, BaseFont> extensionFonts = new HashMap<Integer, BaseFont>();
    private XfaForm xfa;
    public static final int FIELD_TYPE_NONE = 0;
    public static final int FIELD_TYPE_PUSHBUTTON = 1;
    public static final int FIELD_TYPE_CHECKBOX = 2;
    public static final int FIELD_TYPE_RADIOBUTTON = 3;
    public static final int FIELD_TYPE_TEXT = 4;
    public static final int FIELD_TYPE_LIST = 5;
    public static final int FIELD_TYPE_COMBO = 6;
    public static final int FIELD_TYPE_SIGNATURE = 7;
    private boolean lastWasString;
    private boolean generateAppearances = true;
    private Map<String, BaseFont> localFonts = new HashMap<String, BaseFont>();
    private float extraMarginLeft;
    private float extraMarginTop;
    private List<BaseFont> substitutionFonts;
    private static final HashMap<String, String[]> stdFieldFontNames = new HashMap();
    private int totalRevisions;
    private Map<String, BaseField> fieldCache;
    private static final PdfName[] buttonRemove;

    AcroFields(PdfReader reader, PdfWriter writer) {
        this.reader = reader;
        this.writer = writer;
        try {
            this.xfa = new XfaForm(reader);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
        if (writer instanceof PdfStamperImp) {
            this.append = ((PdfStamperImp)writer).isAppend();
        }
        this.fill();
    }

    void fill() {
        this.fields = new HashMap<String, Item>();
        PdfDictionary top = (PdfDictionary)PdfReader.getPdfObjectRelease(this.reader.getCatalog().get(PdfName.ACROFORM));
        if (top == null) {
            return;
        }
        PdfArray arrfds = (PdfArray)PdfReader.getPdfObjectRelease(top.get(PdfName.FIELDS));
        if (arrfds == null || arrfds.size() == 0) {
            return;
        }
        for (int k = 1; k <= this.reader.getNumberOfPages(); ++k) {
            PdfDictionary page = this.reader.getPageNRelease(k);
            PdfArray annots = (PdfArray)PdfReader.getPdfObjectRelease(page.get(PdfName.ANNOTS), page);
            if (annots == null) continue;
            for (int j = 0; j < annots.size(); ++j) {
                Item item;
                PdfDictionary annot = annots.getAsDict(j);
                if (annot == null) {
                    PdfReader.releaseLastXrefPartial(annots.getAsIndirectObject(j));
                    continue;
                }
                if (!PdfName.WIDGET.equals(annot.getAsName(PdfName.SUBTYPE))) {
                    PdfReader.releaseLastXrefPartial(annots.getAsIndirectObject(j));
                    continue;
                }
                PdfDictionary widget = annot;
                PdfDictionary dic = new PdfDictionary();
                dic.putAll(annot);
                String name = "";
                PdfDictionary value = null;
                PdfObject lastV = null;
                while (annot != null) {
                    dic.mergeDifferent(annot);
                    PdfString t = annot.getAsString(PdfName.T);
                    if (t != null) {
                        name = t.toUnicodeString() + "." + name;
                    }
                    if (lastV == null && annot.get(PdfName.V) != null) {
                        lastV = PdfReader.getPdfObjectRelease(annot.get(PdfName.V));
                    }
                    if (value == null && t != null) {
                        value = annot;
                        if (annot.get(PdfName.V) == null && lastV != null) {
                            value.put(PdfName.V, lastV);
                        }
                    }
                    annot = annot.getAsDict(PdfName.PARENT);
                }
                if (name.length() > 0) {
                    name = name.substring(0, name.length() - 1);
                }
                if ((item = this.fields.get(name)) == null) {
                    item = new Item();
                    this.fields.put(name, item);
                }
                if (value == null) {
                    item.addValue(widget);
                } else {
                    item.addValue(value);
                }
                item.addWidget(widget);
                item.addWidgetRef(annots.getAsIndirectObject(j));
                dic.mergeDifferent(top);
                item.addMerged(dic);
                item.addPage(k);
                item.addTabOrder(j);
            }
        }
        PdfNumber sigFlags = top.getAsNumber(PdfName.SIGFLAGS);
        if (sigFlags == null || (sigFlags.intValue() & 1) != 1) {
            return;
        }
        for (int j = 0; j < arrfds.size(); ++j) {
            String name;
            PdfDictionary annot = arrfds.getAsDict(j);
            if (annot == null) {
                PdfReader.releaseLastXrefPartial(arrfds.getAsIndirectObject(j));
                continue;
            }
            if (!PdfName.WIDGET.equals(annot.getAsName(PdfName.SUBTYPE))) {
                PdfReader.releaseLastXrefPartial(arrfds.getAsIndirectObject(j));
                continue;
            }
            PdfArray kids = (PdfArray)PdfReader.getPdfObjectRelease(annot.get(PdfName.KIDS));
            if (kids != null) continue;
            PdfDictionary dic = new PdfDictionary();
            dic.putAll(annot);
            PdfString t = annot.getAsString(PdfName.T);
            if (t == null || this.fields.containsKey(name = t.toUnicodeString())) continue;
            Item item = new Item();
            this.fields.put(name, item);
            item.addValue(dic);
            item.addWidget(dic);
            item.addWidgetRef(arrfds.getAsIndirectObject(j));
            item.addMerged(dic);
            item.addPage(-1);
            item.addTabOrder(-1);
        }
    }

    public String[] getAppearanceStates(String fieldName) {
        Item fd = this.fields.get(fieldName);
        if (fd == null) {
            return null;
        }
        HashMap names = new HashMap();
        PdfDictionary vals = fd.getValue(0);
        PdfString stringOpt = vals.getAsString(PdfName.OPT);
        if (stringOpt != null) {
            names.put(stringOpt.toUnicodeString(), null);
        } else {
            PdfArray arrayOpt = vals.getAsArray(PdfName.OPT);
            if (arrayOpt != null) {
                for (int k = 0; k < arrayOpt.size(); ++k) {
                    PdfString valStr = arrayOpt.getAsString(k);
                    if (valStr == null) continue;
                    names.put(valStr.toUnicodeString(), null);
                }
            }
        }
        for (int k = 0; k < fd.size(); ++k) {
            PdfDictionary dic = fd.getWidget(k);
            if ((dic = dic.getAsDict(PdfName.AP)) == null || (dic = dic.getAsDict(PdfName.N)) == null) continue;
            for (PdfName o : dic.getKeys()) {
                String name = PdfName.decodeName(((Object)o).toString());
                names.put(name, null);
            }
        }
        String[] out = new String[names.size()];
        return names.keySet().toArray(out);
    }

    private String[] getListOption(String fieldName, int idx) {
        Item fd = this.getFieldItem(fieldName);
        if (fd == null) {
            return null;
        }
        PdfArray ar = fd.getMerged(0).getAsArray(PdfName.OPT);
        if (ar == null) {
            return null;
        }
        String[] ret = new String[ar.size()];
        for (int k = 0; k < ar.size(); ++k) {
            PdfObject obj = ar.getDirectObject(k);
            try {
                if (obj.isArray()) {
                    obj = ((PdfArray)obj).getDirectObject(idx);
                }
                if (obj.isString()) {
                    ret[k] = ((PdfString)obj).toUnicodeString();
                    continue;
                }
                ret[k] = obj.toString();
                continue;
            }
            catch (Exception e) {
                ret[k] = "";
            }
        }
        return ret;
    }

    public String[] getListOptionExport(String fieldName) {
        return this.getListOption(fieldName, 0);
    }

    public String[] getListOptionDisplay(String fieldName) {
        return this.getListOption(fieldName, 1);
    }

    public boolean setListOption(String fieldName, String[] exportValues, String[] displayValues) {
        if (exportValues == null && displayValues == null) {
            return false;
        }
        if (exportValues != null && displayValues != null && exportValues.length != displayValues.length) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.export.and.the.display.array.must.have.the.same.size"));
        }
        int ftype = this.getFieldType(fieldName);
        if (ftype != 6 && ftype != 5) {
            return false;
        }
        Item fd = this.fields.get(fieldName);
        String[] sing = null;
        if (exportValues == null && displayValues != null) {
            sing = displayValues;
        } else if (exportValues != null && displayValues == null) {
            sing = exportValues;
        }
        PdfArray opt = new PdfArray();
        if (sing != null) {
            for (String s : sing) {
                opt.add(new PdfString(s, "UnicodeBig"));
            }
        } else {
            for (int k = 0; k < exportValues.length; ++k) {
                PdfArray a = new PdfArray();
                a.add(new PdfString(exportValues[k], "UnicodeBig"));
                a.add(new PdfString(displayValues[k], "UnicodeBig"));
                opt.add(a);
            }
        }
        fd.writeToAll(PdfName.OPT, opt, 5);
        return true;
    }

    public int getFieldType(String fieldName) {
        Item fd = this.getFieldItem(fieldName);
        if (fd == null) {
            return 0;
        }
        PdfDictionary merged = fd.getMerged(0);
        PdfName type = merged.getAsName(PdfName.FT);
        if (type == null) {
            return 0;
        }
        int ff = 0;
        PdfNumber ffo = merged.getAsNumber(PdfName.FF);
        if (ffo != null) {
            ff = ffo.intValue();
        }
        if (PdfName.BTN.equals(type)) {
            if ((ff & 0x10000) != 0) {
                return 1;
            }
            if ((ff & 0x8000) != 0) {
                return 3;
            }
            return 2;
        }
        if (PdfName.TX.equals(type)) {
            return 4;
        }
        if (PdfName.CH.equals(type)) {
            if ((ff & 0x20000) != 0) {
                return 6;
            }
            return 5;
        }
        if (PdfName.SIG.equals(type)) {
            return 7;
        }
        return 0;
    }

    public void exportAsFdf(FdfWriter writer) {
        for (Map.Entry<String, Item> entry : this.fields.entrySet()) {
            Item item = entry.getValue();
            String name = entry.getKey();
            PdfObject v = item.getMerged(0).get(PdfName.V);
            if (v == null) continue;
            String value = this.getField(name);
            if (this.lastWasString) {
                writer.setFieldAsString(name, value);
                continue;
            }
            writer.setFieldAsName(name, value);
        }
    }

    public boolean renameField(String oldName, String newName) {
        int idx2;
        int idx1 = oldName.lastIndexOf(46) + 1;
        if (idx1 != (idx2 = newName.lastIndexOf(46) + 1)) {
            return false;
        }
        if (!oldName.substring(0, idx1).equals(newName.substring(0, idx2))) {
            return false;
        }
        if (this.fields.containsKey(newName)) {
            return false;
        }
        Item item = this.fields.get(oldName);
        if (item == null) {
            return false;
        }
        newName = newName.substring(idx2);
        PdfString ss = new PdfString(newName, "UnicodeBig");
        item.writeToAll(PdfName.T, ss, 5);
        item.markUsed(this, 4);
        this.fields.remove(oldName);
        this.fields.put(newName, item);
        return true;
    }

    public static Object[] splitDAelements(String da) {
        try {
            PRTokeniser tk = new PRTokeniser(PdfEncodings.convertToBytes(da, null));
            ArrayList<String> stack = new ArrayList<String>();
            Object[] ret = new Object[3];
            while (tk.nextToken()) {
                if (tk.getTokenType() == 4) continue;
                if (tk.getTokenType() == 10) {
                    String operator;
                    switch (operator = tk.getStringValue()) {
                        case "Tf": {
                            if (stack.size() < 2) break;
                            ret[0] = stack.get(stack.size() - 2);
                            ret[1] = Float.valueOf(Float.parseFloat((String)stack.get(stack.size() - 1)));
                            break;
                        }
                        case "g": {
                            float gray;
                            if (stack.size() < 1 || (gray = Float.parseFloat((String)stack.get(stack.size() - 1))) == 0.0f) break;
                            ret[2] = new GrayColor(gray);
                            break;
                        }
                        case "rg": {
                            if (stack.size() < 3) break;
                            float red = Float.parseFloat((String)stack.get(stack.size() - 3));
                            float green = Float.parseFloat((String)stack.get(stack.size() - 2));
                            float blue = Float.parseFloat((String)stack.get(stack.size() - 1));
                            ret[2] = new Color(red, green, blue);
                            break;
                        }
                        case "k": {
                            if (stack.size() < 4) break;
                            float cyan = Float.parseFloat((String)stack.get(stack.size() - 4));
                            float magenta = Float.parseFloat((String)stack.get(stack.size() - 3));
                            float yellow = Float.parseFloat((String)stack.get(stack.size() - 2));
                            float black = Float.parseFloat((String)stack.get(stack.size() - 1));
                            ret[2] = new CMYKColor(cyan, magenta, yellow, black);
                        }
                    }
                    stack.clear();
                    continue;
                }
                stack.add(tk.getStringValue());
            }
            return ret;
        }
        catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    public void decodeGenericDictionary(PdfDictionary merged, BaseField tx) throws DocumentException {
        PdfDictionary bs;
        PdfDictionary mk;
        int flags = 0;
        PdfString da = merged.getAsString(PdfName.DA);
        if (da != null) {
            PdfDictionary font;
            Object[] dab = AcroFields.splitDAelements(da.toUnicodeString());
            if (dab[1] != null) {
                tx.setFontSize(((Float)dab[1]).floatValue());
            }
            if (dab[2] != null) {
                tx.setTextColor((Color)dab[2]);
            }
            if (dab[0] != null && (font = merged.getAsDict(PdfName.DR)) != null && (font = font.getAsDict(PdfName.FONT)) != null) {
                PdfObject po = font.get(new PdfName((String)dab[0]));
                if (po != null && po.type() == 10) {
                    PdfDictionary fo;
                    PdfDictionary fd;
                    PRIndirectReference por = (PRIndirectReference)po;
                    DocumentFont bp = new DocumentFont((PRIndirectReference)po);
                    tx.setFont(bp);
                    Integer porkey = por.getNumber();
                    BaseFont porf = this.extensionFonts.get(porkey);
                    if (porf == null && !this.extensionFonts.containsKey(porkey) && (fd = (fo = (PdfDictionary)PdfReader.getPdfObject(po)).getAsDict(PdfName.FONTDESCRIPTOR)) != null) {
                        PRStream prs = (PRStream)PdfReader.getPdfObject(fd.get(PdfName.FONTFILE2));
                        if (prs == null) {
                            prs = (PRStream)PdfReader.getPdfObject(fd.get(PdfName.FONTFILE3));
                        }
                        if (prs == null) {
                            this.extensionFonts.put(porkey, null);
                        } else {
                            try {
                                porf = BaseFont.createFont("font.ttf", "Identity-H", true, false, PdfReader.getStreamBytes(prs), null);
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                            this.extensionFonts.put(porkey, porf);
                        }
                    }
                    if (tx instanceof TextField) {
                        ((TextField)tx).setExtensionFont(porf);
                    }
                } else {
                    BaseFont bf = this.localFonts.get(dab[0]);
                    if (bf == null) {
                        String[] fn = stdFieldFontNames.get(dab[0]);
                        if (fn != null) {
                            try {
                                String enc = "winansi";
                                if (fn.length > 1) {
                                    enc = fn[1];
                                }
                                bf = BaseFont.createFont(fn[0], enc, false);
                                tx.setFont(bf);
                            }
                            catch (Exception exception) {}
                        }
                    } else {
                        tx.setFont(bf);
                    }
                }
            }
        }
        if ((mk = merged.getAsDict(PdfName.MK)) != null) {
            PdfArray ar = mk.getAsArray(PdfName.BC);
            Color border = this.getMKColor(ar);
            tx.setBorderColor(border);
            if (border != null) {
                tx.setBorderWidth(1.0f);
            }
            ar = mk.getAsArray(PdfName.BG);
            tx.setBackgroundColor(this.getMKColor(ar));
            PdfNumber rotation = mk.getAsNumber(PdfName.R);
            if (rotation != null) {
                tx.setRotation(rotation.intValue());
            }
        }
        PdfNumber nfl = merged.getAsNumber(PdfName.F);
        flags = 0;
        tx.setVisibility(2);
        if (nfl != null) {
            flags = nfl.intValue();
            if ((flags & 4) != 0 && (flags & 2) != 0) {
                tx.setVisibility(1);
            } else if ((flags & 4) != 0 && (flags & 0x20) != 0) {
                tx.setVisibility(3);
            } else if ((flags & 4) != 0) {
                tx.setVisibility(0);
            }
        }
        nfl = merged.getAsNumber(PdfName.FF);
        flags = 0;
        if (nfl != null) {
            flags = nfl.intValue();
        }
        tx.setOptions(flags);
        if ((flags & 0x1000000) != 0) {
            PdfNumber maxLen = merged.getAsNumber(PdfName.MAXLEN);
            int len = 0;
            if (maxLen != null) {
                len = maxLen.intValue();
            }
            tx.setMaxCharacterLength(len);
        }
        if ((nfl = merged.getAsNumber(PdfName.Q)) != null) {
            if (nfl.intValue() == 1) {
                tx.setAlignment(1);
            } else if (nfl.intValue() == 2) {
                tx.setAlignment(2);
            }
        }
        if ((bs = merged.getAsDict(PdfName.BS)) != null) {
            PdfName s;
            PdfNumber w = bs.getAsNumber(PdfName.W);
            if (w != null) {
                tx.setBorderWidth(w.floatValue());
            }
            if (PdfName.D.equals(s = bs.getAsName(PdfName.S))) {
                tx.setBorderStyle(1);
            } else if (PdfName.B.equals(s)) {
                tx.setBorderStyle(2);
            } else if (PdfName.I.equals(s)) {
                tx.setBorderStyle(3);
            } else if (PdfName.U.equals(s)) {
                tx.setBorderStyle(4);
            }
        } else {
            PdfArray bd = merged.getAsArray(PdfName.BORDER);
            if (bd != null) {
                if (bd.size() >= 3) {
                    tx.setBorderWidth(bd.getAsNumber(2).floatValue());
                }
                if (bd.size() >= 4) {
                    tx.setBorderStyle(1);
                }
            }
        }
    }

    PdfAppearance getAppearance(PdfDictionary merged, String[] values, String fieldName) throws IOException, DocumentException {
        this.topFirst = 0;
        String text = values.length > 0 ? values[0] : null;
        TextField tx = null;
        if (this.fieldCache == null || !this.fieldCache.containsKey(fieldName)) {
            tx = new TextField(this.writer, null, null);
            tx.setExtraMargin(this.extraMarginLeft, this.extraMarginTop);
            tx.setBorderWidth(0.0f);
            tx.setSubstitutionFontList(this.substitutionFonts);
            this.decodeGenericDictionary(merged, tx);
            PdfArray rect = merged.getAsArray(PdfName.RECT);
            Rectangle box = PdfReader.getNormalizedRectangle(rect);
            if (tx.getRotation() == 90 || tx.getRotation() == 270) {
                box = box.rotate();
            }
            tx.setBox(box);
            if (this.fieldCache != null) {
                this.fieldCache.put(fieldName, tx);
            }
        } else {
            tx = (TextField)this.fieldCache.get(fieldName);
            tx.setWriter(this.writer);
        }
        PdfName fieldType = merged.getAsName(PdfName.FT);
        if (PdfName.TX.equals(fieldType)) {
            if (values.length > 0 && values[0] != null) {
                tx.setText(values[0]);
            }
            return tx.getAppearance();
        }
        if (!PdfName.CH.equals(fieldType)) {
            throw new DocumentException(MessageLocalization.getComposedMessage("an.appearance.was.requested.without.a.variable.text.field"));
        }
        PdfArray opt = merged.getAsArray(PdfName.OPT);
        int flags = 0;
        PdfNumber nfl = merged.getAsNumber(PdfName.FF);
        if (nfl != null) {
            flags = nfl.intValue();
        }
        if ((flags & 0x20000) != 0 && opt == null) {
            tx.setText(text);
            return tx.getAppearance();
        }
        if (opt != null) {
            int k;
            String[] choices = new String[opt.size()];
            String[] choicesExp = new String[opt.size()];
            for (k = 0; k < opt.size(); ++k) {
                PdfObject obj = opt.getPdfObject(k);
                if (obj.isString()) {
                    choices[k] = choicesExp[k] = ((PdfString)obj).toUnicodeString();
                    continue;
                }
                PdfArray a = (PdfArray)obj;
                choicesExp[k] = a.getAsString(0).toUnicodeString();
                choices[k] = a.getAsString(1).toUnicodeString();
            }
            if ((flags & 0x20000) != 0) {
                for (k = 0; k < choices.length; ++k) {
                    if (!text.equals(choicesExp[k])) continue;
                    text = choices[k];
                    break;
                }
                tx.setText(text);
                return tx.getAppearance();
            }
            ArrayList<Integer> indexes = new ArrayList<Integer>();
            block2: for (int k2 = 0; k2 < choicesExp.length; ++k2) {
                for (String val : values) {
                    if (val == null || !val.equals(choicesExp[k2])) continue;
                    indexes.add(k2);
                    continue block2;
                }
            }
            tx.setChoices(choices);
            tx.setChoiceExports(choicesExp);
            tx.setChoiceSelections((List<Integer>)indexes);
        }
        PdfAppearance app = tx.getListAppearance();
        this.topFirst = tx.getTopFirst();
        return app;
    }

    PdfAppearance getAppearance(PdfDictionary merged, String text, String fieldName) throws IOException, DocumentException {
        String[] valueArr = new String[]{text};
        return this.getAppearance(merged, valueArr, fieldName);
    }

    Color getMKColor(PdfArray ar) {
        if (ar == null) {
            return null;
        }
        switch (ar.size()) {
            case 1: {
                return new GrayColor(ar.getAsNumber(0).floatValue());
            }
            case 3: {
                return new Color(ExtendedColor.normalize(ar.getAsNumber(0).floatValue()), ExtendedColor.normalize(ar.getAsNumber(1).floatValue()), ExtendedColor.normalize(ar.getAsNumber(2).floatValue()));
            }
            case 4: {
                return new CMYKColor(ar.getAsNumber(0).floatValue(), ar.getAsNumber(1).floatValue(), ar.getAsNumber(2).floatValue(), ar.getAsNumber(3).floatValue());
            }
        }
        return null;
    }

    public String getField(String name) {
        if (this.xfa.isXfaPresent()) {
            if ((name = this.xfa.findFieldName(name, this)) == null) {
                return null;
            }
            name = XfaForm.Xml2Som.getShortName(name);
            return XfaForm.getNodeText(this.xfa.findDatasetsNode(name));
        }
        Item item = this.fields.get(name);
        if (item == null) {
            return null;
        }
        this.lastWasString = false;
        PdfDictionary mergedDict = item.getMerged(0);
        PdfObject v = PdfReader.getPdfObject(mergedDict.get(PdfName.V));
        if (v == null) {
            return "";
        }
        if (v instanceof PRStream) {
            try {
                byte[] valBytes = PdfReader.getStreamBytes((PRStream)v);
                return new String(valBytes);
            }
            catch (IOException e) {
                throw new ExceptionConverter(e);
            }
        }
        PdfName type = mergedDict.getAsName(PdfName.FT);
        if (PdfName.BTN.equals(type)) {
            PdfNumber ff = mergedDict.getAsNumber(PdfName.FF);
            int flags = 0;
            if (ff != null) {
                flags = ff.intValue();
            }
            if ((flags & 0x10000) != 0) {
                return "";
            }
            String value = "";
            if (v instanceof PdfName) {
                value = PdfName.decodeName(v.toString());
            } else if (v instanceof PdfString) {
                value = ((PdfString)v).toUnicodeString();
            }
            PdfArray opts = item.getValue(0).getAsArray(PdfName.OPT);
            if (opts != null) {
                int idx = 0;
                try {
                    idx = Integer.parseInt(value);
                    PdfString ps = opts.getAsString(idx);
                    value = ps.toUnicodeString();
                    this.lastWasString = true;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            return value;
        }
        if (v instanceof PdfString) {
            this.lastWasString = true;
            return ((PdfString)v).toUnicodeString();
        }
        if (v instanceof PdfName) {
            return PdfName.decodeName(v.toString());
        }
        return "";
    }

    public String[] getListSelection(String name) {
        String s = this.getField(name);
        String[] ret = s == null ? new String[]{} : new String[]{s};
        Item item = this.fields.get(name);
        if (item == null) {
            return ret;
        }
        PdfArray values = item.getMerged(0).getAsArray(PdfName.I);
        if (values == null) {
            return ret;
        }
        ret = new String[values.size()];
        String[] options = this.getListOptionExport(name);
        int idx = 0;
        for (PdfObject pdfObject : values.getElements()) {
            PdfNumber n = (PdfNumber)pdfObject;
            ret[idx++] = options[n.intValue()];
        }
        return ret;
    }

    public boolean setFieldProperty(String field, String name, Object value, int[] inst) {
        if (this.writer == null) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("this.acrofields.instance.is.read.only"));
        }
        try {
            Item item = this.fields.get(field);
            if (item == null) {
                return false;
            }
            InstHit hit = new InstHit(inst);
            if (name.equalsIgnoreCase("textfont")) {
                for (int k = 0; k < item.size(); ++k) {
                    PdfDictionary fonts;
                    if (!hit.isHit(k)) continue;
                    PdfDictionary merged = item.getMerged(k);
                    PdfString da = merged.getAsString(PdfName.DA);
                    PdfDictionary dr = merged.getAsDict(PdfName.DR);
                    if (da == null || dr == null) continue;
                    Object[] dao = AcroFields.splitDAelements(da.toUnicodeString());
                    PdfAppearance cb = new PdfAppearance();
                    if (dao[0] == null) continue;
                    BaseFont bf = (BaseFont)value;
                    PdfName psn = PdfAppearance.stdFieldFontNames.get(bf.getPostscriptFontName());
                    if (psn == null) {
                        psn = new PdfName(bf.getPostscriptFontName());
                    }
                    if ((fonts = dr.getAsDict(PdfName.FONT)) == null) {
                        fonts = new PdfDictionary();
                        dr.put(PdfName.FONT, fonts);
                    }
                    PdfIndirectReference fref = (PdfIndirectReference)fonts.get(psn);
                    PdfDictionary top = this.reader.getCatalog().getAsDict(PdfName.ACROFORM);
                    this.markUsed(top);
                    dr = top.getAsDict(PdfName.DR);
                    if (dr == null) {
                        dr = new PdfDictionary();
                        top.put(PdfName.DR, dr);
                    }
                    this.markUsed(dr);
                    PdfDictionary fontsTop = dr.getAsDict(PdfName.FONT);
                    if (fontsTop == null) {
                        fontsTop = new PdfDictionary();
                        dr.put(PdfName.FONT, fontsTop);
                    }
                    this.markUsed(fontsTop);
                    PdfIndirectReference frefTop = (PdfIndirectReference)fontsTop.get(psn);
                    if (frefTop != null) {
                        if (fref == null) {
                            fonts.put(psn, frefTop);
                        }
                    } else if (fref == null) {
                        FontDetails fd;
                        if (bf.getFontType() == 4) {
                            fd = new FontDetails(null, ((DocumentFont)bf).getIndirectReference(), bf);
                        } else {
                            bf.setSubset(false);
                            fd = this.writer.addSimple(bf);
                            this.localFonts.put(psn.toString().substring(1), bf);
                        }
                        fontsTop.put(psn, fd.getIndirectReference());
                        fonts.put(psn, fd.getIndirectReference());
                    }
                    ByteBuffer buf = cb.getInternalBuffer();
                    buf.append(psn.getBytes()).append(' ').append(((Float)dao[1]).floatValue()).append(" Tf ");
                    if (dao[2] != null) {
                        cb.setColorFill((Color)dao[2]);
                    }
                    PdfString s = new PdfString(cb.toString());
                    item.getMerged(k).put(PdfName.DA, s);
                    item.getWidget(k).put(PdfName.DA, s);
                    this.markUsed(item.getWidget(k));
                }
            } else if (name.equalsIgnoreCase("textcolor")) {
                for (int k = 0; k < item.size(); ++k) {
                    PdfDictionary merged;
                    PdfString da;
                    if (!hit.isHit(k) || (da = (merged = item.getMerged(k)).getAsString(PdfName.DA)) == null) continue;
                    Object[] dao = AcroFields.splitDAelements(da.toUnicodeString());
                    PdfAppearance cb = new PdfAppearance();
                    if (dao[0] == null) continue;
                    ByteBuffer buf = cb.getInternalBuffer();
                    buf.append(new PdfName((String)dao[0]).getBytes()).append(' ').append(((Float)dao[1]).floatValue()).append(" Tf ");
                    cb.setColorFill((Color)value);
                    PdfString s = new PdfString(cb.toString());
                    item.getMerged(k).put(PdfName.DA, s);
                    item.getWidget(k).put(PdfName.DA, s);
                    this.markUsed(item.getWidget(k));
                }
            } else if (name.equalsIgnoreCase("textsize")) {
                for (int k = 0; k < item.size(); ++k) {
                    PdfDictionary merged;
                    PdfString da;
                    if (!hit.isHit(k) || (da = (merged = item.getMerged(k)).getAsString(PdfName.DA)) == null) continue;
                    Object[] dao = AcroFields.splitDAelements(da.toUnicodeString());
                    PdfAppearance cb = new PdfAppearance();
                    if (dao[0] == null) continue;
                    ByteBuffer buf = cb.getInternalBuffer();
                    buf.append(new PdfName((String)dao[0]).getBytes()).append(' ').append(((Float)value).floatValue()).append(" Tf ");
                    if (dao[2] != null) {
                        cb.setColorFill((Color)dao[2]);
                    }
                    PdfString s = new PdfString(cb.toString());
                    item.getMerged(k).put(PdfName.DA, s);
                    item.getWidget(k).put(PdfName.DA, s);
                    this.markUsed(item.getWidget(k));
                }
            } else if (name.equalsIgnoreCase("bgcolor") || name.equalsIgnoreCase("bordercolor")) {
                PdfName dname = name.equalsIgnoreCase("bgcolor") ? PdfName.BG : PdfName.BC;
                for (int k = 0; k < item.size(); ++k) {
                    if (!hit.isHit(k)) continue;
                    PdfDictionary merged = item.getMerged(k);
                    PdfDictionary mk = merged.getAsDict(PdfName.MK);
                    if (mk == null) {
                        if (value == null) {
                            return true;
                        }
                        mk = new PdfDictionary();
                        item.getMerged(k).put(PdfName.MK, mk);
                        item.getWidget(k).put(PdfName.MK, mk);
                        this.markUsed(item.getWidget(k));
                    } else {
                        this.markUsed(mk);
                    }
                    if (value == null) {
                        mk.remove(dname);
                        continue;
                    }
                    mk.put(dname, PdfFormField.getMKColor((Color)value));
                }
            } else {
                return false;
            }
            return true;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public boolean setFieldProperty(String field, String name, int value, int[] inst) {
        if (this.writer == null) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("this.acrofields.instance.is.read.only"));
        }
        Item item = this.fields.get(field);
        if (item == null) {
            return false;
        }
        InstHit hit = new InstHit(inst);
        if (name.equalsIgnoreCase("flags")) {
            PdfNumber num = new PdfNumber(value);
            for (int k = 0; k < item.size(); ++k) {
                if (!hit.isHit(k)) continue;
                item.getMerged(k).put(PdfName.F, num);
                item.getWidget(k).put(PdfName.F, num);
                this.markUsed(item.getWidget(k));
            }
        } else if (name.equalsIgnoreCase("setflags")) {
            for (int k = 0; k < item.size(); ++k) {
                if (!hit.isHit(k)) continue;
                PdfNumber num = item.getWidget(k).getAsNumber(PdfName.F);
                int val = 0;
                if (num != null) {
                    val = num.intValue();
                }
                num = new PdfNumber(val | value);
                item.getMerged(k).put(PdfName.F, num);
                item.getWidget(k).put(PdfName.F, num);
                this.markUsed(item.getWidget(k));
            }
        } else if (name.equalsIgnoreCase("clrflags")) {
            for (int k = 0; k < item.size(); ++k) {
                if (!hit.isHit(k)) continue;
                PdfDictionary widget = item.getWidget(k);
                PdfNumber num = widget.getAsNumber(PdfName.F);
                int val = 0;
                if (num != null) {
                    val = num.intValue();
                }
                num = new PdfNumber(val & ~value);
                item.getMerged(k).put(PdfName.F, num);
                widget.put(PdfName.F, num);
                this.markUsed(widget);
            }
        } else if (name.equalsIgnoreCase("fflags")) {
            PdfNumber num = new PdfNumber(value);
            for (int k = 0; k < item.size(); ++k) {
                if (!hit.isHit(k)) continue;
                item.getMerged(k).put(PdfName.FF, num);
                item.getValue(k).put(PdfName.FF, num);
                this.markUsed(item.getValue(k));
            }
        } else if (name.equalsIgnoreCase("setfflags")) {
            for (int k = 0; k < item.size(); ++k) {
                if (!hit.isHit(k)) continue;
                PdfDictionary valDict = item.getValue(k);
                PdfNumber num = valDict.getAsNumber(PdfName.FF);
                int val = 0;
                if (num != null) {
                    val = num.intValue();
                }
                num = new PdfNumber(val | value);
                item.getMerged(k).put(PdfName.FF, num);
                valDict.put(PdfName.FF, num);
                this.markUsed(valDict);
            }
        } else if (name.equalsIgnoreCase("clrfflags")) {
            for (int k = 0; k < item.size(); ++k) {
                if (!hit.isHit(k)) continue;
                PdfDictionary valDict = item.getValue(k);
                PdfNumber num = valDict.getAsNumber(PdfName.FF);
                int val = 0;
                if (num != null) {
                    val = num.intValue();
                }
                num = new PdfNumber(val & ~value);
                item.getMerged(k).put(PdfName.FF, num);
                valDict.put(PdfName.FF, num);
                this.markUsed(valDict);
            }
        } else {
            return false;
        }
        return true;
    }

    public void mergeXfaData(Node n) throws IOException, DocumentException {
        XfaForm.Xml2SomDatasets data = new XfaForm.Xml2SomDatasets(n);
        for (String name : data.getNamesOrder()) {
            String text = XfaForm.getNodeText(data.getNodesByName().get(name));
            this.setField(name, text);
        }
    }

    public void setFields(FdfReader fdf) throws IOException, DocumentException {
        Map<String, PdfDictionary> fd = fdf.getAllFields();
        for (String f : fd.keySet()) {
            String v = fdf.getFieldValue(f);
            if (v == null) continue;
            this.setField(f, v);
        }
    }

    public void setFields(XfdfReader fieldReader) throws IOException, DocumentException {
        this.setFields((FieldReader)fieldReader);
    }

    public void setFields(FieldReader fieldReader) throws IOException, DocumentException {
        Map<String, String> fd = fieldReader.getAllFields();
        for (String f : fd.keySet()) {
            List<String> list;
            String v = fieldReader.getFieldValue(f);
            if (v != null) {
                this.setField(f, v);
            }
            if ((list = fieldReader.getListValues(f)) == null) continue;
            this.setListSelection(v, list.toArray(new String[0]));
        }
    }

    public boolean regenerateField(String name) throws IOException, DocumentException {
        String value = this.getField(name);
        return this.setField(name, value, value);
    }

    public boolean setField(String name, String value) throws IOException, DocumentException {
        return this.setField(name, value, null);
    }

    public boolean setField(String name, String value, String display) throws IOException, DocumentException {
        Item item;
        if (this.writer == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("this.acrofields.instance.is.read.only"));
        }
        if (this.xfa.isXfaPresent()) {
            if ((name = this.xfa.findFieldName(name, this)) == null) {
                return false;
            }
            String shortName = XfaForm.Xml2Som.getShortName(name);
            Node xn = this.xfa.findDatasetsNode(shortName);
            if (xn == null) {
                xn = this.xfa.getDatasetsSom().insertNode(this.xfa.getDatasetsNode(), shortName);
            }
            this.xfa.setNodeText(xn, value);
        }
        if ((item = this.fields.get(name)) == null) {
            return false;
        }
        PdfDictionary merged = item.getMerged(0);
        PdfName type = merged.getAsName(PdfName.FT);
        if (PdfName.TX.equals(type)) {
            PdfNumber maxLen = merged.getAsNumber(PdfName.MAXLEN);
            int len = 0;
            if (maxLen != null) {
                len = maxLen.intValue();
            }
            if (len > 0) {
                value = value.substring(0, Math.min(len, value.length()));
            }
        }
        if (display == null) {
            display = value;
        }
        if (PdfName.TX.equals(type) || PdfName.CH.equals(type)) {
            PdfString v = new PdfString(value, "UnicodeBig");
            for (int idx = 0; idx < item.size(); ++idx) {
                PdfDictionary valueDic = item.getValue(idx);
                valueDic.put(PdfName.V, v);
                valueDic.remove(PdfName.I);
                this.markUsed(valueDic);
                merged = item.getMerged(idx);
                merged.remove(PdfName.I);
                merged.put(PdfName.V, v);
                PdfDictionary widget = item.getWidget(idx);
                if (this.generateAppearances) {
                    PdfDictionary appDic;
                    PdfAppearance app = this.getAppearance(merged, display, name);
                    if (PdfName.CH.equals(type)) {
                        PdfNumber n = new PdfNumber(this.topFirst);
                        widget.put(PdfName.TI, n);
                        merged.put(PdfName.TI, n);
                    }
                    if ((appDic = widget.getAsDict(PdfName.AP)) == null) {
                        appDic = new PdfDictionary();
                        widget.put(PdfName.AP, appDic);
                        merged.put(PdfName.AP, appDic);
                    }
                    appDic.put(PdfName.N, app.getIndirectReference());
                    this.writer.releaseTemplate(app);
                } else {
                    widget.remove(PdfName.AP);
                    merged.remove(PdfName.AP);
                }
                this.markUsed(widget);
            }
            return true;
        }
        if (PdfName.BTN.equals(type)) {
            int vidx;
            PdfNumber ff = item.getMerged(0).getAsNumber(PdfName.FF);
            int flags = 0;
            if (ff != null) {
                flags = ff.intValue();
            }
            if ((flags & 0x10000) != 0) {
                Image img;
                try {
                    img = Image.getInstance(Base64.getDecoder().decode(value));
                }
                catch (Exception e) {
                    return false;
                }
                PushbuttonField pb = this.getNewPushbuttonFromField(name);
                pb.setImage(img);
                this.replacePushbuttonField(name, pb.getField());
                return true;
            }
            PdfName v = new PdfName(value);
            ArrayList<String> lopt = new ArrayList<String>();
            PdfArray opts = item.getValue(0).getAsArray(PdfName.OPT);
            if (opts != null) {
                for (int k = 0; k < opts.size(); ++k) {
                    PdfString valStr = opts.getAsString(k);
                    if (valStr != null) {
                        lopt.add(valStr.toUnicodeString());
                        continue;
                    }
                    lopt.add(null);
                }
            }
            PdfName vt = (vidx = lopt.indexOf(value)) >= 0 ? new PdfName(String.valueOf(vidx)) : v;
            for (int idx = 0; idx < item.size(); ++idx) {
                merged = item.getMerged(idx);
                PdfDictionary widget = item.getWidget(idx);
                PdfDictionary valDict = item.getValue(idx);
                this.markUsed(item.getValue(idx));
                valDict.put(PdfName.V, vt);
                merged.put(PdfName.V, vt);
                this.markUsed(widget);
                if (this.isInAP(widget, vt)) {
                    merged.put(PdfName.AS, vt);
                    widget.put(PdfName.AS, vt);
                    continue;
                }
                merged.put(PdfName.AS, PdfName.Off);
                widget.put(PdfName.AS, PdfName.Off);
            }
            return true;
        }
        return false;
    }

    public boolean setListSelection(String name, String[] value) throws IOException, DocumentException {
        Item item = this.getFieldItem(name);
        if (item == null) {
            return false;
        }
        PdfDictionary merged = item.getMerged(0);
        PdfName type = merged.getAsName(PdfName.FT);
        if (!PdfName.CH.equals(type)) {
            return false;
        }
        String[] options = this.getListOptionExport(name);
        PdfArray array = new PdfArray();
        block0: for (String s1 : value) {
            for (int j = 0; j < options.length; ++j) {
                if (!options[j].equals(s1)) continue;
                array.add(new PdfNumber(j));
                continue block0;
            }
        }
        item.writeToAll(PdfName.I, array, 5);
        PdfArray vals = new PdfArray();
        for (String s : value) {
            vals.add(new PdfString(s));
        }
        item.writeToAll(PdfName.V, vals, 5);
        PdfAppearance app = this.getAppearance(merged, value, name);
        PdfDictionary apDic = new PdfDictionary();
        apDic.put(PdfName.N, app.getIndirectReference());
        item.writeToAll(PdfName.AP, apDic, 3);
        this.writer.releaseTemplate(app);
        item.markUsed(this, 6);
        return true;
    }

    boolean isInAP(PdfDictionary dic, PdfName check) {
        PdfDictionary appDic = dic.getAsDict(PdfName.AP);
        if (appDic == null) {
            return false;
        }
        PdfDictionary NDic = appDic.getAsDict(PdfName.N);
        return NDic != null && NDic.get(check) != null;
    }

    @Deprecated
    public HashMap getFields() {
        return (HashMap)this.fields;
    }

    public Map<String, Item> getAllFields() {
        return this.fields;
    }

    public Item getFieldItem(String name) {
        if (this.xfa.isXfaPresent() && (name = this.xfa.findFieldName(name, this)) == null) {
            return null;
        }
        return this.fields.get(name);
    }

    public String getTranslatedFieldName(String name) {
        String namex;
        if (this.xfa.isXfaPresent() && (namex = this.xfa.findFieldName(name, this)) != null) {
            name = namex;
        }
        return name;
    }

    public float[] getFieldPositions(String name) {
        Item item = this.getFieldItem(name);
        if (item == null) {
            return null;
        }
        float[] ret = new float[item.size() * 5];
        int ptr = 0;
        for (int k = 0; k < item.size(); ++k) {
            try {
                PdfDictionary wd = item.getWidget(k);
                PdfArray rect = wd.getAsArray(PdfName.RECT);
                if (rect == null) continue;
                Rectangle r = PdfReader.getNormalizedRectangle(rect);
                int page = item.getPage(k);
                int rotation = this.reader.getPageRotation(page);
                ret[ptr++] = page;
                if (rotation != 0) {
                    Rectangle pageSize = this.reader.getPageSize(page);
                    switch (rotation) {
                        case 270: {
                            r = new Rectangle(pageSize.getTop() - r.getBottom(), r.getLeft(), pageSize.getTop() - r.getTop(), r.getRight());
                            break;
                        }
                        case 180: {
                            r = new Rectangle(pageSize.getRight() - r.getLeft(), pageSize.getTop() - r.getBottom(), pageSize.getRight() - r.getRight(), pageSize.getTop() - r.getTop());
                            break;
                        }
                        case 90: {
                            r = new Rectangle(r.getBottom(), pageSize.getRight() - r.getLeft(), r.getTop(), pageSize.getRight() - r.getRight());
                        }
                    }
                    r.normalize();
                }
                ret[ptr++] = r.getLeft();
                ret[ptr++] = r.getBottom();
                ret[ptr++] = r.getRight();
                ret[ptr++] = r.getTop();
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (ptr < ret.length) {
            float[] ret2 = new float[ptr];
            System.arraycopy(ret, 0, ret2, 0, ptr);
            return ret2;
        }
        return ret;
    }

    private int removeRefFromArray(PdfArray array, PdfObject refo) {
        if (refo == null || !refo.isIndirect()) {
            return array.size();
        }
        PdfIndirectReference ref = (PdfIndirectReference)refo;
        for (int j = 0; j < array.size(); ++j) {
            PdfObject obj = array.getPdfObject(j);
            if (!obj.isIndirect() || ((PdfIndirectReference)obj).getNumber() != ref.getNumber()) continue;
            array.remove(j--);
        }
        return array.size();
    }

    public boolean removeFieldsFromPage(int page) {
        if (page < 1) {
            return false;
        }
        String[] names = new String[this.fields.size()];
        this.fields.keySet().toArray(names);
        boolean found = false;
        for (String name : names) {
            boolean fr = this.removeField(name, page);
            found = found || fr;
        }
        return found;
    }

    public boolean removeField(String name, int page) {
        Item item = this.getFieldItem(name);
        if (item == null) {
            return false;
        }
        PdfDictionary acroForm = (PdfDictionary)PdfReader.getPdfObject(this.reader.getCatalog().get(PdfName.ACROFORM), this.reader.getCatalog());
        if (acroForm == null) {
            return false;
        }
        PdfArray arrayf = acroForm.getAsArray(PdfName.FIELDS);
        if (arrayf == null) {
            return false;
        }
        for (int k = 0; k < item.size(); ++k) {
            PdfArray kids;
            int pageV = item.getPage(k);
            if (page != -1 && page != pageV) continue;
            PdfIndirectReference ref = item.getWidgetRef(k);
            PdfDictionary wd = item.getWidget(k);
            PdfDictionary pageDic = this.reader.getPageN(pageV);
            PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
            if (annots != null) {
                if (this.removeRefFromArray(annots, ref) == 0) {
                    pageDic.remove(PdfName.ANNOTS);
                    this.markUsed(pageDic);
                } else {
                    this.markUsed(annots);
                }
            }
            PdfReader.killIndirect(ref);
            PdfIndirectReference kid = ref;
            while ((ref = wd.getAsIndirectObject(PdfName.PARENT)) != null && this.removeRefFromArray(kids = (wd = wd.getAsDict(PdfName.PARENT)).getAsArray(PdfName.KIDS), kid) == 0) {
                kid = ref;
                PdfReader.killIndirect(ref);
            }
            if (ref == null) {
                this.removeRefFromArray(arrayf, kid);
                this.markUsed(arrayf);
            }
            if (page == -1) continue;
            item.remove(k);
            --k;
        }
        if (page == -1 || item.size() == 0) {
            this.fields.remove(name);
        }
        return true;
    }

    public boolean removeField(String name) {
        return this.removeField(name, -1);
    }

    public boolean isGenerateAppearances() {
        return this.generateAppearances;
    }

    public void setGenerateAppearances(boolean generateAppearances) {
        this.generateAppearances = generateAppearances;
        PdfDictionary top = this.reader.getCatalog().getAsDict(PdfName.ACROFORM);
        if (generateAppearances) {
            top.remove(PdfName.NEEDAPPEARANCES);
        } else {
            top.put(PdfName.NEEDAPPEARANCES, PdfBoolean.PDFTRUE);
        }
    }

    @Deprecated
    public ArrayList<String> getSignatureNames() {
        return (ArrayList)this.getSignedFieldNames();
    }

    public List<String> getSignedFieldNames() {
        if (this.sigNames != null) {
            return new ArrayList<String>(this.sigNames.keySet());
        }
        this.sigNames = new HashMap<String, int[]>();
        ArrayList<Object[]> sorter = new ArrayList<Object[]>();
        for (Map.Entry<String, Item> entry : this.fields.entrySet()) {
            int rangeSize;
            PdfArray ro;
            PdfString contents;
            PdfDictionary v;
            Item item = entry.getValue();
            PdfDictionary merged = item.getMerged(0);
            if (!PdfName.SIG.equals(merged.get(PdfName.FT)) || (v = merged.getAsDict(PdfName.V)) == null || (contents = v.getAsString(PdfName.CONTENTS)) == null || (ro = v.getAsArray(PdfName.BYTERANGE)) == null || (rangeSize = ro.size()) < 2) continue;
            int lengthOfSignedBlocks = 0;
            for (int i = rangeSize - 1; i > 0; i -= 2) {
                lengthOfSignedBlocks += ro.getAsNumber(i).intValue();
            }
            int unsignedBlock = contents.getOriginalBytes().length * 2 + 2;
            int length = lengthOfSignedBlocks + unsignedBlock;
            sorter.add(new Object[]{entry.getKey(), new int[]{length, 0}});
        }
        sorter.sort(new SorterComparator());
        if (!sorter.isEmpty()) {
            this.totalRevisions = ((int[])((Object[])sorter.get(sorter.size() - 1))[1])[0] == this.reader.getFileLength() ? sorter.size() : sorter.size() + 1;
            for (int k = 0; k < sorter.size(); ++k) {
                Object[] objs = (Object[])sorter.get(k);
                String name = (String)objs[0];
                int[] p = (int[])objs[1];
                p[1] = k + 1;
                this.sigNames.put(name, p);
            }
        }
        return new ArrayList<String>(this.sigNames.keySet());
    }

    @Deprecated
    public ArrayList getBlankSignatureNames() {
        return (ArrayList)this.getFieldNamesWithBlankSignatures();
    }

    public List<String> getFieldNamesWithBlankSignatures() {
        this.getSignedFieldNames();
        ArrayList<String> sigs = new ArrayList<String>();
        for (Map.Entry<String, Item> entry : this.fields.entrySet()) {
            Item item = entry.getValue();
            PdfDictionary merged = item.getMerged(0);
            if (!PdfName.SIG.equals(merged.getAsName(PdfName.FT)) || this.sigNames.containsKey(entry.getKey())) continue;
            sigs.add(entry.getKey());
        }
        return sigs;
    }

    public PdfDictionary getSignatureDictionary(String name) {
        this.getSignedFieldNames();
        name = this.getTranslatedFieldName(name);
        if (!this.sigNames.containsKey(name)) {
            return null;
        }
        Item item = this.fields.get(name);
        PdfDictionary merged = item.getMerged(0);
        return merged.getAsDict(PdfName.V);
    }

    public boolean signatureCoversWholeDocument(String name) {
        this.getSignedFieldNames();
        name = this.getTranslatedFieldName(name);
        if (!this.sigNames.containsKey(name)) {
            return false;
        }
        return this.sigNames.get(name)[0] == this.reader.getFileLength();
    }

    public PdfPKCS7 verifySignature(String name) {
        return this.verifySignature(name, null);
    }

    public PdfPKCS7 verifySignature(String name, String provider) {
        PdfDictionary v = this.getSignatureDictionary(name);
        if (v == null) {
            return null;
        }
        try {
            PdfObject obj;
            PdfName sub = v.getAsName(PdfName.SUBFILTER);
            PdfString contents = v.getAsString(PdfName.CONTENTS);
            PdfPKCS7 pk = null;
            if (sub.equals(PdfName.ADBE_X509_RSA_SHA1)) {
                PdfString cert = v.getAsString(PdfName.CERT);
                pk = new PdfPKCS7(contents.getOriginalBytes(), cert.getBytes(), provider);
            } else {
                pk = new PdfPKCS7(contents.getOriginalBytes(), provider);
            }
            this.updateByteRange(pk, v);
            PdfString str = v.getAsString(PdfName.M);
            if (str != null) {
                pk.setSignDate(PdfDate.decode(str.toString()));
            }
            if ((obj = PdfReader.getPdfObject(v.get(PdfName.NAME))) != null) {
                if (obj.isString()) {
                    pk.setSignName(((PdfString)obj).toUnicodeString());
                } else if (obj.isName()) {
                    pk.setSignName(PdfName.decodeName(obj.toString()));
                }
            }
            if ((str = v.getAsString(PdfName.REASON)) != null) {
                pk.setReason(str.toUnicodeString());
            }
            if ((str = v.getAsString(PdfName.LOCATION)) != null) {
                pk.setLocation(str.toUnicodeString());
            }
            return pk;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private void updateByteRange(PdfPKCS7 pkcs7, PdfDictionary v) {
        PdfArray b = v.getAsArray(PdfName.BYTERANGE);
        RandomAccessFileOrArray rf = this.reader.getSafeFile();
        try {
            rf.reOpen();
            byte[] buf = new byte[8192];
            for (int k = 0; k < b.size(); ++k) {
                int rd;
                int start = b.getAsNumber(k).intValue();
                rf.seek(start);
                for (int length = b.getAsNumber(++k).intValue(); length > 0 && (rd = rf.read(buf, 0, Math.min(length, buf.length))) > 0; length -= rd) {
                    pkcs7.update(buf, 0, rd);
                }
            }
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
        finally {
            try {
                rf.close();
            }
            catch (Exception exception) {}
        }
    }

    private void markUsed(PdfObject obj) {
        if (!this.append) {
            return;
        }
        ((PdfStamperImp)this.writer).markUsed(obj);
    }

    public int getTotalRevisions() {
        this.getSignedFieldNames();
        return this.totalRevisions;
    }

    public int getRevision(String field) {
        this.getSignedFieldNames();
        field = this.getTranslatedFieldName(field);
        if (!this.sigNames.containsKey(field)) {
            return 0;
        }
        return this.sigNames.get(field)[1];
    }

    public InputStream extractRevision(String field) throws IOException {
        this.getSignedFieldNames();
        field = this.getTranslatedFieldName(field);
        if (!this.sigNames.containsKey(field)) {
            return null;
        }
        int length = this.sigNames.get(field)[0];
        RandomAccessFileOrArray raf = this.reader.getSafeFile();
        raf.reOpen();
        raf.seek(0);
        return new RevisionStream(raf, length);
    }

    @Deprecated
    public Map getFieldCache() {
        return this.fieldCache;
    }

    public Map<String, BaseField> getFieldCacheMap() {
        return this.fieldCache;
    }

    @Deprecated
    public void setFieldCache(Map fieldCache) {
        this.fieldCache = fieldCache;
    }

    public void setFieldCacheMap(Map<String, BaseField> fieldCache) {
        this.fieldCache = fieldCache;
    }

    public void setExtraMargin(float extraMarginLeft, float extraMarginTop) {
        this.extraMarginLeft = extraMarginLeft;
        this.extraMarginTop = extraMarginTop;
    }

    public void addSubstitutionFont(BaseFont font) {
        if (this.substitutionFonts == null) {
            this.substitutionFonts = new ArrayList<BaseFont>();
        }
        this.substitutionFonts.add(font);
    }

    @Deprecated
    public ArrayList getSubstitutionFonts() {
        return (ArrayList)this.substitutionFonts;
    }

    public List<BaseFont> getAllSubstitutionFonts() {
        return this.substitutionFonts;
    }

    @Deprecated
    public void setSubstitutionFonts(ArrayList substitutionFonts) {
        this.substitutionFonts = substitutionFonts;
    }

    public void setAllSubstitutionFonts(List<BaseFont> substitutionFonts) {
        this.substitutionFonts = substitutionFonts;
    }

    public XfaForm getXfa() {
        return this.xfa;
    }

    public PushbuttonField getNewPushbuttonFromField(String field) {
        return this.getNewPushbuttonFromField(field, 0);
    }

    public PushbuttonField getNewPushbuttonFromField(String field, int order) {
        try {
            if (this.getFieldType(field) != 1) {
                return null;
            }
            Item item = this.getFieldItem(field);
            if (order >= item.size()) {
                return null;
            }
            int posi = order * 5;
            float[] pos = this.getFieldPositions(field);
            Rectangle box = new Rectangle(pos[posi + 1], pos[posi + 2], pos[posi + 3], pos[posi + 4]);
            PushbuttonField newButton = new PushbuttonField(this.writer, box, null);
            PdfDictionary dic = item.getMerged(order);
            this.decodeGenericDictionary(dic, newButton);
            PdfDictionary mk = dic.getAsDict(PdfName.MK);
            if (mk != null) {
                PdfObject i;
                PdfDictionary ifit;
                PdfNumber tp;
                PdfString text = mk.getAsString(PdfName.CA);
                if (text != null) {
                    newButton.setText(text.toUnicodeString());
                }
                if ((tp = mk.getAsNumber(PdfName.TP)) != null) {
                    newButton.setLayout(tp.intValue() + 1);
                }
                if ((ifit = mk.getAsDict(PdfName.IF)) != null) {
                    PdfBoolean fb;
                    PdfArray aj;
                    PdfName sw = ifit.getAsName(PdfName.SW);
                    if (sw != null) {
                        int scale = 1;
                        if (sw.equals(PdfName.B)) {
                            scale = 3;
                        } else if (sw.equals(PdfName.S)) {
                            scale = 4;
                        } else if (sw.equals(PdfName.N)) {
                            scale = 2;
                        }
                        newButton.setScaleIcon(scale);
                    }
                    if ((sw = ifit.getAsName(PdfName.S)) != null && sw.equals(PdfName.A)) {
                        newButton.setProportionalIcon(false);
                    }
                    if ((aj = ifit.getAsArray(PdfName.A)) != null && aj.size() == 2) {
                        float left = aj.getAsNumber(0).floatValue();
                        float bottom = aj.getAsNumber(1).floatValue();
                        newButton.setIconHorizontalAdjustment(left);
                        newButton.setIconVerticalAdjustment(bottom);
                    }
                    if ((fb = ifit.getAsBoolean(PdfName.FB)) != null && fb.booleanValue()) {
                        newButton.setIconFitToBounds(true);
                    }
                }
                if ((i = mk.get(PdfName.I)) != null && i.isIndirect()) {
                    newButton.setIconReference((PRIndirectReference)i);
                }
            }
            return newButton;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public boolean replacePushbuttonField(String field, PdfFormField button) {
        return this.replacePushbuttonField(field, button, 0);
    }

    public boolean replacePushbuttonField(String field, PdfFormField button, int order) {
        if (this.getFieldType(field) != 1) {
            return false;
        }
        Item item = this.getFieldItem(field);
        if (order >= item.size()) {
            return false;
        }
        PdfDictionary merged = item.getMerged(order);
        PdfDictionary values = item.getValue(order);
        PdfDictionary widgets = item.getWidget(order);
        for (PdfName pdfName : buttonRemove) {
            merged.remove(pdfName);
            values.remove(pdfName);
            widgets.remove(pdfName);
        }
        for (PdfName key : button.getKeys()) {
            if (key.equals(PdfName.T) || key.equals(PdfName.RECT)) continue;
            if (key.equals(PdfName.FF)) {
                values.put(key, button.get(key));
            } else {
                widgets.put(key, button.get(key));
            }
            merged.put(key, button.get(key));
        }
        return true;
    }

    static {
        stdFieldFontNames.put("CoBO", new String[]{"Courier-BoldOblique"});
        stdFieldFontNames.put("CoBo", new String[]{"Courier-Bold"});
        stdFieldFontNames.put("CoOb", new String[]{"Courier-Oblique"});
        stdFieldFontNames.put("Cour", new String[]{"Courier"});
        stdFieldFontNames.put("HeBO", new String[]{"Helvetica-BoldOblique"});
        stdFieldFontNames.put("HeBo", new String[]{"Helvetica-Bold"});
        stdFieldFontNames.put("HeOb", new String[]{"Helvetica-Oblique"});
        stdFieldFontNames.put("Helv", new String[]{"Helvetica"});
        stdFieldFontNames.put("Symb", new String[]{"Symbol"});
        stdFieldFontNames.put("TiBI", new String[]{"Times-BoldItalic"});
        stdFieldFontNames.put("TiBo", new String[]{"Times-Bold"});
        stdFieldFontNames.put("TiIt", new String[]{"Times-Italic"});
        stdFieldFontNames.put("TiRo", new String[]{"Times-Roman"});
        stdFieldFontNames.put("ZaDb", new String[]{"ZapfDingbats"});
        stdFieldFontNames.put("HySm", new String[]{"HYSMyeongJo-Medium", "UniKS-UCS2-H"});
        stdFieldFontNames.put("HyGo", new String[]{"HYGoThic-Medium", "UniKS-UCS2-H"});
        stdFieldFontNames.put("KaGo", new String[]{"HeiseiKakuGo-W5", "UniKS-UCS2-H"});
        stdFieldFontNames.put("KaMi", new String[]{"HeiseiMin-W3", "UniJIS-UCS2-H"});
        stdFieldFontNames.put("MHei", new String[]{"MHei-Medium", "UniCNS-UCS2-H"});
        stdFieldFontNames.put("MSun", new String[]{"MSung-Light", "UniCNS-UCS2-H"});
        stdFieldFontNames.put("STSo", new String[]{"STSong-Light", "UniGB-UCS2-H"});
        buttonRemove = new PdfName[]{PdfName.MK, PdfName.F, PdfName.FF, PdfName.Q, PdfName.BS, PdfName.BORDER};
    }

    private static class SorterComparator
    implements Comparator<Object[]> {
        private SorterComparator() {
        }

        @Override
        public int compare(Object[] o1, Object[] o2) {
            int n1 = ((int[])o1[1])[0];
            int n2 = ((int[])o2[1])[0];
            return n1 - n2;
        }
    }

    private static class RevisionStream
    extends InputStream {
        private byte[] b = new byte[1];
        private RandomAccessFileOrArray raf;
        private int length;
        private int rangePosition = 0;
        private boolean closed;

        private RevisionStream(RandomAccessFileOrArray raf, int length) {
            this.raf = raf;
            this.length = length;
        }

        @Override
        public int read() throws IOException {
            int n = this.read(this.b);
            if (n != 1) {
                return -1;
            }
            return this.b[0] & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            }
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            if (this.rangePosition >= this.length) {
                this.close();
                return -1;
            }
            int elen = Math.min(len, this.length - this.rangePosition);
            this.raf.readFully(b, off, elen);
            this.rangePosition += elen;
            return elen;
        }

        @Override
        public void close() throws IOException {
            if (!this.closed) {
                this.raf.close();
                this.closed = true;
            }
        }
    }

    private static class InstHit {
        IntHashtable hits;

        public InstHit(int[] inst) {
            if (inst == null) {
                return;
            }
            this.hits = new IntHashtable();
            for (int i : inst) {
                this.hits.put(i, 1);
            }
        }

        public boolean isHit(int n) {
            if (this.hits == null) {
                return true;
            }
            return this.hits.containsKey(n);
        }
    }

    public static class Item {
        public static final int WRITE_MERGED = 1;
        public static final int WRITE_WIDGET = 2;
        public static final int WRITE_VALUE = 4;
        public ArrayList<PdfDictionary> values = new ArrayList();
        public ArrayList<PdfDictionary> widgets = new ArrayList();
        public ArrayList<PdfIndirectReference> widgetRefs = new ArrayList();
        public ArrayList<PdfDictionary> merged = new ArrayList();
        public ArrayList<Integer> page = new ArrayList();
        public ArrayList<Integer> tabOrder = new ArrayList();

        public void writeToAll(PdfName key, PdfObject value, int writeFlags) {
            int i;
            PdfDictionary curDict = null;
            if ((writeFlags & 1) != 0) {
                for (i = 0; i < this.merged.size(); ++i) {
                    curDict = this.getMerged(i);
                    curDict.put(key, value);
                }
            }
            if ((writeFlags & 2) != 0) {
                for (i = 0; i < this.widgets.size(); ++i) {
                    curDict = this.getWidget(i);
                    curDict.put(key, value);
                }
            }
            if ((writeFlags & 4) != 0) {
                for (i = 0; i < this.values.size(); ++i) {
                    curDict = this.getValue(i);
                    curDict.put(key, value);
                }
            }
        }

        public void markUsed(AcroFields parentFields, int writeFlags) {
            int i;
            if ((writeFlags & 4) != 0) {
                for (i = 0; i < this.size(); ++i) {
                    parentFields.markUsed(this.getValue(i));
                }
            }
            if ((writeFlags & 2) != 0) {
                for (i = 0; i < this.size(); ++i) {
                    parentFields.markUsed(this.getWidget(i));
                }
            }
        }

        public int size() {
            return this.values.size();
        }

        void remove(int killIdx) {
            this.values.remove(killIdx);
            this.widgets.remove(killIdx);
            this.widgetRefs.remove(killIdx);
            this.merged.remove(killIdx);
            this.page.remove(killIdx);
            this.tabOrder.remove(killIdx);
        }

        public PdfDictionary getValue(int idx) {
            return this.values.get(idx);
        }

        void addValue(PdfDictionary value) {
            this.values.add(value);
        }

        public PdfDictionary getWidget(int idx) {
            return this.widgets.get(idx);
        }

        void addWidget(PdfDictionary widget) {
            this.widgets.add(widget);
        }

        public PdfIndirectReference getWidgetRef(int idx) {
            return this.widgetRefs.get(idx);
        }

        void addWidgetRef(PdfIndirectReference widgRef) {
            this.widgetRefs.add(widgRef);
        }

        public PdfDictionary getMerged(int idx) {
            return this.merged.get(idx);
        }

        void addMerged(PdfDictionary mergeDict) {
            this.merged.add(mergeDict);
        }

        public Integer getPage(int idx) {
            return this.page.get(idx);
        }

        void addPage(int pg) {
            this.page.add(pg);
        }

        void forcePage(int idx, int pg) {
            this.page.set(idx, pg);
        }

        public Integer getTabOrder(int idx) {
            return this.tabOrder.get(idx);
        }

        void addTabOrder(int order) {
            this.tabOrder.add(order);
        }
    }
}

