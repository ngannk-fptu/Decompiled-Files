/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HierarchicalUriComponents;

public abstract class UriUtils {
    public static String encodeScheme(String scheme, String encoding) {
        return UriUtils.encode(scheme, encoding, HierarchicalUriComponents.Type.SCHEME);
    }

    public static String encodeScheme(String scheme, Charset charset) {
        return UriUtils.encode(scheme, charset, HierarchicalUriComponents.Type.SCHEME);
    }

    public static String encodeAuthority(String authority, String encoding) {
        return UriUtils.encode(authority, encoding, HierarchicalUriComponents.Type.AUTHORITY);
    }

    public static String encodeAuthority(String authority, Charset charset) {
        return UriUtils.encode(authority, charset, HierarchicalUriComponents.Type.AUTHORITY);
    }

    public static String encodeUserInfo(String userInfo, String encoding) {
        return UriUtils.encode(userInfo, encoding, HierarchicalUriComponents.Type.USER_INFO);
    }

    public static String encodeUserInfo(String userInfo, Charset charset) {
        return UriUtils.encode(userInfo, charset, HierarchicalUriComponents.Type.USER_INFO);
    }

    public static String encodeHost(String host, String encoding) {
        return UriUtils.encode(host, encoding, HierarchicalUriComponents.Type.HOST_IPV4);
    }

    public static String encodeHost(String host, Charset charset) {
        return UriUtils.encode(host, charset, HierarchicalUriComponents.Type.HOST_IPV4);
    }

    public static String encodePort(String port, String encoding) {
        return UriUtils.encode(port, encoding, HierarchicalUriComponents.Type.PORT);
    }

    public static String encodePort(String port, Charset charset) {
        return UriUtils.encode(port, charset, HierarchicalUriComponents.Type.PORT);
    }

    public static String encodePath(String path, String encoding) {
        return UriUtils.encode(path, encoding, HierarchicalUriComponents.Type.PATH);
    }

    public static String encodePath(String path, Charset charset) {
        return UriUtils.encode(path, charset, HierarchicalUriComponents.Type.PATH);
    }

    public static String encodePathSegment(String segment, String encoding) {
        return UriUtils.encode(segment, encoding, HierarchicalUriComponents.Type.PATH_SEGMENT);
    }

    public static String encodePathSegment(String segment, Charset charset) {
        return UriUtils.encode(segment, charset, HierarchicalUriComponents.Type.PATH_SEGMENT);
    }

    public static String encodeQuery(String query, String encoding) {
        return UriUtils.encode(query, encoding, HierarchicalUriComponents.Type.QUERY);
    }

    public static String encodeQuery(String query, Charset charset) {
        return UriUtils.encode(query, charset, HierarchicalUriComponents.Type.QUERY);
    }

    public static String encodeQueryParam(String queryParam, String encoding) {
        return UriUtils.encode(queryParam, encoding, HierarchicalUriComponents.Type.QUERY_PARAM);
    }

    public static String encodeQueryParam(String queryParam, Charset charset) {
        return UriUtils.encode(queryParam, charset, HierarchicalUriComponents.Type.QUERY_PARAM);
    }

    public static MultiValueMap<String, String> encodeQueryParams(MultiValueMap<String, String> params) {
        Charset charset = StandardCharsets.UTF_8;
        LinkedMultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>(params.size());
        for (Map.Entry entry : params.entrySet()) {
            for (String value : (List)entry.getValue()) {
                result.add(UriUtils.encodeQueryParam((String)entry.getKey(), charset), UriUtils.encodeQueryParam(value, charset));
            }
        }
        return result;
    }

    public static String encodeFragment(String fragment, String encoding) {
        return UriUtils.encode(fragment, encoding, HierarchicalUriComponents.Type.FRAGMENT);
    }

    public static String encodeFragment(String fragment, Charset charset) {
        return UriUtils.encode(fragment, charset, HierarchicalUriComponents.Type.FRAGMENT);
    }

    public static String encode(String source, String encoding) {
        return UriUtils.encode(source, encoding, HierarchicalUriComponents.Type.URI);
    }

    public static String encode(String source, Charset charset) {
        return UriUtils.encode(source, charset, HierarchicalUriComponents.Type.URI);
    }

    public static Map<String, String> encodeUriVariables(Map<String, ?> uriVariables) {
        LinkedHashMap<String, String> result = CollectionUtils.newLinkedHashMap(uriVariables.size());
        uriVariables.forEach((key, value) -> {
            String stringValue = value != null ? value.toString() : "";
            result.put((String)key, UriUtils.encode(stringValue, StandardCharsets.UTF_8));
        });
        return result;
    }

    public static Object[] encodeUriVariables(Object ... uriVariables) {
        return Arrays.stream(uriVariables).map(value -> {
            String stringValue = value != null ? value.toString() : "";
            return UriUtils.encode(stringValue, StandardCharsets.UTF_8);
        }).toArray();
    }

    private static String encode(String scheme, String encoding, HierarchicalUriComponents.Type type) {
        return HierarchicalUriComponents.encodeUriComponent(scheme, encoding, type);
    }

    private static String encode(String scheme, Charset charset, HierarchicalUriComponents.Type type) {
        return HierarchicalUriComponents.encodeUriComponent(scheme, charset, type);
    }

    public static String decode(String source, String encoding) {
        return StringUtils.uriDecode(source, Charset.forName(encoding));
    }

    public static String decode(String source, Charset charset) {
        return StringUtils.uriDecode(source, charset);
    }

    @Nullable
    public static String extractFileExtension(String path) {
        int begin;
        int paramIndex;
        int extIndex;
        int end = path.indexOf(63);
        int fragmentIndex = path.indexOf(35);
        if (fragmentIndex != -1 && (end == -1 || fragmentIndex < end)) {
            end = fragmentIndex;
        }
        if (end == -1) {
            end = path.length();
        }
        if ((extIndex = path.lastIndexOf(46, end = (paramIndex = path.indexOf(59, begin = path.lastIndexOf(47, end) + 1)) != -1 && paramIndex < end ? paramIndex : end)) != -1 && extIndex >= begin) {
            return path.substring(extIndex + 1, end);
        }
        return null;
    }
}

