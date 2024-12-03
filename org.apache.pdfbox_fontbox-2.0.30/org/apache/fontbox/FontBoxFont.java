/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.List;
import org.apache.fontbox.util.BoundingBox;

public interface FontBoxFont {
    public String getName() throws IOException;

    public BoundingBox getFontBBox() throws IOException;

    public List<Number> getFontMatrix() throws IOException;

    public GeneralPath getPath(String var1) throws IOException;

    public float getWidth(String var1) throws IOException;

    public boolean hasGlyph(String var1) throws IOException;
}

