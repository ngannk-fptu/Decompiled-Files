/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.identity.spi;

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
public final class IdentityProperty<T> {
    private static final ConcurrentMap<Pair<String, String>, IdentityProperty<?>> NAME_HISTORY = new ConcurrentHashMap();
    private final String namespace;
    private final String name;

    private IdentityProperty(String namespace, String name) {
        Validate.paramNotBlank(namespace, "namespace");
        Validate.paramNotBlank(name, "name");
        this.namespace = namespace;
        this.name = name;
        this.ensureUnique();
    }

    public static <T> IdentityProperty<T> create(Class<?> namespace, String name) {
        return new IdentityProperty<T>(namespace.getName(), name);
    }

    private void ensureUnique() {
        IdentityProperty prev = NAME_HISTORY.putIfAbsent(Pair.of(this.namespace, this.name), this);
        Validate.isTrue(prev == null, "No duplicate IdentityProperty names allowed but both IdentityProperties %s and %s have the same namespace (%s) and name (%s). IdentityProperty should be referenced from a shared static constant to protect against erroneous or unexpected collisions.", Integer.toHexString(System.identityHashCode(prev)), Integer.toHexString(System.identityHashCode(this)), this.namespace, this.name);
    }

    public String toString() {
        return ToString.builder("IdentityProperty").add("namespace", this.namespace).add("name", this.name).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IdentityProperty that = (IdentityProperty)o;
        return Objects.equals(this.namespace, that.namespace) && Objects.equals(this.name, that.name);
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + Objects.hashCode(this.namespace);
        hashCode = 31 * hashCode + Objects.hashCode(this.name);
        return hashCode;
    }
}

