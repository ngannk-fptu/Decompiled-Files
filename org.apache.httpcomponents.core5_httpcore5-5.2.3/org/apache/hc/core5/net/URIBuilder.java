/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.net;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.message.ParserCursor;
import org.apache.hc.core5.net.InetAddressUtils;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.net.PercentCodec;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TextUtils;
import org.apache.hc.core5.util.Tokenizer;

public class URIBuilder {
    private String scheme;
    private String encodedSchemeSpecificPart;
    private String encodedAuthority;
    private String userInfo;
    private String encodedUserInfo;
    private String host;
    private int port;
    private String encodedPath;
    private boolean pathRootless;
    private List<String> pathSegments;
    private String encodedQuery;
    private List<NameValuePair> queryParams;
    private String query;
    private Charset charset;
    private String fragment;
    private String encodedFragment;
    private static final char QUERY_PARAM_SEPARATOR = '&';
    private static final char PARAM_VALUE_SEPARATOR = '=';
    private static final char PATH_SEPARATOR = '/';
    private static final BitSet QUERY_PARAM_SEPARATORS = new BitSet(256);
    private static final BitSet QUERY_VALUE_SEPARATORS = new BitSet(256);
    private static final BitSet PATH_SEPARATORS = new BitSet(256);

    public static URIBuilder localhost() throws UnknownHostException {
        return new URIBuilder().setHost(InetAddress.getLocalHost());
    }

    public static URIBuilder loopbackAddress() {
        return new URIBuilder().setHost(InetAddress.getLoopbackAddress());
    }

    public URIBuilder() {
        this.port = -1;
    }

    public URIBuilder(String uriString) throws URISyntaxException {
        this(new URI(uriString), StandardCharsets.UTF_8);
    }

    public URIBuilder(URI uri) {
        this(uri, StandardCharsets.UTF_8);
    }

    public URIBuilder(String uriString, Charset charset) throws URISyntaxException {
        this(new URI(uriString), charset);
    }

    public URIBuilder(URI uri, Charset charset) {
        this.digestURI(uri, charset);
    }

    public URIBuilder setAuthority(NamedEndpoint authority) {
        this.setUserInfo(null);
        this.setHost(authority.getHostName());
        this.setPort(authority.getPort());
        return this;
    }

    public URIBuilder setAuthority(URIAuthority authority) {
        this.setUserInfo(authority.getUserInfo());
        this.setHost(authority.getHostName());
        this.setPort(authority.getPort());
        return this;
    }

    public URIBuilder setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public URIAuthority getAuthority() {
        return new URIAuthority(this.getUserInfo(), this.getHost(), this.getPort());
    }

    public Charset getCharset() {
        return this.charset;
    }

    static List<NameValuePair> parseQuery(CharSequence s, Charset charset, boolean plusAsBlank) {
        if (s == null) {
            return null;
        }
        Tokenizer tokenParser = Tokenizer.INSTANCE;
        ParserCursor cursor = new ParserCursor(0, s.length());
        ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
        while (!cursor.atEnd()) {
            String name = tokenParser.parseToken(s, cursor, QUERY_PARAM_SEPARATORS);
            String value = null;
            if (!cursor.atEnd()) {
                char delim = s.charAt(cursor.getPos());
                cursor.updatePos(cursor.getPos() + 1);
                if (delim == '=') {
                    value = tokenParser.parseToken(s, cursor, QUERY_VALUE_SEPARATORS);
                    if (!cursor.atEnd()) {
                        cursor.updatePos(cursor.getPos() + 1);
                    }
                }
            }
            if (name.isEmpty()) continue;
            list.add(new BasicNameValuePair(PercentCodec.decode(name, charset, plusAsBlank), PercentCodec.decode(value, charset, plusAsBlank)));
        }
        return list;
    }

