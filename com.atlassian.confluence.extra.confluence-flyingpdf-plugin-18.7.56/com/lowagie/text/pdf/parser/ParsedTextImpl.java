/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.parser.TextAssemblyBuffer;
import com.lowagie.text.pdf.parser.Vector;
import javax.annotation.Nullable;

public abstract class ParsedTextImpl
implements TextAssemblyBuffer {
    private final String text;
    private float ascent;
    private float descent;
    private Vector startPoint;
    private Vector endPoint;
    private float spaceWidth;
    private Vector baseline;

    ParsedTextImpl(@Nullable String text, Vector startPoint, Vector endPoint, Vector baseline, float ascent, float descent, float spaceWidth) {
        this.baseline = baseline;
        this.text = text;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.ascent = ascent;
        this.descent = descent;
        this.spaceWidth = spaceWidth;
    }

    @Override
    @Nullable
    public String getText() {
        return this.text;
    }

    public float getSingleSpaceWidth() {
        return this.spaceWidth;
    }

    public float getAscent() {
        return this.ascent;
    }

    public float getDescent() {
        return this.descent;
    }

    public float getWidth() {
        return this.getEndPoint().subtract(this.getStartPoint()).length();
    }

    public Vector getStartPoint() {
        return this.startPoint;
    }

    public Vector getEndPoint() {
        return this.endPoint;
    }

    public Vector getBaseline() {
        return this.baseline;
    }

    public abstract boolean shouldNotSplit();

    public abstract boolean breakBefore();
}

