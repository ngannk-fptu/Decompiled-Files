/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Fn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnNode;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Scope;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;

@SdkInternalApi
public abstract class SingleArgFn
extends Fn {
    public SingleArgFn(FnNode fnNode) {
        super(fnNode);
    }

    public Expr target() {
        return this.expectOneArg();
    }

    @Override
    public Value eval(Scope<Value> scope) {
        return this.evalArg(this.expectOneArg().eval(scope));
    }

    protected abstract Value evalArg(Value var1);
}

