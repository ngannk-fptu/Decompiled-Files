/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.usermodel.TableStyle;

public interface TableStyleInfo {
    public boolean isShowColumnStripes();

    public boolean isShowRowStripes();

    public boolean isShowFirstColumn();

    public boolean isShowLastColumn();

    public String getName();

    public TableStyle getStyle();
}

