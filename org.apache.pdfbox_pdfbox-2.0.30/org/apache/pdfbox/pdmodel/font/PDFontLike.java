/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.fontbox.util.BoundingBox
 */
package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

public interface PDFontLike {
    public String getName();

    public PDFontDescriptor getFontDescriptor();

    public Matrix getFontMatrix();

    public BoundingBox getBoundingBox() throws IOException;

    public Vector getPositionVector(int var1);

    @Deprecated
    public float getHeight(int var1) throws IOException;

    public float getWidth(int var1) throws IOException;

    public boolean hasExplicitWidth(int var1) throws IOException;

    public float getWidthFromFont(int var1) throws IOException;

    public boolean isEmbedded();

    public boolean isDamaged();

    public float getAverageFontWidth();
}

