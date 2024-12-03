/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.apache.poi.xddf.usermodel.PresetColor;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetColor;

public class XDDFColorPreset
extends XDDFColor {
    private CTPresetColor color;

    public XDDFColorPreset(PresetColor color) {
        this(CTPresetColor.Factory.newInstance(), CTColor.Factory.newInstance());
        this.setValue(color);
    }

    @Internal
    protected XDDFColorPreset(CTPresetColor color) {
        this(color, null);
    }

    @Internal
    protected XDDFColorPreset(CTPresetColor color, CTColor container) {
        super(container);
        this.color = color;
    }

    @Override
    @Internal
    protected XmlObject getXmlObject() {
        return this.color;
    }

    public PresetColor getValue() {
        if (this.color.xgetVal() != null) {
            return PresetColor.valueOf(this.color.getVal());
        }
        return null;
    }

    public void setValue(PresetColor value) {
        if (value == null) {
            if (this.color.xgetVal() != null) {
                this.color.setVal(PresetColor.WHITE.underlying);
            }
        } else {
            this.color.setVal(value.underlying);
        }
    }
}

