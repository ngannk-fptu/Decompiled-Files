/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.DecoderException
 *  org.apache.commons.codec.net.URLCodec
 */
package org.apache.commons.httpclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Locale;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.EncodingUtil;

public class URI
implements Cloneable,
Comparable,
Serializable {
    static final long serialVersionUID = 604752400577948726L;
    protected int hash = 0;
    protected char[] _uri = null;
    protected String protocolCharset = null;
    protected static String defaultProtocolCharset;
    protected static String defaultDocumentCharset;
    protected static String defaultDocumentCharsetByLocale;
    protected static String defaultDocumentCharsetByPlatform;
    protected char[] _scheme = null;
    protected char[] _opaque = null;
    protected char[] _authority = null;
    protected char[] _userinfo = null;
    protected char[] _host = null;
    protected int _port = -1;
    protected char[] _path = null;
    protected char[] _query = null;
    protected char[] _fragment = null;
    protected static final char[] rootPath;
    protected static final BitSet percent;
    protected static final BitSet digit;
    protected static final BitSet alpha;
    protected static final BitSet alphanum;
    protected static final BitSet hex;
    protected static final BitSet escaped;
    protected static final BitSet mark;
    protected static final BitSet unreserved;
    protected static final BitSet reserved;
    protected static final BitSet uric;
    protected static final BitSet fragment;
    protected static final BitSet query;
    protected static final BitSet pchar;
    protected static final BitSet param;
    protected static final BitSet segment;
    protected static final BitSet path_segments;
    protected static final BitSet abs_path;
    protected static final BitSet uric_no_slash;
    protected static final BitSet opaque_part;
    protected static final BitSet path;
    protected static final BitSet port;
    protected static final BitSet IPv4address;
    protected static final BitSet IPv6address;
    protected static final BitSet IPv6reference;
    protected static final BitSet toplabel;
    protected static final BitSet domainlabel;
    protected static final BitSet hostname;
    protected static final BitSet host;
    protected static final BitSet hostport;
    protected static final BitSet userinfo;
    public static final BitSet within_userinfo;
    protected static final BitSet server;
    protected static final BitSet reg_name;
    protected static final BitSet authority;
    protected static final BitSet scheme;
    protected static final BitSet rel_segment;
    protected static final BitSet rel_path;
    protected static final BitSet net_path;
    protected static final BitSet hier_part;
    protected static final BitSet relativeURI;
    protected static final BitSet absoluteURI;
    protected static final BitSet URI_reference;
    public static final BitSet control;
    public static final BitSet space;
    public static final BitSet delims;
    public static final BitSet unwise;
    public static final BitSet disallowed_rel_path;
    public static final BitSet disallowed_opaque_part;
    public static final BitSet allowed_authority;
    public static final BitSet allowed_opaque_part;
    public static final BitSet allowed_reg_name;
    public static final BitSet allowed_userinfo;
    public static final BitSet allowed_within_userinfo;
    public static final BitSet allowed_IPv6reference;
    public static final BitSet allowed_host;
    public static final BitSet allowed_within_authority;
    public static final BitSet allowed_abs_path;
    public static final BitSet allowed_rel_path;
    public static final BitSet allowed_within_path;
    public static final BitSet allowed_query;
    public static final BitSet allowed_within_query;
    public static final BitSet allowed_fragment;
    protected boolean _is_hier_part;
    protected boolean _is_opaque_part;
    protected boolean _is_net_path;
    protected boolean _is_abs_path;
    protected boolean _is_rel_path;
    protected boolean _is_reg_name;
    protected boolean _is_server;
    protected boolean _is_hostname;
    protected boolean _is_IPv4address;
    protected boolean _is_IPv6reference;

    protected URI() {
    }

    public URI(String s, boolean escaped, String charset) throws URIException, NullPointerException {
        this.protocolCharset = charset;
        this.parseUriReference(s, escaped);
    }

    public URI(String s, boolean escaped) throws URIException, NullPointerException {
        this.parseUriReference(s, escaped);
    }

    public URI(char[] escaped, String charset) throws URIException, NullPointerException {
        this.protocolCharset = charset;
        this.parseUriReference(new String(escaped), true);
    }

    public URI(char[] escaped) throws URIException, NullPointerException {
        this.parseUriReference(new String(escaped), true);
    }

    public URI(String original, String charset) throws URIException {
        this.protocolCharset = charset;
        this.parseUriReference(original, false);
    }

    public URI(String original) throws URIException {
        this.parseUriReference(original, false);
    }

    public URI(String scheme, String schemeSpecificPart, String fragment) throws URIException {
        if (scheme == null) {
            throw new URIException(1, "scheme required");
        }
        char[] s = scheme.toLowerCase(Locale.ENGLISH).toCharArray();
        if (!this.validate(s, URI.scheme)) {
            throw new URIException(1, "incorrect scheme");
        }
        this._scheme = s;
        this._opaque = URI.encode(schemeSpecificPart, allowed_opaque_part, this.getProtocolCharset());
        this._is_opaque_part = true;
        this._fragment = fragment == null ? null : fragment.toCharArray();
        this.setURI();
    }

    public URI(String scheme, String authority, String path, String query, String fragment) throws URIException {
        StringBuffer buff = new StringBuffer();
        if (scheme != null) {
            buff.append(scheme);
            buff.append(':');
        }
        if (authority != null) {
            buff.append("//");
            buff.append(authority);
        }
        if (path != null) {
            if (!(scheme == null && authority == null || path.startsWith("/"))) {
                throw new URIException(1, "abs_path requested");
            }
            buff.append(path);
        }
        if (query != null) {
            buff.append('?');
            buff.append(query);
        }
        if (fragment != null) {
            buff.append('#');
            buff.append(fragment);
        }
        this.parseUriReference(buff.toString(), false);
    }

    public URI(String scheme, String userinfo, String host, int port) throws URIException {
        this(scheme, userinfo, host, port, null, null, null);
    }

    public URI(String scheme, String userinfo, String host, int port, String path) throws URIException {
        this(scheme, userinfo, host, port, path, null, null);
    }

    public URI(String scheme, String userinfo, String host, int port, String path, String query) throws URIException {
        this(scheme, userinfo, host, port, path, query, null);
    }

    public URI(String scheme, String userinfo, String host, int port, String path, String query, String fragment) throws URIException {
        this(scheme, host == null ? null : (userinfo != null ? userinfo + '@' : "") + host + (port != -1 ? ":" + port : ""), path, query, fragment);
    }

    public URI(String scheme, String host, String path, String fragment) throws URIException {
        this(scheme, host, path, null, fragment);
    }

    public URI(URI base, String relative) throws URIException {
        this(base, new URI(relative));
    }

    public URI(URI base, String relative, boolean escaped) throws URIException {
        this(base, new URI(relative, escaped));
    }

    public URI(URI base, URI relative) throws URIException {
        if (base._scheme == null) {
            throw new URIException(1, "base URI required");
        }
        if (base._scheme != null) {
            this._scheme = base._scheme;
            this._authority = base._authority;
            this._is_net_path = base._is_net_path;
        }
        if (base._is_opaque_part || relative._is_opaque_part) {
            this._scheme = base._scheme;
            this._is_opaque_part = base._is_opaque_part || relative._is_opaque_part;
            this._opaque = relative._opaque;
            this._fragment = relative._fragment;
            this.setURI();
            return;
        }
        boolean schemesEqual = Arrays.equals(base._scheme, relative._scheme);
        if (!(relative._scheme == null || schemesEqual && relative._authority == null)) {
            this._scheme = relative._scheme;
            this._is_net_path = relative._is_net_path;
            this._authority = relative._authority;
            if (relative._is_server) {
                this._is_server = relative._is_server;
                this._userinfo = relative._userinfo;
                this._host = relative._host;
                this._port = relative._port;
            } else if (relative._is_reg_name) {
                this._is_reg_name = relative._is_reg_name;
            }
            this._is_abs_path = relative._is_abs_path;
            this._is_rel_path = relative._is_rel_path;
            this._path = relative._path;
        } else if (base._authority != null && relative._scheme == null) {
            this._is_net_path = base._is_net_path;
            this._authority = base._authority;
            if (base._is_server) {
                this._is_server = base._is_server;
                this._userinfo = base._userinfo;
                this._host = base._host;
                this._port = base._port;
            } else if (base._is_reg_name) {
                this._is_reg_name = base._is_reg_name;
            }
        }
        if (relative._authority != null) {
            this._is_net_path = relative._is_net_path;
            this._authority = relative._authority;
            if (relative._is_server) {
                this._is_server = relative._is_server;
                this._userinfo = relative._userinfo;
                this._host = relative._host;
                this._port = relative._port;
            } else if (relative._is_reg_name) {
                this._is_reg_name = relative._is_reg_name;
            }
            this._is_abs_path = relative._is_abs_path;
            this._is_rel_path = relative._is_rel_path;
            this._path = relative._path;
        }
        if (relative._authority == null && (relative._scheme == null || schemesEqual)) {
            if ((relative._path == null || relative._path.length == 0) && relative._query == null) {
                this._path = base._path;
                this._query = base._query;
            } else {
                this._path = this.resolvePath(base._path, relative._path);
            }
        }
        if (relative._query != null) {
            this._query = relative._query;
        }
        if (relative._fragment != null) {
            this._fragment = relative._fragment;
        }
        this.setURI();
        this.parseUriReference(new String(this._uri), true);
    }

    protected static char[] encode(String original, BitSet allowed, String charset) throws URIException {
        if (original == null) {
            throw new IllegalArgumentException("Original string may not be null");
        }
        if (allowed == null) {
            throw new IllegalArgumentException("Allowed bitset may not be null");
        }
        byte[] rawdata = URLCodec.encodeUrl((BitSet)allowed, (byte[])EncodingUtil.getBytes(original, charset));
        return EncodingUtil.getAsciiString(rawdata).toCharArray();
    }

    protected static String decode(char[] component, String charset) throws URIException {
        if (component == null) {
            throw new IllegalArgumentException("Component array of chars may not be null");
        }
        return URI.decode(new String(component), charset);
    }

    protected static String decode(String component, String charset) throws URIException {
        if (component == null) {
            throw new IllegalArgumentException("Component array of chars may not be null");
        }
        byte[] rawdata = null;
        try {
            rawdata = URLCodec.decodeUrl((byte[])EncodingUtil.getAsciiBytes(component));
        }
        catch (DecoderException e) {
            throw new URIException(e.getMessage());
        }
        return EncodingUtil.getString(rawdata, charset);
    }

    protected boolean prevalidate(String component, BitSet disallowed) {
        if (component == null) {
            return false;
        }
        char[] target = component.toCharArray();
        for (int i = 0; i < target.length; ++i) {
            if (!disallowed.get(target[i])) continue;
            return false;
        }
        return true;
    }

    protected boolean validate(char[] component, BitSet generous) {
        return this.validate(component, 0, -1, generous);
    }

    protected boolean validate(char[] component, int soffset, int eoffset, BitSet generous) {
        if (eoffset == -1) {
            eoffset = component.length - 1;
        }
        for (int i = soffset; i <= eoffset; ++i) {
            if (generous.get(component[i])) continue;
            return false;
        }
        return true;
    }

    protected void parseUriReference(String original, boolean escaped) throws URIException {
        int at;
        char[] lastDelimiter;
        char[] firstDelimiter;
        if (original == null) {
            throw new URIException("URI-Reference required");
        }
        String tmp = original.trim();
        int length = tmp.length();
        if (length > 0 && this.validate(firstDelimiter = new char[]{tmp.charAt(0)}, delims) && length >= 2 && this.validate(lastDelimiter = new char[]{tmp.charAt(length - 1)}, delims)) {
            tmp = tmp.substring(1, length - 1);
            length -= 2;
        }
        int from = 0;
        boolean isStartedFromPath = false;
        int atColon = tmp.indexOf(58);
        int atSlash = tmp.indexOf(47);
        if (atColon <= 0 && !tmp.startsWith("//") || atSlash >= 0 && atSlash < atColon) {
            isStartedFromPath = true;
        }
        if ((at = this.indexFirstOf(tmp, isStartedFromPath ? "/?#" : ":/?#", from)) == -1) {
            at = 0;
        }
        if (at > 0 && at < length && tmp.charAt(at) == ':') {
            char[] target = tmp.substring(0, at).toLowerCase(Locale.ENGLISH).toCharArray();
            if (!this.validate(target, scheme)) {
                throw new URIException("incorrect scheme");
            }
            this._scheme = target;
            from = ++at;
        }
        this._is_hier_part = false;
        this._is_rel_path = false;
        this._is_abs_path = false;
        this._is_net_path = false;
        if (0 <= at && at < length && tmp.charAt(at) == '/') {
            this._is_hier_part = true;
            if (at + 2 < length && tmp.charAt(at + 1) == '/' && !isStartedFromPath) {
                int next = this.indexFirstOf(tmp, "/?#", at + 2);
                if (next == -1) {
                    next = tmp.substring(at + 2).length() == 0 ? at + 2 : tmp.length();
                }
                this.parseAuthority(tmp.substring(at + 2, next), escaped);
                from = at = next;
                this._is_net_path = true;
            }
            if (from == at) {
                this._is_abs_path = true;
            }
        }
        if (from < length) {
            int next = this.indexFirstOf(tmp, "?#", from);
            if (next == -1) {
                next = tmp.length();
            }
            if (!this._is_abs_path) {
                if (!escaped && this.prevalidate(tmp.substring(from, next), disallowed_rel_path) || escaped && this.validate(tmp.substring(from, next).toCharArray(), rel_path)) {
                    this._is_rel_path = true;
                } else if (!escaped && this.prevalidate(tmp.substring(from, next), disallowed_opaque_part) || escaped && this.validate(tmp.substring(from, next).toCharArray(), opaque_part)) {
                    this._is_opaque_part = true;
                } else {
                    this._path = null;
                }
            }
            String s = tmp.substring(from, next);
            if (escaped) {
                this.setRawPath(s.toCharArray());
            } else {
                this.setPath(s);
            }
            at = next;
        }
        String charset = this.getProtocolCharset();
        if (0 <= at && at + 1 < length && tmp.charAt(at) == '?') {
            int next = tmp.indexOf(35, at + 1);
            if (next == -1) {
                next = tmp.length();
            }
            if (escaped) {
                this._query = tmp.substring(at + 1, next).toCharArray();
                if (!this.validate(this._query, uric)) {
                    throw new URIException("Invalid query");
                }
            } else {
                this._query = URI.encode(tmp.substring(at + 1, next), allowed_query, charset);
            }
            at = next;
        }
        if (0 <= at && at + 1 <= length && tmp.charAt(at) == '#') {
            this._fragment = at + 1 == length ? "".toCharArray() : (escaped ? tmp.substring(at + 1).toCharArray() : URI.encode(tmp.substring(at + 1), allowed_fragment, charset));
        }
        this.setURI();
    }

    protected int indexFirstOf(String s, String delims) {
        return this.indexFirstOf(s, delims, -1);
    }

    protected int indexFirstOf(String s, String delims, int offset) {
        if (s == null || s.length() == 0) {
            return -1;
        }
        if (delims == null || delims.length() == 0) {
            return -1;
        }
        if (offset < 0) {
            offset = 0;
        } else if (offset > s.length()) {
            return -1;
        }
        int min = s.length();
        char[] delim = delims.toCharArray();
        for (int i = 0; i < delim.length; ++i) {
            int at = s.indexOf(delim[i], offset);
            if (at < 0 || at >= min) continue;
            min = at;
        }
        return min == s.length() ? -1 : min;
    }

    protected int indexFirstOf(char[] s, char delim) {
        return this.indexFirstOf(s, delim, 0);
    }

    protected int indexFirstOf(char[] s, char delim, int offset) {
        if (s == null || s.length == 0) {
            return -1;
        }
        if (offset < 0) {
            offset = 0;
        } else if (offset > s.length) {
            return -1;
        }
        for (int i = offset; i < s.length; ++i) {
            if (s[i] != delim) continue;
            return i;
        }
        return -1;
    }

    protected void parseAuthority(String original, boolean escaped) throws URIException {
        this._is_IPv6reference = false;
        this._is_IPv4address = false;
        this._is_hostname = false;
        this._is_server = false;
        this._is_reg_name = false;
        String charset = this.getProtocolCharset();
        boolean hasPort = true;
        int from = 0;
        int next = original.indexOf(64);
        if (next != -1) {
            this._userinfo = escaped ? original.substring(0, next).toCharArray() : URI.encode(original.substring(0, next), allowed_userinfo, charset);
            from = next + 1;
        }
        if ((next = original.indexOf(91, from)) >= from) {
            next = original.indexOf(93, from);
            if (next == -1) {
                throw new URIException(1, "IPv6reference");
            }
            this._host = escaped ? original.substring(from, next).toCharArray() : URI.encode(original.substring(from, ++next), allowed_IPv6reference, charset);
            this._is_IPv6reference = true;
        } else {
            next = original.indexOf(58, from);
            if (next == -1) {
                next = original.length();
                hasPort = false;
            }
            this._host = original.substring(from, next).toCharArray();
            if (this.validate(this._host, IPv4address)) {
                this._is_IPv4address = true;
            } else if (this.validate(this._host, hostname)) {
                this._is_hostname = true;
            } else {
                this._is_reg_name = true;
            }
        }
        if (this._is_reg_name) {
            this._is_IPv6reference = false;
            this._is_IPv4address = false;
            this._is_hostname = false;
            this._is_server = false;
            if (escaped) {
                this._authority = original.toCharArray();
                if (!this.validate(this._authority, reg_name)) {
                    throw new URIException("Invalid authority");
                }
            } else {
                this._authority = URI.encode(original, allowed_reg_name, charset);
            }
        } else {
            if (original.length() - 1 > next && hasPort && original.charAt(next) == ':') {
                from = next + 1;
                try {
                    this._port = Integer.parseInt(original.substring(from));
                }
                catch (NumberFormatException error) {
                    throw new URIException(1, "invalid port number");
                }
            }
            StringBuffer buf = new StringBuffer();
            if (this._userinfo != null) {
                buf.append(this._userinfo);
                buf.append('@');
            }
            if (this._host != null) {
                buf.append(this._host);
                if (this._port != -1) {
                    buf.append(':');
                    buf.append(this._port);
                }
            }
            this._authority = buf.toString().toCharArray();
            this._is_server = true;
        }
    }

    protected void setURI() {
        StringBuffer buf = new StringBuffer();
        if (this._scheme != null) {
            buf.append(this._scheme);
            buf.append(':');
        }
        if (this._is_net_path) {
            buf.append("//");
            if (this._authority != null) {
                buf.append(this._authority);
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

    public boolean isAbsoluteURI() {
        return this._scheme != null;
    }

    public boolean isRelativeURI() {
        return this._scheme == null;
    }

    public boolean isHierPart() {
        return this._is_hier_part;
    }

    public boolean isOpaquePart() {
        return this._is_opaque_part;
    }

    public boolean isNetPath() {
        return this._is_net_path || this._authority != null;
    }

    public boolean isAbsPath() {
        return this._is_abs_path;
    }

    public boolean isRelPath() {
        return this._is_rel_path;
    }

    public boolean hasAuthority() {
        return this._authority != null || this._is_net_path;
    }

    public boolean isRegName() {
        return this._is_reg_name;
    }

    public boolean isServer() {
        return this._is_server;
    }

    public boolean hasUserinfo() {
        return this._userinfo != null;
    }

    public boolean isHostname() {
        return this._is_hostname;
    }

    public boolean isIPv4address() {
        return this._is_IPv4address;
    }

    public boolean isIPv6reference() {
        return this._is_IPv6reference;
    }

    public boolean hasQuery() {
        return this._query != null;
    }

    public boolean hasFragment() {
        return this._fragment != null;
    }

    public static void setDefaultProtocolCharset(String charset) throws DefaultCharsetChanged {
        defaultProtocolCharset = charset;
        throw new DefaultCharsetChanged(1, "the default protocol charset changed");
    }

    public static String getDefaultProtocolCharset() {
        return defaultProtocolCharset;
    }

    public String getProtocolCharset() {
        return this.protocolCharset != null ? this.protocolCharset : defaultProtocolCharset;
    }

    public static void setDefaultDocumentCharset(String charset) throws DefaultCharsetChanged {
        defaultDocumentCharset = charset;
        throw new DefaultCharsetChanged(2, "the default document charset changed");
    }

    public static String getDefaultDocumentCharset() {
        return defaultDocumentCharset;
    }

    public static String getDefaultDocumentCharsetByLocale() {
        return defaultDocumentCharsetByLocale;
    }

    public static String getDefaultDocumentCharsetByPlatform() {
        return defaultDocumentCharsetByPlatform;
    }

    public char[] getRawScheme() {
        return this._scheme;
    }

    public String getScheme() {
        return this._scheme == null ? null : new String(this._scheme);
    }

    public void setRawAuthority(char[] escapedAuthority) throws URIException, NullPointerException {
        this.parseAuthority(new String(escapedAuthority), true);
        this.setURI();
    }

    public void setEscapedAuthority(String escapedAuthority) throws URIException {
        this.parseAuthority(escapedAuthority, true);
        this.setURI();
    }

    public char[] getRawAuthority() {
        return this._authority;
    }

    public String getEscapedAuthority() {
        return this._authority == null ? null : new String(this._authority);
    }

    public String getAuthority() throws URIException {
        return this._authority == null ? null : URI.decode(this._authority, this.getProtocolCharset());
    }

    public char[] getRawUserinfo() {
        return this._userinfo;
    }

    public String getEscapedUserinfo() {
        return this._userinfo == null ? null : new String(this._userinfo);
    }

    public String getUserinfo() throws URIException {
        return this._userinfo == null ? null : URI.decode(this._userinfo, this.getProtocolCharset());
    }

    public char[] getRawHost() {
        return this._host;
    }

    public String getHost() throws URIException {
        if (this._host != null) {
            return URI.decode(this._host, this.getProtocolCharset());
        }
        return null;
    }

    public int getPort() {
        return this._port;
    }

    public void setRawPath(char[] escapedPath) throws URIException {
        if (escapedPath == null || escapedPath.length == 0) {
            this._opaque = escapedPath;
            this._path = escapedPath;
            this.setURI();
            return;
        }
        escapedPath = this.removeFragmentIdentifier(escapedPath);
        if (this._is_net_path || this._is_abs_path) {
            if (escapedPath[0] != '/') {
                throw new URIException(1, "not absolute path");
            }
            if (!this.validate(escapedPath, abs_path)) {
                throw new URIException(3, "escaped absolute path not valid");
            }
            this._path = escapedPath;
        } else if (this._is_rel_path) {
            int at = this.indexFirstOf(escapedPath, '/');
            if (at == 0) {
                throw new URIException(1, "incorrect path");
            }
            if (at > 0 && !this.validate(escapedPath, 0, at - 1, rel_segment) && !this.validate(escapedPath, at, -1, abs_path) || at < 0 && !this.validate(escapedPath, 0, -1, rel_segment)) {
                throw new URIException(3, "escaped relative path not valid");
            }
            this._path = escapedPath;
        } else if (this._is_opaque_part) {
            if (!uric_no_slash.get(escapedPath[0]) && !this.validate(escapedPath, 1, -1, uric)) {
                throw new URIException(3, "escaped opaque part not valid");
            }
            this._opaque = escapedPath;
        } else {
            throw new URIException(1, "incorrect path");
        }
        this.setURI();
    }

    public void setEscapedPath(String escapedPath) throws URIException {
        if (escapedPath == null) {
            this._opaque = null;
            this._path = null;
            this.setURI();
            return;
        }
        this.setRawPath(escapedPath.toCharArray());
    }

    public void setPath(String path) throws URIException {
        if (path == null || path.length() == 0) {
            this._opaque = path == null ? null : path.toCharArray();
            this._path = this._opaque;
            this.setURI();
            return;
        }
        String charset = this.getProtocolCharset();
        if (this._is_net_path || this._is_abs_path) {
            this._path = URI.encode(path, allowed_abs_path, charset);
        } else if (this._is_rel_path) {
            StringBuffer buff = new StringBuffer(path.length());
            int at = path.indexOf(47);
            if (at == 0) {
                throw new URIException(1, "incorrect relative path");
            }
            if (at > 0) {
                buff.append(URI.encode(path.substring(0, at), allowed_rel_path, charset));
                buff.append(URI.encode(path.substring(at), allowed_abs_path, charset));
            } else {
                buff.append(URI.encode(path, allowed_rel_path, charset));
            }
            this._path = buff.toString().toCharArray();
        } else if (this._is_opaque_part) {
            StringBuffer buf = new StringBuffer();
            buf.insert(0, URI.encode(path.substring(0, 1), uric_no_slash, charset));
            buf.insert(1, URI.encode(path.substring(1), uric, charset));
            this._opaque = buf.toString().toCharArray();
        } else {
            throw new URIException(1, "incorrect path");
        }
        this.setURI();
    }

    protected char[] resolvePath(char[] basePath, char[] relPath) throws URIException {
        String base;
        String string = base = basePath == null ? "" : new String(basePath);
        if (relPath == null || relPath.length == 0) {
            return this.normalize(basePath);
        }
        if (relPath[0] == '/') {
            return this.normalize(relPath);
        }
        int at = base.lastIndexOf(47);
        if (at != -1) {
            basePath = base.substring(0, at + 1).toCharArray();
        }
        StringBuffer buff = new StringBuffer(base.length() + relPath.length);
        buff.append(at != -1 ? base.substring(0, at + 1) : "/");
        buff.append(relPath);
        return this.normalize(buff.toString().toCharArray());
    }

    protected char[] getRawCurrentHierPath(char[] path) throws URIException {
        if (this._is_opaque_part) {
            throw new URIException(1, "no hierarchy level");
        }
        if (path == null) {
            throw new URIException(1, "empty path");
        }
        String buff = new String(path);
        int first = buff.indexOf(47);
        int last = buff.lastIndexOf(47);
        if (last == 0) {
            return rootPath;
        }
        if (first != last && last != -1) {
            return buff.substring(0, last).toCharArray();
        }
        return path;
    }

    public char[] getRawCurrentHierPath() throws URIException {
        return this._path == null ? null : this.getRawCurrentHierPath(this._path);
    }

    public String getEscapedCurrentHierPath() throws URIException {
        char[] path = this.getRawCurrentHierPath();
        return path == null ? null : new String(path);
    }

    public String getCurrentHierPath() throws URIException {
        char[] path = this.getRawCurrentHierPath();
        return path == null ? null : URI.decode(path, this.getProtocolCharset());
    }

    public char[] getRawAboveHierPath() throws URIException {
        char[] path = this.getRawCurrentHierPath();
        return path == null ? null : this.getRawCurrentHierPath(path);
    }

    public String getEscapedAboveHierPath() throws URIException {
        char[] path = this.getRawAboveHierPath();
        return path == null ? null : new String(path);
    }

    public String getAboveHierPath() throws URIException {
        char[] path = this.getRawAboveHierPath();
        return path == null ? null : URI.decode(path, this.getProtocolCharset());
    }

    public char[] getRawPath() {
        return this._is_opaque_part ? this._opaque : this._path;
    }

    public String getEscapedPath() {
        char[] path = this.getRawPath();
        return path == null ? null : new String(path);
    }

    public String getPath() throws URIException {
        char[] path = this.getRawPath();
        return path == null ? null : URI.decode(path, this.getProtocolCharset());
    }

    public char[] getRawName() {
        if (this._path == null) {
            return null;
        }
        int at = 0;
        for (int i = this._path.length - 1; i >= 0; --i) {
            if (this._path[i] != '/') continue;
            at = i + 1;
            break;
        }
        int len = this._path.length - at;
        char[] basename = new char[len];
        System.arraycopy(this._path, at, basename, 0, len);
        return basename;
    }

    public String getEscapedName() {
        char[] basename = this.getRawName();
        return basename == null ? null : new String(basename);
    }

    public String getName() throws URIException {
        char[] basename = this.getRawName();
        return basename == null ? null : URI.decode(this.getRawName(), this.getProtocolCharset());
    }

    public char[] getRawPathQuery() {
        if (this._path == null && this._query == null) {
            return null;
        }
        StringBuffer buff = new StringBuffer();
        if (this._path != null) {
            buff.append(this._path);
        }
        if (this._query != null) {
            buff.append('?');
            buff.append(this._query);
        }
        return buff.toString().toCharArray();
    }

    public String getEscapedPathQuery() {
        char[] rawPathQuery = this.getRawPathQuery();
        return rawPathQuery == null ? null : new String(rawPathQuery);
    }

    public String getPathQuery() throws URIException {
        char[] rawPathQuery = this.getRawPathQuery();
        return rawPathQuery == null ? null : URI.decode(rawPathQuery, this.getProtocolCharset());
    }

    public void setRawQuery(char[] escapedQuery) throws URIException {
        if (escapedQuery == null || escapedQuery.length == 0) {
            this._query = escapedQuery;
            this.setURI();
            return;
        }
        if (!this.validate(escapedQuery = this.removeFragmentIdentifier(escapedQuery), query)) {
            throw new URIException(3, "escaped query not valid");
        }
        this._query = escapedQuery;
        this.setURI();
    }

    public void setEscapedQuery(String escapedQuery) throws URIException {
        if (escapedQuery == null) {
            this._query = null;
            this.setURI();
            return;
        }
        this.setRawQuery(escapedQuery.toCharArray());
    }

    public void setQuery(String query) throws URIException {
        if (query == null || query.length() == 0) {
            this._query = query == null ? null : query.toCharArray();
            this.setURI();
            return;
        }
        this.setRawQuery(URI.encode(query, allowed_query, this.getProtocolCharset()));
    }

    public char[] getRawQuery() {
        return this._query;
    }

    public String getEscapedQuery() {
        return this._query == null ? null : new String(this._query);
    }

    public String getQuery() throws URIException {
        return this._query == null ? null : URI.decode(this._query, this.getProtocolCharset());
    }

    public void setRawFragment(char[] escapedFragment) throws URIException {
        if (escapedFragment == null || escapedFragment.length == 0) {
            this._fragment = escapedFragment;
            this.hash = 0;
            return;
        }
        if (!this.validate(escapedFragment, fragment)) {
            throw new URIException(3, "escaped fragment not valid");
        }
        this._fragment = escapedFragment;
        this.hash = 0;
    }

    public void setEscapedFragment(String escapedFragment) throws URIException {
        if (escapedFragment == null) {
            this._fragment = null;
            this.hash = 0;
            return;
        }
        this.setRawFragment(escapedFragment.toCharArray());
    }

    public void setFragment(String fragment) throws URIException {
        if (fragment == null || fragment.length() == 0) {
            this._fragment = fragment == null ? null : fragment.toCharArray();
            this.hash = 0;
            return;
        }
        this._fragment = URI.encode(fragment, allowed_fragment, this.getProtocolCharset());
        this.hash = 0;
    }

    public char[] getRawFragment() {
        return this._fragment;
    }

    public String getEscapedFragment() {
        return this._fragment == null ? null : new String(this._fragment);
    }

    public String getFragment() throws URIException {
        return this._fragment == null ? null : URI.decode(this._fragment, this.getProtocolCharset());
    }

    protected char[] removeFragmentIdentifier(char[] component) {
        if (component == null) {
            return null;
        }
        int lastIndex = new String(component).indexOf(35);
        if (lastIndex != -1) {
            component = new String(component).substring(0, lastIndex).toCharArray();
        }
        return component;
    }

    protected char[] normalize(char[] path) throws URIException {
        int slashIndex;
        if (path == null) {
            return null;
        }
        String normalized = new String(path);
        if (normalized.startsWith("./")) {
            normalized = normalized.substring(1);
        } else if (normalized.startsWith("../")) {
            normalized = normalized.substring(2);
        } else if (normalized.startsWith("..")) {
            normalized = normalized.substring(2);
        }
        int index = -1;
        while ((index = normalized.indexOf("/./")) != -1) {
            normalized = normalized.substring(0, index) + normalized.substring(index + 2);
        }
        if (normalized.endsWith("/.")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        int startIndex = 0;
        while ((index = normalized.indexOf("/../", startIndex)) != -1) {
            slashIndex = normalized.lastIndexOf(47, index - 1);
            if (slashIndex >= 0) {
                normalized = normalized.substring(0, slashIndex) + normalized.substring(index + 3);
                continue;
            }
            startIndex = index + 3;
        }
        if (normalized.endsWith("/..") && (slashIndex = normalized.lastIndexOf(47, normalized.length() - 4)) >= 0) {
            normalized = normalized.substring(0, slashIndex + 1);
        }
        while ((index = normalized.indexOf("/../")) != -1 && (slashIndex = normalized.lastIndexOf(47, index - 1)) < 0) {
            normalized = normalized.substring(index + 3);
        }
        if (normalized.endsWith("/..") && (slashIndex = normalized.lastIndexOf(47, normalized.length() - 4)) < 0) {
            normalized = "/";
        }
        return normalized.toCharArray();
    }

    public void normalize() throws URIException {
        if (this.isAbsPath()) {
            this._path = this.normalize(this._path);
            this.setURI();
        }
    }

    protected boolean equals(char[] first, char[] second) {
        if (first == null && second == null) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (first.length != second.length) {
            return false;
        }
        for (int i = 0; i < first.length; ++i) {
            if (first[i] == second[i]) continue;
            return false;
        }
        return true;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof URI)) {
            return false;
        }
        URI another = (URI)obj;
        if (!this.equals(this._scheme, another._scheme)) {
            return false;
        }
        if (!this.equals(this._opaque, another._opaque)) {
            return false;
        }
        if (!this.equals(this._authority, another._authority)) {
            return false;
        }
        if (!this.equals(this._path, another._path)) {
            return false;
        }
        if (!this.equals(this._query, another._query)) {
            return false;
        }
        return this.equals(this._fragment, another._fragment);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }

    public int hashCode() {
        if (this.hash == 0) {
            int i;
            int len;
            char[] c = this._uri;
            if (c != null) {
                len = c.length;
                for (i = 0; i < len; ++i) {
                    this.hash = 31 * this.hash + c[i];
                }
            }
            if ((c = this._fragment) != null) {
                len = c.length;
                for (i = 0; i < len; ++i) {
                    this.hash = 31 * this.hash + c[i];
                }
            }
        }
        return this.hash;
    }

    public int compareTo(Object obj) throws ClassCastException {
        URI another = (URI)obj;
        if (!this.equals(this._authority, another.getRawAuthority())) {
            return -1;
        }
        return this.toString().compareTo(another.toString());
    }

    public synchronized Object clone() throws CloneNotSupportedException {
        URI instance = (URI)super.clone();
        instance._uri = this._uri;
        instance._scheme = this._scheme;
        instance._opaque = this._opaque;
        instance._authority = this._authority;
        instance._userinfo = this._userinfo;
        instance._host = this._host;
        instance._port = this._port;
        instance._path = this._path;
        instance._query = this._query;
        instance._fragment = this._fragment;
        instance.protocolCharset = this.protocolCharset;
        instance._is_hier_part = this._is_hier_part;
        instance._is_opaque_part = this._is_opaque_part;
        instance._is_net_path = this._is_net_path;
        instance._is_abs_path = this._is_abs_path;
        instance._is_rel_path = this._is_rel_path;
        instance._is_reg_name = this._is_reg_name;
        instance._is_server = this._is_server;
        instance._is_hostname = this._is_hostname;
        instance._is_IPv4address = this._is_IPv4address;
        instance._is_IPv6reference = this._is_IPv6reference;
        return instance;
    }

    public char[] getRawURI() {
        return this._uri;
    }

    public String getEscapedURI() {
        return this._uri == null ? null : new String(this._uri);
    }

    public String getURI() throws URIException {
        return this._uri == null ? null : URI.decode(this._uri, this.getProtocolCharset());
    }

    public char[] getRawURIReference() {
        if (this._fragment == null) {
            return this._uri;
        }
        if (this._uri == null) {
            return this._fragment;
        }
        String uriReference = new String(this._uri) + "#" + new String(this._fragment);
        return uriReference.toCharArray();
    }

    public String getEscapedURIReference() {
        char[] uriReference = this.getRawURIReference();
        return uriReference == null ? null : new String(uriReference);
    }

    public String getURIReference() throws URIException {
        char[] uriReference = this.getRawURIReference();
        return uriReference == null ? null : URI.decode(uriReference, this.getProtocolCharset());
    }

    public String toString() {
        return this.getEscapedURI();
    }

    static {
        int i;
        defaultProtocolCharset = "UTF-8";
        defaultDocumentCharset = null;
        defaultDocumentCharsetByLocale = null;
        defaultDocumentCharsetByPlatform = null;
        Locale locale = Locale.getDefault();
        if (locale != null) {
            defaultDocumentCharset = defaultDocumentCharsetByLocale = LocaleToCharsetMap.getCharset(locale);
        }
        try {
            defaultDocumentCharsetByPlatform = System.getProperty("file.encoding");
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        if (defaultDocumentCharset == null) {
            defaultDocumentCharset = defaultDocumentCharsetByPlatform;
        }
        rootPath = new char[]{'/'};
        percent = new BitSet(256);
        percent.set(37);
        digit = new BitSet(256);
        for (i = 48; i <= 57; ++i) {
            digit.set(i);
        }
        alpha = new BitSet(256);
        for (i = 97; i <= 122; ++i) {
            alpha.set(i);
        }
        for (i = 65; i <= 90; ++i) {
            alpha.set(i);
        }
        alphanum = new BitSet(256);
        alphanum.or(alpha);
        alphanum.or(digit);
        hex = new BitSet(256);
        hex.or(digit);
        for (i = 97; i <= 102; ++i) {
            hex.set(i);
        }
        for (i = 65; i <= 70; ++i) {
            hex.set(i);
        }
        escaped = new BitSet(256);
        escaped.or(percent);
        escaped.or(hex);
        mark = new BitSet(256);
        mark.set(45);
        mark.set(95);
        mark.set(46);
        mark.set(33);
        mark.set(126);
        mark.set(42);
        mark.set(39);
        mark.set(40);
        mark.set(41);
        unreserved = new BitSet(256);
        unreserved.or(alphanum);
        unreserved.or(mark);
        reserved = new BitSet(256);
        reserved.set(59);
        reserved.set(47);
        reserved.set(63);
        reserved.set(58);
        reserved.set(64);
        reserved.set(38);
        reserved.set(61);
        reserved.set(43);
        reserved.set(36);
        reserved.set(44);
        uric = new BitSet(256);
        uric.or(reserved);
        uric.or(unreserved);
        uric.or(escaped);
        fragment = uric;
        query = uric;
        pchar = new BitSet(256);
        pchar.or(unreserved);
        pchar.or(escaped);
        pchar.set(58);
        pchar.set(64);
        pchar.set(38);
        pchar.set(61);
        pchar.set(43);
        pchar.set(36);
        pchar.set(44);
        param = pchar;
        segment = new BitSet(256);
        segment.or(pchar);
        segment.set(59);
        segment.or(param);
        path_segments = new BitSet(256);
        path_segments.set(47);
        path_segments.or(segment);
        abs_path = new BitSet(256);
        abs_path.set(47);
        abs_path.or(path_segments);
        uric_no_slash = new BitSet(256);
        uric_no_slash.or(unreserved);
        uric_no_slash.or(escaped);
        uric_no_slash.set(59);
        uric_no_slash.set(63);
        uric_no_slash.set(59);
        uric_no_slash.set(64);
        uric_no_slash.set(38);
        uric_no_slash.set(61);
        uric_no_slash.set(43);
        uric_no_slash.set(36);
        uric_no_slash.set(44);
        opaque_part = new BitSet(256);
        opaque_part.or(uric_no_slash);
        opaque_part.or(uric);
        path = new BitSet(256);
        path.or(abs_path);
        path.or(opaque_part);
        port = digit;
        IPv4address = new BitSet(256);
        IPv4address.or(digit);
        IPv4address.set(46);
        IPv6address = new BitSet(256);
        IPv6address.or(hex);
        IPv6address.set(58);
        IPv6address.or(IPv4address);
        IPv6reference = new BitSet(256);
        IPv6reference.set(91);
        IPv6reference.or(IPv6address);
        IPv6reference.set(93);
        toplabel = new BitSet(256);
        toplabel.or(alphanum);
        toplabel.set(45);
        domainlabel = toplabel;
        hostname = new BitSet(256);
        hostname.or(toplabel);
        hostname.set(46);
        host = new BitSet(256);
        host.or(hostname);
        host.or(IPv6reference);
        hostport = new BitSet(256);
        hostport.or(host);
        hostport.set(58);
        hostport.or(port);
        userinfo = new BitSet(256);
        userinfo.or(unreserved);
        userinfo.or(escaped);
        userinfo.set(59);
        userinfo.set(58);
        userinfo.set(38);
        userinfo.set(61);
        userinfo.set(43);
        userinfo.set(36);
        userinfo.set(44);
        within_userinfo = new BitSet(256);
        within_userinfo.or(userinfo);
        within_userinfo.clear(59);
        within_userinfo.clear(58);
        within_userinfo.clear(64);
        within_userinfo.clear(63);
        within_userinfo.clear(47);
        server = new BitSet(256);
        server.or(userinfo);
        server.set(64);
        server.or(hostport);
        reg_name = new BitSet(256);
        reg_name.or(unreserved);
        reg_name.or(escaped);
        reg_name.set(36);
        reg_name.set(44);
        reg_name.set(59);
        reg_name.set(58);
        reg_name.set(64);
        reg_name.set(38);
        reg_name.set(61);
        reg_name.set(43);
        authority = new BitSet(256);
        authority.or(server);
        authority.or(reg_name);
        scheme = new BitSet(256);
        scheme.or(alpha);
        scheme.or(digit);
        scheme.set(43);
        scheme.set(45);
        scheme.set(46);
        rel_segment = new BitSet(256);
        rel_segment.or(unreserved);
        rel_segment.or(escaped);
        rel_segment.set(59);
        rel_segment.set(64);
        rel_segment.set(38);
        rel_segment.set(61);
        rel_segment.set(43);
        rel_segment.set(36);
        rel_segment.set(44);
        rel_path = new BitSet(256);
        rel_path.or(rel_segment);
        rel_path.or(abs_path);
        net_path = new BitSet(256);
        net_path.set(47);
        net_path.or(authority);
        net_path.or(abs_path);
        hier_part = new BitSet(256);
        hier_part.or(net_path);
        hier_part.or(abs_path);
        hier_part.or(query);
        relativeURI = new BitSet(256);
        relativeURI.or(net_path);
        relativeURI.or(abs_path);
        relativeURI.or(rel_path);
        relativeURI.or(query);
        absoluteURI = new BitSet(256);
        absoluteURI.or(scheme);
        absoluteURI.set(58);
        absoluteURI.or(hier_part);
        absoluteURI.or(opaque_part);
        URI_reference = new BitSet(256);
        URI_reference.or(absoluteURI);
        URI_reference.or(relativeURI);
        URI_reference.set(35);
        URI_reference.or(fragment);
        control = new BitSet(256);
        for (i = 0; i <= 31; ++i) {
            control.set(i);
        }
        control.set(127);
        space = new BitSet(256);
        space.set(32);
        delims = new BitSet(256);
        delims.set(60);
        delims.set(62);
        delims.set(35);
        delims.set(37);
        delims.set(34);
        unwise = new BitSet(256);
        unwise.set(123);
        unwise.set(125);
        unwise.set(124);
        unwise.set(92);
        unwise.set(94);
        unwise.set(91);
        unwise.set(93);
        unwise.set(96);
        disallowed_rel_path = new BitSet(256);
        disallowed_rel_path.or(uric);
        disallowed_rel_path.andNot(rel_path);
        disallowed_opaque_part = new BitSet(256);
        disallowed_opaque_part.or(uric);
        disallowed_opaque_part.andNot(opaque_part);
        allowed_authority = new BitSet(256);
        allowed_authority.or(authority);
        allowed_authority.clear(37);
        allowed_opaque_part = new BitSet(256);
        allowed_opaque_part.or(opaque_part);
        allowed_opaque_part.clear(37);
        allowed_reg_name = new BitSet(256);
        allowed_reg_name.or(reg_name);
        allowed_reg_name.clear(37);
        allowed_userinfo = new BitSet(256);
        allowed_userinfo.or(userinfo);
        allowed_userinfo.clear(37);
        allowed_within_userinfo = new BitSet(256);
        allowed_within_userinfo.or(within_userinfo);
        allowed_within_userinfo.clear(37);
        allowed_IPv6reference = new BitSet(256);
        allowed_IPv6reference.or(IPv6reference);
        allowed_IPv6reference.clear(91);
        allowed_IPv6reference.clear(93);
        allowed_host = new BitSet(256);
        allowed_host.or(hostname);
        allowed_host.or(allowed_IPv6reference);
        allowed_within_authority = new BitSet(256);
        allowed_within_authority.or(server);
        allowed_within_authority.or(reg_name);
        allowed_within_authority.clear(59);
        allowed_within_authority.clear(58);
        allowed_within_authority.clear(64);
        allowed_within_authority.clear(63);
        allowed_within_authority.clear(47);
        allowed_abs_path = new BitSet(256);
        allowed_abs_path.or(abs_path);
        allowed_abs_path.andNot(percent);
        allowed_abs_path.clear(43);
        allowed_rel_path = new BitSet(256);
        allowed_rel_path.or(rel_path);
        allowed_rel_path.clear(37);
        allowed_rel_path.clear(43);
        allowed_within_path = new BitSet(256);
        allowed_within_path.or(abs_path);
        allowed_within_path.clear(47);
        allowed_within_path.clear(59);
        allowed_within_path.clear(61);
        allowed_within_path.clear(63);
        allowed_query = new BitSet(256);
        allowed_query.or(uric);
        allowed_query.clear(37);
        allowed_within_query = new BitSet(256);
        allowed_within_query.or(allowed_query);
        allowed_within_query.andNot(reserved);
        allowed_fragment = new BitSet(256);
        allowed_fragment.or(uric);
        allowed_fragment.clear(37);
    }

    public static class LocaleToCharsetMap {
        private static final Hashtable LOCALE_TO_CHARSET_MAP = new Hashtable();

        public static String getCharset(Locale locale) {
            String charset = (String)LOCALE_TO_CHARSET_MAP.get(locale.toString());
            if (charset != null) {
                return charset;
            }
            charset = (String)LOCALE_TO_CHARSET_MAP.get(locale.getLanguage());
            return charset;
        }

        static {
            LOCALE_TO_CHARSET_MAP.put("ar", "ISO-8859-6");
            LOCALE_TO_CHARSET_MAP.put("be", "ISO-8859-5");
            LOCALE_TO_CHARSET_MAP.put("bg", "ISO-8859-5");
            LOCALE_TO_CHARSET_MAP.put("ca", "ISO-8859-1");
            LOCALE_TO_CHARSET_MAP.put("cs", "ISO-8859-2");
            LOCALE_TO_CHARSET_MAP.put("da", "ISO-8859-1");
            LOCALE_TO_CHARSET_MAP.put("de", "ISO-8859-1");
            LOCALE_TO_CHARSET_MAP.put("el", "ISO-8859-7");
            LOCALE_TO_CHARSET_MAP.put("en", "ISO-8859-1");
            LOCALE_TO_CHARSET_MAP.put("es", "ISO-8859-1");
            LOCALE_TO_CHARSET_MAP.put("et", "ISO-8859-1");
            LOCALE_TO_CHARSET_MAP.put("fi", "ISO-8859-1");
            LOCALE_TO_CHARSET_MAP.put("fr", "ISO-8859-1");
            LOCALE_TO_CHARSET_MAP.put("hr", "ISO-8859-2");
            LOCALE_TO_CHARSET_MAP.put("hu", "ISO-8859-2");
            LOCALE_TO_CHARSET_MAP.put("is", "ISO-8859-1");
            LOCALE_TO_CHARSET_MAP.put("it", "ISO-8859-1");
            LOCALE_TO_CHARSET_MAP.put("iw", "ISO-8859-8");
            LOCALE_TO_CHARSET_MAP.put("ja", "Shift_JIS");
            LOCALE_TO_CHARSET_MAP.put("ko", "EUC-KR");
            LOCALE_TO_CHARSET_MAP.put("lt", "ISO-8859-2");
            LOCALE_TO_CHARSET_MAP.put("lv", "ISO-8859-2");
            LOCALE_TO_CHARSET_MAP.put("mk", "ISO-8859-5");
            LOCALE_TO_CHARSET_MAP.put("nl", "ISO-8859-1");
            LOCALE_TO_CHARSET_MAP.put("no", "ISO-8859-1");
            LOCALE_TO_CHARSET_MAP.put("pl", "ISO-8859-2");
            LOCALE_TO_CHARSET_MAP.put("pt", "ISO-8859-1");
            LOCALE_TO_CHARSET_MAP.put("ro", "ISO-8859-2");
            LOCALE_TO_CHARSET_MAP.put("ru", "ISO-8859-5");
            LOCALE_TO_CHARSET_MAP.put("sh", "ISO-8859-5");
            LOCALE_TO_CHARSET_MAP.put("sk", "ISO-8859-2");
            LOCALE_TO_CHARSET_MAP.put("sl", "ISO-8859-2");
            LOCALE_TO_CHARSET_MAP.put("sq", "ISO-8859-2");
            LOCALE_TO_CHARSET_MAP.put("sr", "ISO-8859-5");
            LOCALE_TO_CHARSET_MAP.put("sv", "ISO-8859-1");
            LOCALE_TO_CHARSET_MAP.put("tr", "ISO-8859-9");
            LOCALE_TO_CHARSET_MAP.put("uk", "ISO-8859-5");
            LOCALE_TO_CHARSET_MAP.put("zh", "GB2312");
            LOCALE_TO_CHARSET_MAP.put("zh_TW", "Big5");
        }
    }

    public static class DefaultCharsetChanged
    extends RuntimeException {
        public static final int UNKNOWN = 0;
        public static final int PROTOCOL_CHARSET = 1;
        public static final int DOCUMENT_CHARSET = 2;
        private int reasonCode;
        private String reason;

        public DefaultCharsetChanged(int reasonCode, String reason) {
            super(reason);
            this.reason = reason;
            this.reasonCode = reasonCode;
        }

        public int getReasonCode() {
            return this.reasonCode;
        }

        public String getReason() {
            return this.reason;
        }
    }
}