    static List<String> splitPath(CharSequence s) {
        if (s == null) {
            return null;
        }
        ParserCursor cursor = new ParserCursor(0, s.length());
        if (cursor.atEnd()) {
            return new ArrayList<String>(0);
        }
        if (PATH_SEPARATORS.get(s.charAt(cursor.getPos()))) {
            cursor.updatePos(cursor.getPos() + 1);
        }
        ArrayList<String> list = new ArrayList<String>();
        StringBuilder buf = new StringBuilder();
        while (true) {
            if (cursor.atEnd()) break;
            char current = s.charAt(cursor.getPos());
            if (PATH_SEPARATORS.get(current)) {
                list.add(buf.toString());
                buf.setLength(0);
            } else {
                buf.append(current);
            }
            cursor.updatePos(cursor.getPos() + 1);
        }
        list.add(buf.toString());
        return list;
    }

    static List<String> parsePath(CharSequence s, Charset charset) {
        if (s == null) {
            return null;
        }
        List<String> segments = URIBuilder.splitPath(s);
        ArrayList<String> list = new ArrayList<String>(segments.size());
        for (String segment : segments) {
            list.add(PercentCodec.decode(segment, charset));
        }
        return list;
    }

    static void formatPath(StringBuilder buf, Iterable<String> segments, boolean rootless, Charset charset) {
        int i = 0;
        for (String segment : segments) {
            if (i > 0 || !rootless) {
                buf.append('/');
            }
            PercentCodec.encode(buf, segment, charset);
            ++i;
        }
    }

    static void formatQuery(StringBuilder buf, Iterable<? extends NameValuePair> params, Charset charset, boolean blankAsPlus) {
        int i = 0;
        for (NameValuePair nameValuePair : params) {
            if (i > 0) {
                buf.append('&');
            }
            PercentCodec.encode(buf, nameValuePair.getName(), charset, blankAsPlus);
            if (nameValuePair.getValue() != null) {
                buf.append('=');
                PercentCodec.encode(buf, nameValuePair.getValue(), charset, blankAsPlus);
            }
            ++i;
        }
    }

    public URI build() throws URISyntaxException {
        return new URI(this.buildString());
    }

    private String buildString() {
        StringBuilder sb = new StringBuilder();
        if (this.scheme != null) {
            sb.append(this.scheme).append(':');
        }
        if (this.encodedSchemeSpecificPart != null) {
            sb.append(this.encodedSchemeSpecificPart);
        } else {
            boolean authoritySpecified;
            if (this.encodedAuthority != null) {
                sb.append("//").append(this.encodedAuthority);
                authoritySpecified = true;
            } else if (this.host != null) {
                sb.append("//");
                if (this.encodedUserInfo != null) {
                    sb.append(this.encodedUserInfo).append("@");
                } else if (this.userInfo != null) {
                    int idx = this.userInfo.indexOf(58);
                    if (idx != -1) {
                        PercentCodec.encode(sb, this.userInfo.substring(0, idx), this.charset);
                        sb.append(':');
                        PercentCodec.encode(sb, this.userInfo.substring(idx + 1), this.charset);
                    } else {
                        PercentCodec.encode(sb, this.userInfo, this.charset);
                    }
                    sb.append("@");
                }
                if (InetAddressUtils.isIPv6Address(this.host)) {
                    sb.append("[").append(this.host).append("]");
                } else {
                    sb.append(PercentCodec.encode(this.host, this.charset));
                }
                if (this.port >= 0) {
                    sb.append(":").append(this.port);
                }
                authoritySpecified = true;
            } else {
                authoritySpecified = false;
            }
            if (this.encodedPath != null) {
                if (authoritySpecified && !TextUtils.isEmpty(this.encodedPath) && !this.encodedPath.startsWith("/")) {
                    sb.append('/');
                }
                sb.append(this.encodedPath);
            } else if (this.pathSegments != null) {
                URIBuilder.formatPath(sb, this.pathSegments, !authoritySpecified && this.pathRootless, this.charset);
            }
            if (this.encodedQuery != null) {
                sb.append("?").append(this.encodedQuery);
            } else if (this.queryParams != null && !this.queryParams.isEmpty()) {
                sb.append("?");
                URIBuilder.formatQuery(sb, this.queryParams, this.charset, false);
            } else if (this.query != null) {
                sb.append("?");
                PercentCodec.encode(sb, this.query, this.charset, PercentCodec.URIC, false);
            }
        }
        if (this.encodedFragment != null) {
            sb.append("#").append(this.encodedFragment);
        } else if (this.fragment != null) {
            sb.append("#");
            PercentCodec.encode(sb, this.fragment, this.charset);
        }
        return sb.toString();
    }

