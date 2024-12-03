/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.streams.api.common.uri;

import com.atlassian.streams.api.common.uri.DefaultUriParser;
import com.atlassian.streams.api.common.uri.UriBuilder;
import com.atlassian.streams.api.common.uri.UriParser;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public final class Uri {
    private final String text;
    private final String scheme;
    private final String authority;
    private final String path;
    private final String query;
    private final String fragment;
    private final Map<String, List<String>> queryParameters;
    private final Map<String, List<String>> fragmentParameters;
    private static UriParser parser = new DefaultUriParser();

    Uri(UriBuilder builder) {
        this.scheme = builder.getScheme();
        this.authority = builder.getAuthority();
        this.path = builder.getPath();
        this.query = builder.getQuery();
        this.fragment = builder.getFragment();
        this.queryParameters = ImmutableMap.copyOf(builder.getQueryParameters());
        this.fragmentParameters = ImmutableMap.copyOf(builder.getFragmentParameters());
        StringBuilder out = new StringBuilder();
        if (this.scheme != null) {
            out.append(this.scheme).append(':');
        }
        if (this.authority != null) {
            out.append("//").append(this.authority);
            if (this.path != null && this.path.length() > 1 && !this.path.startsWith("/")) {
                out.append('/');
            }
        }
        if (this.path != null) {
            out.append(this.path);
        }
        if (this.query != null) {
            out.append('?').append(this.query);
        }
        if (this.fragment != null) {
            out.append('#').append(this.fragment);
        }
        this.text = out.toString();
    }

    public static Uri parse(String text) {
        try {
            return parser.parse(text);
        }
        catch (IllegalArgumentException e) {
            throw new UriException(e);
        }
    }

    public static Uri fromJavaUri(URI uri) {
        if (uri.isOpaque()) {
            throw new UriException("No support for opaque Uris " + uri.toString());
        }
        return new UriBuilder().setScheme(uri.getScheme()).setAuthority(uri.getRawAuthority()).setPath(uri.getRawPath()).setQuery(uri.getRawQuery()).setFragment(uri.getRawFragment()).toUri();
    }

    public URI toJavaUri() {
        try {
            return new URI(this.toString());
        }
        catch (URISyntaxException e) {
            throw new UriException(e);
        }
    }

    public Uri resolve(Uri relative) {
        UriBuilder result;
        if (relative == null) {
            return null;
        }
        if (relative.isAbsolute()) {
            return relative;
        }
        if (StringUtils.isBlank((CharSequence)relative.path) && relative.scheme == null && relative.authority == null && relative.query == null && relative.fragment != null) {
            result = new UriBuilder(this);
            result.setFragment(relative.fragment);
        } else if (relative.scheme != null) {
            result = new UriBuilder(relative);
        } else if (relative.authority != null) {
            result = new UriBuilder(relative);
            result.setScheme(this.scheme);
        } else {
            String relativePath;
            result = new UriBuilder(this);
            result.setFragment(relative.fragment);
            result.setQuery(relative.query);
            String string = relativePath = relative.path == null ? "" : relative.path;
            if (relativePath.startsWith("/")) {
                result.setPath(relativePath);
            } else {
                String basePath = this.path != null ? this.path : "/";
                int endindex = basePath.lastIndexOf(47) + 1;
                result.setPath(Uri.normalizePath(basePath.substring(0, endindex) + relativePath));
            }
        }
        Uri resolved = result.toUri();
        Uri.validate(resolved);
        return resolved;
    }

    private static void validate(Uri uri) {
        if (StringUtils.isBlank((CharSequence)uri.authority) && StringUtils.isBlank((CharSequence)uri.path) && StringUtils.isBlank((CharSequence)uri.query)) {
            throw new UriException("Invalid scheme-specific part");
        }
    }

    private static String normalizePath(String path) {
        int index = -1;
        int pathlen = path.length();
        int size = 0;
        if (pathlen > 0 && path.charAt(0) != '/') {
            ++size;
        }
        while ((index = path.indexOf(47, index + 1)) != -1) {
            if (index + 1 >= pathlen || path.charAt(index + 1) == '/') continue;
            ++size;
        }
        String[] seglist = new String[size];
        boolean[] include = new boolean[size];
        int current = 0;
        int index2 = 0;
        int n = index = pathlen > 0 && path.charAt(0) == '/' ? 1 : 0;
        while ((index2 = path.indexOf(47, index + 1)) != -1) {
            seglist[current++] = path.substring(index, index2);
            index = index2 + 1;
        }
        if (current < size) {
            seglist[current] = path.substring(index);
        }
        for (int i = 0; i < size; ++i) {
            include[i] = true;
            if (seglist[i].equals("..")) {
                int remove;
                for (remove = i - 1; remove > -1 && !include[remove]; --remove) {
                }
                if (remove <= -1 || seglist[remove].equals("..")) continue;
                include[remove] = false;
                include[i] = false;
                continue;
            }
            if (!seglist[i].equals(".")) continue;
            include[i] = false;
        }
        StringBuilder newpath = new StringBuilder();
        if (path.startsWith("/")) {
            newpath.append('/');
        }
        for (int i = 0; i < seglist.length; ++i) {
            if (!include[i]) continue;
            newpath.append(seglist[i]);
            newpath.append('/');
        }
        if (!path.endsWith("/") && seglist.length > 0 && include[seglist.length - 1]) {
            newpath.deleteCharAt(newpath.length() - 1);
        }
        String result = newpath.toString();
        index = result.indexOf(58);
        index2 = result.indexOf(47);
        if (index != -1 && (index < index2 || index2 == -1)) {
            newpath.insert(0, "./");
            result = newpath.toString();
        }
        return result;
    }

    public boolean isAbsolute() {
        return this.scheme != null && this.authority != null;
    }

    public String getScheme() {
        return this.scheme;
    }

    public String getAuthority() {
        return this.authority;
    }

    public String getPath() {
        return this.path;
    }

    public String getQuery() {
        return this.query;
    }

    public Map<String, List<String>> getQueryParameters() {
        return this.queryParameters;
    }

    public Collection<String> getQueryParameters(String name) {
        return this.queryParameters.get(name);
    }

    public String getQueryParameter(String name) {
        Collection values = this.queryParameters.get(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return (String)values.iterator().next();
    }

    public String getFragment() {
        return this.fragment;
    }

    public Map<String, List<String>> getFragmentParameters() {
        return this.fragmentParameters;
    }

    public Collection<String> getFragmentParameters(String name) {
        return this.fragmentParameters.get(name);
    }

    public String getFragmentParameter(String name) {
        Collection values = this.fragmentParameters.get(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return (String)values.iterator().next();
    }

    public String toString() {
        return this.text;
    }

    public int hashCode() {
        return this.text.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Uri)) {
            return false;
        }
        return Objects.equal((Object)this.text, (Object)((Uri)obj).text);
    }

    public static final class UriException
    extends IllegalArgumentException {
        private UriException(Exception e) {
            super(e);
        }

        private UriException(String msg) {
            super(msg);
        }
    }
}

