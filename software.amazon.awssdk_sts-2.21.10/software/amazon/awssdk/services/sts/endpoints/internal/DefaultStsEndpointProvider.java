/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.endpoints.Endpoint
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 *  software.amazon.awssdk.utils.MapUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.services.sts.endpoints.StsEndpointParams;
import software.amazon.awssdk.services.sts.endpoints.StsEndpointProvider;
import software.amazon.awssdk.services.sts.endpoints.internal.AwsEndpointProviderUtils;
import software.amazon.awssdk.services.sts.endpoints.internal.Condition;
import software.amazon.awssdk.services.sts.endpoints.internal.DefaultRuleEngine;
import software.amazon.awssdk.services.sts.endpoints.internal.EndpointResult;
import software.amazon.awssdk.services.sts.endpoints.internal.EndpointRuleset;
import software.amazon.awssdk.services.sts.endpoints.internal.Expr;
import software.amazon.awssdk.services.sts.endpoints.internal.FnNode;
import software.amazon.awssdk.services.sts.endpoints.internal.Identifier;
import software.amazon.awssdk.services.sts.endpoints.internal.Literal;
import software.amazon.awssdk.services.sts.endpoints.internal.Parameter;
import software.amazon.awssdk.services.sts.endpoints.internal.ParameterType;
import software.amazon.awssdk.services.sts.endpoints.internal.Parameters;
import software.amazon.awssdk.services.sts.endpoints.internal.Rule;
import software.amazon.awssdk.services.sts.endpoints.internal.Value;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.MapUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultStsEndpointProvider
implements StsEndpointProvider {
    private static final EndpointRuleset ENDPOINT_RULE_SET = DefaultStsEndpointProvider.ruleSet();

    @Override
    public CompletableFuture<Endpoint> resolveEndpoint(StsEndpointParams endpointParams) {
        Validate.notNull((Object)endpointParams.useDualStack(), (String)"Parameter 'UseDualStack' must not be null", (Object[])new Object[0]);
        Validate.notNull((Object)endpointParams.useFips(), (String)"Parameter 'UseFIPS' must not be null", (Object[])new Object[0]);
        Validate.notNull((Object)endpointParams.useGlobalEndpoint(), (String)"Parameter 'UseGlobalEndpoint' must not be null", (Object[])new Object[0]);
        Value res = new DefaultRuleEngine().evaluate(ENDPOINT_RULE_SET, DefaultStsEndpointProvider.toIdentifierValueMap(endpointParams));
        try {
            return CompletableFuture.completedFuture(AwsEndpointProviderUtils.valueAsEndpointOrThrow(res));
        }
        catch (Exception error) {
            return CompletableFutureUtils.failedFuture((Throwable)error);
        }
    }

    private static Map<Identifier, Value> toIdentifierValueMap(StsEndpointParams params) {
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
        if (params.useGlobalEndpoint() != null) {
            paramsMap.put(Identifier.of("UseGlobalEndpoint"), Value.fromBool(params.useGlobalEndpoint()));
        }
        return paramsMap;
    }

    private static Rule endpointRule_1() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("ap-northeast-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_2() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("ap-south-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_3() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("ap-southeast-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_4() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("ap-southeast-2"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_5() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_6() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("ca-central-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_7() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("eu-central-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_8() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("eu-north-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_9() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("eu-west-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_10() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("eu-west-2"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_11() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("eu-west-3"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_12() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("sa-east-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_13() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("us-east-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_14() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("us-east-2"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_15() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("us-west-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_16() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("us-west-2"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_17() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://sts.{Region}.{PartitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_0() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).result("PartitionResult").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).treeRule(Arrays.asList(DefaultStsEndpointProvider.endpointRule_1(), DefaultStsEndpointProvider.endpointRule_2(), DefaultStsEndpointProvider.endpointRule_3(), DefaultStsEndpointProvider.endpointRule_4(), DefaultStsEndpointProvider.endpointRule_5(), DefaultStsEndpointProvider.endpointRule_6(), DefaultStsEndpointProvider.endpointRule_7(), DefaultStsEndpointProvider.endpointRule_8(), DefaultStsEndpointProvider.endpointRule_9(), DefaultStsEndpointProvider.endpointRule_10(), DefaultStsEndpointProvider.endpointRule_11(), DefaultStsEndpointProvider.endpointRule_12(), DefaultStsEndpointProvider.endpointRule_13(), DefaultStsEndpointProvider.endpointRule_14(), DefaultStsEndpointProvider.endpointRule_15(), DefaultStsEndpointProvider.endpointRule_16(), DefaultStsEndpointProvider.endpointRule_17()));
    }

    private static Rule endpointRule_19() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).error("Invalid Configuration: FIPS and custom endpoint are not supported");
    }

    private static Rule endpointRule_20() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).error("Invalid Configuration: Dualstack and custom endpoint are not supported");
    }

    private static Rule endpointRule_21() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.ref(Identifier.of("Endpoint"))).build());
    }

    private static Rule endpointRule_18() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).treeRule(Arrays.asList(DefaultStsEndpointProvider.endpointRule_19(), DefaultStsEndpointProvider.endpointRule_20(), DefaultStsEndpointProvider.endpointRule_21()));
    }

    private static Rule endpointRule_26() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://sts-fips.{Region}.{PartitionResult#dualStackDnsSuffix}")).build());
    }

    private static Rule endpointRule_25() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.of(true), FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("PartitionResult")), Expr.of("supportsFIPS"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.of(true), FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("PartitionResult")), Expr.of("supportsDualStack"))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultStsEndpointProvider.endpointRule_26()));
    }

    private static Rule endpointRule_27() {
        return Rule.builder().error("FIPS and DualStack are enabled, but this partition does not support one or both");
    }

    private static Rule endpointRule_24() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultStsEndpointProvider.endpointRule_25(), DefaultStsEndpointProvider.endpointRule_27()));
    }

    private static Rule endpointRule_30() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.of("aws-us-gov"), FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("PartitionResult")), Expr.of("name"))).build().validate())).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.{Region}.amazonaws.com")).build());
    }

    private static Rule endpointRule_31() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://sts-fips.{Region}.{PartitionResult#dnsSuffix}")).build());
    }

    private static Rule endpointRule_29() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.of(true), FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("PartitionResult")), Expr.of("supportsFIPS"))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultStsEndpointProvider.endpointRule_30(), DefaultStsEndpointProvider.endpointRule_31()));
    }

    private static Rule endpointRule_32() {
        return Rule.builder().error("FIPS is enabled but this partition does not support FIPS");
    }

    private static Rule endpointRule_28() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultStsEndpointProvider.endpointRule_29(), DefaultStsEndpointProvider.endpointRule_32()));
    }

    private static Rule endpointRule_35() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://sts.{Region}.{PartitionResult#dualStackDnsSuffix}")).build());
    }

    private static Rule endpointRule_34() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.of(true), FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("PartitionResult")), Expr.of("supportsDualStack"))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultStsEndpointProvider.endpointRule_35()));
    }

    private static Rule endpointRule_36() {
        return Rule.builder().error("DualStack is enabled but this partition does not support DualStack");
    }

    private static Rule endpointRule_33() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultStsEndpointProvider.endpointRule_34(), DefaultStsEndpointProvider.endpointRule_36()));
    }

    private static Rule endpointRule_37() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://sts.amazonaws.com")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("sts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_38() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://sts.{Region}.{PartitionResult#dnsSuffix}")).build());
    }

    private static Rule endpointRule_23() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).result("PartitionResult").build()).treeRule(Arrays.asList(DefaultStsEndpointProvider.endpointRule_24(), DefaultStsEndpointProvider.endpointRule_28(), DefaultStsEndpointProvider.endpointRule_33(), DefaultStsEndpointProvider.endpointRule_37(), DefaultStsEndpointProvider.endpointRule_38()));
    }

    private static Rule endpointRule_22() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).build()).treeRule(Arrays.asList(DefaultStsEndpointProvider.endpointRule_23()));
    }

    private static Rule endpointRule_39() {
        return Rule.builder().error("Invalid Configuration: Missing Region");
    }

    private static EndpointRuleset ruleSet() {
        return EndpointRuleset.builder().version("1.0").serviceId(null).parameters(Parameters.builder().addParameter(Parameter.builder().name("Region").type(ParameterType.fromValue("String")).required(false).builtIn("AWS::Region").documentation("The AWS region used to dispatch the request.").build()).addParameter(Parameter.builder().name("UseDualStack").type(ParameterType.fromValue("Boolean")).required(true).builtIn("AWS::UseDualStack").documentation("When true, use the dual-stack endpoint. If the configured endpoint does not support dual-stack, dispatching the request MAY return an error.").defaultValue(Value.fromBool(false)).build()).addParameter(Parameter.builder().name("UseFIPS").type(ParameterType.fromValue("Boolean")).required(true).builtIn("AWS::UseFIPS").documentation("When true, send this request to the FIPS-compliant regional endpoint. If the configured endpoint does not have a FIPS compliant endpoint, dispatching the request will return an error.").defaultValue(Value.fromBool(false)).build()).addParameter(Parameter.builder().name("Endpoint").type(ParameterType.fromValue("String")).required(false).builtIn("SDK::Endpoint").documentation("Override the endpoint used to send this request").build()).addParameter(Parameter.builder().name("UseGlobalEndpoint").type(ParameterType.fromValue("Boolean")).required(true).builtIn("AWS::STS::UseGlobalEndpoint").documentation("Whether the global endpoint should be used, rather then the regional endpoint for us-east-1.").defaultValue(Value.fromBool(false)).build()).build()).addRule(DefaultStsEndpointProvider.endpointRule_0()).addRule(DefaultStsEndpointProvider.endpointRule_18()).addRule(DefaultStsEndpointProvider.endpointRule_22()).addRule(DefaultStsEndpointProvider.endpointRule_39()).build();
    }
}

