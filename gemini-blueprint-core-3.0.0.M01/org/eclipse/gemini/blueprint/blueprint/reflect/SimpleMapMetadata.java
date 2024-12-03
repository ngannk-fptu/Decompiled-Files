/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import java.util.List;
import org.osgi.service.blueprint.reflect.MapEntry;
import org.osgi.service.blueprint.reflect.MapMetadata;
import org.springframework.util.StringUtils;

class SimpleMapMetadata
implements MapMetadata {
    private final List<MapEntry> entries;
    private final String keyValueType;
    private final String valueValueType;

    public SimpleMapMetadata(List<MapEntry> entries, String keyTypeName, String valueTypeName) {
        this.entries = entries;
        this.keyValueType = StringUtils.hasText((String)keyTypeName) ? keyTypeName : null;
        this.valueValueType = StringUtils.hasText((String)valueTypeName) ? valueTypeName : null;
    }

    @Override
    public List<MapEntry> getEntries() {
        return this.entries;
    }

    @Override
    public String getKeyType() {
        return this.keyValueType;
    }

    @Override
    public String getValueType() {
        return this.valueValueType;
    }
}

