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
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType0;
import org.apache.pdfbox.rendering.Glyph2D;

final class CIDType0Glyph2D
implements Glyph2D {
    private static final Log LOG = LogFactory.getLog(CIDType0Glyph2D.class);
    private final Map<Integer, GeneralPath> cache = new HashMap<Integer, GeneralPath>();
    private final PDCIDFontType0 font;
    private final String fontName;

    CIDType0Glyph2D(PDCIDFontType0 font) {
        this.font = font;
        this.fontName = font.getBaseFont();
    }

    @Override
    public GeneralPath getPathForCharacterCode(int code) {
        GeneralPath path = this.cache.get(code);
        if (path == null) {
            try {
                if (!this.font.hasGlyph(code)) {
                    int cid = this.font.getParent().codeToCID(code);
                    String cidHex = String.format("%04x", cid);
                    LOG.warn((Object)("No glyph for " + code + " (CID " + cidHex + ") in font " + this.fontName));
                }
                path = this.font.getPath(code);
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
}

