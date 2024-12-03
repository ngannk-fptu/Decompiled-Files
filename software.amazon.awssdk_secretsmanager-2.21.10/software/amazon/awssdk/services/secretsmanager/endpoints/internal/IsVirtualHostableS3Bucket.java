/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
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
public class IsVirtualHostableS3Bucket
extends VarargFn {
    public static final String ID = "aws.isVirtualHostableS3Bucket";

    public IsVirtualHostableS3Bucket(FnNode fnNode) {
        super(fnNode);
    }

    @Override
    public <T> T acceptFnVisitor(FnVisitor<T> visitor) {
        return visitor.visitIsVirtualHostLabelsS3Bucket(this);
    }

    public static IsVirtualHostableS3Bucket ofExprs(Expr expr, boolean allowDots) {
        return new IsVirtualHostableS3Bucket(FnNode.ofExprs(ID, expr, Expr.of(allowDots)));
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
            return Value.fromBool(hostLabel.matches("[a-z\\d][a-z\\d\\-.]{1,61}[a-z\\d]") && !hostLabel.matches("(\\d+\\.){3}\\d+") && !hostLabel.matches(".*[.-]{2}.*"));
        }
        return Value.fromBool(hostLabel.matches("[a-z\\d][a-z\\d\\-]{1,61}[a-z\\d]"));
    }

    private boolean allowDots(Scope<Value> scope) {
        return this.allowDots().eval(scope).expectBool();
    }
}

