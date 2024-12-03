/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import net.java.ao.RawEntity;

public interface TableNameConverter {
    public String getName(Class<? extends RawEntity<?>> var1);
}

