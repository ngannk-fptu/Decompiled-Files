/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.util.LangUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Cookie
extends NameValuePair
implements Serializable,
Comparator {
    private String cookieComment;
    private String cookieDomain;
    private Date cookieExpiryDate;
    private String cookiePath;
    private boolean isSecure;
    private boolean hasPathAttribute = false;
    private boolean hasDomainAttribute = false;
    private int cookieVersion = 0;
    private static final Log LOG = LogFactory.getLog(Cookie.class);

    public Cookie() {
        this(null, "noname", null, null, null, false);
    }

    public Cookie(String domain, String name, String value) {
        this(domain, name, value, null, null, false);
    }

    public Cookie(String domain, String name, String value, String path, Date expires, boolean secure) {
        super(name, value);
        LOG.trace((Object)"enter Cookie(String, String, String, String, Date, boolean)");
        if (name == null) {
            throw new IllegalArgumentException("Cookie name may not be null");
        }
        if (name.trim().equals("")) {
            throw new IllegalArgumentException("Cookie name may not be blank");
        }
        this.setPath(path);
        this.setDomain(domain);
        this.setExpiryDate(expires);
        this.setSecure(secure);
    }

    public Cookie(String domain, String name, String value, String path, int maxAge, boolean secure) {
        this(domain, name, value, path, null, secure);
        if (maxAge < -1) {
            throw new IllegalArgumentException("Invalid max age:  " + Integer.toString(maxAge));
        }
        if (maxAge >= 0) {
            this.setExpiryDate(new Date(System.currentTimeMillis() + (long)maxAge * 1000L));
        }
    }

    public String getComment() {
        return this.cookieComment;
    }

    public void setComment(String comment) {
        this.cookieComment = comment;
    }

    public Date getExpiryDate() {
        return this.cookieExpiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.cookieExpiryDate = expiryDate;
    }

    public boolean isPersistent() {
        return null != this.cookieExpiryDate;
    }

    public String getDomain() {
        return this.cookieDomain;
    }

    public void setDomain(String domain) {
        if (domain != null) {
            int ndx = domain.indexOf(":");
            if (ndx != -1) {
                domain = domain.substring(0, ndx);
            }
            this.cookieDomain = domain.toLowerCase(Locale.ENGLISH);
        }
    }

    public String getPath() {
        return this.cookiePath;
    }

    public void setPath(String path) {
        this.cookiePath = path;
    }

    public boolean getSecure() {
        return this.isSecure;
    }

    public void setSecure(boolean secure) {
        this.isSecure = secure;
    }

    public int getVersion() {
        return this.cookieVersion;
    }

    public void setVersion(int version) {
        this.cookieVersion = version;
    }

    public boolean isExpired() {
        return this.cookieExpiryDate != null && this.cookieExpiryDate.getTime() <= System.currentTimeMillis();
    }

    public boolean isExpired(Date now) {
        return this.cookieExpiryDate != null && this.cookieExpiryDate.getTime() <= now.getTime();
    }

    public void setPathAttributeSpecified(boolean value) {
        this.hasPathAttribute = value;
    }

    public boolean isPathAttributeSpecified() {
        return this.hasPathAttribute;
    }

    public void setDomainAttributeSpecified(boolean value) {
        this.hasDomainAttribute = value;
    }

    public boolean isDomainAttributeSpecified() {
        return this.hasDomainAttribute;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.getName());
        hash = LangUtils.hashCode(hash, this.cookieDomain);
        hash = LangUtils.hashCode(hash, this.cookiePath);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Cookie) {
            Cookie that = (Cookie)obj;
            return LangUtils.equals(this.getName(), that.getName()) && LangUtils.equals(this.cookieDomain, that.cookieDomain) && LangUtils.equals(this.cookiePath, that.cookiePath);
        }
        return false;
    }

    public String toExternalForm() {
        CookieSpec spec = null;
        spec = this.getVersion() > 0 ? CookiePolicy.getDefaultSpec() : CookiePolicy.getCookieSpec("netscape");
        return spec.formatCookie(this);
    }

    public int compare(Object o1, Object o2) {
        LOG.trace((Object)"enter Cookie.compare(Object, Object)");
        if (!(o1 instanceof Cookie)) {
            throw new ClassCastException(o1.getClass().getName());
        }
        if (!(o2 instanceof Cookie)) {
            throw new ClassCastException(o2.getClass().getName());
        }
        Cookie c1 = (Cookie)o1;
        Cookie c2 = (Cookie)o2;
        if (c1.getPath() == null && c2.getPath() == null) {
            return 0;
        }
        if (c1.getPath() == null) {
            if (c2.getPath().equals("/")) {
                return 0;
            }
            return -1;
        }
        if (c2.getPath() == null) {
            if (c1.getPath().equals("/")) {
                return 0;
            }
            return 1;
        }
        return c1.getPath().compareTo(c2.getPath());
    }

    @Override
    public String toString() {
        return this.toExternalForm();
    }
}

