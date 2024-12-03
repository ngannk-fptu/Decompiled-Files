/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core.simple;

import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public interface SimpleJdbcCallOperations {
    public SimpleJdbcCallOperations withProcedureName(String var1);

    public SimpleJdbcCallOperations withFunctionName(String var1);

    public SimpleJdbcCallOperations withSchemaName(String var1);

    public SimpleJdbcCallOperations withCatalogName(String var1);

    public SimpleJdbcCallOperations withReturnValue();

    public SimpleJdbcCallOperations declareParameters(SqlParameter ... var1);

    public SimpleJdbcCallOperations useInParameterNames(String ... var1);

    public SimpleJdbcCallOperations returningResultSet(String var1, RowMapper<?> var2);

    public SimpleJdbcCallOperations withoutProcedureColumnMetaDataAccess();

    public SimpleJdbcCallOperations withNamedBinding();

    public <T> T executeFunction(Class<T> var1, Object ... var2);

    public <T> T executeFunction(Class<T> var1, Map<String, ?> var2);

    public <T> T executeFunction(Class<T> var1, SqlParameterSource var2);

    public <T> T executeObject(Class<T> var1, Object ... var2);

    public <T> T executeObject(Class<T> var1, Map<String, ?> var2);

    public <T> T executeObject(Class<T> var1, SqlParameterSource var2);

    public Map<String, Object> execute(Object ... var1);

    public Map<String, Object> execute(Map<String, ?> var1);

    public Map<String, Object> execute(SqlParameterSource var1);
}

