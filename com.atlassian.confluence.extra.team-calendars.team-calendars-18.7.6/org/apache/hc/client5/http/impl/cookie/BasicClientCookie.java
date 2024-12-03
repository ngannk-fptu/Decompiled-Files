/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.cookie;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.hc.client5.http.cookie.SetCookie;
import org.apache.hc.client5.http.utils.DateUtils;
import org.apache.hc.core5.util.Args;

public final class BasicClientCookie
implements SetCookie,
Cloneable,
Serializable {
    private static final long serialVersionUID = -3869795591041535538L;
    private final String name;
    private Map<String, String> attribs;
    private String value;
    private String cookieDomain;
    private Instant cookieExpiryDate;
    private String cookiePath;
    private boolean isSecure;
    private Instant creationDate;
    private boolean httpOnly;

    public BasicClientCookie(String name, String value) {
        Args.notNull(name, "Name");
        this.name = name;
        this.attribs = new HashMap<String, String>();
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    @Deprecated
    public Date getExpiryDate() {
        return DateUtils.toDate(this.cookieExpiryDate);
    }

    @Override
    public Instant getExpiryInstant() {
        return this.cookieExpiryDate;
    }

    @Override
    @Deprecated
    public void setExpiryDate(Date expiryDate) {
        this.cookieExpiryDate = DateUtils.toInstant(expiryDate);
    }

    @Override
    public void setExpiryDate(Instant expiryInstant) {
        this.cookieExpiryDate = expiryInstant;
    }

    @Override
    public boolean isPersistent() {
        return null != this.cookieExpiryDate;
    }

    @Override
    public String getDomain() {
        return this.cookieDomain;
    }

    @Override
    public void setDomain(String domain) {
        this.cookieDomain = domain != null ? domain.toLowerCase(Locale.ROOT) : null;
    }

    @Override
    public String getPath() {
        return this.cookiePath;
    }

    @Override
    public void setPath(String path) {
        this.cookiePath = path;
    }

    @Override
    public boolean isSecure() {
        return this.isSecure;
    }

    @Override
    public void setSecure(boolean secure) {
        this.isSecure = secure;
    }

    @Override
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    @Deprecated
    public boolean isExpired(Date date) {
        Args.notNull(date, "Date");
        return this.cookieExpiryDate != null && this.cookieExpiryDate.compareTo(DateUtils.toInstant(date)) <= 0;
    }

    @Override
    public boolean isExpired(Instant instant) {
        Args.notNull(instant, "Instant");
        return this.cookieExpiryDate != null && this.cookieExpiryDate.compareTo(instant) <= 0;
    }

    @Override
    @Deprecated
    public Date getCreationDate() {
        return DateUtils.toDate(this.creationDate);
    }

    @Override
    public Instant getCreationInstant() {
        return this.creationDate;
    }

    @Override
    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    @Deprecated
    public void setCreationDate(Date creationDate) {
        this.creationDate = DateUtils.toInstant(creationDate);
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public void setAttribute(String name, String value) {
        this.attribs.put(name, value);
    }

    @Override
    public String getAttribute(String name) {
        return this.attribs.get(name);
    }

    @Override
    public boolean containsAttribute(String name) {
        return this.attribs.containsKey(name);
    }

    public boolean removeAttribute(String name) {
        return this.attribs.remove(name) != null;
    }

    public Object clone() throws CloneNotSupportedException {
        BasicClientCookie clone = (BasicClientCookie)super.clone();
        clone.attribs = new HashMap<String, String>(this.attribs);
        return clone;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[name: ");
        buffer.append(this.name);
        buffer.append("; ");
        buffer.append("value: ");
        buffer.append(this.value);
        buffer.append("; ");
        buffer.append("domain: ");
        buffer.append(this.cookieDomain);
        buffer.append("; ");
        buffer.append("path: ");
        buffer.append(this.cookiePath);
        buffer.append("; ");
        buffer.append("expiry: ");
        buffer.append(this.cookieExpiryDate);
        buffer.append("]");
        return buffer.toString();
    }
}

