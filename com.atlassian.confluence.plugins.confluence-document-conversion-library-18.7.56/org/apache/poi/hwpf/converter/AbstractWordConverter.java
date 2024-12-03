/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.converter;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.AbstractWordUtils;
import org.apache.poi.hwpf.converter.DefaultFontReplacer;
import org.apache.poi.hwpf.converter.FontReplacer;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.model.FieldsDocumentPart;
import org.apache.poi.hwpf.usermodel.Bookmark;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Field;
import org.apache.poi.hwpf.usermodel.HWPFList;
import org.apache.poi.hwpf.usermodel.Notes;
import org.apache.poi.hwpf.usermodel.OfficeDrawing;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractWordConverter {
    private static final byte BEL_MARK = 7;
    private static final byte FIELD_BEGIN_MARK = 19;
    private static final byte FIELD_END_MARK = 21;
    private static final byte FIELD_SEPARATOR_MARK = 20;
    private static final int FIELD_PAGE_REFERENCE = 37;
    private static final int FIELD_EMBEDDED_OBJECT = 58;
    private static final int FIELD_DROP_DOWN = 83;
    private static final int FIELD_HYPERLINK = 88;
    private static final Logger LOG = LogManager.getLogger(AbstractWordConverter.class);
    private static final Pattern PATTERN_HYPERLINK_EXTERNAL = Pattern.compile("^[ \\t\\r\\n]*HYPERLINK \"(.*)\".*$");
    private static final Pattern PATTERN_HYPERLINK_LOCAL = Pattern.compile("^[ \\t\\r\\n]*HYPERLINK \\\\l \"(.*)\"[ ](.*)$");
    private static final Pattern PATTERN_PAGEREF = Pattern.compile("^[ \\t\\r\\n]*PAGEREF ([^ ]*)[ \\t\\r\\n]*\\\\h.*$");
    private static final byte SPECCHAR_AUTONUMBERED_FOOTNOTE_REFERENCE = 2;
    private static final byte SPECCHAR_DRAWN_OBJECT = 8;
    protected static final char UNICODECHAR_NO_BREAK_SPACE = '\u00a0';
    protected static final char UNICODECHAR_NONBREAKING_HYPHEN = '\u2011';
    protected static final char UNICODECHAR_ZERO_WIDTH_SPACE = '\u200b';
    private final Set<Bookmark> bookmarkStack = new LinkedHashSet<Bookmark>();
    private FontReplacer fontReplacer = new DefaultFontReplacer();
    private final AbstractWordUtils.NumberingState numberingState = new AbstractWordUtils.NumberingState();
    private PicturesManager picturesManager;

    private static void addToStructures(List<Structure> structures, Structure structure) {
        Iterator<Structure> iterator = structures.iterator();
        while (iterator.hasNext()) {
            Structure another = iterator.next();
            if (another.start <= structure.start && another.end >= structure.start) {
                return;
            }
            if (!(structure.start < another.start && another.start < structure.end || structure.start < another.start && another.end <= structure.end) && (structure.start > another.start || another.end >= structure.end)) continue;
            iterator.remove();
        }
        structures.add(structure);
    }

    protected void afterProcess() {
    }

    protected FontReplacer.Triplet getCharacterRunTriplet(CharacterRun characterRun) {
        FontReplacer.Triplet original = new FontReplacer.Triplet();
        original.bold = characterRun.isBold();
        original.italic = characterRun.isItalic();
        original.fontName = characterRun.getFontName();
        return this.getFontReplacer().update(original);
    }

    public abstract Document getDocument();

    public FontReplacer getFontReplacer() {
        return this.fontReplacer;
    }

    protected int getNumberColumnsSpanned(int[] tableCellEdges, int currentEdgeIndex, TableCell tableCell) {
        int nextEdgeIndex = currentEdgeIndex;
        int colSpan = 0;
        int cellRightEdge = tableCell.getLeftEdge() + tableCell.getWidth();
        while (tableCellEdges[nextEdgeIndex] < cellRightEdge) {
            ++colSpan;
            ++nextEdgeIndex;
        }
        return colSpan;
    }

    protected int getNumberRowsSpanned(Table table, int[] tableCellEdges, int currentRowIndex, int currentColumnIndex, TableCell tableCell) {
        TableRow nextRow;
        if (!tableCell.isFirstVerticallyMerged()) {
            return 1;
        }
        int numRows = table.numRows();
        int count = 1;
        for (int r1 = currentRowIndex + 1; r1 < numRows && currentColumnIndex < (nextRow = table.getRow(r1)).numCells(); ++r1) {
            boolean hasCells = false;
            int currentEdgeIndex = 0;
            for (int c = 0; c < nextRow.numCells(); ++c) {
                TableCell nextTableCell = nextRow.getCell(c);
                if (!nextTableCell.isVerticallyMerged() || nextTableCell.isFirstVerticallyMerged()) {
                    int colSpan = this.getNumberColumnsSpanned(tableCellEdges, currentEdgeIndex, nextTableCell);
                    currentEdgeIndex += colSpan;
                    if (colSpan == 0) continue;
                    hasCells = true;
                    break;
                }
                currentEdgeIndex += this.getNumberColumnsSpanned(tableCellEdges, currentEdgeIndex, nextTableCell);
            }
            if (!hasCells) continue;
            TableCell nextCell = nextRow.getCell(currentColumnIndex);
            if (!nextCell.isVerticallyMerged() || nextCell.isFirstVerticallyMerged()) break;
            ++count;
        }
        return count;
    }

    public PicturesManager getPicturesManager() {
        return this.picturesManager;
    }

    protected abstract void outputCharacters(Element var1, CharacterRun var2, String var3);

    protected abstract void processBookmarks(HWPFDocumentCore var1, Element var2, Range var3, int var4, List<Bookmark> var5);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean processCharacters(HWPFDocumentCore wordDocument, int currentTableLevel, Range range, Element block) {
        if (range == null) {
            return false;
        }
        boolean haveAnyText = false;
        AbstractList structures = new LinkedList();
        if (wordDocument instanceof HWPFDocument) {
            HWPFDocument doc = (HWPFDocument)wordDocument;
            Map<Integer, List<Bookmark>> rangeBookmarks = doc.getBookmarks().getBookmarksStartedBetween(range.getStartOffset(), range.getEndOffset());
            if (rangeBookmarks != null) {
                for (List lists : rangeBookmarks.values()) {
                    for (Bookmark bookmark : lists) {
                        if (this.bookmarkStack.contains(bookmark)) continue;
                        AbstractWordConverter.addToStructures(structures, new Structure(bookmark));
                    }
                }
            }
            int skipUntil = -1;
            for (int c = 0; c < range.numCharacterRuns(); ++c) {
                String text;
                CharacterRun characterRun = range.getCharacterRun(c);
                if (characterRun == null) {
                    throw new AssertionError();
                }
                if (characterRun.getStartOffset() < skipUntil || (text = characterRun.text()) == null || text.length() == 0 || text.charAt(0) != '\u0013') continue;
                Field aliveField = ((HWPFDocument)wordDocument).getFields().getFieldByStartOffset(FieldsDocumentPart.MAIN, characterRun.getStartOffset());
                if (aliveField != null) {
                    AbstractWordConverter.addToStructures(structures, new Structure(aliveField));
                    continue;
                }
                int[] separatorEnd = this.tryDeadField_lookupFieldSeparatorEnd(range, c);
                if (separatorEnd == null) continue;
                AbstractWordConverter.addToStructures(structures, new Structure(new DeadFieldBoundaries(c, separatorEnd[0], separatorEnd[1]), characterRun.getStartOffset(), range.getCharacterRun(separatorEnd[1]).getEndOffset()));
                c = separatorEnd[1];
            }
        }
        structures = new ArrayList(structures);
        Collections.sort(structures);
        int previous = range.getStartOffset();
        for (Structure structure : structures) {
            if (structure.start != previous) {
                Range subrange = new Range(previous, structure.start, range){

                    @Override
                    public String toString() {
                        return "BetweenStructuresSubrange " + super.toString();
                    }
                };
                this.processCharacters(wordDocument, currentTableLevel, subrange, block);
            }
            if (structure.structure instanceof Bookmark) {
                assert (wordDocument instanceof HWPFDocument);
                LinkedList<Bookmark> bookmarks = new LinkedList<Bookmark>();
                for (Bookmark bookmark : ((HWPFDocument)wordDocument).getBookmarks().getBookmarksStartedBetween(structure.start, structure.start + 1).values().iterator().next()) {
                    if (bookmark.getStart() != structure.start || bookmark.getEnd() != structure.end) continue;
                    bookmarks.add(bookmark);
                }
                this.bookmarkStack.addAll(bookmarks);
                try {
                    int end = Math.min(range.getEndOffset(), structure.end);
                    Range subrange = new Range(structure.start, end, range){

                        @Override
                        public String toString() {
                            return "BookmarksSubrange " + super.toString();
                        }
                    };
                    this.processBookmarks(wordDocument, block, subrange, currentTableLevel, bookmarks);
                }
                finally {
                    this.bookmarkStack.removeAll(bookmarks);
                }
            } else if (structure.structure instanceof Field) {
                assert (wordDocument instanceof HWPFDocument);
                Field field = (Field)structure.structure;
                this.processField((HWPFDocument)wordDocument, range, currentTableLevel, field, block);
            } else if (structure.structure instanceof DeadFieldBoundaries) {
                DeadFieldBoundaries boundaries = (DeadFieldBoundaries)structure.structure;
                this.processDeadField(wordDocument, block, range, currentTableLevel, boundaries.beginMark, boundaries.separatorMark, boundaries.endMark);
            } else {
                throw new UnsupportedOperationException("NYI: " + structure.structure.getClass());
            }
            previous = Math.min(range.getEndOffset(), structure.end);
        }
        if (previous != range.getStartOffset()) {
            if (previous > range.getEndOffset()) {
                LOG.atWarn().log("Latest structure in {} ended at #{} after range boundaries [{}; {})", (Object)range, (Object)Unbox.box(previous), (Object)Unbox.box(range.getStartOffset()), (Object)Unbox.box(range.getEndOffset()));
                return true;
            }
            if (previous < range.getEndOffset()) {
                Range subrange = new Range(previous, range.getEndOffset(), range){

                    @Override
                    public String toString() {
                        return "AfterStructureSubrange " + super.toString();
                    }
                };
                this.processCharacters(wordDocument, currentTableLevel, subrange, block);
            }
            return true;
        }
        for (int c = 0; c < range.numCharacterRuns(); ++c) {
            CharacterRun characterRun = range.getCharacterRun(c);
            if (characterRun == null) {
                throw new AssertionError();
            }
            if (wordDocument instanceof HWPFDocument && ((HWPFDocument)wordDocument).getPicturesTable().hasPicture(characterRun)) {
                HWPFDocument newFormat = (HWPFDocument)wordDocument;
                Picture picture = newFormat.getPicturesTable().extractPicture(characterRun, true);
                this.processImage(block, characterRun.text().charAt(0) == '\u0001', picture);
                continue;
            }
            String text = characterRun.text();
            if (text.isEmpty()) continue;
            if (characterRun.isCapitalized() || characterRun.isSmallCaps()) {
                text = text.toUpperCase(LocaleUtil.getUserLocale());
            }
            if (characterRun.isSpecialCharacter()) {
                if (text.charAt(0) == '\u0002' && wordDocument instanceof HWPFDocument) {
                    HWPFDocument doc = (HWPFDocument)wordDocument;
                    this.processNoteAnchor(doc, characterRun, block);
                    continue;
                }
                if (text.charAt(0) == '\b' && wordDocument instanceof HWPFDocument) {
                    HWPFDocument doc = (HWPFDocument)wordDocument;
                    this.processDrawnObject(doc, characterRun, block);
                    continue;
                }
                if (characterRun.isOle2() && wordDocument instanceof HWPFDocument) {
                    HWPFDocument doc = (HWPFDocument)wordDocument;
                    this.processOle2(doc, characterRun, block);
                    continue;
                }
                if (characterRun.isSymbol() && wordDocument instanceof HWPFDocument) {
                    HWPFDocument doc = (HWPFDocument)wordDocument;
                    this.processSymbol(doc, characterRun, block);
                    continue;
                }
            }
            if (text.charAt(0) == '\u0013') {
                Field aliveField;
                if (wordDocument instanceof HWPFDocument && (aliveField = ((HWPFDocument)wordDocument).getFields().getFieldByStartOffset(FieldsDocumentPart.MAIN, characterRun.getStartOffset())) != null) {
                    this.processField((HWPFDocument)wordDocument, range, currentTableLevel, aliveField, block);
                    int continueAfter = aliveField.getFieldEndOffset();
                    while (c < range.numCharacterRuns() && range.getCharacterRun(c).getEndOffset() <= continueAfter) {
                        ++c;
                    }
                    if (c >= range.numCharacterRuns()) continue;
                    --c;
                    continue;
                }
                int skipTo = this.tryDeadField(wordDocument, range, currentTableLevel, c, block);
                if (skipTo == c) continue;
                c = skipTo;
                continue;
            }
            if (text.charAt(0) == '\u0014' || text.charAt(0) == '\u0015' || characterRun.isSpecialCharacter() || characterRun.isObj() || characterRun.isOle2()) continue;
            if (text.endsWith("\r") || text.charAt(text.length() - 1) == '\u0007' && currentTableLevel != Integer.MIN_VALUE) {
                text = text.substring(0, text.length() - 1);
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (char charChar : text.toCharArray()) {
                if (charChar == '\u000b') {
                    if (stringBuilder.length() > 0) {
                        this.outputCharacters(block, characterRun, stringBuilder.toString());
                        stringBuilder.setLength(0);
                    }
                    this.processLineBreak(block, characterRun);
                    continue;
                }
                if (charChar == '\u001e') {
                    stringBuilder.append('\u2011');
                    continue;
                }
                if (charChar == '\u001f') {
                    stringBuilder.append('\u200b');
                    continue;
                }
                if (charChar < ' ' && charChar != '\t' && charChar != '\n' && charChar != '\r') continue;
                stringBuilder.append(charChar);
            }
            if (stringBuilder.length() > 0) {
                this.outputCharacters(block, characterRun, stringBuilder.toString());
                stringBuilder.setLength(0);
            }
            haveAnyText |= StringUtil.isNotBlank(text);
        }
        return haveAnyText;
    }

    protected void processDeadField(HWPFDocumentCore wordDocument, Element currentBlock, Range range, int currentTableLevel, int beginMark, int separatorMark, int endMark) {
        if (beginMark + 1 < separatorMark && separatorMark + 1 < endMark) {
            Range formulaRange = new Range(range.getCharacterRun(beginMark + 1).getStartOffset(), range.getCharacterRun(separatorMark - 1).getEndOffset(), range){

                @Override
                public String toString() {
                    return "Dead field formula subrange: " + super.toString();
                }
            };
            Range valueRange = new Range(range.getCharacterRun(separatorMark + 1).getStartOffset(), range.getCharacterRun(endMark - 1).getEndOffset(), range){

                @Override
                public String toString() {
                    return "Dead field value subrange: " + super.toString();
                }
            };
            String formula = formulaRange.text();
            Matcher matcher = PATTERN_HYPERLINK_LOCAL.matcher(formula);
            if (matcher.matches()) {
                String localref = matcher.group(1);
                this.processPageref(wordDocument, currentBlock, valueRange, currentTableLevel, localref);
                return;
            }
        }
        StringBuilder debug = new StringBuilder("Unsupported field type: \n");
        for (int i = beginMark; i <= endMark; ++i) {
            debug.append("\t");
            debug.append(range.getCharacterRun(i));
            debug.append("\n");
        }
        LOG.atWarn().log(debug);
        Range deadFieldValueSubrage = new Range(range.getCharacterRun(separatorMark).getStartOffset() + 1, range.getCharacterRun(endMark).getStartOffset(), range){

            @Override
            public String toString() {
                return "DeadFieldValueSubrange (" + super.toString() + ")";
            }
        };
        if (separatorMark + 1 < endMark) {
            this.processCharacters(wordDocument, currentTableLevel, deadFieldValueSubrage, currentBlock);
        }
    }

    public void processDocument(HWPFDocumentCore wordDocument) {
        try {
            SummaryInformation summaryInformation = wordDocument.getSummaryInformation();
            if (summaryInformation != null) {
                this.processDocumentInformation(summaryInformation);
            }
        }
        catch (Exception exc) {
            LOG.atWarn().withThrowable(exc).log("Unable to process document summary information");
        }
        Range docRange = wordDocument.getRange();
        if (docRange.numSections() == 1) {
            this.processSingleSection(wordDocument, docRange.getSection(0));
            this.afterProcess();
            return;
        }
        this.processDocumentPart(wordDocument, docRange);
        this.afterProcess();
    }

    protected abstract void processDocumentInformation(SummaryInformation var1);

    protected void processDocumentPart(HWPFDocumentCore wordDocument, Range range) {
        for (int s = 0; s < range.numSections(); ++s) {
            this.processSection(wordDocument, range.getSection(s), s);
        }
    }

    protected void processDrawnObject(HWPFDocument doc, CharacterRun characterRun, Element block) {
        if (this.getPicturesManager() == null) {
            return;
        }
        OfficeDrawing officeDrawing = doc.getOfficeDrawingsMain().getOfficeDrawingAt(characterRun.getStartOffset());
        if (officeDrawing == null) {
            LOG.atWarn().log("Characters #{} references missing drawn object", (Object)characterRun);
            return;
        }
        byte[] pictureData = officeDrawing.getPictureData();
        if (pictureData == null) {
            return;
        }
        float width = (float)(officeDrawing.getRectangleRight() - officeDrawing.getRectangleLeft()) / 1440.0f;
        float height = (float)(officeDrawing.getRectangleBottom() - officeDrawing.getRectangleTop()) / 1440.0f;
        PictureType type = PictureType.findMatchingType(pictureData);
        String path = this.getPicturesManager().savePicture(pictureData, type, "s" + characterRun.getStartOffset() + "." + (Object)((Object)type), width, height);
        this.processDrawnObject(doc, characterRun, officeDrawing, path, block);
    }

    protected abstract void processDrawnObject(HWPFDocument var1, CharacterRun var2, OfficeDrawing var3, String var4, Element var5);

    protected void processDropDownList(Element block, CharacterRun characterRun, String[] values, int defaultIndex) {
        this.outputCharacters(block, characterRun, values[defaultIndex]);
    }

    protected abstract void processEndnoteAutonumbered(HWPFDocument var1, int var2, Element var3, Range var4);

    protected void processField(HWPFDocument wordDocument, Range parentRange, int currentTableLevel, Field field, Element currentBlock) {
        switch (field.getType()) {
            case 37: {
                String formula;
                Matcher matcher;
                Range firstSubrange = field.firstSubrange(parentRange);
                if (firstSubrange == null || !(matcher = PATTERN_PAGEREF.matcher(formula = firstSubrange.text())).find()) break;
                String pageref = matcher.group(1);
                this.processPageref(wordDocument, currentBlock, field.secondSubrange(parentRange), currentTableLevel, pageref);
                return;
            }
            case 58: {
                if (!field.hasSeparator()) {
                    LOG.atWarn().log("{} contains {} with 'Embedded Object' but without separator mark", (Object)parentRange, (Object)field);
                    return;
                }
                CharacterRun separator = field.getMarkSeparatorCharacterRun(parentRange);
                if (!separator.isOle2()) break;
                boolean processed = this.processOle2(wordDocument, separator, currentBlock);
                if (!processed) {
                    this.processCharacters(wordDocument, currentTableLevel, field.secondSubrange(parentRange), currentBlock);
                }
                return;
            }
            case 83: {
                Range fieldContent = field.firstSubrange(parentRange);
                CharacterRun cr = fieldContent.getCharacterRun(fieldContent.numCharacterRuns() - 1);
                String[] values = cr.getDropDownListValues();
                Integer defIndex = cr.getDropDownListDefaultItemIndex();
                if (values == null || values.length <= 0) break;
                this.processDropDownList(currentBlock, cr, values, defIndex == null ? -1 : defIndex);
                return;
            }
            case 88: {
                Range firstSubrange = field.firstSubrange(parentRange);
                if (firstSubrange == null) break;
                String formula = firstSubrange.text();
                Matcher matcher = PATTERN_HYPERLINK_EXTERNAL.matcher(formula);
                if (matcher.matches()) {
                    String hyperlink = matcher.group(1);
                    this.processHyperlink(wordDocument, currentBlock, field.secondSubrange(parentRange), currentTableLevel, hyperlink);
                    return;
                }
                matcher.usePattern(PATTERN_HYPERLINK_LOCAL);
                if (!matcher.matches()) break;
                String hyperlink = matcher.group(1);
                Range textRange = null;
                String text = matcher.group(2);
                if (AbstractWordUtils.isNotEmpty(text)) {
                    textRange = new Range(firstSubrange.getStartOffset() + matcher.start(2), firstSubrange.getStartOffset() + matcher.end(2), firstSubrange){

                        @Override
                        public String toString() {
                            return "Local hyperlink text";
                        }
                    };
                }
                this.processPageref(wordDocument, currentBlock, textRange, currentTableLevel, hyperlink);
                return;
            }
        }
        LOG.atWarn().log("{} contains {} with unsupported type or format", (Object)parentRange, (Object)field);
        this.processCharacters(wordDocument, currentTableLevel, field.secondSubrange(parentRange), currentBlock);
    }

    protected abstract void processFootnoteAutonumbered(HWPFDocument var1, int var2, Element var3, Range var4);

    protected abstract void processHyperlink(HWPFDocumentCore var1, Element var2, Range var3, int var4, String var5);

    protected void processImage(Element currentBlock, boolean inlined, Picture picture) {
        PicturesManager fileManager = this.getPicturesManager();
        if (fileManager != null) {
            String url;
            float aspectRatioX = picture.getHorizontalScalingFactor();
            float aspectRatioY = picture.getVerticalScalingFactor();
            float imageWidth = picture.getDxaGoal();
            if (aspectRatioX > 0.0f) {
                imageWidth *= aspectRatioX / 1000.0f;
            }
            imageWidth /= 1440.0f;
            float imageHeight = picture.getDyaGoal();
            if (aspectRatioY > 0.0f) {
                imageHeight *= aspectRatioY / 1000.0f;
            }
            if (AbstractWordUtils.isNotEmpty(url = fileManager.savePicture(picture.getContent(), picture.suggestPictureType(), picture.suggestFullFileName(), imageWidth, imageHeight /= 1440.0f))) {
                this.processImage(currentBlock, inlined, picture, url);
                return;
            }
        }
        this.processImageWithoutPicturesManager(currentBlock, inlined, picture);
    }

    protected abstract void processImage(Element var1, boolean var2, Picture var3, String var4);

    @Internal
    protected abstract void processImageWithoutPicturesManager(Element var1, boolean var2, Picture var3);

    protected abstract void processLineBreak(Element var1, CharacterRun var2);

    protected void processNoteAnchor(HWPFDocument doc, CharacterRun characterRun, Element block) {
        Notes footnotes = doc.getFootnotes();
        int footIndex = footnotes.getNoteIndexByAnchorPosition(characterRun.getStartOffset());
        if (footIndex != -1) {
            Range footRange = doc.getFootnoteRange();
            int rangeStartOffset = footRange.getStartOffset();
            int noteTextStartOffset = footnotes.getNoteTextStartOffset(footIndex);
            int noteTextEndOffset = footnotes.getNoteTextEndOffset(footIndex);
            Range noteTextRange = new Range(rangeStartOffset + noteTextStartOffset, rangeStartOffset + noteTextEndOffset, doc);
            this.processFootnoteAutonumbered(doc, footIndex, block, noteTextRange);
            return;
        }
        Notes endnotes = doc.getEndnotes();
        int endIndex = endnotes.getNoteIndexByAnchorPosition(characterRun.getStartOffset());
        if (endIndex != -1) {
            Range endnoteRange = doc.getEndnoteRange();
            int rangeStartOffset = endnoteRange.getStartOffset();
            int noteTextStartOffset = endnotes.getNoteTextStartOffset(endIndex);
            int noteTextEndOffset = endnotes.getNoteTextEndOffset(endIndex);
            Range noteTextRange = new Range(rangeStartOffset + noteTextStartOffset, rangeStartOffset + noteTextEndOffset, doc);
            this.processEndnoteAutonumbered(doc, endIndex, block, noteTextRange);
        }
    }

    private boolean processOle2(HWPFDocument doc, CharacterRun characterRun, Element block) {
        Entry entry = doc.getObjectsPool().getObjectById("_" + characterRun.getPicOffset());
        if (entry == null) {
            LOG.atWarn().log("Referenced OLE2 object '{}' not found in ObjectPool", (Object)Unbox.box(characterRun.getPicOffset()));
            return false;
        }
        try {
            return this.processOle2(doc, block, entry);
        }
        catch (Exception exc) {
            LOG.atWarn().withThrowable(exc).log("Unable to convert internal OLE2 object '{}'", (Object)Unbox.box(characterRun.getPicOffset()));
            return false;
        }
    }

    protected boolean processOle2(HWPFDocument wordDocument, Element block, Entry entry) throws Exception {
        return false;
    }

    protected abstract void processPageBreak(HWPFDocumentCore var1, Element var2);

    protected abstract void processPageref(HWPFDocumentCore var1, Element var2, Range var3, int var4, String var5);

    protected abstract void processParagraph(HWPFDocumentCore var1, Element var2, int var3, Paragraph var4, String var5);

    protected void processParagraphes(HWPFDocumentCore wordDocument, Element flow, Range range, int currentTableLevel) {
        int paragraphs = range.numParagraphs();
        for (int p = 0; p < paragraphs; ++p) {
            Paragraph paragraph = range.getParagraph(p);
            if (paragraph.isInTable() && paragraph.getTableLevel() != currentTableLevel) {
                if (paragraph.getTableLevel() < currentTableLevel) {
                    throw new IllegalStateException("Trying to process table cell with higher level (" + paragraph.getTableLevel() + ") than current table level (" + currentTableLevel + ") as inner table part");
                }
                Table table = range.getTable(paragraph);
                this.processTable(wordDocument, flow, table);
                p += table.numParagraphs() - 1;
                continue;
            }
            if (paragraph.text().equals("\f")) {
                this.processPageBreak(wordDocument, flow);
            }
            boolean processed = false;
            if (paragraph.isInList()) {
                try {
                    HWPFList hwpfList = paragraph.getList();
                    String label = AbstractWordUtils.getBulletText(this.numberingState, hwpfList, (char)paragraph.getIlvl());
                    this.processParagraph(wordDocument, flow, currentTableLevel, paragraph, label);
                    processed = true;
                }
                catch (Exception exc) {
                    LOG.atWarn().withThrowable(exc).log("Can't process paragraph as list entry, will be processed without list information");
                }
            }
            if (processed) continue;
            this.processParagraph(wordDocument, flow, currentTableLevel, paragraph, "");
        }
    }

    protected abstract void processSection(HWPFDocumentCore var1, Section var2, int var3);

    protected void processSingleSection(HWPFDocumentCore wordDocument, Section section) {
        this.processSection(wordDocument, section, 0);
    }

    protected void processSymbol(HWPFDocument doc, CharacterRun characterRun, Element block) {
    }

    protected abstract void processTable(HWPFDocumentCore var1, Element var2, Table var3);

    public void setFontReplacer(FontReplacer fontReplacer) {
        this.fontReplacer = fontReplacer;
    }

    public void setPicturesManager(PicturesManager fileManager) {
        this.picturesManager = fileManager;
    }

    protected int tryDeadField(HWPFDocumentCore wordDocument, Range range, int currentTableLevel, int beginMark, Element currentBlock) {
        int[] separatorEnd = this.tryDeadField_lookupFieldSeparatorEnd(range, beginMark);
        if (separatorEnd == null) {
            return beginMark;
        }
        this.processDeadField(wordDocument, currentBlock, range, currentTableLevel, beginMark, separatorEnd[0], separatorEnd[1]);
        return separatorEnd[1];
    }

    private int[] tryDeadField_lookupFieldSeparatorEnd(Range range, int beginMark) {
        int separatorMark = -1;
        int endMark = -1;
        for (int c = beginMark + 1; c < range.numCharacterRuns(); ++c) {
            CharacterRun characterRun = range.getCharacterRun(c);
            String text = characterRun.text();
            if (text.isEmpty()) continue;
            char firstByte = text.charAt(0);
            if (firstByte == '\u0013') {
                int[] nested = this.tryDeadField_lookupFieldSeparatorEnd(range, c);
                if (nested == null) continue;
                c = nested[1];
                continue;
            }
            if (firstByte == '\u0014') {
                if (separatorMark != -1) {
                    return null;
                }
                separatorMark = c;
                continue;
            }
            if (firstByte != '\u0015') continue;
            endMark = c;
            break;
        }
        if (separatorMark == -1 || endMark == -1) {
            return null;
        }
        return new int[]{separatorMark, endMark};
    }

    private static final class Structure
    implements Comparable<Structure> {
        final int end;
        final int start;
        final Object structure;

        Structure(Bookmark bookmark) {
            this.start = bookmark.getStart();
            this.end = bookmark.getEnd();
            this.structure = bookmark;
        }

        Structure(DeadFieldBoundaries deadFieldBoundaries, int start, int end) {
            this.start = start;
            this.end = end;
            this.structure = deadFieldBoundaries;
        }

        Structure(Field field) {
            this.start = field.getFieldStartOffset();
            this.end = field.getFieldEndOffset();
            this.structure = field;
        }

        @Override
        public int compareTo(Structure o) {
            return Integer.compare(this.start, o.start);
        }

        public String toString() {
            return "Structure [" + this.start + "; " + this.end + "): " + this.structure;
        }
    }

    private static class DeadFieldBoundaries {
        final int beginMark;
        final int endMark;
        final int separatorMark;

        public DeadFieldBoundaries(int beginMark, int separatorMark, int endMark) {
            this.beginMark = beginMark;
            this.separatorMark = separatorMark;
            this.endMark = endMark;
        }
    }
}

