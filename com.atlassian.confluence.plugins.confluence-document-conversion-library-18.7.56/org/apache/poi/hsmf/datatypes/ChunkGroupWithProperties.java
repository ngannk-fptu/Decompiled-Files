/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import java.util.List;
import java.util.Map;
import org.apache.poi.hsmf.datatypes.ChunkGroup;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.hsmf.datatypes.PropertyValue;

public interface ChunkGroupWithProperties
extends ChunkGroup {
    public Map<MAPIProperty, List<PropertyValue>> getProperties();
}

