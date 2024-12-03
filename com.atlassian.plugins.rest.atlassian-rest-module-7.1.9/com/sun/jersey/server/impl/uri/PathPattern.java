/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri;

import com.sun.jersey.api.uri.UriPattern;
import com.sun.jersey.api.uri.UriTemplate;
import java.util.Comparator;

public final class PathPattern
extends UriPattern {
    public static final PathPattern EMPTY_PATH = new PathPattern();
    private static final String RIGHT_HAND_SIDE = "(/.*)?";
    public static final Comparator<PathPattern> COMPARATOR = new Comparator<PathPattern>(){

        @Override
        public int compare(PathPattern o1, PathPattern o2) {
            return UriTemplate.COMPARATOR.compare(o1.template, o2.template);
        }
    };
    private final UriTemplate template;

    private PathPattern() {
        this.template = UriTemplate.EMPTY;
    }

    public PathPattern(UriTemplate template) {
        super(PathPattern.postfixWithCapturingGroup(template.getPattern().getRegex()), PathPattern.indexCapturingGroup(template.getPattern().getGroupIndexes()));
        this.template = template;
    }

    public PathPattern(UriTemplate template, String rightHandSide) {
        super(PathPattern.postfixWithCapturingGroup(template.getPattern().getRegex(), rightHandSide), PathPattern.indexCapturingGroup(template.getPattern().getGroupIndexes()));
        this.template = template;
    }

    public UriTemplate getTemplate() {
        return this.template;
    }

    private static String postfixWithCapturingGroup(String regex) {
        return PathPattern.postfixWithCapturingGroup(regex, RIGHT_HAND_SIDE);
    }

    private static String postfixWithCapturingGroup(String regex, String rightHandSide) {
        return (regex.endsWith("/") ? regex.substring(0, regex.length() - 1) : regex) + rightHandSide;
    }

    private static int[] indexCapturingGroup(int[] indexes) {
        if (indexes.length == 0) {
            return indexes;
        }
        int[] cgIndexes = new int[indexes.length + 1];
        System.arraycopy(indexes, 0, cgIndexes, 0, indexes.length);
        cgIndexes[indexes.length] = cgIndexes[indexes.length - 1] + 1;
        return cgIndexes;
    }
}

