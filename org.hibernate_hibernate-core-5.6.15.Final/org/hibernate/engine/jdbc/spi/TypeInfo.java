/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.jdbc.spi;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import org.hibernate.engine.jdbc.spi.TypeNullability;
import org.hibernate.engine.jdbc.spi.TypeSearchability;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.jboss.logging.Logger;

public class TypeInfo {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)TypeInfo.class.getName());
    private final String typeName;
    private final int jdbcTypeCode;
    private final String[] createParams;
    private final boolean unsigned;
    private final int precision;
    private final short minimumScale;
    private final short maximumScale;
    private final boolean fixedPrecisionScale;
    private final String literalPrefix;
    private final String literalSuffix;
    private final boolean caseSensitive;
    private final TypeSearchability searchability;
    private final TypeNullability nullability;

    private TypeInfo(String typeName, int jdbcTypeCode, String[] createParams, boolean unsigned, int precision, short minimumScale, short maximumScale, boolean fixedPrecisionScale, String literalPrefix, String literalSuffix, boolean caseSensitive, TypeSearchability searchability, TypeNullability nullability) {
        this.typeName = typeName;
        this.jdbcTypeCode = jdbcTypeCode;
        this.createParams = createParams;
        this.unsigned = unsigned;
        this.precision = precision;
        this.minimumScale = minimumScale;
        this.maximumScale = maximumScale;
        this.fixedPrecisionScale = fixedPrecisionScale;
        this.literalPrefix = literalPrefix;
        this.literalSuffix = literalSuffix;
        this.caseSensitive = caseSensitive;
        this.searchability = searchability;
        this.nullability = nullability;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static LinkedHashSet<TypeInfo> extractTypeInfo(DatabaseMetaData metaData) {
        LinkedHashSet<TypeInfo> typeInfoSet = new LinkedHashSet<TypeInfo>();
        try {
            ResultSet resultSet = metaData.getTypeInfo();
            try {
                while (resultSet.next()) {
                    typeInfoSet.add(new TypeInfo(resultSet.getString("TYPE_NAME"), resultSet.getInt("DATA_TYPE"), TypeInfo.interpretCreateParams(resultSet.getString("CREATE_PARAMS")), resultSet.getBoolean("UNSIGNED_ATTRIBUTE"), resultSet.getInt("PRECISION"), resultSet.getShort("MINIMUM_SCALE"), resultSet.getShort("MAXIMUM_SCALE"), resultSet.getBoolean("FIXED_PREC_SCALE"), resultSet.getString("LITERAL_PREFIX"), resultSet.getString("LITERAL_SUFFIX"), resultSet.getBoolean("CASE_SENSITIVE"), TypeSearchability.interpret(resultSet.getShort("SEARCHABLE")), TypeNullability.interpret(resultSet.getShort("NULLABLE"))));
                }
            }
            catch (SQLException e) {
                LOG.unableToAccessTypeInfoResultSet(e.toString());
            }
            finally {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    LOG.unableToReleaseTypeInfoResultSet();
                }
            }
        }
        catch (SQLException e) {
            LOG.unableToRetrieveTypeInfoResultSet(e.toString());
        }
        return typeInfoSet;
    }

    private static String[] interpretCreateParams(String value) {
        if (value == null || value.length() == 0) {
            return ArrayHelper.EMPTY_STRING_ARRAY;
        }
        return value.split(",");
    }

    public String getTypeName() {
        return this.typeName;
    }

    public int getJdbcTypeCode() {
        return this.jdbcTypeCode;
    }

    public String[] getCreateParams() {
        return this.createParams;
    }

    public boolean isUnsigned() {
        return this.unsigned;
    }

    public int getPrecision() {
        return this.precision;
    }

    public short getMinimumScale() {
        return this.minimumScale;
    }

    public short getMaximumScale() {
        return this.maximumScale;
    }

    public boolean isFixedPrecisionScale() {
        return this.fixedPrecisionScale;
    }

    public String getLiteralPrefix() {
        return this.literalPrefix;
    }

    public String getLiteralSuffix() {
        return this.literalSuffix;
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    public TypeSearchability getSearchability() {
        return this.searchability;
    }

    public TypeNullability getNullability() {
        return this.nullability;
    }
}

