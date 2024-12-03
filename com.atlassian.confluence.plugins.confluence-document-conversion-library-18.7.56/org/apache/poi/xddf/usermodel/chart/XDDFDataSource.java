/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel.chart;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrVal;

public interface XDDFDataSource<T> {
    public int getPointCount();

    public T getPointAt(int var1);

    public boolean isLiteral();

    public boolean isCellRange();

    public boolean isReference();

    public boolean isNumeric();

    public int getColIndex();

    public String getDataRangeReference();

    default public String getFormula() {
        return this.getDataRangeReference();
    }

    public String getFormatCode();

    @Internal
    default public void fillNumericalCache(CTNumData cache) {
        String formatCode = this.getFormatCode();
        if (formatCode == null) {
            if (cache.isSetFormatCode()) {
                cache.unsetFormatCode();
            }
        } else {
            cache.setFormatCode(formatCode);
        }
        cache.setPtArray(null);
        int numOfPoints = this.getPointCount();
        int effectiveNumOfPoints = 0;
        for (int i = 0; i < numOfPoints; ++i) {
            T value = this.getPointAt(i);
            if (value == null) continue;
            CTNumVal ctNumVal = cache.addNewPt();
            ctNumVal.setIdx(i);
            ctNumVal.setV(value.toString());
            ++effectiveNumOfPoints;
        }
        if (effectiveNumOfPoints == 0) {
            if (cache.isSetPtCount()) {
                cache.unsetPtCount();
            }
        } else if (cache.isSetPtCount()) {
            cache.getPtCount().setVal(numOfPoints);
        } else {
            cache.addNewPtCount().setVal(numOfPoints);
        }
    }

    @Internal
    default public void fillStringCache(CTStrData cache) {
        cache.setPtArray(null);
        int numOfPoints = this.getPointCount();
        int effectiveNumOfPoints = 0;
        for (int i = 0; i < numOfPoints; ++i) {
            T value = this.getPointAt(i);
            if (value == null) continue;
            CTStrVal ctStrVal = cache.addNewPt();
            ctStrVal.setIdx(i);
            ctStrVal.setV(value.toString());
            ++effectiveNumOfPoints;
        }
        if (effectiveNumOfPoints == 0) {
            if (cache.isSetPtCount()) {
                cache.unsetPtCount();
            }
        } else if (cache.isSetPtCount()) {
            cache.getPtCount().setVal(numOfPoints);
        } else {
            cache.addNewPtCount().setVal(numOfPoints);
        }
    }
}

