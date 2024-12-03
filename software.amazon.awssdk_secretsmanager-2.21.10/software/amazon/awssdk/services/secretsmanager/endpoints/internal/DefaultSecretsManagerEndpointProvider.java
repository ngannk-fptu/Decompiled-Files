/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.endpoints.Endpoint
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.services.secretsmanager.endpoints.SecretsManagerEndpointParams;
import software.amazon.awssdk.services.secretsmanager.endpoints.SecretsManagerEndpointProvider;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.AwsEndpointProviderUtils;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Condition;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.DefaultRuleEngine;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.EndpointResult;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.EndpointRuleset;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnNode;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Identifier;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Parameter;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.ParameterType;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Parameters;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Rule;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultSecretsManagerEndpointProvider
implements SecretsManagerEndpointProvider {
    private static final EndpointRuleset ENDPOINT_RULE_SET = DefaultSecretsManagerEndpointProvider.ruleSet();

    @Override
    public CompletableFuture<Endpoint> resolveEndpoint(SecretsManagerEndpointParams endpointParams) {
        Validate.notNull((Object)endpointParams.useDualStack(), (String)"Parameter 'UseDualStack' must not be null", (Object[])new Object[0]);
        Validate.notNull((Object)endpointParams.useFips(), (String)"Parameter 'UseFIPS' must not be null", (Object[])new Object[0]);
        Value res = new DefaultRuleEngine().evaluate(ENDPOINT_RULE_SET, DefaultSecretsManagerEndpointProvider.toIdentifierValueMap(endpointParams));
        try {
            return CompletableFuture.completedFuture(AwsEndpointProviderUtils.valueAsEndpointOrThrow(res));
        }
        catch (Exception error) {
            return CompletableFutureUtils.failedFuture((Throwable)error);
        }
    }

    private static Map<Identifier, Value> toIdentifierValueMap(SecretsManagerEndpointParams params) {
        HashMap<Identifier, Value> paramsMap = new HashMap<Identifier, Value>();
        if (params.region() != null) {
            paramsMap.put(Identifier.of("Region"), Value.fromStr(params.region().id()));
        }
        if (params.useDualStack() != null) {
            paramsMap.put(Identifier.of("UseDualStack"), Value.fromBool(params.useDualStack()));
        }
        if (params.useFips() != null) {
            paramsMap.put(Identifier.of("UseFIPS"), Value.fromBool(params.useFips()));
        }
        if (params.endpoint() != null) {
            paramsMap.put(Identifier.of("Endpoint"), Value.fromStr(params.endpoint()));
        }
        return paramsMap;
    }

    private static Rule endpointRule_1() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).error("Invalid Configuration: FIPS and custom endpoint are not supported");
    }

    private static Rule endpointRule_2() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).error("Invalid Configuration: Dualstack and custom endpoint are not supported");
    }

    private static Rule endpointRule_3() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.ref(Identifier.of("Endpoint"))).build());
    }

    private static Rule endpointRule_0() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).treeRule(Arrays.asList(DefaultSecretsManagerEndpointProvider.endpointRule_1(), DefaultSecretsManagerEndpointProvider.endpointRule_2(), DefaultSecretsManagerEndpointProvider.endpointRule_3()));
    }

    private static Rule endpointRule_8() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://secretsmanager-fips.{Region}.{PartitionResult#dualStackDnsSuffix}")).build());
    }

    private static Rule endpointRule_7() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.of(true), FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("PartitionResult")), Expr.of("supportsFIPS"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.of(true), FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("PartitionResult")), Expr.of("supportsDualStack"))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultSecretsManagerEndpointProvider.endpointRule_8()));
    }

    private static Rule endpointRule_9() {
        return Rule.builder().error("FIPS and DualStack are enabled, but this partition does not support one or both");
    }

    private static Rule endpointRule_6() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultSecretsManagerEndpointProvider.endpointRule_7(), DefaultSecretsManagerEndpointProvider.endpointRule_9()));
    }

    private static Rule endpointRule_12() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://secretsmanager-fips.{Region}.{PartitionResult#dnsSuffix}")).build());
    }

    private static Rule endpointRule_11() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("PartitionResult")), Expr.of("supportsFIPS"))).build().validate(), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultSecretsManagerEndpointProvider.endpointRule_12()));
    }

    private static Rule endpointRule_13() {
        return Rule.builder().error("FIPS is enabled but this partition does not support FIPS");
    }

    private static Rule endpointRule_10() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultSecretsManagerEndpointProvider.endpointRule_11(), DefaultSecretsManagerEndpointProvider.endpointRule_13()));
    }

    private static Rule endpointRule_16() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://secretsmanager.{Region}.{PartitionResult#dualStackDnsSuffix}")).build());
    }

    private static Rule endpointRule_15() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.of(true), FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("PartitionResult")), Expr.of("supportsDualStack"))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultSecretsManagerEndpointProvider.endpointRule_16()));
    }

    private static Rule endpointRule_17() {
        return Rule.builder().error("DualStack is enabled but this partition does not support DualStack");
    }

    private static Rule endpointRule_14() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultSecretsManagerEndpointProvider.endpointRule_15(), DefaultSecretsManagerEndpointProvider.endpointRule_17()));
    }

    private static Rule endpointRule_18() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://secretsmanager.{Region}.{PartitionResult#dnsSuffix}")).build());
    }

    private static Rule endpointRule_5() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).result("PartitionResult").build()).treeRule(Arrays.asList(DefaultSecretsManagerEndpointProvider.endpointRule_6(), DefaultSecretsManagerEndpointProvider.endpointRule_10(), DefaultSecretsManagerEndpointProvider.endpointRule_14(), DefaultSecretsManagerEndpointProvider.endpointRule_18()));
    }

    private static Rule endpointRule_4() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).build()).treeRule(Arrays.asList(DefaultSecretsManagerEndpointProvider.endpointRule_5()));
    }

    private static Rule endpointRule_19() {
        return Rule.builder().error("Invalid Configuration: Missing Region");
    }

    private static EndpointRuleset ruleSet() {
        return EndpointRuleset.builder().version("1.0").serviceId(null).parameters(Parameters.builder().addParameter(Parameter.builder().name("Region").type(ParameterType.fromValue("String")).required(false).builtIn("AWS::Region").documentation("The AWS region used to dispatch the request.").build()).addParameter(Parameter.builder().name("UseDualStack").type(ParameterType.fromValue("Boolean")).required(true).builtIn("AWS::UseDualStack").documentation("When true, use the dual-stack endpoint. If the configured endpoint does not support dual-stack, dispatching the request MAY return an error.").defaultValue(Value.fromBool(false)).build()).addParameter(Parameter.builder().name("UseFIPS").type(ParameterType.fromValue("Boolean")).required(true).builtIn("AWS::UseFIPS").documentation("When true, send this request to the FIPS-compliant regional endpoint. If the configured endpoint does not have a FIPS compliant endpoint, dispatching the request will return an error.").defaultValue(Value.fromBool(false)).build()).addParameter(Parameter.builder().name("Endpoint").type(ParameterType.fromValue("String")).required(false).builtIn("SDK::Endpoint").documentation("Override the endpoint used to send this request").build()).build()).addRule(DefaultSecretsManagerEndpointProvider.endpointRule_0()).addRule(DefaultSecretsManagerEndpointProvider.endpointRule_4()).addRule(DefaultSecretsManagerEndpointProvider.endpointRule_19()).build();
    }
}

