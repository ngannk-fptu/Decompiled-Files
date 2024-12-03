/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal;

import com.atlassian.upm.api.util.Option;
import java.util.Objects;

public class PluginLicenseError {
    final Type type;
    final Option<Throwable> cause;

    public PluginLicenseError(Type type) {
        this(type, null);
    }

    public PluginLicenseError(Type type, Throwable cause) {
        this.type = Objects.requireNonNull(type, "type");
        this.cause = Option.option(cause);
    }

    public Type getType() {
        return this.type;
    }

    public Option<Throwable> getCause() {
        return this.cause;
    }

    public static enum Type {
        SETTING_EMPTY_LICENSE(400, "setting.empty.license"),
        INVALID_LICENSE_ERROR(400, "invalid.license"),
        UNKNOWN_VALIDATION_ERROR(400, "validation");

        private final int statusCode;
        private final String subCode;

        private Type(int statusCode, String subCode) {
            this.statusCode = statusCode;
            this.subCode = "upm.plugin.license.error." + subCode;
        }

        public int getStatusCode() {
            return this.statusCode;
        }

        public String getSubCode() {
            return this.subCode;
        }
    }
}

