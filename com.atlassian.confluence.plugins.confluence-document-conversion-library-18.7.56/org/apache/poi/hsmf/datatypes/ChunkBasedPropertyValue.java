/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import org.apache.poi.hsmf.datatypes.Chunk;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.hsmf.datatypes.PropertyValue;
import org.apache.poi.hsmf.datatypes.Types;

public class ChunkBasedPropertyValue
extends PropertyValue {
    public ChunkBasedPropertyValue(MAPIProperty property, long flags, byte[] offsetData) {
        super(property, flags, offsetData);
    }

    public ChunkBasedPropertyValue(MAPIProperty property, long flags, byte[] offsetData, Types.MAPIType actualType) {
        super(property, flags, offsetData, actualType);
    }

    @Override
    public Chunk getValue() {
        return null;
    }

    public void setValue(Chunk chunk) {
    }
}

