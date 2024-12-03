/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

final class DefaultPathContainer
implements PathContainer {
    private static final PathContainer EMPTY_PATH = new DefaultPathContainer("", Collections.emptyList());
    private static final Map<Character, DefaultSeparator> SEPARATORS = new HashMap<Character, DefaultSeparator>(2);
    private final String path;
    private final List<PathContainer.Element> elements;

    private DefaultPathContainer(String path, List<PathContainer.Element> elements) {
        this.path = path;
        this.elements = Collections.unmodifiableList(elements);
    }

    @Override
    public String value() {
        return this.path;
    }

    @Override
    public List<PathContainer.Element> elements() {
        return this.elements;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PathContainer)) {
            return false;
        }
        return this.value().equals(((PathContainer)other).value());
    }

    public int hashCode() {
        return this.path.hashCode();
    }

    public String toString() {
        return this.value();
    }

    static PathContainer createFromUrlPath(String path, PathContainer.Options options) {
        int begin;
        if (path.isEmpty()) {
            return EMPTY_PATH;
        }
        char separator = options.separator();
        DefaultSeparator separatorElement = SEPARATORS.get(Character.valueOf(separator));
        if (separatorElement == null) {
            throw new IllegalArgumentException("Unexpected separator: '" + separator + "'");
        }
        ArrayList<PathContainer.Element> elements = new ArrayList<PathContainer.Element>();
        if (path.charAt(0) == separator) {
            begin = 1;
            elements.add(separatorElement);
        } else {
            begin = 0;
        }
        while (begin < path.length()) {
            String segment;
            int end = path.indexOf(separator, begin);
            String string = segment = end != -1 ? path.substring(begin, end) : path.substring(begin);
            if (!segment.isEmpty()) {
                elements.add(options.shouldDecodeAndParseSegments() ? DefaultPathContainer.decodeAndParsePathSegment(segment) : DefaultPathSegment.from(segment, separatorElement));
            }
            if (end == -1) break;
            elements.add(separatorElement);
            begin = end + 1;
        }
        return new DefaultPathContainer(path, elements);
    }

    private static PathContainer.PathSegment decodeAndParsePathSegment(String segment) {
        Charset charset = StandardCharsets.UTF_8;
        int index = segment.indexOf(59);
        if (index == -1) {
            String valueToMatch = StringUtils.uriDecode(segment, charset);
            return DefaultPathSegment.from(segment, valueToMatch);
        }
        String valueToMatch = StringUtils.uriDecode(segment.substring(0, index), charset);
        String pathParameterContent = segment.substring(index);
        MultiValueMap<String, String> parameters = DefaultPathContainer.parsePathParams(pathParameterContent, charset);
        return DefaultPathSegment.from(segment, valueToMatch, parameters);
    }

    private static MultiValueMap<String, String> parsePathParams(String input, Charset charset) {
        LinkedMultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>();
        int begin = 1;
        while (begin < input.length()) {
            int end = input.indexOf(59, begin);
            String param = end != -1 ? input.substring(begin, end) : input.substring(begin);
            DefaultPathContainer.parsePathParamValues(param, charset, result);
            if (end == -1) break;
            begin = end + 1;
        }
        return result;
    }

    private static void parsePathParamValues(String input, Charset charset, MultiValueMap<String, String> output) {
        if (StringUtils.hasText(input)) {
            int index = input.indexOf(61);
            if (index != -1) {
                String name = input.substring(0, index);
                if (StringUtils.hasText(name = StringUtils.uriDecode(name, charset))) {
                    String value = input.substring(index + 1);
                    for (String v : StringUtils.commaDelimitedListToStringArray(value)) {
                        output.add(name, StringUtils.uriDecode(v, charset));
                    }
                }
            } else {
                String name = StringUtils.uriDecode(input, charset);
                if (StringUtils.hasText(name)) {
                    output.add(input, "");
                }
            }
        }
    }

    static PathContainer subPath(PathContainer container, int fromIndex, int toIndex) {
        List<PathContainer.Element> elements = container.elements();
        if (fromIndex == 0 && toIndex == elements.size()) {
            return container;
        }
        if (fromIndex == toIndex) {
            return EMPTY_PATH;
        }
        Assert.isTrue(fromIndex >= 0 && fromIndex < elements.size(), () -> "Invalid fromIndex: " + fromIndex);
        Assert.isTrue(toIndex >= 0 && toIndex <= elements.size(), () -> "Invalid toIndex: " + toIndex);
        Assert.isTrue(fromIndex < toIndex, () -> "fromIndex: " + fromIndex + " should be < toIndex " + toIndex);
        List<PathContainer.Element> subList = elements.subList(fromIndex, toIndex);
        String path = subList.stream().map(PathContainer.Element::value).collect(Collectors.joining(""));
        return new DefaultPathContainer(path, subList);
    }

    static {
        SEPARATORS.put(Character.valueOf('/'), new DefaultSeparator('/', "%2F"));
        SEPARATORS.put(Character.valueOf('.'), new DefaultSeparator('.', "%2E"));
    }

    private static final class DefaultPathSegment
    implements PathContainer.PathSegment {
        private static final MultiValueMap<String, String> EMPTY_PARAMS = CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap());
        private final String value;
        private final String valueToMatch;
        private final MultiValueMap<String, String> parameters;

        static DefaultPathSegment from(String value, DefaultSeparator separator) {
            String valueToMatch = value.contains(separator.encodedSequence()) ? value.replaceAll(separator.encodedSequence(), separator.value()) : value;
            return DefaultPathSegment.from(value, valueToMatch);
        }

        static DefaultPathSegment from(String value, String valueToMatch) {
            return new DefaultPathSegment(value, valueToMatch, EMPTY_PARAMS);
        }

        static DefaultPathSegment from(String value, String valueToMatch, MultiValueMap<String, String> params) {
            return new DefaultPathSegment(value, valueToMatch, CollectionUtils.unmodifiableMultiValueMap(params));
        }

        private DefaultPathSegment(String value, String valueToMatch, MultiValueMap<String, String> params) {
            this.value = value;
            this.valueToMatch = valueToMatch;
            this.parameters = params;
        }

        @Override
        public String value() {
            return this.value;
        }

        @Override
        public String valueToMatch() {
            return this.valueToMatch;
        }

        @Override
        public char[] valueToMatchAsChars() {
            return this.valueToMatch.toCharArray();
        }

        @Override
        public MultiValueMap<String, String> parameters() {
            return this.parameters;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof PathContainer.PathSegment)) {
                return false;
            }
            return this.value().equals(((PathContainer.PathSegment)other).value());
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return "[value='" + this.value + "']";
        }
    }

    private static class DefaultSeparator
    implements PathContainer.Separator {
        private final String separator;
        private final String encodedSequence;

        DefaultSeparator(char separator, String encodedSequence) {
            this.separator = String.valueOf(separator);
            this.encodedSequence = encodedSequence;
        }

        @Override
        public String value() {
            return this.separator;
        }

        public String encodedSequence() {
            return this.encodedSequence;
        }
    }
}

