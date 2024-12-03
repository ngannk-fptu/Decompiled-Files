/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDComboBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDListBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.apache.pdfbox.pdmodel.interactive.form.PDPushButton;
import org.apache.pdfbox.pdmodel.interactive.form.PDRadioButton;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

public final class PDFieldFactory {
    private static final String FIELD_TYPE_TEXT = "Tx";
    private static final String FIELD_TYPE_BUTTON = "Btn";
    private static final String FIELD_TYPE_CHOICE = "Ch";
    private static final String FIELD_TYPE_SIGNATURE = "Sig";

    private PDFieldFactory() {
    }

    public static PDField createField(PDAcroForm form, COSDictionary field, PDNonTerminalField parent) {
        String fieldType;
        COSArray kids;
        if (field.containsKey(COSName.KIDS) && (kids = (COSArray)field.getDictionaryObject(COSName.KIDS)) != null && kids.size() > 0) {
            for (int i = 0; i < kids.size(); ++i) {
                COSBase kid = kids.getObject(i);
                if (!(kid instanceof COSDictionary) || ((COSDictionary)kid).getString(COSName.T) == null) continue;
                return new PDNonTerminalField(form, field, parent);
            }
        }
        if (FIELD_TYPE_CHOICE.equals(fieldType = PDFieldFactory.findFieldType(field))) {
            return PDFieldFactory.createChoiceSubType(form, field, parent);
        }
        if (FIELD_TYPE_TEXT.equals(fieldType)) {
            return new PDTextField(form, field, parent);
        }
        if (FIELD_TYPE_SIGNATURE.equals(fieldType)) {
            return new PDSignatureField(form, field, parent);
        }
        if (FIELD_TYPE_BUTTON.equals(fieldType)) {
            return PDFieldFactory.createButtonSubType(form, field, parent);
        }
        return null;
    }

    private static PDField createChoiceSubType(PDAcroForm form, COSDictionary field, PDNonTerminalField parent) {
        int flags = field.getInt(COSName.FF, 0);
        if ((flags & 0x20000) != 0) {
            return new PDComboBox(form, field, parent);
        }
        return new PDListBox(form, field, parent);
    }

    private static PDField createButtonSubType(PDAcroForm form, COSDictionary field, PDNonTerminalField parent) {
        int flags = field.getInt(COSName.FF, 0);
        if ((flags & 0x8000) != 0) {
            return new PDRadioButton(form, field, parent);
        }
        if ((flags & 0x10000) != 0) {
            return new PDPushButton(form, field, parent);
        }
        return new PDCheckBox(form, field, parent);
    }

    private static String findFieldType(COSDictionary dic) {
        COSBase base;
        String retval = dic.getNameAsString(COSName.FT);
        if (retval == null && (base = dic.getDictionaryObject(COSName.PARENT, COSName.P)) instanceof COSDictionary) {
            retval = PDFieldFactory.findFieldType((COSDictionary)base);
        }
        return retval;
    }
}

