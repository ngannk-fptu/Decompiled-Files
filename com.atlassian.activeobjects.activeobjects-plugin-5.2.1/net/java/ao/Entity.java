/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;

public interface Entity
extends RawEntity<Integer> {
    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public int getID();
}

