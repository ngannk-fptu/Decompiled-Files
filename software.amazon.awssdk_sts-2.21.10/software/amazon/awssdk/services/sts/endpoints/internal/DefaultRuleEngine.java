/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.sts.endpoints.internal.EndpointRuleset;
import software.amazon.awssdk.services.sts.endpoints.internal.Identifier;
import software.amazon.awssdk.services.sts.endpoints.internal.RuleEngine;
import software.amazon.awssdk.services.sts.endpoints.internal.RuleEvaluator;
import software.amazon.awssdk.services.sts.endpoints.internal.Value;

@SdkInternalApi
public class DefaultRuleEngine
implements RuleEngine {
    private final RuleEvaluator evaluator = new RuleEvaluator();

    @Override
    public Value evaluate(EndpointRuleset ruleset, Map<Identifier, Value> args) {
        return this.evaluator.evaluateRuleset(ruleset, args);
    }
}

