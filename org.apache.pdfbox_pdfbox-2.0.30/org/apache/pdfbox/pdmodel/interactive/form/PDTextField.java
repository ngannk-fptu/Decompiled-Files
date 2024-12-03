/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.form.AppearanceGeneratorHelper;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;

public final class PDTextField
extends PDVariableText {
    private static final int FLAG_MULTILINE = 4096;
    private static final int FLAG_PASSWORD = 8192;
    private static final int FLAG_FILE_SELECT = 0x100000;
    private static final int FLAG_DO_NOT_SPELL_CHECK = 0x400000;
    private static final int FLAG_DO_NOT_SCROLL = 0x800000;
    private static final int FLAG_COMB = 0x1000000;
    private static final int FLAG_RICH_TEXT = 0x2000000;

    public PDTextField(PDAcroForm acroForm) {
        super(acroForm);
        this.getCOSObject().setItem(COSName.FT, (COSBase)COSName.TX);
    }

    PDTextField(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public boolean isMultiline() {
        return this.getCOSObject().getFlag(COSName.FF, 4096);
    }

    public void setMultiline(boolean multiline) {
        this.getCOSObject().setFlag(COSName.FF, 4096, multiline);
    }

    public boolean isPassword() {
        return this.getCOSObject().getFlag(COSName.FF, 8192);
    }

    public void setPassword(boolean password) {
        this.getCOSObject().setFlag(COSName.FF, 8192, password);
    }

    public boolean isFileSelect() {
        return this.getCOSObject().getFlag(COSName.FF, 0x100000);
    }

    public void setFileSelect(boolean fileSelect) {
        this.getCOSObject().setFlag(COSName.FF, 0x100000, fileSelect);
    }

    public boolean doNotSpellCheck() {
        return this.getCOSObject().getFlag(COSName.FF, 0x400000);
    }

    public void setDoNotSpellCheck(boolean doNotSpellCheck) {
        this.getCOSObject().setFlag(COSName.FF, 0x400000, doNotSpellCheck);
    }

    public boolean doNotScroll() {
        return this.getCOSObject().getFlag(COSName.FF, 0x800000);
    }

    public void setDoNotScroll(boolean doNotScroll) {
        this.getCOSObject().setFlag(COSName.FF, 0x800000, doNotScroll);
    }

    public boolean isComb() {
        return this.getCOSObject().getFlag(COSName.FF, 0x1000000);
    }

    public void setComb(boolean comb) {
        this.getCOSObject().setFlag(COSName.FF, 0x1000000, comb);
    }

    public boolean isRichText() {
        return this.getCOSObject().getFlag(COSName.FF, 0x2000000);
    }

    public void setRichText(boolean richText) {
        this.getCOSObject().setFlag(COSName.FF, 0x2000000, richText);
    }

    public int getMaxLen() {
        return this.getCOSObject().getInt(COSName.MAX_LEN);
    }

    public void setMaxLen(int maxLen) {
        this.getCOSObject().setInt(COSName.MAX_LEN, maxLen);
    }

    @Override
    public void setValue(String value) throws IOException {
        this.getCOSObject().setString(COSName.V, value);
        this.applyChange();
    }

    public void setDefaultValue(String value) throws IOException {
        this.getCOSObject().setString(COSName.DV, value);
    }

    public String getValue() {
        return this.getStringOrStream(this.getInheritableAttribute(COSName.V));
    }

    public String getDefaultValue() {
        return this.getStringOrStream(this.getInheritableAttribute(COSName.DV));
    }

    @Override
    public String getValueAsString() {
        return this.getValue();
    }

    @Override
    void constructAppearances() throws IOException {
        AppearanceGeneratorHelper apHelper = new AppearanceGeneratorHelper(this);
        apHelper.setAppearanceValue(this.getValue());
    }
}

