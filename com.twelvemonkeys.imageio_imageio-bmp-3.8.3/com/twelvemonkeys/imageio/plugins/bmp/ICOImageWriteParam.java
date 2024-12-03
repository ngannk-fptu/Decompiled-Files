/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

public final class ICOImageWriteParam
extends ImageWriteParam {
    public ICOImageWriteParam(Locale locale) {
        super(locale);
        this.compressionTypes = new String[]{"BI_RGB", "BI_RLE8", "BI_RLE4", "BI_PNG"};
        this.compressionType = this.compressionTypes[0];
        this.canWriteCompressed = true;
    }
}

