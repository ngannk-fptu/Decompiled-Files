/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.EndpointRuleset;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Identifier;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.RuleEngine;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.RuleEvaluator;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;

@SdkInternalApi
public class DefaultRuleEngine
implements RuleEngine {
    private final RuleEvaluator evaluator = new RuleEvaluator();

    @Override
    public Value evaluate(EndpointRuleset ruleset, Map<Identifier, Value> args) {
        return this.evaluator.evaluateRuleset(ruleset, args);
    }
}

