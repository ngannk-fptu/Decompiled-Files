/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.font.GVTFontFamily
 */
package org.apache.batik.bridge;

import java.io.InputStream;
import org.apache.batik.bridge.FontFace;
import org.apache.batik.gvt.font.GVTFontFamily;

public interface FontFamilyResolver {
    public GVTFontFamily resolve(String var1);

    public GVTFontFamily resolve(String var1, FontFace var2);

    public GVTFontFamily loadFont(InputStream var1, FontFace var2) throws Exception;

    public GVTFontFamily getDefault();

    public GVTFontFamily getFamilyThatCanDisplay(char var1);
}

