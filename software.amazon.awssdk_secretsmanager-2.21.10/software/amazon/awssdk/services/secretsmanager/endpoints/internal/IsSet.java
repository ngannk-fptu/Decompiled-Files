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
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.SingleArgFn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;

@SdkInternalApi
public class IsSet
extends SingleArgFn {
    public static final String ID = "isSet";

    public IsSet(FnNode fnNode) {
        super(fnNode);
    }

    @Override
    public <T> T acceptFnVisitor(FnVisitor<T> visitor) {
        return visitor.visitIsSet(this);
    }

    public static IsSet ofExpr(Expr expr) {
        return new IsSet(FnNode.ofExprs(ID, expr));
    }

    @Override
    protected Value evalArg(Value arg) {
        return Value.fromBool(!arg.isNone());
    }
}

