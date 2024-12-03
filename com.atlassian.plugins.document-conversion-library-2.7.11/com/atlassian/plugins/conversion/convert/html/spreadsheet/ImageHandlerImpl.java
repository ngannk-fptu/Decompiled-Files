/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.convert.html.spreadsheet;

import com.atlassian.plugins.conversion.convert.html.HtmlConversionData;
import com.atlassian.plugins.conversion.convert.html.spreadsheet.ImageHandler;

public class ImageHandlerImpl
implements ImageHandler {
    HtmlConversionData _data;
    String _path;

    public ImageHandlerImpl(HtmlConversionData data, String path) {
        this._data = data;
        this._path = path;
    }

    @Override
    public String handleImage(byte[] buf, String fileName) throws Exception {
        this._data.addImage(fileName, buf);
        return this._path + "&val=" + fileName;
    }
}

