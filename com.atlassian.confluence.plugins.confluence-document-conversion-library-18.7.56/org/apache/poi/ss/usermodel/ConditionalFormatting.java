/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.util.CellRangeAddress;

public interface ConditionalFormatting {
    public CellRangeAddress[] getFormattingRanges();

    public void setFormattingRanges(CellRangeAddress[] var1);

    public void setRule(int var1, ConditionalFormattingRule var2);

    public void addRule(ConditionalFormattingRule var1);

    public ConditionalFormattingRule getRule(int var1);

    public int getNumberOfRules();
}