    private void digestURI(URI uri, Charset charset) {
        this.scheme = uri.getScheme();
        this.encodedSchemeSpecificPart = uri.getRawSchemeSpecificPart();
        this.encodedAuthority = uri.getRawAuthority();
        String uriHost = uri.getHost();
        this.host = uriHost != null && InetAddressUtils.isIPv6URLBracketedAddress(uriHost) ? uriHost.substring(1, uriHost.length() - 1) : uriHost;
        this.port = uri.getPort();
        this.encodedUserInfo = uri.getRawUserInfo();
        this.userInfo = uri.getUserInfo();
        if (this.encodedAuthority != null && this.host == null) {
            try {
                URIAuthority uriAuthority = URIAuthority.parse(this.encodedAuthority);
                this.encodedUserInfo = uriAuthority.getUserInfo();
                this.userInfo = PercentCodec.decode(uriAuthority.getUserInfo(), charset);
                this.host = PercentCodec.decode(uriAuthority.getHostName(), charset);
                this.port = uriAuthority.getPort();
            }
            catch (URISyntaxException uRISyntaxException) {
                // empty catch block
            }
        }
        this.encodedPath = uri.getRawPath();
        this.pathSegments = URIBuilder.parsePath(uri.getRawPath(), charset);
        this.pathRootless = uri.getRawPath() == null || !uri.getRawPath().startsWith("/");
        this.encodedQuery = uri.getRawQuery();
        this.queryParams = URIBuilder.parseQuery(uri.getRawQuery(), charset, false);
        this.encodedFragment = uri.getRawFragment();
        this.fragment = uri.getFragment();
        this.charset = charset;
    }

    public URIBuilder setScheme(String scheme) {
        this.scheme = !TextUtils.isBlank(scheme) ? scheme : null;
        return this;
    }

    public URIBuilder setSchemeSpecificPart(String schemeSpecificPart) {
        this.encodedSchemeSpecificPart = schemeSpecificPart;
        return this;
    }

    public URIBuilder setSchemeSpecificPart(String schemeSpecificPart, NameValuePair ... nvps) {
        return this.setSchemeSpecificPart(schemeSpecificPart, nvps != null ? Arrays.asList(nvps) : null);
    }

    public URIBuilder setSchemeSpecificPart(String schemeSpecificPart, List<NameValuePair> nvps) {
        this.encodedSchemeSpecificPart = null;
        if (!TextUtils.isBlank(schemeSpecificPart)) {
            StringBuilder sb = new StringBuilder(schemeSpecificPart);
            if (nvps != null && !nvps.isEmpty()) {
                sb.append("?");
                URIBuilder.formatQuery(sb, nvps, this.charset, false);
            }
            this.encodedSchemeSpecificPart = sb.toString();
        }
        return this;
    }

    public URIBuilder setUserInfo(String userInfo) {
        this.userInfo = !TextUtils.isBlank(userInfo) ? userInfo : null;
        this.encodedSchemeSpecificPart = null;
        this.encodedAuthority = null;
        this.encodedUserInfo = null;
        return this;
    }

    @Deprecated
    public URIBuilder setUserInfo(String username, String password) {
        return this.setUserInfo(username + ':' + password);
    }

