/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum ConnectionErrorCodeType {
    INVALID_CREDENTIALS("INVALID_CREDENTIALS"),
    CLUSTER_NOT_FOUND("CLUSTER_NOT_FOUND"),
    NETWORK_ERRORS("NETWORK_ERRORS"),
    INTERNAL_ERROR("INTERNAL_ERROR"),
    INSUFFICIENT_CLOUDHSM_HSMS("INSUFFICIENT_CLOUDHSM_HSMS"),
    USER_LOCKED_OUT("USER_LOCKED_OUT"),
    USER_NOT_FOUND("USER_NOT_FOUND"),
    USER_LOGGED_IN("USER_LOGGED_IN"),
    SUBNET_NOT_FOUND("SUBNET_NOT_FOUND"),
    INSUFFICIENT_FREE_ADDRESSES_IN_SUBNET("INSUFFICIENT_FREE_ADDRESSES_IN_SUBNET");

    private String value;

    private ConnectionErrorCodeType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static ConnectionErrorCodeType fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (ConnectionErrorCodeType enumEntry : ConnectionErrorCodeType.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

