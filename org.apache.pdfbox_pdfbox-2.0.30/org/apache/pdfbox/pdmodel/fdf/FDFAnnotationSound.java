/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotation;
import org.w3c.dom.Element;

public class FDFAnnotationSound
extends FDFAnnotation {
    public static final String SUBTYPE = "Sound";

    public FDFAnnotationSound() {
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }

    public FDFAnnotationSound(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationSound(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }
}

