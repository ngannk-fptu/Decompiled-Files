/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.binary.XSSFBCommentsTable;
import org.apache.poi.xssf.binary.XSSFBHyperlinksTable;
import org.apache.poi.xssf.binary.XSSFBSharedStringsTable;
import org.apache.poi.xssf.binary.XSSFBSheetHandler;
import org.apache.poi.xssf.binary.XSSFBStylesTable;
import org.apache.poi.xssf.eventusermodel.XSSFBReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.xmlbeans.XmlException;
import org.xml.sax.SAXException;

public class XSSFBEventBasedExcelExtractor
extends XSSFEventBasedExcelExtractor {
    private static final Logger LOGGER = LogManager.getLogger(XSSFBEventBasedExcelExtractor.class);
    public static final List<XSSFRelation> SUPPORTED_TYPES = Collections.singletonList(XSSFRelation.XLSB_BINARY_WORKBOOK);
    private boolean handleHyperlinksInCells;

    public XSSFBEventBasedExcelExtractor(String path) throws XmlException, OpenXML4JException, IOException {
        super(path);
    }

    public XSSFBEventBasedExcelExtractor(OPCPackage container) throws XmlException, OpenXML4JException, IOException {
        super(container);
    }

    public void setHandleHyperlinksInCells(boolean handleHyperlinksInCells) {
        this.handleHyperlinksInCells = handleHyperlinksInCells;
    }

    @Override
    public void setFormulasNotResults(boolean formulasNotResults) {
        throw new IllegalArgumentException("Not currently supported");
    }

    public void processSheet(XSSFSheetXMLHandler.SheetContentsHandler sheetContentsExtractor, XSSFBStylesTable styles, XSSFBCommentsTable comments, SharedStrings strings, InputStream sheetInputStream) throws IOException {
        DataFormatter formatter = this.getLocale() == null ? new DataFormatter() : new DataFormatter(this.getLocale());
        XSSFBSheetHandler xssfbSheetHandler = new XSSFBSheetHandler(sheetInputStream, styles, comments, strings, sheetContentsExtractor, formatter, this.getFormulasNotResults());
        xssfbSheetHandler.parse();
    }

    @Override
    public String getText() {
        try {
            XSSFBSharedStringsTable strings = new XSSFBSharedStringsTable(this.getPackage());
            XSSFBReader xssfbReader = new XSSFBReader(this.getPackage());
            XSSFBStylesTable styles = xssfbReader.getXSSFBStylesTable();
            XSSFBReader.SheetIterator iter = (XSSFBReader.SheetIterator)xssfbReader.getSheetsData();
            StringBuilder text = new StringBuilder(64);
            XSSFEventBasedExcelExtractor.SheetTextExtractor sheetExtractor = new XSSFEventBasedExcelExtractor.SheetTextExtractor();
            XSSFBHyperlinksTable hyperlinksTable = null;
            while (iter.hasNext()) {
                InputStream stream = iter.next();
                Throwable throwable = null;
                try {
                    if (this.getIncludeSheetNames()) {
                        text.append(iter.getSheetName());
                        text.append('\n');
                    }
                    if (this.handleHyperlinksInCells) {
                        hyperlinksTable = new XSSFBHyperlinksTable(iter.getSheetPart());
                    }
                    XSSFBCommentsTable comments = this.getIncludeCellComments() ? iter.getXSSFBSheetComments() : null;
                    this.processSheet((XSSFSheetXMLHandler.SheetContentsHandler)sheetExtractor, styles, comments, (SharedStrings)strings, stream);
                    if (this.getIncludeHeadersFooters()) {
                        sheetExtractor.appendHeaderText(text);
                    }
                    sheetExtractor.appendCellText(text);
                    if (this.getIncludeTextBoxes()) {
                        this.processShapes(iter.getShapes(), text);
                    }
                    if (this.getIncludeHeadersFooters()) {
                        sheetExtractor.appendFooterText(text);
                    }
                    sheetExtractor.reset();
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    if (stream == null) continue;
                    if (throwable != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable3) {
                            throwable.addSuppressed(throwable3);
                        }
                        continue;
                    }
                    stream.close();
                }
            }
            return text.toString();
        }
        catch (IOException | OpenXML4JException | SAXException e) {
            LOGGER.atWarn().withThrowable(e).log("Failed to load text");
            return "";
        }
    }
}

