/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.SheetIdentifier;
import org.apache.poi.ss.formula.SheetRangeAndWorkbookIndexFormatter;
import org.apache.poi.ss.formula.SheetRangeIdentifier;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Pxg3D;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class Area3DPxg
extends AreaPtgBase
implements Pxg3D {
    private int externalWorkbookNumber = -1;
    private String firstSheetName;
    private String lastSheetName;

    public Area3DPxg(Area3DPxg other) {
        super(other);
        this.externalWorkbookNumber = other.externalWorkbookNumber;
        this.firstSheetName = other.firstSheetName;
        this.lastSheetName = other.lastSheetName;
    }

    public Area3DPxg(int externalWorkbookNumber, SheetIdentifier sheetName, String arearef) {
        this(externalWorkbookNumber, sheetName, new AreaReference(arearef, SpreadsheetVersion.EXCEL2007));
    }

    public Area3DPxg(int externalWorkbookNumber, SheetIdentifier sheetName, AreaReference arearef) {
        super(arearef);
        this.externalWorkbookNumber = externalWorkbookNumber;
        this.firstSheetName = sheetName.getSheetIdentifier().getName();
        this.lastSheetName = sheetName instanceof SheetRangeIdentifier ? ((SheetRangeIdentifier)sheetName).getLastSheetIdentifier().getName() : null;
    }

    public Area3DPxg(SheetIdentifier sheetName, String arearef) {
        this(sheetName, new AreaReference(arearef, SpreadsheetVersion.EXCEL2007));
    }

    public Area3DPxg(SheetIdentifier sheetName, AreaReference arearef) {
        this(-1, sheetName, arearef);
    }

    @Override
    public int getExternalWorkbookNumber() {
        return this.externalWorkbookNumber;
    }

    @Override
    public String getSheetName() {
        return this.firstSheetName;
    }

    @Override
    public String getLastSheetName() {
        return this.lastSheetName;
    }

    @Override
    public void setSheetName(String sheetName) {
        this.firstSheetName = sheetName;
    }

    @Override
    public void setLastSheetName(String sheetName) {
        this.lastSheetName = sheetName;
    }

    public String format2DRefAsString() {
        return this.formatReferenceAsString();
    }

    @Override
    public String toFormulaString() {
        StringBuilder sb = new StringBuilder(64);
        SheetRangeAndWorkbookIndexFormatter.format(sb, this.externalWorkbookNumber, this.firstSheetName, this.lastSheetName);
        sb.append('!');
        sb.append(this.formatReferenceAsString());
        return sb.toString();
    }

    @Override
    public byte getSid() {
        return -1;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public void write(LittleEndianOutput out) {
        throw new IllegalStateException("XSSF-only Ptg, should not be serialised");
    }

    @Override
    public Area3DPxg copy() {
        return new Area3DPxg(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "externalWorkbookNumber", this::getExternalWorkbookNumber, "sheetName", this::getSheetName, "lastSheetName", this::getLastSheetName);
    }
}

