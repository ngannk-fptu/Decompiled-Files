/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.impl;

public class TrustedDomainsCsvZipException
extends RuntimeException {
    public final Type type;

    public TrustedDomainsCsvZipException(Type type, Throwable cause) {
        super(type.message, cause);
        this.type = type;
    }

    public static enum Type {
        INVALID_CLOUD_ID("Cloud ID not recognized"),
        COULD_NOT_CREATE_CSV("Could not create CSV file"),
        COULD_NOT_WRITE_CSV("Could not write CSV file"),
        COULD_NOT_WRITE_ZIP("Could not write ZIP");

        public final String message;

        private Type(String message) {
            this.message = message;
        }
    }
}

