/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;

public class PDArtifactMarkedContent
extends PDMarkedContent {
    public PDArtifactMarkedContent(COSDictionary properties) {
        super(COSName.ARTIFACT, properties);
    }

    public String getType() {
        return this.getProperties().getNameAsString(COSName.TYPE);
    }

    public PDRectangle getBBox() {
        PDRectangle retval = null;
        COSArray a = (COSArray)this.getProperties().getDictionaryObject(COSName.BBOX);
        if (a != null) {
            retval = new PDRectangle(a);
        }
        return retval;
    }

    public boolean isTopAttached() {
        return this.isAttached("Top");
    }

    public boolean isBottomAttached() {
        return this.isAttached("Bottom");
    }

    public boolean isLeftAttached() {
        return this.isAttached("Left");
    }

    public boolean isRightAttached() {
        return this.isAttached("Right");
    }

    public String getSubtype() {
        return this.getProperties().getNameAsString(COSName.SUBTYPE);
    }

    private boolean isAttached(String edge) {
        COSArray a = (COSArray)this.getProperties().getDictionaryObject(COSName.ATTACHED);
        if (a != null) {
            for (int i = 0; i < a.size(); ++i) {
                if (!edge.equals(a.getName(i))) continue;
                return true;
            }
        }
        return false;
    }
}

