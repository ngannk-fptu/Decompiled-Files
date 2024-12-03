/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.marketplace.client;

import com.atlassian.marketplace.client.encoding.SchemaViolation;
import com.atlassian.marketplace.client.model.ErrorDetail;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

public class MpacException
extends Exception {
    public MpacException() {
    }

    public MpacException(String message) {
        super(message);
    }

    public MpacException(Throwable cause) {
        super(cause);
    }

    public MpacException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class CannotUpdateNonServerSideEntity
    extends MpacException {
    }

    public static class InvalidResponseError
    extends MpacException {
        private ImmutableList<SchemaViolation> schemaViolations;

        public InvalidResponseError(Iterable<SchemaViolation> schemaViolations) {
            this.schemaViolations = ImmutableList.copyOf(schemaViolations);
        }

        public InvalidResponseError(String message) {
            super(message);
            this.schemaViolations = ImmutableList.of();
        }

        public InvalidResponseError(String message, Throwable cause) {
            super(message, cause);
            this.schemaViolations = ImmutableList.of();
        }

        public Iterable<SchemaViolation> getSchemaViolations() {
            return this.schemaViolations;
        }

        @Override
        public String getMessage() {
            if (super.getMessage() == null) {
                return Joiner.on((String)", ").join(this.schemaViolations);
            }
            return super.getMessage();
        }
    }

    public static class ServerError
    extends MpacException {
        private final int status;
        private final ImmutableList<ErrorDetail> errorDetails;

        public ServerError(int status) {
            super("error " + status);
            this.status = status;
            this.errorDetails = ImmutableList.of();
        }

        public ServerError(int status, String message) {
            super(status + (StringUtils.isBlank((CharSequence)message) ? "" : ": " + message));
            this.status = status;
            this.errorDetails = ImmutableList.of();
        }

        public ServerError(int status, Iterable<ErrorDetail> errorDetails) {
            super(status + ": " + Joiner.on((String)", ").join(errorDetails));
            this.status = status;
            this.errorDetails = ImmutableList.copyOf(errorDetails);
        }

        public int getStatus() {
            return this.status;
        }

        public Iterable<ErrorDetail> getErrorDetails() {
            return this.errorDetails;
        }
    }

    public static class ConnectionFailure
    extends MpacException {
        public ConnectionFailure(Throwable cause) {
            super(cause);
        }
    }
}

