/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.endpoints;

import java.util.List;
import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public final class EndpointAttributeKey<T> {
    private final String name;
    private final Class<T> clzz;

    public EndpointAttributeKey(String name, Class<T> clzz) {
        this.name = name;
        this.clzz = clzz;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EndpointAttributeKey that = (EndpointAttributeKey)o;
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        return this.clzz != null ? this.clzz.equals(that.clzz) : that.clzz == null;
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.clzz != null ? this.clzz.hashCode() : 0);
        return result;
    }

    public static <E> EndpointAttributeKey<List<E>> forList(String name) {
        return new EndpointAttributeKey<List<E>>(name, List.class);
    }
}

