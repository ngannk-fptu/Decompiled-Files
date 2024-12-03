/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.usermodel;

import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.sl.usermodel.TabStop;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStop;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextTabAlignType;

public class XSLFTabStop
implements TabStop {
    final CTTextTabStop tabStop;

    XSLFTabStop(CTTextTabStop tabStop) {
        this.tabStop = tabStop;
    }

    public int getPosition() {
        return (int)POIXMLUnits.parseLength(this.tabStop.xgetPos());
    }

    public void setPosition(int position) {
        this.tabStop.setPos(position);
    }

    @Override
    public double getPositionInPoints() {
        return Units.toPoints(this.getPosition());
    }

    @Override
    public void setPositionInPoints(double points) {
        this.setPosition(Units.toEMU(points));
    }

    @Override
    public TabStop.TabStopType getType() {
        return TabStop.TabStopType.fromOoxmlId(this.tabStop.getAlgn().intValue());
    }

    @Override
    public void setType(TabStop.TabStopType tabStopType) {
        this.tabStop.setAlgn(STTextTabAlignType.Enum.forInt(tabStopType.ooxmlId));
    }
}

