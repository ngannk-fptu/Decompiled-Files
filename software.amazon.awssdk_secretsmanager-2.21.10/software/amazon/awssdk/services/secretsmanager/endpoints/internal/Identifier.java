/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class Identifier {
    private String name;

    public Identifier(String name) {
        this.name = name;
    }

    public static Identifier fromString(String name) {
        return new Identifier(name);
    }

    public static Identifier of(String name) {
        return new Identifier(name);
    }

    public String asString() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Identifier that = (Identifier)o;
        return this.name != null ? this.name.equals(that.name) : that.name == null;
    }

    public int hashCode() {
        return this.name != null ? this.name.hashCode() : 0;
    }
}

