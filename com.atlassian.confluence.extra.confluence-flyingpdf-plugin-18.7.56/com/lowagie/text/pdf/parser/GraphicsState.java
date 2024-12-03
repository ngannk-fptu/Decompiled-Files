/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.CMapAwareDocumentFont;
import com.lowagie.text.pdf.parser.Matrix;

public class GraphicsState {
    private Matrix ctm;
    private float characterSpacing;
    private float wordSpacing;
    private float horizontalScaling;
    private float leading;
    private CMapAwareDocumentFont font;
    private float fontSize;
    private int renderMode;
    private float rise;
    private boolean knockout;

    public GraphicsState() {
        this.ctm = new Matrix();
        this.characterSpacing = 0.0f;
        this.wordSpacing = 0.0f;
        this.horizontalScaling = 1.0f;
        this.leading = 0.0f;
        this.font = null;
        this.fontSize = 0.0f;
        this.renderMode = 0;
        this.rise = 0.0f;
        this.knockout = true;
    }

    public GraphicsState(GraphicsState source) {
        this.ctm = source.ctm;
        this.characterSpacing = source.characterSpacing;
        this.wordSpacing = source.wordSpacing;
        this.horizontalScaling = source.horizontalScaling;
        this.leading = source.leading;
        this.font = source.font;
        this.fontSize = source.fontSize;
        this.renderMode = source.renderMode;
        this.rise = source.rise;
        this.knockout = source.knockout;
    }

    public Matrix getCtm() {
        return this.ctm;
    }

    public float getCharacterSpacing() {
        return this.characterSpacing;
    }

    public void setCharacterSpacing(float characterSpacing) {
        this.characterSpacing = characterSpacing;
    }

    public float getWordSpacing() {
        return this.wordSpacing;
    }

    public void setWordSpacing(float wordSpacing) {
        this.wordSpacing = wordSpacing;
    }

    public float getHorizontalScaling() {
        return this.horizontalScaling;
    }

    public void setHorizontalScaling(float horizontalScaling) {
        this.horizontalScaling = horizontalScaling;
    }

    public float getLeading() {
        return this.leading;
    }

    public void setLeading(float leading) {
        this.leading = leading;
    }

    public float getFontAscentDescriptor() {
        return this.font.getFontDescriptor(1, this.fontSize);
    }

    public float getFontDescentDescriptor() {
        return this.font.getFontDescriptor(3, this.fontSize);
    }

    public float calculateCharacterWidthWithSpace(float charFontWidth) {
        return (charFontWidth * this.fontSize + this.characterSpacing + this.wordSpacing) * this.horizontalScaling;
    }

    public float calculateCharacterWidthWithoutSpace(float charFontWidth) {
        return (charFontWidth * this.fontSize + this.characterSpacing) * this.horizontalScaling;
    }

    public CMapAwareDocumentFont getFont() {
        return this.font;
    }

    public void setFont(CMapAwareDocumentFont font) {
        this.font = font;
    }

    public float getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public int getRenderMode() {
        return this.renderMode;
    }

    public void setRenderMode(int renderMode) {
        this.renderMode = renderMode;
    }

    public float getRise() {
        return this.rise;
    }

    public void setRise(float rise) {
        this.rise = rise;
    }

    public boolean isKnockout() {
        return this.knockout;
    }

    public Matrix multiplyCtm(Matrix matrix) {
        this.ctm = this.ctm.multiply(matrix);
        return this.ctm;
    }
}

