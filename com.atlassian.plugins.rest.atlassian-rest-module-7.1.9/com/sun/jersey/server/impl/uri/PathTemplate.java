/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri;

import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.api.uri.UriTemplateParser;

public final class PathTemplate
extends UriTemplate {
    public PathTemplate(String path) {
        super(new PathTemplateParser(PathTemplate.prefixWithSlash(path)));
    }

    private static String prefixWithSlash(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }

    private static final class PathTemplateParser
    extends UriTemplateParser {
        public PathTemplateParser(String path) {
            super(path);
        }

        @Override
        protected String encodeLiteralCharacters(String literalCharacters) {
            return UriComponent.contextualEncode(literalCharacters, UriComponent.Type.PATH);
        }
    }
}

