/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.HashMap;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Identifier;

@SdkInternalApi
public final class FatScope<T> {
    private final HashMap<Identifier, T> types;
    private final HashMap<Expr, T> facts;

    public FatScope(HashMap<Identifier, T> types, HashMap<Expr, T> facts) {
        this.types = types;
        this.facts = facts;
    }

    public FatScope() {
        this(new HashMap(), new HashMap());
    }

    public HashMap<Identifier, T> types() {
        return this.types;
    }

    public HashMap<Expr, T> facts() {
        return this.facts;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FatScope fatScope = (FatScope)o;
        if (this.types != null ? !this.types.equals(fatScope.types) : fatScope.types != null) {
            return false;
        }
        return this.facts != null ? this.facts.equals(fatScope.facts) : fatScope.facts == null;
    }

    public int hashCode() {
        int result = this.types != null ? this.types.hashCode() : 0;
        result = 31 * result + (this.facts != null ? this.facts.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "FatScope[types=" + this.types + ", facts=" + this.facts + ']';
    }
}

