/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.CellStyle;

public class CellCopyContext {
    private final Map<CellStyle, CellStyle> styleMap = new HashMap<CellStyle, CellStyle>();

    public CellStyle getMappedStyle(CellStyle srcStyle) {
        return this.styleMap.get(srcStyle);
    }

    public void putMappedStyle(CellStyle srcStyle, CellStyle mappedStyle) {
        this.styleMap.put(srcStyle, mappedStyle);
    }
}

