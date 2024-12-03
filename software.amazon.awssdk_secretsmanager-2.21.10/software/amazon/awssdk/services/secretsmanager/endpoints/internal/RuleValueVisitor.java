/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.EndpointResult;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Rule;

@SdkInternalApi
public interface RuleValueVisitor<R> {
    public R visitTreeRule(List<Rule> var1);

    public R visitErrorRule(Expr var1);

    public R visitEndpointRule(EndpointResult var1);
}

