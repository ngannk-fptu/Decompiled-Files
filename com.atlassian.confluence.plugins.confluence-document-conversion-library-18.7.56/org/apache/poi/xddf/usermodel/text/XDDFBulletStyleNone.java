/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel.text;

import org.apache.poi.util.Internal;
import org.apache.poi.xddf.usermodel.text.XDDFBulletStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNoBullet;

public class XDDFBulletStyleNone
implements XDDFBulletStyle {
    private CTTextNoBullet style;

    @Internal
    protected XDDFBulletStyleNone(CTTextNoBullet style) {
        this.style = style;
    }

    @Internal
    protected CTTextNoBullet getXmlObject() {
        return this.style;
    }
}

