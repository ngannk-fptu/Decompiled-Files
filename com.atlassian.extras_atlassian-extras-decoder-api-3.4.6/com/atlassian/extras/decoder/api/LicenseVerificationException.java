/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.common.LicenseException
 */
package com.atlassian.extras.decoder.api;

import com.atlassian.extras.common.LicenseException;
import java.util.Properties;

public class LicenseVerificationException
extends LicenseException {
    private static final String VERIFICATION_FAILURE_MSG_BASE = "Failed to verify license hash:";
    public static final String MISSING_PROPERTY_MSG = "Failed to verify license hash: missing property ";
    public static final String ERROR_DURING_VERIFICATION_MSG = "Failed to verify license hash: error during verification";
    public static final String VERIFICATION_FAILED_MSG = "Failed to verify license hash: verification failed";
    private final VerificationFailureReason reason;
    private final String additionalInfo;
    private final Properties properties;

    public LicenseVerificationException(VerificationFailureReason reason, Properties properties) {
        this(reason, null, properties);
    }

    public LicenseVerificationException(VerificationFailureReason reason, String additionalInfo, Properties properties) {
        this(reason, additionalInfo, properties, null);
    }

    public LicenseVerificationException(VerificationFailureReason reason, Properties properties, Throwable cause) {
        this(reason, null, properties, cause);
    }

    public LicenseVerificationException(VerificationFailureReason reason, String additionalInfo, Properties properties, Throwable cause) {
        super(reason.msg + (reason == VerificationFailureReason.MISSING_PROPERTY ? additionalInfo : ""), cause);
        this.reason = reason;
        this.additionalInfo = additionalInfo;
        this.properties = properties;
    }

    public VerificationFailureReason getReason() {
        return this.reason;
    }

    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    public Properties getLicenseProperties() {
        return this.properties;
    }

    public static enum VerificationFailureReason {
        MISSING_PROPERTY("Failed to verify license hash: missing property "),
        ERROR_DURING_VERIFICATION("Failed to verify license hash: error during verification"),
        VERIFICATION_FAILED("Failed to verify license hash: verification failed");

        public final String msg;

        private VerificationFailureReason(String msg) {
            this.msg = msg;
        }
    }
}

