/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.core;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.RuntimeDelegate;

public class NewCookie
extends Cookie {
    public static final int DEFAULT_MAX_AGE = -1;
    private static final RuntimeDelegate.HeaderDelegate<NewCookie> delegate = RuntimeDelegate.getInstance().createHeaderDelegate(NewCookie.class);
    private String comment = null;
    private int maxAge = -1;
    private boolean secure = false;

    public NewCookie(String name, String value) {
        super(name, value);
    }

    public NewCookie(String name, String value, String path, String domain, String comment, int maxAge, boolean secure) {
        super(name, value, path, domain);
        this.comment = comment;
        this.maxAge = maxAge;
        this.secure = secure;
    }

    public NewCookie(String name, String value, String path, String domain, int version, String comment, int maxAge, boolean secure) {
        super(name, value, path, domain, version);
        this.comment = comment;
        this.maxAge = maxAge;
        this.secure = secure;
    }

    public NewCookie(Cookie cookie) {
        super(cookie == null ? null : cookie.getName(), cookie == null ? null : cookie.getValue(), cookie == null ? null : cookie.getPath(), cookie == null ? null : cookie.getDomain(), cookie == null ? 1 : cookie.getVersion());
    }

    public NewCookie(Cookie cookie, String comment, int maxAge, boolean secure) {
        this(cookie);
        this.comment = comment;
        this.maxAge = maxAge;
        this.secure = secure;
    }

    public static NewCookie valueOf(String value) throws IllegalArgumentException {
        return delegate.fromString(value);
    }

    public String getComment() {
        return this.comment;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public Cookie toCookie() {
        return new Cookie(this.getName(), this.getValue(), this.getPath(), this.getDomain(), this.getVersion());
    }

    public String toString() {
        return delegate.toString(this);
    }

    public int hashCode() {
        int hash = super.hashCode();
        hash = 59 * hash + (this.comment != null ? this.comment.hashCode() : 0);
        hash = 59 * hash + this.maxAge;
        hash = 59 * hash + (this.secure ? 1 : 0);
        return hash;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        NewCookie other = (NewCookie)obj;
        if (!(this.getName() == other.getName() || this.getName() != null && this.getName().equals(other.getName()))) {
            return false;
        }
        if (!(this.getValue() == other.getValue() || this.getValue() != null && this.getValue().equals(other.getValue()))) {
            return false;
        }
        if (this.getVersion() != other.getVersion()) {
            return false;
        }
        if (!(this.getPath() == other.getPath() || this.getPath() != null && this.getPath().equals(other.getPath()))) {
            return false;
        }
        if (!(this.getDomain() == other.getDomain() || this.getDomain() != null && this.getDomain().equals(other.getDomain()))) {
            return false;
        }
        if (!(this.comment == other.comment || this.comment != null && this.comment.equals(other.comment))) {
            return false;
        }
        if (this.maxAge != other.maxAge) {
            return false;
        }
        return this.secure == other.secure;
    }
}

