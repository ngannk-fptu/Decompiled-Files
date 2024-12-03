/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

class AnnotationBorder {
    float[] dashArray = null;
    boolean underline = false;
    float width = 0.0f;

    AnnotationBorder() {
    }

    static AnnotationBorder getAnnotationBorder(PDAnnotation annotation, PDBorderStyleDictionary borderStyle) {
        AnnotationBorder ab = new AnnotationBorder();
        if (borderStyle == null) {
            COSBase base3;
            COSArray border = annotation.getBorder();
            if (border.size() >= 3 && border.getObject(2) instanceof COSNumber) {
                ab.width = ((COSNumber)border.getObject(2)).floatValue();
            }
            if (border.size() > 3 && (base3 = border.getObject(3)) instanceof COSArray) {
                ab.dashArray = ((COSArray)base3).toFloatArray();
            }
        } else {
            ab.width = borderStyle.getWidth();
            if (borderStyle.getStyle().equals("D")) {
                ab.dashArray = borderStyle.getDashStyle().getDashArray();
            }
            if (borderStyle.getStyle().equals("U")) {
                ab.underline = true;
            }
        }
        if (ab.dashArray != null) {
            boolean allZero = true;
            for (float f : ab.dashArray) {
                if (Float.compare(f, 0.0f) == 0) continue;
                allZero = false;
                break;
            }
            if (allZero) {
                ab.dashArray = null;
            }
        }
        return ab;
    }
}

