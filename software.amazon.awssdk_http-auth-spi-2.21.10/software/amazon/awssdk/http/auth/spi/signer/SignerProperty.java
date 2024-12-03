/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.utils.Pair
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
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
        Validate.paramNotBlank((CharSequence)namespace, (String)"namespace");
        Validate.paramNotBlank((CharSequence)name, (String)"name");
        this.namespace = namespace;
        this.name = name;
        this.ensureUnique();
    }

    public static <T> SignerProperty<T> create(Class<?> namespace, String name) {
        return new SignerProperty<T>(namespace.getName(), name);
    }

    private void ensureUnique() {
        SignerProperty prev = NAME_HISTORY.putIfAbsent((Pair<String, String>)Pair.of((Object)this.namespace, (Object)this.name), this);
        Validate.isTrue((prev == null ? 1 : 0) != 0, (String)"No duplicate SignerProperty names allowed but both SignerProperties %s and %s have the same namespace (%s) and name (%s). SignerProperty should be referenced from a shared static constant to protect against erroneous or unexpected collisions.", (Object[])new Object[]{Integer.toHexString(System.identityHashCode(prev)), Integer.toHexString(System.identityHashCode(this)), this.namespace, this.name});
    }

    public String toString() {
        return ToString.builder((String)"SignerProperty").add("namespace", (Object)this.namespace).add("name", (Object)this.name).build();
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

