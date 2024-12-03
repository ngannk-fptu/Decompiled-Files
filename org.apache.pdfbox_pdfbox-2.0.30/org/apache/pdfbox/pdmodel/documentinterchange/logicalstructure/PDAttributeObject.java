/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDDictionaryWrapper;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDDefaultAttributeObject;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDUserAttributeObject;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDExportFormatAttributeObject;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDLayoutAttributeObject;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDListAttributeObject;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDPrintFieldAttributeObject;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDTableAttributeObject;

public abstract class PDAttributeObject
extends PDDictionaryWrapper {
    private PDStructureElement structureElement;

    public PDAttributeObject() {
    }

    public PDAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public static PDAttributeObject create(COSDictionary dictionary) {
        String owner = dictionary.getNameAsString(COSName.O);
        if ("UserProperties".equals(owner)) {
            return new PDUserAttributeObject(dictionary);
        }
        if ("List".equals(owner)) {
            return new PDListAttributeObject(dictionary);
        }
        if ("PrintField".equals(owner)) {
            return new PDPrintFieldAttributeObject(dictionary);
        }
        if ("Table".equals(owner)) {
            return new PDTableAttributeObject(dictionary);
        }
        if ("Layout".equals(owner)) {
            return new PDLayoutAttributeObject(dictionary);
        }
        if ("XML-1.00".equals(owner) || "HTML-3.2".equals(owner) || "HTML-4.01".equals(owner) || "OEB-1.00".equals(owner) || "RTF-1.05".equals(owner) || "CSS-1.00".equals(owner) || "CSS-2.00".equals(owner)) {
            return new PDExportFormatAttributeObject(dictionary);
        }
        return new PDDefaultAttributeObject(dictionary);
    }

    private PDStructureElement getStructureElement() {
        return this.structureElement;
    }

    protected void setStructureElement(PDStructureElement structureElement) {
        this.structureElement = structureElement;
    }

    public String getOwner() {
        return this.getCOSObject().getNameAsString(COSName.O);
    }

    protected void setOwner(String owner) {
        this.getCOSObject().setName(COSName.O, owner);
    }

    public boolean isEmpty() {
        return this.getCOSObject().size() == 1 && this.getOwner() != null;
    }

    protected void potentiallyNotifyChanged(COSBase oldBase, COSBase newBase) {
        if (this.isValueChanged(oldBase, newBase)) {
            this.notifyChanged();
        }
    }

    private boolean isValueChanged(COSBase oldValue, COSBase newValue) {
        if (oldValue == null) {
            return newValue != null;
        }
        return !oldValue.equals(newValue);
    }

    protected void notifyChanged() {
        if (this.getStructureElement() != null) {
            this.getStructureElement().attributeChanged(this);
        }
    }

    public String toString() {
        return "O=" + this.getOwner();
    }

    protected static String arrayToString(Object[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(array[i]);
        }
        return sb.append(']').toString();
    }

    protected static String arrayToString(float[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(array[i]);
        }
        return sb.append(']').toString();
    }
}

