/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.model.CHPX;
import org.apache.poi.hwpf.model.FileInformationBlock;
import org.apache.poi.hwpf.model.PAPX;
import org.apache.poi.hwpf.model.PropertyNode;
import org.apache.poi.hwpf.model.SEPX;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.model.SubdocumentType;
import org.apache.poi.hwpf.sprm.CharacterSprmCompressor;
import org.apache.poi.hwpf.sprm.ParagraphSprmCompressor;
import org.apache.poi.hwpf.sprm.SprmBuffer;
import org.apache.poi.hwpf.usermodel.BookmarksImpl;
import org.apache.poi.hwpf.usermodel.CharacterProperties;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.ParagraphProperties;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableProperties;
import org.apache.poi.util.DocumentFormatException;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

public class Range {
    private static final Logger LOG = LogManager.getLogger(Range.class);
    @Deprecated
    public static final int TYPE_PARAGRAPH = 0;
    @Deprecated
    public static final int TYPE_CHARACTER = 1;
    @Deprecated
    public static final int TYPE_SECTION = 2;
    @Deprecated
    public static final int TYPE_TEXT = 3;
    @Deprecated
    public static final int TYPE_LISTENTRY = 4;
    @Deprecated
    public static final int TYPE_TABLE = 5;
    @Deprecated
    public static final int TYPE_UNDEFINED = 6;
    private final Range _parent;
    protected final int _start;
    protected int _end;
    protected final HWPFDocumentCore _doc;
    boolean _sectionRangeFound;
    protected final List<SEPX> _sections;
    protected int _sectionStart;
    protected int _sectionEnd;
    protected boolean _parRangeFound;
    protected final List<PAPX> _paragraphs;
    protected int _parStart;
    protected int _parEnd;
    protected boolean _charRangeFound;
    protected List<CHPX> _characters;
    protected int _charStart;
    protected int _charEnd;
    protected StringBuilder _text;

    public Range(int start, int end, HWPFDocumentCore doc) {
        this._start = start;
        this._end = end;
        this._doc = doc;
        this._sections = this._doc.getSectionTable().getSections();
        this._paragraphs = this._doc.getParagraphTable().getParagraphs();
        this._characters = this._doc.getCharacterTable().getTextRuns();
        this._text = this._doc.getText();
        this._parent = null;
        this.sanityCheckStartEnd();
    }

    protected Range(int start, int end, Range parent) {
        this._start = start;
        this._end = end;
        this._doc = parent._doc;
        this._sections = parent._sections;
        this._paragraphs = parent._paragraphs;
        this._characters = parent._characters;
        this._text = parent._text;
        this._parent = parent;
        this.sanityCheckStartEnd();
        this.sanityCheck();
    }

    protected Range(Range other) {
        this._parent = other._parent;
        this._start = other._start;
        this._end = other._end;
        this._doc = other._doc;
        this._sectionRangeFound = other._sectionRangeFound;
        this._sections = other._sections == null ? null : other._sections.stream().map(SEPX::copy).collect(Collectors.toList());
        this._sectionStart = other._sectionStart;
        this._sectionEnd = other._sectionEnd;
        this._parRangeFound = other._parRangeFound;
        this._paragraphs = other._paragraphs == null ? null : other._paragraphs.stream().map(PAPX::copy).collect(Collectors.toList());
        this._parStart = other._parStart;
        this._parEnd = other._parEnd;
        this._charRangeFound = other._charRangeFound;
        this._characters = other._characters == null ? null : other._characters.stream().map(CHPX::copy).collect(Collectors.toList());
        this._charStart = other._charStart;
        this._charEnd = other._charEnd;
        this._text = other._text == null ? null : new StringBuilder(other._text);
    }

    private void sanityCheckStartEnd() {
        if (this._start < 0) {
            throw new IllegalArgumentException("Range start must not be negative. Given " + this._start);
        }
        if (this._end < this._start) {
            throw new IllegalArgumentException("The end (" + this._end + ") must not be before the start (" + this._start + ")");
        }
    }

