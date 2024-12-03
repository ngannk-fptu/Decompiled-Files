/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.HostPort
 *  org.eclipse.jetty.util.Index
 *  org.eclipse.jetty.util.Index$Builder
 *  org.eclipse.jetty.util.StringUtil
 *  org.eclipse.jetty.util.TypeUtil
 *  org.eclipse.jetty.util.URIUtil
 *  org.eclipse.jetty.util.UrlEncoded
 */
package org.eclipse.jetty.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.UriCompliance;
import org.eclipse.jetty.util.HostPort;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.UrlEncoded;

public interface HttpURI {
    public static Mutable build() {
        return new Mutable();
    }

    public static Mutable build(HttpURI uri) {
        return new Mutable(uri);
    }

    public static Mutable build(HttpURI uri, String pathQuery) {
        return new Mutable(uri, pathQuery);
    }

    public static Mutable build(HttpURI uri, String path, String param, String query) {
        return new Mutable(uri, path, param, query);
    }

    public static Mutable build(URI uri) {
        return new Mutable(uri);
    }

    public static Mutable build(String uri) {
        return new Mutable(uri);
    }

    public static Immutable from(URI uri) {
        return new Mutable(uri).asImmutable();
    }

    public static Immutable from(String uri) {
        return new Mutable(uri).asImmutable();
    }

    public static Immutable from(String method, String uri) {
        if (HttpMethod.CONNECT.is(method)) {
            return HttpURI.build().uri(method, uri).asImmutable();
        }
        if (uri.startsWith("/")) {
            return HttpURI.build().pathQuery(uri).asImmutable();
        }
        return HttpURI.from(uri);
    }

    public static Immutable from(String scheme, String host, int port, String pathQuery) {
        return new Mutable(scheme, host, port, pathQuery).asImmutable();
    }

    public Immutable asImmutable();

    public String asString();

    public String getAuthority();

    public String getDecodedPath();

    public String getFragment();

    public String getHost();

    public String getParam();

    public String getPath();

    public String getPathQuery();

    public int getPort();

    public String getQuery();

    public String getScheme();

    public String getUser();

    public boolean hasAuthority();

    public boolean isAbsolute();

    public boolean isAmbiguous();

    public boolean hasViolations();

    public boolean hasViolation(UriCompliance.Violation var1);

    public Collection<UriCompliance.Violation> getViolations();

    default public boolean hasAmbiguousSegment() {
        return this.hasViolation(UriCompliance.Violation.AMBIGUOUS_PATH_SEGMENT);
    }

    default public boolean hasAmbiguousEmptySegment() {
        return this.hasViolation(UriCompliance.Violation.AMBIGUOUS_EMPTY_SEGMENT);
    }

    default public boolean hasAmbiguousSeparator() {
        return this.hasViolation(UriCompliance.Violation.AMBIGUOUS_PATH_SEPARATOR);
    }

    default public boolean hasAmbiguousParameter() {
        return this.hasViolation(UriCompliance.Violation.AMBIGUOUS_PATH_PARAMETER);
    }

    default public boolean hasAmbiguousEncoding() {
        return this.hasViolation(UriCompliance.Violation.AMBIGUOUS_PATH_ENCODING);
    }

    default public boolean hasUtf16Encoding() {
        return this.hasViolation(UriCompliance.Violation.UTF16_ENCODINGS);
    }

    default public URI toURI() {
        try {
            String query = this.getQuery();
            return new URI(this.getScheme(), null, this.getHost(), this.getPort(), this.getPath(), query == null ? null : UrlEncoded.decodeString((String)query), null);
        }
        catch (URISyntaxException x) {
            throw new RuntimeException(x);
        }
    }

