/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDStandardAttributeObject;

public class PDListAttributeObject
extends PDStandardAttributeObject {
    public static final String OWNER_LIST = "List";
    protected static final String LIST_NUMBERING = "ListNumbering";
    public static final String LIST_NUMBERING_CIRCLE = "Circle";
    public static final String LIST_NUMBERING_DECIMAL = "Decimal";
    public static final String LIST_NUMBERING_DISC = "Disc";
    public static final String LIST_NUMBERING_LOWER_ALPHA = "LowerAlpha";
    public static final String LIST_NUMBERING_LOWER_ROMAN = "LowerRoman";
    public static final String LIST_NUMBERING_NONE = "None";
    public static final String LIST_NUMBERING_SQUARE = "Square";
    public static final String LIST_NUMBERING_UPPER_ALPHA = "UpperAlpha";
    public static final String LIST_NUMBERING_UPPER_ROMAN = "UpperRoman";

    public PDListAttributeObject() {
        this.setOwner(OWNER_LIST);
    }

    public PDListAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public String getListNumbering() {
        return this.getName(LIST_NUMBERING, LIST_NUMBERING_NONE);
    }

    public void setListNumbering(String listNumbering) {
        this.setName(LIST_NUMBERING, listNumbering);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(super.toString());
        if (this.isSpecified(LIST_NUMBERING)) {
            sb.append(", ListNumbering=").append(this.getListNumbering());
        }
        return sb.toString();
    }
}

