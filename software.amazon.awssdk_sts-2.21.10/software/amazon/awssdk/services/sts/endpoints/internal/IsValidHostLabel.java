/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.sts.endpoints.internal.Expr;
import software.amazon.awssdk.services.sts.endpoints.internal.FnNode;
import software.amazon.awssdk.services.sts.endpoints.internal.FnVisitor;
import software.amazon.awssdk.services.sts.endpoints.internal.Scope;
import software.amazon.awssdk.services.sts.endpoints.internal.Value;
import software.amazon.awssdk.services.sts.endpoints.internal.VarargFn;

@SdkInternalApi
public class IsValidHostLabel
extends VarargFn {
    public static final String ID = "isValidHostLabel";

    public IsValidHostLabel(FnNode fnNode) {
        super(fnNode);
    }

    @Override
    public <T> T acceptFnVisitor(FnVisitor<T> visitor) {
        return visitor.visitIsValidHostLabel(this);
    }

    public static IsValidHostLabel ofExprs(Expr expr, boolean allowDots) {
        return new IsValidHostLabel(FnNode.ofExprs(ID, expr, Expr.of(allowDots)));
    }

    public Expr hostLabel() {
        return (Expr)this.expectTwoArgs().left();
    }

    public Expr allowDots() {
        return (Expr)this.expectTwoArgs().right();
    }

    @Override
    public Value eval(Scope<Value> scope) {
        String hostLabel = ((Expr)this.expectTwoArgs().left()).eval(scope).expectString();
        if (this.allowDots(scope)) {
            return Value.fromBool(hostLabel.matches("[a-zA-Z\\d][a-zA-Z\\d\\-.]{0,62}"));
        }
        return Value.fromBool(hostLabel.matches("[a-zA-Z\\d][a-zA-Z\\d\\-]{0,62}"));
    }

    private boolean allowDots(Scope<Value> scope) {
        return this.allowDots().eval(scope).expectBool();
    }
}

