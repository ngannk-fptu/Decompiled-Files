/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyContext;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

public final class CellUtil {
    private static final Logger LOGGER = LogManager.getLogger(CellUtil.class);
    public static final String ALIGNMENT = "alignment";
    public static final String BORDER_BOTTOM = "borderBottom";
    public static final String BORDER_LEFT = "borderLeft";
    public static final String BORDER_RIGHT = "borderRight";
    public static final String BORDER_TOP = "borderTop";
    public static final String BOTTOM_BORDER_COLOR = "bottomBorderColor";
    public static final String LEFT_BORDER_COLOR = "leftBorderColor";
    public static final String RIGHT_BORDER_COLOR = "rightBorderColor";
    public static final String TOP_BORDER_COLOR = "topBorderColor";
    public static final String DATA_FORMAT = "dataFormat";
    public static final String FILL_BACKGROUND_COLOR = "fillBackgroundColor";
    public static final String FILL_FOREGROUND_COLOR = "fillForegroundColor";
    public static final String FILL_BACKGROUND_COLOR_COLOR = "fillBackgroundColorColor";
    public static final String FILL_FOREGROUND_COLOR_COLOR = "fillForegroundColorColor";
    public static final String FILL_PATTERN = "fillPattern";
    public static final String FONT = "font";
    public static final String HIDDEN = "hidden";
    public static final String INDENTION = "indention";
    public static final String LOCKED = "locked";
    public static final String ROTATION = "rotation";
    public static final String VERTICAL_ALIGNMENT = "verticalAlignment";
    public static final String WRAP_TEXT = "wrapText";
    public static final String SHRINK_TO_FIT = "shrinkToFit";
    public static final String QUOTE_PREFIXED = "quotePrefixed";
    private static final Set<String> shortValues = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("bottomBorderColor", "leftBorderColor", "rightBorderColor", "topBorderColor", "fillForegroundColor", "fillBackgroundColor", "indention", "dataFormat", "rotation")));
    private static final Set<String> colorValues = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("fillForegroundColorColor", "fillBackgroundColorColor")));
    private static final Set<String> intValues = Collections.unmodifiableSet(new HashSet<String>(Collections.singletonList("font")));
    private static final Set<String> booleanValues = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("locked", "hidden", "wrapText", "shrinkToFit", "quotePrefixed")));
    private static final Set<String> borderTypeValues = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("borderBottom", "borderLeft", "borderRight", "borderTop")));
    private static final UnicodeMapping[] unicodeMappings = new UnicodeMapping[]{CellUtil.um("alpha", "\u03b1"), CellUtil.um("beta", "\u03b2"), CellUtil.um("gamma", "\u03b3"), CellUtil.um("delta", "\u03b4"), CellUtil.um("epsilon", "\u03b5"), CellUtil.um("zeta", "\u03b6"), CellUtil.um("eta", "\u03b7"), CellUtil.um("theta", "\u03b8"), CellUtil.um("iota", "\u03b9"), CellUtil.um("kappa", "\u03ba"), CellUtil.um("lambda", "\u03bb"), CellUtil.um("mu", "\u03bc"), CellUtil.um("nu", "\u03bd"), CellUtil.um("xi", "\u03be"), CellUtil.um("omicron", "\u03bf")};

    private CellUtil() {
    }

    public static Row getRow(int rowIndex, Sheet sheet) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        return row;
    }

    public static Cell getCell(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        return cell;
    }

    public static Cell createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = CellUtil.getCell(row, column);
        cell.setCellValue(cell.getRow().getSheet().getWorkbook().getCreationHelper().createRichTextString(value));
        if (style != null) {
            cell.setCellStyle(style);
        }
        return cell;
    }

    public static Cell createCell(Row row, int column, String value) {
        return CellUtil.createCell(row, column, value, null);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void copyCell(Cell srcCell, Cell destCell, CellCopyPolicy policy, CellCopyContext context) {
        Hyperlink newHyperlink;
        Hyperlink srcHyperlink;
        if (policy.isCopyCellValue()) {
            if (srcCell != null) {
                CellType copyCellType = srcCell.getCellType();
                if (copyCellType == CellType.FORMULA && !policy.isCopyCellFormula()) {
                    copyCellType = srcCell.getCachedFormulaResultType();
                }
                switch (copyCellType) {
                    case NUMERIC: {
                        if (DateUtil.isCellDateFormatted(srcCell)) {
                            destCell.setCellValue(srcCell.getDateCellValue());
                            break;
                        }
                        destCell.setCellValue(srcCell.getNumericCellValue());
                        break;
                    }
                    case STRING: {
                        destCell.setCellValue(srcCell.getRichStringCellValue());
                        break;
                    }
                    case FORMULA: {
                        destCell.setCellFormula(srcCell.getCellFormula());
                        break;
                    }
                    case BLANK: {
                        destCell.setBlank();
                        break;
                    }
                    case BOOLEAN: {
                        destCell.setCellValue(srcCell.getBooleanCellValue());
                        break;
                    }
                    case ERROR: {
                        destCell.setCellErrorValue(srcCell.getErrorCellValue());
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Invalid cell type " + (Object)((Object)srcCell.getCellType()));
                    }
                }
            } else {
                destCell.setBlank();
            }
        }
        if (policy.isCopyCellStyle()) {
            if (srcCell.getSheet() != null && destCell.getSheet() != null && destCell.getSheet().getWorkbook() == srcCell.getSheet().getWorkbook()) {
                destCell.setCellStyle(srcCell.getCellStyle());
            } else {
                CellStyle destStyle;
                CellStyle srcStyle = srcCell.getCellStyle();
                CellStyle cellStyle = destStyle = context == null ? null : context.getMappedStyle(srcStyle);
                if (destStyle == null) {
                    destStyle = destCell.getSheet().getWorkbook().createCellStyle();
                    destStyle.cloneStyleFrom(srcStyle);
                    if (context != null) {
                        context.putMappedStyle(srcStyle, destStyle);
                    }
                }
                destCell.setCellStyle(destStyle);
            }
        }
        Hyperlink hyperlink = srcHyperlink = srcCell == null ? null : srcCell.getHyperlink();
        if (policy.isMergeHyperlink()) {
            if (srcHyperlink == null) return;
            if (!(srcHyperlink instanceof Duplicatable)) throw new IllegalStateException("srcCell hyperlink is not an instance of Duplicatable");
            newHyperlink = (Hyperlink)((Object)((Duplicatable)((Object)srcHyperlink)).copy());
            destCell.setHyperlink(newHyperlink);
            return;
        } else {
            if (!policy.isCopyHyperlink()) return;
            if (srcHyperlink == null) {
                destCell.setHyperlink(null);
                return;
            } else {
                if (!(srcHyperlink instanceof Duplicatable)) throw new IllegalStateException("srcCell hyperlink is not an instance of Duplicatable");
                newHyperlink = (Hyperlink)((Object)((Duplicatable)((Object)srcHyperlink)).copy());
                destCell.setHyperlink(newHyperlink);
            }
        }
    }

    public static void setAlignment(Cell cell, HorizontalAlignment align) {
        CellUtil.setCellStyleProperty(cell, ALIGNMENT, (Object)align);
    }

    public static void setVerticalAlignment(Cell cell, VerticalAlignment align) {
        CellUtil.setCellStyleProperty(cell, VERTICAL_ALIGNMENT, (Object)align);
    }

    public static void setFont(Cell cell, Font font) {
        int fontIndex;
        Workbook wb = cell.getSheet().getWorkbook();
        if (!wb.getFontAt(fontIndex = font.getIndex()).equals(font)) {
            throw new IllegalArgumentException("Font does not belong to this workbook");
        }
        CellUtil.setCellStyleProperty(cell, FONT, fontIndex);
    }

    public static void setCellStyleProperties(Cell cell, Map<String, Object> properties) {
        CellUtil.setCellStyleProperties(cell, properties, false);
    }

    private static void setCellStyleProperties(Cell cell, Map<String, Object> properties, boolean disableNullColorCheck) {
        Workbook workbook = cell.getSheet().getWorkbook();
        CellStyle originalStyle = cell.getCellStyle();
        CellStyle newStyle = null;
        Map<String, Object> values = CellUtil.getFormatProperties(originalStyle);
        if (properties.containsKey(FILL_FOREGROUND_COLOR_COLOR) && properties.get(FILL_FOREGROUND_COLOR_COLOR) == null) {
            values.remove(FILL_FOREGROUND_COLOR);
        }
        if (properties.containsKey(FILL_BACKGROUND_COLOR_COLOR) && properties.get(FILL_BACKGROUND_COLOR_COLOR) == null) {
            values.remove(FILL_BACKGROUND_COLOR);
        }
        CellUtil.putAll(properties, values);
        int numberCellStyles = workbook.getNumCellStyles();
        for (int i = 0; i < numberCellStyles; ++i) {
            CellStyle wbStyle = workbook.getCellStyleAt(i);
            Map<String, Object> wbStyleMap = CellUtil.getFormatProperties(wbStyle);
            if (!CellUtil.styleMapsMatch(wbStyleMap, values, disableNullColorCheck)) continue;
            newStyle = wbStyle;
            break;
        }
        if (newStyle == null) {
            newStyle = workbook.createCellStyle();
            CellUtil.setFormatProperties(newStyle, workbook, values);
        }
        cell.setCellStyle(newStyle);
    }

    private static boolean styleMapsMatch(Map<String, Object> newProps, Map<String, Object> storedProps, boolean disableNullColorCheck) {
        HashMap<String, Object> map1Copy = new HashMap<String, Object>(newProps);
        HashMap<String, Object> map2Copy = new HashMap<String, Object>(storedProps);
        Object backColor1 = map1Copy.remove(FILL_BACKGROUND_COLOR_COLOR);
        Object backColor2 = map2Copy.remove(FILL_BACKGROUND_COLOR_COLOR);
        Object foreColor1 = map1Copy.remove(FILL_FOREGROUND_COLOR_COLOR);
        Object foreColor2 = map2Copy.remove(FILL_FOREGROUND_COLOR_COLOR);
        if (map1Copy.equals(map2Copy)) {
            boolean backColorsMatch = !disableNullColorCheck && backColor2 == null || Objects.equals(backColor1, backColor2);
            boolean foreColorsMatch = !disableNullColorCheck && foreColor2 == null || Objects.equals(foreColor1, foreColor2);
            return backColorsMatch && foreColorsMatch;
        }
        return false;
    }

    public static void setCellStyleProperty(Cell cell, String propertyName, Object propertyValue) {
        Map<Object, Object> propMap;
        boolean disableNullColorCheck = false;
        if (FILL_FOREGROUND_COLOR_COLOR.equals(propertyName) && propertyValue == null) {
            disableNullColorCheck = true;
            propMap = new HashMap();
            propMap.put(FILL_FOREGROUND_COLOR_COLOR, null);
            propMap.put(FILL_FOREGROUND_COLOR, null);
        } else if (FILL_BACKGROUND_COLOR_COLOR.equals(propertyName) && propertyValue == null) {
            disableNullColorCheck = true;
            propMap = new HashMap();
            propMap.put(FILL_BACKGROUND_COLOR_COLOR, null);
            propMap.put(FILL_BACKGROUND_COLOR, null);
        } else {
            propMap = Collections.singletonMap(propertyName, propertyValue);
        }
        CellUtil.setCellStyleProperties(cell, propMap, disableNullColorCheck);
    }

    private static Map<String, Object> getFormatProperties(CellStyle style) {
        HashMap<String, Object> properties = new HashMap<String, Object>();
        CellUtil.put(properties, ALIGNMENT, (Object)style.getAlignment());
        CellUtil.put(properties, VERTICAL_ALIGNMENT, (Object)style.getVerticalAlignment());
        CellUtil.put(properties, BORDER_BOTTOM, (Object)style.getBorderBottom());
        CellUtil.put(properties, BORDER_LEFT, (Object)style.getBorderLeft());
        CellUtil.put(properties, BORDER_RIGHT, (Object)style.getBorderRight());
        CellUtil.put(properties, BORDER_TOP, (Object)style.getBorderTop());
        CellUtil.put(properties, BOTTOM_BORDER_COLOR, style.getBottomBorderColor());
        CellUtil.put(properties, DATA_FORMAT, style.getDataFormat());
        CellUtil.put(properties, FILL_PATTERN, (Object)style.getFillPattern());
        CellUtil.put(properties, FILL_FOREGROUND_COLOR, style.getFillForegroundColor());
        CellUtil.put(properties, FILL_BACKGROUND_COLOR, style.getFillBackgroundColor());
        CellUtil.put(properties, FILL_FOREGROUND_COLOR_COLOR, style.getFillForegroundColorColor());
        CellUtil.put(properties, FILL_BACKGROUND_COLOR_COLOR, style.getFillBackgroundColorColor());
        CellUtil.put(properties, FONT, style.getFontIndex());
        CellUtil.put(properties, HIDDEN, style.getHidden());
        CellUtil.put(properties, INDENTION, style.getIndention());
        CellUtil.put(properties, LEFT_BORDER_COLOR, style.getLeftBorderColor());
        CellUtil.put(properties, LOCKED, style.getLocked());
        CellUtil.put(properties, RIGHT_BORDER_COLOR, style.getRightBorderColor());
        CellUtil.put(properties, ROTATION, style.getRotation());
        CellUtil.put(properties, TOP_BORDER_COLOR, style.getTopBorderColor());
        CellUtil.put(properties, WRAP_TEXT, style.getWrapText());
        CellUtil.put(properties, SHRINK_TO_FIT, style.getShrinkToFit());
        CellUtil.put(properties, QUOTE_PREFIXED, style.getQuotePrefixed());
        return properties;
    }

    private static void putAll(Map<String, Object> src, Map<String, Object> dest) {
        for (String key : src.keySet()) {
            if (shortValues.contains(key)) {
                dest.put(key, CellUtil.nullableShort(src, key));
                continue;
            }
            if (colorValues.contains(key)) {
                dest.put(key, CellUtil.getColor(src, key));
                continue;
            }
            if (intValues.contains(key)) {
                dest.put(key, CellUtil.getInt(src, key));
                continue;
            }
            if (booleanValues.contains(key)) {
                dest.put(key, CellUtil.getBoolean(src, key));
                continue;
            }
            if (borderTypeValues.contains(key)) {
                dest.put(key, (Object)CellUtil.getBorderStyle(src, key));
                continue;
            }
            if (ALIGNMENT.equals(key)) {
                dest.put(key, (Object)CellUtil.getHorizontalAlignment(src, key));
                continue;
            }
            if (VERTICAL_ALIGNMENT.equals(key)) {
                dest.put(key, (Object)CellUtil.getVerticalAlignment(src, key));
                continue;
            }
            if (FILL_PATTERN.equals(key)) {
                dest.put(key, (Object)CellUtil.getFillPattern(src, key));
                continue;
            }
            LOGGER.atInfo().log("Ignoring unrecognized CellUtil format properties key: {}", (Object)key);
        }
    }

    private static void setFormatProperties(CellStyle style, Workbook workbook, Map<String, Object> properties) {
        Short fillBackColorShort;
        style.setAlignment(CellUtil.getHorizontalAlignment(properties, ALIGNMENT));
        style.setVerticalAlignment(CellUtil.getVerticalAlignment(properties, VERTICAL_ALIGNMENT));
        style.setBorderBottom(CellUtil.getBorderStyle(properties, BORDER_BOTTOM));
        style.setBorderLeft(CellUtil.getBorderStyle(properties, BORDER_LEFT));
        style.setBorderRight(CellUtil.getBorderStyle(properties, BORDER_RIGHT));
        style.setBorderTop(CellUtil.getBorderStyle(properties, BORDER_TOP));
        style.setBottomBorderColor(CellUtil.getShort(properties, BOTTOM_BORDER_COLOR));
        style.setDataFormat(CellUtil.getShort(properties, DATA_FORMAT));
        style.setFillPattern(CellUtil.getFillPattern(properties, FILL_PATTERN));
        Short fillForeColorShort = CellUtil.nullableShort(properties, FILL_FOREGROUND_COLOR);
        if (fillForeColorShort != null) {
            style.setFillForegroundColor(fillForeColorShort);
        }
        if ((fillBackColorShort = CellUtil.nullableShort(properties, FILL_BACKGROUND_COLOR)) != null) {
            style.setFillBackgroundColor(fillBackColorShort);
        }
        Color foregroundFillColor = CellUtil.getColor(properties, FILL_FOREGROUND_COLOR_COLOR);
        Color backgroundFillColor = CellUtil.getColor(properties, FILL_BACKGROUND_COLOR_COLOR);
        if (foregroundFillColor != null) {
            try {
                style.setFillForegroundColor(foregroundFillColor);
            }
            catch (IllegalArgumentException iae) {
                LOGGER.atDebug().log("Mismatched FillForegroundColor instance used", (Object)iae);
            }
        }
        if (backgroundFillColor != null) {
            try {
                style.setFillBackgroundColor(backgroundFillColor);
            }
            catch (IllegalArgumentException iae) {
                LOGGER.atDebug().log("Mismatched FillBackgroundColor instance used", (Object)iae);
            }
        }
        style.setFont(workbook.getFontAt(CellUtil.getInt(properties, FONT)));
        style.setHidden(CellUtil.getBoolean(properties, HIDDEN));
        style.setIndention(CellUtil.getShort(properties, INDENTION));
        style.setLeftBorderColor(CellUtil.getShort(properties, LEFT_BORDER_COLOR));
        style.setLocked(CellUtil.getBoolean(properties, LOCKED));
        style.setRightBorderColor(CellUtil.getShort(properties, RIGHT_BORDER_COLOR));
        style.setRotation(CellUtil.getShort(properties, ROTATION));
        style.setTopBorderColor(CellUtil.getShort(properties, TOP_BORDER_COLOR));
        style.setWrapText(CellUtil.getBoolean(properties, WRAP_TEXT));
        style.setShrinkToFit(CellUtil.getBoolean(properties, SHRINK_TO_FIT));
        style.setQuotePrefixed(CellUtil.getBoolean(properties, QUOTE_PREFIXED));
    }

    private static short getShort(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof Number) {
            return ((Number)value).shortValue();
        }
        return 0;
    }

    private static Short nullableShort(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof Short) {
            return (Short)value;
        }
        if (value instanceof Number) {
            return ((Number)value).shortValue();
        }
        return null;
    }

    private static Color getColor(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof Color) {
            return (Color)value;
        }
        return null;
    }

    private static int getInt(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        return 0;
    }

    private static BorderStyle getBorderStyle(Map<String, Object> properties, String name) {
        BorderStyle border;
        Object value = properties.get(name);
        if (value instanceof BorderStyle) {
            border = (BorderStyle)((Object)value);
        } else if (value instanceof Short) {
            LOGGER.atWarn().log("Deprecation warning: CellUtil properties map uses Short values for {}. Should use BorderStyle enums instead.", (Object)name);
            short code = (Short)value;
            border = BorderStyle.valueOf(code);
        } else if (value == null) {
            border = BorderStyle.NONE;
        } else {
            throw new IllegalStateException("Unexpected border style class. Must be BorderStyle or Short (deprecated).");
        }
        return border;
    }

    private static FillPatternType getFillPattern(Map<String, Object> properties, String name) {
        FillPatternType pattern;
        Object value = properties.get(name);
        if (value instanceof FillPatternType) {
            pattern = (FillPatternType)((Object)value);
        } else if (value instanceof Short) {
            LOGGER.atWarn().log("Deprecation warning: CellUtil properties map uses Short values for {}. Should use FillPatternType enums instead.", (Object)name);
            short code = (Short)value;
            pattern = FillPatternType.forInt(code);
        } else if (value == null) {
            pattern = FillPatternType.NO_FILL;
        } else {
            throw new IllegalStateException("Unexpected fill pattern style class. Must be FillPatternType or Short (deprecated).");
        }
        return pattern;
    }

    private static HorizontalAlignment getHorizontalAlignment(Map<String, Object> properties, String name) {
        HorizontalAlignment align;
        Object value = properties.get(name);
        if (value instanceof HorizontalAlignment) {
            align = (HorizontalAlignment)((Object)value);
        } else if (value instanceof Short) {
            LOGGER.atWarn().log("Deprecation warning: CellUtil properties map used a Short value for {}. Should use HorizontalAlignment enums instead.", (Object)name);
            short code = (Short)value;
            align = HorizontalAlignment.forInt(code);
        } else if (value == null) {
            align = HorizontalAlignment.GENERAL;
        } else {
            throw new IllegalStateException("Unexpected horizontal alignment style class. Must be HorizontalAlignment or Short (deprecated).");
        }
        return align;
    }

    private static VerticalAlignment getVerticalAlignment(Map<String, Object> properties, String name) {
        VerticalAlignment align;
        Object value = properties.get(name);
        if (value instanceof VerticalAlignment) {
            align = (VerticalAlignment)((Object)value);
        } else if (value instanceof Short) {
            LOGGER.atWarn().log("Deprecation warning: CellUtil properties map used a Short value for {}. Should use VerticalAlignment enums instead.", (Object)name);
            short code = (Short)value;
            align = VerticalAlignment.forInt(code);
        } else if (value == null) {
            align = VerticalAlignment.BOTTOM;
        } else {
            throw new IllegalStateException("Unexpected vertical alignment style class. Must be VerticalAlignment or Short (deprecated).");
        }
        return align;
    }

    private static boolean getBoolean(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        return false;
    }

    private static void put(Map<String, Object> properties, String name, Object value) {
        properties.put(name, value);
    }

    public static Cell translateUnicodeValues(Cell cell) {
        String s = cell.getRichStringCellValue().getString();
        boolean foundUnicode = false;
        String lowerCaseStr = s.toLowerCase(Locale.ROOT);
        for (UnicodeMapping entry : unicodeMappings) {
            String key = entry.entityName;
            if (!lowerCaseStr.contains(key)) continue;
            s = s.replaceAll(key, entry.resolvedValue);
            foundUnicode = true;
        }
        if (foundUnicode) {
            cell.setCellValue(cell.getRow().getSheet().getWorkbook().getCreationHelper().createRichTextString(s));
        }
        return cell;
    }

    private static UnicodeMapping um(String entityName, String resolvedValue) {
        return new UnicodeMapping(entityName, resolvedValue);
    }

    private static final class UnicodeMapping {
        public final String entityName;
        public final String resolvedValue;

        public UnicodeMapping(String pEntityName, String pResolvedValue) {
            this.entityName = "&" + pEntityName + ";";
            this.resolvedValue = pResolvedValue;
        }
    }
}

