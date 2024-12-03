/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.usermodel.BorderFormatting;
import org.apache.poi.ss.usermodel.ExcelNumberFormat;
import org.apache.poi.ss.usermodel.FontFormatting;
import org.apache.poi.ss.usermodel.PatternFormatting;

public interface DifferentialStyleProvider {
    public BorderFormatting getBorderFormatting();

    public FontFormatting getFontFormatting();

    public ExcelNumberFormat getNumberFormat();

    public PatternFormatting getPatternFormatting();

    public int getStripeSize();
}

