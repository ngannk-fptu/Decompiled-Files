/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.StringBuilderWriter
 */
package org.apache.poi.hwpf.converter;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.AbstractWordConverter;
import org.apache.poi.hwpf.converter.AbstractWordUtils;
import org.apache.poi.hwpf.converter.TextDocumentFacade;
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
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WordToTextConverter
extends AbstractWordConverter {
    private static final Logger LOG = LogManager.getLogger(WordToTextConverter.class);
    private AtomicInteger noteCounters = new AtomicInteger(1);
    private Element notes;
    private boolean outputSummaryInformation;
    private final TextDocumentFacade textDocumentFacade;

    public static String getText(DirectoryNode root) throws Exception {
        HWPFDocumentCore wordDocument = AbstractWordUtils.loadDoc(root);
        return WordToTextConverter.getText(wordDocument);
    }

    public static String getText(File docFile) throws Exception {
        HWPFDocumentCore wordDocument = AbstractWordUtils.loadDoc(docFile);
        return WordToTextConverter.getText(wordDocument);
    }

    public static String getText(HWPFDocumentCore wordDocument) throws Exception {
        WordToTextConverter wordToTextConverter = new WordToTextConverter(XMLHelper.newDocumentBuilder().newDocument());
        wordToTextConverter.processDocument(wordDocument);
        return wordToTextConverter.getText();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: WordToTextConverter <inputFile.doc> <saveTo.txt>");
            return;
        }
        System.out.println("Converting " + args[0]);
        System.out.println("Saving output to " + args[1]);
        Document doc = WordToTextConverter.process(new File(args[0]));
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File(args[1]));
        Transformer serializer = XMLHelper.newTransformer();
        serializer.setOutputProperty("method", "text");
        serializer.transform(domSource, streamResult);
    }

    private static Document process(File docFile) throws IOException, ParserConfigurationException {
        try (HWPFDocumentCore wordDocument = AbstractWordUtils.loadDoc(docFile);){
            WordToTextConverter wordToTextConverter = new WordToTextConverter(XMLHelper.newDocumentBuilder().newDocument());
            wordToTextConverter.processDocument(wordDocument);
            Document document = wordToTextConverter.getDocument();
            return document;
        }
    }

    public WordToTextConverter() throws ParserConfigurationException {
        this.textDocumentFacade = new TextDocumentFacade(XMLHelper.newDocumentBuilder().newDocument());
    }

    public WordToTextConverter(Document document) {
        this.textDocumentFacade = new TextDocumentFacade(document);
    }

    public WordToTextConverter(TextDocumentFacade textDocumentFacade) {
        this.textDocumentFacade = textDocumentFacade;
    }

    @Override
    protected void afterProcess() {
        if (this.notes != null) {
            this.textDocumentFacade.getBody().appendChild(this.notes);
        }
    }

    @Override
    public Document getDocument() {
        return this.textDocumentFacade.getDocument();
    }

    public String getText() throws Exception {
        StringBuilderWriter stringWriter = new StringBuilderWriter(1024);
        DOMSource domSource = new DOMSource(this.getDocument());
        StreamResult streamResult = new StreamResult((Writer)stringWriter);
        Transformer serializer = XMLHelper.newTransformer();
        serializer.setOutputProperty("method", "text");
        serializer.transform(domSource, streamResult);
        return stringWriter.toString();
    }

    public boolean isOutputSummaryInformation() {
        return this.outputSummaryInformation;
    }

    @Override
    protected void outputCharacters(Element block, CharacterRun characterRun, String text) {
        block.appendChild(this.textDocumentFacade.createText(text));
    }

    @Override
    protected void processBookmarks(HWPFDocumentCore wordDocument, Element currentBlock, Range range, int currentTableLevel, List<Bookmark> rangeBookmarks) {
        this.processCharacters(wordDocument, currentTableLevel, range, currentBlock);
    }

    @Override
    protected void processDocumentInformation(SummaryInformation summaryInformation) {
        if (this.isOutputSummaryInformation()) {
            if (AbstractWordUtils.isNotEmpty(summaryInformation.getTitle())) {
                this.textDocumentFacade.setTitle(summaryInformation.getTitle());
            }
            if (AbstractWordUtils.isNotEmpty(summaryInformation.getAuthor())) {
                this.textDocumentFacade.addAuthor(summaryInformation.getAuthor());
            }
            if (AbstractWordUtils.isNotEmpty(summaryInformation.getComments())) {
                this.textDocumentFacade.addDescription(summaryInformation.getComments());
            }
            if (AbstractWordUtils.isNotEmpty(summaryInformation.getKeywords())) {
                this.textDocumentFacade.addKeywords(summaryInformation.getKeywords());
            }
        }
    }

    @Override
    public void processDocumentPart(HWPFDocumentCore wordDocument, Range range) {
        super.processDocumentPart(wordDocument, range);
        this.afterProcess();
    }

    @Override
    protected void processDrawnObject(HWPFDocument doc, CharacterRun characterRun, OfficeDrawing officeDrawing, String path, Element block) {
    }

    @Override
    protected void processEndnoteAutonumbered(HWPFDocument wordDocument, int noteIndex, Element block, Range endnoteTextRange) {
        this.processNote(wordDocument, block, endnoteTextRange);
    }

    @Override
    protected void processFootnoteAutonumbered(HWPFDocument wordDocument, int noteIndex, Element block, Range footnoteTextRange) {
        this.processNote(wordDocument, block, footnoteTextRange);
    }

    @Override
    protected void processHyperlink(HWPFDocumentCore wordDocument, Element currentBlock, Range textRange, int currentTableLevel, String hyperlink) {
        this.processCharacters(wordDocument, currentTableLevel, textRange, currentBlock);
        currentBlock.appendChild(this.textDocumentFacade.createText(" (\u200b" + hyperlink.replace("/", "\u200b\\/\u200b") + '\u200b' + ")"));
    }

    @Override
    protected void processImage(Element currentBlock, boolean inlined, Picture picture) {
    }

    @Override
    protected void processImage(Element currentBlock, boolean inlined, Picture picture, String url) {
    }

    @Override
    protected void processImageWithoutPicturesManager(Element currentBlock, boolean inlined, Picture picture) {
    }

    @Override
    protected void processLineBreak(Element block, CharacterRun characterRun) {
        block.appendChild(this.textDocumentFacade.createText("\n"));
    }

    private void processNote(HWPFDocument wordDocument, Element block, Range noteTextRange) {
        int noteIndex = this.noteCounters.getAndIncrement();
        block.appendChild(this.textDocumentFacade.createText("\u200b[" + noteIndex + "]" + '\u200b'));
        if (this.notes == null) {
            this.notes = this.textDocumentFacade.createBlock();
        }
        Element note = this.textDocumentFacade.createBlock();
        this.notes.appendChild(note);
        note.appendChild(this.textDocumentFacade.createText("^" + noteIndex + "\t "));
        this.processCharacters(wordDocument, Integer.MIN_VALUE, noteTextRange, note);
        note.appendChild(this.textDocumentFacade.createText("\n"));
    }

    @Override
    protected boolean processOle2(HWPFDocument wordDocument, Element block, Entry entry) throws Exception {
        Object extractor;
        if (!(entry instanceof DirectoryNode)) {
            return false;
        }
        DirectoryNode directoryNode = (DirectoryNode)entry;
        if (directoryNode.hasEntry("WordDocument")) {
            String text = WordToTextConverter.getText((DirectoryNode)entry);
            block.appendChild(this.textDocumentFacade.createText('\u200b' + text + '\u200b'));
            return true;
        }
        try {
            Class<?> cls = Class.forName("org.apache.poi.extractor.ExtractorFactory");
            Method createExtractor = cls.getMethod("createExtractor", DirectoryNode.class);
            extractor = createExtractor.invoke(null, directoryNode);
        }
        catch (Exception exc) {
            LOG.atWarn().withThrowable(exc).log("There is an OLE object entry '{}', but there is no text extractor for this object type or text extractor factory is not available", (Object)entry.getName());
            return false;
        }
        try {
            Method getText = extractor.getClass().getMethod("getText", new Class[0]);
            String text = (String)getText.invoke(extractor, new Object[0]);
            block.appendChild(this.textDocumentFacade.createText('\u200b' + text + '\u200b'));
            return true;
        }
        catch (Exception exc) {
            LOG.atError().withThrowable(exc).log("Unable to extract text from OLE entry '{}'", (Object)entry.getName());
            return false;
        }
    }

    @Override
    protected void processPageBreak(HWPFDocumentCore wordDocument, Element flow) {
        Element block = this.textDocumentFacade.createBlock();
        block.appendChild(this.textDocumentFacade.createText("\n"));
        flow.appendChild(block);
    }

    @Override
    protected void processPageref(HWPFDocumentCore wordDocument, Element currentBlock, Range textRange, int currentTableLevel, String pageref) {
        this.processCharacters(wordDocument, currentTableLevel, textRange, currentBlock);
    }

    @Override
    protected void processParagraph(HWPFDocumentCore wordDocument, Element parentElement, int currentTableLevel, Paragraph paragraph, String bulletText) {
        Element pElement = this.textDocumentFacade.createParagraph();
        pElement.appendChild(this.textDocumentFacade.createText(bulletText));
        this.processCharacters(wordDocument, currentTableLevel, paragraph, pElement);
        pElement.appendChild(this.textDocumentFacade.createText("\n"));
        parentElement.appendChild(pElement);
    }

    @Override
    protected void processSection(HWPFDocumentCore wordDocument, Section section, int s) {
        Element sectionElement = this.textDocumentFacade.createBlock();
        this.processParagraphes(wordDocument, sectionElement, section, Integer.MIN_VALUE);
        sectionElement.appendChild(this.textDocumentFacade.createText("\n"));
        this.textDocumentFacade.body.appendChild(sectionElement);
    }

    @Override
    protected void processTable(HWPFDocumentCore wordDocument, Element flow, Table table) {
        int tableRows = table.numRows();
        for (int r = 0; r < tableRows; ++r) {
            TableRow tableRow = table.getRow(r);
            Element tableRowElement = this.textDocumentFacade.createTableRow();
            int rowCells = tableRow.numCells();
            for (int c = 0; c < rowCells; ++c) {
                TableCell tableCell = tableRow.getCell(c);
                Element tableCellElement = this.textDocumentFacade.createTableCell();
                if (c != 0) {
                    tableCellElement.appendChild(this.textDocumentFacade.createText("\t"));
                }
                this.processCharacters(wordDocument, table.getTableLevel(), tableCell, tableCellElement);
                tableRowElement.appendChild(tableCellElement);
            }
            tableRowElement.appendChild(this.textDocumentFacade.createText("\n"));
            flow.appendChild(tableRowElement);
        }
    }

    public void setOutputSummaryInformation(boolean outputDocumentInformation) {
        this.outputSummaryInformation = outputDocumentInformation;
    }
}

