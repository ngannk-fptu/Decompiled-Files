/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import java.util.HashMap;

public class Footnote
extends Phrase {
    public static final int TEXT = 0;
    public static final String CONTENT = "content";
    public static final String FONT = "font";
    public static final String DESTINATION = "destination";
    public static final String PAGE = "page";
    public static final String NAMED = "named";
    protected int footnoteType;
    protected HashMap footnoteAttributes = new HashMap();

    public Footnote() {
    }

    public Footnote(Chunk chunk) {
        super(chunk);
    }

    public Footnote(String text, Font font) {
        super(text, font);
    }

    public Footnote(String text) {
        super(text);
    }

    @Override
    public int type() {
        return 56;
    }

    public int footnoteType() {
        return this.footnoteType;
    }
}

