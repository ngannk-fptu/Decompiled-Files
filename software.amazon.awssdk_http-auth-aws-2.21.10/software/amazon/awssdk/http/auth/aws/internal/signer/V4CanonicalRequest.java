/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.utils.Pair
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.http.auth.aws.internal.signer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.utils.Pair;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
@Immutable
public final class V4CanonicalRequest {
    private static final List<String> HEADERS_TO_IGNORE_IN_LOWER_CASE = Arrays.asList("connection", "x-amzn-trace-id", "user-agent", "expect");
    private final SdkHttpRequest request;
    private final String contentHash;
    private final Options options;
    private String canonicalUri;
    private SortedMap<String, List<String>> canonicalParams;
    private List<Pair<String, List<String>>> canonicalHeaders;
    private String canonicalQueryString;
    private String canonicalHeadersString;
    private String signedHeadersString;
    private String canonicalRequestString;

    public V4CanonicalRequest(SdkHttpRequest request, String contentHash, Options options) {
        this.request = request;
        this.contentHash = contentHash;
        this.options = options;
    }

    public String getSignedHeadersString() {
        if (this.signedHeadersString == null) {
            this.signedHeadersString = V4CanonicalRequest.getSignedHeadersString(this.canonicalHeaders());
        }
        return this.signedHeadersString;
    }

    public String getCanonicalRequestString() {
        if (this.canonicalRequestString == null) {
            this.canonicalRequestString = V4CanonicalRequest.getCanonicalRequestString(this.request.method().toString(), this.canonicalUri(), this.canonicalQueryString(), this.canonicalHeadersString(), this.getSignedHeadersString(), this.contentHash);
        }
        return this.canonicalRequestString;
    }

    private SortedMap<String, List<String>> canonicalQueryParams() {
        if (this.canonicalParams == null) {
            this.canonicalParams = V4CanonicalRequest.getCanonicalQueryParams(this.request);
        }
        return this.canonicalParams;
    }

    private List<Pair<String, List<String>>> canonicalHeaders() {
        if (this.canonicalHeaders == null) {
            this.canonicalHeaders = V4CanonicalRequest.getCanonicalHeaders(this.request);
        }
        return this.canonicalHeaders;
    }

    private String canonicalUri() {
        if (this.canonicalUri == null) {
            this.canonicalUri = V4CanonicalRequest.getCanonicalUri(this.request, this.options);
        }
        return this.canonicalUri;
    }

    private String canonicalQueryString() {
        if (this.canonicalQueryString == null) {
            this.canonicalQueryString = V4CanonicalRequest.getCanonicalQueryString(this.canonicalQueryParams());
        }
        return this.canonicalQueryString;
    }

    private String canonicalHeadersString() {
        if (this.canonicalHeadersString == null) {
            this.canonicalHeadersString = V4CanonicalRequest.getCanonicalHeadersString(this.canonicalHeaders());
        }
        return this.canonicalHeadersString;
    }

    public static List<Pair<String, List<String>>> getCanonicalHeaders(SdkHttpRequest request) {
        ArrayList<Pair<String, List<String>>> result = new ArrayList<Pair<String, List<String>>>(request.numHeaders());
        request.forEachHeader((key, value) -> {
            String lowerCaseHeader = StringUtils.lowerCase((String)key);
            if (!HEADERS_TO_IGNORE_IN_LOWER_CASE.contains(lowerCaseHeader)) {
                result.add(Pair.of((Object)lowerCaseHeader, (Object)value));
            }
        });
        result.sort(Comparator.comparing(Pair::left));
        return result;
    }

    public static List<Pair<String, List<String>>> getCanonicalHeaders(Map<String, List<String>> headers) {
        ArrayList<Pair<String, List<String>>> result = new ArrayList<Pair<String, List<String>>>(headers.size());
        headers.forEach((key, value) -> {
            String lowerCaseHeader = StringUtils.lowerCase((String)key);
            if (!HEADERS_TO_IGNORE_IN_LOWER_CASE.contains(lowerCaseHeader)) {
                result.add(Pair.of((Object)lowerCaseHeader, (Object)value));
            }
        });
        result.sort(Comparator.comparing(Pair::left));
        return result;
    }

    public static String getCanonicalHeadersString(List<Pair<String, List<String>>> canonicalHeaders) {
        StringBuilder result = new StringBuilder(512);
        canonicalHeaders.forEach(header -> {
            result.append((String)header.left());
            result.append(":");
            for (String headerValue : (List)header.right()) {
                V4CanonicalRequest.addAndTrim(result, headerValue);
                result.append(",");
            }
            result.setLength(result.length() - 1);
            result.append("\n");
        });
        return result.toString();
    }

