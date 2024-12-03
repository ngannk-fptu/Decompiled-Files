/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.text;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.contentstream.operator.markedcontent.BeginMarkedContentSequence;
import org.apache.pdfbox.contentstream.operator.markedcontent.BeginMarkedContentSequenceWithProperties;
import org.apache.pdfbox.contentstream.operator.markedcontent.DrawObject;
import org.apache.pdfbox.contentstream.operator.markedcontent.EndMarkedContentSequence;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.text.LegacyPDFStreamEngine;
import org.apache.pdfbox.text.TextPosition;

public class PDFMarkedContentExtractor
extends LegacyPDFStreamEngine {
    private boolean suppressDuplicateOverlappingText = true;
    private final List<PDMarkedContent> markedContents = new ArrayList<PDMarkedContent>();
    private final Deque<PDMarkedContent> currentMarkedContents = new ArrayDeque<PDMarkedContent>();
    private final Map<String, List<TextPosition>> characterListMapping = new HashMap<String, List<TextPosition>>();

    public PDFMarkedContentExtractor() throws IOException {
        this(null);
    }

    public PDFMarkedContentExtractor(String encoding) throws IOException {
        this.addOperator(new BeginMarkedContentSequenceWithProperties());
        this.addOperator(new BeginMarkedContentSequence());
        this.addOperator(new EndMarkedContentSequence());
        this.addOperator(new DrawObject());
    }

    public boolean isSuppressDuplicateOverlappingText() {
        return this.suppressDuplicateOverlappingText;
    }

    public void setSuppressDuplicateOverlappingText(boolean suppressDuplicateOverlappingText) {
        this.suppressDuplicateOverlappingText = suppressDuplicateOverlappingText;
    }

    private boolean within(float first, float second, float variance) {
        return second > first - variance && second < first + variance;
    }

    @Override
    public void beginMarkedContentSequence(COSName tag, COSDictionary properties) {
        PDMarkedContent markedContent = PDMarkedContent.create(tag, properties);
        if (this.currentMarkedContents.isEmpty()) {
            this.markedContents.add(markedContent);
        } else {
            PDMarkedContent currentMarkedContent = this.currentMarkedContents.peek();
            if (currentMarkedContent != null) {
                currentMarkedContent.addMarkedContent(markedContent);
            }
        }
        this.currentMarkedContents.push(markedContent);
    }

    @Override
    public void endMarkedContentSequence() {
        if (!this.currentMarkedContents.isEmpty()) {
            this.currentMarkedContents.pop();
        }
    }

    public void xobject(PDXObject xobject) {
        if (!this.currentMarkedContents.isEmpty()) {
            this.currentMarkedContents.peek().addXObject(xobject);
        }
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        boolean showCharacter = true;
        if (this.suppressDuplicateOverlappingText) {
            showCharacter = false;
            String textCharacter = text.getUnicode();
            float textX = text.getX();
            float textY = text.getY();
            List<TextPosition> sameTextCharacters = this.characterListMapping.get(textCharacter);
            if (sameTextCharacters == null) {
                sameTextCharacters = new ArrayList<TextPosition>();
                this.characterListMapping.put(textCharacter, sameTextCharacters);
            }
            boolean suppressCharacter = false;
            float tolerance = text.getWidth() / (float)textCharacter.length() / 3.0f;
            for (TextPosition sameTextCharacter : sameTextCharacters) {
                String charCharacter = sameTextCharacter.getUnicode();
                float charX = sameTextCharacter.getX();
                float charY = sameTextCharacter.getY();
                if (charCharacter == null || !this.within(charX, textX, tolerance) || !this.within(charY, textY, tolerance)) continue;
                suppressCharacter = true;
                break;
            }
            if (!suppressCharacter) {
                sameTextCharacters.add(text);
                showCharacter = true;
            }
        }
        if (showCharacter) {
            ArrayList<TextPosition> textList = new ArrayList<TextPosition>();
            if (textList.isEmpty()) {
                textList.add(text);
            } else {
                TextPosition previousTextPosition = (TextPosition)textList.get(textList.size() - 1);
                if (text.isDiacritic() && previousTextPosition.contains(text)) {
                    previousTextPosition.mergeDiacritic(text);
                } else if (previousTextPosition.isDiacritic() && text.contains(previousTextPosition)) {
                    text.mergeDiacritic(previousTextPosition);
                    textList.remove(textList.size() - 1);
                    textList.add(text);
                } else {
                    textList.add(text);
                }
            }
            if (!this.currentMarkedContents.isEmpty()) {
                this.currentMarkedContents.peek().addText(text);
            }
        }
    }

    public List<PDMarkedContent> getMarkedContents() {
        return this.markedContents;
    }
}

