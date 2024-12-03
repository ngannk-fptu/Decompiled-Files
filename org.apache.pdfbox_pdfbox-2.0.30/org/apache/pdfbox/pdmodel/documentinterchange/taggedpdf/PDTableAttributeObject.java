/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDStandardAttributeObject;

public class PDTableAttributeObject
extends PDStandardAttributeObject {
    public static final String OWNER_TABLE = "Table";
    protected static final String ROW_SPAN = "RowSpan";
    protected static final String COL_SPAN = "ColSpan";
    protected static final String HEADERS = "Headers";
    protected static final String SCOPE = "Scope";
    protected static final String SUMMARY = "Summary";
    public static final String SCOPE_BOTH = "Both";
    public static final String SCOPE_COLUMN = "Column";
    public static final String SCOPE_ROW = "Row";

    public PDTableAttributeObject() {
        this.setOwner(OWNER_TABLE);
    }

    public PDTableAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public int getRowSpan() {
        return this.getInteger(ROW_SPAN, 1);
    }

    public void setRowSpan(int rowSpan) {
        this.setInteger(ROW_SPAN, rowSpan);
    }

    public int getColSpan() {
        return this.getInteger(COL_SPAN, 1);
    }

    public void setColSpan(int colSpan) {
        this.setInteger(COL_SPAN, colSpan);
    }

    public String[] getHeaders() {
        return this.getArrayOfString(HEADERS);
    }

    public void setHeaders(String[] headers) {
        this.setArrayOfString(HEADERS, headers);
    }

    public String getScope() {
        return this.getName(SCOPE);
    }

    public void setScope(String scope) {
        this.setName(SCOPE, scope);
    }

    public String getSummary() {
        return this.getString(SUMMARY);
    }

    public void setSummary(String summary) {
        this.setString(SUMMARY, summary);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(super.toString());
        if (this.isSpecified(ROW_SPAN)) {
            sb.append(", RowSpan=").append(this.getRowSpan());
        }
        if (this.isSpecified(COL_SPAN)) {
            sb.append(", ColSpan=").append(this.getColSpan());
        }
        if (this.isSpecified(HEADERS)) {
            sb.append(", Headers=").append(PDTableAttributeObject.arrayToString(this.getHeaders()));
        }
        if (this.isSpecified(SCOPE)) {
            sb.append(", Scope=").append(this.getScope());
        }
        if (this.isSpecified(SUMMARY)) {
            sb.append(", Summary=").append(this.getSummary());
        }
        return sb.toString();
    }
}

