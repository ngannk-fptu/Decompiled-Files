/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectionSite;

public class XDDFConnectionSite {
    private CTConnectionSite site;

    @Internal
    protected XDDFConnectionSite(CTConnectionSite site) {
        this.site = site;
    }

    @Internal
    public CTConnectionSite getXmlObject() {
        return this.site;
    }
}

