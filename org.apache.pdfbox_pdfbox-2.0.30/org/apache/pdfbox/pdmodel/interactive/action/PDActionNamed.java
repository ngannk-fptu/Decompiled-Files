/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;

public class PDActionNamed
extends PDAction {
    public static final String SUB_TYPE = "Named";

    public PDActionNamed() {
        this.setSubType(SUB_TYPE);
    }

    public PDActionNamed(COSDictionary a) {
        super(a);
    }

    public String getN() {
        return this.action.getNameAsString("N");
    }

    public void setN(String name) {
        this.action.setName("N", name);
    }
}

