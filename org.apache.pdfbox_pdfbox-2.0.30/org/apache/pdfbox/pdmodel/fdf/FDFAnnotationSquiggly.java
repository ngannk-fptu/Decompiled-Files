/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationTextMarkup;
import org.w3c.dom.Element;

public class FDFAnnotationSquiggly
extends FDFAnnotationTextMarkup {
    public static final String SUBTYPE = "Squiggly";

    public FDFAnnotationSquiggly() {
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }

    public FDFAnnotationSquiggly(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationSquiggly(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }
}

