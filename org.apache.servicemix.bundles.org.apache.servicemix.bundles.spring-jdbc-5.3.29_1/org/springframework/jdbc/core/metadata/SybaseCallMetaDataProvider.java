/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core.metadata;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.springframework.jdbc.core.metadata.GenericCallMetaDataProvider;
import org.springframework.lang.Nullable;

public class SybaseCallMetaDataProvider
extends GenericCallMetaDataProvider {
    private static final String REMOVABLE_COLUMN_PREFIX = "@";
    private static final String RETURN_VALUE_NAME = "RETURN_VALUE";

    public SybaseCallMetaDataProvider(DatabaseMetaData databaseMetaData) throws SQLException {
        super(databaseMetaData);
    }

    @Override
    @Nullable
    public String parameterNameToUse(@Nullable String parameterName) {
        if (parameterName == null) {
            return null;
        }
        if (parameterName.length() > 1 && parameterName.startsWith(REMOVABLE_COLUMN_PREFIX)) {
            return super.parameterNameToUse(parameterName.substring(1));
        }
        return super.parameterNameToUse(parameterName);
    }

    @Override
    public boolean byPassReturnParameter(String parameterName) {
        return RETURN_VALUE_NAME.equals(parameterName) || RETURN_VALUE_NAME.equals(this.parameterNameToUse(parameterName));
    }
}