    public String text() {
        return this._text.substring(this._start, this._end);
    }

    public static String stripFields(String text) {
        if (text.indexOf(19) == -1) {
            return text;
        }
        while (text.indexOf(19) > -1 && text.indexOf(21) > -1) {
            int first13 = text.indexOf(19);
            int next13 = text.indexOf(19, first13 + 1);
            int first14 = text.indexOf(20, first13 + 1);
            int last15 = text.lastIndexOf(21);
            if (last15 < first13) break;
            if (next13 == -1 && first14 == -1) {
                text = text.substring(0, first13) + text.substring(last15 + 1);
                break;
            }
            if (first14 != -1 && (first14 < next13 || next13 == -1)) {
                text = text.substring(0, first13) + text.substring(first14 + 1, last15) + text.substring(last15 + 1);
                continue;
            }
            text = text.substring(0, first13) + text.substring(last15 + 1);
        }
        return text;
    }

    public int numSections() {
        this.initSections();
        return this._sectionEnd - this._sectionStart;
    }

    public int numParagraphs() {
        this.initParagraphs();
        return this._parEnd - this._parStart;
    }

    public int numCharacterRuns() {
        this.initCharacterRuns();
        return this._charEnd - this._charStart;
    }

    public CharacterRun insertBefore(String text) {
        this.initAll();
        this._text.insert(this._start, text);
        this._doc.getCharacterTable().adjustForInsert(this._charStart, text.length());
        this._doc.getParagraphTable().adjustForInsert(this._parStart, text.length());
        this._doc.getSectionTable().adjustForInsert(this._sectionStart, text.length());
        if (this._doc instanceof HWPFDocument) {
            ((BookmarksImpl)((HWPFDocument)this._doc).getBookmarks()).afterInsert(this._start, text.length());
        }
        this.adjustForInsert(text.length());
        this.adjustFIB(text.length());
        this.sanityCheck();
        return this.getCharacterRun(0);
    }

    public CharacterRun insertAfter(String text) {
        this.initAll();
        this._text.insert(this._end, text);
        this._doc.getCharacterTable().adjustForInsert(this._charEnd - 1, text.length());
        this._doc.getParagraphTable().adjustForInsert(this._parEnd - 1, text.length());
        this._doc.getSectionTable().adjustForInsert(this._sectionEnd - 1, text.length());
        if (this._doc instanceof HWPFDocument) {
            ((BookmarksImpl)((HWPFDocument)this._doc).getBookmarks()).afterInsert(this._end, text.length());
        }
        this.adjustForInsert(text.length());
        this.sanityCheck();
        return this.getCharacterRun(this.numCharacterRuns() - 1);
    }

    @Deprecated
    private CharacterRun insertBefore(String text, CharacterProperties props) {
        this.initAll();
        PAPX papx = this._paragraphs.get(this._parStart);
        short istd = papx.getIstd();
        StyleSheet ss = this._doc.getStyleSheet();
        CharacterProperties baseStyle = ss.getCharacterStyle(istd);
        byte[] grpprl = CharacterSprmCompressor.compressCharacterProperty(props, baseStyle);
        SprmBuffer buf = new SprmBuffer(grpprl, 0);
        this._doc.getCharacterTable().insert(this._charStart, this._start, buf);
        return this.insertBefore(text);
    }

    @Deprecated
    private CharacterRun insertAfter(String text, CharacterProperties props) {
        this.initAll();
        PAPX papx = this._paragraphs.get(this._parEnd - 1);
        short istd = papx.getIstd();
        StyleSheet ss = this._doc.getStyleSheet();
        CharacterProperties baseStyle = ss.getCharacterStyle(istd);
        byte[] grpprl = CharacterSprmCompressor.compressCharacterProperty(props, baseStyle);
        SprmBuffer buf = new SprmBuffer(grpprl, 0);
        this._doc.getCharacterTable().insert(this._charEnd, this._end, buf);
        ++this._charEnd;
        return this.insertAfter(text);
    }

