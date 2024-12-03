/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.crt.http.HttpHeader
 *  software.amazon.awssdk.crt.http.HttpRequest
 *  software.amazon.awssdk.crt.http.HttpRequestBodyStream
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.http.auth.aws.crt.internal.util;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.crt.http.HttpHeader;
import software.amazon.awssdk.crt.http.HttpRequest;
import software.amazon.awssdk.crt.http.HttpRequestBodyStream;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.crt.internal.io.CrtInputStream;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public final class CrtHttpRequestConverter {
    private CrtHttpRequestConverter() {
    }

    public static HttpRequest toRequest(SdkHttpRequest request, ContentStreamProvider payload) {
        String method = request.method().name();
        String encodedPath = CrtHttpRequestConverter.encodedPathToCrtFormat(request.encodedPath());
        String encodedQueryString = request.encodedQueryParameters().map(value -> "?" + value).orElse("");
        HttpHeader[] crtHeaderArray = CrtHttpRequestConverter.createHttpHeaderArray(request);
        CrtInputStream crtInputStream = null;
        if (payload != null) {
            crtInputStream = new CrtInputStream(payload);
        }
        return new HttpRequest(method, encodedPath + encodedQueryString, crtHeaderArray, (HttpRequestBodyStream)crtInputStream);
    }

    public static SdkHttpRequest toRequest(SdkHttpRequest request, HttpRequest crtRequest) {
        int nextQuery;
        URI fullUri;
        SdkHttpRequest.Builder builder = (SdkHttpRequest.Builder)request.toBuilder();
        builder.clearHeaders();
        for (HttpHeader header : crtRequest.getHeaders()) {
            builder.appendHeader(header.getName(), header.getValue());
        }
        try {
            String portString = SdkHttpUtils.isUsingStandardPort((String)builder.protocol(), (Integer)builder.port()) ? "" : ":" + builder.port();
            String encodedPath = CrtHttpRequestConverter.encodedPathFromCrtFormat(request.encodedPath(), crtRequest.getEncodedPath());
            String fullUriString = builder.protocol() + "://" + builder.host() + portString + encodedPath;
            fullUri = new URI(fullUriString);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException("Full URI could not be formed.", e);
        }
        builder.encodedPath(fullUri.getRawPath());
        builder.clearQueryParameters();
        for (String remainingQuery = fullUri.getQuery(); remainingQuery != null && !remainingQuery.isEmpty(); remainingQuery = remainingQuery.substring(nextQuery + 1)) {
            String queryName;
            nextQuery = remainingQuery.indexOf(38);
            int nextAssign = remainingQuery.indexOf(61);
            if (nextAssign < nextQuery || nextAssign >= 0 && nextQuery < 0) {
                queryName = remainingQuery.substring(0, nextAssign);
                String queryValue = remainingQuery.substring(nextAssign + 1);
                if (nextQuery >= 0) {
                    queryValue = remainingQuery.substring(nextAssign + 1, nextQuery);
                }
                builder.appendRawQueryParameter(queryName, queryValue);
            } else {
                queryName = remainingQuery;
                if (nextQuery >= 0) {
                    queryName = remainingQuery.substring(0, nextQuery);
                }
                builder.appendRawQueryParameter(queryName, null);
            }
            if (nextQuery < 0) break;
        }
        return (SdkHttpRequest)builder.build();
    }

    private static HttpHeader[] createHttpHeaderArray(SdkHttpRequest request) {
        ArrayList<HttpHeader> crtHeaderList = new ArrayList<HttpHeader>(request.numHeaders() + 2);
        if (!request.firstMatchingHeader("Host").isPresent()) {
            crtHeaderList.add(new HttpHeader("Host", request.host()));
        }
        request.forEachHeader((name, values) -> {
            for (String val : values) {
                HttpHeader h = new HttpHeader(name, val);
                crtHeaderList.add(h);
            }
        });
        return crtHeaderList.toArray(new HttpHeader[0]);
    }

    private static String encodedPathToCrtFormat(String sdkEncodedPath) {
        if (StringUtils.isEmpty((CharSequence)sdkEncodedPath)) {
            return "/";
        }
        return sdkEncodedPath;
    }

    private static String encodedPathFromCrtFormat(String sdkEncodedPath, String crtEncodedPath) {
        if ("/".equals(crtEncodedPath) && StringUtils.isEmpty((CharSequence)sdkEncodedPath)) {
            return "";
        }
        return crtEncodedPath;
    }

    public static HttpRequestBodyStream toCrtStream(byte[] data) {
        return new CrtInputStream(() -> new ByteArrayInputStream(data));
    }
}

