/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private static final MultiValueMap<String, String> EMPTY_MAP = new LinkedMultiValueMap<String, String>();
    private static final PathContainer EMPTY_PATH = new DefaultPathContainer("", Collections.emptyList());
    private static final PathContainer.Separator SEPARATOR = () -> "/";
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
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        return this.path.equals(((DefaultPathContainer)other).path);
    }

    public int hashCode() {
        return this.path.hashCode();
    }

    public String toString() {
        return "[path='" + this.path + "']";
    }

    static PathContainer createFromUrlPath(String path) {
        int begin;
        if (path.equals("")) {
            return EMPTY_PATH;
        }
        String separator = "/";
        PathContainer.Separator separatorElement = separator.equals(SEPARATOR.value()) ? SEPARATOR : () -> separator;
        ArrayList<PathContainer.Element> elements = new ArrayList<PathContainer.Element>();
        if (path.length() > 0 && path.startsWith(separator)) {
            begin = separator.length();
            elements.add(separatorElement);
        } else {
            begin = 0;
        }
        while (begin < path.length()) {
            String segment;
            int end = path.indexOf(separator, begin);
            String string = segment = end != -1 ? path.substring(begin, end) : path.substring(begin);
            if (!segment.equals("")) {
                elements.add(DefaultPathContainer.parsePathSegment(segment));
            }
            if (end == -1) break;
            elements.add(separatorElement);
            begin = end + separator.length();
        }
        return new DefaultPathContainer(path, elements);
    }

    private static PathContainer.PathSegment parsePathSegment(String segment) {
        Charset charset = StandardCharsets.UTF_8;
        int index = segment.indexOf(59);
        if (index == -1) {
            String valueToMatch = StringUtils.uriDecode(segment, charset);
            return new DefaultPathSegment(segment, valueToMatch, EMPTY_MAP);
        }
        String valueToMatch = StringUtils.uriDecode(segment.substring(0, index), charset);
        String pathParameterContent = segment.substring(index);
        MultiValueMap<String, String> parameters = DefaultPathContainer.parsePathParams(pathParameterContent, charset);
        return new DefaultPathSegment(segment, valueToMatch, parameters);
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
                String value = input.substring(index + 1);
                for (String v : StringUtils.commaDelimitedListToStringArray(value)) {
                    if (!StringUtils.hasText(name = StringUtils.uriDecode(name, charset))) continue;
                    output.add(name, StringUtils.uriDecode(v, charset));
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

    private static class DefaultPathSegment
    implements PathContainer.PathSegment {
        private final String value;
        private final String valueToMatch;
        private final char[] valueToMatchAsChars;
        private final MultiValueMap<String, String> parameters;

        public DefaultPathSegment(String value, String valueToMatch, MultiValueMap<String, String> params) {
            Assert.isTrue(!value.contains("/"), () -> "Invalid path segment value: " + value);
            this.value = value;
            this.valueToMatch = valueToMatch;
            this.valueToMatchAsChars = valueToMatch.toCharArray();
            this.parameters = CollectionUtils.unmodifiableMultiValueMap(params);
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
            return this.valueToMatchAsChars;
        }

        @Override
        public MultiValueMap<String, String> parameters() {
            return this.parameters;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            return this.value.equals(((DefaultPathSegment)other).value);
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return "[value='" + this.value + "']";
        }
    }
}