    public static class Mutable
    implements HttpURI {
        private static final Index<Boolean> __ambiguousSegments = new Index.Builder().caseSensitive(false).with(".", (Object)Boolean.FALSE).with("%2e", (Object)Boolean.TRUE).with("%u002e", (Object)Boolean.TRUE).with("..", (Object)Boolean.FALSE).with(".%2e", (Object)Boolean.TRUE).with(".%u002e", (Object)Boolean.TRUE).with("%2e.", (Object)Boolean.TRUE).with("%2e%2e", (Object)Boolean.TRUE).with("%2e%u002e", (Object)Boolean.TRUE).with("%u002e.", (Object)Boolean.TRUE).with("%u002e%2e", (Object)Boolean.TRUE).with("%u002e%u002e", (Object)Boolean.TRUE).build();
        private String _scheme;
        private String _user;
        private String _host;
        private int _port;
        private String _path;
        private String _param;
        private String _query;
        private String _fragment;
        private String _uri;
        private String _decodedPath;
        private final EnumSet<UriCompliance.Violation> _violations = EnumSet.noneOf(UriCompliance.Violation.class);
        private boolean _emptySegment;

        private Mutable() {
        }

        private Mutable(HttpURI uri) {
            this.uri(uri);
        }

        private Mutable(HttpURI baseURI, String pathQuery) {
            this._uri = null;
            this._scheme = baseURI.getScheme();
            this._user = baseURI.getUser();
            this._host = baseURI.getHost();
            this._port = baseURI.getPort();
            if (pathQuery != null) {
                this.parse(State.PATH, pathQuery);
            }
        }

        private Mutable(HttpURI baseURI, String path, String param, String query) {
            this._uri = null;
            this._scheme = baseURI.getScheme();
            this._user = baseURI.getUser();
            this._host = baseURI.getHost();
            this._port = baseURI.getPort();
            if (path != null) {
                this.parse(State.PATH, path);
            }
            if (param != null) {
                this._param = param;
            }
            if (query != null) {
                this._query = query;
            }
        }

        private Mutable(String uri) {
            this._port = -1;
            this.parse(State.START, uri);
        }

        private Mutable(URI uri) {
            this._uri = null;
            this._scheme = uri.getScheme();
            this._host = uri.getHost();
            if (this._host == null && uri.getRawSchemeSpecificPart().startsWith("//")) {
                this._host = "";
            }
            this._port = uri.getPort();
            this._user = uri.getUserInfo();
            String path = uri.getRawPath();
            if (path != null) {
                this.parse(State.PATH, path);
            }
            this._query = uri.getRawQuery();
            this._fragment = uri.getRawFragment();
        }

        private Mutable(String scheme, String host, int port, String pathQuery) {
            this._uri = null;
            this._scheme = scheme;
            this._host = host;
            this._port = port;
            if (pathQuery != null) {
                this.parse(State.PATH, pathQuery);
            }
        }

        @Override
        public Immutable asImmutable() {
            return new Immutable(this);
        }

        @Override
        public String asString() {
            return this.asImmutable().toString();
        }

        public Mutable authority(String host, int port) {
            if (host != null && !this.isPathValidForAuthority(this._path)) {
                throw new IllegalArgumentException("Relative path with authority");
            }
            this._user = null;
            this._host = host;
            this._port = port;
            this._uri = null;
            return this;
        }

        public Mutable authority(String hostPort) {
            if (hostPort != null && !this.isPathValidForAuthority(this._path)) {
                throw new IllegalArgumentException("Relative path with authority");
            }
            HostPort hp = new HostPort(hostPort);
            this._user = null;
            this._host = hp.getHost();
            this._port = hp.getPort();
            this._uri = null;
            return this;
        }

        private boolean isPathValidForAuthority(String path) {
            if (path == null) {
                return true;
            }
            if (path.isEmpty() || "*".equals(path)) {
                return true;
            }
            return path.startsWith("/");
        }

        public Mutable clear() {
            this._scheme = null;
            this._user = null;
            this._host = null;
            this._port = -1;
            this._path = null;
            this._param = null;
            this._query = null;
            this._fragment = null;
            this._uri = null;
            this._decodedPath = null;
            this._emptySegment = false;
            this._violations.clear();
            return this;
        }

        public Mutable decodedPath(String path) {
            this._uri = null;
            this._path = URIUtil.encodePath((String)path);
            this._decodedPath = path;
            return this;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof HttpURI)) {
                return false;
            }
            return this.asString().equals(((HttpURI)o).asString());
        }

        public Mutable fragment(String fragment) {
            this._fragment = fragment;
            return this;
        }

        @Override
        public String getAuthority() {
            if (this._port > 0) {
                return this._host + ":" + this._port;
            }
            return this._host;
        }

        @Override
        public String getDecodedPath() {
            if (this._decodedPath == null && this._path != null) {
                this._decodedPath = URIUtil.canonicalPath((String)URIUtil.decodePath((String)this._path));
            }
            return this._decodedPath;
        }

        @Override
        public String getFragment() {
            return this._fragment;
        }

        @Override
        public String getHost() {
            return this._host;
        }

        @Override
        public String getParam() {
            return this._param;
        }

        @Override
        public String getPath() {
            return this._path;
        }

        @Override
        public String getPathQuery() {
            if (this._query == null) {
                return this._path;
            }
            return this._path + "?" + this._query;
        }

        @Override
        public int getPort() {
            return this._port;
        }

        @Override
        public String getQuery() {
            return this._query;
        }

        @Override
        public String getScheme() {
            return this._scheme;
        }

        @Override
        public String getUser() {
            return this._user;
        }

        @Override
        public boolean hasAuthority() {
            return this._host != null;
        }

        public int hashCode() {
            return this.asString().hashCode();
        }

        public Mutable host(String host) {
            if (host != null && !this.isPathValidForAuthority(this._path)) {
                throw new IllegalArgumentException("Relative path with authority");
            }
            this._host = host;
            this._uri = null;
            return this;
        }

        @Override
        public boolean isAbsolute() {
            return this._scheme != null && !this._scheme.isEmpty();
        }

        @Override
        public boolean isAmbiguous() {
            return !this._violations.isEmpty() && (this._violations.size() != 1 || !this._violations.contains(UriCompliance.Violation.UTF16_ENCODINGS));
        }

        @Override
        public boolean hasViolations() {
            return !this._violations.isEmpty();
        }

        @Override
        public boolean hasViolation(UriCompliance.Violation violation) {
            return this._violations.contains(violation);
        }

        @Override
        public Collection<UriCompliance.Violation> getViolations() {
            return Collections.unmodifiableCollection(this._violations);
        }

        public Mutable normalize() {
            HttpScheme scheme;
            HttpScheme httpScheme = scheme = this._scheme == null ? null : (HttpScheme)((Object)HttpScheme.CACHE.get(this._scheme));
            if (scheme != null && this._port == scheme.getDefaultPort()) {
                this._port = 0;
                this._uri = null;
            }
            return this;
        }

        public Mutable param(String param) {
            this._param = param;
            if (this._path != null && this._param != null && !this._path.contains(this._param)) {
                this._path = this._path + ";" + this._param;
            }
            this._uri = null;
            return this;
        }

        public Mutable path(String path) {
            if (this.hasAuthority() && !this.isPathValidForAuthority(path)) {
                throw new IllegalArgumentException("Relative path with authority");
            }
            this._uri = null;
            this._path = path;
            this._decodedPath = null;
            return this;
        }

        public Mutable pathQuery(String pathQuery) {
            if (this.hasAuthority() && !this.isPathValidForAuthority(pathQuery)) {
                throw new IllegalArgumentException("Relative path with authority");
            }
            this._uri = null;
            this._path = null;
            this._decodedPath = null;
            this._param = null;
            this._query = null;
            if (pathQuery != null) {
                this.parse(State.PATH, pathQuery);
            }
            return this;
        }

        public Mutable port(int port) {
            this._port = port;
            this._uri = null;
            return this;
        }

        public Mutable query(String query) {
            this._query = query;
            this._uri = null;
            return this;
        }

        public Mutable scheme(HttpScheme scheme) {
            return this.scheme(scheme.asString());
        }

        public Mutable scheme(String scheme) {
            this._scheme = scheme;
            this._uri = null;
            return this;
        }

        public String toString() {
            return this.asString();
        }

        @Override
        public URI toURI() {
            try {
                return new URI(this._scheme, null, this._host, this._port, this._path, this._query == null ? null : UrlEncoded.decodeString((String)this._query), null);
            }
            catch (URISyntaxException x) {
                throw new RuntimeException(x);
            }
        }

        public Mutable uri(HttpURI uri) {
            this._scheme = uri.getScheme();
            this._user = uri.getUser();
            this._host = uri.getHost();
            this._port = uri.getPort();
            this._path = uri.getPath();
            this._param = uri.getParam();
            this._query = uri.getQuery();
            this._uri = null;
            this._decodedPath = uri.getDecodedPath();
            this._violations.addAll(uri.getViolations());
            return this;
        }

        public Mutable uri(String uri) {
            this.clear();
            this._uri = uri;
            this.parse(State.START, uri);
            return this;
        }

        public Mutable uri(String method, String uri) {
            if (HttpMethod.CONNECT.is(method)) {
                this.clear();
                this.parse(State.HOST, uri);
            } else if (uri.startsWith("/")) {
                this.clear();
                this.pathQuery(uri);
            } else {
                this.uri(uri);
            }
            return this;
        }

        public Mutable uri(String uri, int offset, int length) {
            this.clear();
            int end = offset + length;
            this._uri = uri.substring(offset, end);
            this.parse(State.START, uri);
            return this;
        }

        public Mutable user(String user) {
            this._user = user;
            this._uri = null;
            return this;
        }

        private void parse(State state, String uri) {
            int mark = 0;
            int pathMark = 0;
            int segment = 0;
            boolean encodedPath = false;
            boolean encodedUtf16 = false;
            int encodedCharacters = 0;
            int encodedValue = 0;
            boolean dot = false;
            int end = uri.length();
            this._emptySegment = false;
            block75: for (int i = 0; i < end; ++i) {
                char c = uri.charAt(i);
                switch (state) {
                    case START: {
                        switch (c) {
                            case '/': {
                                mark = i;
                                state = State.HOST_OR_PATH;
                                continue block75;
                            }
                            case ';': {
                                this.checkSegment(uri, segment, i, true);
                                mark = i + 1;
                                state = State.PARAM;
                                continue block75;
                            }
                            case '?': {
                                this.checkSegment(uri, segment, i, false);
                                this._path = "";
                                mark = i + 1;
                                state = State.QUERY;
                                continue block75;
                            }
                            case '#': {
                                this.checkSegment(uri, segment, i, false);
                                this._path = "";
                                mark = i + 1;
                                state = State.FRAGMENT;
                                continue block75;
                            }
                            case '*': {
                                this._path = "*";
                                state = State.ASTERISK;
                                continue block75;
                            }
                            case '%': {
                                encodedPath = true;
                                encodedCharacters = 2;
                                encodedValue = 0;
                                pathMark = segment = i;
                                mark = segment;
                                state = State.PATH;
                                continue block75;
                            }
                            case '.': {
                                dot = true;
                                pathMark = segment = i;
                                state = State.PATH;
                                continue block75;
                            }
                        }
                        mark = i;
                        if (this._scheme == null) {
                            state = State.SCHEME_OR_PATH;
                            continue block75;
                        }
                        pathMark = segment = i;
                        state = State.PATH;
                        continue block75;
                    }
                    case SCHEME_OR_PATH: {
                        switch (c) {
                            case ':': {
                                this._scheme = uri.substring(mark, i);
                                state = State.START;
                                continue block75;
                            }
                            case '/': {
                                segment = i + 1;
                                state = State.PATH;
                                continue block75;
                            }
                            case ';': {
                                mark = i + 1;
                                state = State.PARAM;
                                continue block75;
                            }
                            case '?': {
                                this._path = uri.substring(mark, i);
                                mark = i + 1;
                                state = State.QUERY;
                                continue block75;
                            }
                            case '%': {
                                encodedPath = true;
                                encodedCharacters = 2;
                                encodedValue = 0;
                                state = State.PATH;
                                continue block75;
                            }
                            case '#': {
                                this._path = uri.substring(mark, i);
                                state = State.FRAGMENT;
                                continue block75;
                            }
                        }
                        continue block75;
                    }
                    case HOST_OR_PATH: {
                        switch (c) {
                            case '/': {
                                this._host = "";
                                mark = i + 1;
                                state = State.HOST;
                                continue block75;
                            }
                            case '#': 
                            case '%': 
                            case '.': 
                            case ';': 
                            case '?': 
                            case '@': {
                                --i;
                                pathMark = mark;
                                segment = mark + 1;
                                state = State.PATH;
                                continue block75;
                            }
                        }
                        pathMark = mark;
                        segment = mark + 1;
                        state = State.PATH;
                        continue block75;
                    }
                    case HOST: {
                        switch (c) {
                            case '/': {
                                this._host = uri.substring(mark, i);
                                pathMark = mark = i;
                                segment = mark + 1;
                                state = State.PATH;
                                continue block75;
                            }
                            case ':': {
                                if (i > mark) {
                                    this._host = uri.substring(mark, i);
                                }
                                mark = i + 1;
                                state = State.PORT;
                                continue block75;
                            }
                            case '@': {
                                if (this._user != null) {
                                    throw new IllegalArgumentException("Bad authority");
                                }
                                this._user = uri.substring(mark, i);
                                mark = i + 1;
                                continue block75;
                            }
                            case '[': {
                                state = State.IPV6;
                                continue block75;
                            }
                        }
                        continue block75;
                    }
                    case IPV6: {
                        switch (c) {
                            case '/': {
                                throw new IllegalArgumentException("No closing ']' for ipv6 in " + uri);
                            }
                            case ']': {
                                c = uri.charAt(++i);
                                this._host = uri.substring(mark, i);
                                if (c == ':') {
                                    mark = i + 1;
                                    state = State.PORT;
                                    continue block75;
                                }
                                pathMark = mark = i;
                                state = State.PATH;
                                continue block75;
                            }
                        }
                        continue block75;
                    }
                    case PORT: {
                        if (c == '@') {
                            if (this._user != null) {
                                throw new IllegalArgumentException("Bad authority");
                            }
                            this._user = this._host + ":" + uri.substring(mark, i);
                            mark = i + 1;
                            state = State.HOST;
                            continue block75;
                        }
                        if (c != '/') continue block75;
                        this._port = TypeUtil.parseInt((String)uri, (int)mark, (int)(i - mark), (int)10);
                        pathMark = mark = i;
                        segment = i + 1;
                        state = State.PATH;
                        continue block75;
                    }
                    case PATH: {
                        if (encodedCharacters > 0) {
                            if (encodedCharacters == 2 && c == 'u' && !encodedUtf16) {
                                this._violations.add(UriCompliance.Violation.UTF16_ENCODINGS);
                                encodedUtf16 = true;
                                encodedCharacters = 4;
                                continue block75;
                            }
                            encodedValue = (encodedValue << 4) + TypeUtil.convertHexDigit((char)c);
                            if (--encodedCharacters != 0) continue block75;
                            switch (encodedValue) {
                                case 0: {
                                    throw new IllegalArgumentException("Illegal character in path");
                                }
                                case 47: {
                                    this._violations.add(UriCompliance.Violation.AMBIGUOUS_PATH_SEPARATOR);
                                    continue block75;
                                }
                                case 37: {
                                    this._violations.add(UriCompliance.Violation.AMBIGUOUS_PATH_ENCODING);
                                    continue block75;
                                }
                            }
                            continue block75;
                        }
                        switch (c) {
                            case ';': {
                                this.checkSegment(uri, segment, i, true);
                                mark = i + 1;
                                state = State.PARAM;
                                continue block75;
                            }
                            case '?': {
                                this.checkSegment(uri, segment, i, false);
                                this._path = uri.substring(pathMark, i);
                                mark = i + 1;
                                state = State.QUERY;
                                continue block75;
                            }
                            case '#': {
                                this.checkSegment(uri, segment, i, false);
                                this._path = uri.substring(pathMark, i);
                                mark = i + 1;
                                state = State.FRAGMENT;
                                continue block75;
                            }
                            case '/': {
                                if (i != 0) {
                                    this.checkSegment(uri, segment, i, false);
                                }
                                segment = i + 1;
                                continue block75;
                            }
                            case '.': {
                                dot |= segment == i;
                                continue block75;
                            }
                            case '%': {
                                encodedPath = true;
                                encodedUtf16 = false;
                                encodedCharacters = 2;
                                encodedValue = 0;
                                continue block75;
                            }
                        }
                        continue block75;
                    }
                    case PARAM: {
                        switch (c) {
                            case '?': {
                                this._path = uri.substring(pathMark, i);
                                this._param = uri.substring(mark, i);
                                mark = i + 1;
                                state = State.QUERY;
                                continue block75;
                            }
                            case '#': {
                                this._path = uri.substring(pathMark, i);
                                this._param = uri.substring(mark, i);
                                mark = i + 1;
                                state = State.FRAGMENT;
                                continue block75;
                            }
                            case '/': {
                                encodedPath = true;
                                segment = i + 1;
                                state = State.PATH;
                                continue block75;
                            }
                            case ';': {
                                mark = i + 1;
                                continue block75;
                            }
                        }
                        continue block75;
                    }
                    case QUERY: {
                        if (c != '#') continue block75;
                        this._query = uri.substring(mark, i);
                        mark = i + 1;
                        state = State.FRAGMENT;
                        continue block75;
                    }
                    case ASTERISK: {
                        throw new IllegalArgumentException("Bad character '*'");
                    }
                    case FRAGMENT: {
                        this._fragment = uri.substring(mark, end);
                        i = end;
                        continue block75;
                    }
                    default: {
                        throw new IllegalStateException(state.toString());
                    }
                }
            }
            switch (state) {
                case START: {
                    this._path = "";
                    this.checkSegment(uri, segment, end, false);
                    break;
                }
                case ASTERISK: {
                    break;
                }
                case SCHEME_OR_PATH: 
                case HOST_OR_PATH: {
                    this._path = uri.substring(mark, end);
                    break;
                }
                case HOST: {
                    if (end <= mark) break;
                    this._host = uri.substring(mark, end);
                    break;
                }
                case IPV6: {
                    throw new IllegalArgumentException("No closing ']' for ipv6 in " + uri);
                }
                case PORT: {
                    this._port = TypeUtil.parseInt((String)uri, (int)mark, (int)(end - mark), (int)10);
                    break;
                }
                case PARAM: {
                    this._path = uri.substring(pathMark, end);
                    this._param = uri.substring(mark, end);
                    break;
                }
                case PATH: {
                    this.checkSegment(uri, segment, end, false);
                    this._path = uri.substring(pathMark, end);
                    break;
                }
                case QUERY: {
                    this._query = uri.substring(mark, end);
                    break;
                }
                case FRAGMENT: {
                    this._fragment = uri.substring(mark, end);
                    break;
                }
                default: {
                    throw new IllegalStateException(state.toString());
                }
            }
            if (!encodedPath && !dot) {
                this._decodedPath = this._param == null ? this._path : this._path.substring(0, this._path.length() - this._param.length() - 1);
            } else if (this._path != null) {
                String decodedNonCanonical = URIUtil.decodePath((String)this._path);
                this._decodedPath = URIUtil.canonicalPath((String)decodedNonCanonical);
                if (this._decodedPath == null) {
                    throw new IllegalArgumentException("Bad URI");
                }
            }
        }

        private void checkSegment(String uri, int segment, int end, boolean param) {
            Boolean ambiguous;
            if (this._emptySegment) {
                this._violations.add(UriCompliance.Violation.AMBIGUOUS_EMPTY_SEGMENT);
            }
            if (end == segment) {
                if (end >= uri.length() || "#?".indexOf(uri.charAt(end)) >= 0) {
                    return;
                }
                if (segment == 0) {
                    this._violations.add(UriCompliance.Violation.AMBIGUOUS_EMPTY_SEGMENT);
                    return;
                }
                if (!this._emptySegment) {
                    this._emptySegment = true;
                    return;
                }
            }
            if ((ambiguous = (Boolean)__ambiguousSegments.get(uri, segment, end - segment)) != null) {
                if (Boolean.TRUE.equals(ambiguous)) {
                    this._violations.add(UriCompliance.Violation.AMBIGUOUS_PATH_SEGMENT);
                }
                if (param) {
                    this._violations.add(UriCompliance.Violation.AMBIGUOUS_PATH_PARAMETER);
                }
            }
        }

        private static enum State {
            START,
            HOST_OR_PATH,
            SCHEME_OR_PATH,
            HOST,
            IPV6,
            PORT,
            PATH,
            PARAM,
            QUERY,
            FRAGMENT,
            ASTERISK;

        }
    }

    public static class Immutable
    implements HttpURI {
        private final String _scheme;
        private final String _user;
        private final String _host;
        private final int _port;
        private final String _path;
        private final String _param;
        private final String _query;
        private final String _fragment;
        private String _uri;
        private String _decodedPath;
        private final EnumSet<UriCompliance.Violation> _violations = EnumSet.noneOf(UriCompliance.Violation.class);

        private Immutable(Mutable builder) {
            this._scheme = builder._scheme;
            this._user = builder._user;
            this._host = builder._host;
            this._port = builder._port;
            this._path = builder._path;
            this._param = builder._param;
            this._query = builder._query;
            this._fragment = builder._fragment;
            this._uri = builder._uri;
            this._decodedPath = builder._decodedPath;
            this._violations.addAll(builder._violations);
        }

        private Immutable(String uri) {
            this._scheme = null;
            this._user = null;
            this._host = null;
            this._port = -1;
            this._path = uri;
            this._param = null;
            this._query = null;
            this._fragment = null;
            this._uri = uri;
            this._decodedPath = null;
        }

        @Override
        public Immutable asImmutable() {
            return this;
        }

        @Override
        public String asString() {
            if (this._uri == null) {
                StringBuilder out = new StringBuilder();
                if (this._scheme != null) {
                    out.append(this._scheme).append(':');
                }
                if (this._host != null) {
                    out.append("//");
                    if (this._user != null) {
                        out.append(this._user).append('@');
                    }
                    out.append(this._host);
                }
                if (this._port > 0) {
                    out.append(':').append(this._port);
                }
                if (this._path != null) {
                    out.append(this._path);
                }
                if (this._query != null) {
                    out.append('?').append(this._query);
                }
                if (this._fragment != null) {
                    out.append('#').append(this._fragment);
                }
                this._uri = out.length() > 0 ? out.toString() : "";
            }
            return this._uri;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof HttpURI)) {
                return false;
            }
            return this.asString().equals(((HttpURI)o).asString());
        }

        @Override
        public String getAuthority() {
            if (this._port > 0) {
                return this._host + ":" + this._port;
            }
            return this._host;
        }

        @Override
        public String getDecodedPath() {
            if (this._decodedPath == null && this._path != null) {
                this._decodedPath = URIUtil.canonicalPath((String)URIUtil.decodePath((String)this._path));
            }
            return this._decodedPath;
        }

        @Override
        public String getFragment() {
            return this._fragment;
        }

        @Override
        public String getHost() {
            if (this._host != null && this._host.isEmpty()) {
                return null;
            }
            return this._host;
        }

        @Override
        public String getParam() {
            return this._param;
        }

        @Override
        public String getPath() {
            return this._path;
        }

        @Override
        public String getPathQuery() {
            if (this._query == null) {
                return this._path;
            }
            return this._path + "?" + this._query;
        }

        @Override
        public int getPort() {
            return this._port;
        }

        @Override
        public String getQuery() {
            return this._query;
        }

        @Override
        public String getScheme() {
            return this._scheme;
        }

        @Override
        public String getUser() {
            return this._user;
        }

        @Override
        public boolean hasAuthority() {
            return this._host != null;
        }

        public int hashCode() {
            return this.asString().hashCode();
        }

        @Override
        public boolean isAbsolute() {
            return !StringUtil.isEmpty((String)this._scheme);
        }

        @Override
        public boolean isAmbiguous() {
            return !this._violations.isEmpty() && (this._violations.size() != 1 || !this._violations.contains(UriCompliance.Violation.UTF16_ENCODINGS));
        }

        @Override
        public boolean hasViolations() {
            return !this._violations.isEmpty();
        }

        @Override
        public boolean hasViolation(UriCompliance.Violation violation) {
            return this._violations.contains(violation);
        }

        @Override
        public Collection<UriCompliance.Violation> getViolations() {
            return Collections.unmodifiableCollection(this._violations);
        }

        public String toString() {
            return this.asString();
        }

        @Override
        public URI toURI() {
            try {
                return new URI(this._scheme, null, this._host, this._port, this._path, this._query == null ? null : UrlEncoded.decodeString((String)this._query), this._fragment);
            }
            catch (URISyntaxException x) {
                throw new RuntimeException(x);
            }
        }
    }
}

