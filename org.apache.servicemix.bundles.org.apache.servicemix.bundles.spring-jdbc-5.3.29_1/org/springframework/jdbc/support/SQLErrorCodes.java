/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.support;

import org.springframework.jdbc.support.CustomSQLErrorCodesTranslation;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class SQLErrorCodes {
    @Nullable
    private String[] databaseProductNames;
    private boolean useSqlStateForTranslation = false;
    private String[] badSqlGrammarCodes = new String[0];
    private String[] invalidResultSetAccessCodes = new String[0];
    private String[] duplicateKeyCodes = new String[0];
    private String[] dataIntegrityViolationCodes = new String[0];
    private String[] permissionDeniedCodes = new String[0];
    private String[] dataAccessResourceFailureCodes = new String[0];
    private String[] transientDataAccessResourceCodes = new String[0];
    private String[] cannotAcquireLockCodes = new String[0];
    private String[] deadlockLoserCodes = new String[0];
    private String[] cannotSerializeTransactionCodes = new String[0];
    @Nullable
    private CustomSQLErrorCodesTranslation[] customTranslations;
    @Nullable
    private SQLExceptionTranslator customSqlExceptionTranslator;

    public void setDatabaseProductName(@Nullable String databaseProductName) {
        this.databaseProductNames = new String[]{databaseProductName};
    }

    @Nullable
    public String getDatabaseProductName() {
        return this.databaseProductNames != null && this.databaseProductNames.length > 0 ? this.databaseProductNames[0] : null;
    }

    public void setDatabaseProductNames(String ... databaseProductNames) {
        this.databaseProductNames = databaseProductNames;
    }

    @Nullable
    public String[] getDatabaseProductNames() {
        return this.databaseProductNames;
    }

    public void setUseSqlStateForTranslation(boolean useStateCodeForTranslation) {
        this.useSqlStateForTranslation = useStateCodeForTranslation;
    }

    public boolean isUseSqlStateForTranslation() {
        return this.useSqlStateForTranslation;
    }

    public void setBadSqlGrammarCodes(String ... badSqlGrammarCodes) {
        this.badSqlGrammarCodes = StringUtils.sortStringArray((String[])badSqlGrammarCodes);
    }

    public String[] getBadSqlGrammarCodes() {
        return this.badSqlGrammarCodes;
    }

    public void setInvalidResultSetAccessCodes(String ... invalidResultSetAccessCodes) {
        this.invalidResultSetAccessCodes = StringUtils.sortStringArray((String[])invalidResultSetAccessCodes);
    }

    public String[] getInvalidResultSetAccessCodes() {
        return this.invalidResultSetAccessCodes;
    }

    public String[] getDuplicateKeyCodes() {
        return this.duplicateKeyCodes;
    }

    public void setDuplicateKeyCodes(String ... duplicateKeyCodes) {
        this.duplicateKeyCodes = duplicateKeyCodes;
    }

    public void setDataIntegrityViolationCodes(String ... dataIntegrityViolationCodes) {
        this.dataIntegrityViolationCodes = StringUtils.sortStringArray((String[])dataIntegrityViolationCodes);
    }

    public String[] getDataIntegrityViolationCodes() {
        return this.dataIntegrityViolationCodes;
    }

    public void setPermissionDeniedCodes(String ... permissionDeniedCodes) {
        this.permissionDeniedCodes = StringUtils.sortStringArray((String[])permissionDeniedCodes);
    }

    public String[] getPermissionDeniedCodes() {
        return this.permissionDeniedCodes;
    }

    public void setDataAccessResourceFailureCodes(String ... dataAccessResourceFailureCodes) {
        this.dataAccessResourceFailureCodes = StringUtils.sortStringArray((String[])dataAccessResourceFailureCodes);
    }

    public String[] getDataAccessResourceFailureCodes() {
        return this.dataAccessResourceFailureCodes;
    }

    public void setTransientDataAccessResourceCodes(String ... transientDataAccessResourceCodes) {
        this.transientDataAccessResourceCodes = StringUtils.sortStringArray((String[])transientDataAccessResourceCodes);
    }

    public String[] getTransientDataAccessResourceCodes() {
        return this.transientDataAccessResourceCodes;
    }

    public void setCannotAcquireLockCodes(String ... cannotAcquireLockCodes) {
        this.cannotAcquireLockCodes = StringUtils.sortStringArray((String[])cannotAcquireLockCodes);
    }

    public String[] getCannotAcquireLockCodes() {
        return this.cannotAcquireLockCodes;
    }

    public void setDeadlockLoserCodes(String ... deadlockLoserCodes) {
        this.deadlockLoserCodes = StringUtils.sortStringArray((String[])deadlockLoserCodes);
    }

    public String[] getDeadlockLoserCodes() {
        return this.deadlockLoserCodes;
    }

    public void setCannotSerializeTransactionCodes(String ... cannotSerializeTransactionCodes) {
        this.cannotSerializeTransactionCodes = StringUtils.sortStringArray((String[])cannotSerializeTransactionCodes);
    }

    public String[] getCannotSerializeTransactionCodes() {
        return this.cannotSerializeTransactionCodes;
    }

    public void setCustomTranslations(CustomSQLErrorCodesTranslation ... customTranslations) {
        this.customTranslations = customTranslations;
    }

    @Nullable
    public CustomSQLErrorCodesTranslation[] getCustomTranslations() {
        return this.customTranslations;
    }

    public void setCustomSqlExceptionTranslatorClass(@Nullable Class<? extends SQLExceptionTranslator> customTranslatorClass) {
        if (customTranslatorClass != null) {
            try {
                this.customSqlExceptionTranslator = (SQLExceptionTranslator)ReflectionUtils.accessibleConstructor(customTranslatorClass, (Class[])new Class[0]).newInstance(new Object[0]);
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Unable to instantiate custom translator", ex);
            }
        } else {
            this.customSqlExceptionTranslator = null;
        }
    }

    public void setCustomSqlExceptionTranslator(@Nullable SQLExceptionTranslator customSqlExceptionTranslator) {
        this.customSqlExceptionTranslator = customSqlExceptionTranslator;
    }

    @Nullable
    public SQLExceptionTranslator getCustomSqlExceptionTranslator() {
        return this.customSqlExceptionTranslator;
    }
}

