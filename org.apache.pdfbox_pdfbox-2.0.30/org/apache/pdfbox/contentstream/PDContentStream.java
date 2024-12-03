/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream;

import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

public interface PDContentStream {
    public InputStream getContents() throws IOException;

    public PDResources getResources();

    public PDRectangle getBBox();

    public Matrix getMatrix();
}

