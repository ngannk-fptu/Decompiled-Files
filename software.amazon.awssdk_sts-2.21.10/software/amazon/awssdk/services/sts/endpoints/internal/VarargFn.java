/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.sts.endpoints.internal.Expr;
import software.amazon.awssdk.services.sts.endpoints.internal.Fn;
import software.amazon.awssdk.services.sts.endpoints.internal.FnNode;
import software.amazon.awssdk.services.sts.endpoints.internal.Scope;
import software.amazon.awssdk.services.sts.endpoints.internal.Value;

@SdkInternalApi
abstract class VarargFn
extends Fn {
    VarargFn(FnNode fnNode) {
        super(fnNode);
    }

    @Override
    public abstract Value eval(Scope<Value> var1);

    protected List<Expr> args() {
        return this.fnNode.getArgv();
    }
}

