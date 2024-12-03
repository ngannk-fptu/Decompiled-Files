/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.converter;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.AbstractWordConverter;
import org.apache.poi.hwpf.converter.AbstractWordUtils;
import org.apache.poi.hwpf.converter.FontReplacer;
import org.apache.poi.hwpf.converter.HtmlDocumentFacade;
import org.apache.poi.hwpf.converter.WordToHtmlUtils;
import org.apache.poi.hwpf.usermodel.Bookmark;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.OfficeDrawing;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class WordToHtmlConverter
extends AbstractWordConverter {
    private static final Logger LOG = LogManager.getLogger(WordToHtmlConverter.class);
    private final Deque<BlockProperies> blocksProperies = new LinkedList<BlockProperies>();
    private final HtmlDocumentFacade htmlDocumentFacade;
    private Element notes;

    public WordToHtmlConverter(Document document) {
        this.htmlDocumentFacade = new HtmlDocumentFacade(document);
    }

    public WordToHtmlConverter(HtmlDocumentFacade htmlDocumentFacade) {
        this.htmlDocumentFacade = htmlDocumentFacade;
    }

    private static String getSectionStyle(Section section) {
        float leftMargin = (float)section.getMarginLeft() / 1440.0f;
        float rightMargin = (float)section.getMarginRight() / 1440.0f;
        float topMargin = (float)section.getMarginTop() / 1440.0f;
        float bottomMargin = (float)section.getMarginBottom() / 1440.0f;
        String style = "margin: " + topMargin + "in " + rightMargin + "in " + bottomMargin + "in " + leftMargin + "in;";
        if (section.getNumColumns() > 1) {
            style = style + "column-count: " + section.getNumColumns() + ";";
            if (section.isColumnsEvenlySpaced()) {
                float distance = (float)section.getDistanceBetweenColumns() / 1440.0f;
                style = style + "column-gap: " + distance + "in;";
            } else {
                style = style + "column-gap: 0.25in;";
            }
        }
        return style;
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {
        if (args.length < 2) {
            System.err.println("Usage: WordToHtmlConverter <inputFile.doc> <saveTo.html>");
            return;
        }
        System.out.println("Converting " + args[0]);
        System.out.println("Saving output to " + args[1]);
        Document doc = WordToHtmlConverter.process(new File(args[0]));
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File(args[1]));
        Transformer serializer = XMLHelper.newTransformer();
        serializer.setOutputProperty("method", "html");
        serializer.transform(domSource, streamResult);
    }

    static Document process(File docFile) throws IOException, ParserConfigurationException {
        DocumentBuilder docBuild = XMLHelper.newDocumentBuilder();
        try (HWPFDocumentCore wordDocument = AbstractWordUtils.loadDoc(docFile);){
            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(docBuild.newDocument());
            wordToHtmlConverter.processDocument(wordDocument);
            Document document = wordToHtmlConverter.getDocument();
            return document;
        }
    }

    @Override
    protected void afterProcess() {
        if (this.notes != null) {
            this.htmlDocumentFacade.getBody().appendChild(this.notes);
        }
        this.htmlDocumentFacade.updateStylesheet();
    }

    @Override
    public Document getDocument() {
        return this.htmlDocumentFacade.getDocument();
    }

    @Override
    protected void outputCharacters(Element pElement, CharacterRun characterRun, String text) {
        Element span = this.htmlDocumentFacade.getDocument().createElement("span");
        pElement.appendChild(span);
        StringBuilder style = new StringBuilder();
        BlockProperies blockProperies = this.blocksProperies.peek();
        FontReplacer.Triplet triplet = this.getCharacterRunTriplet(characterRun);
        if (AbstractWordUtils.isNotEmpty(triplet.fontName) && !Objects.equals(triplet.fontName, blockProperies.pFontName)) {
            style.append("font-family:").append(triplet.fontName).append(";");
        }
        if (characterRun.getFontSize() / 2 != blockProperies.pFontSize) {
            style.append("font-size:").append(characterRun.getFontSize() / 2).append("pt;");
        }
        if (triplet.bold) {
            style.append("font-weight:bold;");
        }
        if (triplet.italic) {
            style.append("font-style:italic;");
        }
        WordToHtmlUtils.addCharactersProperties(characterRun, style);
        if (style.length() != 0) {
            this.htmlDocumentFacade.addStyleClass(span, "s", style.toString());
        }
        Text textNode = this.htmlDocumentFacade.createText(text);
        span.appendChild(textNode);
    }

    @Override
    protected void processBookmarks(HWPFDocumentCore wordDocument, Element currentBlock, Range range, int currentTableLevel, List<Bookmark> rangeBookmarks) {
        Element parent = currentBlock;
        for (Bookmark bookmark : rangeBookmarks) {
            Element bookmarkElement = this.htmlDocumentFacade.createBookmark(bookmark.getName());
            parent.appendChild(bookmarkElement);
            parent = bookmarkElement;
        }
        if (range != null) {
            this.processCharacters(wordDocument, currentTableLevel, range, parent);
        }
    }

    @Override
    protected void processDocumentInformation(SummaryInformation summaryInformation) {
        if (AbstractWordUtils.isNotEmpty(summaryInformation.getTitle())) {
            this.htmlDocumentFacade.setTitle(summaryInformation.getTitle());
        }
        if (AbstractWordUtils.isNotEmpty(summaryInformation.getAuthor())) {
            this.htmlDocumentFacade.addAuthor(summaryInformation.getAuthor());
        }
        if (AbstractWordUtils.isNotEmpty(summaryInformation.getKeywords())) {
            this.htmlDocumentFacade.addKeywords(summaryInformation.getKeywords());
        }
        if (AbstractWordUtils.isNotEmpty(summaryInformation.getComments())) {
            this.htmlDocumentFacade.addDescription(summaryInformation.getComments());
        }
    }

    @Override
    public void processDocumentPart(HWPFDocumentCore wordDocument, Range range) {
        super.processDocumentPart(wordDocument, range);
        this.afterProcess();
    }

    @Override
    protected void processDropDownList(Element block, CharacterRun characterRun, String[] values, int defaultIndex) {
        Element select = this.htmlDocumentFacade.createSelect();
        for (int i = 0; i < values.length; ++i) {
            select.appendChild(this.htmlDocumentFacade.createOption(values[i], defaultIndex == i));
        }
        block.appendChild(select);
    }

    @Override
    protected void processDrawnObject(HWPFDocument doc, CharacterRun characterRun, OfficeDrawing officeDrawing, String path, Element block) {
        Element img = this.htmlDocumentFacade.createImage(path);
        block.appendChild(img);
    }

    @Override
    protected void processEndnoteAutonumbered(HWPFDocument wordDocument, int noteIndex, Element block, Range endnoteTextRange) {
        this.processNoteAutonumbered(wordDocument, "end", noteIndex, block, endnoteTextRange);
    }

    @Override
    protected void processFootnoteAutonumbered(HWPFDocument wordDocument, int noteIndex, Element block, Range footnoteTextRange) {
        this.processNoteAutonumbered(wordDocument, "foot", noteIndex, block, footnoteTextRange);
    }

    @Override
    protected void processHyperlink(HWPFDocumentCore wordDocument, Element currentBlock, Range textRange, int currentTableLevel, String hyperlink) {
        Element basicLink = this.htmlDocumentFacade.createHyperlink(hyperlink);
        currentBlock.appendChild(basicLink);
        if (textRange != null) {
            this.processCharacters(wordDocument, currentTableLevel, textRange, basicLink);
        }
    }

    @Override
    protected void processImage(Element currentBlock, boolean inlined, Picture picture, String imageSourcePath) {
        Element root;
        float cropBottom;
        float cropTop;
        float imageHeight;
        float cropLeft;
        float cropRight;
        float imageWidth;
        int aspectRatioX = picture.getHorizontalScalingFactor();
        int aspectRatioY = picture.getVerticalScalingFactor();
        if (aspectRatioX > 0) {
            imageWidth = (float)aspectRatioX / 1000.0f * (float)picture.getDxaGoal() / 1440.0f;
            cropRight = (float)aspectRatioX / 1000.0f * (float)picture.getDxaCropRight() / 1440.0f;
            cropLeft = (float)aspectRatioX / 1000.0f * (float)picture.getDxaCropLeft() / 1440.0f;
        } else {
            imageWidth = (float)picture.getDxaGoal() / 1440.0f;
            cropRight = (float)picture.getDxaCropRight() / 1440.0f;
            cropLeft = (float)picture.getDxaCropLeft() / 1440.0f;
        }
        if (aspectRatioY > 0) {
            imageHeight = (float)aspectRatioY / 1000.0f * (float)picture.getDyaGoal() / 1440.0f;
            cropTop = (float)aspectRatioY / 1000.0f * (float)picture.getDyaCropTop() / 1440.0f;
            cropBottom = (float)aspectRatioY / 1000.0f * (float)picture.getDyaCropBottom() / 1440.0f;
        } else {
            imageHeight = (float)picture.getDyaGoal() / 1440.0f;
            cropTop = (float)picture.getDyaCropTop() / 1440.0f;
            cropBottom = (float)picture.getDyaCropBottom() / 1440.0f;
        }
        if (Math.abs(cropTop) + Math.abs(cropRight) + Math.abs(cropBottom) + Math.abs(cropLeft) > 0.0f) {
            float visibleWidth = Math.max(0.0f, imageWidth - cropLeft - cropRight);
            float visibleHeight = Math.max(0.0f, imageHeight - cropTop - cropBottom);
            root = this.htmlDocumentFacade.createBlock();
            this.htmlDocumentFacade.addStyleClass(root, "d", "vertical-align:text-bottom;width:" + visibleWidth + "in;height:" + visibleHeight + "in;");
            Element inner = this.htmlDocumentFacade.createBlock();
            this.htmlDocumentFacade.addStyleClass(inner, "d", "position:relative;width:" + visibleWidth + "in;height:" + visibleHeight + "in;overflow:hidden;");
            root.appendChild(inner);
            Element image = this.htmlDocumentFacade.createImage(imageSourcePath);
            this.htmlDocumentFacade.addStyleClass(image, "i", "position:absolute;left:-" + cropLeft + ";top:-" + cropTop + ";width:" + imageWidth + "in;height:" + imageHeight + "in;");
            inner.appendChild(image);
        } else {
            root = this.htmlDocumentFacade.createImage(imageSourcePath);
            root.setAttribute("style", "width:" + imageWidth + "in;height:" + imageHeight + "in;vertical-align:text-bottom;");
        }
        currentBlock.appendChild(root);
    }

    @Override
    protected void processImageWithoutPicturesManager(Element currentBlock, boolean inlined, Picture picture) {
        currentBlock.appendChild(this.htmlDocumentFacade.getDocument().createComment("Image link to '" + picture.suggestFullFileName() + "' can be here"));
    }

    @Override
    protected void processLineBreak(Element block, CharacterRun characterRun) {
        block.appendChild(this.htmlDocumentFacade.createLineBreak());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void processNoteAutonumbered(HWPFDocument doc, String type, int noteIndex, Element block, Range noteTextRange) {
        String textIndex = String.valueOf(noteIndex + 1);
        String textIndexClass = this.htmlDocumentFacade.getOrCreateCssClass("a", "vertical-align:super;font-size:smaller;");
        String forwardNoteLink = type + "note_" + textIndex;
        String backwardNoteLink = type + "note_back_" + textIndex;
        Element anchor = this.htmlDocumentFacade.createHyperlink("#" + forwardNoteLink);
        anchor.setAttribute("name", backwardNoteLink);
        anchor.setAttribute("class", textIndexClass + " " + type + "noteanchor");
        anchor.setTextContent(textIndex);
        block.appendChild(anchor);
        if (this.notes == null) {
            this.notes = this.htmlDocumentFacade.createBlock();
            this.notes.setAttribute("class", "notes");
        }
        Element note = this.htmlDocumentFacade.createBlock();
        note.setAttribute("class", type + "note");
        this.notes.appendChild(note);
        Element bookmark = this.htmlDocumentFacade.createBookmark(forwardNoteLink);
        bookmark.setAttribute("href", "#" + backwardNoteLink);
        bookmark.setTextContent(textIndex);
        bookmark.setAttribute("class", textIndexClass + " " + type + "noteindex");
        note.appendChild(bookmark);
        note.appendChild(this.htmlDocumentFacade.createText(" "));
        Element span = this.htmlDocumentFacade.getDocument().createElement("span");
        span.setAttribute("class", type + "notetext");
        note.appendChild(span);
        this.blocksProperies.add(new BlockProperies("", -1));
        try {
            this.processCharacters(doc, Integer.MIN_VALUE, noteTextRange, span);
        }
        finally {
            this.blocksProperies.pop();
        }
    }

    @Override
    protected void processPageBreak(HWPFDocumentCore wordDocument, Element flow) {
        flow.appendChild(this.htmlDocumentFacade.createLineBreak());
    }

    @Override
    protected void processPageref(HWPFDocumentCore hwpfDocument, Element currentBlock, Range textRange, int currentTableLevel, String pageref) {
        Element basicLink = this.htmlDocumentFacade.createHyperlink("#" + pageref);
        currentBlock.appendChild(basicLink);
        if (textRange != null) {
            this.processCharacters(hwpfDocument, currentTableLevel, textRange, basicLink);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void processParagraph(HWPFDocumentCore hwpfDocument, Element parentElement, int currentTableLevel, Paragraph paragraph, String bulletText) {
        String pFontName;
        int pFontSize;
        Element pElement = this.htmlDocumentFacade.createParagraph();
        parentElement.appendChild(pElement);
        StringBuilder style = new StringBuilder();
        WordToHtmlUtils.addParagraphProperties(paragraph, style);
        int charRuns = paragraph.numCharacterRuns();
        if (charRuns == 0) {
            return;
        }
        CharacterRun characterRun = paragraph.getCharacterRun(0);
        if (characterRun != null) {
            FontReplacer.Triplet triplet = this.getCharacterRunTriplet(characterRun);
            pFontSize = characterRun.getFontSize() / 2;
            pFontName = triplet.fontName;
            WordToHtmlUtils.addFontFamily(pFontName, style);
            WordToHtmlUtils.addFontSize(pFontSize, style);
        } else {
            pFontSize = -1;
            pFontName = "";
        }
        this.blocksProperies.push(new BlockProperies(pFontName, pFontSize));
        try {
            if (AbstractWordUtils.isNotEmpty(bulletText)) {
                if (bulletText.endsWith("\t")) {
                    float defaultTab = 720.0f;
                    float firstLinePosition = (float)(paragraph.getIndentFromLeft() + paragraph.getFirstLineIndent()) + 20.0f;
                    float nextStop = (float)(Math.ceil(firstLinePosition / 720.0f) * 720.0);
                    float spanMinWidth = nextStop - firstLinePosition;
                    Element span = this.htmlDocumentFacade.getDocument().createElement("span");
                    this.htmlDocumentFacade.addStyleClass(span, "s", "display: inline-block; text-indent: 0; min-width: " + spanMinWidth / 1440.0f + "in;");
                    pElement.appendChild(span);
                    Text textNode = this.htmlDocumentFacade.createText(bulletText.substring(0, bulletText.length() - 1) + '\u200b' + '\u00a0');
                    span.appendChild(textNode);
                } else {
                    Text textNode = this.htmlDocumentFacade.createText(bulletText.substring(0, bulletText.length() - 1));
                    pElement.appendChild(textNode);
                }
            }
            this.processCharacters(hwpfDocument, currentTableLevel, paragraph, pElement);
        }
        finally {
            this.blocksProperies.pop();
        }
        if (style.length() > 0) {
            this.htmlDocumentFacade.addStyleClass(pElement, "p", style.toString());
        }
        WordToHtmlUtils.compactSpans(pElement);
    }

    @Override
    protected void processSection(HWPFDocumentCore wordDocument, Section section, int sectionCounter) {
        Element div = this.htmlDocumentFacade.createBlock();
        this.htmlDocumentFacade.addStyleClass(div, "d", WordToHtmlConverter.getSectionStyle(section));
        this.htmlDocumentFacade.getBody().appendChild(div);
        this.processParagraphes(wordDocument, div, section, Integer.MIN_VALUE);
    }

    @Override
    protected void processSingleSection(HWPFDocumentCore wordDocument, Section section) {
        this.htmlDocumentFacade.addStyleClass(this.htmlDocumentFacade.getBody(), "b", WordToHtmlConverter.getSectionStyle(section));
        this.processParagraphes(wordDocument, this.htmlDocumentFacade.getBody(), section, Integer.MIN_VALUE);
    }

    @Override
    protected void processTable(HWPFDocumentCore hwpfDocument, Element flow, Table table) {
        int r;
        Element tableHeader = this.htmlDocumentFacade.createTableHeader();
        Element tableBody = this.htmlDocumentFacade.createTableBody();
        int[] tableCellEdges = AbstractWordUtils.buildTableCellEdgesArray(table);
        int tableRows = table.numRows();
        int maxColumns = Integer.MIN_VALUE;
        for (r = 0; r < tableRows; ++r) {
            maxColumns = Math.max(maxColumns, table.getRow(r).numCells());
        }
        for (r = 0; r < tableRows; ++r) {
            TableRow tableRow = table.getRow(r);
            Element tableRowElement = this.htmlDocumentFacade.createTableRow();
            StringBuilder tableRowStyle = new StringBuilder();
            WordToHtmlUtils.addTableRowProperties(tableRow, tableRowStyle);
            int currentEdgeIndex = 0;
            int rowCells = tableRow.numCells();
            for (int c = 0; c < rowCells; ++c) {
                int rowSpan;
                TableCell tableCell = tableRow.getCell(c);
                if (tableCell.isVerticallyMerged() && !tableCell.isFirstVerticallyMerged()) {
                    currentEdgeIndex += this.getNumberColumnsSpanned(tableCellEdges, currentEdgeIndex, tableCell);
                    continue;
                }
                Element tableCellElement = tableRow.isTableHeader() ? this.htmlDocumentFacade.createTableHeaderCell() : this.htmlDocumentFacade.createTableCell();
                StringBuilder tableCellStyle = new StringBuilder();
                WordToHtmlUtils.addTableCellProperties(tableRow, tableCell, r == 0, r == tableRows - 1, c == 0, c == rowCells - 1, tableCellStyle);
                int colSpan = this.getNumberColumnsSpanned(tableCellEdges, currentEdgeIndex, tableCell);
                currentEdgeIndex += colSpan;
                if (colSpan == 0) continue;
                if (colSpan != 1) {
                    tableCellElement.setAttribute("colspan", String.valueOf(colSpan));
                }
                if ((rowSpan = this.getNumberRowsSpanned(table, tableCellEdges, r, c, tableCell)) > 1) {
                    tableCellElement.setAttribute("rowspan", String.valueOf(rowSpan));
                }
                this.processParagraphes(hwpfDocument, tableCellElement, tableCell, table.getTableLevel());
                if (!tableCellElement.hasChildNodes()) {
                    tableCellElement.appendChild(this.htmlDocumentFacade.createParagraph());
                }
                if (tableCellStyle.length() > 0) {
                    this.htmlDocumentFacade.addStyleClass(tableCellElement, tableCellElement.getTagName(), tableCellStyle.toString());
                }
                tableRowElement.appendChild(tableCellElement);
            }
            if (tableRowStyle.length() > 0) {
                tableRowElement.setAttribute("class", this.htmlDocumentFacade.getOrCreateCssClass("r", tableRowStyle.toString()));
            }
            if (tableRow.isTableHeader()) {
                tableHeader.appendChild(tableRowElement);
                continue;
            }
            tableBody.appendChild(tableRowElement);
        }
        Element tableElement = this.htmlDocumentFacade.createTable();
        tableElement.setAttribute("class", this.htmlDocumentFacade.getOrCreateCssClass("t", "table-layout:fixed;border-collapse:collapse;border-spacing:0;"));
        if (tableHeader.hasChildNodes()) {
            tableElement.appendChild(tableHeader);
        }
        if (tableBody.hasChildNodes()) {
            tableElement.appendChild(tableBody);
            flow.appendChild(tableElement);
        } else {
            LOG.atWarn().log("Table without body starting at [{}; {})", (Object)Unbox.box(table.getStartOffset()), (Object)Unbox.box(table.getEndOffset()));
        }
    }

    private static class BlockProperies {
        final String pFontName;
        final int pFontSize;

        public BlockProperies(String pFontName, int pFontSize) {
            this.pFontName = pFontName;
            this.pFontSize = pFontSize;
        }
    }
}

