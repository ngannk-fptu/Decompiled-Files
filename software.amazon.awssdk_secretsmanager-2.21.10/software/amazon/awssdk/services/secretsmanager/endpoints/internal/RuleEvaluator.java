/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.BooleanEqualsFn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Condition;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.EndpointResult;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.EndpointRuleset;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.ExprVisitor;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Fn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnVisitor;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.GetAttr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Identifier;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.IsSet;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.IsValidHostLabel;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.IsVirtualHostableS3Bucket;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Literal;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Not;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.ParseArn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.ParseUrl;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.PartitionFn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Ref;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Rule;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.RuleValueVisitor;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Scope;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.StringEqualsFn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Substring;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.UriEncodeFn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;

@SdkInternalApi
public class RuleEvaluator
implements FnVisitor<Value>,
ExprVisitor<Value> {
    private final Scope<Value> scope = new Scope();

    public Value evaluateRuleset(EndpointRuleset ruleset, Map<Identifier, Value> input) {
        return this.scope.inScope(() -> {
            ruleset.getParameters().toList().forEach(param -> param.getDefault().ifPresent(value -> this.scope.insert(param.getName(), (Value)value)));
            input.forEach(this.scope::insert);
            for (Rule rule : ruleset.getRules()) {
                Value result = this.handleRule(rule);
                if (result.isNone()) continue;
                return result;
            }
            throw new RuntimeException("No rules in ruleset matched");
        });
    }

    @Override
    public Value visitLiteral(Literal literal) {
        return literal.eval(this.scope);
    }

    @Override
    public Value visitRef(Ref ref) {
        return this.scope.getValue(ref.getName()).orElseThrow(() -> new RuntimeException(String.format("Invalid ruleset: %s was not in scope", ref)));
    }

    @Override
    public Value visitFn(Fn fn) {
        return fn.acceptFnVisitor(this);
    }

    @Override
    public Value visitPartition(PartitionFn fn) {
        return fn.eval(this.scope);
    }

    @Override
    public Value visitParseArn(ParseArn fn) {
        return fn.eval(this.scope);
    }

    @Override
    public Value visitIsValidHostLabel(IsValidHostLabel fn) {
        return fn.eval(this.scope);
    }

    @Override
    public Value visitBoolEquals(BooleanEqualsFn fn) {
        return fn.eval(this.scope);
    }

    @Override
    public Value visitStringEquals(StringEqualsFn fn) {
        return fn.eval(this.scope);
    }

    @Override
    public Value visitIsSet(IsSet fn) {
        return fn.eval(this.scope);
    }

    @Override
    public Value visitNot(Not not) {
        return Value.fromBool(!not.target().accept(this).expectBool());
    }

    @Override
    public Value visitGetAttr(GetAttr getAttr) {
        return getAttr.eval(this.scope);
    }

    @Override
    public Value visitParseUrl(ParseUrl parseUrl) {
        return parseUrl.eval(this.scope);
    }

    @Override
    public Value visitSubstring(Substring fn) {
        return fn.eval(this.scope);
    }

    @Override
    public Value visitUriEncode(UriEncodeFn fn) {
        return fn.eval(this.scope);
    }

    @Override
    public Value visitIsVirtualHostLabelsS3Bucket(IsVirtualHostableS3Bucket fn) {
        return fn.eval(this.scope);
    }

    private Value handleRule(Rule rule) {
        final RuleEvaluator self = this;
        return this.scope.inScope(() -> {
            for (Condition condition : rule.getConditions()) {
                Value value = this.evaluateCondition(condition);
                if (!value.isNone() && !value.equals(Value.fromBool(false))) continue;
                return Value.none();
            }
            return rule.accept(new RuleValueVisitor<Value>(){

                @Override
                public Value visitTreeRule(List<Rule> rules) {
                    for (Rule subrule : rules) {
                        Value result = RuleEvaluator.this.handleRule(subrule);
                        if (result.isNone()) continue;
                        return result;
                    }
                    throw new RuntimeException(String.format("no rules inside of tree rule matched\u2014invalid rules (%s)", this));
                }

                @Override
                public Value visitErrorRule(Expr error) {
                    return error.accept(self);
                }

                @Override
                public Value visitEndpointRule(EndpointResult endpoint) {
                    return RuleEvaluator.this.generateEndpoint(endpoint);
                }
            });
        });
    }

    public Value evaluateCondition(Condition condition) {
        Value value = condition.getFn().accept(this);
        if (!value.isNone()) {
            condition.getResult().ifPresent(res -> this.scope.insert((Identifier)res, value));
        }
        return value;
    }

    public Value generateEndpoint(EndpointResult endpoint) {
        Value.Endpoint.Builder builder = Value.Endpoint.builder().url(endpoint.getUrl().accept(this).expectString());
        endpoint.getProperties().forEach((key, value) -> builder.property(key.toString(), value.accept(this)));
        endpoint.getHeaders().forEach((name, exprs) -> exprs.forEach(expr -> builder.addHeader((String)name, expr.accept(this).expectString())));
        return builder.build();
    }
}

