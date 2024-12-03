/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;

public class Cookie {
    public static final int DEFAULT_VERSION = 1;
    private static final RuntimeDelegate.HeaderDelegate<Cookie> delegate = RuntimeDelegate.getInstance().createHeaderDelegate(Cookie.class);
    private String name;
    private String value;
    private int version;
    private String path;
    private String domain;

    public Cookie(String name, String value, String path, String domain, int version) {
        if (name == null) {
            throw new IllegalArgumentException("name==null");
        }
        this.name = name;
        this.value = value;
        this.version = version;
        this.domain = domain;
        this.path = path;
    }

    public Cookie(String name, String value, String path, String domain) {
        this(name, value, path, domain, 1);
    }

    public Cookie(String name, String value) {
        this(name, value, null, null);
    }

    public static Cookie valueOf(String value) throws IllegalArgumentException {
        return delegate.fromString(value);
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public int getVersion() {
        return this.version;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getPath() {
        return this.path;
    }

    public String toString() {
        return delegate.toString(this);
    }

    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 97 * hash + this.version;
        hash = 97 * hash + (this.path != null ? this.path.hashCode() : 0);
        hash = 97 * hash + (this.domain != null ? this.domain.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Cookie other = (Cookie)obj;
        if (!(this.name == other.name || this.name != null && this.name.equals(other.name))) {
            return false;
        }
        if (!(this.value == other.value || this.value != null && this.value.equals(other.value))) {
            return false;
        }
        if (this.version != other.version) {
            return false;
        }
        if (!(this.path == other.path || this.path != null && this.path.equals(other.path))) {
            return false;
        }
        return this.domain == other.domain || this.domain != null && this.domain.equals(other.domain);
    }
}