    public URIBuilder setHost(InetAddress host) {
        this.host = host != null ? host.getHostAddress() : null;
        this.encodedSchemeSpecificPart = null;
        this.encodedAuthority = null;
        return this;
    }

    public URIBuilder setHost(String host) {
        this.host = host;
        this.encodedSchemeSpecificPart = null;
        this.encodedAuthority = null;
        return this;
    }

    public URIBuilder setHttpHost(HttpHost httpHost) {
        this.setScheme(httpHost.getSchemeName());
        this.setHost(httpHost.getHostName());
        this.setPort(httpHost.getPort());
        return this;
    }

    public URIBuilder setPort(int port) {
        this.port = port < 0 ? -1 : port;
        this.encodedSchemeSpecificPart = null;
        this.encodedAuthority = null;
        return this;
    }

    public URIBuilder setPath(String path) {
        this.setPathSegments(path != null ? URIBuilder.splitPath(path) : null);
        this.pathRootless = path != null && !path.startsWith("/");
        return this;
    }

    public URIBuilder appendPath(String path) {
        if (path != null) {
            this.appendPathSegments(URIBuilder.splitPath(path));
        }
        return this;
    }

    public URIBuilder setPathSegments(String ... pathSegments) {
        return this.setPathSegments(Arrays.asList(pathSegments));
    }

    public URIBuilder appendPathSegments(String ... pathSegments) {
        return this.appendPathSegments(Arrays.asList(pathSegments));
    }

    public URIBuilder setPathSegmentsRootless(String ... pathSegments) {
        return this.setPathSegmentsRootless(Arrays.asList(pathSegments));
    }

    public URIBuilder setPathSegments(List<String> pathSegments) {
        this.pathSegments = pathSegments != null && !pathSegments.isEmpty() ? new ArrayList<String>(pathSegments) : null;
        this.encodedSchemeSpecificPart = null;
        this.encodedPath = null;
        this.pathRootless = false;
        return this;
    }

    public URIBuilder appendPathSegments(List<String> pathSegments) {
        if (pathSegments != null && !pathSegments.isEmpty()) {
            if (this.pathSegments == null) {
                this.pathSegments = new ArrayList<String>();
            }
            this.pathSegments.addAll(pathSegments);
            this.encodedSchemeSpecificPart = null;
            this.encodedPath = null;
        }
        return this;
    }

    public URIBuilder setPathSegmentsRootless(List<String> pathSegments) {
        this.pathSegments = pathSegments != null && !pathSegments.isEmpty() ? new ArrayList<String>(pathSegments) : null;
        this.encodedSchemeSpecificPart = null;
        this.encodedPath = null;
        this.pathRootless = true;
        return this;
    }

    public URIBuilder removeQuery() {
        this.queryParams = null;
        this.query = null;
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        return this;
    }

