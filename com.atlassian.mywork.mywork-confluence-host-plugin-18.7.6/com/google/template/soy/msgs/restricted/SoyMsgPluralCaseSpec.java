/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.google.template.soy.msgs.restricted;

import com.google.common.base.Objects;
import com.google.template.soy.msgs.SoyMsgException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Locale;

public class SoyMsgPluralCaseSpec {
    private static final EnumMap<Type, String> TYPE_TO_STRING = new EnumMap(Type.class);
    private final Type type;
    private final int explicitValue;

    public SoyMsgPluralCaseSpec(String typeStr) {
        this.type = Type.valueOf(typeStr.toUpperCase(Locale.ENGLISH));
        this.explicitValue = -1;
    }

    public SoyMsgPluralCaseSpec(int explicitValue) {
        if (explicitValue < 0) {
            throw new SoyMsgException("Negative plural case value.");
        }
        this.type = Type.EXPLICIT;
        this.explicitValue = explicitValue;
    }

    public Type getType() {
        return this.type;
    }

    public int getExplicitValue() {
        return this.explicitValue;
    }

    public String toString() {
        return this.type == Type.EXPLICIT ? "=" + this.explicitValue : TYPE_TO_STRING.get((Object)this.type);
    }

    public boolean equals(Object other) {
        if (!(other instanceof SoyMsgPluralCaseSpec)) {
            return false;
        }
        SoyMsgPluralCaseSpec otherSpec = (SoyMsgPluralCaseSpec)other;
        return this.type == otherSpec.type && this.explicitValue == otherSpec.explicitValue;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{SoyMsgPluralCaseSpec.class, this.type, this.explicitValue});
    }

    static {
        for (Type t : EnumSet.allOf(Type.class)) {
            TYPE_TO_STRING.put(t, t.name().toLowerCase(Locale.ENGLISH));
        }
    }

    public static enum Type {
        EXPLICIT,
        ZERO,
        ONE,
        TWO,
        FEW,
        MANY,
        OTHER;

    }
}