    @Deprecated
    private Paragraph insertBefore(ParagraphProperties props, int styleIndex) {
        return this.insertBefore(props, styleIndex, "\r");
    }

    @Deprecated
    private Paragraph insertBefore(ParagraphProperties props, int styleIndex, String text) {
        this.initAll();
        StyleSheet ss = this._doc.getStyleSheet();
        ParagraphProperties baseStyle = ss.getParagraphStyle(styleIndex);
        CharacterProperties baseChp = ss.getCharacterStyle(styleIndex);
        byte[] grpprl = ParagraphSprmCompressor.compressParagraphProperty(props, baseStyle);
        byte[] withIndex = new byte[grpprl.length + 2];
        LittleEndian.putShort(withIndex, 0, (short)styleIndex);
        System.arraycopy(grpprl, 0, withIndex, 2, grpprl.length);
        SprmBuffer buf = new SprmBuffer(withIndex, 2);
        this._doc.getParagraphTable().insert(this._parStart, this._start, buf);
        this.insertBefore(text, baseChp);
        return this.getParagraph(0);
    }

    @Deprecated
    Paragraph insertAfter(ParagraphProperties props, int styleIndex) {
        return this.insertAfter(props, styleIndex, "\r");
    }

    @Deprecated
    Paragraph insertAfter(ParagraphProperties props, int styleIndex, String text) {
        this.initAll();
        StyleSheet ss = this._doc.getStyleSheet();
        ParagraphProperties baseStyle = ss.getParagraphStyle(styleIndex);
        CharacterProperties baseChp = ss.getCharacterStyle(styleIndex);
        byte[] grpprl = ParagraphSprmCompressor.compressParagraphProperty(props, baseStyle);
        byte[] withIndex = new byte[grpprl.length + 2];
        LittleEndian.putShort(withIndex, 0, (short)styleIndex);
        System.arraycopy(grpprl, 0, withIndex, 2, grpprl.length);
        SprmBuffer buf = new SprmBuffer(withIndex, 2);
        this._doc.getParagraphTable().insert(this._parEnd, this._end, buf);
        ++this._parEnd;
        this.insertAfter(text, baseChp);
        return this.getParagraph(this.numParagraphs() - 1);
    }

    public void delete() {
        int x;
        this.initAll();
        int numSections = this._sections.size();
        int numRuns = this._characters.size();
        int numParagraphs = this._paragraphs.size();
        for (x = this._charStart; x < numRuns; ++x) {
            CHPX chpx = this._characters.get(x);
            chpx.adjustForDelete(this._start, this._end - this._start);
        }
        for (x = this._parStart; x < numParagraphs; ++x) {
            PAPX papx = this._paragraphs.get(x);
            papx.adjustForDelete(this._start, this._end - this._start);
        }
        for (x = this._sectionStart; x < numSections; ++x) {
            SEPX sepx = this._sections.get(x);
            sepx.adjustForDelete(this._start, this._end - this._start);
        }
        if (this._doc instanceof HWPFDocument) {
            ((BookmarksImpl)((HWPFDocument)this._doc).getBookmarks()).afterDelete(this._start, this._end - this._start);
        }
        this._text.delete(this._start, this._end);
        Range parent = this._parent;
        if (parent != null) {
            parent.adjustForInsert(-(this._end - this._start));
        }
        this.adjustFIB(-(this._end - this._start));
    }

