/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.extractor;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.extractor.POIOLE2TextExtractor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.WordToTextConverter;
import org.apache.poi.hwpf.usermodel.HeaderStories;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public final class WordExtractor
implements POIOLE2TextExtractor {
    private final HWPFDocument doc;
    private boolean doCloseFilesystem = true;

    public WordExtractor(InputStream is) throws IOException {
        this(HWPFDocumentCore.verifyAndBuildPOIFS(is));
    }

    public WordExtractor(POIFSFileSystem fs) throws IOException {
        this(new HWPFDocument(fs));
    }

    public WordExtractor(DirectoryNode dir) throws IOException {
        this(new HWPFDocument(dir));
    }

    public WordExtractor(HWPFDocument doc) {
        this.doc = doc;
    }

    public String[] getParagraphText() {
        String[] ret;
        try {
            Range r = this.doc.getRange();
            ret = WordExtractor.getParagraphText(r);
        }
        catch (Exception e) {
            ret = new String[]{this.getTextFromPieces()};
        }
        return ret;
    }

    public String[] getFootnoteText() {
        Range r = this.doc.getFootnoteRange();
        return WordExtractor.getParagraphText(r);
    }

    public String[] getMainTextboxText() {
        Range r = this.doc.getMainTextboxRange();
        return WordExtractor.getParagraphText(r);
    }

    public String[] getEndnoteText() {
        Range r = this.doc.getEndnoteRange();
        return WordExtractor.getParagraphText(r);
    }

    public String[] getCommentsText() {
        Range r = this.doc.getCommentsRange();
        return WordExtractor.getParagraphText(r);
    }

    static String[] getParagraphText(Range r) {
        String[] ret = new String[r.numParagraphs()];
        for (int i = 0; i < ret.length; ++i) {
            Paragraph p = r.getParagraph(i);
            ret[i] = p.text();
            if (!ret[i].endsWith("\r")) continue;
            ret[i] = ret[i] + "\n";
        }
        return ret;
    }

    private void appendHeaderFooter(String text, StringBuilder out) {
        if (text == null || text.length() == 0) {
            return;
        }
        if (!(text = text.replace('\r', '\n')).endsWith("\n")) {
            out.append(text);
            out.append('\n');
            return;
        }
        if (text.endsWith("\n\n")) {
            out.append(text, 0, text.length() - 1);
            return;
        }
        out.append(text);
    }

    @Deprecated
    public String getHeaderText() {
        HeaderStories hs = new HeaderStories(this.doc);
        StringBuilder ret = new StringBuilder();
        if (hs.getFirstHeader() != null) {
            this.appendHeaderFooter(hs.getFirstHeader(), ret);
        }
        if (hs.getEvenHeader() != null) {
            this.appendHeaderFooter(hs.getEvenHeader(), ret);
        }
        if (hs.getOddHeader() != null) {
            this.appendHeaderFooter(hs.getOddHeader(), ret);
        }
        return ret.toString();
    }

    @Deprecated
    public String getFooterText() {
        HeaderStories hs = new HeaderStories(this.doc);
        StringBuilder ret = new StringBuilder();
        if (hs.getFirstFooter() != null) {
            this.appendHeaderFooter(hs.getFirstFooter(), ret);
        }
        if (hs.getEvenFooter() != null) {
            this.appendHeaderFooter(hs.getEvenFooter(), ret);
        }
        if (hs.getOddFooter() != null) {
            this.appendHeaderFooter(hs.getOddFooter(), ret);
        }
        return ret.toString();
    }

    public String getTextFromPieces() {
        String text = this.doc.getDocumentText();
        text = text.replace("\r\r\r", "\r\n\r\n\r\n");
        if ((text = text.replace("\r\r", "\r\n\r\n")).endsWith("\r")) {
            text = text + "\n";
        }
        return text;
    }

    @Override
    public String getText() {
        try {
            WordToTextConverter wordToTextConverter = new WordToTextConverter();
            HeaderStories hs = new HeaderStories(this.doc);
            if (hs.getFirstHeaderSubrange() != null) {
                wordToTextConverter.processDocumentPart(this.doc, hs.getFirstHeaderSubrange());
            }
            if (hs.getEvenHeaderSubrange() != null) {
                wordToTextConverter.processDocumentPart(this.doc, hs.getEvenHeaderSubrange());
            }
            if (hs.getOddHeaderSubrange() != null) {
                wordToTextConverter.processDocumentPart(this.doc, hs.getOddHeaderSubrange());
            }
            wordToTextConverter.processDocument(this.doc);
            wordToTextConverter.processDocumentPart(this.doc, this.doc.getMainTextboxRange());
            if (hs.getFirstFooterSubrange() != null) {
                wordToTextConverter.processDocumentPart(this.doc, hs.getFirstFooterSubrange());
            }
            if (hs.getEvenFooterSubrange() != null) {
                wordToTextConverter.processDocumentPart(this.doc, hs.getEvenFooterSubrange());
            }
            if (hs.getOddFooterSubrange() != null) {
                wordToTextConverter.processDocumentPart(this.doc, hs.getOddFooterSubrange());
            }
            return wordToTextConverter.getText();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    public static String stripFields(String text) {
        return Range.stripFields(text);
    }

    @Override
    public HWPFDocument getDocument() {
        return this.doc;
    }

    @Override
    public void setCloseFilesystem(boolean doCloseFilesystem) {
        this.doCloseFilesystem = doCloseFilesystem;
    }

    @Override
    public boolean isCloseFilesystem() {
        return this.doCloseFilesystem;
    }

    @Override
    public HWPFDocument getFilesystem() {
        return this.doc;
    }
}

