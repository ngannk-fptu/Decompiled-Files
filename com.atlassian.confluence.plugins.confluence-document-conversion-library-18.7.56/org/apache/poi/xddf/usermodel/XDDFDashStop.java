/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel;

import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDashStop;

public class XDDFDashStop {
    private CTDashStop stop;

    @Internal
    protected XDDFDashStop(CTDashStop stop) {
        this.stop = stop;
    }

    @Internal
    protected CTDashStop getXmlObject() {
        return this.stop;
    }

    public int getDashLength() {
        return POIXMLUnits.parsePercent(this.stop.xgetD());
    }

    public void setDashLength(int length) {
        this.stop.setD(length);
    }

    public int getSpaceLength() {
        return POIXMLUnits.parsePercent(this.stop.xgetSp());
    }

    public void setSpaceLength(int length) {
        this.stop.setSp(length);
    }
}

