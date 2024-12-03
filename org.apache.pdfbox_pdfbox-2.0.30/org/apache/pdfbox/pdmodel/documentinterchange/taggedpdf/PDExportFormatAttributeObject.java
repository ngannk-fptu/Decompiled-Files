/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDLayoutAttributeObject;

public class PDExportFormatAttributeObject
extends PDLayoutAttributeObject {
    public static final String OWNER_XML_1_00 = "XML-1.00";
    public static final String OWNER_HTML_3_20 = "HTML-3.2";
    public static final String OWNER_HTML_4_01 = "HTML-4.01";
    public static final String OWNER_OEB_1_00 = "OEB-1.00";
    public static final String OWNER_RTF_1_05 = "RTF-1.05";
    public static final String OWNER_CSS_1_00 = "CSS-1.00";
    public static final String OWNER_CSS_2_00 = "CSS-2.00";

    public PDExportFormatAttributeObject(String owner) {
        this.setOwner(owner);
    }

    public PDExportFormatAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public String getListNumbering() {
        return this.getName("ListNumbering", "None");
    }

    public void setListNumbering(String listNumbering) {
        this.setName("ListNumbering", listNumbering);
    }

    public int getRowSpan() {
        return this.getInteger("RowSpan", 1);
    }

    public void setRowSpan(int rowSpan) {
        this.setInteger("RowSpan", rowSpan);
    }

    public int getColSpan() {
        return this.getInteger("ColSpan", 1);
    }

    public void setColSpan(int colSpan) {
        this.setInteger("ColSpan", colSpan);
    }

    public String[] getHeaders() {
        return this.getArrayOfString("Headers");
    }

    public void setHeaders(String[] headers) {
        this.setArrayOfString("Headers", headers);
    }

    public String getScope() {
        return this.getName("Scope");
    }

    public void setScope(String scope) {
        this.setName("Scope", scope);
    }

    public String getSummary() {
        return this.getString("Summary");
    }

    public void setSummary(String summary) {
        this.setString("Summary", summary);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(super.toString());
        if (this.isSpecified("ListNumbering")) {
            sb.append(", ListNumbering=").append(this.getListNumbering());
        }
        if (this.isSpecified("RowSpan")) {
            sb.append(", RowSpan=").append(this.getRowSpan());
        }
        if (this.isSpecified("ColSpan")) {
            sb.append(", ColSpan=").append(this.getColSpan());
        }
        if (this.isSpecified("Headers")) {
            sb.append(", Headers=").append(PDExportFormatAttributeObject.arrayToString(this.getHeaders()));
        }
        if (this.isSpecified("Scope")) {
            sb.append(", Scope=").append(this.getScope());
        }
        if (this.isSpecified("Summary")) {
            sb.append(", Summary=").append(this.getSummary());
        }
        return sb.toString();
    }
}

