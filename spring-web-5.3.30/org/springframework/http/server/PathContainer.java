/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.http.server;

import java.util.List;
import org.springframework.http.server.DefaultPathContainer;
import org.springframework.util.MultiValueMap;

public interface PathContainer {
    public String value();

    public List<Element> elements();

    default public PathContainer subPath(int index) {
        return this.subPath(index, this.elements().size());
    }

    default public PathContainer subPath(int startIndex, int endIndex) {
        return DefaultPathContainer.subPath(this, startIndex, endIndex);
    }

    public static PathContainer parsePath(String path) {
        return DefaultPathContainer.createFromUrlPath(path, Options.HTTP_PATH);
    }

    public static PathContainer parsePath(String path, Options options) {
        return DefaultPathContainer.createFromUrlPath(path, options);
    }

    public static class Options {
        public static final Options HTTP_PATH = Options.create('/', true);
        public static final Options MESSAGE_ROUTE = Options.create('.', false);
        private final char separator;
        private final boolean decodeAndParseSegments;

        private Options(char separator, boolean decodeAndParseSegments) {
            this.separator = separator;
            this.decodeAndParseSegments = decodeAndParseSegments;
        }

        public char separator() {
            return this.separator;
        }

        public boolean shouldDecodeAndParseSegments() {
            return this.decodeAndParseSegments;
        }

        public static Options create(char separator, boolean decodeAndParseSegments) {
            return new Options(separator, decodeAndParseSegments);
        }
    }

    public static interface PathSegment
    extends Element {
        public String valueToMatch();

        public char[] valueToMatchAsChars();

        public MultiValueMap<String, String> parameters();
    }

    public static interface Separator
    extends Element {
    }

    public static interface Element {
        public String value();
    }
}

