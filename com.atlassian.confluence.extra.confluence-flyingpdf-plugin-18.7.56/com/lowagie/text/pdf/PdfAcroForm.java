/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class PdfAcroForm
extends PdfDictionary {
    private PdfWriter writer;
    private Map<PdfTemplate, Object> fieldTemplates = new HashMap<PdfTemplate, Object>();
    private PdfArray documentFields = new PdfArray();
    private PdfArray calculationOrder = new PdfArray();
    private int sigFlags = 0;

    public PdfAcroForm(PdfWriter writer) {
        this.writer = writer;
    }

    public void setNeedAppearances(boolean value) {
        this.put(PdfName.NEEDAPPEARANCES, new PdfBoolean(value));
    }

    @Deprecated
    public void addFieldTemplates(HashMap ft) {
        this.fieldTemplates.putAll(ft);
    }

    public void addFieldTemplates(Map<PdfTemplate, Object> ft) {
        this.fieldTemplates.putAll(ft);
    }

    public void addDocumentField(PdfIndirectReference ref) {
        this.documentFields.add(ref);
    }

    public boolean isValid() {
        if (this.documentFields.size() == 0) {
            return false;
        }
        this.put(PdfName.FIELDS, this.documentFields);
        if (this.sigFlags != 0) {
            this.put(PdfName.SIGFLAGS, new PdfNumber(this.sigFlags));
        }
        if (this.calculationOrder.size() > 0) {
            this.put(PdfName.CO, this.calculationOrder);
        }
        if (this.fieldTemplates.isEmpty()) {
            return true;
        }
        PdfDictionary dic = new PdfDictionary();
        Iterator<PdfTemplate> iterator = this.fieldTemplates.keySet().iterator();
        while (iterator.hasNext()) {
            PdfTemplate o;
            PdfTemplate template = o = iterator.next();
            PdfFormField.mergeResources(dic, (PdfDictionary)template.getResources());
        }
        this.put(PdfName.DR, dic);
        this.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
        PdfDictionary fonts = (PdfDictionary)dic.get(PdfName.FONT);
        if (fonts != null) {
            this.writer.eliminateFontSubset(fonts);
        }
        return true;
    }

    public void addCalculationOrder(PdfFormField formField) {
        this.calculationOrder.add(formField.getIndirectReference());
    }

    public void setSigFlags(int f) {
        this.sigFlags |= f;
    }

    public void addFormField(PdfFormField formField) {
        this.writer.addAnnotation(formField);
    }

    public PdfFormField addHtmlPostButton(String name, String caption, String value, String url, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfAction action = PdfAction.createSubmitForm(url, null, 4);
        PdfFormField button = new PdfFormField(this.writer, llx, lly, urx, ury, action);
        this.setButtonParams(button, 65536, name, value);
        this.drawButton(button, caption, font, fontSize, llx, lly, urx, ury);
        this.addFormField(button);
        return button;
    }

    public PdfFormField addResetButton(String name, String caption, String value, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfAction action = PdfAction.createResetForm(null, 0);
        PdfFormField button = new PdfFormField(this.writer, llx, lly, urx, ury, action);
        this.setButtonParams(button, 65536, name, value);
        this.drawButton(button, caption, font, fontSize, llx, lly, urx, ury);
        this.addFormField(button);
        return button;
    }

    public PdfFormField addMap(String name, String value, String url, PdfContentByte appearance, float llx, float lly, float urx, float ury) {
        PdfAction action = PdfAction.createSubmitForm(url, null, 20);
        PdfFormField button = new PdfFormField(this.writer, llx, lly, urx, ury, action);
        this.setButtonParams(button, 65536, name, null);
        PdfAppearance pa = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        pa.add(appearance);
        button.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, pa);
        this.addFormField(button);
        return button;
    }

    public void setButtonParams(PdfFormField button, int characteristics, String name, String value) {
        button.setButton(characteristics);
        button.setFlags(4);
        button.setPage();
        button.setFieldName(name);
        if (value != null) {
            button.setValueAsString(value);
        }
    }

    public void drawButton(PdfFormField button, String caption, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfAppearance pa = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        pa.drawButton(0.0f, 0.0f, urx - llx, ury - lly, caption, font, fontSize);
        button.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, pa);
    }

    public PdfFormField addHiddenField(String name, String value) {
        PdfFormField hidden = PdfFormField.createEmpty(this.writer);
        hidden.setFieldName(name);
        hidden.setValueAsName(value);
        this.addFormField(hidden);
        return hidden;
    }

    public PdfFormField addSingleLineTextField(String name, String text, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfFormField field = PdfFormField.createTextField(this.writer, false, false, 0);
        this.setTextFieldParams(field, text, name, llx, lly, urx, ury);
        this.drawSingleLineOfText(field, text, font, fontSize, llx, lly, urx, ury);
        this.addFormField(field);
        return field;
    }

    public PdfFormField addMultiLineTextField(String name, String text, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfFormField field = PdfFormField.createTextField(this.writer, true, false, 0);
        this.setTextFieldParams(field, text, name, llx, lly, urx, ury);
        this.drawMultiLineOfText(field, text, font, fontSize, llx, lly, urx, ury);
        this.addFormField(field);
        return field;
    }

    public PdfFormField addSingleLinePasswordField(String name, String text, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfFormField field = PdfFormField.createTextField(this.writer, false, true, 0);
        this.setTextFieldParams(field, text, name, llx, lly, urx, ury);
        this.drawSingleLineOfText(field, text, font, fontSize, llx, lly, urx, ury);
        this.addFormField(field);
        return field;
    }

    public void setTextFieldParams(PdfFormField field, String text, String name, float llx, float lly, float urx, float ury) {
        field.setWidget(new Rectangle(llx, lly, urx, ury), PdfAnnotation.HIGHLIGHT_INVERT);
        field.setValueAsString(text);
        field.setDefaultValueAsString(text);
        field.setFieldName(name);
        field.setFlags(4);
        field.setPage();
    }

    public void drawSingleLineOfText(PdfFormField field, String text, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfAppearance tp = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        PdfAppearance tp2 = (PdfAppearance)tp.getDuplicate();
        tp2.setFontAndSize(font, fontSize);
        tp2.resetRGBColorFill();
        field.setDefaultAppearanceString(tp2);
        tp.drawTextField(0.0f, 0.0f, urx - llx, ury - lly);
        tp.beginVariableText();
        tp.saveState();
        tp.rectangle(3.0f, 3.0f, urx - llx - 6.0f, ury - lly - 6.0f);
        tp.clip();
        tp.newPath();
        tp.beginText();
        tp.setFontAndSize(font, fontSize);
        tp.resetRGBColorFill();
        tp.setTextMatrix(4.0f, (ury - lly) / 2.0f - fontSize * 0.3f);
        tp.showText(text);
        tp.endText();
        tp.restoreState();
        tp.endVariableText();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
    }

    public void drawMultiLineOfText(PdfFormField field, String text, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfAppearance tp = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        PdfAppearance tp2 = (PdfAppearance)tp.getDuplicate();
        tp2.setFontAndSize(font, fontSize);
        tp2.resetRGBColorFill();
        field.setDefaultAppearanceString(tp2);
        tp.drawTextField(0.0f, 0.0f, urx - llx, ury - lly);
        tp.beginVariableText();
        tp.saveState();
        tp.rectangle(3.0f, 3.0f, urx - llx - 6.0f, ury - lly - 6.0f);
        tp.clip();
        tp.newPath();
        tp.beginText();
        tp.setFontAndSize(font, fontSize);
        tp.resetRGBColorFill();
        tp.setTextMatrix(4.0f, 5.0f);
        StringTokenizer tokenizer = new StringTokenizer(text, "\n");
        float yPos = ury - lly;
        while (tokenizer.hasMoreTokens()) {
            tp.showTextAligned(0, tokenizer.nextToken(), 3.0f, yPos -= fontSize * 1.2f, 0.0f);
        }
        tp.endText();
        tp.restoreState();
        tp.endVariableText();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
    }

    public PdfFormField addCheckBox(String name, String value, boolean status, float llx, float lly, float urx, float ury) {
        PdfFormField field = PdfFormField.createCheckBox(this.writer);
        this.setCheckBoxParams(field, name, value, status, llx, lly, urx, ury);
        this.drawCheckBoxAppearences(field, value, llx, lly, urx, ury);
        this.addFormField(field);
        return field;
    }

    public void setCheckBoxParams(PdfFormField field, String name, String value, boolean status, float llx, float lly, float urx, float ury) {
        field.setWidget(new Rectangle(llx, lly, urx, ury), PdfAnnotation.HIGHLIGHT_TOGGLE);
        field.setFieldName(name);
        if (status) {
            field.setValueAsName(value);
            field.setAppearanceState(value);
        } else {
            field.setValueAsName("Off");
            field.setAppearanceState("Off");
        }
        field.setFlags(4);
        field.setPage();
        field.setBorderStyle(new PdfBorderDictionary(1.0f, 0));
    }

    public void drawCheckBoxAppearences(PdfFormField field, String value, float llx, float lly, float urx, float ury) {
        BaseFont font = null;
        try {
            font = BaseFont.createFont("ZapfDingbats", "Cp1252", false);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
        float size = ury - lly;
        PdfAppearance tpOn = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        PdfAppearance tp2 = (PdfAppearance)tpOn.getDuplicate();
        tp2.setFontAndSize(font, size);
        tp2.resetRGBColorFill();
        field.setDefaultAppearanceString(tp2);
        tpOn.drawTextField(0.0f, 0.0f, urx - llx, ury - lly);
        tpOn.saveState();
        tpOn.resetRGBColorFill();
        tpOn.beginText();
        tpOn.setFontAndSize(font, size);
        tpOn.showTextAligned(1, "4", (urx - llx) / 2.0f, (ury - lly) / 2.0f - size * 0.3f, 0.0f);
        tpOn.endText();
        tpOn.restoreState();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, value, tpOn);
        PdfAppearance tpOff = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        tpOff.drawTextField(0.0f, 0.0f, urx - llx, ury - lly);
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", tpOff);
    }

    public PdfFormField getRadioGroup(String name, String defaultValue, boolean noToggleToOff) {
        PdfFormField radio = PdfFormField.createRadioButton(this.writer, noToggleToOff);
        radio.setFieldName(name);
        radio.setValueAsName(defaultValue);
        return radio;
    }

    public void addRadioGroup(PdfFormField radiogroup) {
        this.addFormField(radiogroup);
    }

    public PdfFormField addRadioButton(PdfFormField radiogroup, String value, float llx, float lly, float urx, float ury) {
        PdfFormField radio = PdfFormField.createEmpty(this.writer);
        radio.setWidget(new Rectangle(llx, lly, urx, ury), PdfAnnotation.HIGHLIGHT_TOGGLE);
        String name = radiogroup.get(PdfName.V).toString().substring(1);
        if (name.equals(value)) {
            radio.setAppearanceState(value);
        } else {
            radio.setAppearanceState("Off");
        }
        this.drawRadioAppearences(radio, value, llx, lly, urx, ury);
        radiogroup.addKid(radio);
        return radio;
    }

    public void drawRadioAppearences(PdfFormField field, String value, float llx, float lly, float urx, float ury) {
        PdfAppearance tpOn = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        tpOn.drawRadioField(0.0f, 0.0f, urx - llx, ury - lly, true);
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, value, tpOn);
        PdfAppearance tpOff = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        tpOff.drawRadioField(0.0f, 0.0f, urx - llx, ury - lly, false);
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", tpOff);
    }

    public PdfFormField addSelectList(String name, String[] options, String defaultValue, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfFormField choice = PdfFormField.createList(this.writer, options, 0);
        this.setChoiceParams(choice, name, defaultValue, llx, lly, urx, ury);
        StringBuilder text = new StringBuilder();
        for (String option : options) {
            text.append(option).append('\n');
        }
        this.drawMultiLineOfText(choice, text.toString(), font, fontSize, llx, lly, urx, ury);
        this.addFormField(choice);
        return choice;
    }

    public PdfFormField addSelectList(String name, String[][] options, String defaultValue, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfFormField choice = PdfFormField.createList(this.writer, options, 0);
        this.setChoiceParams(choice, name, defaultValue, llx, lly, urx, ury);
        StringBuilder text = new StringBuilder();
        for (String[] option : options) {
            text.append(option[1]).append('\n');
        }
        this.drawMultiLineOfText(choice, text.toString(), font, fontSize, llx, lly, urx, ury);
        this.addFormField(choice);
        return choice;
    }

    public PdfFormField addComboBox(String name, String[] options, String defaultValue, boolean editable, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfFormField choice = PdfFormField.createCombo(this.writer, editable, options, 0);
        this.setChoiceParams(choice, name, defaultValue, llx, lly, urx, ury);
        if (defaultValue == null) {
            defaultValue = options[0];
        }
        this.drawSingleLineOfText(choice, defaultValue, font, fontSize, llx, lly, urx, ury);
        this.addFormField(choice);
        return choice;
    }

    public PdfFormField addComboBox(String name, String[][] options, String defaultValue, boolean editable, BaseFont font, float fontSize, float llx, float lly, float urx, float ury) {
        PdfFormField choice = PdfFormField.createCombo(this.writer, editable, options, 0);
        this.setChoiceParams(choice, name, defaultValue, llx, lly, urx, ury);
        String value = null;
        for (String[] option : options) {
            if (!option[0].equals(defaultValue)) continue;
            value = option[1];
            break;
        }
        if (value == null) {
            value = options[0][1];
        }
        this.drawSingleLineOfText(choice, value, font, fontSize, llx, lly, urx, ury);
        this.addFormField(choice);
        return choice;
    }

    public void setChoiceParams(PdfFormField field, String name, String defaultValue, float llx, float lly, float urx, float ury) {
        field.setWidget(new Rectangle(llx, lly, urx, ury), PdfAnnotation.HIGHLIGHT_INVERT);
        if (defaultValue != null) {
            field.setValueAsString(defaultValue);
            field.setDefaultValueAsString(defaultValue);
        }
        field.setFieldName(name);
        field.setFlags(4);
        field.setPage();
        field.setBorderStyle(new PdfBorderDictionary(2.0f, 0));
    }

    public PdfFormField addSignature(String name, float llx, float lly, float urx, float ury) {
        PdfFormField signature = PdfFormField.createSignature(this.writer);
        this.setSignatureParams(signature, name, llx, lly, urx, ury);
        this.drawSignatureAppearences(signature, llx, lly, urx, ury);
        this.addFormField(signature);
        return signature;
    }

    public void setSignatureParams(PdfFormField field, String name, float llx, float lly, float urx, float ury) {
        field.setWidget(new Rectangle(llx, lly, urx, ury), PdfAnnotation.HIGHLIGHT_INVERT);
        field.setFieldName(name);
        field.setFlags(4);
        field.setPage();
        field.setMKBorderColor(Color.black);
        field.setMKBackgroundColor(Color.white);
    }

    public void drawSignatureAppearences(PdfFormField field, float llx, float lly, float urx, float ury) {
        PdfAppearance tp = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        tp.setGrayFill(1.0f);
        tp.rectangle(0.0f, 0.0f, urx - llx, ury - lly);
        tp.fill();
        tp.setGrayStroke(0.0f);
        tp.setLineWidth(1.0f);
        tp.rectangle(0.5f, 0.5f, urx - llx - 0.5f, ury - lly - 0.5f);
        tp.closePathStroke();
        tp.saveState();
        tp.rectangle(1.0f, 1.0f, urx - llx - 2.0f, ury - lly - 2.0f);
        tp.clip();
        tp.newPath();
        tp.restoreState();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
    }
}

