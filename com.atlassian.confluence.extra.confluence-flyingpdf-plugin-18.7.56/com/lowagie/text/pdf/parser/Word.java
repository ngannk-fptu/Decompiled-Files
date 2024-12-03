/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text.pdf.parser;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.FinalText;
import com.lowagie.text.pdf.parser.ParsedTextImpl;
import com.lowagie.text.pdf.parser.TextAssembler;
import com.lowagie.text.pdf.parser.Vector;
import javax.annotation.Nullable;

public class Word
extends ParsedTextImpl {
    private boolean shouldNotSplit;
    private boolean breakBefore;

    Word(String text, float ascent, float descent, Vector startPoint, Vector endPoint, Vector baseline, float spaceWidth, boolean isCompleteWord, boolean breakBefore) {
        super(text, startPoint, endPoint, baseline, ascent, descent, spaceWidth);
        this.shouldNotSplit = isCompleteWord;
        this.breakBefore = breakBefore;
    }

    @Override
    public void accumulate(TextAssembler p, String contextName) {
        p.process(this, contextName);
    }

    @Override
    public void assemble(TextAssembler p) {
        p.renderText(this);
    }

    private static String formatPercent(float f) {
        return String.format("%.2f%%", Float.valueOf(f));
    }

    private String wordMarkup(@Nullable String text, PdfReader reader, int page, TextAssembler assembler) {
        if (text == null) {
            return "";
        }
        Rectangle mediaBox = reader.getPageSize(page);
        Rectangle cropBox = reader.getBoxSize(page, "crop");
        if ((text = text.replaceAll("[\u00a0\u202f]", " ").trim()).length() == 0) {
            return text;
        }
        mediaBox.normalize();
        if (cropBox != null) {
            cropBox.normalize();
        } else {
            cropBox = reader.getBoxSize(page, "trim");
            if (cropBox != null) {
                cropBox.normalize();
            } else {
                cropBox = mediaBox;
            }
        }
        float xOffset = cropBox.getLeft() - mediaBox.getLeft();
        float yOffset = cropBox.getTop() - mediaBox.getTop();
        Vector startPoint = this.getStartPoint();
        Vector endPoint = this.getEndPoint();
        float pageWidth = cropBox.getWidth();
        float pageHeight = cropBox.getHeight();
        float leftPercent = (float)((double)((startPoint.get(0) - xOffset - mediaBox.getLeft()) / pageWidth) * 100.0);
        float bottom = endPoint.get(1) + yOffset - this.getDescent() - mediaBox.getBottom();
        float bottomPercent = bottom / pageHeight * 100.0f;
        StringBuilder result = new StringBuilder();
        float width = this.getWidth();
        float widthPercent = width / pageWidth * 100.0f;
        float height = this.getAscent();
        float heightPercent = height / pageHeight * 100.0f;
        String myId = assembler.getWordId();
        Rectangle resultRect = new Rectangle(leftPercent, bottomPercent, leftPercent + widthPercent, bottomPercent + heightPercent);
        result.append("<span class=\"t-word\" style=\"bottom: ").append(Word.formatPercent(resultRect.getBottom())).append("; left: ").append(Word.formatPercent(resultRect.getLeft())).append("; width: ").append(Word.formatPercent(resultRect.getWidth())).append("; height: ").append(Word.formatPercent(resultRect.getHeight())).append(";\"").append(" id=\"").append(myId).append("\">").append(Word.escapeHTML(text)).append(" ");
        result.append("</span> ");
        return result.toString();
    }

    private static String escapeHTML(String s) {
        return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    @Override
    public FinalText getFinalText(PdfReader reader, int page, TextAssembler assembler, boolean useMarkup) {
        if (useMarkup) {
            return new FinalText(this.wordMarkup(this.getText(), reader, page, assembler));
        }
        return new FinalText(this.getText() + " ");
    }

    public String toString() {
        return "[Word: [" + this.getText() + "] " + this.getStartPoint() + ", " + this.getEndPoint() + "] lead" + this.getAscent() + "]";
    }

    @Override
    public boolean shouldNotSplit() {
        return this.shouldNotSplit;
    }

    @Override
    public boolean breakBefore() {
        return this.breakBefore;
    }
}

