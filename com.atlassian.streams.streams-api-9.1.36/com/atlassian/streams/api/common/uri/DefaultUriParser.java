/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.api.common.uri;

import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.api.common.uri.UriParser;
import java.net.URI;
import java.net.URISyntaxException;

public class DefaultUriParser
implements UriParser {
    @Override
    public Uri parse(String text) {
        try {
            return Uri.fromJavaUri(new URI(text));
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

