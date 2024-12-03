/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.common;

import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.util.LittleEndianOutput;

public interface SharedFeature
extends GenericRecord {
    public String toString();

    public void serialize(LittleEndianOutput var1);

    public int getDataSize();

    public SharedFeature copy();
}

