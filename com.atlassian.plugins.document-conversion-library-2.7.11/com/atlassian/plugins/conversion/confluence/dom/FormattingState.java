/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.words.Font
 *  com.aspose.words.Run
 */
package com.atlassian.plugins.conversion.confluence.dom;

import com.aspose.words.Font;
import com.aspose.words.Run;
import java.awt.Color;
import java.io.IOException;

public class FormattingState {
    private static final String BOLD = "*";
    private static final String ITALIC = "_";
    private static final String STRIKETHROUGH = "-";
    private static final String UNDERLINE = "+";
    private static final String SUPERSCRIPT = "^";
    private static final String SUBSCRIPT = "~";
    private static final String OPEN_MOD = "{";
    private static final String CLOSE_MOD = "}";
    private static Color AUTO = new Color(0, 0, 0, 0);
    private boolean _bold;
    private boolean _italic;
    private boolean _strikethrough;
    private boolean _underline;
    private boolean _superscript;
    private boolean _subscript;
    private Color _color = AUTO;
    private int _trailingSpaces;

    public void processNextRun(StringBuilder out, Run run, boolean firstRun) throws Exception {
        int x;
        boolean subscript;
        boolean superscript;
        boolean underline;
        boolean strikethrough;
        boolean italic;
        String text = run.getText();
        if (text.trim().length() == 0) {
            this.finishBlock(out, true);
            this._trailingSpaces = text.length();
            return;
        }
        boolean leadingSpace = text.charAt(0) == ' ' || firstRun;
        boolean allSpace = false;
        Font font = run.getFont();
        Color newColor = font.getColor();
        if (!this._color.equals(newColor)) {
            this.handleColorChange(out, leadingSpace);
        }
        boolean bold = font.getBold();
        StringBuilder openString = new StringBuilder();
        StringBuilder closeString = new StringBuilder();
        if (bold != this._bold) {
            if (bold) {
                openString.append(BOLD);
            } else {
                closeString.append(BOLD);
            }
        }
        if ((italic = font.getItalic()) != this._italic) {
            if (italic) {
                openString.append(ITALIC);
            } else {
                closeString.append(ITALIC);
            }
        }
        boolean bl = strikethrough = font.getStrikeThrough() || font.getDoubleStrikeThrough();
        if (strikethrough != this._strikethrough) {
            if (strikethrough) {
                openString.append(STRIKETHROUGH);
            } else {
                closeString.append(STRIKETHROUGH);
            }
        }
        boolean bl2 = underline = font.getUnderline() != 0;
        if (underline != this._underline) {
            if (underline) {
                openString.append(UNDERLINE);
            } else {
                closeString.append(UNDERLINE);
            }
        }
        if ((superscript = font.getSuperscript()) != this._superscript) {
            if (superscript) {
                openString.append(SUPERSCRIPT);
            } else {
                closeString.append(SUPERSCRIPT);
            }
        }
        if ((subscript = font.getSubscript()) != this._subscript) {
            if (subscript) {
                openString.append(SUBSCRIPT);
            } else {
                closeString.append(SUBSCRIPT);
            }
        }
        closeString.reverse();
        if (this._trailingSpaces == 0 && !leadingSpace) {
            for (x = 0; x < closeString.length(); ++x) {
                out.append(OPEN_MOD);
                out.append(closeString.charAt(x));
                out.append(CLOSE_MOD);
            }
            if (!newColor.equals(this._color) && this.isNotBlack(newColor)) {
                this.writeColorMacro(out, newColor);
            }
            for (x = 0; x < openString.length(); ++x) {
                out.append(OPEN_MOD);
                out.append(openString.charAt(x));
                out.append(CLOSE_MOD);
            }
        } else {
            out.append((CharSequence)closeString);
            for (x = 0; x < this._trailingSpaces; ++x) {
                out.append(' ');
            }
            for (x = 0; x < text.length() && Character.isWhitespace(text.charAt(x)); ++x) {
                out.append(' ');
            }
            if (x < text.length()) {
                if (!newColor.equals(this._color) && this.isNotBlack(newColor)) {
                    this.writeColorMacro(out, newColor);
                }
                out.append((CharSequence)openString);
            } else {
                allSpace = true;
            }
        }
        if (!allSpace) {
            this.filter(out, text.trim());
            for (x = text.length(); x > 0 && Character.isWhitespace(text.charAt(x - 1)); --x) {
            }
            this._trailingSpaces = text.length() - x;
            this._bold = bold;
            this._italic = italic;
            this._strikethrough = strikethrough;
            this._underline = underline;
            this._subscript = subscript;
            this._superscript = superscript;
        } else {
            this.reset();
        }
        this._color = newColor;
    }