    public Table insertTableBefore(short columns, int rows) {
        ParagraphProperties parProps = new ParagraphProperties();
        parProps.setFInTable(true);
        parProps.setItap(1);
        int oldEnd = this._end;
        for (int x = 0; x < rows; ++x) {
            Paragraph cell = this.insertBefore(parProps, 4095);
            cell.insertAfter(String.valueOf('\u0007'));
            for (int y = 1; y < columns; ++y) {
                cell = cell.insertAfter(parProps, 4095);
                cell.insertAfter(String.valueOf('\u0007'));
            }
            cell = cell.insertAfter(parProps, 4095, String.valueOf('\u0007'));
            cell.setTableRowEnd(new TableProperties((short)columns));
        }
        int newEnd = this._end;
        int diff = newEnd - oldEnd;
        return new Table(this._start, this._start + diff, this, 1);
    }

    public void replaceText(String newText, boolean addAfter) {
        if (addAfter) {
            int originalEnd = this.getEndOffset();
            this.insertAfter(newText);
            new Range(this.getStartOffset(), originalEnd, this).delete();
        } else {
            int originalStart = this.getStartOffset();
            int originalEnd = this.getEndOffset();
            this.insertBefore(newText);
            new Range(originalStart + newText.length(), originalEnd + newText.length(), this).delete();
        }
    }

    @Internal
    public void replaceText(String pPlaceHolder, String pValue, int pOffset) {
        int absPlaceHolderIndex = this.getStartOffset() + pOffset;
        Range subRange = new Range(absPlaceHolderIndex, absPlaceHolderIndex + pPlaceHolder.length(), this);
        subRange.insertBefore(pValue);
        subRange = new Range(absPlaceHolderIndex + pValue.length(), absPlaceHolderIndex + pPlaceHolder.length() + pValue.length(), this);
        subRange.delete();
    }

    public void replaceText(String pPlaceHolder, String pValue) {
        String text;
        int offset;
        while ((offset = (text = this.text()).indexOf(pPlaceHolder)) >= 0) {
            this.replaceText(pPlaceHolder, pValue, offset);
        }
    }

    public CharacterRun getCharacterRun(int index) {
        short istd;
        this.initCharacterRuns();
        if (index + this._charStart >= this._charEnd) {
            throw new IndexOutOfBoundsException("CHPX #" + index + " (" + (index + this._charStart) + ") not in range [" + this._charStart + "; " + this._charEnd + ")");
        }
        CHPX chpx = this._characters.get(index + this._charStart);
        if (chpx == null) {
            return null;
        }
        if (this instanceof Paragraph) {
            istd = ((Paragraph)this)._istd;
        } else {
            int[] point = this.findRange(this._paragraphs, Math.max(chpx.getStart(), this._start), Math.min(chpx.getEnd(), this._end));
            this.initParagraphs();
            int parStart = Math.max(point[0], this._parStart);
            if (parStart >= this._paragraphs.size()) {
                return null;
            }
            PAPX papx = this._paragraphs.get(point[0]);
            istd = papx.getIstd();
        }
        return new CharacterRun(chpx, this._doc.getStyleSheet(), istd, this);
    }

    public Section getSection(int index) {
        this.initSections();
        SEPX sepx = this._sections.get(index + this._sectionStart);
        return new Section(sepx, this);
    }

    public Paragraph getParagraph(int index) {
        this.initParagraphs();
        if (index + this._parStart >= this._parEnd) {
            throw new IndexOutOfBoundsException("Paragraph #" + index + " (" + (index + this._parStart) + ") not in range [" + this._parStart + "; " + this._parEnd + ")");
        }
        PAPX papx = this._paragraphs.get(index + this._parStart);
        return Paragraph.newParagraph(this, papx);
    }

