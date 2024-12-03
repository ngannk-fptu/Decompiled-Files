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
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.metadata.CallParameterMetaData;
import org.springframework.lang.Nullable;

public interface CallMetaDataProvider {
    public void initializeWithMetaData(DatabaseMetaData var1) throws SQLException;

    public void initializeWithProcedureColumnMetaData(DatabaseMetaData var1, @Nullable String var2, @Nullable String var3, @Nullable String var4) throws SQLException;

    @Nullable
    public String procedureNameToUse(@Nullable String var1);

    @Nullable
    public String catalogNameToUse(@Nullable String var1);

    @Nullable
    public String schemaNameToUse(@Nullable String var1);

    @Nullable
    public String metaDataCatalogNameToUse(@Nullable String var1);

    @Nullable
    public String metaDataSchemaNameToUse(@Nullable String var1);

    @Nullable
    public String parameterNameToUse(@Nullable String var1);

    public SqlParameter createDefaultOutParameter(String var1, CallParameterMetaData var2);

    public SqlParameter createDefaultInOutParameter(String var1, CallParameterMetaData var2);

    public SqlParameter createDefaultInParameter(String var1, CallParameterMetaData var2);

    @Nullable
    public String getUserName();

    public boolean isReturnResultSetSupported();

    public boolean isRefCursorSupported();

    public int getRefCursorSqlType();

    public boolean isProcedureColumnMetaDataUsed();

    public boolean byPassReturnParameter(String var1);

    public List<CallParameterMetaData> getCallParameterMetaData();

    public boolean isSupportsCatalogsInProcedureCalls();

    public boolean isSupportsSchemasInProcedureCalls();
}

