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
import com.google.template.soy.data.SoyList;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.types.SoyType;

public class ListType
implements SoyType {
    private final SoyType elementType;

    private ListType(SoyType elementType) {
        Preconditions.checkNotNull((Object)elementType);
        this.elementType = elementType;
    }

    public static ListType of(SoyType elementType) {
        return new ListType(elementType);
    }

    @Override
    public SoyType.Kind getKind() {
        return SoyType.Kind.LIST;
    }

    public SoyType getElementType() {
        return this.elementType;
    }

    @Override
    public boolean isAssignableFrom(SoyType srcType) {
        if (srcType.getKind() == SoyType.Kind.LIST) {
            ListType srcListType = (ListType)srcType;
            return this.elementType.isAssignableFrom(srcListType.elementType);
        }
        return false;
    }

    @Override
    public boolean isInstance(SoyValue value) {
        return value instanceof SoyList;
    }

    public String toString() {
        return "list<" + this.elementType + ">";
    }

    public boolean equals(Object other) {
        return other != null && this.getClass() == other.getClass() && ((ListType)other).elementType.equals(this.elementType);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.getClass(), this.elementType});
    }
}

