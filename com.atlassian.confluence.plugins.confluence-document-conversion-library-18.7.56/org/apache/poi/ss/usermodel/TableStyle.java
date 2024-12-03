/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.usermodel.DifferentialStyleProvider;
import org.apache.poi.ss.usermodel.TableStyleType;

public interface TableStyle {
    public String getName();

    public int getIndex();

    public boolean isBuiltin();

    public DifferentialStyleProvider getStyle(TableStyleType var1);
}

