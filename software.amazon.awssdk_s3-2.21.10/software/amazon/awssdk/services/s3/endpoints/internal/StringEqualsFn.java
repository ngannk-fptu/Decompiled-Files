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
import software.amazon.awssdk.services.s3.endpoints.internal.Scope;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;
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
        return (Expr)this.expectTwoArgs().left();
    }

    public Expr getRight() {
        return (Expr)this.expectTwoArgs().right();
    }

    @Override
    public Value eval(Scope<Value> scope) {
        Pair<Expr, Expr> args = this.expectTwoArgs();
        return Value.fromBool(((Expr)args.left()).eval(scope).expectString().equals(((Expr)args.right()).eval(scope).expectString()));
    }
}

