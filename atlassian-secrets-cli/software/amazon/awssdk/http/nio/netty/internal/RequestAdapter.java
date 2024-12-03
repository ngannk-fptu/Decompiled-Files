/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http2.HttpConversionUtil;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public final class RequestAdapter {
    private static final String HOST = "Host";
    private static final List<String> IGNORE_HEADERS = Collections.singletonList("Host");
    private final Protocol protocol;

    public RequestAdapter(Protocol protocol) {
        this.protocol = Validate.paramNotNull(protocol, "protocol");
    }

    public HttpRequest adapt(SdkHttpRequest sdkRequest) {
        HttpMethod method = RequestAdapter.toNettyHttpMethod(sdkRequest.method());
        DefaultHttpHeaders headers = new DefaultHttpHeaders();
        String uri = RequestAdapter.encodedPathAndQueryParams(sdkRequest);
        DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, uri, headers);
        this.addHeadersToRequest(request, sdkRequest);
        return request;
    }

    private static HttpMethod toNettyHttpMethod(SdkHttpMethod method) {
        return HttpMethod.valueOf(method.name());
    }

    private static String encodedPathAndQueryParams(SdkHttpRequest sdkRequest) {
        String encodedPath = sdkRequest.encodedPath();
        if (StringUtils.isBlank(encodedPath)) {
            encodedPath = "/";
        }
        String encodedQueryParams = sdkRequest.encodedQueryParameters().map(queryParams -> "?" + queryParams).orElse("");
        return encodedPath + encodedQueryParams;
    }

    private void addHeadersToRequest(DefaultHttpRequest httpRequest, SdkHttpRequest request) {
        httpRequest.headers().add(HOST, (Object)this.getHostHeaderValue(request));
        String scheme = request.getUri().getScheme();
        if (Protocol.HTTP2 == this.protocol && !StringUtils.isBlank(scheme)) {
            httpRequest.headers().add((CharSequence)HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), (Object)scheme);
        }
        request.forEachHeader((name, value) -> {
            if (!IGNORE_HEADERS.contains(name)) {
                value.forEach(h -> httpRequest.headers().add((String)name, h));
            }
        });
    }

    private String getHostHeaderValue(SdkHttpRequest request) {
        return SdkHttpUtils.isUsingStandardPort(request.protocol(), request.port()) ? request.host() : request.host() + ":" + request.port();
    }
}