    public Table getTable(Paragraph paragraph) {
        Paragraph next;
        int tableEndInclusive;
        Paragraph previous;
        if (!paragraph.isInTable()) {
            throw new IllegalArgumentException("This paragraph doesn't belong to a table");
        }
        Paragraph r = paragraph;
        if (r._parent != this) {
            throw new IllegalArgumentException("This paragraph is not a child of this range instance");
        }
        r.initAll();
        int tableLevel = paragraph.getTableLevel();
        if (r._parStart != 0 && (previous = Paragraph.newParagraph(this, this._paragraphs.get(r._parStart - 1))).isInTable() && previous.getTableLevel() == tableLevel && previous._sectionEnd >= r._sectionStart) {
            throw new IllegalArgumentException("This paragraph is not the first one in the table");
        }
        Range overallRange = this._doc.getOverallRange();
        int limit = this._paragraphs.size();
        for (tableEndInclusive = r._parStart; tableEndInclusive < limit - 1 && (next = Paragraph.newParagraph(overallRange, this._paragraphs.get(tableEndInclusive + 1))).isInTable() && next.getTableLevel() >= tableLevel; ++tableEndInclusive) {
        }
        this.initAll();
        if (tableEndInclusive >= this._parEnd) {
            LOG.atWarn().log("The table's bounds [{}; {}) fall outside of this Range paragraphs numbers [{}; {})", (Object)this._parStart, (Object)Unbox.box(tableEndInclusive), (Object)Unbox.box(this._parStart), (Object)Unbox.box(this._parEnd));
        }
        if (tableEndInclusive < 0) {
            throw new ArrayIndexOutOfBoundsException("The table's end is negative, which isn't allowed!");
        }
        int endOffsetExclusive = this._paragraphs.get(tableEndInclusive).getEnd();
        return new Table(paragraph.getStartOffset(), endOffsetExclusive, this, paragraph.getTableLevel());
    }

    protected void initAll() {
        this.initCharacterRuns();
        this.initParagraphs();
        this.initSections();
    }

    private void initParagraphs() {
        if (!this._parRangeFound) {
            int[] point = this.findRange(this._paragraphs, this._start, this._end);
            this._parStart = point[0];
            this._parEnd = point[1];
            this._parRangeFound = true;
        }
    }

    private void initCharacterRuns() {
        if (!this._charRangeFound) {
            int[] point = this.findRange(this._characters, this._start, this._end);
            this._charStart = point[0];
            this._charEnd = point[1];
            this._charRangeFound = true;
        }
    }

    private void initSections() {
        if (!this._sectionRangeFound) {
            int[] point = this.findRange(this._sections, this._sectionStart, this._start, this._end);
            this._sectionStart = point[0];
            this._sectionEnd = point[1];
            this._sectionRangeFound = true;
        }
    }

    private static int binarySearchStart(List<? extends PropertyNode<?>> rpl, int start) {
        if (rpl.isEmpty()) {
            return -1;
        }
        if (rpl.get(0).getStart() >= start) {
            return 0;
        }
        int low = 0;
        int high = rpl.size() - 1;
        while (low <= high) {
            int mid = low + high >>> 1;
            PropertyNode<?> node = rpl.get(mid);
            if (node.getStart() < start) {
                low = mid + 1;
                continue;
            }
            if (node.getStart() > start) {
                high = mid - 1;
                continue;
            }
            assert (node.getStart() == start);
            return mid;
        }
        assert (low != 0);
        return low - 1;
    }

    private static int binarySearchEnd(List<? extends PropertyNode<?>> rpl, int foundStart, int end) {
        if (rpl.get(rpl.size() - 1).getEnd() <= end) {
            return rpl.size() - 1;
        }
        int low = foundStart;
        int high = rpl.size() - 1;
        while (low <= high) {
            int mid = low + high >>> 1;
            PropertyNode<?> node = rpl.get(mid);
            if (node.getEnd() < end) {
                low = mid + 1;
                continue;
            }
            if (node.getEnd() > end) {
                high = mid - 1;
                continue;
            }
            assert (node.getEnd() == end);
            return mid;
        }
        assert (0 <= low && low < rpl.size());
        return low;
    }

