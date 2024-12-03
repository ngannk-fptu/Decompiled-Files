/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDAbstractContentStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;

public final class PDFormContentStream
extends PDAbstractContentStream {
    public PDFormContentStream(PDFormXObject form) throws IOException {
        super(null, form.getContentStream().createOutputStream(), form.getResources());
    }
}

