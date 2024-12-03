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
package software.amazon.awssdk.services.s3.endpoints.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointParams;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;
import software.amazon.awssdk.services.s3.endpoints.internal.AwsEndpointProviderUtils;
import software.amazon.awssdk.services.s3.endpoints.internal.Condition;
import software.amazon.awssdk.services.s3.endpoints.internal.DefaultRuleEngine;
import software.amazon.awssdk.services.s3.endpoints.internal.EndpointResult;
import software.amazon.awssdk.services.s3.endpoints.internal.EndpointRuleset;
import software.amazon.awssdk.services.s3.endpoints.internal.Expr;
import software.amazon.awssdk.services.s3.endpoints.internal.FnNode;
import software.amazon.awssdk.services.s3.endpoints.internal.Identifier;
import software.amazon.awssdk.services.s3.endpoints.internal.Literal;
import software.amazon.awssdk.services.s3.endpoints.internal.Parameter;
import software.amazon.awssdk.services.s3.endpoints.internal.ParameterType;
import software.amazon.awssdk.services.s3.endpoints.internal.Parameters;
import software.amazon.awssdk.services.s3.endpoints.internal.Rule;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.MapUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultS3EndpointProvider
implements S3EndpointProvider {
    private static final EndpointRuleset ENDPOINT_RULE_SET = DefaultS3EndpointProvider.ruleSet();

    @Override
    public CompletableFuture<Endpoint> resolveEndpoint(S3EndpointParams endpointParams) {
        Validate.notNull((Object)endpointParams.useFips(), (String)"Parameter 'UseFIPS' must not be null", (Object[])new Object[0]);
        Validate.notNull((Object)endpointParams.useDualStack(), (String)"Parameter 'UseDualStack' must not be null", (Object[])new Object[0]);
        Validate.notNull((Object)endpointParams.forcePathStyle(), (String)"Parameter 'ForcePathStyle' must not be null", (Object[])new Object[0]);
        Validate.notNull((Object)endpointParams.accelerate(), (String)"Parameter 'Accelerate' must not be null", (Object[])new Object[0]);
        Validate.notNull((Object)endpointParams.useGlobalEndpoint(), (String)"Parameter 'UseGlobalEndpoint' must not be null", (Object[])new Object[0]);
        Validate.notNull((Object)endpointParams.disableMultiRegionAccessPoints(), (String)"Parameter 'DisableMultiRegionAccessPoints' must not be null", (Object[])new Object[0]);
        Value res = new DefaultRuleEngine().evaluate(ENDPOINT_RULE_SET, DefaultS3EndpointProvider.toIdentifierValueMap(endpointParams));
        try {
            return CompletableFuture.completedFuture(AwsEndpointProviderUtils.valueAsEndpointOrThrow(res));
        }
        catch (Exception error) {
            return CompletableFutureUtils.failedFuture((Throwable)error);
        }
    }

    private static Map<Identifier, Value> toIdentifierValueMap(S3EndpointParams params) {
        HashMap<Identifier, Value> paramsMap = new HashMap<Identifier, Value>();
        if (params.bucket() != null) {
            paramsMap.put(Identifier.of("Bucket"), Value.fromStr(params.bucket()));
        }
        if (params.region() != null) {
            paramsMap.put(Identifier.of("Region"), Value.fromStr(params.region().id()));
        }
        if (params.useFips() != null) {
            paramsMap.put(Identifier.of("UseFIPS"), Value.fromBool(params.useFips()));
        }
        if (params.useDualStack() != null) {
            paramsMap.put(Identifier.of("UseDualStack"), Value.fromBool(params.useDualStack()));
        }
        if (params.endpoint() != null) {
            paramsMap.put(Identifier.of("Endpoint"), Value.fromStr(params.endpoint()));
        }
        if (params.forcePathStyle() != null) {
            paramsMap.put(Identifier.of("ForcePathStyle"), Value.fromBool(params.forcePathStyle()));
        }
        if (params.accelerate() != null) {
            paramsMap.put(Identifier.of("Accelerate"), Value.fromBool(params.accelerate()));
        }
        if (params.useGlobalEndpoint() != null) {
            paramsMap.put(Identifier.of("UseGlobalEndpoint"), Value.fromBool(params.useGlobalEndpoint()));
        }
        if (params.useObjectLambdaEndpoint() != null) {
            paramsMap.put(Identifier.of("UseObjectLambdaEndpoint"), Value.fromBool(params.useObjectLambdaEndpoint()));
        }
        if (params.disableAccessPoints() != null) {
            paramsMap.put(Identifier.of("DisableAccessPoints"), Value.fromBool(params.disableAccessPoints()));
        }
        if (params.disableMultiRegionAccessPoints() != null) {
            paramsMap.put(Identifier.of("DisableMultiRegionAccessPoints"), Value.fromBool(params.disableMultiRegionAccessPoints()));
        }
        if (params.useArnRegion() != null) {
            paramsMap.put(Identifier.of("UseArnRegion"), Value.fromBool(params.useArnRegion()));
        }
        return paramsMap;
    }

    private static Rule endpointRule_1() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).error("Accelerate cannot be used with FIPS");
    }

    private static Rule endpointRule_2() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).error("Cannot set dual-stack in combination with a custom endpoint.");
    }

    private static Rule endpointRule_3() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).error("A custom endpoint cannot be combined with FIPS");
    }

    private static Rule endpointRule_4() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(true))).build().validate()).build()).error("A custom endpoint cannot be combined with S3 Accelerate");
    }

    private static Rule endpointRule_5() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).result("partitionResult").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("partitionResult")), Expr.of("name"))).build().validate(), Expr.of("aws-cn"))).build().validate()).build()).error("Partition does not support FIPS");
    }

    private static Rule endpointRule_10() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).error("Expected a endpoint to be specified but no endpoint was found");
    }

    private static Rule endpointRule_11() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.ec2.{url#authority}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3-outposts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_9() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("regionPrefix")), Expr.of("beta"))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_10(), DefaultS3EndpointProvider.endpointRule_11()));
    }

    private static Rule endpointRule_12() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.ec2.s3-outposts.{Region}.{regionPartition#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3-outposts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_8() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("hardwareType")), Expr.of("e"))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_9(), DefaultS3EndpointProvider.endpointRule_12()));
    }

    private static Rule endpointRule_15() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).error("Expected a endpoint to be specified but no endpoint was found");
    }

    private static Rule endpointRule_16() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.op-{outpostId}.{url#authority}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3-outposts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_14() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("regionPrefix")), Expr.of("beta"))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_15(), DefaultS3EndpointProvider.endpointRule_16()));
    }

    private static Rule endpointRule_17() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.op-{outpostId}.s3-outposts.{Region}.{regionPartition#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3-outposts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_13() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("hardwareType")), Expr.of("o"))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_14(), DefaultS3EndpointProvider.endpointRule_17()));
    }

    private static Rule endpointRule_18() {
        return Rule.builder().error("Unrecognized hardware type: \"Expected hardware type o or e but got {hardwareType}\"");
    }

    private static Rule endpointRule_7() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(Expr.ref(Identifier.of("outpostId")), Expr.of(false))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_8(), DefaultS3EndpointProvider.endpointRule_13(), DefaultS3EndpointProvider.endpointRule_18()));
    }

    private static Rule endpointRule_19() {
        return Rule.builder().error("Invalid ARN: The outpost Id must only contain a-z, A-Z, 0-9 and `-`.");
    }

    private static Rule endpointRule_6() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Bucket")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("substring").argv(Arrays.asList(Expr.ref(Identifier.of("Bucket")), Expr.of(49), Expr.of(50), Expr.of(true))).build().validate()).result("hardwareType").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("substring").argv(Arrays.asList(Expr.ref(Identifier.of("Bucket")), Expr.of(8), Expr.of(12), Expr.of(true))).build().validate()).result("regionPrefix").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("substring").argv(Arrays.asList(Expr.ref(Identifier.of("Bucket")), Expr.of(0), Expr.of(7), Expr.of(true))).build().validate()).result("bucketAliasSuffix").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("substring").argv(Arrays.asList(Expr.ref(Identifier.of("Bucket")), Expr.of(32), Expr.of(49), Expr.of(true))).build().validate()).result("outpostId").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).result("regionPartition").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("bucketAliasSuffix")), Expr.of("--op-s3"))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_7(), DefaultS3EndpointProvider.endpointRule_19()));
    }

    private static Rule endpointRule_21() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate())).build().validate()).build()).error("Custom endpoint `{Endpoint}` was not a valid URI");
    }

    private static Rule endpointRule_25() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("partitionResult")), Expr.of("name"))).build().validate(), Expr.of("aws-cn"))).build().validate()).build()).error("S3 Accelerate cannot be used in this region");
    }

    private static Rule endpointRule_26() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3-fips.dualstack.us-east-1.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_28() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3-fips.dualstack.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_27() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_28()));
    }

    private static Rule endpointRule_29() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3-fips.dualstack.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_30() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3-fips.us-east-1.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_32() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3-fips.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_31() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_32()));
    }

    private static Rule endpointRule_33() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3-fips.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_34() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3-accelerate.dualstack.us-east-1.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_36() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3-accelerate.dualstack.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_35() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_36()));
    }

    private static Rule endpointRule_37() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3-accelerate.dualstack.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_38() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3.dualstack.us-east-1.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_40() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3.dualstack.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_39() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_40()));
    }

    private static Rule endpointRule_41() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3.dualstack.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_42() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("url")), Expr.of("isIp"))).build().validate(), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{url#authority}{url#normalizedPath}{Bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_43() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("url")), Expr.of("isIp"))).build().validate(), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{Bucket}.{url#authority}{url#path}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_45() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("us-east-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{url#authority}{url#normalizedPath}{Bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_46() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{url#authority}{url#normalizedPath}{Bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_44() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("url")), Expr.of("isIp"))).build().validate(), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_45(), DefaultS3EndpointProvider.endpointRule_46()));
    }

    private static Rule endpointRule_48() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("us-east-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{Bucket}.{url#authority}{url#path}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_49() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{Bucket}.{url#authority}{url#path}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_47() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("url")), Expr.of("isIp"))).build().validate(), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_48(), DefaultS3EndpointProvider.endpointRule_49()));
    }

    private static Rule endpointRule_50() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("url")), Expr.of("isIp"))).build().validate(), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{url#authority}{url#normalizedPath}{Bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_51() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("url")), Expr.of("isIp"))).build().validate(), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{Bucket}.{url#authority}{url#path}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_52() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3-accelerate.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_54() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("us-east-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3-accelerate.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_55() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3-accelerate.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_53() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_54(), DefaultS3EndpointProvider.endpointRule_55()));
    }

    private static Rule endpointRule_56() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3-accelerate.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_57() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_59() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("us-east-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_60() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_58() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_59(), DefaultS3EndpointProvider.endpointRule_60()));
    }

    private static Rule endpointRule_61() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{Bucket}.s3.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_24() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of(false))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_25(), DefaultS3EndpointProvider.endpointRule_26(), DefaultS3EndpointProvider.endpointRule_27(), DefaultS3EndpointProvider.endpointRule_29(), DefaultS3EndpointProvider.endpointRule_30(), DefaultS3EndpointProvider.endpointRule_31(), DefaultS3EndpointProvider.endpointRule_33(), DefaultS3EndpointProvider.endpointRule_34(), DefaultS3EndpointProvider.endpointRule_35(), DefaultS3EndpointProvider.endpointRule_37(), DefaultS3EndpointProvider.endpointRule_38(), DefaultS3EndpointProvider.endpointRule_39(), DefaultS3EndpointProvider.endpointRule_41(), DefaultS3EndpointProvider.endpointRule_42(), DefaultS3EndpointProvider.endpointRule_43(), DefaultS3EndpointProvider.endpointRule_44(), DefaultS3EndpointProvider.endpointRule_47(), DefaultS3EndpointProvider.endpointRule_50(), DefaultS3EndpointProvider.endpointRule_51(), DefaultS3EndpointProvider.endpointRule_52(), DefaultS3EndpointProvider.endpointRule_53(), DefaultS3EndpointProvider.endpointRule_56(), DefaultS3EndpointProvider.endpointRule_57(), DefaultS3EndpointProvider.endpointRule_58(), DefaultS3EndpointProvider.endpointRule_61()));
    }

    private static Rule endpointRule_62() {
        return Rule.builder().error("Invalid region: region was not a valid DNS name.");
    }

    private static Rule endpointRule_23() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).result("partitionResult").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_24(), DefaultS3EndpointProvider.endpointRule_62()));
    }

    private static Rule endpointRule_22() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("ForcePathStyle")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("aws.isVirtualHostableS3Bucket").argv(Arrays.asList(Expr.ref(Identifier.of("Bucket")), Expr.of(false))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_23()));
    }

    private static Rule endpointRule_66() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{Bucket}.{url#authority}{url#path}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_65() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of(false))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_66()));
    }

    private static Rule endpointRule_67() {
        return Rule.builder().error("Invalid region: region was not a valid DNS name.");
    }

    private static Rule endpointRule_64() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).result("partitionResult").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_65(), DefaultS3EndpointProvider.endpointRule_67()));
    }

    private static Rule endpointRule_63() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("url")), Expr.of("scheme"))).build().validate(), Expr.of("http"))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("aws.isVirtualHostableS3Bucket").argv(Arrays.asList(Expr.ref(Identifier.of("Bucket")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("ForcePathStyle")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_64()));
    }

    private static Rule endpointRule_73() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).error("S3 Object Lambda does not support Dual-stack");
    }

    private static Rule endpointRule_74() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(true))).build().validate()).build()).error("S3 Object Lambda does not support S3 Accelerate");
    }

    private static Rule endpointRule_76() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("DisableAccessPoints")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("DisableAccessPoints")), Expr.of(true))).build().validate()).build()).error("Access points are not supported for this operation");
    }

    private static Rule endpointRule_78() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("UseArnRegion")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseArnRegion")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("region"))).build().validate(), Expr.of("{Region}"))).build().validate())).build().validate()).build()).error("Invalid configuration: region from ARN `{bucketArn#region}` does not match client region `{Region}` and UseArnRegion is `false`");
    }

    private static Rule endpointRule_83() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("accountId"))).build().validate(), Expr.of(""))).build().validate()).build()).error("Invalid ARN: Missing account id");
    }

    private static Rule endpointRule_86() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{accessPointName}-{bucketArn#accountId}.{url#authority}{url#path}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3-object-lambda"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{bucketArn#region}")))))).build());
    }

    private static Rule endpointRule_87() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{accessPointName}-{bucketArn#accountId}.s3-object-lambda-fips.{bucketArn#region}.{bucketPartition#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3-object-lambda"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{bucketArn#region}")))))).build());
    }

    private static Rule endpointRule_88() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://{accessPointName}-{bucketArn#accountId}.s3-object-lambda.{bucketArn#region}.{bucketPartition#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3-object-lambda"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{bucketArn#region}")))))).build());
    }

    private static Rule endpointRule_85() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(Expr.ref(Identifier.of("accessPointName")), Expr.of(false))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_86(), DefaultS3EndpointProvider.endpointRule_87(), DefaultS3EndpointProvider.endpointRule_88()));
    }

    private static Rule endpointRule_89() {
        return Rule.builder().error("Invalid ARN: The access point name may only contain a-z, A-Z, 0-9 and `-`. Found: `{accessPointName}`");
    }

    private static Rule endpointRule_84() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("accountId"))).build().validate(), Expr.of(false))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_85(), DefaultS3EndpointProvider.endpointRule_89()));
    }

    private static Rule endpointRule_90() {
        return Rule.builder().error("Invalid ARN: The account id may only contain a-z, A-Z, 0-9 and `-`. Found: `{bucketArn#accountId}`");
    }

    private static Rule endpointRule_82() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("region"))).build().validate(), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_83(), DefaultS3EndpointProvider.endpointRule_84(), DefaultS3EndpointProvider.endpointRule_90()));
    }

    private static Rule endpointRule_91() {
        return Rule.builder().error("Invalid region in ARN: `{bucketArn#region}` (invalid DNS name)");
    }

    private static Rule endpointRule_81() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketPartition")), Expr.of("name"))).build().validate(), FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("partitionResult")), Expr.of("name"))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_82(), DefaultS3EndpointProvider.endpointRule_91()));
    }

    private static Rule endpointRule_92() {
        return Rule.builder().error("Client was configured for partition `{partitionResult#name}` but ARN (`{Bucket}`) has `{bucketPartition#name}`");
    }

    private static Rule endpointRule_80() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).result("partitionResult").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_81(), DefaultS3EndpointProvider.endpointRule_92()));
    }

    private static Rule endpointRule_79() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("region"))).build().validate())).build().validate()).result("bucketPartition").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_80()));
    }

    private static Rule endpointRule_77() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("resourceId[2]"))).build().validate())).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_78(), DefaultS3EndpointProvider.endpointRule_79()));
    }

    private static Rule endpointRule_93() {
        return Rule.builder().error("Invalid ARN: The ARN may only contain a single resource component after `accesspoint`.");
    }

    private static Rule endpointRule_75() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("region"))).build().validate(), Expr.of(""))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_76(), DefaultS3EndpointProvider.endpointRule_77(), DefaultS3EndpointProvider.endpointRule_93()));
    }

    private static Rule endpointRule_94() {
        return Rule.builder().error("Invalid ARN: bucket ARN is missing a region");
    }

    private static Rule endpointRule_72() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("resourceId[1]"))).build().validate()).result("accessPointName").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("accessPointName")), Expr.of(""))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_73(), DefaultS3EndpointProvider.endpointRule_74(), DefaultS3EndpointProvider.endpointRule_75(), DefaultS3EndpointProvider.endpointRule_94()));
    }

    private static Rule endpointRule_95() {
        return Rule.builder().error("Invalid ARN: Expected a resource of the format `accesspoint:<accesspoint name>` but no name was provided");
    }

    private static Rule endpointRule_71() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("arnType")), Expr.of("accesspoint"))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_72(), DefaultS3EndpointProvider.endpointRule_95()));
    }

    private static Rule endpointRule_96() {
        return Rule.builder().error("Invalid ARN: Object Lambda ARNs only support `accesspoint` arn types, but found: `{arnType}`");
    }

    private static Rule endpointRule_70() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("service"))).build().validate(), Expr.of("s3-object-lambda"))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_71(), DefaultS3EndpointProvider.endpointRule_96()));
    }

    private static Rule endpointRule_102() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("DisableAccessPoints")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("DisableAccessPoints")), Expr.of(true))).build().validate()).build()).error("Access points are not supported for this operation");
    }

    private static Rule endpointRule_104() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("UseArnRegion")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseArnRegion")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("region"))).build().validate(), Expr.of("{Region}"))).build().validate())).build().validate()).build()).error("Invalid configuration: region from ARN `{bucketArn#region}` does not match client region `{Region}` and UseArnRegion is `false`");
    }

    private static Rule endpointRule_112() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(true))).build().validate()).build()).error("Access Points do not support S3 Accelerate");
    }

    private static Rule endpointRule_113() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{accessPointName}-{bucketArn#accountId}.s3-accesspoint-fips.dualstack.{bucketArn#region}.{bucketPartition#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{bucketArn#region}")))))).build());
    }

    private static Rule endpointRule_114() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{accessPointName}-{bucketArn#accountId}.s3-accesspoint-fips.{bucketArn#region}.{bucketPartition#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{bucketArn#region}")))))).build());
    }

    private static Rule endpointRule_115() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{accessPointName}-{bucketArn#accountId}.s3-accesspoint.dualstack.{bucketArn#region}.{bucketPartition#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{bucketArn#region}")))))).build());
    }

    private static Rule endpointRule_116() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{accessPointName}-{bucketArn#accountId}.{url#authority}{url#path}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{bucketArn#region}")))))).build());
    }

    private static Rule endpointRule_117() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://{accessPointName}-{bucketArn#accountId}.s3-accesspoint.{bucketArn#region}.{bucketPartition#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{bucketArn#region}")))))).build());
    }

    private static Rule endpointRule_111() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(Expr.ref(Identifier.of("accessPointName")), Expr.of(false))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_112(), DefaultS3EndpointProvider.endpointRule_113(), DefaultS3EndpointProvider.endpointRule_114(), DefaultS3EndpointProvider.endpointRule_115(), DefaultS3EndpointProvider.endpointRule_116(), DefaultS3EndpointProvider.endpointRule_117()));
    }

    private static Rule endpointRule_118() {
        return Rule.builder().error("Invalid ARN: The access point name may only contain a-z, A-Z, 0-9 and `-`. Found: `{accessPointName}`");
    }

    private static Rule endpointRule_110() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("accountId"))).build().validate(), Expr.of(false))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_111(), DefaultS3EndpointProvider.endpointRule_118()));
    }

    private static Rule endpointRule_119() {
        return Rule.builder().error("Invalid ARN: The account id may only contain a-z, A-Z, 0-9 and `-`. Found: `{bucketArn#accountId}`");
    }

    private static Rule endpointRule_109() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("service"))).build().validate(), Expr.of("s3"))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_110(), DefaultS3EndpointProvider.endpointRule_119()));
    }

    private static Rule endpointRule_120() {
        return Rule.builder().error("Invalid ARN: The ARN was not for the S3 service, found: {bucketArn#service}");
    }

    private static Rule endpointRule_108() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("region"))).build().validate(), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_109(), DefaultS3EndpointProvider.endpointRule_120()));
    }

    private static Rule endpointRule_121() {
        return Rule.builder().error("Invalid region in ARN: `{bucketArn#region}` (invalid DNS name)");
    }

    private static Rule endpointRule_107() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketPartition")), Expr.of("name"))).build().validate(), Expr.of("{partitionResult#name}"))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_108(), DefaultS3EndpointProvider.endpointRule_121()));
    }

    private static Rule endpointRule_122() {
        return Rule.builder().error("Client was configured for partition `{partitionResult#name}` but ARN (`{Bucket}`) has `{bucketPartition#name}`");
    }

    private static Rule endpointRule_106() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).result("partitionResult").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_107(), DefaultS3EndpointProvider.endpointRule_122()));
    }

    private static Rule endpointRule_105() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("region"))).build().validate())).build().validate()).result("bucketPartition").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_106()));
    }

    private static Rule endpointRule_103() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("resourceId[2]"))).build().validate())).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_104(), DefaultS3EndpointProvider.endpointRule_105()));
    }

    private static Rule endpointRule_123() {
        return Rule.builder().error("Invalid ARN: The ARN may only contain a single resource component after `accesspoint`.");
    }

    private static Rule endpointRule_101() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("region"))).build().validate(), Expr.of(""))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_102(), DefaultS3EndpointProvider.endpointRule_103(), DefaultS3EndpointProvider.endpointRule_123()));
    }

    private static Rule endpointRule_100() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("arnType")), Expr.of("accesspoint"))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_101()));
    }

    private static Rule endpointRule_99() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("region"))).build().validate(), Expr.of(""))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_100()));
    }

    private static Rule endpointRule_125() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).error("S3 MRAP does not support dual-stack");
    }

    private static Rule endpointRule_126() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).error("S3 MRAP does not support FIPS");
    }

    private static Rule endpointRule_127() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(true))).build().validate()).build()).error("S3 MRAP does not support S3 Accelerate");
    }

    private static Rule endpointRule_128() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("DisableMultiRegionAccessPoints")), Expr.of(true))).build().validate()).build()).error("Invalid configuration: Multi-Region Access Point ARNs are disabled.");
    }

    private static Rule endpointRule_131() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://{accessPointName}.accesspoint.s3-global.{mrapPartition#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4a"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegionSet"), (Object)Literal.fromTuple(Arrays.asList(Literal.fromStr("*")))))))).build());
    }

    private static Rule endpointRule_130() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("mrapPartition")), Expr.of("name"))).build().validate(), FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("partition"))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_131()));
    }

    private static Rule endpointRule_132() {
        return Rule.builder().error("Client was configured for partition `{mrapPartition#name}` but bucket referred to partition `{bucketArn#partition}`");
    }

    private static Rule endpointRule_129() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).result("mrapPartition").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_130(), DefaultS3EndpointProvider.endpointRule_132()));
    }

    private static Rule endpointRule_124() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(Expr.ref(Identifier.of("accessPointName")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_125(), DefaultS3EndpointProvider.endpointRule_126(), DefaultS3EndpointProvider.endpointRule_127(), DefaultS3EndpointProvider.endpointRule_128(), DefaultS3EndpointProvider.endpointRule_129()));
    }

    private static Rule endpointRule_133() {
        return Rule.builder().error("Invalid Access Point Name");
    }

    private static Rule endpointRule_98() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("resourceId[1]"))).build().validate()).result("accessPointName").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("accessPointName")), Expr.of(""))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_99(), DefaultS3EndpointProvider.endpointRule_124(), DefaultS3EndpointProvider.endpointRule_133()));
    }

    private static Rule endpointRule_134() {
        return Rule.builder().error("Invalid ARN: Expected a resource of the format `accesspoint:<accesspoint name>` but no name was provided");
    }

    private static Rule endpointRule_97() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("arnType")), Expr.of("accesspoint"))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_98(), DefaultS3EndpointProvider.endpointRule_134()));
    }

    private static Rule endpointRule_136() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).error("S3 Outposts does not support Dual-stack");
    }

    private static Rule endpointRule_137() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).error("S3 Outposts does not support FIPS");
    }

    private static Rule endpointRule_138() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(true))).build().validate()).build()).error("S3 Outposts does not support S3 Accelerate");
    }

    private static Rule endpointRule_139() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("resourceId[4]"))).build().validate())).build().validate()).build()).error("Invalid Arn: Outpost Access Point ARN contains sub resources");
    }

    private static Rule endpointRule_142() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("UseArnRegion")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseArnRegion")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("region"))).build().validate(), Expr.of("{Region}"))).build().validate())).build().validate()).build()).error("Invalid configuration: region from ARN `{bucketArn#region}` does not match client region `{Region}` and UseArnRegion is `false`");
    }

    private static Rule endpointRule_151() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).endpoint(EndpointResult.builder().url(Expr.of("https://{accessPointName}-{bucketArn#accountId}.{outpostId}.{url#authority}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3-outposts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{bucketArn#region}")))))).build());
    }

    private static Rule endpointRule_152() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://{accessPointName}-{bucketArn#accountId}.{outpostId}.s3-outposts.{bucketArn#region}.{bucketPartition#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3-outposts"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{bucketArn#region}")))))).build());
    }

    private static Rule endpointRule_150() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("outpostType")), Expr.of("accesspoint"))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_151(), DefaultS3EndpointProvider.endpointRule_152()));
    }

    private static Rule endpointRule_153() {
        return Rule.builder().error("Expected an outpost type `accesspoint`, found {outpostType}");
    }

    private static Rule endpointRule_149() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("resourceId[3]"))).build().validate()).result("accessPointName").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_150(), DefaultS3EndpointProvider.endpointRule_153()));
    }

    private static Rule endpointRule_154() {
        return Rule.builder().error("Invalid ARN: expected an access point name");
    }

    private static Rule endpointRule_148() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("resourceId[2]"))).build().validate()).result("outpostType").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_149(), DefaultS3EndpointProvider.endpointRule_154()));
    }

    private static Rule endpointRule_155() {
        return Rule.builder().error("Invalid ARN: Expected a 4-component resource");
    }

    private static Rule endpointRule_147() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("accountId"))).build().validate(), Expr.of(false))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_148(), DefaultS3EndpointProvider.endpointRule_155()));
    }

    private static Rule endpointRule_156() {
        return Rule.builder().error("Invalid ARN: The account id may only contain a-z, A-Z, 0-9 and `-`. Found: `{bucketArn#accountId}`");
    }

    private static Rule endpointRule_146() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("region"))).build().validate(), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_147(), DefaultS3EndpointProvider.endpointRule_156()));
    }

    private static Rule endpointRule_157() {
        return Rule.builder().error("Invalid region in ARN: `{bucketArn#region}` (invalid DNS name)");
    }

    private static Rule endpointRule_145() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketPartition")), Expr.of("name"))).build().validate(), FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("partitionResult")), Expr.of("name"))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_146(), DefaultS3EndpointProvider.endpointRule_157()));
    }

    private static Rule endpointRule_158() {
        return Rule.builder().error("Client was configured for partition `{partitionResult#name}` but ARN (`{Bucket}`) has `{bucketPartition#name}`");
    }

    private static Rule endpointRule_144() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).result("partitionResult").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_145(), DefaultS3EndpointProvider.endpointRule_158()));
    }

    private static Rule endpointRule_143() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("region"))).build().validate())).build().validate()).result("bucketPartition").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_144()));
    }

    private static Rule endpointRule_141() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(Expr.ref(Identifier.of("outpostId")), Expr.of(false))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_142(), DefaultS3EndpointProvider.endpointRule_143()));
    }

    private static Rule endpointRule_159() {
        return Rule.builder().error("Invalid ARN: The outpost Id may only contain a-z, A-Z, 0-9 and `-`. Found: `{outpostId}`");
    }

    private static Rule endpointRule_140() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("resourceId[1]"))).build().validate()).result("outpostId").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_141(), DefaultS3EndpointProvider.endpointRule_159()));
    }

    private static Rule endpointRule_160() {
        return Rule.builder().error("Invalid ARN: The Outpost Id was not set");
    }

    private static Rule endpointRule_135() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("service"))).build().validate(), Expr.of("s3-outposts"))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_136(), DefaultS3EndpointProvider.endpointRule_137(), DefaultS3EndpointProvider.endpointRule_138(), DefaultS3EndpointProvider.endpointRule_139(), DefaultS3EndpointProvider.endpointRule_140(), DefaultS3EndpointProvider.endpointRule_160()));
    }

    private static Rule endpointRule_161() {
        return Rule.builder().error("Invalid ARN: Unrecognized format: {Bucket} (type: {arnType})");
    }

    private static Rule endpointRule_69() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("getAttr").argv(Arrays.asList(Expr.ref(Identifier.of("bucketArn")), Expr.of("resourceId[0]"))).build().validate()).result("arnType").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("arnType")), Expr.of(""))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_70(), DefaultS3EndpointProvider.endpointRule_97(), DefaultS3EndpointProvider.endpointRule_135(), DefaultS3EndpointProvider.endpointRule_161()));
    }

    private static Rule endpointRule_162() {
        return Rule.builder().error("Invalid ARN: No ARN type specified");
    }

    private static Rule endpointRule_68() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("ForcePathStyle")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("aws.parseArn").argv(Arrays.asList(Expr.ref(Identifier.of("Bucket")))).build().validate()).result("bucketArn").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_69(), DefaultS3EndpointProvider.endpointRule_162()));
    }

    private static Rule endpointRule_163() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("substring").argv(Arrays.asList(Expr.ref(Identifier.of("Bucket")), Expr.of(0), Expr.of(4), Expr.of(false))).build().validate()).result("arnPrefix").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("arnPrefix")), Expr.of("arn:"))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(FnNode.builder().fn("aws.parseArn").argv(Arrays.asList(Expr.ref(Identifier.of("Bucket")))).build().validate())).build().validate())).build().validate()).build()).error("Invalid ARN: `{Bucket}` was not a valid ARN");
    }

    private static Rule endpointRule_164() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("ForcePathStyle")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("aws.parseArn").argv(Arrays.asList(Expr.ref(Identifier.of("Bucket")))).build().validate()).build()).error("Path-style addressing cannot be used with ARN buckets");
    }

    private static Rule endpointRule_168() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3-fips.dualstack.us-east-1.{partitionResult#dnsSuffix}/{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_170() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://s3-fips.dualstack.{Region}.{partitionResult#dnsSuffix}/{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_169() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_170()));
    }

    private static Rule endpointRule_171() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3-fips.dualstack.{Region}.{partitionResult#dnsSuffix}/{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_172() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3-fips.us-east-1.{partitionResult#dnsSuffix}/{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_174() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://s3-fips.{Region}.{partitionResult#dnsSuffix}/{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_173() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_174()));
    }

    private static Rule endpointRule_175() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3-fips.{Region}.{partitionResult#dnsSuffix}/{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_176() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3.dualstack.us-east-1.{partitionResult#dnsSuffix}/{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_178() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://s3.dualstack.{Region}.{partitionResult#dnsSuffix}/{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_177() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_178()));
    }

    private static Rule endpointRule_179() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3.dualstack.{Region}.{partitionResult#dnsSuffix}/{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_180() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{url#authority}{url#normalizedPath}{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_182() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("us-east-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{url#authority}{url#normalizedPath}{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_183() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{url#authority}{url#normalizedPath}{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_181() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_182(), DefaultS3EndpointProvider.endpointRule_183()));
    }

    private static Rule endpointRule_184() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{url#authority}{url#normalizedPath}{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_185() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3.{partitionResult#dnsSuffix}/{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_187() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("us-east-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3.{partitionResult#dnsSuffix}/{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_188() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://s3.{Region}.{partitionResult#dnsSuffix}/{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_186() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_187(), DefaultS3EndpointProvider.endpointRule_188()));
    }

    private static Rule endpointRule_189() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3.{Region}.{partitionResult#dnsSuffix}/{uri_encoded_bucket}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_167() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(false))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_168(), DefaultS3EndpointProvider.endpointRule_169(), DefaultS3EndpointProvider.endpointRule_171(), DefaultS3EndpointProvider.endpointRule_172(), DefaultS3EndpointProvider.endpointRule_173(), DefaultS3EndpointProvider.endpointRule_175(), DefaultS3EndpointProvider.endpointRule_176(), DefaultS3EndpointProvider.endpointRule_177(), DefaultS3EndpointProvider.endpointRule_179(), DefaultS3EndpointProvider.endpointRule_180(), DefaultS3EndpointProvider.endpointRule_181(), DefaultS3EndpointProvider.endpointRule_184(), DefaultS3EndpointProvider.endpointRule_185(), DefaultS3EndpointProvider.endpointRule_186(), DefaultS3EndpointProvider.endpointRule_189()));
    }

    private static Rule endpointRule_190() {
        return Rule.builder().error("Path-style addressing cannot be used with S3 Accelerate");
    }

    private static Rule endpointRule_166() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).result("partitionResult").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_167(), DefaultS3EndpointProvider.endpointRule_190()));
    }

    private static Rule endpointRule_165() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("uriEncode").argv(Arrays.asList(Expr.ref(Identifier.of("Bucket")))).build().validate()).result("uri_encoded_bucket").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_166()));
    }

    private static Rule endpointRule_20() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Bucket")))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_21(), DefaultS3EndpointProvider.endpointRule_22(), DefaultS3EndpointProvider.endpointRule_63(), DefaultS3EndpointProvider.endpointRule_68(), DefaultS3EndpointProvider.endpointRule_163(), DefaultS3EndpointProvider.endpointRule_164(), DefaultS3EndpointProvider.endpointRule_165()));
    }

    private static Rule endpointRule_194() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).error("S3 Object Lambda does not support Dual-stack");
    }

    private static Rule endpointRule_195() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Accelerate")), Expr.of(true))).build().validate()).build()).error("S3 Object Lambda does not support S3 Accelerate");
    }

    private static Rule endpointRule_196() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{url#authority}{url#path}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3-object-lambda"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_197() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3-object-lambda-fips.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3-object-lambda"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_198() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://s3-object-lambda.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3-object-lambda"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_193() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_194(), DefaultS3EndpointProvider.endpointRule_195(), DefaultS3EndpointProvider.endpointRule_196(), DefaultS3EndpointProvider.endpointRule_197(), DefaultS3EndpointProvider.endpointRule_198()));
    }

    private static Rule endpointRule_199() {
        return Rule.builder().error("Invalid region: region was not a valid DNS name.");
    }

    private static Rule endpointRule_192() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).result("partitionResult").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_193(), DefaultS3EndpointProvider.endpointRule_199()));
    }

    private static Rule endpointRule_191() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("UseObjectLambdaEndpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseObjectLambdaEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_192()));
    }

    private static Rule endpointRule_203() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3-fips.dualstack.us-east-1.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_205() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://s3-fips.dualstack.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_204() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_205()));
    }

    private static Rule endpointRule_206() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3-fips.dualstack.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_207() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3-fips.us-east-1.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_209() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://s3-fips.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_208() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_209()));
    }

    private static Rule endpointRule_210() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3-fips.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_211() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3.dualstack.us-east-1.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_213() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://s3.dualstack.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_212() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_213()));
    }

    private static Rule endpointRule_214() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(true))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3.dualstack.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_215() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{url#authority}{url#path}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_217() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("us-east-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{url#authority}{url#path}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_218() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{url#authority}{url#path}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_216() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_217(), DefaultS3EndpointProvider.endpointRule_218()));
    }

    private static Rule endpointRule_219() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("parseURL").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate()).result("url").build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("{url#scheme}://{url#authority}{url#path}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_220() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("us-east-1")))))).build());
    }

    private static Rule endpointRule_222() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("us-east-1"))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_223() {
        return Rule.builder().endpoint(EndpointResult.builder().url(Expr.of("https://s3.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_221() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_222(), DefaultS3EndpointProvider.endpointRule_223()));
    }

    private static Rule endpointRule_224() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseFIPS")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseDualStack")), Expr.of(false))).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Endpoint")))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("stringEquals").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of("aws-global"))).build().validate())).build().validate()).build()).addCondition(Condition.builder().fn(FnNode.builder().fn("booleanEquals").argv(Arrays.asList(Expr.ref(Identifier.of("UseGlobalEndpoint")), Expr.of(false))).build().validate()).build()).endpoint(EndpointResult.builder().url(Expr.of("https://s3.{Region}.{partitionResult#dnsSuffix}")).addProperty(Identifier.of("authSchemes"), Literal.fromTuple(Arrays.asList(Literal.fromRecord(MapUtils.of((Object)Identifier.of("disableDoubleEncoding"), (Object)Literal.fromBool(true), (Object)Identifier.of("name"), (Object)Literal.fromStr("sigv4"), (Object)Identifier.of("signingName"), (Object)Literal.fromStr("s3"), (Object)Identifier.of("signingRegion"), (Object)Literal.fromStr("{Region}")))))).build());
    }

    private static Rule endpointRule_202() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isValidHostLabel").argv(Arrays.asList(Expr.ref(Identifier.of("Region")), Expr.of(true))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_203(), DefaultS3EndpointProvider.endpointRule_204(), DefaultS3EndpointProvider.endpointRule_206(), DefaultS3EndpointProvider.endpointRule_207(), DefaultS3EndpointProvider.endpointRule_208(), DefaultS3EndpointProvider.endpointRule_210(), DefaultS3EndpointProvider.endpointRule_211(), DefaultS3EndpointProvider.endpointRule_212(), DefaultS3EndpointProvider.endpointRule_214(), DefaultS3EndpointProvider.endpointRule_215(), DefaultS3EndpointProvider.endpointRule_216(), DefaultS3EndpointProvider.endpointRule_219(), DefaultS3EndpointProvider.endpointRule_220(), DefaultS3EndpointProvider.endpointRule_221(), DefaultS3EndpointProvider.endpointRule_224()));
    }

    private static Rule endpointRule_225() {
        return Rule.builder().error("Invalid region: region was not a valid DNS name.");
    }

    private static Rule endpointRule_201() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("aws.partition").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).result("partitionResult").build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_202(), DefaultS3EndpointProvider.endpointRule_225()));
    }

    private static Rule endpointRule_200() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("not").argv(Arrays.asList(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Bucket")))).build().validate())).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_201()));
    }

    private static Rule endpointRule_0() {
        return Rule.builder().addCondition(Condition.builder().fn(FnNode.builder().fn("isSet").argv(Arrays.asList(Expr.ref(Identifier.of("Region")))).build().validate()).build()).treeRule(Arrays.asList(DefaultS3EndpointProvider.endpointRule_1(), DefaultS3EndpointProvider.endpointRule_2(), DefaultS3EndpointProvider.endpointRule_3(), DefaultS3EndpointProvider.endpointRule_4(), DefaultS3EndpointProvider.endpointRule_5(), DefaultS3EndpointProvider.endpointRule_6(), DefaultS3EndpointProvider.endpointRule_20(), DefaultS3EndpointProvider.endpointRule_191(), DefaultS3EndpointProvider.endpointRule_200()));
    }

    private static Rule endpointRule_226() {
        return Rule.builder().error("A region must be set when sending requests to S3.");
    }

    private static EndpointRuleset ruleSet() {
        return EndpointRuleset.builder().version("1.0").serviceId(null).parameters(Parameters.builder().addParameter(Parameter.builder().name("Bucket").type(ParameterType.fromValue("String")).required(false).documentation("The S3 bucket used to send the request. This is an optional parameter that will be set automatically for operations that are scoped to an S3 bucket.").build()).addParameter(Parameter.builder().name("Region").type(ParameterType.fromValue("String")).required(false).builtIn("AWS::Region").documentation("The AWS region used to dispatch the request.").build()).addParameter(Parameter.builder().name("UseFIPS").type(ParameterType.fromValue("Boolean")).required(true).builtIn("AWS::UseFIPS").documentation("When true, send this request to the FIPS-compliant regional endpoint. If the configured endpoint does not have a FIPS compliant endpoint, dispatching the request will return an error.").defaultValue(Value.fromBool(false)).build()).addParameter(Parameter.builder().name("UseDualStack").type(ParameterType.fromValue("Boolean")).required(true).builtIn("AWS::UseDualStack").documentation("When true, use the dual-stack endpoint. If the configured endpoint does not support dual-stack, dispatching the request MAY return an error.").defaultValue(Value.fromBool(false)).build()).addParameter(Parameter.builder().name("Endpoint").type(ParameterType.fromValue("String")).required(false).builtIn("SDK::Endpoint").documentation("Override the endpoint used to send this request").build()).addParameter(Parameter.builder().name("ForcePathStyle").type(ParameterType.fromValue("Boolean")).required(true).builtIn("AWS::S3::ForcePathStyle").documentation("When true, force a path-style endpoint to be used where the bucket name is part of the path.").defaultValue(Value.fromBool(false)).build()).addParameter(Parameter.builder().name("Accelerate").type(ParameterType.fromValue("Boolean")).required(true).builtIn("AWS::S3::Accelerate").documentation("When true, use S3 Accelerate. NOTE: Not all regions support S3 accelerate.").defaultValue(Value.fromBool(false)).build()).addParameter(Parameter.builder().name("UseGlobalEndpoint").type(ParameterType.fromValue("Boolean")).required(true).builtIn("AWS::S3::UseGlobalEndpoint").documentation("Whether the global endpoint should be used, rather then the regional endpoint for us-east-1.").defaultValue(Value.fromBool(false)).build()).addParameter(Parameter.builder().name("UseObjectLambdaEndpoint").type(ParameterType.fromValue("Boolean")).required(false).documentation("Internal parameter to use object lambda endpoint for an operation (eg: WriteGetObjectResponse)").build()).addParameter(Parameter.builder().name("DisableAccessPoints").type(ParameterType.fromValue("Boolean")).required(false).documentation("Internal parameter to disable Access Point Buckets").build()).addParameter(Parameter.builder().name("DisableMultiRegionAccessPoints").type(ParameterType.fromValue("Boolean")).required(true).builtIn("AWS::S3::DisableMultiRegionAccessPoints").documentation("Whether multi-region access points (MRAP) should be disabled.").defaultValue(Value.fromBool(false)).build()).addParameter(Parameter.builder().name("UseArnRegion").type(ParameterType.fromValue("Boolean")).required(false).builtIn("AWS::S3::UseArnRegion").documentation("When an Access Point ARN is provided and this flag is enabled, the SDK MUST use the ARN's region when constructing the endpoint instead of the client's configured region.").build()).build()).addRule(DefaultS3EndpointProvider.endpointRule_0()).addRule(DefaultS3EndpointProvider.endpointRule_226()).build();
    }
}

