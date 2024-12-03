/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.Rule;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.util.ParsedURL;

public class FontFaceRule
implements Rule {
    public static final short TYPE = 3;
    StyleMap sm;
    ParsedURL purl;

    public FontFaceRule(StyleMap sm, ParsedURL purl) {
        this.sm = sm;
        this.purl = purl;
    }

    @Override
    public short getType() {
        return 3;
    }

    public ParsedURL getURL() {
        return this.purl;
    }

    public StyleMap getStyleMap() {
        return this.sm;
    }

    @Override
    public String toString(CSSEngine eng) {
        StringBuffer sb = new StringBuffer();
        sb.append("@font-face { ");
        sb.append(this.sm.toString(eng));
        sb.append(" }\n");
        return sb.toString();
    }
}

