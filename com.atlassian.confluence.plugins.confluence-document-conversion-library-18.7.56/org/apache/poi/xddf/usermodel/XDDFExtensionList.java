/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;

public class XDDFExtensionList {
    private CTOfficeArtExtensionList list;

    @Internal
    public XDDFExtensionList(CTOfficeArtExtensionList list) {
        this.list = list;
    }

    @Internal
    public CTOfficeArtExtensionList getXmlObject() {
        return this.list;
    }
}

