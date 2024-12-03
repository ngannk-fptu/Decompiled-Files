/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core.metadata;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.metadata.TableParameterMetaData;
import org.springframework.lang.Nullable;

public interface TableMetaDataProvider {
    public void initializeWithMetaData(DatabaseMetaData var1) throws SQLException;

    public void initializeWithTableColumnMetaData(DatabaseMetaData var1, @Nullable String var2, @Nullable String var3, @Nullable String var4) throws SQLException;

    @Nullable
    public String tableNameToUse(@Nullable String var1);

    @Nullable
    public String catalogNameToUse(@Nullable String var1);

    @Nullable
    public String schemaNameToUse(@Nullable String var1);

    @Nullable
    public String metaDataCatalogNameToUse(@Nullable String var1);

    @Nullable
    public String metaDataSchemaNameToUse(@Nullable String var1);

    public boolean isTableColumnMetaDataUsed();

    public boolean isGetGeneratedKeysSupported();

    public boolean isGetGeneratedKeysSimulated();

    @Nullable
    public String getSimpleQueryForGetGeneratedKey(String var1, String var2);

    public boolean isGeneratedKeysColumnNameArraySupported();

    public List<TableParameterMetaData> getTableParameterMetaData();
}

