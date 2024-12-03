/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.fdf.FDFField;
import org.apache.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDChoice;
import org.apache.pdfbox.pdmodel.interactive.form.PDFieldFactory;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField;

public abstract class PDField
implements COSObjectable {
    private static final int FLAG_READ_ONLY = 1;
    private static final int FLAG_REQUIRED = 2;
    private static final int FLAG_NO_EXPORT = 4;
    private final PDAcroForm acroForm;
    private final PDNonTerminalField parent;
    private final COSDictionary dictionary;

    PDField(PDAcroForm acroForm) {
        this(acroForm, new COSDictionary(), null);
    }

    PDField(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        this.acroForm = acroForm;
        this.dictionary = field;
        this.parent = parent;
    }

    static PDField fromDictionary(PDAcroForm form, COSDictionary field, PDNonTerminalField parent) {
        return PDFieldFactory.createField(form, field, parent);
    }

    protected COSBase getInheritableAttribute(COSName key) {
        if (this.dictionary.containsKey(key)) {
            return this.dictionary.getDictionaryObject(key);
        }
        if (this.parent != null) {
            return this.parent.getInheritableAttribute(key);
        }
        return this.acroForm.getCOSObject().getDictionaryObject(key);
    }

    public abstract String getFieldType();

    public abstract String getValueAsString();

    public abstract void setValue(String var1) throws IOException;

    public abstract List<PDAnnotationWidget> getWidgets();

    public void setReadOnly(boolean readonly) {
        this.dictionary.setFlag(COSName.FF, 1, readonly);
    }

    public boolean isReadOnly() {
        return this.dictionary.getFlag(COSName.FF, 1);
    }

    public void setRequired(boolean required) {
        this.dictionary.setFlag(COSName.FF, 2, required);
    }

    public boolean isRequired() {
        return this.dictionary.getFlag(COSName.FF, 2);
    }

    public void setNoExport(boolean noExport) {
        this.dictionary.setFlag(COSName.FF, 4, noExport);
    }

    public boolean isNoExport() {
        return this.dictionary.getFlag(COSName.FF, 4);
    }

    public abstract int getFieldFlags();

    public void setFieldFlags(int flags) {
        this.dictionary.setInt(COSName.FF, flags);
    }

    public PDFormFieldAdditionalActions getActions() {
        COSDictionary aa = (COSDictionary)this.dictionary.getDictionaryObject(COSName.AA);
        if (aa != null) {
            return new PDFormFieldAdditionalActions(aa);
        }
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    void importFDF(FDFField fdfField) throws IOException {
        Integer ff;
        COSBase fieldValue = fdfField.getCOSValue();
        if (fieldValue != null && this instanceof PDTerminalField) {
            PDTerminalField currentField = (PDTerminalField)this;
            if (fieldValue instanceof COSName) {
                currentField.setValue(((COSName)fieldValue).getName());
            } else if (fieldValue instanceof COSString) {
                currentField.setValue(((COSString)fieldValue).getString());
            } else if (fieldValue instanceof COSStream) {
                currentField.setValue(((COSStream)fieldValue).toTextString());
            } else {
                if (!(fieldValue instanceof COSArray) || !(this instanceof PDChoice)) throw new IOException("Error:Unknown type for field import" + fieldValue);
                ((PDChoice)this).setValue(COSArrayList.convertCOSStringCOSArrayToList((COSArray)fieldValue));
            }
        } else if (fieldValue != null) {
            this.dictionary.setItem(COSName.V, fieldValue);
        }
        if ((ff = fdfField.getFieldFlags()) != null) {
            this.setFieldFlags(ff);
            return;
        } else {
            Integer clrFf;
            Integer setFf = fdfField.getSetFieldFlags();
            int fieldFlags = this.getFieldFlags();
            if (setFf != null) {
                int setFfInt = setFf;
                this.setFieldFlags(fieldFlags |= setFfInt);
            }
            if ((clrFf = fdfField.getClearFieldFlags()) == null) return;
            int clrFfValue = clrFf;
            this.setFieldFlags(fieldFlags &= (clrFfValue ^= 0xFFFFFFFF));
        }
    }

    abstract FDFField exportFDF() throws IOException;

    public PDNonTerminalField getParent() {
        return this.parent;
    }

    PDField findKid(String[] name, int nameIndex) {
        PDField retval = null;
        COSArray kids = (COSArray)this.dictionary.getDictionaryObject(COSName.KIDS);
        if (kids != null) {
            for (int i = 0; retval == null && i < kids.size(); ++i) {
                COSDictionary kidDictionary = (COSDictionary)kids.getObject(i);
                if (!name[nameIndex].equals(kidDictionary.getString(COSName.T)) || (retval = PDField.fromDictionary(this.acroForm, kidDictionary, (PDNonTerminalField)this)) == null || name.length <= nameIndex + 1) continue;
                retval = retval.findKid(name, nameIndex + 1);
            }
        }
        return retval;
    }

    public PDAcroForm getAcroForm() {
        return this.acroForm;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public String getPartialName() {
        return this.dictionary.getString(COSName.T);
    }

    public void setPartialName(String name) {
        if (name.contains(".")) {
            throw new IllegalArgumentException("A field partial name shall not contain a period character: " + name);
        }
        this.dictionary.setString(COSName.T, name);
    }

    public String getFullyQualifiedName() {
        String parentName;
        String finalName = this.getPartialName();
        String string = parentName = this.parent != null ? this.parent.getFullyQualifiedName() : null;
        if (parentName != null) {
            finalName = finalName != null ? parentName + "." + finalName : parentName;
        }
        return finalName;
    }

    public String getAlternateFieldName() {
        return this.dictionary.getString(COSName.TU);
    }

    public void setAlternateFieldName(String alternateFieldName) {
        this.dictionary.setString(COSName.TU, alternateFieldName);
    }

    public String getMappingName() {
        return this.dictionary.getString(COSName.TM);
    }

    public void setMappingName(String mappingName) {
        this.dictionary.setString(COSName.TM, mappingName);
    }

    public String toString() {
        return this.getFullyQualifiedName() + "{type: " + this.getClass().getSimpleName() + " value: " + this.getInheritableAttribute(COSName.V) + "}";
    }
}

