/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.types.aggregate;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.template.soy.data.SoyMap;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.types.SoyType;

public class MapType
implements SoyType {
    private final SoyType keyType;
    private final SoyType valueType;

    private MapType(SoyType keyType, SoyType valueType) {
        Preconditions.checkNotNull((Object)keyType);
        Preconditions.checkNotNull((Object)valueType);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public static MapType of(SoyType keyType, SoyType valueType) {
        return new MapType(keyType, valueType);
    }

    @Override
    public SoyType.Kind getKind() {
        return SoyType.Kind.MAP;
    }

    public SoyType getKeyType() {
        return this.keyType;
    }

    public SoyType getValueType() {
        return this.valueType;
    }

    @Override
    public boolean isAssignableFrom(SoyType srcType) {
        if (srcType.getKind() == SoyType.Kind.MAP) {
            MapType srcMapType = (MapType)srcType;
            return this.keyType.isAssignableFrom(srcMapType.keyType) && this.valueType.isAssignableFrom(srcMapType.valueType);
        }
        return false;
    }

    @Override
    public boolean isInstance(SoyValue value) {
        return value instanceof SoyMap;
    }

    public String toString() {
        return "map<" + this.keyType + "," + this.valueType + ">";
    }

    public boolean equals(Object other) {
        if (other != null && other.getClass() == this.getClass()) {
            MapType otherMap = (MapType)other;
            return otherMap.keyType.equals(this.keyType) && otherMap.valueType.equals(this.valueType);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.getClass(), this.keyType, this.valueType});
    }
}

