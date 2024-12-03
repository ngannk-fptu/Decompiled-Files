/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Internal;

@Internal
class AbstractExcelUtils {
    static final String EMPTY = "";
    private static final short EXCEL_COLUMN_WIDTH_FACTOR = 256;
    private static final int UNIT_OFFSET_LENGTH = 7;

    AbstractExcelUtils() {
    }

    public static String getAlign(HorizontalAlignment alignment) {
        switch (alignment) {
            case CENTER: 
            case CENTER_SELECTION: {
                return "center";
            }
            case FILL: {
                return EMPTY;
            }
            case JUSTIFY: {
                return "justify";
            }
            case LEFT: {
                return "left";
            }
            case RIGHT: {
                return "right";
            }
        }
        return EMPTY;
    }

    public static String getBorderStyle(BorderStyle xlsBorder) {
        String borderStyle;
        switch (xlsBorder) {
            case NONE: {
                borderStyle = "none";
                break;
            }
            case DASH_DOT: 
            case DASH_DOT_DOT: 
            case DOTTED: 
            case HAIR: 
            case MEDIUM_DASH_DOT: 
            case MEDIUM_DASH_DOT_DOT: 
            case SLANTED_DASH_DOT: {
                borderStyle = "dotted";
                break;
            }
            case DASHED: 
            case MEDIUM_DASHED: {
                borderStyle = "dashed";
                break;
            }
            case DOUBLE: {
                borderStyle = "double";
                break;
            }
            default: {
                borderStyle = "solid";
            }
        }
        return borderStyle;
    }

    public static String getBorderWidth(BorderStyle xlsBorder) {
        String borderWidth;
        switch (xlsBorder) {
            case MEDIUM_DASH_DOT: 
            case MEDIUM_DASH_DOT_DOT: 
            case MEDIUM_DASHED: {
                borderWidth = "2pt";
                break;
            }
            case THICK: {
                borderWidth = "thick";
                break;
            }
            default: {
                borderWidth = "thin";
            }
        }
        return borderWidth;
    }

    public static String getColor(HSSFColor color) {
        StringBuilder stringBuilder = new StringBuilder(7);
        stringBuilder.append('#');
        for (short s : color.getTriplet()) {
            if (s < 10) {
                stringBuilder.append('0');
            }
            stringBuilder.append(Integer.toHexString(s));
        }
        String result = stringBuilder.toString();
        if (result.equals("#ffffff")) {
            return "white";
        }
        if (result.equals("#c0c0c0")) {
            return "silver";
        }
        if (result.equals("#808080")) {
            return "gray";
        }
        if (result.equals("#000000")) {
            return "black";
        }
        return result;
    }

    public static int getColumnWidthInPx(int widthUnits) {
        int pixels = widthUnits / 256 * 7;
        int offsetWidthUnits = widthUnits % 256;
        return pixels += Math.round((float)offsetWidthUnits / 36.57143f);
    }

    public static CellRangeAddress getMergedRange(CellRangeAddress[][] mergedRanges, int rowNumber, int columnNumber) {
        CellRangeAddress[] mergedRangeRowInfo = rowNumber < mergedRanges.length ? mergedRanges[rowNumber] : null;
        return mergedRangeRowInfo != null && columnNumber < mergedRangeRowInfo.length ? mergedRangeRowInfo[columnNumber] : null;
    }

    static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    static boolean isNotEmpty(String str) {
        return !AbstractExcelUtils.isEmpty(str);
    }

    public static HSSFWorkbook loadXls(File xlsFile) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(xlsFile);){
            HSSFWorkbook hSSFWorkbook = new HSSFWorkbook(inputStream);
            return hSSFWorkbook;
        }
    }

    public static void appendAlign(StringBuilder style, HorizontalAlignment alignment) {
        String cssAlign = AbstractExcelUtils.getAlign(alignment);
        if (AbstractExcelUtils.isEmpty(cssAlign)) {
            return;
        }
        style.append("text-align:");
        style.append(cssAlign);
        style.append(";");
    }

    public static CellRangeAddress[][] buildMergedRangesMap(Sheet sheet) {
        CellRangeAddress[][] mergedRanges = new CellRangeAddress[1][];
        for (CellRangeAddress cellRangeAddress : sheet.getMergedRegions()) {
            int requiredHeight = cellRangeAddress.getLastRow() + 1;
            if (mergedRanges.length < requiredHeight) {
                mergedRanges = (CellRangeAddress[][])Arrays.copyOf(mergedRanges, requiredHeight, CellRangeAddress[][].class);
            }
            for (int r = cellRangeAddress.getFirstRow(); r <= cellRangeAddress.getLastRow(); ++r) {
                int requiredWidth = cellRangeAddress.getLastColumn() + 1;
                Object[] rowMerged = mergedRanges[r];
                if (rowMerged == null) {
                    rowMerged = new CellRangeAddress[requiredWidth];
                    mergedRanges[r] = rowMerged;
                } else {
                    int rowMergedLength = rowMerged.length;
                    if (rowMergedLength < requiredWidth) {
                        mergedRanges[r] = (CellRangeAddress[])Arrays.copyOf(rowMerged, requiredWidth, CellRangeAddress[].class);
                        rowMerged = mergedRanges[r];
                    }
                }
                Arrays.fill(rowMerged, cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn() + 1, cellRangeAddress);
            }
        }
        return mergedRanges;
    }
}

