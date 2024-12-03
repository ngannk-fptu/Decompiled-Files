/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.FileInformationBlock;
import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.hwpf.model.PlexOfCps;
import org.apache.poi.hwpf.model.SubdocumentType;
import org.apache.poi.hwpf.usermodel.Range;

public final class HeaderStories {
    private final Range headerStories;
    private PlexOfCps plcfHdd;
    private boolean stripFields;

    public HeaderStories(HWPFDocument doc) {
        this.headerStories = doc.getHeaderStoryRange();
        FileInformationBlock fib = doc.getFileInformationBlock();
        if (fib.getSubdocumentTextStreamLength(SubdocumentType.HEADER) == 0) {
            return;
        }
        if (fib.getPlcfHddSize() == 0) {
            return;
        }
        this.plcfHdd = new PlexOfCps(doc.getTableStream(), fib.getPlcfHddOffset(), fib.getPlcfHddSize(), 0);
    }

    @Deprecated
    public String getFootnoteSeparator() {
        return this.getAt(0);
    }

    @Deprecated
    public String getFootnoteContSeparator() {
        return this.getAt(1);
    }

    @Deprecated
    public String getFootnoteContNote() {
        return this.getAt(2);
    }

    @Deprecated
    public String getEndnoteSeparator() {
        return this.getAt(3);
    }

    @Deprecated
    public String getEndnoteContSeparator() {
        return this.getAt(4);
    }

    @Deprecated
    public String getEndnoteContNote() {
        return this.getAt(5);
    }

    public Range getFootnoteSeparatorSubrange() {
        return this.getSubrangeAt(0);
    }

    public Range getFootnoteContSeparatorSubrange() {
        return this.getSubrangeAt(1);
    }

    public Range getFootnoteContNoteSubrange() {
        return this.getSubrangeAt(2);
    }

    public Range getEndnoteSeparatorSubrange() {
        return this.getSubrangeAt(3);
    }

    public Range getEndnoteContSeparatorSubrange() {
        return this.getSubrangeAt(4);
    }

    public Range getEndnoteContNoteSubrange() {
        return this.getSubrangeAt(5);
    }

    @Deprecated
    public String getEvenHeader() {
        return this.getAt(6);
    }

    @Deprecated
    public String getOddHeader() {
        return this.getAt(7);
    }

    @Deprecated
    public String getFirstHeader() {
        return this.getAt(10);
    }

    public Range getEvenHeaderSubrange() {
        return this.getSubrangeAt(6);
    }

    public Range getOddHeaderSubrange() {
        return this.getSubrangeAt(7);
    }

    public Range getFirstHeaderSubrange() {
        return this.getSubrangeAt(10);
    }

    public String getHeader(int pageNumber) {
        String eh;
        String fh;
        if (pageNumber == 1 && (fh = this.getFirstHeader()) != null && !fh.isEmpty()) {
            return fh;
        }
        if (pageNumber % 2 == 0 && (eh = this.getEvenHeader()) != null && !eh.isEmpty()) {
            return eh;
        }
        return this.getOddHeader();
    }

    @Deprecated
    public String getEvenFooter() {
        return this.getAt(8);
    }

    @Deprecated
    public String getOddFooter() {
        return this.getAt(9);
    }

    @Deprecated
    public String getFirstFooter() {
        return this.getAt(11);
    }

    public Range getEvenFooterSubrange() {
        return this.getSubrangeAt(8);
    }

    public Range getOddFooterSubrange() {
        return this.getSubrangeAt(9);
    }

    public Range getFirstFooterSubrange() {
        return this.getSubrangeAt(11);
    }

    public String getFooter(int pageNumber) {
        String ef;
        String ff;
        if (pageNumber == 1 && (ff = this.getFirstFooter()) != null && !ff.isEmpty()) {
            return ff;
        }
        if (pageNumber % 2 == 0 && (ef = this.getEvenFooter()) != null && !ef.isEmpty()) {
            return ef;
        }
        return this.getOddFooter();
    }

    @Deprecated
    private String getAt(int plcfHddIndex) {
        if (this.plcfHdd == null) {
            return null;
        }
        GenericPropertyNode prop = this.plcfHdd.getProperty(plcfHddIndex);
        if (prop.getStart() == prop.getEnd()) {
            return "";
        }
        if (prop.getEnd() < prop.getStart()) {
            return "";
        }
        String rawText = this.headerStories.text();
        int start = Math.min(prop.getStart(), rawText.length());
        int end = Math.min(prop.getEnd(), rawText.length());
        String text = rawText.substring(start, end);
        if (this.stripFields) {
            return Range.stripFields(text);
        }
        if (text.equals("\r\r")) {
            return "";
        }
        return text;
    }

    private Range getSubrangeAt(int plcfHddIndex) {
        if (this.plcfHdd == null) {
            return null;
        }
        GenericPropertyNode prop = this.plcfHdd.getProperty(plcfHddIndex);
        if (prop.getStart() == prop.getEnd()) {
            return null;
        }
        if (prop.getEnd() < prop.getStart()) {
            return null;
        }
        int headersLength = this.headerStories.getEndOffset() - this.headerStories.getStartOffset();
        int start = Math.min(prop.getStart(), headersLength);
        int end = Math.min(prop.getEnd(), headersLength);
        return new Range(this.headerStories.getStartOffset() + start, this.headerStories.getStartOffset() + end, this.headerStories);
    }

    public Range getRange() {
        return this.headerStories;
    }

    protected PlexOfCps getPlcfHdd() {
        return this.plcfHdd;
    }

    public boolean areFieldsStripped() {
        return this.stripFields;
    }

    public void setAreFieldsStripped(boolean stripFields) {
        this.stripFields = stripFields;
    }
}