    private int[] findRange(List<? extends PropertyNode<?>> rpl, int start, int end) {
        int endIndex;
        int startIndex;
        for (startIndex = Range.binarySearchStart(rpl, start); startIndex > 0 && rpl.get(startIndex - 1).getStart() >= start; --startIndex) {
        }
        for (endIndex = Range.binarySearchEnd(rpl, startIndex, end); endIndex < rpl.size() - 1 && rpl.get(endIndex + 1).getEnd() <= end; ++endIndex) {
        }
        if (startIndex < 0 || startIndex >= rpl.size() || startIndex > endIndex || endIndex < 0 || endIndex >= rpl.size()) {
            throw new DocumentFormatException("problem finding range");
        }
        return new int[]{startIndex, endIndex + 1};
    }

    private int[] findRange(List<? extends PropertyNode<?>> rpl, int min, int start, int end) {
        int x = min;
        if (rpl.size() == min) {
            return new int[]{min, min};
        }
        PropertyNode<?> node = rpl.get(x);
        while (node == null || node.getEnd() <= start && x < rpl.size() - 1) {
            if (++x >= rpl.size()) {
                return new int[]{0, 0};
            }
            node = rpl.get(x);
        }
        if (node.getStart() > end) {
            return new int[]{0, 0};
        }
        if (node.getEnd() <= start) {
            return new int[]{rpl.size(), rpl.size()};
        }
        for (int y = x; y < rpl.size(); ++y) {
            node = rpl.get(y);
            if (node == null || node.getStart() < end && node.getEnd() <= end) continue;
            if (node.getStart() < end) {
                return new int[]{x, y + 1};
            }
            return new int[]{x, y};
        }
        return new int[]{x, rpl.size()};
    }

    protected void reset() {
        this._charRangeFound = false;
        this._parRangeFound = false;
        this._sectionRangeFound = false;
    }

    protected void adjustFIB(int adjustment) {
        if (!(this._doc instanceof HWPFDocument)) {
            throw new IllegalArgumentException("doc must be instance of HWPFDocument");
        }
        FileInformationBlock fib = this._doc.getFileInformationBlock();
        int currentEnd = 0;
        for (SubdocumentType type : SubdocumentType.ORDERED) {
            int currentLength = fib.getSubdocumentTextStreamLength(type);
            if (this._start > (currentEnd += currentLength)) continue;
            fib.setSubdocumentTextStreamLength(type, currentLength + adjustment);
            break;
        }
    }

    private void adjustForInsert(int length) {
        this._end += length;
        this.reset();
        Range parent = this._parent;
        if (parent != null) {
            parent.adjustForInsert(length);
        }
    }

    public int getStartOffset() {
        return this._start;
    }

    public int getEndOffset() {
        return this._end;
    }

    protected HWPFDocumentCore getDocument() {
        return this._doc;
    }

    public String toString() {
        return "Range from " + this.getStartOffset() + " to " + this.getEndOffset() + " (chars)";
    }

    public boolean sanityCheck() {
        int right;
        int left;
        DocumentFormatException.check(this._start >= 0, "start can't be < 0");
        DocumentFormatException.check(this._start <= this._text.length(), "start can't be > text length");
        DocumentFormatException.check(this._end >= 0, "end can't be < 0");
        DocumentFormatException.check(this._end <= this._text.length(), "end can't be > text length");
        DocumentFormatException.check(this._start <= this._end, "start can't be > end");
        if (this._charRangeFound) {
            for (int c = this._charStart; c < this._charEnd; ++c) {
                CHPX chpx = this._characters.get(c);
                left = Math.max(this._start, chpx.getStart());
                DocumentFormatException.check(left < (right = Math.min(this._end, chpx.getEnd())), "left must be < right");
            }
        }
        if (this._parRangeFound) {
            for (int p = this._parStart; p < this._parEnd; ++p) {
                PAPX papx = this._paragraphs.get(p);
                left = Math.max(this._start, papx.getStart());
                DocumentFormatException.check(left < (right = Math.min(this._end, papx.getEnd())), "left must be < right");
            }
        }
        return true;
    }
}

