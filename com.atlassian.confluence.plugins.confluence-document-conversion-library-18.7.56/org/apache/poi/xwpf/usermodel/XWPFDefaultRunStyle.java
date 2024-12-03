/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.util.Removal;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;

public class XWPFDefaultRunStyle {
    private CTRPr rpr;

    public XWPFDefaultRunStyle(CTRPr rpr) {
        this.rpr = rpr;
    }

    protected CTRPr getRPr() {
        return this.rpr;
    }

    @Deprecated
    @Removal(version="6.0.0")
    public int getFontSize() {
        BigDecimal bd = this.getFontSizeAsBigDecimal(0);
        return bd == null ? -1 : bd.intValue();
    }

    public Double getFontSizeAsDouble() {
        BigDecimal bd = this.getFontSizeAsBigDecimal(1);
        return bd == null ? null : Double.valueOf(bd.doubleValue());
    }

    private BigDecimal getFontSizeAsBigDecimal(int scale) {
        return this.rpr != null && this.rpr.sizeOfSzArray() > 0 ? BigDecimal.valueOf(Units.toPoints(POIXMLUnits.parseLength(this.rpr.getSzArray(0).xgetVal()))).divide(BigDecimal.valueOf(4L), scale, RoundingMode.HALF_UP) : null;
    }
}

