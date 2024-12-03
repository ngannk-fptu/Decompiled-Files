/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Fn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnNode;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnVisitor;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Scope;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public class StringEqualsFn
extends Fn {
    public static final String ID = "stringEquals";

    public StringEqualsFn(FnNode fnNode) {
        super(fnNode);
    }

    @Override
    public <T> T acceptFnVisitor(FnVisitor<T> visitor) {
        return visitor.visitStringEquals(this);
    }

    public static StringEqualsFn ofExprs(Expr expr, Expr of) {
        return new StringEqualsFn(FnNode.ofExprs(ID, expr, of));
    }

    public Expr getLeft() {
        return this.expectTwoArgs().left();
    }

    public Expr getRight() {
        return this.expectTwoArgs().right();
    }

    @Override
    public Value eval(Scope<Value> scope) {
        Pair<Expr, Expr> args = this.expectTwoArgs();
        return Value.fromBool(args.left().eval(scope).expectString().equals(args.right().eval(scope).expectString()));
    }
}

