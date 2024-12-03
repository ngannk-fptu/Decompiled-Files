/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.dom;

import java.awt.Color;

public class TextFormat
implements Cloneable {
    private boolean _bold;
    private boolean _emphasis;
    private boolean _citation;
    private boolean _underline;
    private boolean _superscript;
    private boolean _subscript;
    private boolean _strikethrough;
    private boolean _mono;
    private Color _color;

    public boolean isBold() {
        return this._bold;
    }

    public TextFormat setBold(boolean bold) {
        TextFormat newFormat = null;
        try {
            newFormat = (TextFormat)this.clone();
            newFormat._bold = bold;
        }
        catch (Exception exception) {
            // empty catch block
        }
        return newFormat;
    }

    public boolean isCitation() {
        return this._citation;
    }

    public TextFormat setCitation(boolean citation) {
        TextFormat newFormat = null;
        try {
            newFormat = (TextFormat)this.clone();
            newFormat._citation = citation;
        }
        catch (Exception exception) {
            // empty catch block
        }
        return newFormat;
    }

    public boolean isEmphasis() {
        return this._emphasis;
    }

    public TextFormat setEmphasis(boolean emphasis) {
        TextFormat newFormat = null;
        try {
            newFormat = (TextFormat)this.clone();
            newFormat._emphasis = emphasis;
        }
        catch (Exception exception) {
            // empty catch block
        }
        return newFormat;
    }

    public boolean isMono() {
        return this._mono;
    }

    public TextFormat setMono(boolean mono) {
        TextFormat newFormat = null;
        try {
            newFormat = (TextFormat)this.clone();
            newFormat._mono = mono;
        }
        catch (Exception exception) {
            // empty catch block
        }
        return newFormat;
    }

    public boolean isStrikethrough() {
        return this._strikethrough;
    }

    public TextFormat setStrikethrough(boolean strikethrough) {
        TextFormat newFormat = null;
        try {
            newFormat = (TextFormat)this.clone();
            newFormat._strikethrough = strikethrough;
        }
        catch (Exception exception) {
            // empty catch block
        }
        return newFormat;
    }

    public boolean isSubscript() {
        return this._subscript;
    }

    public TextFormat setSubscript(boolean subscript) {
        TextFormat newFormat = null;
        try {
            newFormat = (TextFormat)this.clone();
            newFormat._subscript = subscript;
        }
        catch (Exception exception) {
            // empty catch block
        }
        return newFormat;
    }

    public boolean isSuperscript() {
        return this._superscript;
    }

    public TextFormat setSuperscript(boolean superscript) {
        TextFormat newFormat = null;
        try {
            newFormat = (TextFormat)this.clone();
            newFormat._superscript = superscript;
        }
        catch (Exception exception) {
            // empty catch block
        }
        return newFormat;
    }

    public boolean isUnderline() {
        return this._underline;
    }

    public TextFormat setUnderline(boolean underline) {
        TextFormat newFormat = null;
        try {
            newFormat = (TextFormat)this.clone();
            newFormat._underline = underline;
        }
        catch (Exception exception) {
            // empty catch block
        }
        return newFormat;
    }

    public TextFormat setColor(Color color) {
        TextFormat newFormat = null;
        try {
            newFormat = (TextFormat)this.clone();
            newFormat._color = color;
        }
        catch (Exception exception) {
            // empty catch block
        }
        return newFormat;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Color getColor() {
        return this._color;
    }
}

