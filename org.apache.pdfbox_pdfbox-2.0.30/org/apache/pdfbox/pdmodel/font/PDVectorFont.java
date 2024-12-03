/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font;

import java.awt.geom.GeneralPath;
import java.io.IOException;

public interface PDVectorFont {
    public GeneralPath getPath(int var1) throws IOException;

    public boolean hasGlyph(int var1) throws IOException;
}

