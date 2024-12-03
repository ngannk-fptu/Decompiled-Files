/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import com.mchange.util.IntEnumeration;

public interface IntChecklist {
    public void check(int var1);

    public void uncheck(int var1);

    public boolean isChecked(int var1);

    public void clear();

    public int countChecked();

    public int[] getChecked();

    public IntEnumeration checked();
}

