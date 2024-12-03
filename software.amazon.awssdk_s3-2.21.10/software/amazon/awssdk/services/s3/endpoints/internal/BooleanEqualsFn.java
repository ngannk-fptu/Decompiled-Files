/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.services.s3.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.endpoints.internal.Expr;
import software.amazon.awssdk.services.s3.endpoints.internal.Fn;
import software.amazon.awssdk.services.s3.endpoints.internal.FnNode;
import software.amazon.awssdk.services.s3.endpoints.internal.FnVisitor;
import software.amazon.awssdk.services.s3.endpoints.internal.Parameter;
import software.amazon.awssdk.services.s3.endpoints.internal.RuleError;
import software.amazon.awssdk.services.s3.endpoints.internal.Scope;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;
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
        return (Expr)this.expectTwoArgs().left();
    }

    public Expr getRight() {
        return (Expr)this.expectTwoArgs().right();
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

