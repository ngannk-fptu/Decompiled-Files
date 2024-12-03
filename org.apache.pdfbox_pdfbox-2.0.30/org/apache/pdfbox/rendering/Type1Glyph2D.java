/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.rendering;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.rendering.Glyph2D;

final class Type1Glyph2D
implements Glyph2D {
    private static final Log LOG = LogFactory.getLog(Type1Glyph2D.class);
    private final Map<Integer, GeneralPath> cache = new HashMap<Integer, GeneralPath>();
    private final PDSimpleFont font;

    Type1Glyph2D(PDSimpleFont font) {
        this.font = font;
    }

    @Override
    public GeneralPath getPathForCharacterCode(int code) {
        GeneralPath path = this.cache.get(code);
        if (path == null) {
            try {
                String name = this.font.getEncoding().getName(code);
                if (!this.font.hasGlyph(name)) {
                    String uniName;
                    LOG.warn((Object)("No glyph for code " + code + " (" + name + ") in font " + this.font.getName()));
                    if (code == 10 && this.font.isStandard14()) {
                        path = new GeneralPath();
                        this.cache.put(code, path);
                        return path;
                    }
                    String unicodes = this.font.getGlyphList().toUnicode(name);
                    if (unicodes != null && unicodes.length() == 1 && this.font.hasGlyph(uniName = Type1Glyph2D.getUniNameOfCodePoint(unicodes.codePointAt(0)))) {
                        name = uniName;
                    }
                }
                if ((path = this.font.getPath(name)) == null) {
                    path = this.font.getPath(".notdef");
                }
                this.cache.put(code, path);
                return path;
            }
            catch (IOException e) {
                LOG.error((Object)"Glyph rendering failed", (Throwable)e);
                path = new GeneralPath();
            }
        }
        return path;
    }

    @Override
    public void dispose() {
        this.cache.clear();
    }

    private static String getUniNameOfCodePoint(int codePoint) {
        String hex = Integer.toString(codePoint, 16).toUpperCase(Locale.US);
        switch (hex.length()) {
            case 1: {
                return "uni000" + hex;
            }
            case 2: {
                return "uni00" + hex;
            }
            case 3: {
                return "uni0" + hex;
            }
        }
        return "uni" + hex;
    }
}

