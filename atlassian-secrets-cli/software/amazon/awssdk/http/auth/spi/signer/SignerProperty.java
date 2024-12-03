/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.spi.signer;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.utils.Pair;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
@Immutable
@ThreadSafe
public final class SignerProperty<T> {
    private static final ConcurrentMap<Pair<String, String>, SignerProperty<?>> NAME_HISTORY = new ConcurrentHashMap();
    private final String namespace;
    private final String name;

    private SignerProperty(String namespace, String name) {
        Validate.paramNotBlank(namespace, "namespace");
        Validate.paramNotBlank(name, "name");
        this.namespace = namespace;
        this.name = name;
        this.ensureUnique();
    }

    public static <T> SignerProperty<T> create(Class<?> namespace, String name) {
        return new SignerProperty<T>(namespace.getName(), name);
    }

    private void ensureUnique() {
        SignerProperty prev = NAME_HISTORY.putIfAbsent(Pair.of(this.namespace, this.name), this);
        Validate.isTrue(prev == null, "No duplicate SignerProperty names allowed but both SignerProperties %s and %s have the same namespace (%s) and name (%s). SignerProperty should be referenced from a shared static constant to protect against erroneous or unexpected collisions.", Integer.toHexString(System.identityHashCode(prev)), Integer.toHexString(System.identityHashCode(this)), this.namespace, this.name);
    }

    public String toString() {
        return ToString.builder("SignerProperty").add("namespace", this.namespace).add("name", this.name).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SignerProperty that = (SignerProperty)o;
        return Objects.equals(this.namespace, that.namespace) && Objects.equals(this.name, that.name);
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + Objects.hashCode(this.namespace);
        hashCode = 31 * hashCode + Objects.hashCode(this.name);
        return hashCode;
    }
}

