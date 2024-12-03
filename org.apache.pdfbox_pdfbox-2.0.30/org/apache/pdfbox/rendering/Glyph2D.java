/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.rendering;

import java.awt.geom.GeneralPath;
import java.io.IOException;

interface Glyph2D {
    public GeneralPath getPathForCharacterCode(int var1) throws IOException;

    public void dispose();
}

