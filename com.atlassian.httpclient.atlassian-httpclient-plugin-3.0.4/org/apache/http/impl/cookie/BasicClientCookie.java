/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.cookie;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.SetCookie;
import org.apache.http.util.Args;

public class BasicClientCookie
implements SetCookie,
ClientCookie,
Cloneable,
Serializable {
    private static final long serialVersionUID = -3869795591041535538L;
    private final String name;
    private Map<String, String> attribs;
    private String value;
    private String cookieComment;
    private String cookieDomain;
    private Date cookieExpiryDate;
    private String cookiePath;
    private boolean isSecure;
    private int cookieVersion;
    private Date creationDate;

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
    public String getComment() {
        return this.cookieComment;
    }

    @Override
    public void setComment(String comment) {
        this.cookieComment = comment;
    }

    @Override
    public String getCommentURL() {
        return null;
    }

    @Override
    public Date getExpiryDate() {
        return this.cookieExpiryDate;
    }

    @Override
    public void setExpiryDate(Date expiryDate) {
        this.cookieExpiryDate = expiryDate;
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
    public int[] getPorts() {
        return null;
    }

    @Override
    public int getVersion() {
        return this.cookieVersion;
    }

    @Override
    public void setVersion(int version) {
        this.cookieVersion = version;
    }

    @Override
    public boolean isExpired(Date date) {
        Args.notNull(date, "Date");
        return this.cookieExpiryDate != null && this.cookieExpiryDate.getTime() <= date.getTime();
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
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
        buffer.append("[version: ");
        buffer.append(Integer.toString(this.cookieVersion));
        buffer.append("]");
        buffer.append("[name: ");
        buffer.append(this.name);
        buffer.append("]");
        buffer.append("[value: ");
        buffer.append(this.value);
        buffer.append("]");
        buffer.append("[domain: ");
        buffer.append(this.cookieDomain);
        buffer.append("]");
        buffer.append("[path: ");
        buffer.append(this.cookiePath);
        buffer.append("]");
        buffer.append("[expiry: ");
        buffer.append(this.cookieExpiryDate);
        buffer.append("]");
        return buffer.toString();
    }
}

