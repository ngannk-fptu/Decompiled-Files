/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.core.util;

import com.atlassian.applinks.internal.common.net.Uris;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public final class URIUtil {
    private URIUtil() {
    }

    @Deprecated
    public static String concatenate(String base, String ... paths) {
        return StringUtils.stripEnd((String)base, (String)"/") + URIUtil.removeRedundantSlashes("/" + StringUtils.join((Object[])paths, (String)"/"));
    }

    @Deprecated
    public static URI concatenate(URI base, String ... paths) throws URISyntaxException {
        return new URI(URIUtil.concatenate(base.toASCIIString(), paths));
    }

    public static URI concatenate(URI base, URI ... paths) {
        try {
            Object[] pathStrings = (String[])Iterables.toArray((Iterable)Lists.transform((List)Lists.newArrayList((Object[])paths), URI::toASCIIString), String.class);
            return new URI(StringUtils.stripEnd((String)base.toASCIIString(), (String)"/") + Uris.removeRedundantSlashes("/" + StringUtils.join((Object[])pathStrings, (String)"/")));
        }
        catch (URISyntaxException e) {
            throw new URIUtilException("Failed to concatenate URIs", e);
        }
    }

    @Deprecated
    public static String utf8Encode(String string) {
        return Uris.utf8Encode(string);
    }

    public static String utf8Decode(String string) {
        try {
            return URLDecoder.decode(string, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new URIUtilException("UTF-8 not installed!?", e);
        }
    }

    @Deprecated
    public static String utf8Encode(URI uri) {
        return Uris.utf8Encode(uri);
    }

    @Deprecated
    @Nonnull
    public static URI uncheckedToUri(String uri) {
        try {
            return new URI(uri);
        }
        catch (URISyntaxException e) {
            throw new URIUtilException(String.format("Failed to convert %s to URI (%s)", uri, e.getReason()), e);
        }
    }

    public static URI uncheckedConcatenateAndToUri(String base, String ... paths) {
        String uri = Uris.concatenate(base, paths);
        try {
            return new URI(uri);
        }
        catch (URISyntaxException e) {
            throw new URIUtilException(String.format("Failed to convert %s to URI (%s)", uri, e.getReason()), e);
        }
    }

    @Deprecated
    public static URI uncheckedConcatenate(URI base, String ... paths) {
        try {
            return URIUtil.concatenate(base, paths);
        }
        catch (URISyntaxException e) {
            throw new URIUtilException(String.format("Failed to concatenate %s to form URI (%s)", base, e.getReason()), e);
        }
    }

    public static URI uncheckedCreate(String uri) {
        try {
            return new URI(uri);
        }
        catch (URISyntaxException e) {
            throw new URIUtilException(String.format("%s is not a valid URI (%s)", uri, e.getReason()), e);
        }
    }

    @Deprecated
    public static String removeRedundantSlashes(String path) {
        return Uris.removeRedundantSlashes(path);
    }

    public static URI copyOf(URI uri) {
        if (uri == null) {
            return null;
        }
        try {
            return new URI(uri.toASCIIString());
        }
        catch (URISyntaxException e) {
            throw new URIUtilException("Failed to copy URI: " + uri.toASCIIString());
        }
    }

    public static class URIUtilException
    extends RuntimeException {
        public URIUtilException(String message) {
            super(message);
        }

        public URIUtilException(String message, Exception e) {
            super(message, e);
        }
    }
}

