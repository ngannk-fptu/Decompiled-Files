/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.converter;

import org.apache.poi.hssf.converter.AbstractExcelUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hwpf.converter.DefaultFontReplacer;
import org.apache.poi.hwpf.converter.FontReplacer;
import org.apache.poi.hwpf.converter.NumberFormatter;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.w3c.dom.Document;

public abstract class AbstractExcelConverter {
    protected final HSSFDataFormatter _formatter = new HSSFDataFormatter();
    private FontReplacer fontReplacer = new DefaultFontReplacer();
    private boolean outputColumnHeaders = true;
    private boolean outputHiddenColumns;
    private boolean outputHiddenRows;
    private boolean outputLeadingSpacesAsNonBreaking = true;
    private boolean outputRowNumbers = true;

    protected static int getColumnWidth(HSSFSheet sheet, int columnIndex) {
        return AbstractExcelUtils.getColumnWidthInPx(sheet.getColumnWidth(columnIndex));
    }

    protected static int getDefaultColumnWidth(HSSFSheet sheet) {
        return AbstractExcelUtils.getColumnWidthInPx(sheet.getDefaultColumnWidth());
    }

    protected String getColumnName(int columnIndex) {
        return NumberFormatter.getNumber(columnIndex + 1, 3);
    }

    protected abstract Document getDocument();

    public FontReplacer getFontReplacer() {
        return this.fontReplacer;
    }

    protected String getRowName(HSSFRow row) {
        return String.valueOf(row.getRowNum() + 1);
    }

    public boolean isOutputColumnHeaders() {
        return this.outputColumnHeaders;
    }

    public boolean isOutputHiddenColumns() {
        return this.outputHiddenColumns;
    }

    public boolean isOutputHiddenRows() {
        return this.outputHiddenRows;
    }

    public boolean isOutputLeadingSpacesAsNonBreaking() {
        return this.outputLeadingSpacesAsNonBreaking;
    }

    public boolean isOutputRowNumbers() {
        return this.outputRowNumbers;
    }

    protected boolean isTextEmpty(HSSFCell cell) {
        String value;
        block0 : switch (cell.getCellType()) {
            case STRING: {
                value = cell.getRichStringCellValue().getString();
                break;
            }
            case FORMULA: {
                switch (cell.getCachedFormulaResultType()) {
                    case STRING: {
                        HSSFRichTextString str = cell.getRichStringCellValue();
                        if (str == null || str.length() <= 0) {
                            return false;
                        }
                        value = str.toString();
                        break block0;
                    }
                    case NUMERIC: {
                        HSSFCellStyle style = cell.getCellStyle();
                        double nval = cell.getNumericCellValue();
                        short df = style.getDataFormat();
                        String dfs = style.getDataFormatString();
                        value = this._formatter.formatRawCellContents(nval, df, dfs);
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
                return true;
            }
        }
        return AbstractExcelUtils.isEmpty(value);
    }

    public void setFontReplacer(FontReplacer fontReplacer) {
        this.fontReplacer = fontReplacer;
    }

    public void setOutputColumnHeaders(boolean outputColumnHeaders) {
        this.outputColumnHeaders = outputColumnHeaders;
    }

    public void setOutputHiddenColumns(boolean outputZeroWidthColumns) {
        this.outputHiddenColumns = outputZeroWidthColumns;
    }

    public void setOutputHiddenRows(boolean outputZeroHeightRows) {
        this.outputHiddenRows = outputZeroHeightRows;
    }

    public void setOutputLeadingSpacesAsNonBreaking(boolean outputPrePostSpacesAsNonBreaking) {
        this.outputLeadingSpacesAsNonBreaking = outputPrePostSpacesAsNonBreaking;
    }

    public void setOutputRowNumbers(boolean outputRowNumbers) {
        this.outputRowNumbers = outputRowNumbers;
    }
}