    public static String getSignedHeadersString(List<Pair<String, List<String>>> canonicalHeaders) {
        boolean trimTrailingSemicolon;
        StringBuilder headersString = new StringBuilder(512);
        for (Pair<String, List<String>> header : canonicalHeaders) {
            headersString.append((String)header.left()).append(";");
        }
        String signedHeadersString = headersString.toString();
        boolean bl = trimTrailingSemicolon = signedHeadersString.length() > 1 && signedHeadersString.endsWith(";");
        if (trimTrailingSemicolon) {
            signedHeadersString = signedHeadersString.substring(0, signedHeadersString.length() - 1);
        }
        return signedHeadersString;
    }

    private static String getCanonicalRequestString(String httpMethod, String canonicalUri, String canonicalParamsString, String canonicalHeadersString, String signedHeadersString, String contentHash) {
        return httpMethod + "\n" + canonicalUri + "\n" + canonicalParamsString + "\n" + canonicalHeadersString + "\n" + signedHeadersString + "\n" + contentHash;
    }

    private static void addAndTrim(StringBuilder result, String value) {
        int lengthBefore = result.length();
        boolean isStart = true;
        boolean previousIsWhiteSpace = false;
        for (int i = 0; i < value.length(); ++i) {
            char ch = value.charAt(i);
            if (V4CanonicalRequest.isWhiteSpace(ch)) {
                if (previousIsWhiteSpace || isStart) continue;
                result.append(' ');
                previousIsWhiteSpace = true;
                continue;
            }
            result.append(ch);
            isStart = false;
            previousIsWhiteSpace = false;
        }
        if (lengthBefore == result.length()) {
            return;
        }
        int lastNonWhitespaceChar = result.length() - 1;
        while (V4CanonicalRequest.isWhiteSpace(result.charAt(lastNonWhitespaceChar))) {
            --lastNonWhitespaceChar;
        }
        result.setLength(lastNonWhitespaceChar + 1);
    }

    private static String getCanonicalUri(SdkHttpRequest request, Options options) {
        boolean trimTrailingSlash;
        String path;
        String string = path = options.normalizePath ? request.getUri().normalize().getRawPath() : request.encodedPath();
        if (StringUtils.isEmpty((CharSequence)path)) {
            return "/";
        }
        if (options.doubleUrlEncode) {
            path = SdkHttpUtils.urlEncodeIgnoreSlashes((String)path);
        }
        if (!path.startsWith("/")) {
            path = path + "/";
        }
        boolean bl = trimTrailingSlash = options.normalizePath && path.length() > 1 && !request.getUri().getPath().endsWith("/") && path.charAt(path.length() - 1) == '/';
        if (trimTrailingSlash) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private static SortedMap<String, List<String>> getCanonicalQueryParams(SdkHttpRequest request) {
        TreeMap<String, List<String>> sorted = new TreeMap<String, List<String>>();
        request.forEachRawQueryParameter((key, values) -> {
            if (StringUtils.isEmpty((CharSequence)key)) {
                return;
            }
            String encodedParamName = SdkHttpUtils.urlEncode((String)key);
            ArrayList<String> encodedValues = new ArrayList<String>(values.size());
            for (String value : values) {
                String encodedValue = SdkHttpUtils.urlEncode((String)value);
                String signatureFormattedEncodedValue = encodedValue == null ? "" : encodedValue;
                encodedValues.add(signatureFormattedEncodedValue);
            }
            Collections.sort(encodedValues);
            sorted.put(encodedParamName, encodedValues);
        });
        return sorted;
    }

    private static String getCanonicalQueryString(SortedMap<String, List<String>> canonicalParams) {
        if (canonicalParams.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder(512);
        SdkHttpUtils.flattenQueryParameters((StringBuilder)stringBuilder, canonicalParams);
        return stringBuilder.toString();
    }

    private static boolean isWhiteSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\u000b' || ch == '\r' || ch == '\f';
    }

    public static class Options {
        final boolean doubleUrlEncode;
        final boolean normalizePath;

        public Options(boolean doubleUrlEncode, boolean normalizePath) {
            this.doubleUrlEncode = doubleUrlEncode;
            this.normalizePath = normalizePath;
        }
    }
}

