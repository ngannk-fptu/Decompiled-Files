/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.cells.Cell
 *  com.aspose.cells.CellArea
 *  com.aspose.cells.Cells
 *  com.aspose.cells.Chart
 *  com.aspose.cells.ChartCollection
 *  com.aspose.cells.ChartShape
 *  com.aspose.cells.Hyperlink
 *  com.aspose.cells.HyperlinkCollection
 *  com.aspose.cells.ImageOrPrintOptions
 *  com.aspose.cells.Picture
 *  com.aspose.cells.PictureCollection
 *  com.aspose.cells.Range
 *  com.aspose.cells.Workbook
 *  com.aspose.cells.Worksheet
 *  com.aspose.cells.WorksheetCollection
 *  com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner
 *  com.atlassian.confluence.util.HtmlUtil
 */
package com.atlassian.plugins.conversion.convert.html.spreadsheet;

import com.aspose.cells.Cell;
import com.aspose.cells.CellArea;
import com.aspose.cells.Cells;
import com.aspose.cells.Chart;
import com.aspose.cells.ChartCollection;
import com.aspose.cells.ChartShape;
import com.aspose.cells.Hyperlink;
import com.aspose.cells.HyperlinkCollection;
import com.aspose.cells.ImageOrPrintOptions;
import com.aspose.cells.Picture;
import com.aspose.cells.PictureCollection;
import com.aspose.cells.Range;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.plugins.conversion.AsposeAware;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.atlassian.plugins.conversion.convert.html.HtmlConversionData;
import com.atlassian.plugins.conversion.convert.html.HtmlConversionResult;
import com.atlassian.plugins.conversion.convert.html.spreadsheet.CustomFormat;
import com.atlassian.plugins.conversion.convert.html.spreadsheet.ImageHandler;
import com.atlassian.plugins.conversion.convert.html.spreadsheet.ImageHandlerImpl;
import com.atlassian.plugins.conversion.convert.html.spreadsheet.MacroUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SpreadsheetConverter
extends AsposeAware {
    public static final String SHEET_KEY = "sheet";
    public static final String COL_KEY = "col";
    public static final String ROW_KEY = "row";
    public static final String GRID_KEY = "grid";
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    private static final long MAX_CELLS = 100000L;
    private final Map<CustomFormat, String> formatToCssClassMap = new LinkedHashMap<CustomFormat, String>();
    private final Writer writer;
    private final int instanceId;
    private final RenderedContentCleaner renderedContentCleaner;

    public static HtmlConversionResult convertToHtml(InputStream inputStream, String imgPath, Map<String, Object> args, RenderedContentCleaner renderedContentCleaner) throws ConversionException {
        HtmlConversionData data = new HtmlConversionData();
        StringWriter writer = new StringWriter();
        ImageHandlerImpl handler = new ImageHandlerImpl(data, imgPath);
        try {
            SpreadsheetConverter.convert(inputStream, args, writer, handler, renderedContentCleaner);
        }
        catch (Exception e) {
            throw new ConversionException(e);
        }
        data.setHtml(writer.toString());
        return data;
    }

    private SpreadsheetConverter(Writer writer, RenderedContentCleaner renderedContentCleaner) {
        this.writer = writer;
        this.renderedContentCleaner = renderedContentCleaner;
        this.instanceId = ID_GENERATOR.incrementAndGet();
    }

    static void convert(InputStream in, Map<String, Object> args, Writer writer, ImageHandler handler, RenderedContentCleaner renderedContentCleaner) throws Exception {
        long numCells;
        SpreadsheetConverter converter = new SpreadsheetConverter(writer, renderedContentCleaner);
        String sheetName = MacroUtils.getStringValue(args, SHEET_KEY);
        boolean showGrid = MacroUtils.getBoolValue(args, GRID_KEY, true);
        Workbook book = new Workbook(in);
        WorksheetCollection worksheets = book.getWorksheets();
        Worksheet sheet = null;
        if (sheetName != null && sheetName.trim().length() > 0) {
            sheet = worksheets.get(sheetName);
        }
        if (sheet == null) {
            sheet = worksheets.get(worksheets.getActiveSheetIndex());
        }
        book.calculateFormula();
        Cells cells = sheet.getCells();
        int lastRow = cells.getMaxRow();
        int lastCol = cells.getMaxColumn();
        ChartCollection charts = sheet.getCharts();
        PictureCollection pictures = sheet.getPictures();
        ImageCollection imgCollection = SpreadsheetConverter.collectImageObjects(charts, pictures, handler);
        lastRow = Math.max(imgCollection.getMaxRow(), lastRow);
        lastCol = Math.max(imgCollection.getMaxColumn(), lastCol);
        int rowParam = MacroUtils.getIntValue(args, ROW_KEY, lastRow);
        int colParam = MacroUtils.getIntValue(args, COL_KEY, lastCol);
        if (rowParam < lastRow) {
            lastRow = rowParam;
        }
        if (colParam < lastCol) {
            lastCol = colParam;
        }
        if ((numCells = (long)lastCol * (long)lastRow) > 100000L) {
            throw new ConversionException(String.format("Cannot convert spreadsheet, the number of cells: %d is greater than the maximum allowed cells: %d", numCells, 100000L));
        }
        converter.write(cells, imgCollection.getImgMap(), lastCol, lastRow, sheet, showGrid);
    }

    public void write(Cells cells, Map<String, EmbeddedXlsImage> imgMap, int lastCol, int lastRow, Worksheet worksheet, boolean showGrid) throws Exception {
        this.writer.write("<div id=\"panel1\" style='display:block'>\r\n");
        String tableClassName = this.getTableClassName();
        this.writer.write("<table class=\"" + tableClassName + "\" style='border:1pt solid #c0c0c0;border-collapse:collapse;table-layout:fixed;width:" + this.calcTableWidth(cells, lastCol) + "' border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#FFFFFF\" >\r\n");
        this.writeTable(cells, imgMap, lastCol, lastRow, worksheet, showGrid);
        this.writer.write("</table>\r\n");
        this.writeStyles(tableClassName, this.writer);
        this.writer.write("</div>\r\n");
    }

    private void writeTable(Cells cells, Map<String, EmbeddedXlsImage> imgMap, int lastCol, int lastRow, Worksheet sheet, boolean showGrid) throws Exception {
        HyperlinkCollection hyperlinks = sheet.getHyperlinks();
        Map<String, Hyperlink> hyperlinkMap = this.collectHyperlinks(hyperlinks);
        for (int colnum = 0; colnum <= lastCol; ++colnum) {
            int colWidth = cells.getColumnWidthPixel(colnum);
            this.writeColumn(colWidth);
        }
        for (int row = 0; row <= lastRow; ++row) {
            this.writeRowStart(cells.getRowHeightPixel(row));
            for (int colnum = 0; colnum <= lastCol; ++colnum) {
                int rowspan = 0;
                int colspan = 0;
                Cell cell = cells.get(row, colnum);
                try {
                    if (cell.isMerged()) {
                        Range mergedRange = cell.getMergedRange();
                        if (mergedRange.getFirstColumn() != colnum || mergedRange.getFirstRow() != row) continue;
                        rowspan = mergedRange.getRowCount();
                        colspan = mergedRange.getColumnCount();
                    }
                }
                catch (RuntimeException mergedRange) {
                    // empty catch block
                }
                CustomFormat customFormat = new CustomFormat(cell.getDisplayStyle(), showGrid);
                this.writeTdCell(cell, this.getCssClass(customFormat), rowspan, colspan, row, colnum, imgMap, hyperlinkMap);
            }
            this.writeRowEnd();
        }
    }

    private void writeTdCell(Cell cell, String cssClass, int rowspan, int colspan, int row, int colnum, Map<String, EmbeddedXlsImage> grobjs, Map<String, Hyperlink> hyperlinkMap) throws IOException {
        Hyperlink hyperlink;
        this.writer.write("<td class=\"" + cssClass + "\"");
        if (rowspan > 0) {
            this.writer.write(" rowspan=\"");
            this.writer.write(String.valueOf(rowspan));
            this.writer.write("\"");
        }
        if (colspan > 0) {
            this.writer.write(" colspan=\"");
            this.writer.write(String.valueOf(colspan));
            this.writer.write("\"");
        }
        this.writer.write(">");
        String name = colnum + "_" + row;
        if (grobjs.containsKey(name)) {
            EmbeddedXlsImage grobj = grobjs.get(name);
            this.writeEmbeddedImage(grobj);
        }
        if ((hyperlink = hyperlinkMap.get(name)) != null) {
            this.writeHLink(cell, hyperlink);
        } else {
            this.writer.write(this.encodeHTML(cell.getStringValue()));
        }
        this.writer.write("</td>");
    }

    private void writeHLink(Cell cell, Hyperlink hyperlink) throws IOException {
        String url = hyperlink.getAddress();
        String unclean = "<a href=\"" + this.encodeHTML(url) + "\">" + this.encodeHTML(cell.getStringValue()) + "</a>";
        this.writer.write(this.renderedContentCleaner.cleanQuietly(unclean));
    }

    private String getCssClass(CustomFormat format) {
        if (this.formatToCssClassMap.containsKey(format)) {
            return this.formatToCssClassMap.get(format);
        }
        int count = this.formatToCssClassMap.size();
        String cssClass = "oc-" + count;
        this.formatToCssClassMap.put(format, cssClass);
        return cssClass;
    }

    private void writeEmbeddedImage(EmbeddedXlsImage grobj) throws IOException {
        this.writer.write(" <span style='position:absolute;z-index:1;");
        this.writer.write("margin-left:" + grobj.getDx() + "px;margin-top:" + grobj.getDy() + "pt;'>\r\n");
        this.writer.write("<img src=\"" + grobj.getRef() + "\" />\r\n");
        this.writer.write(" </span>");
    }

    private void writeColumn(int colWidth) throws IOException {
        this.writer.write("<col width=\"");
        this.writer.write(String.valueOf(colWidth));
        this.writer.write("\" />\r\n");
    }

    private void writeRowStart(double rowHeight) throws IOException {
        this.writer.write("<tr style='height:");
        this.writer.write(String.valueOf(rowHeight));
        this.writer.write("px'>");
    }

    private void writeRowEnd() throws IOException {
        this.writer.write("</tr>\r\n");
    }

    private static ImageCollection collectImageObjects(ChartCollection charts, PictureCollection pictures, ImageHandler handler) throws Exception {
        HashMap<String, EmbeddedXlsImage> picMap = new HashMap<String, EmbeddedXlsImage>();
        int maxRow = 0;
        int maxColumn = 0;
        int imagecount = 0;
        for (Object object : charts) {
            Chart chart = (Chart)object;
            ChartShape chartObject = chart.getChartObject();
            int column = chartObject.getUpperLeftColumn();
            int row = chartObject.getUpperLeftRow();
            maxRow = Math.max(maxRow, chartObject.getLowerRightRow());
            maxColumn = Math.max(maxColumn, chartObject.getLowerRightColumn());
            int dx = chartObject.getLeft();
            int dy = chartObject.getTop();
            String filename = "chart_img" + imagecount++ + ".jpg";
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageOrPrintOptions options = new ImageOrPrintOptions();
            options.setImageType(5);
            options.setQuality(90);
            chart.toImage((OutputStream)bout, options);
            String ref = handler.handleImage(bout.toByteArray(), filename);
            picMap.put(column + "_" + row, new EmbeddedXlsImage(ref, dx, dy));
        }
        for (Object object : pictures) {
            Picture pic = (Picture)object;
            int column = pic.getUpperLeftColumn();
            int row = pic.getUpperLeftRow();
            maxRow = Math.max(maxRow, pic.getLowerRightRow());
            maxColumn = Math.max(maxColumn, pic.getLowerRightColumn());
            int dx = pic.getLeft();
            int dy = pic.getTop();
            String filename = "pic_img" + imagecount++ + ".jpg";
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageOrPrintOptions options = new ImageOrPrintOptions();
            options.setImageType(5);
            options.setQuality(90);
            pic.toImage((OutputStream)bout, options);
            String ref = handler.handleImage(bout.toByteArray(), filename);
            picMap.put(column + "_" + row, new EmbeddedXlsImage(ref, dx, dy));
        }
        return new ImageCollection(picMap, maxRow, maxColumn);
    }

    private Map<String, Hyperlink> collectHyperlinks(HyperlinkCollection hyperlinks) {
        HashMap<String, Hyperlink> hyperlinkMap = new HashMap<String, Hyperlink>();
        for (Object object : hyperlinks) {
            Hyperlink hyperlink = (Hyperlink)object;
            CellArea area = hyperlink.getArea();
            int startRow = area.StartRow;
            int startCol = area.StartColumn;
            hyperlinkMap.put(startCol + "_" + startRow, hyperlink);
        }
        return hyperlinkMap;
    }

    private void writeStyles(String tableCssClassName, Writer writer) throws IOException {
        writer.write("<style>\r\n");
        for (Map.Entry<CustomFormat, String> formatEntry : this.formatToCssClassMap.entrySet()) {
            this.writeStyle(tableCssClassName, formatEntry.getKey(), formatEntry.getValue());
        }
        writer.write("</style>\r\n");
    }

    private void writeStyle(String tableClassName, CustomFormat format, String tdCssClassName) throws IOException {
        this.writer.write("table.");
        this.writer.write(tableClassName);
        this.writer.write(" tr td.");
        this.writer.write(tdCssClassName);
        this.writer.write(" {\r\n");
        this.writer.write(this.renderedContentCleaner.cleanStyleAttribute(format.getStyleProps()));
        this.writer.write(" }\r\n");
    }

    private int calcTableWidth(Cells cells, int lastCol) {
        int tableWidth = 0;
        for (int colnum = 0; colnum <= lastCol; ++colnum) {
            int colWidth = cells.getColumnWidthPixel(colnum);
            tableWidth += colWidth;
        }
        return tableWidth;
    }

    private String getTableClassName() {
        return "offconn-" + this.instanceId;
    }

    private String encodeHTML(String html) {
        return HtmlUtil.htmlEncode((String)html);
    }

    private static class EmbeddedXlsImage {
        private final String ref;
        private final int dx;
        private final int dy;

        public EmbeddedXlsImage(String ref, int dx, int dy) {
            this.ref = ref;
            this.dx = dx;
            this.dy = dy;
        }

        public String getRef() {
            return this.ref;
        }

        public int getDx() {
            return this.dx;
        }

        public int getDy() {
            return this.dy;
        }
    }

    private static class ImageCollection {
        private final Map<String, EmbeddedXlsImage> imgMap;
        private final int maxRow;
        private final int maxColumn;

        public ImageCollection(Map<String, EmbeddedXlsImage> imgMap, int maxRow, int maxColumn) {
            this.imgMap = imgMap;
            this.maxRow = maxRow;
            this.maxColumn = maxColumn;
        }

        public Map<String, EmbeddedXlsImage> getImgMap() {
            return this.imgMap;
        }

        public int getMaxRow() {
            return this.maxRow;
        }

        public int getMaxColumn() {
            return this.maxColumn;
        }
    }
}

