/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.endpoints.internal.Expr;
import software.amazon.awssdk.services.s3.endpoints.internal.ExprVisitor;
import software.amazon.awssdk.services.s3.endpoints.internal.Identifier;
import software.amazon.awssdk.services.s3.endpoints.internal.Scope;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

@SdkInternalApi
public class Ref
extends Expr {
    private final Identifier name;

    public Ref(Identifier name) {
        this.name = name;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitRef(this);
    }

    public Identifier getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Ref ref = (Ref)o;
        return this.name.equals(ref.name);
    }

    @Override
    public String template() {
        return String.format("{%s}", this.name);
    }

    public String toString() {
        return this.name.asString();
    }

    public int hashCode() {
        return this.name != null ? this.name.hashCode() : 0;
    }

    @Override
    public Value eval(Scope<Value> scope) {
        return scope.getValue(this.name).orElse(new Value.None());
    }
}

