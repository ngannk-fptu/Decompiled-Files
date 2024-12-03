/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomRect;

public class XDDFGeometryRectangle {
    private CTGeomRect rectangle;

    @Internal
    protected XDDFGeometryRectangle(CTGeomRect rectangle) {
        this.rectangle = rectangle;
    }

    @Internal
    public CTGeomRect getXmlObject() {
        return this.rectangle;
    }
}

