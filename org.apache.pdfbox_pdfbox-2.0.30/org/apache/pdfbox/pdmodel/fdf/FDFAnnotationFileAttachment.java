/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotation;
import org.w3c.dom.Element;

public class FDFAnnotationFileAttachment
extends FDFAnnotation {
    public static final String SUBTYPE = "FileAttachment";

    public FDFAnnotationFileAttachment() {
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }

    public FDFAnnotationFileAttachment(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationFileAttachment(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }
}

