/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.model;

import org.apache.poi.xssf.usermodel.XSSFColor;

public interface Themes {
    public XSSFColor getThemeColor(int var1);

    public void inheritFromThemeAsRequired(XSSFColor var1);
}

