/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.synchrony.model;

import org.apache.commons.lang3.StringUtils;

public class SynchronyError {
    public static SynchronyError ERROR_CREATING_REQUEST = new SynchronyError(Code.ERROR_CREATING_REQUEST);
    public static SynchronyError CONNECTION_FAILURE = new SynchronyError(Code.CONNECTION_FAILURE);
    private final Code code;
    private final String conflictingRev;

    public SynchronyError(Code code) {
        this(code, "");
    }

    public SynchronyError(Code code, String conflictingRev) {
        this.code = code;
        this.conflictingRev = conflictingRev;
    }

    public Code getCode() {
        return this.code;
    }

    public String getConflictingRev() {
        return this.conflictingRev;
    }

    public static enum Code {
        INVALID_ANCESTOR("invalid-ancestor"),
        OUT_OF_ORDER_REVISION("out-of-order-revision"),
        INTERNAL_SERVER_ERROR("Internal Server Error"),
        JWT_DECRYPTION_FAILED("jwt/decryption-failed"),
        ERROR_CREATING_REQUEST(""),
        CONFIGURATION_ERROR(""),
        CONNECTION_FAILURE(""),
        UNKNOWN_ERROR("");

        private final String synchronyValue;

        private Code(String synchronyValue) {
            this.synchronyValue = synchronyValue;
        }

        public String getSynchronyValue() {
            return this.synchronyValue;
        }

        public static Code from(String synchronyValue) {
            if (StringUtils.isBlank((CharSequence)synchronyValue)) {
                return UNKNOWN_ERROR;
            }
            for (Code code : Code.values()) {
                if (!code.synchronyValue.equals(synchronyValue)) continue;
                return code;
            }
            return UNKNOWN_ERROR;
        }
    }
}

