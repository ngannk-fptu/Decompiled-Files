/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core.simple;

import java.util.Map;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

public interface SimpleJdbcInsertOperations {
    public SimpleJdbcInsertOperations withTableName(String var1);

    public SimpleJdbcInsertOperations withSchemaName(String var1);

    public SimpleJdbcInsertOperations withCatalogName(String var1);

    public SimpleJdbcInsertOperations usingColumns(String ... var1);

    public SimpleJdbcInsertOperations usingGeneratedKeyColumns(String ... var1);

    public SimpleJdbcInsertOperations withoutTableColumnMetaDataAccess();

    public SimpleJdbcInsertOperations includeSynonymsForTableColumnMetaData();

    public int execute(Map<String, ?> var1);

    public int execute(SqlParameterSource var1);

    public Number executeAndReturnKey(Map<String, ?> var1);

    public Number executeAndReturnKey(SqlParameterSource var1);

    public KeyHolder executeAndReturnKeyHolder(Map<String, ?> var1);

    public KeyHolder executeAndReturnKeyHolder(SqlParameterSource var1);

    public int[] executeBatch(Map<String, ?> ... var1);

    public int[] executeBatch(SqlParameterSource ... var1);
}