    public void finishBlock(StringBuilder out) throws IOException {
        this.finishBlock(out, true);
    }

    private void writeColorMacro(StringBuilder out, Color newColor) throws IOException {
        String hexStr = Integer.toHexString(newColor.getRGB() & 0xFFFFFF);
        int padding = 6 - hexStr.length();
        for (int c = 0; c < padding; ++c) {
            hexStr = "0" + hexStr;
        }
        out.append("{color:#").append(hexStr).append(CLOSE_MOD);
    }

    private boolean isNotBlack(Color newColor) {
        return (newColor.getRGB() & 0xFFFFFF) != 0;
    }

    public void handleColorChange(StringBuilder out, boolean leadingSpace) throws IOException {
        this.closeProperties(out, leadingSpace);
        this.resetProps();
    }

    public void finishBlock(StringBuilder out, boolean leadingSpace) throws IOException {
        this.closeProperties(out, leadingSpace);
        this.flushTrailingSpaces(out);
        this.reset();
    }

    private void closeProperties(StringBuilder out, boolean leadingSpace) throws IOException {
        StringBuilder closeString = new StringBuilder();
        if (this._bold) {
            closeString.append(BOLD);
        }
        if (this._italic) {
            closeString.append(ITALIC);
        }
        if (this._strikethrough) {
            closeString.append(STRIKETHROUGH);
        }
        if (this._underline) {
            closeString.append(UNDERLINE);
        }
        if (this._superscript) {
            closeString.append(SUPERSCRIPT);
        }
        if (this._subscript) {
            closeString.append(SUBSCRIPT);
        }
        if (closeString.length() > 0) {
            closeString.reverse();
            if (!leadingSpace && this._trailingSpaces == 0) {
                for (int x = 0; x < closeString.length(); ++x) {
                    out.append(OPEN_MOD);
                    out.append(closeString.charAt(x));
                    out.append(CLOSE_MOD);
                }
            } else {
                out.append((CharSequence)closeString);
            }
        }
        if (this.isNotBlack(this._color)) {
            out.append("{color}");
        }
    }

    public void flushTrailingSpaces(StringBuilder out) throws IOException {
        for (int x = 0; x < this._trailingSpaces; ++x) {
            out.append(' ');
        }
        this._trailingSpaces = 0;
    }

    public void filter(StringBuilder out, String text) throws IOException {
        int len = text.length();
        block6: for (int x = 0; x < len; ++x) {
            char ch = text.charAt(x);
            switch (ch) {
                case '\u0001': 
                case '\u0002': 
                case '\u0003': 
                case '\u0004': 
                case '\u0005': 
                case '\u0006': 
                case '\u0007': 
                case '\b': 
                case '\t': 
                case '\n': 
                case '\u000b': 
                case '\f': 
                case '\r': 
                case '\u000e': 
                case '\u000f': 
                case '\u0010': 
                case '\u0011': 
                case '\u0012': 
                case '\u0013': 
                case '\u0014': 
                case '\u0015': 
                case '\u0016': 
                case '\u0017': 
                case '\u0018': 
                case '\u0019': 
                case '\u001a': 
                case '\u001b': 
                case '\u001c': 
                case '\u001d': 
                case '\u001e': 
                case '\u001f': {
                    continue block6;
                }
                case '\u2018': 
                case '\u2019': {
                    out.append('\'');
                    continue block6;
                }
                case '\u201c': 
                case '\u201d': {
                    out.append('\"');
                    continue block6;
                }
                case '[': 
                case ']': 
                case '{': 
                case '}': {
                    out.append('\\');
                    out.append(ch);
                    continue block6;
                }
                default: {
                    out.append(ch);
                }
            }
        }
    }

    public void reset() {
        this._trailingSpaces = 0;
        this.resetProps();
    }

    private void resetProps() {
        this._bold = false;
        this._italic = false;
        this._strikethrough = false;
        this._underline = false;
        this._superscript = false;
        this._subscript = false;
        this._color = AUTO;
    }

    public int getTrailingSpaces() {
        return this._trailingSpaces;
    }

    public void setTrailingSpaces(int spaces) {
        this._trailingSpaces = spaces;
    }
}

