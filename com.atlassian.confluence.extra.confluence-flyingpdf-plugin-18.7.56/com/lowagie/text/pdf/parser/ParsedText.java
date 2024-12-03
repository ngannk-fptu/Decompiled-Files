/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.CMapAwareDocumentFont;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.parser.FinalText;
import com.lowagie.text.pdf.parser.GraphicsState;
import com.lowagie.text.pdf.parser.Matrix;
import com.lowagie.text.pdf.parser.ParsedTextImpl;
import com.lowagie.text.pdf.parser.TextAssembler;
import com.lowagie.text.pdf.parser.Vector;
import com.lowagie.text.pdf.parser.Word;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParsedText
extends ParsedTextImpl {
    private final Matrix textToUserSpaceTransformMatrix;
    private final GraphicsState graphicsState;
    private PdfString pdfText = null;

    @Deprecated
    ParsedText(String text, GraphicsState graphicsState, Matrix textMatrix) {
        this(text, new GraphicsState(graphicsState), textMatrix.multiply(graphicsState.getCtm()), ParsedText.getUnscaledFontSpaceWidth(graphicsState));
    }

    ParsedText(PdfString text, GraphicsState graphicsState, Matrix textMatrix) {
        this(text, new GraphicsState(graphicsState), textMatrix.multiply(graphicsState.getCtm()), ParsedText.getUnscaledFontSpaceWidth(graphicsState));
    }

    private ParsedText(PdfString text, GraphicsState graphicsState, Matrix textMatrix, float unscaledWidth) {
        super(null, ParsedText.pointToUserSpace(0.0f, 0.0f, textMatrix), ParsedText.pointToUserSpace(ParsedText.getStringWidth(text.toString(), graphicsState), 0.0f, textMatrix), ParsedText.pointToUserSpace(1.0f, 0.0f, textMatrix), ParsedText.convertHeightToUser(graphicsState.getFontAscentDescriptor(), textMatrix), ParsedText.convertHeightToUser(graphicsState.getFontDescentDescriptor(), textMatrix), ParsedText.convertWidthToUser(unscaledWidth, textMatrix));
        this.pdfText = "Identity-H".equals(graphicsState.getFont().getEncoding()) ? new PdfString(new String(text.getBytes(), StandardCharsets.UTF_16)) : text;
        this.textToUserSpaceTransformMatrix = textMatrix;
        this.graphicsState = graphicsState;
    }

    @Deprecated
    private ParsedText(String text, GraphicsState graphicsState, Matrix textMatrix, float unscaledWidth) {
        super(text, ParsedText.pointToUserSpace(0.0f, 0.0f, textMatrix), ParsedText.pointToUserSpace(ParsedText.getStringWidth(text, graphicsState), 0.0f, textMatrix), ParsedText.pointToUserSpace(1.0f, 0.0f, textMatrix), ParsedText.convertHeightToUser(graphicsState.getFontAscentDescriptor(), textMatrix), ParsedText.convertHeightToUser(graphicsState.getFontDescentDescriptor(), textMatrix), ParsedText.convertWidthToUser(unscaledWidth, textMatrix));
        this.textToUserSpaceTransformMatrix = textMatrix;
        this.graphicsState = graphicsState;
    }

    private static Vector pointToUserSpace(float xOffset, float yOffset, Matrix textToUserSpaceTransformMatrix) {
        return new Vector(xOffset, yOffset, 1.0f).cross(textToUserSpaceTransformMatrix);
    }

    private static float getUnscaledFontSpaceWidth(GraphicsState graphicsState) {
        char charToUse = ' ';
        if (graphicsState.getFont().getWidth(charToUse) == 0) {
            charToUse = '\u00a0';
        }
        return ParsedText.getStringWidth(String.valueOf(charToUse), graphicsState);
    }

    private static float getStringWidth(String string, GraphicsState graphicsState) {
        char[] chars = string.toCharArray();
        float totalWidth = 0.0f;
        for (char c : chars) {
            float w = (float)graphicsState.getFont().getWidth(c) / 1000.0f;
            float wordSpacing = Character.isSpaceChar(c) ? graphicsState.getWordSpacing() : 0.0f;
            totalWidth += (w * graphicsState.getFontSize() + graphicsState.getCharacterSpacing() + wordSpacing) * graphicsState.getHorizontalScaling();
        }
        return totalWidth;
    }

    private static float convertWidthToUser(float width, Matrix textToUserSpaceTransformMatrix) {
        Vector startPos = ParsedText.pointToUserSpace(0.0f, 0.0f, textToUserSpaceTransformMatrix);
        Vector endPos = ParsedText.pointToUserSpace(width, 0.0f, textToUserSpaceTransformMatrix);
        return ParsedText.distance(startPos, endPos);
    }

    private static float distance(Vector startPos, Vector endPos) {
        return endPos.subtract(startPos).length();
    }

    private static float convertHeightToUser(float height, Matrix textToUserSpaceTransformMatrix) {
        Vector startPos = ParsedText.pointToUserSpace(0.0f, 0.0f, textToUserSpaceTransformMatrix);
        Vector endPos = ParsedText.pointToUserSpace(0.0f, height, textToUserSpaceTransformMatrix);
        return ParsedText.distance(endPos, startPos);
    }

    protected String decode(String in) {
        if ("Identity-H".equals(this.graphicsState.getFont().getEncoding())) {
            byte[] byArray = in.getBytes(StandardCharsets.UTF_16);
        }
        byte[] bytes = in.getBytes();
        return this.graphicsState.getFont().decode(bytes, 0, bytes.length);
    }

    protected String decode(PdfString pdfString) {
        byte[] bytes = pdfString.getOriginalBytes();
        return this.graphicsState.getFont().decode(bytes, 0, bytes.length);
    }

    public List<Word> getAsPartialWords() {
        ArrayList<Word> result = new ArrayList<Word>();
        CMapAwareDocumentFont font = this.graphicsState.getFont();
        char[] chars = this.pdfText.getOriginalChars();
        boolean[] hasSpace = new boolean[chars.length];
        float totalWidth = 0.0f;
        StringBuffer wordAccum = new StringBuffer(3);
        float wordStartOffset = 0.0f;
        boolean wordsAreComplete = this.preprocessString(chars, hasSpace);
        boolean currentBreakBefore = false;
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            float w = (float)font.getWidth(c) / 1000.0f;
            if (hasSpace[i]) {
                if (wordAccum.length() > 0) {
                    result.add(this.createWord(wordAccum, wordStartOffset, totalWidth, this.getBaseline(), wordsAreComplete, currentBreakBefore));
                    wordAccum = new StringBuffer();
                }
                if (!Character.isWhitespace(c)) {
                    wordStartOffset = totalWidth;
                }
                totalWidth += this.graphicsState.calculateCharacterWidthWithSpace(w);
                if (Character.isWhitespace(c)) {
                    wordStartOffset = totalWidth;
                }
                wordAccum.append(c);
                currentBreakBefore = true;
                continue;
            }
            wordAccum.append(c);
            totalWidth += this.graphicsState.calculateCharacterWidthWithoutSpace(w);
        }
        if (wordAccum.length() > 0) {
            result.add(this.createWord(wordAccum, wordStartOffset, totalWidth, this.getBaseline(), wordsAreComplete, currentBreakBefore));
        }
        return result;
    }

    private boolean preprocessString(char[] chars, boolean[] hasSpace) {
        boolean wordsAreComplete = false;
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            hasSpace[i] = false;
            String charValue = this.graphicsState.getFont().decode(c);
            if (charValue == null) continue;
            for (char cFinal : charValue.toCharArray()) {
                if (!Character.isSpaceChar(cFinal)) continue;
                wordsAreComplete = true;
                hasSpace[i] = true;
            }
        }
        return wordsAreComplete;
    }

    private Word createWord(StringBuffer wordAccum, float wordStartOffset, float wordEndOffset, Vector baseline, boolean wordsAreComplete, boolean currentBreakBefore) {
        return new Word(this.graphicsState.getFont().decode(wordAccum.toString()), this.getAscent(), this.getDescent(), ParsedText.pointToUserSpace(wordStartOffset, 0.0f, this.textToUserSpaceTransformMatrix), ParsedText.pointToUserSpace(wordEndOffset, 0.0f, this.textToUserSpaceTransformMatrix), baseline, this.getSingleSpaceWidth(), wordsAreComplete, currentBreakBefore);
    }

    public float getUnscaledTextWidth(GraphicsState gs) {
        return ParsedText.getStringWidth(this.getFontCodes(), gs);
    }

    @Override
    public void accumulate(TextAssembler textAssembler, String contextName) {
        textAssembler.process(this, contextName);
    }

    @Override
    public void assemble(TextAssembler textAssembler) {
        textAssembler.renderText(this);
    }

    @Override
    @Nullable
    public String getText() {
        String text = super.getText();
        if (text == null && this.pdfText != null) {
            return this.decode(this.pdfText);
        }
        return text;
    }

    @Nonnull
    public String getFontCodes() {
        return Optional.ofNullable(this.pdfText).map(PdfString::toString).orElse("");
    }

    @Override
    public FinalText getFinalText(PdfReader reader, int page, TextAssembler assembler, boolean useMarkup) {
        throw new RuntimeException("Final text should never be called on unprocessed word fragment.");
    }

    public String toString() {
        return "[ParsedText: [" + this.getText() + "] " + this.getStartPoint() + ", " + this.getEndPoint() + "] lead]";
    }

    @Override
    public boolean shouldNotSplit() {
        return false;
    }

    @Override
    public boolean breakBefore() {
        return false;
    }
}

