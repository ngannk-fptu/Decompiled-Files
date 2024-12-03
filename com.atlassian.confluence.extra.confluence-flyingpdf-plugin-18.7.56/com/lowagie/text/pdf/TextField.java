/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseField;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.FontSelector;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfDashPattern;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class TextField
extends BaseField {
    private String defaultText;
    private String[] choices;
    private String[] choiceExports;
    private ArrayList<Integer> choiceSelections = new ArrayList();
    private int topFirst;
    private float extraMarginLeft;
    private float extraMarginTop;
    private List<BaseFont> substitutionFonts;
    private BaseFont extensionFont;

    public TextField(PdfWriter writer, Rectangle box, String fieldName) {
        super(writer, box, fieldName);
    }

    private static boolean checkRTL(String text) {
        char[] cc;
        if (text == null || text.length() == 0) {
            return false;
        }
        for (char c : cc = text.toCharArray()) {
            if (c < '\u0590' || c >= '\u0780') continue;
            return true;
        }
        return false;
    }

    private static void changeFontSize(Phrase p, float size) {
        for (Object o : p) {
            ((Chunk)o).getFont().setSize(size);
        }
    }

    private Phrase composePhrase(String text, BaseFont ufont, Color color, float fontSize) {
        Phrase phrase = null;
        if (this.extensionFont == null && (this.substitutionFonts == null || this.substitutionFonts.isEmpty())) {
            phrase = new Phrase(new Chunk(text, new Font(ufont, fontSize, 0, color)));
        } else {
            FontSelector fs = new FontSelector();
            fs.addFont(new Font(ufont, fontSize, 0, color));
            if (this.extensionFont != null) {
                fs.addFont(new Font(this.extensionFont, fontSize, 0, color));
            }
            if (this.substitutionFonts != null) {
                for (BaseFont substitutionFont : this.substitutionFonts) {
                    fs.addFont(new Font(substitutionFont, fontSize, 0, color));
                }
            }
            phrase = fs.process(text);
        }
        return phrase;
    }

    public static String removeCRLF(String text) {
        if (text.indexOf(10) >= 0 || text.indexOf(13) >= 0) {
            char[] p = text.toCharArray();
            StringBuilder sb = new StringBuilder(p.length);
            for (int k = 0; k < p.length; ++k) {
                char c = p[k];
                if (c == '\n') {
                    sb.append(' ');
                    continue;
                }
                if (c == '\r') {
                    sb.append(' ');
                    if (k >= p.length - 1 || p[k + 1] != '\n') continue;
                    ++k;
                    continue;
                }
                sb.append(c);
            }
            return sb.toString();
        }
        return text;
    }

    public static String obfuscatePassword(String text) {
        char[] pchar = new char[text.length()];
        for (int i = 0; i < text.length(); ++i) {
            pchar[i] = 42;
        }
        return new String(pchar);
    }

    public PdfAppearance getAppearance() throws IOException, DocumentException {
        PdfAppearance app = this.getBorderAppearance();
        app.beginVariableText();
        if (this.text == null || this.text.length() == 0) {
            app.endVariableText();
            return app;
        }
        boolean borderExtra = this.borderStyle == 2 || this.borderStyle == 3;
        float h = this.box.getHeight() - this.borderWidth * 2.0f - this.extraMarginTop;
        float bw2 = this.borderWidth;
        if (borderExtra) {
            h -= this.borderWidth * 2.0f;
            bw2 *= 2.0f;
        }
        float offsetX = Math.max(bw2, 1.0f);
        float offX = Math.min(bw2, offsetX);
        app.saveState();
        app.rectangle(offX, offX, this.box.getWidth() - 2.0f * offX, this.box.getHeight() - 2.0f * offX);
        app.clip();
        app.newPath();
        String ptext = (this.options & 0x2000) != 0 ? TextField.obfuscatePassword(this.text) : ((this.options & 0x1000) == 0 ? TextField.removeCRLF(this.text) : this.text);
        BaseFont ufont = this.getRealFont();
        Color fcolor = this.textColor == null ? GrayColor.GRAYBLACK : this.textColor;
        int rtl = TextField.checkRTL(ptext) ? 2 : 1;
        float usize = this.fontSize;
        Phrase phrase = this.composePhrase(ptext, ufont, fcolor, usize);
        if ((this.options & 0x1000) != 0) {
            float width = this.box.getWidth() - 4.0f * offsetX - this.extraMarginLeft;
            float factor = ufont.getFontDescriptor(8, 1.0f) - ufont.getFontDescriptor(6, 1.0f);
            ColumnText ct = new ColumnText(null);
            if (usize == 0.0f) {
                usize = h / factor;
                if (usize > 4.0f) {
                    if (usize > 12.0f) {
                        usize = 12.0f;
                    }
                    float step = Math.max((usize - 4.0f) / 10.0f, 0.2f);
                    ct.setSimpleColumn(0.0f, -h, width, 0.0f);
                    ct.setAlignment(this.alignment);
                    ct.setRunDirection(rtl);
                    while (usize > 4.0f) {
                        ct.setYLine(0.0f);
                        TextField.changeFontSize(phrase, usize);
                        ct.setText(phrase);
                        ct.setLeading(factor * usize);
                        int status = ct.go(true);
                        if ((status & 2) == 0) break;
                        usize -= step;
                    }
                }
                if (usize < 4.0f) {
                    usize = 4.0f;
                }
            }
            TextField.changeFontSize(phrase, usize);
            ct.setCanvas(app);
            float leading = usize * factor;
            float offsetY = offsetX + h - ufont.getFontDescriptor(8, usize);
            ct.setSimpleColumn(this.extraMarginLeft + 2.0f * offsetX, -20000.0f, this.box.getWidth() - 2.0f * offsetX, offsetY + leading);
            ct.setLeading(leading);
            ct.setAlignment(this.alignment);
            ct.setRunDirection(rtl);
            ct.setText(phrase);
            ct.go();
        } else {
            if (usize == 0.0f) {
                float maxCalculatedSize = h / (ufont.getFontDescriptor(7, 1.0f) - ufont.getFontDescriptor(6, 1.0f));
                TextField.changeFontSize(phrase, 1.0f);
                float wd = ColumnText.getWidth(phrase, rtl, 0);
                usize = wd == 0.0f ? maxCalculatedSize : Math.min(maxCalculatedSize, (this.box.getWidth() - this.extraMarginLeft - 4.0f * offsetX) / wd);
                if (usize < 4.0f) {
                    usize = 4.0f;
                }
            }
            TextField.changeFontSize(phrase, usize);
            float offsetY = offX + (this.box.getHeight() - 2.0f * offX - ufont.getFontDescriptor(1, usize)) / 2.0f;
            if (offsetY < offX) {
                offsetY = offX;
            }
            if (offsetY - offX < -ufont.getFontDescriptor(3, usize)) {
                float ny = -ufont.getFontDescriptor(3, usize) + offX;
                float dy = this.box.getHeight() - offX - ufont.getFontDescriptor(1, usize);
                offsetY = Math.min(ny, Math.max(offsetY, dy));
            }
            if ((this.options & 0x1000000) != 0 && this.maxCharacterLength > 0) {
                int textLen = Math.min(this.maxCharacterLength, ptext.length());
                int position = 0;
                if (this.alignment == 2) {
                    position = this.maxCharacterLength - textLen;
                } else if (this.alignment == 1) {
                    position = (this.maxCharacterLength - textLen) / 2;
                }
                float step = (this.box.getWidth() - this.extraMarginLeft) / (float)this.maxCharacterLength;
                float start = step / 2.0f + (float)position * step;
                if (this.textColor == null) {
                    app.setGrayFill(0.0f);
                } else {
                    app.setColorFill(this.textColor);
                }
                app.beginText();
                for (Object o : phrase) {
                    Chunk ck = (Chunk)o;
                    BaseFont bf = ck.getFont().getBaseFont();
                    app.setFontAndSize(bf, usize);
                    StringBuffer sb = ck.append("");
                    for (int j = 0; j < sb.length(); ++j) {
                        String c = sb.substring(j, j + 1);
                        float wd = bf.getWidthPoint(c, usize);
                        app.setTextMatrix(this.extraMarginLeft + start - wd / 2.0f, offsetY - this.extraMarginTop);
                        app.showText(c);
                        start += step;
                    }
                }
                app.endText();
            } else {
                float x;
                switch (this.alignment) {
                    case 2: {
                        x = this.extraMarginLeft + this.box.getWidth() - 2.0f * offsetX;
                        break;
                    }
                    case 1: {
                        x = this.extraMarginLeft + this.box.getWidth() / 2.0f;
                        break;
                    }
                    default: {
                        x = this.extraMarginLeft + 2.0f * offsetX;
                    }
                }
                ColumnText.showTextAligned(app, this.alignment, phrase, x, offsetY - this.extraMarginTop, 0.0f, rtl, 0);
            }
        }
        app.restoreState();
        app.endVariableText();
        return app;
    }

    PdfAppearance getListAppearance() throws IOException, DocumentException {
        PdfAppearance app = this.getBorderAppearance();
        if (this.choices == null || this.choices.length == 0) {
            return app;
        }
        app.beginVariableText();
        int topChoice = this.getTopChoice();
        BaseFont ufont = this.getRealFont();
        float usize = this.fontSize;
        if (usize == 0.0f) {
            usize = 12.0f;
        }
        boolean borderExtra = this.borderStyle == 2 || this.borderStyle == 3;
        float h = this.box.getHeight() - this.borderWidth * 2.0f;
        float offsetX = this.borderWidth;
        if (borderExtra) {
            h -= this.borderWidth * 2.0f;
            offsetX *= 2.0f;
        }
        float leading = ufont.getFontDescriptor(8, usize) - ufont.getFontDescriptor(6, usize);
        int maxFit = (int)(h / leading) + 1;
        int first = 0;
        int last = 0;
        first = topChoice;
        last = first + maxFit;
        if (last > this.choices.length) {
            last = this.choices.length;
        }
        this.topFirst = first;
        app.saveState();
        app.rectangle(offsetX, offsetX, this.box.getWidth() - 2.0f * offsetX, this.box.getHeight() - 2.0f * offsetX);
        app.clip();
        app.newPath();
        Color fcolor = this.textColor == null ? GrayColor.GRAYBLACK : this.textColor;
        app.setColorFill(new Color(10, 36, 106));
        for (Integer choiceSelection : this.choiceSelections) {
            int curChoice = choiceSelection;
            if (curChoice < first || curChoice > last) continue;
            app.rectangle(offsetX, offsetX + h - (float)(curChoice - first + 1) * leading, this.box.getWidth() - 2.0f * offsetX, leading);
            app.fill();
        }
        float xp = offsetX * 2.0f;
        float yp = offsetX + h - ufont.getFontDescriptor(8, usize);
        int idx = first;
        while (idx < last) {
            String ptext = this.choices[idx];
            int rtl = TextField.checkRTL(ptext) ? 2 : 1;
            ptext = TextField.removeCRLF(ptext);
            GrayColor textCol = this.choiceSelections.contains(idx) ? GrayColor.GRAYWHITE : fcolor;
            Phrase phrase = this.composePhrase(ptext, ufont, textCol, usize);
            ColumnText.showTextAligned(app, 0, phrase, xp, yp, 0.0f, rtl, 0);
            ++idx;
            yp -= leading;
        }
        app.restoreState();
        app.endVariableText();
        return app;
    }

    public PdfFormField getTextField() throws IOException, DocumentException {
        if (this.maxCharacterLength <= 0) {
            this.options &= 0xFEFFFFFF;
        }
        if ((this.options & 0x1000000) != 0) {
            this.options &= 0xFFFFEFFF;
        }
        PdfFormField field = PdfFormField.createTextField(this.writer, false, false, this.maxCharacterLength);
        field.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
        switch (this.alignment) {
            case 1: {
                field.setQuadding(1);
                break;
            }
            case 2: {
                field.setQuadding(2);
            }
        }
        if (this.rotation != 0) {
            field.setMKRotation(this.rotation);
        }
        if (this.fieldName != null) {
            field.setFieldName(this.fieldName);
            if (!"".equals(this.text)) {
                field.setValueAsString(this.text);
            }
            if (this.defaultText != null) {
                field.setDefaultValueAsString(this.defaultText);
            }
            if ((this.options & 1) != 0) {
                field.setFieldFlags(1);
            }
            if ((this.options & 2) != 0) {
                field.setFieldFlags(2);
            }
            if ((this.options & 0x1000) != 0) {
                field.setFieldFlags(4096);
            }
            if ((this.options & 0x800000) != 0) {
                field.setFieldFlags(0x800000);
            }
            if ((this.options & 0x2000) != 0) {
                field.setFieldFlags(8192);
            }
            if ((this.options & 0x100000) != 0) {
                field.setFieldFlags(0x100000);
            }
            if ((this.options & 0x400000) != 0) {
                field.setFieldFlags(0x400000);
            }
            if ((this.options & 0x1000000) != 0) {
                field.setFieldFlags(0x1000000);
            }
        }
        field.setBorderStyle(new PdfBorderDictionary(this.borderWidth, this.borderStyle, new PdfDashPattern(3.0f)));
        PdfAppearance tp = this.getAppearance();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
        PdfAppearance da = (PdfAppearance)tp.getDuplicate();
        da.setFontAndSize(this.getRealFont(), this.fontSize);
        if (this.textColor == null) {
            da.setGrayFill(0.0f);
        } else {
            da.setColorFill(this.textColor);
        }
        field.setDefaultAppearanceString(da);
        if (this.borderColor != null) {
            field.setMKBorderColor(this.borderColor);
        }
        if (this.backgroundColor != null) {
            field.setMKBackgroundColor(this.backgroundColor);
        }
        switch (this.visibility) {
            case 1: {
                field.setFlags(6);
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                field.setFlags(36);
                break;
            }
            default: {
                field.setFlags(4);
            }
        }
        return field;
    }

    public PdfFormField getComboField() throws IOException, DocumentException {
        return this.getChoiceField(false);
    }

    public PdfFormField getListField() throws IOException, DocumentException {
        return this.getChoiceField(true);
    }

    private int getTopChoice() {
        if (this.choiceSelections == null || this.choiceSelections.size() == 0) {
            return 0;
        }
        Integer firstValue = this.choiceSelections.get(0);
        if (firstValue == null) {
            return 0;
        }
        int topChoice = 0;
        if (this.choices != null) {
            topChoice = firstValue;
            topChoice = Math.min(topChoice, this.choices.length);
            topChoice = Math.max(0, topChoice);
        }
        return topChoice;
    }

    protected PdfFormField getChoiceField(boolean isList) throws IOException, DocumentException {
        PdfAppearance tp;
        this.options &= 0xFEFFEFFF;
        String[] uchoices = this.choices;
        if (uchoices == null) {
            uchoices = new String[]{};
        }
        int topChoice = this.getTopChoice();
        if (this.text == null) {
            this.text = "";
        }
        if (topChoice >= 0) {
            this.text = uchoices[topChoice];
        }
        PdfFormField field = null;
        String[][] mix = null;
        if (this.choiceExports == null) {
            field = isList ? PdfFormField.createList(this.writer, uchoices, topChoice) : PdfFormField.createCombo(this.writer, (this.options & 0x40000) != 0, uchoices, topChoice);
        } else {
            mix = new String[uchoices.length][2];
            for (int k = 0; k < mix.length; ++k) {
                String string = uchoices[k];
                mix[k][1] = string;
                mix[k][0] = string;
            }
            int top = Math.min(uchoices.length, this.choiceExports.length);
            for (int k = 0; k < top; ++k) {
                if (this.choiceExports[k] == null) continue;
                mix[k][0] = this.choiceExports[k];
            }
            field = isList ? PdfFormField.createList(this.writer, mix, topChoice) : PdfFormField.createCombo(this.writer, (this.options & 0x40000) != 0, mix, topChoice);
        }
        field.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
        if (this.rotation != 0) {
            field.setMKRotation(this.rotation);
        }
        if (this.fieldName != null) {
            field.setFieldName(this.fieldName);
            if (uchoices.length > 0) {
                if (mix != null) {
                    if (this.choiceSelections.size() < 2) {
                        field.setValueAsString(mix[topChoice][0]);
                        field.setDefaultValueAsString(mix[topChoice][0]);
                    } else {
                        this.writeMultipleValues(field, mix);
                    }
                } else if (this.choiceSelections.size() < 2) {
                    field.setValueAsString(this.text);
                    field.setDefaultValueAsString(this.text);
                } else {
                    this.writeMultipleValues(field, null);
                }
            }
            if ((this.options & 1) != 0) {
                field.setFieldFlags(1);
            }
            if ((this.options & 2) != 0) {
                field.setFieldFlags(2);
            }
            if ((this.options & 0x400000) != 0) {
                field.setFieldFlags(0x400000);
            }
            if ((this.options & 0x200000) != 0) {
                field.setFieldFlags(0x200000);
            }
        }
        field.setBorderStyle(new PdfBorderDictionary(this.borderWidth, this.borderStyle, new PdfDashPattern(3.0f)));
        if (isList) {
            tp = this.getListAppearance();
            if (this.topFirst > 0) {
                field.put(PdfName.TI, new PdfNumber(this.topFirst));
            }
        } else {
            tp = this.getAppearance();
        }
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
        PdfAppearance da = (PdfAppearance)tp.getDuplicate();
        da.setFontAndSize(this.getRealFont(), this.fontSize);
        if (this.textColor == null) {
            da.setGrayFill(0.0f);
        } else {
            da.setColorFill(this.textColor);
        }
        field.setDefaultAppearanceString(da);
        if (this.borderColor != null) {
            field.setMKBorderColor(this.borderColor);
        }
        if (this.backgroundColor != null) {
            field.setMKBackgroundColor(this.backgroundColor);
        }
        switch (this.visibility) {
            case 1: {
                field.setFlags(6);
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                field.setFlags(36);
                break;
            }
            default: {
                field.setFlags(4);
            }
        }
        return field;
    }

    private void writeMultipleValues(PdfFormField field, String[][] mix) {
        PdfArray indexes = new PdfArray();
        PdfArray values = new PdfArray();
        for (Integer choiceSelection : this.choiceSelections) {
            int idx = choiceSelection;
            indexes.add(new PdfNumber(idx));
            if (mix != null) {
                values.add(new PdfString(mix[idx][0]));
                continue;
            }
            if (this.choices == null) continue;
            values.add(new PdfString(this.choices[idx]));
        }
        field.put(PdfName.V, values);
        field.put(PdfName.I, indexes);
    }

    public String getDefaultText() {
        return this.defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public String[] getChoices() {
        return this.choices;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }

    public String[] getChoiceExports() {
        return this.choiceExports;
    }

    public void setChoiceExports(String[] choiceExports) {
        this.choiceExports = choiceExports;
    }

    public int getChoiceSelection() {
        return this.getTopChoice();
    }

    public ArrayList<Integer> gteChoiceSelections() {
        return this.choiceSelections;
    }

    public void setChoiceSelection(int choiceSelection) {
        this.choiceSelections = new ArrayList();
        this.choiceSelections.add(choiceSelection);
    }

    public void addChoiceSelection(int selection) {
        if ((this.options & 0x200000) != 0) {
            this.choiceSelections.add(selection);
        }
    }

    @Deprecated
    public void setChoiceSelections(@Nullable ArrayList selections) {
        this.setChoiceSelections((List<Integer>)selections);
    }

    public void setChoiceSelections(@Nullable List<Integer> selections) {
        if (selections != null) {
            this.choiceSelections = new ArrayList<Integer>(selections);
            if (this.choiceSelections.size() > 1 && (this.options & 0x200000) == 0) {
                while (this.choiceSelections.size() > 1) {
                    this.choiceSelections.remove(1);
                }
            }
        } else {
            this.choiceSelections.clear();
        }
    }

    int getTopFirst() {
        return this.topFirst;
    }

    public void setExtraMargin(float extraMarginLeft, float extraMarginTop) {
        this.extraMarginLeft = extraMarginLeft;
        this.extraMarginTop = extraMarginTop;
    }

    @Deprecated
    public ArrayList getSubstitutionFonts() {
        return (ArrayList)this.substitutionFonts;
    }

    public List<BaseFont> getSubstitutionFontList() {
        return this.substitutionFonts;
    }

    @Deprecated
    public void setSubstitutionFonts(List<BaseFont> substitutionFonts) {
        this.substitutionFonts = substitutionFonts;
    }

    public void setSubstitutionFontList(List<BaseFont> substitutionFonts) {
        this.substitutionFonts = substitutionFonts;
    }

    public BaseFont getExtensionFont() {
        return this.extensionFont;
    }

    public void setExtensionFont(BaseFont extensionFont) {
        this.extensionFont = extensionFont;
    }
}

