/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import net.java.ao.RawEntity;

public interface PolymorphicTypeMapper {
    public String convert(Class<? extends RawEntity<?>> var1);

    public Class<? extends RawEntity<?>> invert(Class<? extends RawEntity<?>> var1, String var2);
}

