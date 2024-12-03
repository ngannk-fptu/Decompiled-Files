/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLatentStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLsdException;

public class XWPFLatentStyles {
    protected XWPFStyles styles;
    private CTLatentStyles latentStyles;

    protected XWPFLatentStyles() {
    }

    protected XWPFLatentStyles(CTLatentStyles latentStyles) {
        this(latentStyles, null);
    }

    protected XWPFLatentStyles(CTLatentStyles latentStyles, XWPFStyles styles) {
        this.latentStyles = latentStyles;
        this.styles = styles;
    }

    public int getNumberOfStyles() {
        return this.latentStyles.sizeOfLsdExceptionArray();
    }

    public boolean isLatentStyle(String latentStyleName) {
        for (CTLsdException lsd : this.latentStyles.getLsdExceptionArray()) {
            if (!lsd.getName().equals(latentStyleName)) continue;
            return true;
        }
        return false;
    }
}

