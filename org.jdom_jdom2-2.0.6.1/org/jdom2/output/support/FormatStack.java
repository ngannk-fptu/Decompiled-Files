/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import org.jdom2.internal.ArrayCopy;
import org.jdom2.output.EscapeStrategy;
import org.jdom2.output.Format;

public final class FormatStack {
    private int capacity = 16;
    private int depth = 0;
    private final Format.TextMode defaultMode;
    private final String indent;
    private final String encoding;
    private final String lineSeparator;
    private final boolean omitDeclaration;
    private final boolean omitEncoding;
    private final boolean expandEmptyElements;
    private final boolean specifiedAttributesOnly;
    private final EscapeStrategy escapeStrategy;
    private String[] levelIndent = new String[this.capacity];
    private String[] levelEOL = new String[this.capacity];
    private String[] levelEOLIndent = new String[this.capacity];
    private String[] termEOLIndent = new String[this.capacity];
    private boolean[] ignoreTrAXEscapingPIs = new boolean[this.capacity];
    private Format.TextMode[] mode = new Format.TextMode[this.capacity];
    private boolean[] escapeOutput = new boolean[this.capacity];

    public FormatStack(Format format) {
        this.indent = format.getIndent();
        this.lineSeparator = format.getLineSeparator();
        this.encoding = format.getEncoding();
        this.omitDeclaration = format.getOmitDeclaration();
        this.omitEncoding = format.getOmitEncoding();
        this.expandEmptyElements = format.getExpandEmptyElements();
        this.escapeStrategy = format.getEscapeStrategy();
        this.defaultMode = format.getTextMode();
        this.specifiedAttributesOnly = format.isSpecifiedAttributesOnly();
        this.mode[this.depth] = format.getTextMode();
        if (this.mode[this.depth] == Format.TextMode.PRESERVE) {
            this.levelIndent[this.depth] = null;
            this.levelEOL[this.depth] = null;
            this.levelEOLIndent[this.depth] = null;
            this.termEOLIndent[this.depth] = null;
        } else {
            this.levelIndent[this.depth] = format.getIndent() == null ? null : "";
            this.levelEOL[this.depth] = format.getLineSeparator();
            this.levelEOLIndent[this.depth] = this.levelIndent[this.depth] == null ? null : this.levelEOL[this.depth];
            this.termEOLIndent[this.depth] = this.levelEOLIndent[this.depth];
        }
        this.ignoreTrAXEscapingPIs[this.depth] = format.getIgnoreTrAXEscapingPIs();
        this.escapeOutput[this.depth] = true;
    }

    private final void resetReusableIndents() {
        for (int d = this.depth + 1; d < this.levelIndent.length && this.levelIndent[d] != null; ++d) {
            this.levelIndent[d] = null;
        }
    }

    public String getIndent() {
        return this.indent;
    }

