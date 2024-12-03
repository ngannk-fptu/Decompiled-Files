/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableSortedMap
 */
package com.google.template.soy.types.aggregate;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSortedMap;
import com.google.template.soy.base.SoyBackendKind;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.types.SoyObjectType;
import com.google.template.soy.types.SoyType;
import java.util.Map;

public final class RecordType
implements SoyObjectType {
    private final ImmutableSortedMap<String, SoyType> members;

    private RecordType(Map<String, SoyType> members) {
        this.members = ImmutableSortedMap.copyOf(members);
    }

    public static RecordType of(Map<String, SoyType> members) {
        return new RecordType(members);
    }

    @Override
    public SoyType.Kind getKind() {
        return SoyType.Kind.RECORD;
    }

    @Override
    public boolean isAssignableFrom(SoyType srcType) {
        if (srcType.getKind() == SoyType.Kind.RECORD) {
            RecordType srcRecord = (RecordType)srcType;
            for (Map.Entry entry : this.members.entrySet()) {
                SoyType fieldType = (SoyType)srcRecord.members.get(entry.getKey());
                if (fieldType != null && ((SoyType)entry.getValue()).isAssignableFrom(fieldType)) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isInstance(SoyValue value) {
        return value instanceof SoyRecord;
    }

    @Override
    public String getName() {
        return "Record";
    }

    public ImmutableSortedMap<String, SoyType> getMembers() {
        return this.members;
    }

    @Override
    public String getNameForBackend(SoyBackendKind backend) {
        return "Object";
    }

    @Override
    public SoyType getFieldType(String fieldName) {
        return (SoyType)this.members.get((Object)fieldName);
    }

    @Override
    public String getFieldAccessor(String fieldName, SoyBackendKind backendKind) {
        if (backendKind == SoyBackendKind.JS_SRC) {
            return "." + fieldName;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFieldImport(String fieldName, SoyBackendKind backend) {
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (Map.Entry entry : this.members.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append((String)entry.getKey());
            sb.append(": ");
            sb.append(((SoyType)entry.getValue()).toString());
        }
        sb.append("]");
        return sb.toString();
    }

    public boolean equals(Object other) {
        return other != null && other.getClass() == this.getClass() && ((RecordType)other).members.equals(this.members);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.getClass(), this.members});
    }
}

