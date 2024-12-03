/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.converter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.converter.AbstractExcelConverter;
import org.apache.poi.hssf.converter.AbstractExcelUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hwpf.converter.HtmlDocumentFacade;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class ExcelToHtmlConverter
extends AbstractExcelConverter {
    private static final Logger LOG = LogManager.getLogger(ExcelToHtmlConverter.class);
    private String cssClassContainerCell;
    private String cssClassContainerDiv;
    private String cssClassPrefixCell = "c";
    private String cssClassPrefixDiv = "d";
    private String cssClassPrefixRow = "r";
    private String cssClassPrefixTable = "t";
    private final Map<Short, String> excelStyleToClass = new LinkedHashMap<Short, String>();
    private final HtmlDocumentFacade htmlDocumentFacade;
    private boolean useDivsToSpan;

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: ExcelToHtmlConverter <inputFile.xls> <saveTo.html>");
            return;
        }
        System.out.println("Converting " + args[0]);
        System.out.println("Saving output to " + args[1]);
        Document doc = ExcelToHtmlConverter.process(new File(args[0]));
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File(args[1]));
        Transformer serializer = XMLHelper.newTransformer();
        serializer.setOutputProperty("method", "html");
        serializer.transform(domSource, streamResult);
    }

    public static Document process(File xlsFile) throws IOException, ParserConfigurationException {
        try (HSSFWorkbook workbook = AbstractExcelUtils.loadXls(xlsFile);){
            Document document = ExcelToHtmlConverter.process(workbook);
            return document;
        }
    }

    public static Document process(InputStream xlsStream) throws IOException, ParserConfigurationException {
        try (HSSFWorkbook workbook = new HSSFWorkbook(xlsStream);){
            Document document = ExcelToHtmlConverter.process(workbook);
            return document;
        }
    }

    public static Document process(HSSFWorkbook workbook) throws IOException, ParserConfigurationException {
        ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter(XMLHelper.newDocumentBuilder().newDocument());
        excelToHtmlConverter.processWorkbook(workbook);
        return excelToHtmlConverter.getDocument();
    }

    public ExcelToHtmlConverter(Document doc) {
        this.htmlDocumentFacade = new HtmlDocumentFacade(doc);
    }

    public ExcelToHtmlConverter(HtmlDocumentFacade htmlDocumentFacade) {
        this.htmlDocumentFacade = htmlDocumentFacade;
    }

    protected String buildStyle(HSSFWorkbook workbook, HSSFCellStyle cellStyle) {
        StringBuilder style = new StringBuilder();
        style.append("white-space:pre-wrap;");
        AbstractExcelUtils.appendAlign(style, cellStyle.getAlignment());
        switch (cellStyle.getFillPattern()) {
            case NO_FILL: {
                break;
            }
            case SOLID_FOREGROUND: {
                HSSFColor foregroundColor = cellStyle.getFillForegroundColorColor();
                if (foregroundColor == null) break;
                String fgCol = AbstractExcelUtils.getColor(foregroundColor);
                style.append("background-color:").append(fgCol).append(";");
                break;
            }
            default: {
                HSSFColor backgroundColor = cellStyle.getFillBackgroundColorColor();
                if (backgroundColor == null) break;
                String bgCol = AbstractExcelUtils.getColor(backgroundColor);
                style.append("background-color:").append(bgCol).append(";");
            }
        }
        this.buildStyle_border(workbook, style, "top", cellStyle.getBorderTop(), cellStyle.getTopBorderColor());
        this.buildStyle_border(workbook, style, "right", cellStyle.getBorderRight(), cellStyle.getRightBorderColor());
        this.buildStyle_border(workbook, style, "bottom", cellStyle.getBorderBottom(), cellStyle.getBottomBorderColor());
        this.buildStyle_border(workbook, style, "left", cellStyle.getBorderLeft(), cellStyle.getLeftBorderColor());
        HSSFFont font = cellStyle.getFont(workbook);
        this.buildStyle_font(workbook, style, font);
        return style.toString();
    }

    private void buildStyle_border(HSSFWorkbook workbook, StringBuilder style, String type, BorderStyle xlsBorder, short borderColor) {
        if (xlsBorder == BorderStyle.NONE) {
            return;
        }
        StringBuilder borderStyle = new StringBuilder();
        borderStyle.append(AbstractExcelUtils.getBorderWidth(xlsBorder));
        borderStyle.append(' ');
        borderStyle.append(AbstractExcelUtils.getBorderStyle(xlsBorder));
        HSSFColor color = workbook.getCustomPalette().getColor(borderColor);
        if (color != null) {
            borderStyle.append(' ');
            borderStyle.append(AbstractExcelUtils.getColor(color));
        }
        style.append("border-").append(type).append(":").append((CharSequence)borderStyle).append(";");
    }

    void buildStyle_font(HSSFWorkbook workbook, StringBuilder style, HSSFFont font) {
        HSSFColor fontColor;
        if (font.getBold()) {
            style.append("font-weight:bold;");
        }
        if ((fontColor = workbook.getCustomPalette().getColor(font.getColor())) != null) {
            style.append("color: ").append(AbstractExcelUtils.getColor(fontColor)).append("; ");
        }
        if (font.getFontHeightInPoints() != 0) {
            style.append("font-size:").append(font.getFontHeightInPoints()).append("pt;");
        }
        if (font.getItalic()) {
            style.append("font-style:italic;");
        }
    }

    public String getCssClassPrefixCell() {
        return this.cssClassPrefixCell;
    }

    public String getCssClassPrefixDiv() {
        return this.cssClassPrefixDiv;
    }

    public String getCssClassPrefixRow() {
        return this.cssClassPrefixRow;
    }

    public String getCssClassPrefixTable() {
        return this.cssClassPrefixTable;
    }

    @Override
    public Document getDocument() {
        return this.htmlDocumentFacade.getDocument();
    }

    protected String getStyleClassName(HSSFWorkbook workbook, HSSFCellStyle cellStyle) {
        Short cellStyleKey = cellStyle.getIndex();
        String knownClass = this.excelStyleToClass.get(cellStyleKey);
        if (knownClass != null) {
            return knownClass;
        }
        String cssStyle = this.buildStyle(workbook, cellStyle);
        String cssClass = this.htmlDocumentFacade.getOrCreateCssClass(this.cssClassPrefixCell, cssStyle);
        this.excelStyleToClass.put(cellStyleKey, cssClass);
        return cssClass;
    }

    public boolean isUseDivsToSpan() {
        return this.useDivsToSpan;
    }

    protected boolean processCell(HSSFCell cell, Element tableCellElement, int normalWidthPx, int maxSpannedWidthPx, float normalHeightPt) {
        boolean wrapInDivs;
        String value;
        HSSFCellStyle cellStyle = cell.getCellStyle();
        block0 : switch (cell.getCellType()) {
            case STRING: {
                value = cell.getRichStringCellValue().getString();
                break;
            }
            case FORMULA: {
                switch (cell.getCachedFormulaResultType()) {
                    case STRING: {
                        HSSFRichTextString str = cell.getRichStringCellValue();
                        if (str != null && str.length() > 0) {
                            value = str.toString();
                            break block0;
                        }
                        value = "";
                        break block0;
                    }
                    case NUMERIC: {
                        double nValue = cell.getNumericCellValue();
                        short df = cellStyle.getDataFormat();
                        String dfs = cellStyle.getDataFormatString();
                        value = this._formatter.formatRawCellContents(nValue, df, dfs);
                        break block0;
                    }
                    case BOOLEAN: {
                        value = String.valueOf(cell.getBooleanCellValue());
                        break block0;
                    }
                    case ERROR: {
                        value = ErrorEval.getText(cell.getErrorCellValue());
                        break block0;
                    }
                }
                LOG.atWarn().log("Unexpected cell cachedFormulaResultType ({})", (Object)cell.getCachedFormulaResultType());
                value = "";
                break;
            }
            case BLANK: {
                value = "";
                break;
            }
            case NUMERIC: {
                value = this._formatter.formatCellValue(cell);
                break;
            }
            case BOOLEAN: {
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            }
            case ERROR: {
                value = ErrorEval.getText(cell.getErrorCellValue());
                break;
            }
            default: {
                LOG.atWarn().log("Unexpected cell type ({})", (Object)cell.getCellType());
                return true;
            }
        }
        boolean noText = AbstractExcelUtils.isEmpty(value);
        boolean bl = wrapInDivs = !noText && this.isUseDivsToSpan() && !cellStyle.getWrapText();
        if (cellStyle.getIndex() != 0) {
            HSSFWorkbook workbook = cell.getRow().getSheet().getWorkbook();
            String mainCssClass = this.getStyleClassName(workbook, cellStyle);
            if (wrapInDivs) {
                tableCellElement.setAttribute("class", mainCssClass + " " + this.cssClassContainerCell);
            } else {
                tableCellElement.setAttribute("class", mainCssClass);
            }
            if (noText) {
                value = "\u00a0";
            }
        }
        if (this.isOutputLeadingSpacesAsNonBreaking() && value.startsWith(" ")) {
            StringBuilder builder = new StringBuilder();
            for (int c = 0; c < value.length() && value.charAt(c) == ' '; ++c) {
                builder.append('\u00a0');
            }
            if (value.length() != builder.length()) {
                builder.append(value.substring(builder.length()));
            }
            value = builder.toString();
        }
        Text text = this.htmlDocumentFacade.createText(value);
        if (wrapInDivs) {
            Element outerDiv = this.htmlDocumentFacade.createBlock();
            outerDiv.setAttribute("class", this.cssClassContainerDiv);
            Element innerDiv = this.htmlDocumentFacade.createBlock();
            StringBuilder innerDivStyle = new StringBuilder();
            innerDivStyle.append("position:absolute;min-width:");
            innerDivStyle.append(normalWidthPx);
            innerDivStyle.append("px;");
            if (maxSpannedWidthPx != Integer.MAX_VALUE) {
                innerDivStyle.append("max-width:");
                innerDivStyle.append(maxSpannedWidthPx);
                innerDivStyle.append("px;");
            }
            innerDivStyle.append("overflow:hidden;max-height:");
            innerDivStyle.append(normalHeightPt);
            innerDivStyle.append("pt;white-space:nowrap;");
            AbstractExcelUtils.appendAlign(innerDivStyle, cellStyle.getAlignment());
            this.htmlDocumentFacade.addStyleClass(outerDiv, this.cssClassPrefixDiv, innerDivStyle.toString());
            innerDiv.appendChild(text);
            outerDiv.appendChild(innerDiv);
            tableCellElement.appendChild(outerDiv);
        } else {
            tableCellElement.appendChild(text);
        }
        return AbstractExcelUtils.isEmpty(value) && cellStyle.getIndex() == 0;
    }

    protected void processColumnHeaders(HSSFSheet sheet, int maxSheetColumns, Element table) {
        Element tableHeader = this.htmlDocumentFacade.createTableHeader();
        table.appendChild(tableHeader);
        Element tr = this.htmlDocumentFacade.createTableRow();
        if (this.isOutputRowNumbers()) {
            tr.appendChild(this.htmlDocumentFacade.createTableHeaderCell());
        }
        for (int c = 0; c < maxSheetColumns; ++c) {
            if (!this.isOutputHiddenColumns() && sheet.isColumnHidden(c)) continue;
            Element th = this.htmlDocumentFacade.createTableHeaderCell();
            String text = this.getColumnName(c);
            th.appendChild(this.htmlDocumentFacade.createText(text));
            tr.appendChild(th);
        }
        tableHeader.appendChild(tr);
    }

    protected void processColumnWidths(HSSFSheet sheet, int maxSheetColumns, Element table) {
        Element columnGroup = this.htmlDocumentFacade.createTableColumnGroup();
        if (this.isOutputRowNumbers()) {
            columnGroup.appendChild(this.htmlDocumentFacade.createTableColumn());
        }
        for (int c = 0; c < maxSheetColumns; ++c) {
            if (!this.isOutputHiddenColumns() && sheet.isColumnHidden(c)) continue;
            Element col = this.htmlDocumentFacade.createTableColumn();
            col.setAttribute("width", String.valueOf(ExcelToHtmlConverter.getColumnWidth(sheet, c)));
            columnGroup.appendChild(col);
        }
        table.appendChild(columnGroup);
    }

    protected void processDocumentInformation(SummaryInformation summaryInformation) {
        if (AbstractExcelUtils.isNotEmpty(summaryInformation.getTitle())) {
            this.htmlDocumentFacade.setTitle(summaryInformation.getTitle());
        }
        if (AbstractExcelUtils.isNotEmpty(summaryInformation.getAuthor())) {
            this.htmlDocumentFacade.addAuthor(summaryInformation.getAuthor());
        }
        if (AbstractExcelUtils.isNotEmpty(summaryInformation.getKeywords())) {
            this.htmlDocumentFacade.addKeywords(summaryInformation.getKeywords());
        }
        if (AbstractExcelUtils.isNotEmpty(summaryInformation.getComments())) {
            this.htmlDocumentFacade.addDescription(summaryInformation.getComments());
        }
    }

    protected int processRow(CellRangeAddress[][] mergedRanges, HSSFRow row, Element tableRowElement) {
        HSSFSheet sheet = row.getSheet();
        int maxColIx = row.getLastCellNum();
        if (maxColIx <= 0) {
            return 0;
        }
        ArrayList<Element> emptyCells = new ArrayList<Element>(maxColIx);
        if (this.isOutputRowNumbers()) {
            Element tableRowNumberCellElement = this.htmlDocumentFacade.createTableHeaderCell();
            this.processRowNumber(row, tableRowNumberCellElement);
            emptyCells.add(tableRowNumberCellElement);
        }
        int maxRenderedColumn = 0;
        for (int colIx = 0; colIx < maxColIx; ++colIx) {
            boolean emptyCell;
            CellRangeAddress range;
            if (!this.isOutputHiddenColumns() && sheet.isColumnHidden(colIx) || (range = AbstractExcelUtils.getMergedRange(mergedRanges, row.getRowNum(), colIx)) != null && (range.getFirstColumn() != colIx || range.getFirstRow() != row.getRowNum())) continue;
            HSSFCell cell = row.getCell(colIx);
            int divWidthPx = 0;
            if (this.isUseDivsToSpan()) {
                divWidthPx = ExcelToHtmlConverter.getColumnWidth(sheet, colIx);
                boolean hasBreaks = false;
                for (int nextColumnIndex = colIx + 1; nextColumnIndex < maxColIx; ++nextColumnIndex) {
                    if (!this.isOutputHiddenColumns() && sheet.isColumnHidden(nextColumnIndex)) continue;
                    if (row.getCell(nextColumnIndex) != null && !this.isTextEmpty(row.getCell(nextColumnIndex))) {
                        hasBreaks = true;
                        break;
                    }
                    divWidthPx += ExcelToHtmlConverter.getColumnWidth(sheet, nextColumnIndex);
                }
                if (!hasBreaks) {
                    divWidthPx = Integer.MAX_VALUE;
                }
            }
            Element tableCellElement = this.htmlDocumentFacade.createTableCell();
            if (range != null) {
                if (range.getFirstColumn() != range.getLastColumn()) {
                    tableCellElement.setAttribute("colspan", String.valueOf(range.getLastColumn() - range.getFirstColumn() + 1));
                }
                if (range.getFirstRow() != range.getLastRow()) {
                    tableCellElement.setAttribute("rowspan", String.valueOf(range.getLastRow() - range.getFirstRow() + 1));
                }
            }
            if (emptyCell = cell != null ? this.processCell(cell, tableCellElement, ExcelToHtmlConverter.getColumnWidth(sheet, colIx), divWidthPx, (float)row.getHeight() / 20.0f) : true) {
                emptyCells.add(tableCellElement);
                continue;
            }
            for (Element emptyCellElement : emptyCells) {
                tableRowElement.appendChild(emptyCellElement);
            }
            emptyCells.clear();
            tableRowElement.appendChild(tableCellElement);
            maxRenderedColumn = colIx;
        }
        return maxRenderedColumn + 1;
    }

    protected void processRowNumber(HSSFRow row, Element tableRowNumberCellElement) {
        tableRowNumberCellElement.setAttribute("class", "rownumber");
        Text text = this.htmlDocumentFacade.createText(this.getRowName(row));
        tableRowNumberCellElement.appendChild(text);
    }

    protected void processSheet(HSSFSheet sheet) {
        this.processSheetHeader(this.htmlDocumentFacade.getBody(), sheet);
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        if (physicalNumberOfRows <= 0) {
            return;
        }
        Element table = this.htmlDocumentFacade.createTable();
        this.htmlDocumentFacade.addStyleClass(table, this.cssClassPrefixTable, "border-collapse:collapse;border-spacing:0;");
        Element tableBody = this.htmlDocumentFacade.createTableBody();
        CellRangeAddress[][] mergedRanges = AbstractExcelUtils.buildMergedRangesMap(sheet);
        ArrayList<Element> emptyRowElements = new ArrayList<Element>(physicalNumberOfRows);
        int maxSheetColumns = 1;
        for (int r = sheet.getFirstRowNum(); r <= sheet.getLastRowNum(); ++r) {
            HSSFRow row = sheet.getRow(r);
            if (row == null || !this.isOutputHiddenRows() && row.getZeroHeight()) continue;
            Element tableRowElement = this.htmlDocumentFacade.createTableRow();
            this.htmlDocumentFacade.addStyleClass(tableRowElement, this.cssClassPrefixRow, "height:" + (float)row.getHeight() / 20.0f + "pt;");
            int maxRowColumnNumber = this.processRow(mergedRanges, row, tableRowElement);
            if (maxRowColumnNumber == 0) {
                emptyRowElements.add(tableRowElement);
            } else {
                if (!emptyRowElements.isEmpty()) {
                    for (Element emptyRowElement : emptyRowElements) {
                        tableBody.appendChild(emptyRowElement);
                    }
                    emptyRowElements.clear();
                }
                tableBody.appendChild(tableRowElement);
            }
            maxSheetColumns = Math.max(maxSheetColumns, maxRowColumnNumber);
        }
        this.processColumnWidths(sheet, maxSheetColumns, table);
        if (this.isOutputColumnHeaders()) {
            this.processColumnHeaders(sheet, maxSheetColumns, table);
        }
        table.appendChild(tableBody);
        this.htmlDocumentFacade.getBody().appendChild(table);
    }

    protected void processSheetHeader(Element htmlBody, HSSFSheet sheet) {
        Element h2 = this.htmlDocumentFacade.createHeader2();
        h2.appendChild(this.htmlDocumentFacade.createText(sheet.getSheetName()));
        htmlBody.appendChild(h2);
    }

    public void processWorkbook(HSSFWorkbook workbook) {
        SummaryInformation summaryInformation = workbook.getSummaryInformation();
        if (summaryInformation != null) {
            this.processDocumentInformation(summaryInformation);
        }
        if (this.isUseDivsToSpan()) {
            this.cssClassContainerCell = this.htmlDocumentFacade.getOrCreateCssClass(this.cssClassPrefixCell, "padding:0;margin:0;align:left;vertical-align:top;");
            this.cssClassContainerDiv = this.htmlDocumentFacade.getOrCreateCssClass(this.cssClassPrefixDiv, "position:relative;");
        }
        for (int s = 0; s < workbook.getNumberOfSheets(); ++s) {
            HSSFSheet sheet = workbook.getSheetAt(s);
            this.processSheet(sheet);
        }
        this.htmlDocumentFacade.updateStylesheet();
    }

    public void setCssClassPrefixCell(String cssClassPrefixCell) {
        this.cssClassPrefixCell = cssClassPrefixCell;
    }

    public void setCssClassPrefixDiv(String cssClassPrefixDiv) {
        this.cssClassPrefixDiv = cssClassPrefixDiv;
    }

    public void setCssClassPrefixRow(String cssClassPrefixRow) {
        this.cssClassPrefixRow = cssClassPrefixRow;
    }

    public void setCssClassPrefixTable(String cssClassPrefixTable) {
        this.cssClassPrefixTable = cssClassPrefixTable;
    }

    public void setUseDivsToSpan(boolean useDivsToSpan) {
        this.useDivsToSpan = useDivsToSpan;
    }
}

