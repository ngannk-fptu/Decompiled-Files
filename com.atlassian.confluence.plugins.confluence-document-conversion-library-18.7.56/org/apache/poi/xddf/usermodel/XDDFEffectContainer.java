/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectContainer;

public class XDDFEffectContainer {
    private CTEffectContainer container;

    @Internal
    public XDDFEffectContainer(CTEffectContainer container) {
        this.container = container;
    }

    @Internal
    public CTEffectContainer getXmlObject() {
        return this.container;
    }
}

