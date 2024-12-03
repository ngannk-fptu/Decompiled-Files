/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.vault.auth;

import com.atlassian.secrets.api.SecretStoreException;

public class AuthenticationConfigUtils {
    private AuthenticationConfigUtils() {
    }

    public static String parseRequiredValueFromEnv(String envVariableName, String sysPropName) {
        String result = AuthenticationConfigUtils.parseOptionalValueFromEnv(envVariableName, sysPropName);
        if (result != null) {
            return result;
        }
        throw new SecretStoreException(String.format("Value not found for %s env variable and %s system property", envVariableName, sysPropName));
    }

    public static String parseOptionalValueFromEnv(String envVariableName, String sysPropName) {
        String envValue = System.getenv(envVariableName);
        if (envValue != null) {
            return envValue;
        }
        return System.getProperty(sysPropName);
    }
}

