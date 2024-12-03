/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDAbstractContentStream;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;

public final class PDPatternContentStream
extends PDAbstractContentStream {
    public PDPatternContentStream(PDTilingPattern pattern) throws IOException {
        super(null, pattern.getContentStream().createOutputStream(), pattern.getResources());
    }
}