    public URIBuilder setParameters(List<NameValuePair> nameValuePairs) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        } else {
            this.queryParams.clear();
        }
        if (nameValuePairs != null) {
            this.queryParams.addAll(nameValuePairs);
        }
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder addParameters(List<NameValuePair> nameValuePairs) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        }
        if (nameValuePairs != null) {
            this.queryParams.addAll(nameValuePairs);
        }
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder setParameters(NameValuePair ... nameValuePairs) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        } else {
            this.queryParams.clear();
        }
        if (nameValuePairs != null) {
            Collections.addAll(this.queryParams, nameValuePairs);
        }
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder addParameter(String param, String value) {
        return this.addParameter(new BasicNameValuePair(param, value));
    }

    public URIBuilder addParameter(NameValuePair nameValuePair) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        }
        if (nameValuePair != null) {
            this.queryParams.add(nameValuePair);
        }
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder removeParameter(String param) {
        Args.notNull(param, "param");
        if (this.queryParams != null && !this.queryParams.isEmpty()) {
            this.queryParams.removeIf(nvp -> nvp.getName().equals(param));
        }
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder setParameter(String param, String value) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        }
        if (!this.queryParams.isEmpty()) {
            this.queryParams.removeIf(nvp -> nvp.getName().equals(param));
        }
        this.queryParams.add(new BasicNameValuePair(param, value));
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder clearParameters() {
        this.queryParams = null;
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        return this;
    }

    public URIBuilder setCustomQuery(String query) {
        this.query = !TextUtils.isBlank(query) ? query : null;
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.queryParams = null;
        return this;
    }

    public URIBuilder setFragment(String fragment) {
        this.fragment = !TextUtils.isBlank(fragment) ? fragment : null;
        this.encodedFragment = null;
        return this;
    }

    public boolean isAbsolute() {
        return this.scheme != null;
    }

    public boolean isOpaque() {
        return this.pathSegments == null && this.encodedPath == null;
    }

    public String getScheme() {
        return this.scheme;
    }

    public String getSchemeSpecificPart() {
        return this.encodedSchemeSpecificPart;
    }

    public String getUserInfo() {
        return this.userInfo;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public boolean isPathEmpty() {
        return !(this.pathSegments != null && !this.pathSegments.isEmpty() || this.encodedPath != null && !this.encodedPath.isEmpty());
    }

    public List<String> getPathSegments() {
        return this.pathSegments != null ? new ArrayList<String>(this.pathSegments) : new ArrayList();
    }

    public String getPath() {
        if (this.pathSegments == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (String segment : this.pathSegments) {
            result.append('/').append(segment);
        }
        return result.toString();
    }

    public boolean isQueryEmpty() {
        return (this.queryParams == null || this.queryParams.isEmpty()) && this.encodedQuery == null;
    }

    public List<NameValuePair> getQueryParams() {
        return this.queryParams != null ? new ArrayList<NameValuePair>(this.queryParams) : new ArrayList();
    }

    public NameValuePair getFirstQueryParam(String name) {
        return this.queryParams.stream().filter(e -> name.equals(e.getName())).findFirst().orElse(null);
    }

    public String getFragment() {
        return this.fragment;
    }

    public URIBuilder normalizeSyntax() {
        String scheme = this.scheme;
        if (scheme != null) {
            this.scheme = TextUtils.toLowerCase(scheme);
        }
        if (this.pathRootless) {
            return this;
        }
        this.encodedSchemeSpecificPart = null;
        this.encodedAuthority = null;
        this.encodedUserInfo = null;
        this.encodedPath = null;
        this.encodedQuery = null;
        this.encodedFragment = null;
        String host = this.host;
        if (host != null) {
            this.host = TextUtils.toLowerCase(host);
        }
        if (this.pathSegments != null) {
            List<String> inputSegments = this.pathSegments;
            if (!inputSegments.isEmpty()) {
                String lastSegment;
                LinkedList<String> outputSegments = new LinkedList<String>();
                for (String inputSegment : inputSegments) {
                    if (inputSegment.isEmpty() || ".".equals(inputSegment)) continue;
                    if ("..".equals(inputSegment)) {
                        if (outputSegments.isEmpty()) continue;
                        outputSegments.removeLast();
                        continue;
                    }
                    outputSegments.addLast(inputSegment);
                }
                if (!inputSegments.isEmpty() && (lastSegment = inputSegments.get(inputSegments.size() - 1)).isEmpty()) {
                    outputSegments.addLast("");
                }
                this.pathSegments = outputSegments;
            } else {
                this.pathSegments = Collections.singletonList("");
            }
        }
        return this;
    }

    public String toString() {
        return this.buildString();
    }

    static {
        QUERY_PARAM_SEPARATORS.set(38);
        QUERY_PARAM_SEPARATORS.set(61);
        QUERY_VALUE_SEPARATORS.set(38);
        PATH_SEPARATORS.set(47);
    }
}

