/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.core;

import java.net.URI;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.protocols.core.OperationInfo;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkProtectedApi
public final class ProtocolUtils {
    private ProtocolUtils() {
    }

    public static SdkHttpFullRequest.Builder createSdkHttpRequest(OperationInfo operationInfo, URI endpoint) {
        SdkHttpFullRequest.Builder request = SdkHttpFullRequest.builder().method(operationInfo.httpMethod()).uri(endpoint);
        return request.encodedPath(SdkHttpUtils.appendUri(request.encodedPath(), ProtocolUtils.addStaticQueryParametersToRequest(request, operationInfo.requestUri())));
    }

    @SdkTestInternalApi
    static String addStaticQueryParametersToRequest(SdkHttpFullRequest.Builder request, String uriResourcePath) {
        if (request == null || uriResourcePath == null) {
            return null;
        }
        String resourcePath = uriResourcePath;
        int index = resourcePath.indexOf(63);
        if (index != -1) {
            String queryString = resourcePath.substring(index + 1);
            resourcePath = resourcePath.substring(0, index);
            for (String s : queryString.split("[;&]")) {
                index = s.indexOf(61);
                if (index != -1) {
                    request.putRawQueryParameter(s.substring(0, index), s.substring(index + 1));
                    continue;
                }
                request.putRawQueryParameter(s, (String)null);
            }
        }
        return resourcePath;
    }
}

