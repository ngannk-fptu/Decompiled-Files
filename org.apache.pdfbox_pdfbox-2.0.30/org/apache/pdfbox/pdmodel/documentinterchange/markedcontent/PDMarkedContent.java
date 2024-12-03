/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.markedcontent;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDArtifactMarkedContent;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.text.TextPosition;

public class PDMarkedContent {
    private final String tag;
    private final COSDictionary properties;
    private final List<Object> contents;

    public static PDMarkedContent create(COSName tag, COSDictionary properties) {
        if (COSName.ARTIFACT.equals(tag)) {
            return new PDArtifactMarkedContent(properties);
        }
        return new PDMarkedContent(tag, properties);
    }

    public PDMarkedContent(COSName tag, COSDictionary properties) {
        this.tag = tag == null ? null : tag.getName();
        this.properties = properties;
        this.contents = new ArrayList<Object>();
    }

    public String getTag() {
        return this.tag;
    }

    public COSDictionary getProperties() {
        return this.properties;
    }

    public int getMCID() {
        return this.getProperties() == null ? -1 : this.getProperties().getInt(COSName.MCID);
    }

    public String getLanguage() {
        return this.getProperties() == null ? null : this.getProperties().getNameAsString(COSName.LANG);
    }

    public String getActualText() {
        return this.getProperties() == null ? null : this.getProperties().getString(COSName.ACTUAL_TEXT);
    }

    public String getAlternateDescription() {
        return this.getProperties() == null ? null : this.getProperties().getString(COSName.ALT);
    }

    public String getExpandedForm() {
        return this.getProperties() == null ? null : this.getProperties().getString(COSName.E);
    }

    public List<Object> getContents() {
        return this.contents;
    }

    public void addText(TextPosition text) {
        this.getContents().add(text);
    }

    public void addMarkedContent(PDMarkedContent markedContent) {
        this.getContents().add(markedContent);
    }

    public void addXObject(PDXObject xobject) {
        this.getContents().add(xobject);
    }

    public String toString() {
        return "tag=" + this.tag + ", properties=" + this.properties + ", contents=" + this.contents;
    }
}

