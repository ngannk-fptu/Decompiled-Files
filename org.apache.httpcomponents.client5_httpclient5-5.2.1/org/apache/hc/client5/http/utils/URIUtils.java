/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.URIScheme
 *  org.apache.hc.core5.net.URIAuthority
 *  org.apache.hc.core5.net.URIBuilder
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.TextUtils
 */
package org.apache.hc.client5.http.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TextUtils;

public class URIUtils {
    @Deprecated
    public static URI rewriteURI(URI uri, HttpHost target, boolean dropFragment) throws URISyntaxException {
        Args.notNull((Object)uri, (String)"URI");
        if (uri.isOpaque()) {
            return uri;
        }
        URIBuilder uribuilder = new URIBuilder(uri);
        if (target != null) {
            uribuilder.setScheme(target.getSchemeName());
            uribuilder.setHost(target.getHostName());
            uribuilder.setPort(target.getPort());
        } else {
            uribuilder.setScheme(null);
            uribuilder.setHost((String)null);
            uribuilder.setPort(-1);
        }
        if (dropFragment) {
            uribuilder.setFragment(null);
        }
        List originalPathSegments = uribuilder.getPathSegments();
        ArrayList pathSegments = new ArrayList(originalPathSegments);
        Iterator it = pathSegments.iterator();
        while (it.hasNext()) {
            String pathSegment = (String)it.next();
            if (!pathSegment.isEmpty() || !it.hasNext()) continue;
            it.remove();
        }
        if (pathSegments.size() != originalPathSegments.size()) {
            uribuilder.setPathSegments(pathSegments);
        }
        if (pathSegments.isEmpty()) {
            uribuilder.setPathSegments(new String[]{""});
        }
        return uribuilder.build();
    }

    @Deprecated
    public static URI rewriteURI(URI uri, HttpHost target) throws URISyntaxException {
        return URIUtils.rewriteURI(uri, target, false);
    }

    @Deprecated
    public static URI rewriteURI(URI uri) throws URISyntaxException {
        Args.notNull((Object)uri, (String)"URI");
        if (uri.isOpaque()) {
            return uri;
        }
        URIBuilder uribuilder = new URIBuilder(uri);
        if (uribuilder.getUserInfo() != null) {
            uribuilder.setUserInfo(null);
        }
        if (uribuilder.isPathEmpty()) {
            uribuilder.setPathSegments(new String[]{""});
        }
        if (uribuilder.getHost() != null) {
            uribuilder.setHost(uribuilder.getHost().toLowerCase(Locale.ROOT));
        }
        uribuilder.setFragment(null);
        return uribuilder.build();
    }

    public static URI resolve(URI baseURI, String reference) {
        return URIUtils.resolve(baseURI, URI.create(reference));
    }

    public static URI resolve(URI baseURI, URI reference) {
        URI resolved;
        Args.notNull((Object)baseURI, (String)"Base URI");
        Args.notNull((Object)reference, (String)"Reference URI");
        String s = reference.toASCIIString();
        if (s.startsWith("?")) {
            String baseUri = baseURI.toASCIIString();
            int i = baseUri.indexOf(63);
            baseUri = i > -1 ? baseUri.substring(0, i) : baseUri;
            return URI.create(baseUri + s);
        }
        boolean emptyReference = s.isEmpty();
        if (emptyReference) {
            resolved = baseURI.resolve(URI.create("#"));
            String resolvedString = resolved.toASCIIString();
            resolved = URI.create(resolvedString.substring(0, resolvedString.indexOf(35)));
        } else {
            resolved = baseURI.resolve(reference);
        }
        try {
            return URIUtils.normalizeSyntax(resolved);
        }
        catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    static URI normalizeSyntax(URI uri) throws URISyntaxException {
        if (uri.isOpaque() || uri.getAuthority() == null) {
            return uri;
        }
        URIBuilder builder = new URIBuilder(uri);
        builder.normalizeSyntax();
        if (builder.getScheme() == null) {
            builder.setScheme(URIScheme.HTTP.id);
        }
        if (builder.isPathEmpty()) {
            builder.setPathSegments(new String[]{""});
        }
        return builder.build();
    }

    public static HttpHost extractHost(URI uri) {
        if (uri == null) {
            return null;
        }
        URIBuilder uriBuilder = new URIBuilder(uri);
        String scheme = uriBuilder.getScheme();
        String host = uriBuilder.getHost();
        int port = uriBuilder.getPort();
        if (!TextUtils.isBlank((CharSequence)host)) {
            try {
                return new HttpHost(scheme, host, port);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return null;
    }

    public static URI resolve(URI originalURI, HttpHost target, List<URI> redirects) throws URISyntaxException {
        URIBuilder uribuilder;
        Args.notNull((Object)originalURI, (String)"Request URI");
        if (redirects == null || redirects.isEmpty()) {
            uribuilder = new URIBuilder(originalURI);
        } else {
            uribuilder = new URIBuilder(redirects.get(redirects.size() - 1));
            String frag = uribuilder.getFragment();
            for (int i = redirects.size() - 1; frag == null && i >= 0; --i) {
                frag = redirects.get(i).getFragment();
            }
            uribuilder.setFragment(frag);
        }
        if (uribuilder.getFragment() == null) {
            uribuilder.setFragment(originalURI.getFragment());
        }
        if (target != null && !uribuilder.isAbsolute()) {
            uribuilder.setScheme(target.getSchemeName());
            uribuilder.setHost(target.getHostName());
            uribuilder.setPort(target.getPort());
        }
        return uribuilder.build();
    }

    @Deprecated
    public static URI create(HttpHost host, String path) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(path);
        if (host != null) {
            builder.setHost(host.getHostName()).setPort(host.getPort()).setScheme(host.getSchemeName());
        }
        return builder.build();
    }

    @Deprecated
    public static URI create(String scheme, URIAuthority host, String path) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(path);
        if (scheme != null) {
            builder.setScheme(scheme);
        }
        if (host != null) {
            builder.setHost(host.getHostName()).setPort(host.getPort());
        }
        return builder.build();
    }

    private URIUtils() {
    }
}

