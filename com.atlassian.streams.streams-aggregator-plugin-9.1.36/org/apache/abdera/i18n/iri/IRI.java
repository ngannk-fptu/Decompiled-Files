/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.iri;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.abdera.i18n.iri.HttpScheme;
import org.apache.abdera.i18n.iri.IDNA;
import org.apache.abdera.i18n.iri.IRISyntaxException;
import org.apache.abdera.i18n.iri.Scheme;
import org.apache.abdera.i18n.iri.SchemeRegistry;
import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.InvalidCharacterException;
import org.apache.abdera.i18n.text.Nameprep;
import org.apache.abdera.i18n.text.Normalizer;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.i18n.text.data.UnicodeCharacterDatabase;

public final class IRI
implements Serializable,
Cloneable {
    private static final long serialVersionUID = -4530530782760282284L;
    protected Scheme _scheme;
    private String scheme;
    private String authority;
    private String userinfo;
    private String host;
    private int port = -1;
    private String path;
    private String query;
    private String fragment;
    private String a_host;
    private String a_fragment;
    private String a_path;
    private String a_query;
    private String a_userinfo;
    private String a_authority;
    private static final Pattern IRIPATTERN = Pattern.compile("^(?:([^:/?#]+):)?(?://([^/?#]*))?([^?#]*)(?:\\?([^#]*))?(?:#(.*))?");
    private static final Pattern AUTHORITYPATTERN = Pattern.compile("^(?:(.*)?@)?((?:\\[.*\\])|(?:[^:]*))?(?::(\\d+))?");

    public IRI(URL url) {
        this(url.toString());
    }

    public IRI(URI uri) {
        this(uri.toString());
    }

    public IRI(String iri) {
        this.parse(CharUtils.stripBidi(iri));
        this.init();
    }

    public IRI(String iri, Normalizer.Form nf) throws IOException {
        this(Normalizer.normalize(CharUtils.stripBidi(iri), nf).toString());
    }

    public IRI(String scheme, String userinfo, String host, int port, String path, String query, String fragment) {
        this.scheme = scheme;
        this._scheme = SchemeRegistry.getInstance().getScheme(scheme);
        this.userinfo = userinfo;
        this.host = host;
        this.port = port;
        this.path = path;
        this.query = query;
        this.fragment = fragment;
        StringBuilder buf = new StringBuilder();
        this.buildAuthority(buf, userinfo, host, port);
        this.authority = buf.length() != 0 ? buf.toString() : null;
        this.init();
    }

    public IRI(String scheme, String authority, String path, String query, String fragment) {
        this.scheme = scheme;
        this._scheme = SchemeRegistry.getInstance().getScheme(scheme);
        this.authority = authority;
        this.path = path;
        this.query = query;
        this.fragment = fragment;
        this.parseAuthority();
        this.init();
    }

    public IRI(String scheme, String host, String path, String fragment) {
        this(scheme, null, host, -1, path, null, fragment);
    }

    IRI(Scheme _scheme, String scheme, String authority, String userinfo, String host, int port, String path, String query, String fragment) {
        this._scheme = _scheme;
        this.scheme = scheme;
        this.authority = authority;
        this.userinfo = userinfo;
        this.host = host;
        this.port = port;
        this.path = path;
        this.query = query;
        this.fragment = fragment;
        this.init();
    }

    private void init() {
        this.a_host = this.host != null && this.host.startsWith("[") ? this.host : IDNA.toASCII(this.host);
        this.a_fragment = UrlEncoding.encode((CharSequence)this.fragment, CharUtils.Profile.FRAGMENT.filter());
        this.a_path = UrlEncoding.encode((CharSequence)this.path, CharUtils.Profile.PATH.filter());
        this.a_query = UrlEncoding.encode((CharSequence)this.query, CharUtils.Profile.QUERY.filter(), CharUtils.Profile.PATH.filter());
        this.a_userinfo = UrlEncoding.encode((CharSequence)this.userinfo, CharUtils.Profile.USERINFO.filter());
        this.a_authority = this.buildASCIIAuthority();
    }

    public int hashCode() {
        int PRIME = 31;
        int result = 1;
        result = 31 * result + (this.authority == null ? 0 : this.authority.hashCode());
        result = 31 * result + (this.fragment == null ? 0 : this.fragment.hashCode());
        result = 31 * result + (this.host == null ? 0 : this.host.hashCode());
        result = 31 * result + (this.path == null ? 0 : this.path.hashCode());
        result = 31 * result + this.port;
        result = 31 * result + (this.query == null ? 0 : this.query.hashCode());
        result = 31 * result + (this.scheme == null ? 0 : this.scheme.hashCode());
        result = 31 * result + (this.userinfo == null ? 0 : this.userinfo.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        IRI other = (IRI)obj;
        if (this.authority == null ? other.authority != null : !this.authority.equals(other.authority)) {
            return false;
        }
        if (this.fragment == null ? other.fragment != null : !this.fragment.equals(other.fragment)) {
            return false;
        }
        if (this.host == null ? other.host != null : !this.host.equals(other.host)) {
            return false;
        }
        if (this.path == null ? other.path != null : !this.path.equals(other.path)) {
            return false;
        }
        if (this.port != other.port) {
            return false;
        }
        if (this.query == null ? other.query != null : !this.query.equals(other.query)) {
            return false;
        }
        if (this.scheme == null ? other.scheme != null : !this.scheme.equals(other.scheme)) {
            return false;
        }
        return !(this.userinfo == null ? other.userinfo != null : !this.userinfo.equals(other.userinfo));
    }

    public String getAuthority() {
        return this.authority != null && this.authority.length() > 0 ? this.authority : null;
    }

    public String getFragment() {
        return this.fragment;
    }

    public String getHost() {
        return this.host != null && this.host.length() > 0 ? this.host : null;
    }

    public IDNA getIDN() {
        return new IDNA(this.host);
    }

    public String getASCIIHost() {
        return this.a_host != null && this.a_host.length() > 0 ? this.a_host : null;
    }

    public String getPath() {
        return this.path;
    }

    public int getPort() {
        return this.port;
    }

    public String getQuery() {
        return this.query;
    }

    public String getScheme() {
        return this.scheme != null ? this.scheme.toLowerCase() : null;
    }

    public String getSchemeSpecificPart() {
        return this.buildSchemeSpecificPart(this.authority, this.path, this.query, this.fragment);
    }

    public String getUserInfo() {
        return this.userinfo;
    }

    void buildAuthority(StringBuilder buf, String aui, String ah, int port) {
        if (aui != null && aui.length() != 0) {
            buf.append(aui);
            buf.append('@');
        }
        if (ah != null && ah.length() != 0) {
            buf.append(ah);
        }
        if (port != -1) {
            buf.append(':');
            buf.append(port);
        }
    }

    private String buildASCIIAuthority() {
        if (this._scheme instanceof HttpScheme) {
            StringBuilder buf = new StringBuilder();
            String aui = this.getASCIIUserInfo();
            String ah = this.getASCIIHost();
            int port = this.getPort();
            this.buildAuthority(buf, aui, ah, port);
            return buf.toString();
        }
        return UrlEncoding.encode((CharSequence)this.authority, CharUtils.Profile.AUTHORITY.filter());
    }

    public String getASCIIAuthority() {
        return this.a_authority != null && this.a_authority.length() > 0 ? this.a_authority : null;
    }

    public String getASCIIFragment() {
        return this.a_fragment;
    }

    public String getASCIIPath() {
        return this.a_path;
    }

    public String getASCIIQuery() {
        return this.a_query;
    }

    public String getASCIIUserInfo() {
        return this.a_userinfo;
    }

    public String getASCIISchemeSpecificPart() {
        return this.buildSchemeSpecificPart(this.a_authority, this.a_path, this.a_query, this.a_fragment);
    }

    private String buildSchemeSpecificPart(String authority, String path, String query, String fragment) {
        StringBuilder buf = new StringBuilder();
        if (authority != null) {
            buf.append("//");
            buf.append(authority);
        }
        if (path != null && path.length() != 0) {
            buf.append(path);
        }
        if (query != null) {
            buf.append('?');
            buf.append(query);
        }
        if (fragment != null) {
            buf.append('#');
            buf.append(fragment);
        }
        return buf.toString();
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            return new IRI(this.toString());
        }
    }

    public boolean isAbsolute() {
        return this.scheme != null;
    }

    public boolean isOpaque() {
        return this.path == null;
    }

    public static IRI relativize(IRI b, IRI c) {
        if (c.isOpaque() || b.isOpaque()) {
            return c;
        }
        if (b.scheme == null && c.scheme != null || b.scheme != null && c.scheme == null || b.scheme != null && c.scheme != null && !b.scheme.equalsIgnoreCase(c.scheme)) {
            return c;
        }
        String bpath = IRI.normalize(b.getPath());
        String cpath = IRI.normalize(c.getPath());
        bpath = bpath != null ? bpath : "/";
        String string = cpath = cpath != null ? cpath : "/";
        if (!bpath.equals(cpath)) {
            if (bpath.charAt(bpath.length() - 1) != '/') {
                bpath = bpath + "/";
            }
            if (!cpath.startsWith(bpath)) {
                return c;
            }
        }
        IRI iri = new IRI(null, null, null, null, null, -1, IRI.normalize(cpath.substring(bpath.length())), c.getQuery(), c.getFragment());
        return iri;
    }

    public IRI relativize(IRI iri) {
        return IRI.relativize(this, iri);
    }

    public boolean isPathAbsolute() {
        String path = this.getPath();
        return path != null && path.length() > 0 && path.charAt(0) == '/';
    }

    public boolean isSameDocumentReference() {
        return this.scheme == null && this.authority == null && (this.path == null || this.path.length() == 0 || this.path.equals(".")) && this.query == null;
    }

    public static IRI resolve(IRI b, String c) throws IOException {
        return IRI.resolve(b, new IRI(c));
    }

    public static IRI resolve(IRI b, IRI c) {
        if (c == null) {
            return null;
        }
        if ("".equals(c.toString()) || "#".equals(c.toString()) || ".".equals(c.toString()) || "./".equals(c.toString())) {
            return b;
        }
        if (b == null) {
            return c;
        }
        if (c.isOpaque() || b.isOpaque()) {
            return c;
        }
        if (c.isSameDocumentReference()) {
            String cfragment = c.getFragment();
            String bfragment = b.getFragment();
            if (cfragment == null && bfragment == null || cfragment != null && cfragment.equals(bfragment)) {
                return (IRI)b.clone();
            }
            return new IRI(b._scheme, b.getScheme(), b.getAuthority(), b.getUserInfo(), b.getHost(), b.getPort(), IRI.normalize(b.getPath()), b.getQuery(), cfragment);
        }
        if (c.isAbsolute()) {
            return c;
        }
        Scheme _scheme = b._scheme;
        String scheme = b.scheme;
        String query = c.getQuery();
        String fragment = c.getFragment();
        String userinfo = null;
        String authority = null;
        String host = null;
        int port = -1;
        String path = null;
        if (c.getAuthority() == null) {
            authority = b.getAuthority();
            userinfo = b.getUserInfo();
            host = b.getHost();
            port = b.getPort();
            path = c.isPathAbsolute() ? IRI.normalize(c.getPath()) : IRI.resolve(b.getPath(), c.getPath());
        } else {
            authority = c.getAuthority();
            userinfo = c.getUserInfo();
            host = c.getHost();
            port = c.getPort();
            path = IRI.normalize(c.getPath());
        }
        return new IRI(_scheme, scheme, authority, userinfo, host, port, path, query, fragment);
    }

    public IRI normalize() {
        return IRI.normalize(this);
    }

    public static String normalizeString(String iri) {
        return IRI.normalize(new IRI(iri)).toString();
    }

    public static IRI normalize(IRI iri) {
        if (iri.isOpaque() || iri.getPath() == null) {
            return iri;
        }
        IRI normalized = null;
        if (iri._scheme != null) {
            normalized = iri._scheme.normalize(iri);
        }
        return normalized != null ? normalized : new IRI(iri._scheme, iri.getScheme(), iri.getAuthority(), iri.getUserInfo(), iri.getHost(), iri.getPort(), IRI.normalize(iri.getPath()), UrlEncoding.encode((CharSequence)UrlEncoding.decode(iri.getQuery()), CharUtils.Profile.IQUERY.filter()), UrlEncoding.encode((CharSequence)UrlEncoding.decode(iri.getFragment()), CharUtils.Profile.IFRAGMENT.filter()));
    }

    protected static String normalize(String path) {
        int n;
        if (path == null || path.length() == 0) {
            return "/";
        }
        String[] segments = path.split("/");
        if (segments.length < 2) {
            return path;
        }
        StringBuilder buf = new StringBuilder("/");
        for (n = 0; n < segments.length; ++n) {
            String segment = segments[n].intern();
            if (segment == ".") {
                segments[n] = null;
                continue;
            }
            if (segment != "..") continue;
            segments[n] = null;
            int i = n;
            while (--i > -1 && segments[i] == null) {
            }
            if (i <= -1) continue;
            segments[i] = null;
        }
        for (n = 0; n < segments.length; ++n) {
            if (segments[n] == null) continue;
            if (buf.length() > 1) {
                buf.append('/');
            }
            buf.append(UrlEncoding.encode((CharSequence)UrlEncoding.decode(segments[n]), CharUtils.Profile.IPATHNODELIMS_SEG.filter()));
        }
        if (path.endsWith("/") || path.endsWith("/.")) {
            buf.append('/');
        }
        return buf.toString();
    }

    private static String resolve(String bpath, String cpath) {
        if (bpath == null && cpath == null) {
            return null;
        }
        if (bpath == null && cpath != null) {
            return !cpath.startsWith("/") ? "/" + cpath : cpath;
        }
        if (bpath != null && cpath == null) {
            return bpath;
        }
        StringBuilder buf = new StringBuilder("");
        int n = bpath.lastIndexOf(47);
        if (n > -1) {
            buf.append(bpath.substring(0, n + 1));
        }
        if (cpath.length() != 0) {
            buf.append(cpath);
        }
        if (buf.charAt(0) != '/') {
            buf.insert(0, '/');
        }
        return IRI.normalize(buf.toString());
    }

    public IRI resolve(IRI iri) {
        return IRI.resolve(this, iri);
    }

    public IRI resolve(String iri) {
        return IRI.resolve(this, new IRI(iri));
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        String scheme = this.getScheme();
        if (scheme != null && scheme.length() != 0) {
            buf.append(scheme);
            buf.append(':');
        }
        buf.append(this.getSchemeSpecificPart());
        return UrlEncoding.encode((CharSequence)buf.toString(), CharUtils.Profile.SCHEMESPECIFICPART.filter());
    }

    public String toASCIIString() {
        StringBuilder buf = new StringBuilder();
        String scheme = this.getScheme();
        if (scheme != null && scheme.length() != 0) {
            buf.append(scheme);
            buf.append(':');
        }
        buf.append(this.getASCIISchemeSpecificPart());
        return buf.toString();
    }

    public String toBIDIString() {
        return CharUtils.wrapBidi(this.toString(), '\u202a');
    }

    public URI toURI() throws URISyntaxException {
        return new URI(this.toASCIIString());
    }

    public URL toURL() throws MalformedURLException, URISyntaxException {
        return this.toURI().toURL();
    }

    private void parseAuthority() {
        if (this.authority != null) {
            Matcher auth = AUTHORITYPATTERN.matcher(this.authority);
            if (auth.find()) {
                this.userinfo = auth.group(1);
                this.host = auth.group(2);
                this.port = auth.group(3) != null ? Integer.parseInt(auth.group(3)) : -1;
            }
            try {
                CharUtils.verify(this.userinfo, CharUtils.Profile.IUSERINFO);
                CharUtils.verify(this.host, CharUtils.Profile.IHOST);
            }
            catch (InvalidCharacterException e) {
                throw new IRISyntaxException(e);
            }
        }
    }

    private void parse(String iri) {
        block6: {
            try {
                SchemeRegistry reg = SchemeRegistry.getInstance();
                Matcher irim = IRIPATTERN.matcher(iri);
                if (irim.find()) {
                    this.scheme = irim.group(1);
                    this._scheme = reg.getScheme(this.scheme);
                    this.authority = irim.group(2);
                    this.path = irim.group(3);
                    this.query = irim.group(4);
                    this.fragment = irim.group(5);
                    this.parseAuthority();
                    try {
                        CharUtils.verify(this.scheme, CharUtils.Profile.SCHEME);
                        CharUtils.verify(this.path, CharUtils.Profile.IPATH);
                        CharUtils.verify(this.query, CharUtils.Profile.IQUERY);
                        CharUtils.verify(this.fragment, CharUtils.Profile.IFRAGMENT);
                        break block6;
                    }
                    catch (InvalidCharacterException e) {
                        throw new IRISyntaxException(e);
                    }
                }
                throw new IRISyntaxException("Invalid Syntax");
            }
            catch (IRISyntaxException e) {
                throw e;
            }
            catch (Exception e) {
                throw new IRISyntaxException(e);
            }
        }
    }

    public static void preinit() {
        UnicodeCharacterDatabase.getCanonicalClass(1);
        Nameprep.prep("");
    }

    public IRI trailingSlash() {
        return new IRI(this._scheme, this.scheme, this.authority, this.userinfo, this.host, this.port, this.path.endsWith("/") ? this.path : this.path + "/", this.query, this.fragment);
    }
}

