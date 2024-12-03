/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.transport.http.client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;

final class HttpCookie
implements Cloneable {
    private String name;
    private String value;
    private String comment;
    private String commentURL;
    private boolean toDiscard;
    private String domain;
    private long maxAge = -1L;
    private String path;
    private String portlist;
    private boolean secure;
    private boolean httpOnly;
    private int version = 1;
    private long whenCreated = 0L;
    private static final long MAX_AGE_UNSPECIFIED = -1L;
    private static final String[] COOKIE_DATE_FORMATS = new String[]{"EEE',' dd-MMM-yyyy HH:mm:ss 'GMT'", "EEE',' dd MMM yyyy HH:mm:ss 'GMT'", "EEE MMM dd yyyy HH:mm:ss 'GMT'Z"};
    private static final String SET_COOKIE = "set-cookie:";
    private static final String SET_COOKIE2 = "set-cookie2:";
    private static final String tspecials = ",;";
    static Map<String, CookieAttributeAssignor> assignors = null;
    private static SimpleDateFormat[] cDateFormats;

    HttpCookie(String name, String value) {
        name = name.trim();
        if (name.length() == 0 || !HttpCookie.isToken(name) || HttpCookie.isReserved(name)) {
            throw new IllegalArgumentException("Illegal cookie name");
        }
        this.name = name;
        this.value = value;
        this.toDiscard = false;
        this.secure = false;
        this.whenCreated = System.currentTimeMillis();
        this.portlist = null;
    }

    public static List<HttpCookie> parse(String header) {
        int version = HttpCookie.guessCookieVersion(header);
        if (HttpCookie.startsWithIgnoreCase(header, SET_COOKIE2)) {
            header = header.substring(SET_COOKIE2.length());
        } else if (HttpCookie.startsWithIgnoreCase(header, SET_COOKIE)) {
            header = header.substring(SET_COOKIE.length());
        }
        ArrayList<HttpCookie> cookies = new ArrayList<HttpCookie>();
        if (version == 0) {
            HttpCookie cookie = HttpCookie.parseInternal(header);
            cookie.setVersion(0);
            cookies.add(cookie);
        } else {
            List<String> cookieStrings = HttpCookie.splitMultiCookies(header);
            for (String cookieStr : cookieStrings) {
                HttpCookie cookie = HttpCookie.parseInternal(cookieStr);
                cookie.setVersion(1);
                cookies.add(cookie);
            }
        }
        return cookies;
    }

    public boolean hasExpired() {
        if (this.maxAge == 0L) {
            return true;
        }
        if (this.maxAge == -1L) {
            return false;
        }
        long deltaSecond = (System.currentTimeMillis() - this.whenCreated) / 1000L;
        return deltaSecond > this.maxAge;
    }

    public void setComment(String purpose) {
        this.comment = purpose;
    }

    public String getComment() {
        return this.comment;
    }

    public void setCommentURL(String purpose) {
        this.commentURL = purpose;
    }

    public String getCommentURL() {
        return this.commentURL;
    }

    public void setDiscard(boolean discard) {
        this.toDiscard = discard;
    }

    public boolean getDiscard() {
        return this.toDiscard;
    }

    public void setPortlist(String ports) {
        this.portlist = ports;
    }

    public String getPortlist() {
        return this.portlist;
    }

    public void setDomain(String pattern) {
        this.domain = pattern == null ? null : pattern.toLowerCase();
    }

    public String getDomain() {
        return this.domain;
    }

    public void setMaxAge(long expiry) {
        this.maxAge = expiry;
    }

    public long getMaxAge() {
        return this.maxAge;
    }

    public void setPath(String uri) {
        this.path = uri;
    }

    public String getPath() {
        return this.path;
    }

    public void setSecure(boolean flag) {
        this.secure = flag;
    }

    public boolean getSecure() {
        return this.secure;
    }

    public String getName() {
        return this.name;
    }

    public void setValue(String newValue) {
        this.value = newValue;
    }

    public String getValue() {
        return this.value;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int v) {
        if (v != 0 && v != 1) {
            throw new IllegalArgumentException("cookie version should be 0 or 1");
        }
        this.version = v;
    }

    boolean isHttpOnly() {
        return this.httpOnly;
    }

    void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public static boolean domainMatches(String domain, String host) {
        if (domain == null || host == null) {
            return false;
        }
        boolean isLocalDomain = ".local".equalsIgnoreCase(domain);
        int embeddedDotInDomain = domain.indexOf(46);
        if (embeddedDotInDomain == 0) {
            embeddedDotInDomain = domain.indexOf(46, 1);
        }
        if (!(isLocalDomain || embeddedDotInDomain != -1 && embeddedDotInDomain != domain.length() - 1)) {
            return false;
        }
        int firstDotInHost = host.indexOf(46);
        if (firstDotInHost == -1 && isLocalDomain) {
            return true;
        }
        int domainLength = domain.length();
        int lengthDiff = host.length() - domainLength;
        if (lengthDiff == 0) {
            return host.equalsIgnoreCase(domain);
        }
        if (lengthDiff > 0) {
            String H = host.substring(0, lengthDiff);
            String D = host.substring(lengthDiff);
            return H.indexOf(46) == -1 && D.equalsIgnoreCase(domain);
        }
        if (lengthDiff == -1) {
            return domain.charAt(0) == '.' && host.equalsIgnoreCase(domain.substring(1));
        }
        return false;
    }

    public String toString() {
        if (this.getVersion() > 0) {
            return this.toRFC2965HeaderString();
        }
        return this.toNetscapeHeaderString();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HttpCookie)) {
            return false;
        }
        HttpCookie other = (HttpCookie)obj;
        return HttpCookie.equalsIgnoreCase(this.getName(), other.getName()) && HttpCookie.equalsIgnoreCase(this.getDomain(), other.getDomain()) && HttpCookie.equals(this.getPath(), other.getPath());
    }

    public int hashCode() {
        int h1 = this.name.toLowerCase().hashCode();
        int h2 = this.domain != null ? this.domain.toLowerCase().hashCode() : 0;
        int h3 = this.path != null ? this.path.hashCode() : 0;
        return h1 + h2 + h3;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static boolean isToken(String value) {
        int len = value.length();
        for (int i = 0; i < len; ++i) {
            char c = value.charAt(i);
            if (c >= ' ' && c < '\u007f' && tspecials.indexOf(c) == -1) continue;
            return false;
        }
        return true;
    }

    private static boolean isReserved(String name) {
        return name.equalsIgnoreCase("Comment") || name.equalsIgnoreCase("CommentURL") || name.equalsIgnoreCase("Discard") || name.equalsIgnoreCase("Domain") || name.equalsIgnoreCase("Expires") || name.equalsIgnoreCase("Max-Age") || name.equalsIgnoreCase("Path") || name.equalsIgnoreCase("Port") || name.equalsIgnoreCase("Secure") || name.equalsIgnoreCase("Version") || name.equalsIgnoreCase("HttpOnly") || name.charAt(0) == '$';
    }

    private static HttpCookie parseInternal(String header) {
        String value;
        String name;
        int index;
        String namevaluePair;
        HttpCookie cookie = null;
        StringTokenizer tokenizer = new StringTokenizer(header, ";");
        try {
            namevaluePair = tokenizer.nextToken();
            index = namevaluePair.indexOf(61);
            if (index == -1) {
                throw new IllegalArgumentException("Invalid cookie name-value pair");
            }
            name = namevaluePair.substring(0, index).trim();
            value = namevaluePair.substring(index + 1).trim();
            cookie = new HttpCookie(name, HttpCookie.stripOffSurroundingQuote(value));
        }
        catch (NoSuchElementException ignored) {
            throw new IllegalArgumentException("Empty cookie header string");
        }
        while (tokenizer.hasMoreTokens()) {
            namevaluePair = tokenizer.nextToken();
            index = namevaluePair.indexOf(61);
            if (index != -1) {
                name = namevaluePair.substring(0, index).trim();
                value = namevaluePair.substring(index + 1).trim();
            } else {
                name = namevaluePair.trim();
                value = null;
            }
            HttpCookie.assignAttribute(cookie, name, value);
        }
        return cookie;
    }

    private static void assignAttribute(HttpCookie cookie, String attrName, String attrValue) {
        attrValue = HttpCookie.stripOffSurroundingQuote(attrValue);
        CookieAttributeAssignor assignor = assignors.get(attrName.toLowerCase());
        if (assignor != null) {
            assignor.assign(cookie, attrName, attrValue);
        }
    }

    private String toNetscapeHeaderString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append("=").append(this.getValue());
        return sb.toString();
    }

    private String toRFC2965HeaderString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append("=\"").append(this.getValue()).append('\"');
        if (this.getPath() != null) {
            sb.append(";$Path=\"").append(this.getPath()).append('\"');
        }
        if (this.getDomain() != null) {
            sb.append(";$Domain=\"").append(this.getDomain()).append('\"');
        }
        if (this.getPortlist() != null) {
            sb.append(";$Port=\"").append(this.getPortlist()).append('\"');
        }
        return sb.toString();
    }

    private long expiryDate2DeltaSeconds(String dateString) {
        for (SimpleDateFormat df : cDateFormats) {
            try {
                Date date = df.parse(dateString);
                return (date.getTime() - this.whenCreated) / 1000L;
            }
            catch (Exception exception) {
            }
        }
        return 0L;
    }

    private static int guessCookieVersion(String header) {
        int version = 0;
        if ((header = header.toLowerCase()).indexOf("expires=") != -1) {
            version = 0;
        } else if (header.indexOf("version=") != -1) {
            version = 1;
        } else if (header.indexOf("max-age") != -1) {
            version = 1;
        } else if (HttpCookie.startsWithIgnoreCase(header, SET_COOKIE2)) {
            version = 1;
        }
        return version;
    }

    private static String stripOffSurroundingQuote(String str) {
        if (str != null && str.length() > 0 && str.charAt(0) == '\"' && str.charAt(str.length() - 1) == '\"') {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    private static boolean equalsIgnoreCase(String s, String t) {
        if (s == t) {
            return true;
        }
        if (s != null && t != null) {
            return s.equalsIgnoreCase(t);
        }
        return false;
    }

    private static boolean equals(String s, String t) {
        if (s == t) {
            return true;
        }
        if (s != null && t != null) {
            return s.equals(t);
        }
        return false;
    }

    private static boolean startsWithIgnoreCase(String s, String start) {
        if (s == null || start == null) {
            return false;
        }
        return s.length() >= start.length() && start.equalsIgnoreCase(s.substring(0, start.length()));
    }

    private static List<String> splitMultiCookies(String header) {
        ArrayList<String> cookies = new ArrayList<String>();
        int quoteCount = 0;
        int q = 0;
        for (int p = 0; p < header.length(); ++p) {
            char c = header.charAt(p);
            if (c == '\"') {
                ++quoteCount;
            }
            if (c != ',' || quoteCount % 2 != 0) continue;
            cookies.add(header.substring(q, p));
            q = p + 1;
        }
        cookies.add(header.substring(q));
        return cookies;
    }

    static {
        assignors = new HashMap<String, CookieAttributeAssignor>();
        assignors.put("comment", new CookieAttributeAssignor(){

            @Override
            public void assign(HttpCookie cookie, String attrName, String attrValue) {
                if (cookie.getComment() == null) {
                    cookie.setComment(attrValue);
                }
            }
        });
        assignors.put("commenturl", new CookieAttributeAssignor(){

            @Override
            public void assign(HttpCookie cookie, String attrName, String attrValue) {
                if (cookie.getCommentURL() == null) {
                    cookie.setCommentURL(attrValue);
                }
            }
        });
        assignors.put("discard", new CookieAttributeAssignor(){

            @Override
            public void assign(HttpCookie cookie, String attrName, String attrValue) {
                cookie.setDiscard(true);
            }
        });
        assignors.put("domain", new CookieAttributeAssignor(){

            @Override
            public void assign(HttpCookie cookie, String attrName, String attrValue) {
                if (cookie.getDomain() == null) {
                    cookie.setDomain(attrValue);
                }
            }
        });
        assignors.put("max-age", new CookieAttributeAssignor(){

            @Override
            public void assign(HttpCookie cookie, String attrName, String attrValue) {
                try {
                    long maxage = Long.parseLong(attrValue);
                    if (cookie.getMaxAge() == -1L) {
                        cookie.setMaxAge(maxage);
                    }
                }
                catch (NumberFormatException ignored) {
                    throw new IllegalArgumentException("Illegal cookie max-age attribute");
                }
            }
        });
        assignors.put("path", new CookieAttributeAssignor(){

            @Override
            public void assign(HttpCookie cookie, String attrName, String attrValue) {
                if (cookie.getPath() == null) {
                    cookie.setPath(attrValue);
                }
            }
        });
        assignors.put("port", new CookieAttributeAssignor(){

            @Override
            public void assign(HttpCookie cookie, String attrName, String attrValue) {
                if (cookie.getPortlist() == null) {
                    cookie.setPortlist(attrValue == null ? "" : attrValue);
                }
            }
        });
        assignors.put("secure", new CookieAttributeAssignor(){

            @Override
            public void assign(HttpCookie cookie, String attrName, String attrValue) {
                cookie.setSecure(true);
            }
        });
        assignors.put("httponly", new CookieAttributeAssignor(){

            @Override
            public void assign(HttpCookie cookie, String attrName, String attrValue) {
                cookie.setHttpOnly(true);
            }
        });
        assignors.put("version", new CookieAttributeAssignor(){

            @Override
            public void assign(HttpCookie cookie, String attrName, String attrValue) {
                try {
                    int version = Integer.parseInt(attrValue);
                    cookie.setVersion(version);
                }
                catch (NumberFormatException ignored) {
                    throw new IllegalArgumentException("Illegal cookie version attribute");
                }
            }
        });
        assignors.put("expires", new CookieAttributeAssignor(){

            @Override
            public void assign(HttpCookie cookie, String attrName, String attrValue) {
                if (cookie.getMaxAge() == -1L) {
                    cookie.setMaxAge(cookie.expiryDate2DeltaSeconds(attrValue));
                }
            }
        });
        cDateFormats = null;
        cDateFormats = new SimpleDateFormat[COOKIE_DATE_FORMATS.length];
        for (int i = 0; i < COOKIE_DATE_FORMATS.length; ++i) {
            HttpCookie.cDateFormats[i] = new SimpleDateFormat(COOKIE_DATE_FORMATS[i], Locale.US);
            cDateFormats[i].setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    }

    static interface CookieAttributeAssignor {
        public void assign(HttpCookie var1, String var2, String var3);
    }
}

