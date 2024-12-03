/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Utilities;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BaseFont;
import java.util.ArrayList;

public class FontSelector {
    protected ArrayList<Font> fonts = new ArrayList();

    public void addFont(Font font) {
        if (font.getBaseFont() != null) {
            this.fonts.add(font);
            return;
        }
        BaseFont bf = font.getCalculatedBaseFont(true);
        Font f2 = new Font(bf, font.getSize(), font.getCalculatedStyle(), font.getColor());
        this.fonts.add(f2);
    }

    public Phrase process(String text) {
        int fsize = this.fonts.size();
        if (fsize == 0) {
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("no.font.is.defined"));
        }
        char[] cc = text.toCharArray();
        int len = cc.length;
        StringBuilder sb = new StringBuilder();
        Font font = null;
        int lastidx = -1;
        Phrase ret = new Phrase();
        block0: for (int k = 0; k < len; ++k) {
            char c = cc[k];
            if (c == '\n' || c == '\r') {
                sb.append(c);
                continue;
            }
            if (Utilities.isSurrogatePair(cc, k)) {
                int u = Utilities.convertToUtf32(cc, k);
                for (int f = 0; f < fsize; ++f) {
                    font = this.fonts.get(f);
                    if (!font.getBaseFont().charExists(u)) continue;
                    if (lastidx != f) {
                        if (sb.length() > 0 && lastidx != -1) {
                            Chunk ck = new Chunk(sb.toString(), this.fonts.get(lastidx));
                            ret.add(ck);
                            sb.setLength(0);
                        }
                        lastidx = f;
                    }
                    sb.append(c);
                    if (cc.length <= k + 1) continue block0;
                    sb.append(cc[++k]);
                    continue block0;
                }
                continue;
            }
            for (int f = 0; f < fsize; ++f) {
                font = this.fonts.get(f);
                if (!font.getBaseFont().charExists(c)) continue;
                if (lastidx != f) {
                    if (sb.length() > 0 && lastidx != -1) {
                        Chunk ck = new Chunk(sb.toString(), this.fonts.get(lastidx));
                        ret.add(ck);
                        sb.setLength(0);
                    }
                    lastidx = f;
                }
                sb.append(c);
                continue block0;
            }
        }
        if (sb.length() > 0) {
            Chunk ck = new Chunk(sb.toString(), this.fonts.get(lastidx == -1 ? 0 : lastidx));
            ret.add(ck);
        }
        return ret;
    }
}

