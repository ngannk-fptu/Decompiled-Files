/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDStandardAttributeObject;

public class PDPrintFieldAttributeObject
extends PDStandardAttributeObject {
    public static final String OWNER_PRINT_FIELD = "PrintField";
    private static final String ROLE = "Role";
    private static final String CHECKED = "checked";
    private static final String DESC = "Desc";
    public static final String ROLE_RB = "rb";
    public static final String ROLE_CB = "cb";
    public static final String ROLE_PB = "pb";
    public static final String ROLE_TV = "tv";
    public static final String CHECKED_STATE_ON = "on";
    public static final String CHECKED_STATE_OFF = "off";
    public static final String CHECKED_STATE_NEUTRAL = "neutral";

    public PDPrintFieldAttributeObject() {
        this.setOwner(OWNER_PRINT_FIELD);
    }

    public PDPrintFieldAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public String getRole() {
        return this.getName(ROLE);
    }

    public void setRole(String role) {
        this.setName(ROLE, role);
    }

    public String getCheckedState() {
        return this.getName(CHECKED, CHECKED_STATE_OFF);
    }

    public void setCheckedState(String checkedState) {
        this.setName(CHECKED, checkedState);
    }

    public String getAlternateName() {
        return this.getString(DESC);
    }

    public void setAlternateName(String alternateName) {
        this.setString(DESC, alternateName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(super.toString());
        if (this.isSpecified(ROLE)) {
            sb.append(", Role=").append(this.getRole());
        }
        if (this.isSpecified(CHECKED)) {
            sb.append(", Checked=").append(this.getCheckedState());
        }
        if (this.isSpecified(DESC)) {
            sb.append(", Desc=").append(this.getAlternateName());
        }
        return sb.toString();
    }
}

