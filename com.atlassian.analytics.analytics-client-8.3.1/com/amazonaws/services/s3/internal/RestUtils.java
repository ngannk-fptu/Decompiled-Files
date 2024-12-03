/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.SignableRequest;
import com.amazonaws.util.StringUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RestUtils {
    private static final List<String> SIGNED_PARAMETERS = Arrays.asList("acl", "torrent", "logging", "location", "policy", "requestPayment", "versioning", "versions", "versionId", "notification", "uploadId", "uploads", "partNumber", "website", "delete", "lifecycle", "tagging", "cors", "restore", "replication", "accelerate", "inventory", "analytics", "metrics", "response-cache-control", "response-content-disposition", "response-content-encoding", "response-content-language", "response-content-type", "response-expires");

    public static <T> String makeS3CanonicalString(String method, String resource, SignableRequest<T> request, String expires) {
        return RestUtils.makeS3CanonicalString(method, resource, request, expires, null);
    }

    /*
     * WARNING - void declaration
     */
    public static <T> String makeS3CanonicalString(String method, String resource, SignableRequest<T> request, String expires, Collection<String> additionalQueryParamsToSign) {
        void var10_19;
        StringBuilder buf = new StringBuilder();
        buf.append(method + "\n");
        Map<String, String> headersMap = request.getHeaders();
        TreeMap<String, String> interestingHeaders = new TreeMap<String, String>();
        if (headersMap != null && headersMap.size() > 0) {
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                Object lk;
                String string = entry.getKey();
                String value = entry.getValue();
                if (string == null || !((String)(lk = StringUtils.lowerCase(string))).equals("content-type") && !((String)lk).equals("content-md5") && !((String)lk).equals("date") && !((String)lk).startsWith("x-amz-")) continue;
                interestingHeaders.put((String)lk, value);
            }
        }
        if (interestingHeaders.containsKey("x-amz-date")) {
            interestingHeaders.put("date", "");
        }
        if (expires != null) {
            interestingHeaders.put("date", expires);
        }
        if (!interestingHeaders.containsKey("content-type")) {
            interestingHeaders.put("content-type", "");
        }
        if (!interestingHeaders.containsKey("content-md5")) {
            interestingHeaders.put("content-md5", "");
        }
        Map<String, List<String>> requestParameters = request.getParameters();
        for (Map.Entry<String, List<String>> entry : requestParameters.entrySet()) {
            if (!entry.getKey().startsWith("x-amz-")) continue;
            StringBuilder parameterValueBuilder = new StringBuilder();
            for (String value : entry.getValue()) {
                if (parameterValueBuilder.length() > 0) {
                    parameterValueBuilder.append(",");
                }
                parameterValueBuilder.append(value);
            }
            interestingHeaders.put(entry.getKey(), parameterValueBuilder.toString());
        }
        for (Map.Entry entry : interestingHeaders.entrySet()) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            if (key.startsWith("x-amz-")) {
                buf.append(key).append(':');
                if (value != null) {
                    buf.append(value);
                }
            } else if (value != null) {
                buf.append(value);
            }
            buf.append("\n");
        }
        buf.append(resource);
        Object[] objectArray = requestParameters.keySet().toArray(new String[request.getParameters().size()]);
        Arrays.sort(objectArray);
        StringBuilder stringBuilder = new StringBuilder();
        for (Object parameterName : objectArray) {
            if (!SIGNED_PARAMETERS.contains(parameterName) && (additionalQueryParamsToSign == null || !additionalQueryParamsToSign.contains(parameterName))) continue;
            List<String> values = requestParameters.get(parameterName);
            for (String value : values) {
                StringBuilder stringBuilder2 = var10_19.length() > 0 ? var10_19.append("&") : var10_19.append("?");
                stringBuilder2.append((String)parameterName);
                if (value == null) continue;
                stringBuilder2.append("=").append(value);
            }
        }
        buf.append(var10_19.toString());
        return buf.toString();
    }
}

