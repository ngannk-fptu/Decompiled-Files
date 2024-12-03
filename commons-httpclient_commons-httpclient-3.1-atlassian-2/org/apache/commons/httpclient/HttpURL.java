/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;

public class HttpURL
extends URI {
    public static final char[] DEFAULT_SCHEME = new char[]{'h', 't', 't', 'p'};
    public static final char[] _default_scheme = DEFAULT_SCHEME;
    public static final int DEFAULT_PORT = 80;
    public static final int _default_port = 80;
    static final long serialVersionUID = -7158031098595039459L;

    protected HttpURL() {
    }

    public HttpURL(char[] escaped, String charset) throws URIException, NullPointerException {
        this.protocolCharset = charset;
        this.parseUriReference(new String(escaped), true);
        this.checkValid();
    }

    public HttpURL(char[] escaped) throws URIException, NullPointerException {
        this.parseUriReference(new String(escaped), true);
        this.checkValid();
    }

    public HttpURL(String original, String charset) throws URIException {
        this.protocolCharset = charset;
        this.parseUriReference(original, false);
        this.checkValid();
    }

    public HttpURL(String original) throws URIException {
        this.parseUriReference(original, false);
        this.checkValid();
    }

    public HttpURL(String host, int port, String path) throws URIException {
        this(null, null, host, port, path, null, null);
    }

    public HttpURL(String host, int port, String path, String query) throws URIException {
        this(null, null, host, port, path, query, null);
    }

    public HttpURL(String user, String password, String host) throws URIException {
        this(user, password, host, -1, null, null, null);
    }

    public HttpURL(String user, String password, String host, int port) throws URIException {
        this(user, password, host, port, null, null, null);
    }

    public HttpURL(String user, String password, String host, int port, String path) throws URIException {
        this(user, password, host, port, path, null, null);
    }

    public HttpURL(String user, String password, String host, int port, String path, String query) throws URIException {
        this(user, password, host, port, path, query, null);
    }

    public HttpURL(String host, String path, String query, String fragment) throws URIException {
        this(null, null, host, -1, path, query, fragment);
    }

    public HttpURL(String userinfo, String host, String path, String query, String fragment) throws URIException {
        this(userinfo, host, -1, path, query, fragment);
    }

    public HttpURL(String userinfo, String host, int port, String path) throws URIException {
        this(userinfo, host, port, path, null, null);
    }

    public HttpURL(String userinfo, String host, int port, String path, String query) throws URIException {
        this(userinfo, host, port, path, query, null);
    }

    public HttpURL(String userinfo, String host, int port, String path, String query, String fragment) throws URIException {
        StringBuffer buff = new StringBuffer();
        if (userinfo != null || host != null || port != -1) {
            this._scheme = DEFAULT_SCHEME;
            buff.append(_default_scheme);
            buff.append("://");
            if (userinfo != null) {
                buff.append(userinfo);
                buff.append('@');
            }
            if (host != null) {
                buff.append(URIUtil.encode(host, URI.allowed_host));
                if (port != -1 || port != 80) {
                    buff.append(':');
                    buff.append(port);
                }
            }
        }
        if (path != null) {
            if (scheme != null && !path.startsWith("/")) {
                throw new URIException(1, "abs_path requested");
            }
            buff.append(URIUtil.encode(path, URI.allowed_abs_path));
        }
        if (query != null) {
            buff.append('?');
            buff.append(URIUtil.encode(query, URI.allowed_query));
        }
        if (fragment != null) {
            buff.append('#');
            buff.append(URIUtil.encode(fragment, URI.allowed_fragment));
        }
        this.parseUriReference(buff.toString(), true);
        this.checkValid();
    }

    public HttpURL(String user, String password, String host, int port, String path, String query, String fragment) throws URIException {
        this(HttpURL.toUserinfo(user, password), host, port, path, query, fragment);
    }

    protected static String toUserinfo(String user, String password) throws URIException {
        if (user == null) {
            return null;
        }
        StringBuffer usrinfo = new StringBuffer(20);
        usrinfo.append(URIUtil.encode(user, URI.allowed_within_userinfo));
        if (password == null) {
            return usrinfo.toString();
        }
        usrinfo.append(':');
        usrinfo.append(URIUtil.encode(password, URI.allowed_within_userinfo));
        return usrinfo.toString();
    }

    public HttpURL(HttpURL base, String relative) throws URIException {
        this(base, new HttpURL(relative));
    }

    public HttpURL(HttpURL base, HttpURL relative) throws URIException {
        super((URI)base, relative);
        this.checkValid();
    }

    @Override
    public char[] getRawScheme() {
        return this._scheme == null ? null : DEFAULT_SCHEME;
    }

    @Override
    public String getScheme() {
        return this._scheme == null ? null : new String(DEFAULT_SCHEME);
    }

    @Override
    public int getPort() {
        return this._port == -1 ? 80 : this._port;
    }

    public void setRawUserinfo(char[] escapedUser, char[] escapedPassword) throws URIException {
        if (escapedUser == null || escapedUser.length == 0) {
            throw new URIException(1, "user required");
        }
        if (!this.validate(escapedUser, within_userinfo) || escapedPassword != null && !this.validate(escapedPassword, within_userinfo)) {
            throw new URIException(3, "escaped userinfo not valid");
        }
        String username = new String(escapedUser);
        String password = escapedPassword == null ? null : new String(escapedPassword);
        String userinfo = username + (password == null ? "" : ":" + password);
        String hostname = new String(this.getRawHost());
        String hostport = this._port == -1 ? hostname : hostname + ":" + this._port;
        String authority = userinfo + "@" + hostport;
        this._userinfo = userinfo.toCharArray();
        this._authority = authority.toCharArray();
        this.setURI();
    }

    public void setEscapedUserinfo(String escapedUser, String escapedPassword) throws URIException, NullPointerException {
        this.setRawUserinfo(escapedUser.toCharArray(), escapedPassword == null ? null : escapedPassword.toCharArray());
    }

    public void setUserinfo(String user, String password) throws URIException, NullPointerException {
        String charset = this.getProtocolCharset();
        this.setRawUserinfo(HttpURL.encode(user, within_userinfo, charset), password == null ? null : HttpURL.encode(password, within_userinfo, charset));
    }

    public void setRawUser(char[] escapedUser) throws URIException {
        if (escapedUser == null || escapedUser.length == 0) {
            throw new URIException(1, "user required");
        }
        if (!this.validate(escapedUser, within_userinfo)) {
            throw new URIException(3, "escaped user not valid");
        }
        String username = new String(escapedUser);
        char[] rawPassword = this.getRawPassword();
        String password = rawPassword == null ? null : new String(rawPassword);
        String userinfo = username + (password == null ? "" : ":" + password);
        String hostname = new String(this.getRawHost());
        String hostport = this._port == -1 ? hostname : hostname + ":" + this._port;
        String authority = userinfo + "@" + hostport;
        this._userinfo = userinfo.toCharArray();
        this._authority = authority.toCharArray();
        this.setURI();
    }

    public void setEscapedUser(String escapedUser) throws URIException, NullPointerException {
        this.setRawUser(escapedUser.toCharArray());
    }

    public void setUser(String user) throws URIException, NullPointerException {
        this.setRawUser(HttpURL.encode(user, allowed_within_userinfo, this.getProtocolCharset()));
    }

    public char[] getRawUser() {
        if (this._userinfo == null || this._userinfo.length == 0) {
            return null;
        }
        int to = this.indexFirstOf(this._userinfo, ':');
        if (to == -1) {
            return this._userinfo;
        }
        char[] result = new char[to];
        System.arraycopy(this._userinfo, 0, result, 0, to);
        return result;
    }

    public String getEscapedUser() {
        char[] user = this.getRawUser();
        return user == null ? null : new String(user);
    }

    public String getUser() throws URIException {
        char[] user = this.getRawUser();
        return user == null ? null : HttpURL.decode(user, this.getProtocolCharset());
    }

    public void setRawPassword(char[] escapedPassword) throws URIException {
        if (escapedPassword != null && !this.validate(escapedPassword, within_userinfo)) {
            throw new URIException(3, "escaped password not valid");
        }
        if (this.getRawUser() == null || this.getRawUser().length == 0) {
            throw new URIException(1, "username required");
        }
        String username = new String(this.getRawUser());
        String password = escapedPassword == null ? null : new String(escapedPassword);
        String userinfo = username + (password == null ? "" : ":" + password);
        String hostname = new String(this.getRawHost());
        String hostport = this._port == -1 ? hostname : hostname + ":" + this._port;
        String authority = userinfo + "@" + hostport;
        this._userinfo = userinfo.toCharArray();
        this._authority = authority.toCharArray();
        this.setURI();
    }

    public void setEscapedPassword(String escapedPassword) throws URIException {
        this.setRawPassword(escapedPassword == null ? null : escapedPassword.toCharArray());
    }

    public void setPassword(String password) throws URIException {
        this.setRawPassword(password == null ? null : HttpURL.encode(password, allowed_within_userinfo, this.getProtocolCharset()));
    }

    public char[] getRawPassword() {
        int from = this.indexFirstOf(this._userinfo, ':');
        if (from == -1) {
            return null;
        }
        int len = this._userinfo.length - from - 1;
        char[] result = new char[len];
        System.arraycopy(this._userinfo, from + 1, result, 0, len);
        return result;
    }

    public String getEscapedPassword() {
        char[] password = this.getRawPassword();
        return password == null ? null : new String(password);
    }

    public String getPassword() throws URIException {
        char[] password = this.getRawPassword();
        return password == null ? null : HttpURL.decode(password, this.getProtocolCharset());
    }

    @Override
    public char[] getRawCurrentHierPath() throws URIException {
        return this._path == null || this._path.length == 0 ? rootPath : super.getRawCurrentHierPath(this._path);
    }

    @Override
    public char[] getRawAboveHierPath() throws URIException {
        char[] path = this.getRawCurrentHierPath();
        return path == null || path.length == 0 ? rootPath : this.getRawCurrentHierPath(path);
    }

    @Override
    public char[] getRawPath() {
        char[] path = super.getRawPath();
        return path == null || path.length == 0 ? rootPath : path;
    }

    public void setQuery(String queryName, String queryValue) throws URIException, NullPointerException {
        StringBuffer buff = new StringBuffer();
        String charset = this.getProtocolCharset();
        buff.append(HttpURL.encode(queryName, allowed_within_query, charset));
        buff.append('=');
        buff.append(HttpURL.encode(queryValue, allowed_within_query, charset));
        this._query = buff.toString().toCharArray();
        this.setURI();
    }

    public void setQuery(String[] queryName, String[] queryValue) throws URIException, NullPointerException {
        int length = queryName.length;
        if (length != queryValue.length) {
            throw new URIException("wrong array size of query");
        }
        StringBuffer buff = new StringBuffer();
        String charset = this.getProtocolCharset();
        for (int i = 0; i < length; ++i) {
            buff.append(HttpURL.encode(queryName[i], allowed_within_query, charset));
            buff.append('=');
            buff.append(HttpURL.encode(queryValue[i], allowed_within_query, charset));
            if (i + 1 >= length) continue;
            buff.append('&');
        }
        this._query = buff.toString().toCharArray();
        this.setURI();
    }

    protected void checkValid() throws URIException {
        if (!this.equals(this._scheme, DEFAULT_SCHEME) && this._scheme != null) {
            throw new URIException(1, "wrong class use");
        }
    }

    @Override
    protected void setURI() {
        StringBuffer buf = new StringBuffer();
        if (this._scheme != null) {
            buf.append(this._scheme);
            buf.append(':');
        }
        if (this._is_net_path) {
            buf.append("//");
            if (this._authority != null) {
                if (this._userinfo != null) {
                    if (this._host != null) {
                        buf.append(this._host);
                        if (this._port != -1) {
                            buf.append(':');
                            buf.append(this._port);
                        }
                    }
                } else {
                    buf.append(this._authority);
                }
            }
        }
        if (this._opaque != null && this._is_opaque_part) {
            buf.append(this._opaque);
        } else if (this._path != null && this._path.length != 0) {
            buf.append(this._path);
        }
        if (this._query != null) {
            buf.append('?');
            buf.append(this._query);
        }
        this._uri = buf.toString().toCharArray();
        this.hash = 0;
    }
}

