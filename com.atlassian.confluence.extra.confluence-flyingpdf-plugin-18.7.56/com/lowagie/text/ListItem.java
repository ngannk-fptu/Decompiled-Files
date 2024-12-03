/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;

public class ListItem
extends Paragraph {
    private static final long serialVersionUID = 1970670787169329006L;
    protected Chunk symbol;

    public ListItem() {
    }

    public ListItem(float leading) {
        super(leading);
    }

    public ListItem(Chunk chunk) {
        super(chunk);
    }

    public ListItem(String string) {
        super(string);
    }

    public ListItem(String string, Font font) {
        super(string, font);
    }

    public ListItem(float leading, Chunk chunk) {
        super(leading, chunk);
    }

    public ListItem(float leading, String string) {
        super(leading, string);
    }

    public ListItem(float leading, String string, Font font) {
        super(leading, string, font);
    }

    public ListItem(Phrase phrase) {
        super(phrase);
    }

    @Override
    public int type() {
        return 15;
    }

    public void setListSymbol(Chunk symbol) {
        if (this.symbol == null) {
            this.symbol = symbol;
            if (this.symbol.getFont().isStandardFont()) {
                this.symbol.setFont(this.font);
            }
        }
    }

    public void setIndentationLeft(float indentation, boolean autoindent) {
        if (autoindent) {
            this.setIndentationLeft(this.getListSymbol().getWidthPoint());
        } else {
            this.setIndentationLeft(indentation);
        }
    }

    public Chunk getListSymbol() {
        return this.symbol;
    }
}

