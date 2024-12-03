/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotation;
import org.w3c.dom.Element;

public class FDFAnnotationText
extends FDFAnnotation {
    public static final String SUBTYPE = "Text";

    public FDFAnnotationText() {
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }

    public FDFAnnotationText(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationText(Element element) throws IOException {
        super(element);
        String statemodel;
        String state;
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
        String icon = element.getAttribute("icon");
        if (icon != null && !icon.isEmpty()) {
            this.setIcon(element.getAttribute("icon"));
        }
        if ((state = element.getAttribute("state")) != null && !state.isEmpty() && (statemodel = element.getAttribute("statemodel")) != null && !statemodel.isEmpty()) {
            this.setState(element.getAttribute("state"));
            this.setStateModel(element.getAttribute("statemodel"));
        }
    }

    public void setIcon(String icon) {
        this.annot.setName(COSName.NAME, icon);
    }

    public String getIcon() {
        return this.annot.getNameAsString(COSName.NAME, "Note");
    }

    public String getState() {
        return this.annot.getString(COSName.STATE);
    }

    public void setState(String state) {
        this.annot.setString(COSName.STATE, state);
    }

    public String getStateModel() {
        return this.annot.getString(COSName.STATE_MODEL);
    }

    public void setStateModel(String stateModel) {
        this.annot.setString(COSName.STATE_MODEL, stateModel);
    }
}

