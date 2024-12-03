/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ddf;

import org.apache.poi.ddf.EscherRecord;

public interface EscherSerializationListener {
    public void beforeRecordSerialize(int var1, short var2, EscherRecord var3);

    public void afterRecordSerialize(int var1, short var2, int var3, EscherRecord var4);
}

