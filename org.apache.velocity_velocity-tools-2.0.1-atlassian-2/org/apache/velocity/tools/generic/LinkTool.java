/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.runtime.log.Log
 */
package org.apache.velocity.tools.generic;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.SkipSetters;
import org.apache.velocity.tools.config.ValidScope;
import org.apache.velocity.tools.generic.SafeConfig;
import org.apache.velocity.tools.generic.ValueParser;

@DefaultKey(value="link")
@SkipSetters
@ValidScope(value={"request"})
public class LinkTool
extends SafeConfig
implements Cloneable {
    public static final String HTML_QUERY_DELIMITER = "&";
    public static final String XHTML_QUERY_DELIMITER = "&amp;";
    public static final String APPEND_PARAMS_KEY = "appendParameters";
    public static final String FORCE_RELATIVE_KEY = "forceRelative";
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String DEFAULT_SCHEME = "http";
    public static final String SECURE_SCHEME = "https";
    public static final String URI_KEY = "uri";
    public static final String SCHEME_KEY = "scheme";
    public static final String USER_KEY = "user";
    public static final String HOST_KEY = "host";
    public static final String PORT_KEY = "port";
    public static final String PATH_KEY = "requestPath";
    public static final String QUERY_KEY = "params";
    public static final String FRAGMENT_KEY = "anchor";
    public static final String CHARSET_KEY = "charset";
    public static final String XHTML_MODE_KEY = "xhtml";
    protected Log LOG;
    protected String scheme = null;
    protected String user = null;
    protected String host = null;
    protected int port = -1;
    protected String path = null;
    protected Map query = null;
    protected String fragment = null;
    protected String charset = "UTF-8";
    protected String queryDelim = "&amp;";
    protected boolean appendParams = true;
    protected boolean forceRelative = false;
    protected boolean opaque = false;
    protected final LinkTool self = this;

    protected final void debug(String msg, Object ... args) {
        this.debug(msg, null, args);
    }

    protected final void debug(String msg, Throwable t, Object ... args) {
        if (this.LOG != null && this.LOG.isDebugEnabled()) {
            this.LOG.debug((Object)("LinkTool: " + String.format(msg, args)), t);
        }
    }

    @Override
    protected void configure(ValueParser props) {
        Boolean forceRelative;
        Boolean addParams;
        Boolean xhtml;
        String chrst;
        String anchor;
        String params;
        String pth;
        Integer prt;
        String hst;
        String info;
        String schm;
        this.LOG = (Log)props.getValue("log");
        String link = props.getString(URI_KEY);
        if (link != null) {
            this.setFromURI(link);
        }
        if ((schm = props.getString(SCHEME_KEY)) != null) {
            this.setScheme(schm);
        }
        if ((info = props.getString(USER_KEY)) != null) {
            this.setUserInfo(info);
        }
        if ((hst = props.getString(HOST_KEY)) != null) {
            this.setHost(hst);
        }
        if ((prt = props.getInteger(PORT_KEY)) != null) {
            this.setPort((int)prt);
        }
        if ((pth = props.getString(PATH_KEY)) != null) {
            this.setPath(pth);
        }
        if ((params = props.getString(QUERY_KEY)) != null) {
            this.setQuery(params);
        }
        if ((anchor = props.getString(FRAGMENT_KEY)) != null) {
            this.setFragment(anchor);
        }
        if ((chrst = props.getString(CHARSET_KEY)) != null) {
            this.setCharacterEncoding(chrst);
        }
        if ((xhtml = props.getBoolean(XHTML_MODE_KEY)) != null) {
            this.setXHTML(xhtml);
        }
        if ((addParams = props.getBoolean(APPEND_PARAMS_KEY)) != null) {
            this.setAppendParams(addParams);
        }
        if ((forceRelative = props.getBoolean(FORCE_RELATIVE_KEY)) != null) {
            this.setForceRelative(forceRelative);
        }
    }

    protected LinkTool duplicate() {
        return this.duplicate(false);
    }

    protected LinkTool duplicate(boolean deep) {
        try {
            LinkTool that = (LinkTool)this.clone();
            if (deep && this.query != null) {
                that.query = new LinkedHashMap(this.query);
            }
            return that;
        }
        catch (CloneNotSupportedException e) {
            String msg = "Could not properly clone " + this.getClass();
            if (this.LOG != null) {
                this.LOG.error((Object)msg, (Throwable)e);
            }
            throw new RuntimeException(msg, e);
        }
    }

    public void setCharacterEncoding(String chrst) {
        this.charset = chrst;
    }

    public void setXHTML(boolean xhtml) {
        this.queryDelim = xhtml ? XHTML_QUERY_DELIMITER : HTML_QUERY_DELIMITER;
    }

    public void setAppendParams(boolean addParams) {
        this.appendParams = addParams;
    }

    public void setForceRelative(boolean forceRelative) {
        this.forceRelative = forceRelative;
    }

    public void setScheme(Object obj) {
        if (obj == null) {
            this.scheme = null;
        } else {
            this.scheme = String.valueOf(obj);
            if (this.scheme.length() == 0) {
                this.scheme = null;
            }
            if (this.scheme.endsWith(":")) {
                this.scheme = this.scheme.substring(0, this.scheme.length() - 1);
            }
        }
    }

    public void setUserInfo(Object obj) {
        this.user = obj == null ? null : String.valueOf(obj);
    }

    public void setHost(Object obj) {
        this.host = obj == null ? null : String.valueOf(obj);
    }

    public void setPort(Object obj) {
        if (obj == null) {
            this.port = -1;
        } else if (obj instanceof Number) {
            this.port = ((Number)obj).intValue();
        } else {
            try {
                this.port = Integer.parseInt(String.valueOf(obj));
            }
            catch (NumberFormatException nfe) {
                this.debug("Could not convert '%s' to int", nfe, obj);
                this.port = -2;
            }
        }
    }

    public void setPath(Object obj) {
        if (obj == null) {
            this.path = null;
        } else {
            this.path = String.valueOf(obj);
            if (!this.opaque && !this.path.startsWith("/")) {
                this.path = '/' + this.path;
            }
        }
    }

    public void appendPath(Object obj) {
        if (obj != null && !this.opaque) {
            this.setPath(this.combinePath(this.getPath(), String.valueOf(obj)));
        }
    }

    protected String combinePath(String start, String end) {
        boolean endStarts;
        if (end == null) {
            return start;
        }
        if (start == null) {
            return end;
        }
        boolean startEnds = start.endsWith("/");
        if (startEnds ^ (endStarts = end.startsWith("/"))) {
            return start + end;
        }
        if (startEnds & endStarts) {
            return start + end.substring(1, end.length());
        }
        return start + '/' + end;
    }

    public void setQuery(Object obj) {
        if (obj == null) {
            this.query = null;
        } else if (obj instanceof Map) {
            this.query = new LinkedHashMap((Map)obj);
        } else {
            String qs = this.normalizeQuery(String.valueOf(obj));
            this.query = this.parseQuery(qs);
        }
    }

    protected String normalizeQuery(String qs) {
        if (qs.indexOf(38) >= 0) {
            qs = qs.replaceAll("&(amp;)?", this.queryDelim);
        }
        return qs;
    }

    public String toQuery(Map parameters) {
        if (parameters == null) {
            return null;
        }
        StringBuilder query = new StringBuilder();
        Iterator iterator = parameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry e;
            Map.Entry entry = e = iterator.next();
            if (query.length() > 0) {
                query.append(this.queryDelim);
            }
            query.append(this.toQuery(entry.getKey(), entry.getValue()));
        }
        return query.toString();
    }

    public void appendQuery(Object obj) {
        if (obj != null) {
            this.setQuery(this.combineQuery(this.getQuery(), String.valueOf(obj)));
        }
    }

    public void setParam(Object key, Object value, boolean append) {
        key = String.valueOf(key);
        if (this.query == null) {
            this.query = new LinkedHashMap();
            this.putParam(key, value);
        } else if (append) {
            this.appendParam((String)key, value);
        } else {
            this.putParam(key, value);
        }
    }

    private void appendParam(String key, Object value) {
        if (this.query.containsKey(key)) {
            Object cur = this.query.get(key);
            if (cur instanceof List) {
                this.addToList((List)cur, value);
            } else {
                ArrayList vals = new ArrayList();
                vals.add(cur);
                this.addToList(vals, value);
                this.putParam(key, vals);
            }
        } else {
            this.putParam(key, value);
        }
    }

    private void putParam(Object key, Object value) {
        if (value instanceof Object[]) {
            ArrayList<Object> vals = new ArrayList<Object>();
            for (Object v : (Object[])value) {
                vals.add(v);
            }
            value = vals;
        }
        this.query.put(key, value);
    }

    private void addToList(List vals, Object value) {
        if (value instanceof List) {
            for (Object v : (List)value) {
                vals.add(v);
            }
        } else if (value instanceof Object[]) {
            for (Object v : (Object[])value) {
                vals.add(v);
            }
        } else {
            vals.add(value);
        }
    }

    public void setParams(Object obj, boolean append) {
        if (!append) {
            this.setQuery(obj);
        } else if (obj != null) {
            if (!(obj instanceof Map)) {
                obj = this.parseQuery(String.valueOf(obj));
            }
            if (obj != null) {
                if (this.query == null) {
                    this.query = new LinkedHashMap();
                }
                Iterator<Map.Entry<String, Object>> iterator = obj.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> e;
                    Map.Entry<String, Object> entry = e = iterator.next();
                    String key = String.valueOf(entry.getKey());
                    this.appendParam(key, entry.getValue());
                }
            }
        }
    }

    public Object removeParam(Object key) {
        if (this.query != null) {
            key = String.valueOf(key);
            return this.query.remove(key);
        }
        return null;
    }

    protected void handleParamsBoolean(boolean keep) {
        if (!keep) {
            this.setQuery(null);
        }
    }

    protected String combineQuery(String current, String add) {
        if (add == null || add.length() == 0) {
            return current;
        }
        if (add.startsWith("?")) {
            add = add.substring(1, add.length());
        }
        if (current == null || current.length() == 0) {
            return add;
        }
        if (current.endsWith(this.queryDelim)) {
            current = current.substring(0, current.length() - this.queryDelim.length());
        } else if (current.endsWith(HTML_QUERY_DELIMITER)) {
            current = current.substring(0, current.length() - 1);
        }
        if (add.startsWith(this.queryDelim)) {
            return current + add;
        }
        if (add.startsWith(HTML_QUERY_DELIMITER)) {
            add = add.substring(1, add.length());
        }
        return current + this.queryDelim + add;
    }

    protected String toQuery(Object key, Object value) {
        StringBuilder out = new StringBuilder();
        if (value == null) {
            out.append(this.encode(key));
            out.append('=');
        } else if (value instanceof List) {
            this.appendAsArray(out, key, ((List)value).toArray());
        } else if (value instanceof Object[]) {
            this.appendAsArray(out, key, (Object[])value);
        } else {
            out.append(this.encode(key));
            out.append('=');
            out.append(this.encode(value));
        }
        return out.toString();
    }

    protected void appendAsArray(StringBuilder out, Object key, Object[] arr) {
        String encKey = this.encode(key);
        for (int i = 0; i < arr.length; ++i) {
            out.append(encKey);
            out.append('=');
            if (arr[i] != null) {
                out.append(this.encode(arr[i]));
            }
            if (i + 1 >= arr.length) continue;
            out.append(this.queryDelim);
        }
    }

    protected Map<String, Object> parseQuery(String query) {
        return this.parseQuery(this.normalizeQuery(query), this.queryDelim);
    }

    protected Map<String, Object> parseQuery(String query, String queryDelim) {
        String[] pairs;
        if (query.startsWith("?")) {
            query = query.substring(1, query.length());
        }
        if ((pairs = query.split(queryDelim)).length == 0) {
            return null;
        }
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>(pairs.length);
        for (String pair : pairs) {
            Object value;
            String[] kv = pair.split("=");
            String key = kv[0];
            Object object = value = kv.length > 1 ? kv[1] : null;
            if (params.containsKey(kv[0])) {
                Object oldval = params.get(key);
                if (oldval instanceof List) {
                    ((List)oldval).add(value);
                    value = oldval;
                } else {
                    ArrayList<String> list = new ArrayList<String>();
                    list.add((String)oldval);
                    list.add((String)value);
                    value = list;
                }
            }
            params.put(key, value);
        }
        return params;
    }

    public void setFragment(Object obj) {
        if (obj == null) {
            this.fragment = null;
        } else {
            this.fragment = String.valueOf(obj);
            if (this.fragment.length() == 0) {
                this.fragment = null;
            }
        }
    }

    protected boolean setFromURI(Object obj) {
        if (obj == null) {
            this.setScheme(null);
            this.setUserInfo(null);
            this.setHost(null);
            this.setPort(null);
            this.setPath(null);
            this.setQuery(null);
            this.setFragment(null);
            return true;
        }
        URI uri = this.toURI(obj);
        if (uri == null) {
            return false;
        }
        if (uri.getScheme() != null) {
            this.setScheme(uri.getScheme());
        }
        if (uri.isOpaque()) {
            this.opaque = true;
            if (uri.getSchemeSpecificPart() != null) {
                this.setPath(uri.getSchemeSpecificPart());
            }
        } else {
            String pth;
            if (uri.getUserInfo() != null) {
                this.setUserInfo(uri.getUserInfo());
            }
            if (uri.getHost() != null) {
                this.setHost(uri.getHost());
            }
            if (uri.getPort() > -1) {
                this.setPort(uri.getPort());
            }
            if ((pth = uri.getPath()) != null) {
                if (pth.equals("/") || pth.length() == 0) {
                    pth = null;
                }
                this.setPath(pth);
            }
        }
        if (uri.getQuery() != null) {
            this.setQuery(uri.getQuery());
        }
        if (uri.getFragment() != null) {
            this.setFragment(uri.getFragment());
        }
        return true;
    }

    protected URI toURI(Object obj) {
        if (obj instanceof URI) {
            return (URI)obj;
        }
        try {
            return new URI(String.valueOf(obj));
        }
        catch (Exception e) {
            this.debug("Could not convert '%s' to URI", e, obj);
            return null;
        }
    }

    protected URI createURI() {
        try {
            if (this.port > -2) {
                if (this.opaque) {
                    return new URI(this.scheme, this.path, this.fragment);
                }
                if (this.forceRelative) {
                    if (this.path == null && this.query == null && this.fragment == null) {
                        return null;
                    }
                    return new URI(null, null, null, -1, this.path, this.toQuery(this.query), this.fragment);
                }
                if (this.scheme == null && this.user == null && this.host == null && this.path == null && this.query == null && this.fragment == null) {
                    return null;
                }
                return new URI(this.scheme, this.user, this.host, this.port, this.path, this.toQuery(this.query), this.fragment);
            }
        }
        catch (Exception e) {
            this.debug("Could not create URI", e, new Object[0]);
        }
        return null;
    }

    public String getCharacterEncoding() {
        return this.charset;
    }

    public boolean isXHTML() {
        return this.queryDelim.equals(XHTML_QUERY_DELIMITER);
    }

    public boolean getAppendParams() {
        return this.appendParams;
    }

    public LinkTool scheme(Object scheme) {
        LinkTool copy = this.duplicate();
        copy.setScheme(scheme);
        return copy;
    }

    public LinkTool secure() {
        return this.scheme(SECURE_SCHEME);
    }

    public LinkTool insecure() {
        return this.scheme(DEFAULT_SCHEME);
    }

    public String getScheme() {
        return this.scheme;
    }

    public boolean isSecure() {
        return SECURE_SCHEME.equalsIgnoreCase(this.getScheme());
    }

    public boolean isOpaque() {
        return this.opaque;
    }

    public LinkTool user(Object info) {
        LinkTool copy = this.duplicate();
        copy.setUserInfo(info);
        return copy;
    }

    public String getUser() {
        return this.user;
    }

    public LinkTool host(Object host) {
        LinkTool copy = this.duplicate();
        copy.setHost(host);
        if (copy.getHost() != null && !copy.isAbsolute()) {
            copy.setScheme(DEFAULT_SCHEME);
        }
        return copy;
    }

    public String getHost() {
        return this.host;
    }

    public LinkTool port(Object port) {
        LinkTool copy = this.duplicate();
        copy.setPort(port);
        return copy;
    }

    public Integer getPort() {
        if (this.port < 0) {
            return null;
        }
        return this.port;
    }

    public LinkTool path(Object pth) {
        LinkTool copy = this.duplicate();
        copy.setPath(pth);
        return copy;
    }

    public String getPath() {
        return this.path;
    }

    public LinkTool append(Object pth) {
        LinkTool copy = this.duplicate();
        copy.appendPath(pth);
        return copy;
    }

    public String getDirectory() {
        if (this.path == null || this.opaque) {
            return null;
        }
        int lastSlash = this.path.lastIndexOf(47);
        if (lastSlash < 0) {
            return "";
        }
        return this.path.substring(0, lastSlash + 1);
    }

    public String getFile() {
        if (this.path == null || this.opaque) {
            return null;
        }
        int lastSlash = this.path.lastIndexOf(47);
        if (lastSlash < 0) {
            return this.path;
        }
        return this.path.substring(lastSlash + 1, this.path.length());
    }

    public String getRoot() {
        LinkTool root = this.root();
        if (root == null) {
            return null;
        }
        return root.toString();
    }

    public LinkTool root() {
        if (this.host == null || this.opaque || this.port == -2) {
            return null;
        }
        LinkTool copy = this.absolute();
        copy.setPath(null);
        copy.setQuery(null);
        copy.setFragment(null);
        return copy;
    }

    public LinkTool directory() {
        LinkTool copy = this.root();
        if (copy == null) {
            copy = this.duplicate();
            copy.setQuery(null);
            copy.setFragment(null);
        }
        copy.setPath(this.getDirectory());
        return copy;
    }

    public boolean isRelative() {
        return this.forceRelative || this.scheme == null;
    }

    public LinkTool relative() {
        LinkTool copy = this.duplicate();
        copy.setForceRelative(true);
        return copy;
    }

    public LinkTool relative(Object obj) {
        String pth;
        LinkTool copy = this.relative();
        if (copy.setFromURI(pth = obj == null ? this.getContextPath() : this.combinePath(this.getContextPath(), String.valueOf(obj)))) {
            return copy;
        }
        return null;
    }

    public String getContextPath() {
        return this.getDirectory();
    }

    public boolean isAbsolute() {
        return this.scheme != null && !this.forceRelative;
    }

    public LinkTool absolute() {
        LinkTool copy = this.duplicate();
        copy.setForceRelative(false);
        if (copy.getScheme() == null) {
            copy.setScheme(DEFAULT_SCHEME);
        }
        return copy;
    }

    public LinkTool absolute(Object obj) {
        LinkTool copy = this.absolute();
        if (obj == null) {
            copy.setPath(this.getDirectory());
        } else {
            String pth = String.valueOf(obj);
            if (!pth.startsWith(DEFAULT_SCHEME) && !pth.startsWith("/")) {
                pth = this.combinePath(this.getDirectory(), pth);
            }
            if (!copy.setFromURI(pth)) {
                return null;
            }
        }
        return copy;
    }

    public LinkTool uri(Object uri) {
        LinkTool copy = this.duplicate();
        if (copy.setFromURI(uri)) {
            return copy;
        }
        return null;
    }

    public URI getUri() {
        if (!this.isSafeMode()) {
            return this.createURI();
        }
        return null;
    }

    public String getBaseRef() {
        LinkTool copy = this.duplicate();
        copy.setQuery(null);
        copy.setFragment(null);
        return copy.toString();
    }

    public LinkTool query(Object query) {
        LinkTool copy = this.duplicate();
        copy.setQuery(query);
        return copy;
    }

    public String getQuery() {
        return this.toQuery(this.query);
    }

    public LinkTool param(Object key, Object value) {
        LinkTool copy = this.duplicate(true);
        copy.setParam(key, value, this.appendParams);
        return copy;
    }

    public LinkTool append(Object key, Object value) {
        LinkTool copy = this.duplicate(true);
        copy.setParam(key, value, true);
        return copy;
    }

    public LinkTool set(Object key, Object value) {
        LinkTool copy = this.duplicate(true);
        copy.setParam(key, value, false);
        return copy;
    }

    public LinkTool remove(Object key) {
        LinkTool copy = this.duplicate(true);
        copy.removeParam(key);
        return copy;
    }

    public LinkTool params(Object parameters) {
        if (parameters == null) {
            return this;
        }
        if (parameters instanceof Boolean) {
            Boolean action = (boolean)((Boolean)parameters);
            LinkTool copy = this.duplicate(true);
            copy.handleParamsBoolean(action);
            return copy;
        }
        if (parameters instanceof Map && ((Map)parameters).isEmpty()) {
            return this.duplicate(false);
        }
        LinkTool copy = this.duplicate(this.appendParams);
        copy.setParams(parameters, this.appendParams);
        return copy;
    }

    public Map getParams() {
        if (this.query == null || this.query.isEmpty()) {
            return null;
        }
        return this.query;
    }

    public LinkTool anchor(Object anchor) {
        LinkTool copy = this.duplicate();
        copy.setFragment(anchor);
        return copy;
    }

    public String getAnchor() {
        return this.fragment;
    }

    public LinkTool getSelf() {
        return this.self;
    }

    public String toString() {
        URI uri = this.createURI();
        if (uri == null) {
            return null;
        }
        if (this.query != null) {
            return this.decodeQueryPercents(uri.toString());
        }
        return uri.toString();
    }

    protected String decodeQueryPercents(String url) {
        StringBuilder out = new StringBuilder(url.length());
        boolean inQuery = false;
        boolean havePercent = false;
        boolean haveTwo = false;
        for (int i = 0; i < url.length(); ++i) {
            char c = url.charAt(i);
            if (inQuery) {
                if (havePercent) {
                    if (haveTwo) {
                        out.append('%');
                        if (c != '5') {
                            out.append('2').append(c);
                        }
                        haveTwo = false;
                        havePercent = false;
                    } else if (c == '2') {
                        haveTwo = true;
                    } else {
                        out.append('%').append(c);
                        havePercent = false;
                    }
                } else if (c == '%') {
                    havePercent = true;
                } else {
                    out.append(c);
                }
                if (c != '#') continue;
                inQuery = false;
                continue;
            }
            out.append(c);
            if (c != '?') continue;
            inQuery = true;
        }
        if (havePercent) {
            out.append('%');
            if (haveTwo) {
                out.append('2');
            }
        }
        return out.toString();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof LinkTool)) {
            return false;
        }
        String that = obj.toString();
        if (that == null && this.toString() == null) {
            return true;
        }
        return that.equals(this.toString());
    }

    public int hashCode() {
        String hashme = this.toString();
        if (hashme == null) {
            return -1;
        }
        return hashme.hashCode();
    }

    public String encode(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return URLEncoder.encode(String.valueOf(obj), this.charset);
        }
        catch (UnsupportedEncodingException uee) {
            this.debug("Character encoding '%s' is unsupported", uee, this.charset);
            return null;
        }
    }

    public String decode(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return URLDecoder.decode(String.valueOf(obj), this.charset);
        }
        catch (UnsupportedEncodingException uee) {
            this.debug("Character encoding '%s' is unsupported", uee, this.charset);
            return null;
        }
    }
}

