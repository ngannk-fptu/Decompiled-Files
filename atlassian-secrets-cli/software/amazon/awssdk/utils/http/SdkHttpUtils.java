/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.http;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.ProxyEnvironmentSetting;
import software.amazon.awssdk.utils.ProxySystemSetting;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public final class SdkHttpUtils {
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String[] ENCODED_CHARACTERS_WITH_SLASHES = new String[]{"+", "*", "%7E", "%2F"};
    private static final String[] ENCODED_CHARACTERS_WITH_SLASHES_REPLACEMENTS = new String[]{"%20", "%2A", "~", "/"};
    private static final String[] ENCODED_CHARACTERS_WITHOUT_SLASHES = new String[]{"+", "*", "%7E"};
    private static final String[] ENCODED_CHARACTERS_WITHOUT_SLASHES_REPLACEMENTS = new String[]{"%20", "%2A", "~"};
    private static final Set<String> SINGLE_HEADERS = Stream.of("age", "authorization", "content-length", "content-location", "content-md5", "content-range", "content-type", "date", "etag", "expires", "from", "host", "if-modified-since", "if-range", "if-unmodified-since", "last-modified", "location", "max-forwards", "proxy-authorization", "range", "referer", "retry-after", "server", "user-agent").collect(Collectors.toSet());

    private SdkHttpUtils() {
    }

    public static String urlEncode(String value) {
        return SdkHttpUtils.urlEncode(value, false);
    }

    public static String urlEncodeIgnoreSlashes(String value) {
        return SdkHttpUtils.urlEncode(value, true);
    }

    public static String formDataEncode(String value) {
        return value == null ? null : FunctionalUtils.invokeSafely(() -> URLEncoder.encode(value, DEFAULT_ENCODING));
    }

    public static String urlDecode(String value) {
        if (value == null) {
            return null;
        }
        try {
            return URLDecoder.decode(value, DEFAULT_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to decode value", e);
        }
    }

    public static Map<String, List<String>> encodeQueryParameters(Map<String, List<String>> rawQueryParameters) {
        return SdkHttpUtils.encodeMapOfLists(rawQueryParameters, SdkHttpUtils::urlEncode);
    }

    public static Map<String, List<String>> encodeFormData(Map<String, List<String>> rawFormData) {
        return SdkHttpUtils.encodeMapOfLists(rawFormData, SdkHttpUtils::formDataEncode);
    }

    private static Map<String, List<String>> encodeMapOfLists(Map<String, List<String>> map, UnaryOperator<String> encoder) {
        Validate.notNull(map, "Map must not be null.", new Object[0]);
        LinkedHashMap<String, List<String>> result = new LinkedHashMap<String, List<String>>();
        for (Map.Entry<String, List<String>> queryParameter : map.entrySet()) {
            String key = queryParameter.getKey();
            String encodedKey = (String)encoder.apply(key);
            List<String> value = queryParameter.getValue();
            List encodedValue = value == null ? null : queryParameter.getValue().stream().map(encoder).collect(Collectors.toList());
            result.put(encodedKey, encodedValue);
        }
        return result;
    }

    private static String urlEncode(String value, boolean ignoreSlashes) {
        if (value == null) {
            return null;
        }
        String encoded = FunctionalUtils.invokeSafely(() -> URLEncoder.encode(value, DEFAULT_ENCODING));
        if (!ignoreSlashes) {
            return StringUtils.replaceEach(encoded, ENCODED_CHARACTERS_WITHOUT_SLASHES, ENCODED_CHARACTERS_WITHOUT_SLASHES_REPLACEMENTS);
        }
        return StringUtils.replaceEach(encoded, ENCODED_CHARACTERS_WITH_SLASHES, ENCODED_CHARACTERS_WITH_SLASHES_REPLACEMENTS);
    }

    public static Optional<String> encodeAndFlattenQueryParameters(Map<String, List<String>> rawQueryParameters) {
        return SdkHttpUtils.encodeAndFlatten(rawQueryParameters, SdkHttpUtils::urlEncode);
    }

    public static Optional<String> encodeAndFlattenFormData(Map<String, List<String>> rawFormData) {
        return SdkHttpUtils.encodeAndFlatten(rawFormData, SdkHttpUtils::formDataEncode);
    }

    private static Optional<String> encodeAndFlatten(Map<String, List<String>> data, UnaryOperator<String> encoder) {
        Validate.notNull(data, "Map must not be null.", new Object[0]);
        if (data.isEmpty()) {
            return Optional.empty();
        }
        StringBuilder queryString = new StringBuilder();
        data.forEach((key, values) -> {
            String encodedKey = (String)encoder.apply((String)key);
            if (values != null) {
                values.forEach(value -> {
                    if (queryString.length() > 0) {
                        queryString.append('&');
                    }
                    queryString.append(encodedKey);
                    if (value != null) {
                        queryString.append('=').append((String)encoder.apply((String)value));
                    }
                });
            }
        });
        return Optional.of(queryString.toString());
    }

    public static Optional<String> flattenQueryParameters(Map<String, List<String>> toFlatten) {
        if (toFlatten.isEmpty()) {
            return Optional.empty();
        }
        StringBuilder result = new StringBuilder();
        SdkHttpUtils.flattenQueryParameters(result, toFlatten);
        return Optional.of(result.toString());
    }

    public static void flattenQueryParameters(StringBuilder result, Map<String, List<String>> toFlatten) {
        if (toFlatten.isEmpty()) {
            return;
        }
        boolean first = true;
        for (Map.Entry<String, List<String>> encodedQueryParameter : toFlatten.entrySet()) {
            String key = encodedQueryParameter.getKey();
            List values = Optional.ofNullable(encodedQueryParameter.getValue()).orElseGet(Collections::emptyList);
            for (String value : values) {
                if (!first) {
                    result.append('&');
                } else {
                    first = false;
                }
                result.append(key);
                if (value == null) continue;
                result.append('=');
                result.append(value);
            }
        }
    }

    public static boolean isUsingStandardPort(String protocol, Integer port) {
        Validate.paramNotNull(protocol, "protocol");
        Validate.isTrue(protocol.equals("http") || protocol.equals("https"), "Protocol must be 'http' or 'https', but was '%s'.", protocol);
        String scheme = StringUtils.lowerCase(protocol);
        return port == null || port == -1 || scheme.equals("http") && port == 80 || scheme.equals("https") && port == 443;
    }

    public static int standardPort(String protocol) {
        if (protocol.equalsIgnoreCase("http")) {
            return 80;
        }
        if (protocol.equalsIgnoreCase("https")) {
            return 443;
        }
        throw new IllegalArgumentException("Unknown protocol: " + protocol);
    }

    public static String appendUri(String baseUri, String path) {
        Validate.paramNotNull(baseUri, "baseUri");
        StringBuilder resultUri = new StringBuilder(baseUri);
        if (!StringUtils.isEmpty(path)) {
            if (!baseUri.endsWith("/")) {
                resultUri.append("/");
            }
            resultUri.append(path.startsWith("/") ? path.substring(1) : path);
        }
        return resultUri.toString();
    }

    @Deprecated
    public static Stream<String> allMatchingHeaders(Map<String, List<String>> headers, String header) {
        return headers.entrySet().stream().filter(e -> ((String)e.getKey()).equalsIgnoreCase(header)).flatMap(e -> e.getValue() != null ? ((List)e.getValue()).stream() : Stream.empty());
    }

    @Deprecated
    public static Stream<String> allMatchingHeadersFromCollection(Map<String, List<String>> headersToSearch, Collection<String> headersToFind) {
        return headersToSearch.entrySet().stream().filter(e -> headersToFind.stream().anyMatch(headerToFind -> ((String)e.getKey()).equalsIgnoreCase((String)headerToFind))).flatMap(e -> e.getValue() != null ? ((List)e.getValue()).stream() : Stream.empty());
    }

    @Deprecated
    public static Optional<String> firstMatchingHeader(Map<String, List<String>> headers, String header) {
        for (Map.Entry<String, List<String>> headerEntry : headers.entrySet()) {
            if (!headerEntry.getKey().equalsIgnoreCase(header) || headerEntry.getValue() == null || headerEntry.getValue().isEmpty()) continue;
            return Optional.of(headerEntry.getValue().get(0));
        }
        return Optional.empty();
    }

    @Deprecated
    public static Optional<String> firstMatchingHeaderFromCollection(Map<String, List<String>> headersToSearch, Collection<String> headersToFind) {
        for (Map.Entry<String, List<String>> headerEntry : headersToSearch.entrySet()) {
            for (String headerToFind : headersToFind) {
                if (!headerEntry.getKey().equalsIgnoreCase(headerToFind) || headerEntry.getValue() == null || headerEntry.getValue().isEmpty()) continue;
                return Optional.of(headerEntry.getValue().get(0));
            }
        }
        return Optional.empty();
    }

    public static boolean isSingleHeader(String h) {
        return SINGLE_HEADERS.contains(StringUtils.lowerCase(h));
    }

    public static Map<String, List<String>> uriParams(URI uri) {
        return SdkHttpUtils.splitQueryString(uri.getRawQuery()).stream().map(s -> s.split("=")).map(s -> {
            String[] stringArray;
            if (((String[])s).length == 1) {
                String[] stringArray2 = new String[2];
                stringArray2[0] = s[0];
                stringArray = stringArray2;
                stringArray2[1] = null;
            } else {
                stringArray = s;
            }
            return stringArray;
        }).collect(Collectors.groupingBy(a -> SdkHttpUtils.urlDecode(a[0]), Collectors.mapping(a -> SdkHttpUtils.urlDecode(a[1]), Collectors.toList())));
    }

    public static List<String> splitQueryString(String queryString) {
        ArrayList<String> results = new ArrayList<String>();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < queryString.length(); ++i) {
            char character = queryString.charAt(i);
            if (character != '&') {
                result.append(character);
                continue;
            }
            results.add(StringUtils.trimToEmpty(result.toString()));
            result.setLength(0);
        }
        results.add(StringUtils.trimToEmpty(result.toString()));
        return results;
    }

    public static Set<String> parseNonProxyHostsProperty() {
        String systemNonProxyHosts = ProxySystemSetting.NON_PROXY_HOSTS.getStringValue().orElse(null);
        return SdkHttpUtils.extractNonProxyHosts(systemNonProxyHosts);
    }

    private static Set<String> extractNonProxyHosts(String systemNonProxyHosts) {
        if (systemNonProxyHosts != null && !StringUtils.isEmpty(systemNonProxyHosts)) {
            return Arrays.stream(systemNonProxyHosts.split("\\|")).map(String::toLowerCase).map(s -> StringUtils.replace(s, "*", ".*?")).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public static Set<String> parseNonProxyHostsEnvironmentVariable() {
        String systemNonProxyHosts = ProxyEnvironmentSetting.NO_PROXY.getStringValue().orElse(null);
        return SdkHttpUtils.extractNonProxyHosts(systemNonProxyHosts);
    }
}

