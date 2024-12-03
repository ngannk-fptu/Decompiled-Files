/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ooxml.util;

import java.util.Locale;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.openxmlformats.schemas.drawingml.x2006.chart.STDepthPercent;
import org.openxmlformats.schemas.drawingml.x2006.chart.STGapAmount;
import org.openxmlformats.schemas.drawingml.x2006.chart.STHPercent;
import org.openxmlformats.schemas.drawingml.x2006.chart.STHoleSize;
import org.openxmlformats.schemas.drawingml.x2006.chart.STOverlap;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate32;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveFixedPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositivePercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextBulletSizePercent;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontScalePercentOrPercentString;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextPoint;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextSpacingPercentOrPercentString;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumberOrPercent;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMeasurementOrPercent;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTextScale;

public class POIXMLUnits {
    public static int parsePercent(STPositivePercentage pctUnion) {
        return POIXMLUnits.parsePercentInner(pctUnion, 1);
    }

    public static int parsePercent(STPositiveFixedPercentage pctUnion) {
        return POIXMLUnits.parsePercentInner(pctUnion, 1);
    }

    public static int parsePercent(STPercentage pctUnion) {
        return POIXMLUnits.parsePercentInner(pctUnion, 1);
    }

    public static int parsePercent(STTextBulletSizePercent pctUnion) {
        return POIXMLUnits.parsePercentInner(pctUnion, 1);
    }

    public static int parsePercent(STTextSpacingPercentOrPercentString pctUnion) {
        return POIXMLUnits.parsePercentInner(pctUnion, 1);
    }

    public static int parsePercent(STTextFontScalePercentOrPercentString pctUnion) {
        return POIXMLUnits.parsePercentInner(pctUnion, 1);
    }

    public static int parsePercent(STDecimalNumberOrPercent pctUnion) {
        return POIXMLUnits.parsePercentInner(pctUnion, 1000);
    }

    public static int parsePercent(STTextScale pctUnion) {
        return POIXMLUnits.parsePercentInner(pctUnion, 1000);
    }

    public static int parsePercent(STGapAmount pctUnion) {
        return POIXMLUnits.parsePercentInner(pctUnion, 1000);
    }

    public static int parsePercent(STOverlap pctUnion) {
        return POIXMLUnits.parsePercentInner(pctUnion, 1000);
    }

    public static int parsePercent(STDepthPercent pctUnion) {
        return POIXMLUnits.parsePercentInner(pctUnion, 1000);
    }

    public static int parsePercent(STHPercent pctUnion) {
        return POIXMLUnits.parsePercentInner(pctUnion, 1000);
    }

    public static int parsePercent(STHoleSize pctUnion) {
        return POIXMLUnits.parsePercentInner(pctUnion, 1);
    }

    private static int parsePercentInner(XmlAnySimpleType pctUnion, int noUnitScale) {
        String strVal = pctUnion.getStringValue();
        if (strVal.endsWith("%")) {
            return Integer.parseInt(strVal.substring(0, strVal.length() - 1)) * 1000;
        }
        return Integer.parseInt(strVal) * noUnitScale;
    }

    public static long parseLength(STCoordinate32 coordUnion) {
        return POIXMLUnits.parseLengthInner(coordUnion, 1.0);
    }

    public static long parseLength(STCoordinate coordUnion) {
        return POIXMLUnits.parseLengthInner(coordUnion, 1.0);
    }

    public static long parseLength(STTextPoint coordUnion) {
        return POIXMLUnits.parseLengthInner(coordUnion, 127.0);
    }

    public static long parseLength(STTwipsMeasure coordUnion) {
        return POIXMLUnits.parseLengthInner(coordUnion, 635.0);
    }

    public static long parseLength(STSignedTwipsMeasure coordUnion) {
        return POIXMLUnits.parseLengthInner(coordUnion, 635.0);
    }

    public static long parseLength(STHpsMeasure coordUnion) {
        return POIXMLUnits.parseLengthInner(coordUnion, 25400.0);
    }

    public static long parseLength(STSignedHpsMeasure coordUnion) {
        return POIXMLUnits.parseLengthInner(coordUnion, 25400.0);
    }

    public static long parseLength(STMeasurementOrPercent coordUnion) {
        if (coordUnion.getStringValue().endsWith("%")) {
            return -1L;
        }
        return POIXMLUnits.parseLengthInner(coordUnion, 635.0);
    }

    private static long parseLengthInner(XmlAnySimpleType coordUnion, double noUnitEmuFactor) {
        String strVal = coordUnion.getStringValue().toLowerCase(Locale.ROOT);
        double digVal = Double.parseDouble(strVal.replaceAll("(mm|cm|in|pt|pc|pi)", ""));
        long emu = strVal.endsWith("mm") ? (long)(digVal / 10.0 / (double)2.54f * 914400.0) : (strVal.endsWith("cm") ? (long)(digVal / (double)2.54f * 914400.0) : (strVal.endsWith("in") ? (long)(digVal * 914400.0) : (strVal.endsWith("pc") || strVal.endsWith("pi") ? (long)(digVal * (double)0.166f * 914400.0) : (strVal.endsWith("pt") ? (long)(digVal * 12700.0) : (long)(digVal * noUnitEmuFactor)))));
        return emu;
    }

    public static boolean parseOnOff(CTOnOff onOff) {
        if (onOff == null) {
            return false;
        }
        if (!onOff.isSetVal()) {
            return true;
        }
        return POIXMLUnits.parseOnOff(onOff.xgetVal());
    }

    public static boolean parseOnOff(STOnOff onOff) {
        if (onOff == null) {
            return false;
        }
        String str = onOff.getStringValue();
        return "true".equalsIgnoreCase(str) || "on".equalsIgnoreCase(str) || "x".equalsIgnoreCase(str) || "1".equals(str);
    }
}

