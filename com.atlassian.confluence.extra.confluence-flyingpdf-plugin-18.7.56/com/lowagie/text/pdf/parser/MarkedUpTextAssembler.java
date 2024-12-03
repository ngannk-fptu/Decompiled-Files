/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.FinalText;
import com.lowagie.text.pdf.parser.ParsedText;
import com.lowagie.text.pdf.parser.ParsedTextImpl;
import com.lowagie.text.pdf.parser.TextAssembler;
import com.lowagie.text.pdf.parser.TextAssemblyBuffer;
import com.lowagie.text.pdf.parser.Vector;
import com.lowagie.text.pdf.parser.Word;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class MarkedUpTextAssembler
implements TextAssembler {
    List<FinalText> result = new ArrayList<FinalText>();
    private PdfReader reader;
    @Nullable
    private ParsedTextImpl inProgress = null;
    private int page;
    private int wordIdCounter = 1;
    private boolean usePdfMarkupElements = false;
    private List<TextAssemblyBuffer> partialWords = new ArrayList<TextAssemblyBuffer>();

    MarkedUpTextAssembler(PdfReader reader) {
        this.reader = reader;
    }

    MarkedUpTextAssembler(PdfReader reader, boolean usePdfMarkupElements) {
        this.reader = reader;
        this.usePdfMarkupElements = usePdfMarkupElements;
    }

    @Override
    public void process(ParsedText unassembled, String contextName) {
        this.partialWords.addAll(unassembled.getAsPartialWords());
    }

    @Override
    public void process(FinalText completed, String contextName) {
        this.clearAccumulator();
        this.result.add(completed);
    }

    @Override
    public void process(Word completed, String contextName) {
        this.partialWords.add(completed);
    }

    private void clearAccumulator() {
        for (TextAssemblyBuffer partialWord : this.partialWords) {
            partialWord.assemble(this);
        }
        this.partialWords.clear();
        if (this.inProgress != null) {
            this.result.add(this.inProgress.getFinalText(this.reader, this.page, this, this.usePdfMarkupElements));
            this.inProgress = null;
        }
    }

    private FinalText concatenateResult(@Nullable String containingElementName) {
        if (containingElementName == null) {
            return null;
        }
        StringBuilder res = new StringBuilder();
        if (this.usePdfMarkupElements && !containingElementName.isEmpty()) {
            res.append('<').append(containingElementName).append('>');
        }
        for (FinalText item : this.result) {
            res.append(item.getText());
        }
        this.result.clear();
        if (this.usePdfMarkupElements && !containingElementName.isEmpty()) {
            res.append("</");
            int spacePos = containingElementName.indexOf(32);
            if (spacePos >= 0) {
                containingElementName = containingElementName.substring(0, spacePos);
            }
            res.append(containingElementName).append('>');
        }
        return new FinalText(res.toString());
    }

    @Override
    public FinalText endParsingContext(@Nullable String containingElementName) {
        this.clearAccumulator();
        return this.concatenateResult(containingElementName);
    }

    @Override
    public void reset() {
        this.result.clear();
        this.partialWords.clear();
        this.inProgress = null;
    }

    @Override
    public void renderText(FinalText finalText) {
        this.result.add(finalText);
    }

    @Override
    public void renderText(ParsedTextImpl partialWord) {
        float sameLineThreshold;
        boolean firstRender = this.inProgress == null;
        boolean hardReturn = false;
        if (firstRender) {
            this.inProgress = partialWord;
            return;
        }
        Vector start = partialWord.getStartPoint();
        Vector lastStart = this.inProgress.getStartPoint();
        Vector lastEnd = this.inProgress.getEndPoint();
        float dist = this.inProgress.getBaseline().subtract(lastStart).cross(lastStart.subtract(start)).lengthSquared() / this.inProgress.getBaseline().subtract(lastStart).lengthSquared();
        if (dist > (sameLineThreshold = partialWord.getAscent() * 0.5f) || Float.isNaN(dist)) {
            hardReturn = true;
        }
        float spacing = lastEnd.subtract(start).length();
        if (hardReturn || partialWord.breakBefore()) {
            this.result.add(this.inProgress.getFinalText(this.reader, this.page, this, this.usePdfMarkupElements));
            if (hardReturn) {
                this.result.add(new FinalText("\n"));
                if (this.usePdfMarkupElements) {
                    this.result.add(new FinalText("<br class='t-pdf' />"));
                }
            }
            this.inProgress = partialWord;
        } else if ((double)spacing < (double)partialWord.getSingleSpaceWidth() / 2.3 || this.inProgress.shouldNotSplit()) {
            this.inProgress = new Word(this.inProgress.getText() + partialWord.getText().trim(), partialWord.getAscent(), partialWord.getDescent(), lastStart, partialWord.getEndPoint(), this.inProgress.getBaseline(), partialWord.getSingleSpaceWidth(), this.inProgress.shouldNotSplit(), this.inProgress.breakBefore());
        } else {
            this.result.add(this.inProgress.getFinalText(this.reader, this.page, this, this.usePdfMarkupElements));
            this.inProgress = partialWord;
        }
    }

    protected PdfReader getReader() {
        return this.reader;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public String getWordId() {
        return "word" + this.wordIdCounter++;
    }
}

