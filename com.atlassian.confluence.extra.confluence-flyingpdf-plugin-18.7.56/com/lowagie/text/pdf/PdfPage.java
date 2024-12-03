/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfRectangle;
import java.util.HashMap;

public class PdfPage
extends PdfDictionary {
    private static final String[] boxStrings = new String[]{"crop", "trim", "art", "bleed"};
    private static final PdfName[] boxNames = new PdfName[]{PdfName.CROPBOX, PdfName.TRIMBOX, PdfName.ARTBOX, PdfName.BLEEDBOX};
    public static final PdfNumber PORTRAIT = new PdfNumber(0);
    public static final PdfNumber LANDSCAPE = new PdfNumber(90);
    public static final PdfNumber INVERTEDPORTRAIT = new PdfNumber(180);
    public static final PdfNumber SEASCAPE = new PdfNumber(270);
    PdfRectangle mediaBox;

    PdfPage(PdfRectangle mediaBox, HashMap<String, ? extends PdfObject> boxSize, PdfDictionary resources, int rotate) {
        super(PAGE);
        this.mediaBox = mediaBox;
        this.put(PdfName.MEDIABOX, mediaBox);
        this.put(PdfName.RESOURCES, resources);
        if (rotate != 0) {
            this.put(PdfName.ROTATE, new PdfNumber(rotate));
        }
        for (int k = 0; k < boxStrings.length; ++k) {
            PdfObject rect = boxSize.get(boxStrings[k]);
            if (rect == null) continue;
            this.put(boxNames[k], rect);
        }
    }

    PdfPage(PdfRectangle mediaBox, HashMap<String, ? extends PdfObject> boxSize, PdfDictionary resources) {
        this(mediaBox, boxSize, resources, 0);
    }

    public boolean isParent() {
        return false;
    }

    void add(PdfIndirectReference contents) {
        this.put(PdfName.CONTENTS, contents);
    }

    PdfRectangle rotateMediaBox() {
        this.mediaBox = this.mediaBox.rotate();
        this.put(PdfName.MEDIABOX, this.mediaBox);
        return this.mediaBox;
    }

    PdfRectangle getMediaBox() {
        return this.mediaBox;
    }
}

