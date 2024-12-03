/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.exception;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class CalendarException
extends RuntimeException {
    private String errorMessageKey;
    private List<Object> errorMessageSubstitutionsList;
    private final String message;
    private boolean isCustomError = false;
    private int status;

    public CalendarException(String message) {
        this.message = message;
    }

    public CalendarException(String message, boolean isCustomError) {
        this.message = message;
        this.isCustomError = isCustomError;
    }

    public CalendarException(String errorMessageKey, Object ... errorMessageSubstitutions) {
        this(null, errorMessageKey, errorMessageSubstitutions);
    }

    public CalendarException(Exception cause, String errorMessageKey, Object ... errorMessageSubstitutions) {
        this(errorMessageKey);
        if (null != cause) {
            this.initCause(cause);
        }
        this.errorMessageKey = errorMessageKey;
        this.errorMessageSubstitutionsList = null != errorMessageSubstitutions ? Collections.unmodifiableList(Arrays.asList(errorMessageSubstitutions)) : Collections.emptyList();
    }

    public CalendarException(Exception cause, boolean isCustomError, String errorMessageKey, Object ... errorMessageSubstitutions) {
        this(cause, errorMessageKey, errorMessageSubstitutions);
        this.isCustomError = isCustomError;
    }

    public CalendarException(Exception cause, boolean isCustomError, int status, String errorMessageKey, Object ... errorMessageSubstitutions) {
        this(cause, errorMessageKey, errorMessageSubstitutions);
        this.isCustomError = isCustomError;
        this.status = status;
    }

    public List<?> getErrorMessageSubstitutions() {
        return this.errorMessageSubstitutionsList;
    }

    public String getErrorMessageKey() {
        return StringUtils.defaultString(this.errorMessageKey);
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public boolean isCustomError() {
        return this.isCustomError;
    }

    public int getStatus() {
        return this.status;
    }

    public static enum StatusError {
        JQL_WRONG(1);

        private final int statusNum;

        private StatusError(int statusNum) {
            this.statusNum = statusNum;
        }

        public int getStatusNum() {
            return this.statusNum;
        }
    }
}

