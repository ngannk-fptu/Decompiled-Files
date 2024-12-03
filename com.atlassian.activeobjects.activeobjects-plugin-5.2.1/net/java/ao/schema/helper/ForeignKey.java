/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.helper;

public interface ForeignKey {
    public String getLocalTableName();

    public String getLocalFieldName();

    public String getForeignTableName();

    public String getForeignFieldName();
}

