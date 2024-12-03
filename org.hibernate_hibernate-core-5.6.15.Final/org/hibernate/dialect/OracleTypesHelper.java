/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.dialect;

import org.hibernate.HibernateException;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.jboss.logging.Logger;

public class OracleTypesHelper {
    private static final CoreMessageLogger log = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)OracleTypesHelper.class.getName());
    public static final OracleTypesHelper INSTANCE = new OracleTypesHelper();
    private static final String ORACLE_TYPES_CLASS_NAME = "oracle.jdbc.OracleTypes";
    private static final String DEPRECATED_ORACLE_TYPES_CLASS_NAME = "oracle.jdbc.driver.OracleTypes";
    private final int oracleCursorTypeSqlType;

    private OracleTypesHelper() {
        int typeCode = -99;
        try {
            typeCode = this.extractOracleCursorTypeValue();
        }
        catch (Exception e) {
            log.warn("Unable to resolve Oracle CURSOR JDBC type code: the class OracleTypesHelper was initialized but the Oracle JDBC driver could not be loaded.");
        }
        this.oracleCursorTypeSqlType = typeCode;
    }

    private int extractOracleCursorTypeValue() {
        try {
            return this.locateOracleTypesClass().getField("CURSOR").getInt(null);
        }
        catch (Exception se) {
            throw new HibernateException("Unable to access OracleTypes.CURSOR value", se);
        }
    }

    private Class locateOracleTypesClass() {
        try {
            return ReflectHelper.classForName(ORACLE_TYPES_CLASS_NAME);
        }
        catch (ClassNotFoundException e) {
            try {
                return ReflectHelper.classForName(DEPRECATED_ORACLE_TYPES_CLASS_NAME);
            }
            catch (ClassNotFoundException e2) {
                throw new HibernateException(String.format("Unable to locate OracleTypes class using either known FQN [%s, %s]", ORACLE_TYPES_CLASS_NAME, DEPRECATED_ORACLE_TYPES_CLASS_NAME), e);
            }
        }
    }

    public int getOracleCursorTypeSqlType() {
        return this.oracleCursorTypeSqlType;
    }
}

