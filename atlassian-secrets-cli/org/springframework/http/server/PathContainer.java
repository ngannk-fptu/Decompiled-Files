/*
 * Decompiled with CFR 0.152.
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
        return DefaultPathContainer.createFromUrlPath(path);
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

