/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.sts.endpoints.internal.Scope;
import software.amazon.awssdk.services.sts.endpoints.internal.Value;

@SdkInternalApi
public interface Eval {
    public Value eval(Scope<Value> var1);
}

