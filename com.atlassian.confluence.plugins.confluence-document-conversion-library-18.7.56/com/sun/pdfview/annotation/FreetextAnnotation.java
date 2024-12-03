/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.annotation;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.annotation.PDFAnnotation;
import com.sun.pdfview.annotation.StampAnnotation;
import java.io.IOException;

public class FreetextAnnotation
extends StampAnnotation {
    public FreetextAnnotation(PDFObject annotObject) throws IOException {
        super(annotObject, PDFAnnotation.ANNOTATION_TYPE.FREETEXT);
    }
}

