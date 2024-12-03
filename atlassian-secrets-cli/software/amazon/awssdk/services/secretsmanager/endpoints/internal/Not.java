/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnNode;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnVisitor;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.SingleArgFn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;

@SdkInternalApi
public class Not
extends SingleArgFn {
    public static final String ID = "not";

    public Not(FnNode fnNode) {
        super(fnNode);
    }

    public static Not ofExpr(Expr expr) {
        return new Not(FnNode.ofExprs(ID, expr));
    }

    @Override
    public <T> T acceptFnVisitor(FnVisitor<T> visitor) {
        return visitor.visitNot(this);
    }

    public static Not ofExprs(Expr expr) {
        return new Not(FnNode.ofExprs(ID, expr));
    }

    @Override
    protected Value evalArg(Value arg) {
        return Value.fromBool(!arg.expectBool());
    }

    @Override
    public Expr target() {
        return this.expectOneArg();
    }
}

