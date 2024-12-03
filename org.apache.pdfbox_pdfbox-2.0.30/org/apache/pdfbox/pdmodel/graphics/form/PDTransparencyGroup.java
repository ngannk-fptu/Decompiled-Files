/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.form;

import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;

public class PDTransparencyGroup
extends PDFormXObject {
    public PDTransparencyGroup(PDStream stream) {
        super(stream);
    }

    public PDTransparencyGroup(COSStream stream, ResourceCache cache) {
        super(stream, cache);
    }

    public PDTransparencyGroup(PDDocument document) {
        super(document);
    }
}