    public String getLineSeparator() {
        return this.lineSeparator;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public boolean isOmitDeclaration() {
        return this.omitDeclaration;
    }

    public boolean isSpecifiedAttributesOnly() {
        return this.specifiedAttributesOnly;
    }

    public boolean isOmitEncoding() {
        return this.omitEncoding;
    }

    public boolean isExpandEmptyElements() {
        return this.expandEmptyElements;
    }

    public EscapeStrategy getEscapeStrategy() {
        return this.escapeStrategy;
    }

    public boolean isIgnoreTrAXEscapingPIs() {
        return this.ignoreTrAXEscapingPIs[this.depth];
    }

    public void setIgnoreTrAXEscapingPIs(boolean ignoreTrAXEscapingPIs) {
        this.ignoreTrAXEscapingPIs[this.depth] = ignoreTrAXEscapingPIs;
    }

    public boolean getEscapeOutput() {
        return this.escapeOutput[this.depth];
    }

    public void setEscapeOutput(boolean escape) {
        this.escapeOutput[this.depth] = escape;
    }

    public Format.TextMode getDefaultMode() {
        return this.defaultMode;
    }

    public String getLevelIndent() {
        return this.levelIndent[this.depth];
    }

    public String getPadBetween() {
        return this.levelEOLIndent[this.depth];
    }

    public String getPadLast() {
        return this.termEOLIndent[this.depth];
    }

    public void setLevelIndent(String indent) {
        this.levelIndent[this.depth] = indent;
        this.levelEOLIndent[this.depth] = indent == null || this.levelEOL[this.depth] == null ? null : this.levelEOL[this.depth] + indent;
        this.resetReusableIndents();
    }

    public String getLevelEOL() {
        return this.levelEOL[this.depth];
    }

    public void setLevelEOL(String newline) {
        this.levelEOL[this.depth] = newline;
        this.resetReusableIndents();
    }

    public Format.TextMode getTextMode() {
        return this.mode[this.depth];
    }

    public void setTextMode(Format.TextMode mode) {
        if (this.mode[this.depth] == mode) {
            return;
        }
        this.mode[this.depth] = mode;
        switch (mode) {
            case PRESERVE: {
                this.levelEOL[this.depth] = null;
                this.levelIndent[this.depth] = null;
                this.levelEOLIndent[this.depth] = null;
                this.termEOLIndent[this.depth] = null;
                break;
            }
            default: {
                this.levelEOL[this.depth] = this.lineSeparator;
                if (this.indent == null || this.lineSeparator == null) {
                    this.levelEOLIndent[this.depth] = null;
                    this.termEOLIndent[this.depth] = null;
                    break;
                }
                if (this.depth > 0) {
                    StringBuilder sb = new StringBuilder(this.indent.length() * this.depth);
                    for (int i = 1; i < this.depth; ++i) {
                        sb.append(this.indent);
                    }
                    this.termEOLIndent[this.depth] = this.lineSeparator + sb.toString();
                    sb.append(this.indent);
                    this.levelIndent[this.depth] = sb.toString();
                } else {
                    this.termEOLIndent[this.depth] = this.lineSeparator;
                    this.levelIndent[this.depth] = "";
                }
                this.levelEOLIndent[this.depth] = this.lineSeparator + this.levelIndent[this.depth];
            }
        }
        this.resetReusableIndents();
    }

    public void push() {
        int prev = this.depth++;
        if (this.depth >= this.capacity) {
            this.capacity *= 2;
            this.levelIndent = ArrayCopy.copyOf(this.levelIndent, this.capacity);
            this.levelEOL = ArrayCopy.copyOf(this.levelEOL, this.capacity);
            this.levelEOLIndent = ArrayCopy.copyOf(this.levelEOLIndent, this.capacity);
            this.termEOLIndent = ArrayCopy.copyOf(this.termEOLIndent, this.capacity);
            this.ignoreTrAXEscapingPIs = ArrayCopy.copyOf(this.ignoreTrAXEscapingPIs, this.capacity);
            this.mode = ArrayCopy.copyOf(this.mode, this.capacity);
            this.escapeOutput = ArrayCopy.copyOf(this.escapeOutput, this.capacity);
        }
        this.ignoreTrAXEscapingPIs[this.depth] = this.ignoreTrAXEscapingPIs[prev];
        this.mode[this.depth] = this.mode[prev];
        this.escapeOutput[this.depth] = this.escapeOutput[prev];
        if (this.levelIndent[prev] == null || this.levelEOL[prev] == null) {
            this.levelIndent[this.depth] = null;
            this.levelEOL[this.depth] = null;
            this.levelEOLIndent[this.depth] = null;
            this.termEOLIndent[this.depth] = null;
        } else if (this.levelIndent[this.depth] == null) {
            this.levelEOL[this.depth] = this.levelEOL[prev];
            this.termEOLIndent[this.depth] = this.levelEOL[this.depth] + this.levelIndent[prev];
            this.levelIndent[this.depth] = this.levelIndent[prev] + this.indent;
            this.levelEOLIndent[this.depth] = this.levelEOL[this.depth] + this.levelIndent[this.depth];
        }
    }

    public void pop() {
        --this.depth;
    }
}

