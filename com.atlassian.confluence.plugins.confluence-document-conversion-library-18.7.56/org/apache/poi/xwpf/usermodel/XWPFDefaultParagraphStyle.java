/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;

public class XWPFDefaultParagraphStyle {
    private final CTPPrGeneral ppr;

    public XWPFDefaultParagraphStyle(CTPPrGeneral ppr) {
        this.ppr = ppr;
    }

    protected CTPPrGeneral getPPr() {
        return this.ppr;
    }

    public int getSpacingAfter() {
        return this.ppr.isSetSpacing() ? (int)Units.toDXA(POIXMLUnits.parseLength(this.ppr.getSpacing().xgetAfter())) : -1;
    }
}

