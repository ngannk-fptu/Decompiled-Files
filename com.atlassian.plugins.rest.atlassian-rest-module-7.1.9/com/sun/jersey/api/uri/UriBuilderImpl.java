/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.uri;

import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

public class UriBuilderImpl
extends UriBuilder {
    private String scheme;
    private String ssp;
    private String authority;
    private String userInfo;
    private String host;
    private int port = -1;
    private final StringBuilder path;
    private MultivaluedMap<String, String> matrixParams;
    private final StringBuilder query;
    private MultivaluedMap<String, String> queryParams;
    private String fragment;

    public UriBuilderImpl() {
        this.path = new StringBuilder();
        this.query = new StringBuilder();
    }

    private UriBuilderImpl(UriBuilderImpl that) {
        this.scheme = that.scheme;
        this.ssp = that.ssp;
        this.authority = that.authority;
        this.userInfo = that.userInfo;
        this.host = that.host;
        this.port = that.port;
        this.path = new StringBuilder(that.path);
        this.matrixParams = that.matrixParams == null ? null : new MultivaluedMapImpl(that.matrixParams);
        this.query = new StringBuilder(that.query);
        this.queryParams = that.queryParams == null ? null : new MultivaluedMapImpl(that.queryParams);
        this.fragment = that.fragment;
    }

    @Override
    public UriBuilder clone() {
        return new UriBuilderImpl(this);
    }

    @Override
    public UriBuilder uri(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("URI parameter is null");
        }
        if (uri.getRawFragment() != null) {
            this.fragment = uri.getRawFragment();
        }
        if (uri.isOpaque()) {
            this.scheme = uri.getScheme();
            this.ssp = uri.getRawSchemeSpecificPart();
            return this;
        }
        if (uri.getScheme() == null) {
            if (this.ssp != null && uri.getRawSchemeSpecificPart() != null) {
                this.ssp = uri.getRawSchemeSpecificPart();
                return this;
            }
        } else {
            this.scheme = uri.getScheme();
        }
        this.ssp = null;
        if (uri.getRawAuthority() != null) {
            if (uri.getRawUserInfo() == null && uri.getHost() == null && uri.getPort() == -1) {
                this.authority = uri.getRawAuthority();
                this.userInfo = null;
                this.host = null;
                this.port = -1;
            } else {
                this.authority = null;
                if (uri.getRawUserInfo() != null) {
                    this.userInfo = uri.getRawUserInfo();
                }
                if (uri.getHost() != null) {
                    this.host = uri.getHost();
                }
                if (uri.getPort() != -1) {
                    this.port = uri.getPort();
                }
            }
        }
        if (uri.getRawPath() != null && uri.getRawPath().length() > 0) {
            this.path.setLength(0);
            this.path.append(uri.getRawPath());
        }
        if (uri.getRawQuery() != null && uri.getRawQuery().length() > 0) {
            this.query.setLength(0);
            this.query.append(uri.getRawQuery());
        }
        return this;
    }

    @Override
    public UriBuilder scheme(String scheme) {
        if (scheme != null) {
            this.scheme = scheme;
            UriComponent.validate(scheme, UriComponent.Type.SCHEME, true);
        } else {
            this.scheme = null;
        }
        return this;
    }

    @Override
    public UriBuilder schemeSpecificPart(String ssp) {
        URI uri;
        if (ssp == null) {
            throw new IllegalArgumentException("Scheme specific part parameter is null");
        }
        StringBuilder sb = new StringBuilder();
        if (this.scheme != null) {
            sb.append(this.scheme).append(':');
        }
        if (ssp != null) {
            sb.append(ssp);
        }
        if (this.fragment != null && this.fragment.length() > 0) {
            sb.append('#').append(this.fragment);
        }
        if ((uri = this.createURI(sb.toString())).getRawSchemeSpecificPart() != null && uri.getRawPath() == null) {
            this.ssp = uri.getRawSchemeSpecificPart();
        } else {
            this.ssp = null;
            if (uri.getRawAuthority() != null) {
                if (uri.getRawUserInfo() == null && uri.getHost() == null && uri.getPort() == -1) {
                    this.authority = uri.getRawAuthority();
                    this.userInfo = null;
                    this.host = null;
                    this.port = -1;
                } else {
                    this.authority = null;
                    this.userInfo = uri.getRawUserInfo();
                    this.host = uri.getHost();
                    this.port = uri.getPort();
                }
            }
            this.path.setLength(0);
            this.path.append(this.replaceNull(uri.getRawPath()));
            this.query.setLength(0);
            this.query.append(this.replaceNull(uri.getRawQuery()));
        }
        return this;
    }

    @Override
    public UriBuilder userInfo(String ui) {
        this.checkSsp();
        this.userInfo = ui != null ? this.encode(ui, UriComponent.Type.USER_INFO) : null;
        return this;
    }

    @Override
    public UriBuilder host(String host) {
        this.checkSsp();
        if (host != null) {
            if (host.length() == 0) {
                throw new IllegalArgumentException("Invalid host name");
            }
            this.host = this.encode(host, UriComponent.Type.HOST);
        } else {
            this.host = null;
        }
        return this;
    }

    @Override
    public UriBuilder port(int port) {
        this.checkSsp();
        if (port < -1) {
            throw new IllegalArgumentException("Invalid port value");
        }
        this.port = port;
        return this;
    }

    @Override
    public UriBuilder replacePath(String path) {
        this.checkSsp();
        this.path.setLength(0);
        if (path != null) {
            this.appendPath(path);
        }
        return this;
    }

    @Override
    public UriBuilder path(String path) {
        this.checkSsp();
        this.appendPath(path);
        return this;
    }

    @Override
    public UriBuilder path(Class resource) {
        this.checkSsp();
        if (resource == null) {
            throw new IllegalArgumentException("Resource parameter is null");
        }
        Class c = resource;
        Path p = c.getAnnotation(Path.class);
        if (p == null) {
            throw new IllegalArgumentException("The class, " + resource + " is not annotated with @Path");
        }
        this.appendPath(p);
        return this;
    }

    @Override
    public UriBuilder path(Class resource, String methodName) {
        this.checkSsp();
        if (resource == null) {
            throw new IllegalArgumentException("Resource parameter is null");
        }
        if (methodName == null) {
            throw new IllegalArgumentException("MethodName parameter is null");
        }
        Method[] methods = resource.getMethods();
        Method found = null;
        for (Method m : methods) {
            if (!methodName.equals(m.getName())) continue;
            if (found == null) {
                found = m;
                continue;
            }
            throw new IllegalArgumentException();
        }
        if (found == null) {
            throw new IllegalArgumentException("The method named, " + methodName + ", is not specified by " + resource);
        }
        this.appendPath(this.getPath(found));
        return this;
    }

    @Override
    public UriBuilder path(Method method) {
        this.checkSsp();
        if (method == null) {
            throw new IllegalArgumentException("Method is null");
        }
        this.appendPath(this.getPath(method));
        return this;
    }

    private Path getPath(AnnotatedElement ae) {
        Path p = ae.getAnnotation(Path.class);
        if (p == null) {
            throw new IllegalArgumentException("The annotated element, " + ae + " is not annotated with @Path");
        }
        return p;
    }

    @Override
    public UriBuilder segment(String ... segments) throws IllegalArgumentException {
        this.checkSsp();
        if (segments == null) {
            throw new IllegalArgumentException("Segments parameter is null");
        }
        for (String segment : segments) {
            this.appendPath(segment, true);
        }
        return this;
    }

    @Override
    public UriBuilder replaceMatrix(String matrix) {
        this.checkSsp();
        int i = this.path.lastIndexOf("/");
        if (i != -1) {
            i = 0;
        }
        if ((i = this.path.indexOf(";", i)) != -1) {
            this.path.setLength(i + 1);
        } else {
            this.path.append(';');
        }
        if (matrix != null) {
            this.path.append(this.encode(matrix, UriComponent.Type.PATH));
        }
        return this;
    }

    @Override
    public UriBuilder matrixParam(String name, Object ... values) {
        this.checkSsp();
        if (name == null) {
            throw new IllegalArgumentException("Name parameter is null");
        }
        if (values == null) {
            throw new IllegalArgumentException("Value parameter is null");
        }
        if (values.length == 0) {
            return this;
        }
        name = this.encode(name, UriComponent.Type.MATRIX_PARAM);
        if (this.matrixParams == null) {
            for (Object value : values) {
                this.path.append(';').append(name);
                if (value == null) {
                    throw new IllegalArgumentException("One or more of matrix value parameters are null");
                }
                String stringValue = value.toString();
                if (stringValue.length() <= 0) continue;
                this.path.append('=').append(this.encode(stringValue, UriComponent.Type.MATRIX_PARAM));
            }
        } else {
            for (Object value : values) {
                if (value == null) {
                    throw new IllegalArgumentException("One or more of matrix value parameters are null");
                }
                this.matrixParams.add(name, this.encode(value.toString(), UriComponent.Type.MATRIX_PARAM));
            }
        }
        return this;
    }

    @Override
    public UriBuilder replaceMatrixParam(String name, Object ... values) {
        this.checkSsp();
        if (name == null) {
            throw new IllegalArgumentException("Name parameter is null");
        }
        if (this.matrixParams == null) {
            int i = this.path.lastIndexOf("/");
            if (i != -1) {
                i = 0;
            }
            this.matrixParams = UriComponent.decodeMatrix(i != -1 ? this.path.substring(i) : "", false);
            if ((i = this.path.indexOf(";", i)) != -1) {
                this.path.setLength(i);
            }
        }
        name = this.encode(name, UriComponent.Type.MATRIX_PARAM);
        this.matrixParams.remove(name);
        if (values != null) {
            for (Object value : values) {
                if (value == null) {
                    throw new IllegalArgumentException("One or more of matrix value parameters are null");
                }
                this.matrixParams.add(name, this.encode(value.toString(), UriComponent.Type.MATRIX_PARAM));
            }
        }
        return this;
    }

    @Override
    public UriBuilder replaceQuery(String query) {
        this.checkSsp();
        this.query.setLength(0);
        if (query != null) {
            this.query.append(this.encode(query, UriComponent.Type.QUERY));
        }
        return this;
    }

    @Override
    public UriBuilder queryParam(String name, Object ... values) {
        this.checkSsp();
        if (name == null) {
            throw new IllegalArgumentException("Name parameter is null");
        }
        if (values == null) {
            throw new IllegalArgumentException("Value parameter is null");
        }
        if (values.length == 0) {
            return this;
        }
        name = this.encode(name, UriComponent.Type.QUERY_PARAM);
        if (this.queryParams == null) {
            for (Object value : values) {
                if (this.query.length() > 0) {
                    this.query.append('&');
                }
                this.query.append(name);
                if (value == null) {
                    throw new IllegalArgumentException("One or more of query value parameters are null");
                }
                this.query.append('=').append(this.encode(value.toString(), UriComponent.Type.QUERY_PARAM));
            }
        } else {
            for (Object value : values) {
                if (value == null) {
                    throw new IllegalArgumentException("One or more of query value parameters are null");
                }
                this.queryParams.add(name, this.encode(value.toString(), UriComponent.Type.QUERY_PARAM));
            }
        }
        return this;
    }

    @Override
    public UriBuilder replaceQueryParam(String name, Object ... values) {
        this.checkSsp();
        if (this.queryParams == null) {
            this.queryParams = UriComponent.decodeQuery(this.query.toString(), false, false);
            this.query.setLength(0);
        }
        name = this.encode(name, UriComponent.Type.QUERY_PARAM);
        this.queryParams.remove(name);
        if (values == null) {
            return this;
        }
        for (Object value : values) {
            if (value == null) {
                throw new IllegalArgumentException("One or more of query value parameters are null");
            }
            this.queryParams.add(name, this.encode(value.toString(), UriComponent.Type.QUERY_PARAM));
        }
        return this;
    }

    @Override
    public UriBuilder fragment(String fragment) {
        this.fragment = fragment != null ? this.encode(fragment, UriComponent.Type.FRAGMENT) : null;
        return this;
    }

    private void checkSsp() {
        if (this.ssp != null) {
            throw new IllegalArgumentException("Schema specific part is opaque");
        }
    }

    private void appendPath(Path t) {
        if (t == null) {
            throw new IllegalArgumentException("Path is null");
        }
        this.appendPath(t.value());
    }

    private void appendPath(String path) {
        this.appendPath(path, false);
    }

    private void appendPath(String segments, boolean isSegment) {
        boolean segmentStartsWithSlash;
        if (segments == null) {
            throw new IllegalArgumentException("Path segment is null");
        }
        if (segments.length() == 0) {
            return;
        }
        this.encodeMatrix();
        segments = this.encode(segments, isSegment ? UriComponent.Type.PATH_SEGMENT : UriComponent.Type.PATH);
        boolean pathEndsInSlash = this.path.length() > 0 && this.path.charAt(this.path.length() - 1) == '/';
        boolean bl = segmentStartsWithSlash = segments.charAt(0) == '/';
        if (this.path.length() > 0 && !pathEndsInSlash && !segmentStartsWithSlash) {
            this.path.append('/');
        } else if (pathEndsInSlash && segmentStartsWithSlash && (segments = segments.substring(1)).length() == 0) {
            return;
        }
        this.path.append(segments);
    }

    private void encodeMatrix() {
        if (this.matrixParams == null || this.matrixParams.isEmpty()) {
            return;
        }
        for (Map.Entry e : this.matrixParams.entrySet()) {
            String name = (String)e.getKey();
            for (String value : (List)e.getValue()) {
                this.path.append(';').append(name);
                if (value.length() <= 0) continue;
                this.path.append('=').append(value);
            }
        }
        this.matrixParams = null;
    }

    private void encodeQuery() {
        if (this.queryParams == null || this.queryParams.isEmpty()) {
            return;
        }
        for (Map.Entry e : this.queryParams.entrySet()) {
            String name = (String)e.getKey();
            for (String value : (List)e.getValue()) {
                if (this.query.length() > 0) {
                    this.query.append('&');
                }
                this.query.append(name).append('=').append(value);
            }
        }
        this.queryParams = null;
    }

    private String encode(String s, UriComponent.Type type) {
        return UriComponent.contextualEncode(s, type, true);
    }

    @Override
    public URI buildFromMap(Map<String, ? extends Object> values) {
        return this._buildFromMap(true, values);
    }

    @Override
    public URI buildFromEncodedMap(Map<String, ? extends Object> values) throws IllegalArgumentException, UriBuilderException {
        return this._buildFromMap(false, values);
    }

    private URI _buildFromMap(boolean encode, Map<String, ? extends Object> values) {
        if (this.ssp != null) {
            throw new IllegalArgumentException("Schema specific part is opaque");
        }
        this.encodeMatrix();
        this.encodeQuery();
        String uri = UriTemplate.createURI(this.scheme, this.authority, this.userInfo, this.host, this.port != -1 ? String.valueOf(this.port) : null, this.path.toString(), this.query.toString(), this.fragment, values, encode);
        return this.createURI(uri);
    }

    @Override
    public URI build(Object ... values) {
        return this._build(true, values);
    }

    @Override
    public URI buildFromEncoded(Object ... values) {
        return this._build(false, values);
    }

    private URI _build(boolean encode, Object ... values) {
        if (values == null || values.length == 0) {
            return this.createURI(this.create());
        }
        if (this.ssp != null) {
            throw new IllegalArgumentException("Schema specific part is opaque");
        }
        this.encodeMatrix();
        this.encodeQuery();
        String uri = UriTemplate.createURI(this.scheme, this.authority, this.userInfo, this.host, this.port != -1 ? String.valueOf(this.port) : null, this.path.toString(), this.query.toString(), this.fragment, values, encode);
        return this.createURI(uri);
    }

    private String create() {
        this.encodeMatrix();
        this.encodeQuery();
        StringBuilder sb = new StringBuilder();
        if (this.scheme != null) {
            sb.append(this.scheme).append(':');
        }
        if (this.ssp != null) {
            sb.append(this.ssp);
        } else {
            if (this.userInfo != null || this.host != null || this.port != -1) {
                sb.append("//");
                if (this.userInfo != null && this.userInfo.length() > 0) {
                    sb.append(this.userInfo).append('@');
                }
                if (this.host != null) {
                    sb.append(this.host);
                }
                if (this.port != -1) {
                    sb.append(':').append(this.port);
                }
            } else if (this.authority != null) {
                sb.append("//").append(this.authority);
            }
            if (this.path.length() > 0) {
                if (sb.length() > 0 && this.path.charAt(0) != '/') {
                    sb.append("/");
                }
                sb.append((CharSequence)this.path);
            }
            if (this.query.length() > 0) {
                sb.append('?').append((CharSequence)this.query);
            }
        }
        if (this.fragment != null && this.fragment.length() > 0) {
            sb.append('#').append(this.fragment);
        }
        return UriComponent.encodeTemplateNames(sb.toString());
    }

    private URI createURI(String uri) {
        try {
            return new URI(uri);
        }
        catch (URISyntaxException ex) {
            throw new UriBuilderException(ex);
        }
    }

    private String replaceNull(String s) {
        return s != null ? s : "";
    }
}

