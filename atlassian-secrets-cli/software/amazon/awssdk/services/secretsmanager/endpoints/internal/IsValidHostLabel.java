/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnNode;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnVisitor;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Scope;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.VarargFn;

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
        return this.expectTwoArgs().left();
    }

    public Expr allowDots() {
        return this.expectTwoArgs().right();
    }

    @Override
    public Value eval(Scope<Value> scope) {
        String hostLabel = this.expectTwoArgs().left().eval(scope).expectString();
        if (this.allowDots(scope)) {
            return Value.fromBool(hostLabel.matches("[a-zA-Z\\d][a-zA-Z\\d\\-.]{0,62}"));
        }
        return Value.fromBool(hostLabel.matches("[a-zA-Z\\d][a-zA-Z\\d\\-]{0,62}"));
    }

    private boolean allowDots(Scope<Value> scope) {
        return this.allowDots().eval(scope).expectBool();
    }
}

