/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.endpoints.internal;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.endpoints.internal.DefaultRuleEngine;
import software.amazon.awssdk.services.s3.endpoints.internal.EndpointRuleset;
import software.amazon.awssdk.services.s3.endpoints.internal.Identifier;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

@SdkInternalApi
public interface RuleEngine {
    public Value evaluate(EndpointRuleset var1, Map<Identifier, Value> var2);

    public static RuleEngine defaultEngine() {
        return new DefaultRuleEngine();
    }
}

