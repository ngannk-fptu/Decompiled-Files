/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class CredentialType {
    public static final CredentialType TOKEN = CredentialType.of("TOKEN");
    private final String value;

    private CredentialType(String value) {
        this.value = value;
    }

    public static CredentialType of(String value) {
        Validate.paramNotNull(value, "value");
        return CredentialTypeCache.put(value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CredentialType that = (CredentialType)o;
        return Objects.equals(this.value, that.value);
    }

    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    public String toString() {
        return ToString.builder("CredentialType{value='" + this.value + '\'' + '}').build();
    }

    private static class CredentialTypeCache {
        private static final ConcurrentHashMap<String, CredentialType> VALUES = new ConcurrentHashMap();

        private CredentialTypeCache() {
        }

        private static CredentialType put(String value) {
            return VALUES.computeIfAbsent(value, v -> new CredentialType(value));
        }
    }
}

