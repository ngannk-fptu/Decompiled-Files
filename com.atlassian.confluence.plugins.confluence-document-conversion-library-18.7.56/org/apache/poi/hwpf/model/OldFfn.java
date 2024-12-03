/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.nio.charset.Charset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.util.Internal;
import org.apache.poi.util.StringUtil;

@Internal
public final class OldFfn {
    private static final Logger LOG = LogManager.getLogger(OldFfn.class);
    private final byte _chs;
    private final String fontName;
    private final String altFontName;
    private final int length;

    static OldFfn build(byte[] buf, int offset, int fontTableEnd) {
        short fontDescriptionLength;
        int start = offset;
        if (offset + 6 > fontTableEnd) {
            return null;
        }
        if (++offset + (fontDescriptionLength = (short)buf[offset]) > fontTableEnd) {
            LOG.atWarn().log("Asked to read beyond font table end. Skipping font");
            return null;
        }
        byte chs = buf[offset += 3];
        Charset charset = null;
        FontCharset wmfCharset = FontCharset.valueOf(chs & 0xFF);
        if (wmfCharset == null) {
            LOG.atWarn().log("Couldn't find font for type: {}", (Object)Unbox.box(chs & 0xFF));
        } else {
            charset = wmfCharset.getCharset();
        }
        charset = charset == null ? StringUtil.WIN_1252 : charset;
        ++offset;
        int fontNameLength = -1;
        for (int i = ++offset; i < fontTableEnd; ++i) {
            if (buf[i] != 0) continue;
            fontNameLength = i - offset;
            break;
        }
        if (fontNameLength == -1) {
            LOG.atWarn().log("Couldn't find the zero-byte delimited font name length");
            return null;
        }
        String fontName = new String(buf, offset, fontNameLength, charset);
        String altFontName = null;
        int altFontNameLength = -1;
        if ((offset += fontNameLength + 1) - start < fontDescriptionLength) {
            for (int i = offset; i <= start + fontDescriptionLength; ++i) {
                if (buf[i] != 0) continue;
                altFontNameLength = i - offset;
                break;
            }
            if (altFontNameLength > -1) {
                altFontName = new String(buf, offset, altFontNameLength, charset);
            }
        }
        altFontNameLength = altFontNameLength < 0 ? 0 : altFontNameLength + 1;
        int len = 6 + fontNameLength + altFontNameLength + 1;
        return new OldFfn(chs, fontName, altFontName, len);
    }

    public OldFfn(byte charsetIdentifier, String fontName, String altFontName, int length) {
        this._chs = charsetIdentifier;
        this.fontName = fontName;
        this.altFontName = altFontName;
        this.length = length;
    }

    public byte getChs() {
        return this._chs;
    }

    public String getMainFontName() {
        return this.fontName;
    }

    public String getAltFontName() {
        return this.altFontName;
    }

    public int getLength() {
        return this.length;
    }

    public String toString() {
        return "OldFfn{_chs=" + (this._chs & 0xFF) + ", fontName='" + this.fontName + '\'' + ", altFontName='" + this.altFontName + '\'' + ", length=" + this.length + '}';
    }
}

