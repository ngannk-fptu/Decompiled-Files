/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Fn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnNode;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnVisitor;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Parameter;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.RuleError;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Scope;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public class BooleanEqualsFn
extends Fn {
    public static final String ID = "booleanEquals";

    public BooleanEqualsFn(FnNode fnNode) {
        super(fnNode);
    }

    @Override
    public <T> T acceptFnVisitor(FnVisitor<T> visitor) {
        return visitor.visitBoolEquals(this);
    }

    public static BooleanEqualsFn ofExprs(Expr left, Expr right) {
        return new BooleanEqualsFn(FnNode.ofExprs(ID, left, right));
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
        return RuleError.ctx("while evaluating booleanEquals", () -> Value.fromBool(((Expr)args.left()).eval(scope).expectBool() == ((Expr)args.right()).eval(scope).expectBool()));
    }

    public static BooleanEqualsFn fromParam(Parameter param, Expr value) {
        return BooleanEqualsFn.ofExprs(param.expr(), value);
    }
}

