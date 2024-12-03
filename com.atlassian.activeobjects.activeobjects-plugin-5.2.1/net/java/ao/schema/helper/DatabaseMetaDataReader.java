/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.helper;

import java.sql.DatabaseMetaData;
import net.java.ao.RawEntity;
import net.java.ao.schema.helper.Field;
import net.java.ao.schema.helper.ForeignKey;
import net.java.ao.schema.helper.Index;

public interface DatabaseMetaDataReader {
    public boolean isTablePresent(DatabaseMetaData var1, Class<? extends RawEntity<?>> var2);

    public Iterable<String> getTableNames(DatabaseMetaData var1);

    public Iterable<? extends Field> getFields(DatabaseMetaData var1, String var2);

    public Iterable<? extends ForeignKey> getForeignKeys(DatabaseMetaData var1, String var2);

    public Iterable<? extends Index> getIndexes(DatabaseMetaData var1, String var2);
}

