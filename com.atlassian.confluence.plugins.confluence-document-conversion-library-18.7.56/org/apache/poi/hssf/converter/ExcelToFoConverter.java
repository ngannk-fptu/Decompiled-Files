/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.converter;

import java.io.File;
import java.util.ArrayList;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.converter.AbstractExcelConverter;
import org.apache.poi.hssf.converter.AbstractExcelUtils;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hwpf.converter.FoDocumentFacade;
import org.apache.poi.hwpf.converter.FontReplacer;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class ExcelToFoConverter
extends AbstractExcelConverter {
    private static final float CM_PER_INCH = 2.54f;
    private static final float DPI = 72.0f;
    private static final Logger LOG = LogManager.getLogger(ExcelToFoConverter.class);
    private static final float PAPER_A4_HEIGHT_INCHES = 11.574803f;
    private static final float PAPER_A4_WIDTH_INCHES = 8.267716f;
    private final FoDocumentFacade foDocumentFacade;
    private float pageMarginInches = 0.4f;

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: ExcelToFoConverter <inputFile.xls> <saveTo.xml>");
            return;
        }
        System.out.println("Converting " + args[0]);
        System.out.println("Saving output to " + args[1]);
        Document doc = ExcelToHtmlConverter.process(new File(args[0]));
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File(args[1]));
        Transformer serializer = XMLHelper.newTransformer();
        serializer.transform(domSource, streamResult);
    }

    public static Document process(File xlsFile) throws Exception {
        try (HSSFWorkbook workbook = AbstractExcelUtils.loadXls(xlsFile);){
            ExcelToFoConverter excelToHtmlConverter = new ExcelToFoConverter(XMLHelper.newDocumentBuilder().newDocument());
            excelToHtmlConverter.processWorkbook(workbook);
            Document document = excelToHtmlConverter.getDocument();
            return document;
        }
    }

    public ExcelToFoConverter(Document document) {
        this.foDocumentFacade = new FoDocumentFacade(document);
    }

    public ExcelToFoConverter(FoDocumentFacade foDocumentFacade) {
        this.foDocumentFacade = foDocumentFacade;
    }

    protected String createPageMaster(float tableWidthIn, String pageMasterName) {
        float paperHeightIn;
        float paperWidthIn;
        float requiredWidthIn = tableWidthIn + 2.0f * this.getPageMarginInches();
        if (requiredWidthIn < 8.267716f) {
            paperWidthIn = 8.267716f;
            paperHeightIn = 11.574803f;
        } else {
            paperWidthIn = requiredWidthIn;
            paperHeightIn = paperWidthIn * 0.7142857f;
        }
        float leftMargin = this.getPageMarginInches();
        float rightMargin = this.getPageMarginInches();
        float topMargin = this.getPageMarginInches();
        float bottomMargin = this.getPageMarginInches();
        Element pageMaster = this.foDocumentFacade.addSimplePageMaster(pageMasterName);
        pageMaster.setAttribute("page-height", paperHeightIn + "in");
        pageMaster.setAttribute("page-width", paperWidthIn + "in");
        Element regionBody = this.foDocumentFacade.addRegionBody(pageMaster);
        regionBody.setAttribute("margin", topMargin + "in " + rightMargin + "in " + bottomMargin + "in " + leftMargin + "in");
        return pageMasterName;
    }

    @Override
    protected Document getDocument() {
        return this.foDocumentFacade.getDocument();
    }

    public float getPageMarginInches() {
        return this.pageMarginInches;
    }

    protected boolean isEmptyStyle(CellStyle cellStyle) {
        return cellStyle == null || cellStyle.getFillPattern() == FillPatternType.NO_FILL && cellStyle.getBorderTop() == BorderStyle.NONE && cellStyle.getBorderRight() == BorderStyle.NONE && cellStyle.getBorderBottom() == BorderStyle.NONE && cellStyle.getBorderLeft() == BorderStyle.NONE;
    }

    protected boolean processCell(HSSFWorkbook workbook, HSSFCell cell, Element tableCellElement, int normalWidthPx, int maxSpannedWidthPx, float normalHeightPt) {
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
                        value = Boolean.toString(cell.getBooleanCellValue());
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
                value = Boolean.toString(cell.getBooleanCellValue());
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
        boolean wrapInDivs = !noText && !cellStyle.getWrapText();
        boolean emptyStyle = this.isEmptyStyle(cellStyle);
        if (!emptyStyle && noText) {
            value = "\u00a0";
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
        Text text = this.foDocumentFacade.createText(value);
        Element block = this.foDocumentFacade.createBlock();
        if (wrapInDivs) {
            block.setAttribute("absolute-position", "fixed");
            block.setAttribute("left", "0px");
            block.setAttribute("top", "0px");
            block.setAttribute("bottom", "0px");
            block.setAttribute("min-width", normalWidthPx + "px");
            if (maxSpannedWidthPx != Integer.MAX_VALUE) {
                block.setAttribute("max-width", maxSpannedWidthPx + "px");
            }
            block.setAttribute("overflow", "hidden");
            block.setAttribute("height", normalHeightPt + "pt");
            block.setAttribute("keep-together.within-line", "always");
            block.setAttribute("wrap-option", "no-wrap");
        }
        this.processCellStyle(workbook, cell.getCellStyle(), tableCellElement, block);
        block.appendChild(text);
        tableCellElement.appendChild(block);
        return AbstractExcelUtils.isEmpty(value) && emptyStyle;
    }

    protected void processCellStyle(HSSFWorkbook workbook, HSSFCellStyle cellStyle, Element cellTarget, Element blockTarget) {
        blockTarget.setAttribute("white-space-collapse", "false");
        String textAlign = AbstractExcelUtils.getAlign(cellStyle.getAlignment());
        if (AbstractExcelUtils.isNotEmpty(textAlign)) {
            blockTarget.setAttribute("text-align", textAlign);
        }
        if (cellStyle.getFillPattern() != FillPatternType.NO_FILL) {
            if (cellStyle.getFillPattern() == FillPatternType.SOLID_FOREGROUND) {
                HSSFColor foregroundColor = cellStyle.getFillForegroundColorColor();
                if (foregroundColor != null) {
                    cellTarget.setAttribute("background-color", AbstractExcelUtils.getColor(foregroundColor));
                }
            } else {
                HSSFColor backgroundColor = cellStyle.getFillBackgroundColorColor();
                if (backgroundColor != null) {
                    cellTarget.setAttribute("background-color", AbstractExcelUtils.getColor(backgroundColor));
                }
            }
        }
        this.processCellStyleBorder(workbook, cellTarget, "top", cellStyle.getBorderTop(), cellStyle.getTopBorderColor());
        this.processCellStyleBorder(workbook, cellTarget, "right", cellStyle.getBorderRight(), cellStyle.getRightBorderColor());
        this.processCellStyleBorder(workbook, cellTarget, "bottom", cellStyle.getBorderBottom(), cellStyle.getBottomBorderColor());
        this.processCellStyleBorder(workbook, cellTarget, "left", cellStyle.getBorderLeft(), cellStyle.getLeftBorderColor());
        HSSFFont font = cellStyle.getFont(workbook);
        this.processCellStyleFont(workbook, blockTarget, font);
    }

    protected void processCellStyleBorder(HSSFWorkbook workbook, Element cellTarget, String type, BorderStyle xlsBorder, short borderColor) {
        if (xlsBorder == BorderStyle.NONE) {
            return;
        }
        StringBuilder borderStyle = new StringBuilder();
        borderStyle.append(AbstractExcelUtils.getBorderWidth(xlsBorder));
        HSSFColor color = workbook.getCustomPalette().getColor(borderColor);
        if (color != null) {
            borderStyle.append(' ');
            borderStyle.append(AbstractExcelUtils.getColor(color));
            borderStyle.append(' ');
            borderStyle.append(AbstractExcelUtils.getBorderStyle(xlsBorder));
        }
        cellTarget.setAttribute("border-" + type, borderStyle.toString());
    }

    protected void processCellStyleFont(HSSFWorkbook workbook, Element blockTarget, HSSFFont font) {
        FontReplacer.Triplet triplet = new FontReplacer.Triplet();
        triplet.fontName = font.getFontName();
        triplet.bold = font.getBold();
        triplet.italic = font.getItalic();
        this.getFontReplacer().update(triplet);
        this.setBlockProperties(blockTarget, triplet);
        HSSFColor fontColor = workbook.getCustomPalette().getColor(font.getColor());
        if (fontColor != null) {
            blockTarget.setAttribute("color", AbstractExcelUtils.getColor(fontColor));
        }
        if (font.getFontHeightInPoints() != 0) {
            blockTarget.setAttribute("font-size", font.getFontHeightInPoints() + "pt");
        }
    }

    protected void processColumnHeaders(HSSFSheet sheet, int maxSheetColumns, Element table) {
        Element tableHeader = this.foDocumentFacade.createTableHeader();
        Element row = this.foDocumentFacade.createTableRow();
        if (this.isOutputRowNumbers()) {
            Element tableCellElement = this.foDocumentFacade.createTableCell();
            tableCellElement.appendChild(this.foDocumentFacade.createBlock());
            row.appendChild(tableCellElement);
        }
        for (int c = 0; c < maxSheetColumns; ++c) {
            if (!this.isOutputHiddenColumns() && sheet.isColumnHidden(c)) continue;
            Element cell = this.foDocumentFacade.createTableCell();
            Element block = this.foDocumentFacade.createBlock();
            block.setAttribute("text-align", "center");
            block.setAttribute("font-weight", "bold");
            String text = this.getColumnName(c);
            block.appendChild(this.foDocumentFacade.createText(text));
            cell.appendChild(block);
            row.appendChild(cell);
        }
        tableHeader.appendChild(row);
        table.appendChild(tableHeader);
    }

    protected float processColumnWidths(HSSFSheet sheet, int maxSheetColumns, Element table) {
        float tableWidth = 0.0f;
        if (this.isOutputRowNumbers()) {
            float columnWidthIn = (float)ExcelToFoConverter.getDefaultColumnWidth(sheet) / 72.0f;
            Element rowNumberColumn = this.foDocumentFacade.createTableColumn();
            rowNumberColumn.setAttribute("column-width", columnWidthIn + "in");
            table.appendChild(rowNumberColumn);
            tableWidth += columnWidthIn;
        }
        for (int c = 0; c < maxSheetColumns; ++c) {
            if (!this.isOutputHiddenColumns() && sheet.isColumnHidden(c)) continue;
            float columnWidthIn = (float)ExcelToFoConverter.getColumnWidth(sheet, c) / 72.0f;
            Element col = this.foDocumentFacade.createTableColumn();
            col.setAttribute("column-width", columnWidthIn + "in");
            table.appendChild(col);
            tableWidth += columnWidthIn;
        }
        table.setAttribute("width", tableWidth + "in");
        return tableWidth;
    }

    protected void processDocumentInformation(SummaryInformation summaryInformation) {
        if (AbstractExcelUtils.isNotEmpty(summaryInformation.getTitle())) {
            this.foDocumentFacade.setTitle(summaryInformation.getTitle());
        }
        if (AbstractExcelUtils.isNotEmpty(summaryInformation.getAuthor())) {
            this.foDocumentFacade.setCreator(summaryInformation.getAuthor());
        }
        if (AbstractExcelUtils.isNotEmpty(summaryInformation.getKeywords())) {
            this.foDocumentFacade.setKeywords(summaryInformation.getKeywords());
        }
        if (AbstractExcelUtils.isNotEmpty(summaryInformation.getComments())) {
            this.foDocumentFacade.setDescription(summaryInformation.getComments());
        }
    }

    protected int processRow(HSSFWorkbook workbook, CellRangeAddress[][] mergedRanges, HSSFRow row, Element tableRowElement) {
        HSSFSheet sheet = row.getSheet();
        int maxColIx = row.getLastCellNum();
        if (maxColIx <= 0) {
            return 0;
        }
        ArrayList<Element> emptyCells = new ArrayList<Element>(maxColIx);
        if (this.isOutputRowNumbers()) {
            Element tableRowNumberCellElement = this.processRowNumber(row);
            emptyCells.add(tableRowNumberCellElement);
        }
        int maxRenderedColumn = 0;
        for (int colIx = 0; colIx < maxColIx; ++colIx) {
            boolean emptyCell;
            CellRangeAddress range;
            if (!this.isOutputHiddenColumns() && sheet.isColumnHidden(colIx) || (range = AbstractExcelUtils.getMergedRange(mergedRanges, row.getRowNum(), colIx)) != null && (range.getFirstColumn() != colIx || range.getFirstRow() != row.getRowNum())) continue;
            HSSFCell cell = row.getCell(colIx);
            int divWidthPx = 0;
            divWidthPx = ExcelToFoConverter.getColumnWidth(sheet, colIx);
            boolean hasBreaks = false;
            for (int nextColumnIndex = colIx + 1; nextColumnIndex < maxColIx; ++nextColumnIndex) {
                if (!this.isOutputHiddenColumns() && sheet.isColumnHidden(nextColumnIndex)) continue;
                if (row.getCell(nextColumnIndex) != null && !this.isTextEmpty(row.getCell(nextColumnIndex))) {
                    hasBreaks = true;
                    break;
                }
                divWidthPx += ExcelToFoConverter.getColumnWidth(sheet, nextColumnIndex);
            }
            if (!hasBreaks) {
                divWidthPx = Integer.MAX_VALUE;
            }
            Element tableCellElement = this.foDocumentFacade.createTableCell();
            if (range != null) {
                if (range.getFirstColumn() != range.getLastColumn()) {
                    tableCellElement.setAttribute("number-columns-spanned", String.valueOf(range.getLastColumn() - range.getFirstColumn() + 1));
                }
                if (range.getFirstRow() != range.getLastRow()) {
                    tableCellElement.setAttribute("number-rows-spanned", String.valueOf(range.getLastRow() - range.getFirstRow() + 1));
                }
            }
            if (cell != null) {
                emptyCell = this.processCell(workbook, cell, tableCellElement, ExcelToFoConverter.getColumnWidth(sheet, colIx), divWidthPx, (float)row.getHeight() / 20.0f);
            } else {
                tableCellElement.appendChild(this.foDocumentFacade.createBlock());
                emptyCell = true;
            }
            if (emptyCell) {
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

    protected Element processRowNumber(HSSFRow row) {
        Element tableRowNumberCellElement = this.foDocumentFacade.createTableCell();
        Element block = this.foDocumentFacade.createBlock();
        block.setAttribute("text-align", "right");
        block.setAttribute("font-weight", "bold");
        Text text = this.foDocumentFacade.createText(this.getRowName(row));
        block.appendChild(text);
        tableRowNumberCellElement.appendChild(block);
        return tableRowNumberCellElement;
    }

    protected float processSheet(HSSFWorkbook workbook, HSSFSheet sheet, Element flow) {
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        if (physicalNumberOfRows <= 0) {
            return 0.0f;
        }
        this.processSheetName(sheet, flow);
        Element table = this.foDocumentFacade.createTable();
        table.setAttribute("table-layout", "fixed");
        Element tableBody = this.foDocumentFacade.createTableBody();
        CellRangeAddress[][] mergedRanges = AbstractExcelUtils.buildMergedRangesMap(sheet);
        ArrayList<Element> emptyRowElements = new ArrayList<Element>(physicalNumberOfRows);
        int maxSheetColumns = 1;
        for (int r = sheet.getFirstRowNum(); r <= sheet.getLastRowNum(); ++r) {
            HSSFRow row = sheet.getRow(r);
            if (row == null || !this.isOutputHiddenRows() && row.getZeroHeight()) continue;
            Element tableRowElement = this.foDocumentFacade.createTableRow();
            tableRowElement.setAttribute("height", (float)row.getHeight() / 20.0f + "pt");
            int maxRowColumnNumber = this.processRow(workbook, mergedRanges, row, tableRowElement);
            if (tableRowElement.getChildNodes().getLength() == 0) {
                Element emptyCellElement = this.foDocumentFacade.createTableCell();
                emptyCellElement.appendChild(this.foDocumentFacade.createBlock());
                tableRowElement.appendChild(emptyCellElement);
            }
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
        float tableWidthIn = this.processColumnWidths(sheet, maxSheetColumns, table);
        if (this.isOutputColumnHeaders()) {
            this.processColumnHeaders(sheet, maxSheetColumns, table);
        }
        table.appendChild(tableBody);
        flow.appendChild(table);
        return tableWidthIn;
    }

    protected boolean processSheet(HSSFWorkbook workbook, int sheetIndex) {
        String pageMasterName = "sheet-" + sheetIndex;
        Element pageSequence = this.foDocumentFacade.createPageSequence(pageMasterName);
        Element flow = this.foDocumentFacade.addFlowToPageSequence(pageSequence, "xsl-region-body");
        HSSFSheet sheet = workbook.getSheetAt(sheetIndex);
        float tableWidthIn = this.processSheet(workbook, sheet, flow);
        if (tableWidthIn == 0.0f) {
            return false;
        }
        this.createPageMaster(tableWidthIn, pageMasterName);
        this.foDocumentFacade.addPageSequence(pageSequence);
        return true;
    }

    protected void processSheetName(HSSFSheet sheet, Element flow) {
        Element titleBlock = this.foDocumentFacade.createBlock();
        FontReplacer.Triplet triplet = new FontReplacer.Triplet();
        triplet.bold = true;
        triplet.italic = false;
        triplet.fontName = "Arial";
        this.getFontReplacer().update(triplet);
        this.setBlockProperties(titleBlock, triplet);
        titleBlock.setAttribute("font-size", "200%");
        Element titleInline = this.foDocumentFacade.createInline();
        titleInline.appendChild(this.foDocumentFacade.createText(sheet.getSheetName()));
        titleBlock.appendChild(titleInline);
        flow.appendChild(titleBlock);
        Element titleBlock2 = this.foDocumentFacade.createBlock();
        Element titleInline2 = this.foDocumentFacade.createInline();
        titleBlock2.appendChild(titleInline2);
        flow.appendChild(titleBlock2);
    }

    public void processWorkbook(HSSFWorkbook workbook) {
        SummaryInformation summaryInformation = workbook.getSummaryInformation();
        if (summaryInformation != null) {
            this.processDocumentInformation(summaryInformation);
        }
        for (int s = 0; s < workbook.getNumberOfSheets(); ++s) {
            this.processSheet(workbook, s);
        }
    }

    private void setBlockProperties(Element textBlock, FontReplacer.Triplet triplet) {
        if (triplet.bold) {
            textBlock.setAttribute("font-weight", "bold");
        }
        if (triplet.italic) {
            textBlock.setAttribute("font-style", "italic");
        }
        if (AbstractExcelUtils.isNotEmpty(triplet.fontName)) {
            textBlock.setAttribute("font-family", triplet.fontName);
        }
    }

    public void setPageMarginInches(float pageMarginInches) {
        this.pageMarginInches = pageMarginInches;
    }
}

