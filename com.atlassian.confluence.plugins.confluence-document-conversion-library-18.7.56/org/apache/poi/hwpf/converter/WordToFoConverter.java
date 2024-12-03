/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.AbstractWordConverter;
import org.apache.poi.hwpf.converter.FoDocumentFacade;
import org.apache.poi.hwpf.converter.FontReplacer;
import org.apache.poi.hwpf.converter.WordToFoUtils;
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
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class WordToFoConverter
extends AbstractWordConverter {
    private static final Logger LOG = LogManager.getLogger(WordToFoConverter.class);
    private List<Element> endnotes = new ArrayList<Element>(0);
    protected final FoDocumentFacade foDocumentFacade;
    private AtomicInteger internalLinkCounter = new AtomicInteger(0);
    private boolean outputCharactersLanguage;
    private Set<String> usedIds = new LinkedHashSet<String>();

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: WordToFoConverter <inputFile.doc> <saveTo.fo>");
            return;
        }
        System.out.println("Converting " + args[0]);
        System.out.println("Saving output to " + args[1]);
        Document doc = WordToFoConverter.process(new File(args[0]));
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File(args[1]));
        Transformer serializer = XMLHelper.newTransformer();
        serializer.transform(domSource, streamResult);
    }

    static Document process(File docFile) throws Exception {
        DocumentBuilder docBuild = XMLHelper.newDocumentBuilder();
        try (HWPFDocumentCore hwpfDocument = WordToFoUtils.loadDoc(docFile);){
            WordToFoConverter wordToFoConverter = new WordToFoConverter(docBuild.newDocument());
            wordToFoConverter.processDocument(hwpfDocument);
            Document document = wordToFoConverter.getDocument();
            return document;
        }
    }

    public WordToFoConverter(Document document) {
        this.foDocumentFacade = new FoDocumentFacade(document);
    }

    public WordToFoConverter(FoDocumentFacade foDocumentFacade) {
        this.foDocumentFacade = foDocumentFacade;
    }

    protected Element createNoteInline(String noteIndexText) {
        Element inline = this.foDocumentFacade.createInline();
        inline.setTextContent(noteIndexText);
        inline.setAttribute("baseline-shift", "super");
        inline.setAttribute("font-size", "smaller");
        return inline;
    }

    protected String createPageMaster(Section section, String type, int sectionIndex) {
        float height = (float)section.getPageHeight() / 1440.0f;
        float width = (float)section.getPageWidth() / 1440.0f;
        float leftMargin = (float)section.getMarginLeft() / 1440.0f;
        float rightMargin = (float)section.getMarginRight() / 1440.0f;
        float topMargin = (float)section.getMarginTop() / 1440.0f;
        float bottomMargin = (float)section.getMarginBottom() / 1440.0f;
        String pageMasterName = type + "-page" + sectionIndex;
        Element pageMaster = this.foDocumentFacade.addSimplePageMaster(pageMasterName);
        pageMaster.setAttribute("page-height", height + "in");
        pageMaster.setAttribute("page-width", width + "in");
        Element regionBody = this.foDocumentFacade.addRegionBody(pageMaster);
        regionBody.setAttribute("margin", topMargin + "in " + rightMargin + "in " + bottomMargin + "in " + leftMargin + "in");
        if (section.getNumColumns() > 1) {
            regionBody.setAttribute("column-count", "" + section.getNumColumns());
            if (section.isColumnsEvenlySpaced()) {
                float distance = (float)section.getDistanceBetweenColumns() / 1440.0f;
                regionBody.setAttribute("column-gap", distance + "in");
            } else {
                regionBody.setAttribute("column-gap", "0.25in");
            }
        }
        return pageMasterName;
    }

    @Override
    public Document getDocument() {
        return this.foDocumentFacade.getDocument();
    }

    public boolean isOutputCharactersLanguage() {
        return this.outputCharactersLanguage;
    }

    @Override
    protected void outputCharacters(Element block, CharacterRun characterRun, String text) {
        Element inline = this.foDocumentFacade.createInline();
        FontReplacer.Triplet triplet = this.getCharacterRunTriplet(characterRun);
        if (WordToFoUtils.isNotEmpty(triplet.fontName)) {
            WordToFoUtils.setFontFamily(inline, triplet.fontName);
        }
        WordToFoUtils.setBold(inline, triplet.bold);
        WordToFoUtils.setItalic(inline, triplet.italic);
        WordToFoUtils.setFontSize(inline, characterRun.getFontSize() / 2);
        WordToFoUtils.setCharactersProperties(characterRun, inline);
        if (this.isOutputCharactersLanguage()) {
            WordToFoUtils.setLanguage(characterRun, inline);
        }
        block.appendChild(inline);
        Text textNode = this.foDocumentFacade.createText(text);
        inline.appendChild(textNode);
    }

    @Override
    protected void processBookmarks(HWPFDocumentCore wordDocument, Element currentBlock, Range range, int currentTableLevel, List<Bookmark> rangeBookmarks) {
        Element parent = currentBlock;
        for (Bookmark bookmark : rangeBookmarks) {
            String idName;
            Element bookmarkElement = this.foDocumentFacade.createInline();
            if (!this.setId(bookmarkElement, idName = "bookmark_" + bookmark.getName())) continue;
            parent.appendChild(bookmarkElement);
            parent = bookmarkElement;
        }
        if (range != null) {
            this.processCharacters(wordDocument, currentTableLevel, range, parent);
        }
    }

    @Override
    protected void processDocumentInformation(SummaryInformation summaryInformation) {
        if (WordToFoUtils.isNotEmpty(summaryInformation.getTitle())) {
            this.foDocumentFacade.setTitle(summaryInformation.getTitle());
        }
        if (WordToFoUtils.isNotEmpty(summaryInformation.getAuthor())) {
            this.foDocumentFacade.setCreator(summaryInformation.getAuthor());
        }
        if (WordToFoUtils.isNotEmpty(summaryInformation.getKeywords())) {
            this.foDocumentFacade.setKeywords(summaryInformation.getKeywords());
        }
        if (WordToFoUtils.isNotEmpty(summaryInformation.getComments())) {
            this.foDocumentFacade.setDescription(summaryInformation.getComments());
        }
    }

    @Override
    protected void processDrawnObject(HWPFDocument doc, CharacterRun characterRun, OfficeDrawing officeDrawing, String path, Element block) {
        Element externalGraphic = this.foDocumentFacade.createExternalGraphic(path);
        block.appendChild(externalGraphic);
    }

    @Override
    protected void processEndnoteAutonumbered(HWPFDocument wordDocument, int noteIndex, Element block, Range endnoteTextRange) {
        String textIndex = String.valueOf(this.internalLinkCounter.incrementAndGet());
        String forwardLinkName = "endnote_" + textIndex;
        String backwardLinkName = "endnote_back_" + textIndex;
        Element forwardLink = this.foDocumentFacade.createBasicLinkInternal(forwardLinkName);
        forwardLink.appendChild(this.createNoteInline(textIndex));
        this.setId(forwardLink, backwardLinkName);
        block.appendChild(forwardLink);
        Element endnote = this.foDocumentFacade.createBlock();
        Element backwardLink = this.foDocumentFacade.createBasicLinkInternal(backwardLinkName);
        backwardLink.appendChild(this.createNoteInline(textIndex + " "));
        this.setId(backwardLink, forwardLinkName);
        endnote.appendChild(backwardLink);
        this.processCharacters(wordDocument, Integer.MIN_VALUE, endnoteTextRange, endnote);
        WordToFoUtils.compactInlines(endnote);
        this.endnotes.add(endnote);
    }

    @Override
    protected void processFootnoteAutonumbered(HWPFDocument wordDocument, int noteIndex, Element block, Range footnoteTextRange) {
        String textIndex = String.valueOf(this.internalLinkCounter.incrementAndGet());
        String forwardLinkName = "footnote_" + textIndex;
        String backwardLinkName = "footnote_back_" + textIndex;
        Element footNote = this.foDocumentFacade.createFootnote();
        block.appendChild(footNote);
        Element inline = this.foDocumentFacade.createInline();
        Element forwardLink = this.foDocumentFacade.createBasicLinkInternal(forwardLinkName);
        forwardLink.appendChild(this.createNoteInline(textIndex));
        this.setId(forwardLink, backwardLinkName);
        inline.appendChild(forwardLink);
        footNote.appendChild(inline);
        Element footnoteBody = this.foDocumentFacade.createFootnoteBody();
        Element footnoteBlock = this.foDocumentFacade.createBlock();
        Element backwardLink = this.foDocumentFacade.createBasicLinkInternal(backwardLinkName);
        backwardLink.appendChild(this.createNoteInline(textIndex + " "));
        this.setId(backwardLink, forwardLinkName);
        footnoteBlock.appendChild(backwardLink);
        footnoteBody.appendChild(footnoteBlock);
        footNote.appendChild(footnoteBody);
        this.processCharacters(wordDocument, Integer.MIN_VALUE, footnoteTextRange, footnoteBlock);
        WordToFoUtils.compactInlines(footnoteBlock);
    }

    @Override
    protected void processHyperlink(HWPFDocumentCore wordDocument, Element currentBlock, Range textRange, int currentTableLevel, String hyperlink) {
        Element basicLink = this.foDocumentFacade.createBasicLinkExternal(hyperlink);
        currentBlock.appendChild(basicLink);
        if (textRange != null) {
            this.processCharacters(wordDocument, currentTableLevel, textRange, basicLink);
        }
    }

    @Override
    protected void processImage(Element currentBlock, boolean inlined, Picture picture, String url) {
        Element externalGraphic = this.foDocumentFacade.createExternalGraphic(url);
        WordToFoUtils.setPictureProperties(picture, externalGraphic);
        currentBlock.appendChild(externalGraphic);
    }

    @Override
    protected void processImageWithoutPicturesManager(Element currentBlock, boolean inlined, Picture picture) {
        currentBlock.appendChild(this.foDocumentFacade.getDocument().createComment("Image link to '" + picture.suggestFullFileName() + "' can be here"));
    }

    @Override
    protected void processLineBreak(Element block, CharacterRun characterRun) {
        block.appendChild(this.foDocumentFacade.createBlock());
    }

    @Override
    protected void processPageBreak(HWPFDocumentCore wordDocument, Element flow) {
        Element lastElement;
        Node lastChild;
        Element block = null;
        NodeList childNodes = flow.getChildNodes();
        if (childNodes.getLength() > 0 && (lastChild = childNodes.item(childNodes.getLength() - 1)) instanceof Element && !(lastElement = (Element)lastChild).hasAttribute("break-after")) {
            block = lastElement;
        }
        if (block == null) {
            block = this.foDocumentFacade.createBlock();
            flow.appendChild(block);
        }
        block.setAttribute("break-after", "page");
    }

    @Override
    protected void processPageref(HWPFDocumentCore hwpfDocument, Element currentBlock, Range textRange, int currentTableLevel, String pageref) {
        Element basicLink = this.foDocumentFacade.createBasicLinkInternal("bookmark_" + pageref);
        currentBlock.appendChild(basicLink);
        if (textRange != null) {
            this.processCharacters(hwpfDocument, currentTableLevel, textRange, basicLink);
        }
    }

    @Override
    protected void processParagraph(HWPFDocumentCore hwpfDocument, Element parentFopElement, int currentTableLevel, Paragraph paragraph, String bulletText) {
        Element block = this.foDocumentFacade.createBlock();
        parentFopElement.appendChild(block);
        WordToFoUtils.setParagraphProperties(paragraph, block);
        int charRuns = paragraph.numCharacterRuns();
        if (charRuns == 0) {
            return;
        }
        boolean haveAnyText = false;
        if (WordToFoUtils.isNotEmpty(bulletText)) {
            Element inline = this.foDocumentFacade.createInline();
            block.appendChild(inline);
            Text textNode = this.foDocumentFacade.createText(bulletText);
            inline.appendChild(textNode);
            haveAnyText |= StringUtil.isNotBlank(bulletText);
        }
        if (!(haveAnyText = this.processCharacters(hwpfDocument, currentTableLevel, paragraph, block))) {
            Element leader = this.foDocumentFacade.createLeader();
            block.appendChild(leader);
        }
        WordToFoUtils.compactInlines(block);
    }

    @Override
    protected void processSection(HWPFDocumentCore wordDocument, Section section, int sectionCounter) {
        String regularPage = this.createPageMaster(section, "page", sectionCounter);
        Element pageSequence = this.foDocumentFacade.addPageSequence(regularPage);
        Element flow = this.foDocumentFacade.addFlowToPageSequence(pageSequence, "xsl-region-body");
        this.processParagraphes(wordDocument, flow, section, Integer.MIN_VALUE);
        if (this.endnotes != null && !this.endnotes.isEmpty()) {
            for (Element endnote : this.endnotes) {
                flow.appendChild(endnote);
            }
            this.endnotes.clear();
        }
    }

    @Override
    protected void processTable(HWPFDocumentCore wordDocument, Element flow, Table table) {
        int r;
        Element tableHeader = this.foDocumentFacade.createTableHeader();
        Element tableBody = this.foDocumentFacade.createTableBody();
        int[] tableCellEdges = WordToFoUtils.buildTableCellEdgesArray(table);
        int tableRows = table.numRows();
        int maxColumns = Integer.MIN_VALUE;
        for (r = 0; r < tableRows; ++r) {
            maxColumns = Math.max(maxColumns, table.getRow(r).numCells());
        }
        for (r = 0; r < tableRows; ++r) {
            TableRow tableRow = table.getRow(r);
            Element tableRowElement = this.foDocumentFacade.createTableRow();
            WordToFoUtils.setTableRowProperties(tableRow, tableRowElement);
            int currentEdgeIndex = 0;
            int rowCells = tableRow.numCells();
            for (int c = 0; c < rowCells; ++c) {
                int rowSpan;
                TableCell tableCell = tableRow.getCell(c);
                if (tableCell.isVerticallyMerged() && !tableCell.isFirstVerticallyMerged()) {
                    currentEdgeIndex += this.getNumberColumnsSpanned(tableCellEdges, currentEdgeIndex, tableCell);
                    continue;
                }
                Element tableCellElement = this.foDocumentFacade.createTableCell();
                WordToFoUtils.setTableCellProperties(tableRow, tableCell, tableCellElement, r == 0, r == tableRows - 1, c == 0, c == rowCells - 1);
                int colSpan = this.getNumberColumnsSpanned(tableCellEdges, currentEdgeIndex, tableCell);
                currentEdgeIndex += colSpan;
                if (colSpan == 0) continue;
                if (colSpan != 1) {
                    tableCellElement.setAttribute("number-columns-spanned", String.valueOf(colSpan));
                }
                if ((rowSpan = this.getNumberRowsSpanned(table, tableCellEdges, r, c, tableCell)) > 1) {
                    tableCellElement.setAttribute("number-rows-spanned", String.valueOf(rowSpan));
                }
                this.processParagraphes(wordDocument, tableCellElement, tableCell, table.getTableLevel());
                if (!tableCellElement.hasChildNodes()) {
                    tableCellElement.appendChild(this.foDocumentFacade.createBlock());
                }
                tableRowElement.appendChild(tableCellElement);
            }
            if (!tableRowElement.hasChildNodes()) continue;
            if (tableRow.isTableHeader()) {
                tableHeader.appendChild(tableRowElement);
                continue;
            }
            tableBody.appendChild(tableRowElement);
        }
        Element tableElement = this.foDocumentFacade.createTable();
        tableElement.setAttribute("table-layout", "fixed");
        if (tableHeader.hasChildNodes()) {
            tableElement.appendChild(tableHeader);
        }
        if (tableBody.hasChildNodes()) {
            tableElement.appendChild(tableBody);
            flow.appendChild(tableElement);
        } else {
            LOG.atWarn().log("Table without body starting on offset {} -- {}", (Object)Unbox.box(table.getStartOffset()), (Object)Unbox.box(table.getEndOffset()));
        }
    }

    protected boolean setId(Element element, String id) {
        if (this.usedIds.contains(id)) {
            LOG.atWarn().log("Tried to create element with same ID '{}'. Skipped", (Object)id);
            return false;
        }
        element.setAttribute("id", id);
        this.usedIds.add(id);
        return true;
    }

    public void setOutputCharactersLanguage(boolean outputCharactersLanguage) {
        this.outputCharactersLanguage = outputCharactersLanguage;
    }
}

