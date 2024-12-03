/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.content;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VersionRestoreParameters {
    public static final String VERSION_NUMBER = "versionNumber";
    public static final String MESSAGE = "message";
    public static final String RESTORE_TITLE = "restoreTitle";
    private final int versionNumber;
    private final String message;
    private final boolean restoreTitle;

    private VersionRestoreParameters(VersionRestoreParametersBuilder builder) {
        if (builder.versionNumber < 1) {
            throw new BadRequestException("version Number cannot be less than 1. but it is " + builder.versionNumber);
        }
        this.versionNumber = builder.versionNumber;
        this.message = builder.message;
        this.restoreTitle = builder.restoreTitle;
    }

    public static VersionRestoreParametersBuilder builder() {
        return new VersionRestoreParametersBuilder();
    }

    public static VersionRestoreParameters fromMap(Map<String, String> propertyBag) {
        if (!propertyBag.containsKey(VERSION_NUMBER)) {
            throw new BadRequestException("Must have a version number to restore.");
        }
        return VersionRestoreParameters.builder().setVersionNumber(Integer.parseInt(propertyBag.get(VERSION_NUMBER))).setMessage(propertyBag.getOrDefault(MESSAGE, null)).setRestoreTitle(Boolean.parseBoolean(propertyBag.getOrDefault(RESTORE_TITLE, "false"))).build();
    }

    public Map<String, String> toMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(VERSION_NUMBER, String.valueOf(this.versionNumber));
        map.put(MESSAGE, this.message);
        map.put(RESTORE_TITLE, String.valueOf(this.restoreTitle));
        return Collections.unmodifiableMap(map);
    }

    public int getVersionNumber() {
        return this.versionNumber;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean getRestoreTitle() {
        return this.restoreTitle;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        VersionRestoreParameters vrp = (VersionRestoreParameters)o;
        return this.versionNumber == vrp.versionNumber && Objects.equals(this.message, vrp.message) && this.restoreTitle == vrp.restoreTitle;
    }

    public int hashCode() {
        int PRIME = 31;
        int result = this.versionNumber;
        result = 31 * result + (this.message != null ? this.message.hashCode() : 0);
        result = 31 * result + (this.restoreTitle ? 1 : 0);
        return result;
    }

    public static class VersionRestoreParametersBuilder {
        private int versionNumber;
        private String message;
        private boolean restoreTitle;

        public VersionRestoreParametersBuilder setVersionNumber(int versionNumber) {
            this.versionNumber = versionNumber;
            return this;
        }

        public VersionRestoreParametersBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public VersionRestoreParametersBuilder setRestoreTitle(boolean restoreTitle) {
            this.restoreTitle = restoreTitle;
            return this;
        }

        public VersionRestoreParameters build() {
            return new VersionRestoreParameters(this);
        }
    }
}

