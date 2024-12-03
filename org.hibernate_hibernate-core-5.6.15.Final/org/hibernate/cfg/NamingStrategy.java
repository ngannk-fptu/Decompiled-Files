/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg;

@Deprecated
public interface NamingStrategy {
    public String classToTableName(String var1);

    public String propertyToColumnName(String var1);

    public String tableName(String var1);

    public String columnName(String var1);

    public String collectionTableName(String var1, String var2, String var3, String var4, String var5);

    public String joinKeyColumnName(String var1, String var2);

    public String foreignKeyColumnName(String var1, String var2, String var3, String var4);

    public String logicalColumnName(String var1, String var2);

    public String logicalCollectionTableName(String var1, String var2, String var3, String var4);

    public String logicalCollectionColumnName(String var1, String var2, String var3);
}

