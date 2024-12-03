/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.support;

import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class CustomSQLErrorCodesTranslation {
    private String[] errorCodes = new String[0];
    @Nullable
    private Class<?> exceptionClass;

    public void setErrorCodes(String ... errorCodes) {
        this.errorCodes = StringUtils.sortStringArray((String[])errorCodes);
    }

    public String[] getErrorCodes() {
        return this.errorCodes;
    }

    public void setExceptionClass(@Nullable Class<?> exceptionClass) {
        if (exceptionClass != null && !DataAccessException.class.isAssignableFrom(exceptionClass)) {
            throw new IllegalArgumentException("Invalid exception class [" + exceptionClass + "]: needs to be a subclass of [org.springframework.dao.DataAccessException]");
        }
        this.exceptionClass = exceptionClass;
    }

    @Nullable
    public Class<?> getExceptionClass() {
        return this.exceptionClass;
    }
}

