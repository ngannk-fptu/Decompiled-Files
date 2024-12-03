/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.utils.Pair
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.core.internal.handler;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import java.util.function.BiFunction;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.CredentialType;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptorChain;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.internal.InternalCoreExecutionAttribute;
import software.amazon.awssdk.core.internal.io.SdkLengthAwareInputStream;
import software.amazon.awssdk.core.internal.util.MetricUtils;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.utils.Pair;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public abstract class BaseClientHandler {
    private SdkClientConfiguration clientConfiguration;

    protected BaseClientHandler(SdkClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    static <InputT extends SdkRequest, OutputT> InterceptorContext finalizeSdkHttpFullRequest(ClientExecutionParams<InputT, OutputT> executionParams, ExecutionContext executionContext, InputT inputT, SdkClientConfiguration clientConfiguration) {
        BaseClientHandler.runBeforeMarshallingInterceptors(executionContext);
        Pair<SdkHttpFullRequest, Duration> measuredMarshall = MetricUtils.measureDuration(() -> executionParams.getMarshaller().marshall(inputT));
        executionContext.metricCollector().reportMetric(CoreMetric.MARSHALLING_DURATION, measuredMarshall.right());
        SdkHttpFullRequest request = (SdkHttpFullRequest)measuredMarshall.left();
        request = BaseClientHandler.modifyEndpointHostIfNeeded(request, clientConfiguration, executionParams);
        BaseClientHandler.addHttpRequest(executionContext, request);
        BaseClientHandler.runAfterMarshallingInterceptors(executionContext);
        return BaseClientHandler.runModifyHttpRequestAndHttpContentInterceptors(executionContext);
    }

    private static void runBeforeMarshallingInterceptors(ExecutionContext executionContext) {
        executionContext.interceptorChain().beforeMarshalling(executionContext.interceptorContext(), executionContext.executionAttributes());
    }

    private static SdkHttpFullRequest modifyEndpointHostIfNeeded(SdkHttpFullRequest originalRequest, SdkClientConfiguration clientConfiguration, ClientExecutionParams executionParams) {
        if (executionParams.discoveredEndpoint() != null) {
            URI discoveredEndpoint = executionParams.discoveredEndpoint();
            executionParams.putExecutionAttribute(SdkInternalExecutionAttribute.IS_DISCOVERED_ENDPOINT, true);
            return originalRequest.toBuilder().host(discoveredEndpoint.getHost()).port(Integer.valueOf(discoveredEndpoint.getPort())).build();
        }
        Boolean disableHostPrefixInjection = clientConfiguration.option(SdkAdvancedClientOption.DISABLE_HOST_PREFIX_INJECTION);
        if (disableHostPrefixInjection != null && disableHostPrefixInjection.equals(Boolean.TRUE) || StringUtils.isEmpty((CharSequence)executionParams.hostPrefixExpression())) {
            return originalRequest;
        }
        return originalRequest.toBuilder().host(executionParams.hostPrefixExpression() + originalRequest.host()).build();
    }

    private static void addHttpRequest(ExecutionContext executionContext, SdkHttpFullRequest request) {
        InterceptorContext interceptorContext = executionContext.interceptorContext();
        Optional contentStreamProvider = request.contentStreamProvider();
        interceptorContext = contentStreamProvider.isPresent() ? (InterceptorContext)interceptorContext.copy(b -> b.httpRequest((SdkHttpRequest)request).requestBody(BaseClientHandler.getBody(request))) : (InterceptorContext)interceptorContext.copy(b -> b.httpRequest((SdkHttpRequest)request));
        executionContext.interceptorContext(interceptorContext);
    }

    private static RequestBody getBody(SdkHttpFullRequest request) {
        Optional contentStreamProviderOptional = request.contentStreamProvider();
        if (contentStreamProviderOptional.isPresent()) {
            Optional contentLengthOptional = request.firstMatchingHeader("Content-Length");
            long contentLength = Long.parseLong(contentLengthOptional.orElse("0"));
            String contentType = request.firstMatchingHeader("Content-Type").orElse("");
            ContentStreamProvider streamProvider = (ContentStreamProvider)contentStreamProviderOptional.get();
            if (contentLengthOptional.isPresent()) {
                ContentStreamProvider toWrap = (ContentStreamProvider)contentStreamProviderOptional.get();
                streamProvider = () -> new SdkLengthAwareInputStream(toWrap.newStream(), contentLength);
            }
            return RequestBody.fromContentProvider(streamProvider, contentLength, contentType);
        }
        return null;
    }

    private static void runAfterMarshallingInterceptors(ExecutionContext executionContext) {
        executionContext.interceptorChain().afterMarshalling(executionContext.interceptorContext(), executionContext.executionAttributes());
    }

    private static InterceptorContext runModifyHttpRequestAndHttpContentInterceptors(ExecutionContext executionContext) {
        InterceptorContext interceptorContext = executionContext.interceptorChain().modifyHttpRequestAndHttpContent(executionContext.interceptorContext(), executionContext.executionAttributes());
        executionContext.interceptorContext(interceptorContext);
        return interceptorContext;
    }

    private static <OutputT extends SdkResponse> BiFunction<OutputT, SdkHttpFullResponse, OutputT> runAfterUnmarshallingInterceptors(ExecutionContext context) {
        return (input, httpFullResponse) -> {
            InterceptorContext interceptorContext = (InterceptorContext)context.interceptorContext().copy(b -> b.response((SdkResponse)input));
            context.interceptorChain().afterUnmarshalling(interceptorContext, context.executionAttributes());
            interceptorContext = context.interceptorChain().modifyResponse(interceptorContext, context.executionAttributes());
            context.interceptorContext(interceptorContext);
            return interceptorContext.response();
        };
    }

    private static <OutputT extends SdkResponse> BiFunction<OutputT, SdkHttpFullResponse, OutputT> attachHttpResponseToResult() {
        return (response, httpFullResponse) -> response.toBuilder().sdkHttpResponse((SdkHttpResponse)httpFullResponse).build();
    }

    protected <InputT extends SdkRequest, OutputT extends SdkResponse> ExecutionContext invokeInterceptorsAndCreateExecutionContext(ClientExecutionParams<InputT, OutputT> params) {
        SdkClientConfiguration clientConfiguration = this.resolveRequestConfiguration(params);
        InputT originalRequest = params.getInput();
        ExecutionAttributes executionAttributes = params.executionAttributes();
        executionAttributes.putAttribute(InternalCoreExecutionAttribute.EXECUTION_ATTEMPT, 1).putAttribute(SdkExecutionAttribute.SERVICE_CONFIG, clientConfiguration.option(SdkClientOption.SERVICE_CONFIGURATION)).putAttribute(SdkExecutionAttribute.SERVICE_NAME, clientConfiguration.option(SdkClientOption.SERVICE_NAME)).putAttribute(SdkExecutionAttribute.PROFILE_FILE, clientConfiguration.option(SdkClientOption.PROFILE_FILE_SUPPLIER) != null ? clientConfiguration.option(SdkClientOption.PROFILE_FILE_SUPPLIER).get() : null).putAttribute(SdkExecutionAttribute.PROFILE_FILE_SUPPLIER, clientConfiguration.option(SdkClientOption.PROFILE_FILE_SUPPLIER)).putAttribute(SdkExecutionAttribute.PROFILE_NAME, clientConfiguration.option(SdkClientOption.PROFILE_NAME));
        ExecutionInterceptorChain interceptorChain = new ExecutionInterceptorChain(clientConfiguration.option(SdkClientOption.EXECUTION_INTERCEPTORS));
        InterceptorContext interceptorContext = InterceptorContext.builder().request((SdkRequest)originalRequest).build();
        interceptorChain.beforeExecution(interceptorContext, executionAttributes);
        interceptorContext = interceptorChain.modifyRequest(interceptorContext, executionAttributes);
        MetricCollector metricCollector = this.resolveMetricCollector(params);
        return ExecutionContext.builder().interceptorChain(interceptorChain).interceptorContext(interceptorContext).executionAttributes(executionAttributes).signer(clientConfiguration.option(SdkAdvancedClientOption.SIGNER)).metricCollector(metricCollector).build();
    }

    protected boolean isCalculateCrc32FromCompressedData() {
        return this.clientConfiguration.option(SdkClientOption.CRC32_FROM_COMPRESSED_DATA_ENABLED);
    }

    protected void validateSigningConfiguration(SdkHttpRequest request, Signer signer) {
        if (signer == null) {
            return;
        }
        if (signer.credentialType() != CredentialType.TOKEN) {
            return;
        }
        URI endpoint = request.getUri();
        if (!"https".equals(endpoint.getScheme())) {
            throw SdkClientException.create("Cannot use bearer token signer with a plaintext HTTP endpoint: " + endpoint);
        }
    }

    protected SdkClientConfiguration resolveRequestConfiguration(ClientExecutionParams<?, ?> params) {
        SdkClientConfiguration config = params.requestConfiguration();
        if (config != null) {
            return config;
        }
        return this.clientConfiguration;
    }

    <OutputT extends SdkResponse> HttpResponseHandler<OutputT> decorateResponseHandlers(HttpResponseHandler<OutputT> delegate, ExecutionContext executionContext) {
        return this.resultTransformationResponseHandler(delegate, BaseClientHandler.responseTransformations(executionContext));
    }

    <OutputT extends SdkResponse> HttpResponseHandler<Response<OutputT>> decorateSuccessResponseHandlers(HttpResponseHandler<Response<OutputT>> delegate, ExecutionContext executionContext) {
        return this.successTransformationResponseHandler(delegate, BaseClientHandler.responseTransformations(executionContext));
    }

    <OutputT extends SdkResponse> HttpResponseHandler<Response<OutputT>> successTransformationResponseHandler(HttpResponseHandler<Response<OutputT>> responseHandler, BiFunction<OutputT, SdkHttpFullResponse, OutputT> successTransformer) {
        return (response, executionAttributes) -> {
            Response delegateResponse = (Response)responseHandler.handle(response, executionAttributes);
            if (delegateResponse.isSuccess().booleanValue()) {
                return delegateResponse.toBuilder().response(successTransformer.apply(delegateResponse.response(), response)).build();
            }
            return delegateResponse;
        };
    }

    <OutputT extends SdkResponse> HttpResponseHandler<OutputT> resultTransformationResponseHandler(HttpResponseHandler<OutputT> responseHandler, BiFunction<OutputT, SdkHttpFullResponse, OutputT> successTransformer) {
        return (response, executionAttributes) -> {
            SdkResponse delegateResponse = (SdkResponse)responseHandler.handle(response, executionAttributes);
            return (SdkResponse)successTransformer.apply(delegateResponse, response);
        };
    }

    static void validateCombinedResponseHandler(ClientExecutionParams<?, ?> executionParams) {
        if (executionParams.getCombinedResponseHandler() != null) {
            if (executionParams.getResponseHandler() != null) {
                throw new IllegalArgumentException("Only one of 'combinedResponseHandler' and 'responseHandler' may be specified in a ClientExecutionParams object");
            }
            if (executionParams.getErrorResponseHandler() != null) {
                throw new IllegalArgumentException("Only one of 'combinedResponseHandler' and 'errorResponseHandler' may be specified in a ClientExecutionParams object");
            }
        }
    }

    private static <T extends SdkResponse> BiFunction<T, SdkHttpFullResponse, T> responseTransformations(ExecutionContext executionContext) {
        return BaseClientHandler.composeResponseFunctions(BaseClientHandler.runAfterUnmarshallingInterceptors(executionContext), BaseClientHandler.attachHttpResponseToResult());
    }

    private static <T, R> BiFunction<T, R, T> composeResponseFunctions(BiFunction<T, R, T> function1, BiFunction<T, R, T> function2) {
        return (x, y) -> function2.apply(function1.apply(x, y), y);
    }

    private MetricCollector resolveMetricCollector(ClientExecutionParams<?, ?> params) {
        MetricCollector metricCollector = params.getMetricCollector();
        if (metricCollector == null) {
            metricCollector = MetricCollector.create((String)"ApiCall");
        }
        return metricCollector;
    }
}

