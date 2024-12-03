/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.auth.signer.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public final class HeaderTransformsHelper {
    private static final List<String> LIST_OF_HEADERS_TO_IGNORE_IN_LOWER_CASE = Arrays.asList("connection", "x-amzn-trace-id", "user-agent", "expect");

    private HeaderTransformsHelper() {
    }

    public static Map<String, List<String>> canonicalizeSigningHeaders(Map<String, List<String>> headers) {
        TreeMap<String, List<String>> result = new TreeMap<String, List<String>>();
        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            String lowerCaseHeader = StringUtils.lowerCase((String)header.getKey());
            if (LIST_OF_HEADERS_TO_IGNORE_IN_LOWER_CASE.contains(lowerCaseHeader)) continue;
            result.computeIfAbsent(lowerCaseHeader, x -> new ArrayList()).addAll((Collection)header.getValue());
        }
        return result;
    }

    public static String trimAll(String value) {
        boolean previousIsWhiteSpace = false;
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); ++i) {
            char ch = value.charAt(i);
            if (HeaderTransformsHelper.isWhiteSpace(ch)) {
                if (previousIsWhiteSpace) continue;
                sb.append(' ');
                previousIsWhiteSpace = true;
                continue;
            }
            sb.append(ch);
            previousIsWhiteSpace = false;
        }
        return sb.toString().trim();
    }

    private static List<String> trimAll(List<String> values) {
        return values.stream().map(HeaderTransformsHelper::trimAll).collect(Collectors.toList());
    }

    private static boolean isWhiteSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\u000b' || ch == '\r' || ch == '\f';
    }

    public static String getCanonicalizedHeaderString(Map<String, List<String>> canonicalizedHeaders) {
        StringBuilder buffer = new StringBuilder();
        canonicalizedHeaders.forEach((headerName, headerValues) -> {
            buffer.append((String)headerName);
            buffer.append(":");
            buffer.append(String.join((CharSequence)",", HeaderTransformsHelper.trimAll(headerValues)));
            buffer.append("\n");
        });
        return buffer.toString();
    }
}

