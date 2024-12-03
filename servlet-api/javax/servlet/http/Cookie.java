/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.http;

import java.io.Serializable;
import java.security.AccessController;
import java.util.Locale;
import javax.servlet.http.CookieNameValidator;
import javax.servlet.http.RFC2109Validator;
import javax.servlet.http.RFC6265Validator;

public class Cookie
implements Cloneable,
Serializable {
    private static final CookieNameValidator validation;
    private static final long serialVersionUID = 1L;
    private final String name;
    private String value;
    private int version = 0;
    private String comment;
    private String domain;
    private int maxAge = -1;
    private String path;
    private boolean secure;
    private boolean httpOnly;

    public Cookie(String name, String value) {
        validation.validate(name);
        this.name = name;
        this.value = value;
    }

    public void setComment(String purpose) {
        this.comment = purpose;
    }

    public String getComment() {
        return this.comment;
    }

    public void setDomain(String pattern) {
        this.domain = pattern.toLowerCase(Locale.ENGLISH);
    }

    public String getDomain() {
        return this.domain;
    }

    public void setMaxAge(int expiry) {
        this.maxAge = expiry;
    }

    public int getMaxAge() {
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
        this.version = v;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    static {
        String propFwdSlashIsSeparator;
        String propStrictNaming;
        boolean strictServletCompliance;
        if (System.getSecurityManager() == null) {
            strictServletCompliance = Boolean.getBoolean("org.apache.catalina.STRICT_SERVLET_COMPLIANCE");
            propStrictNaming = System.getProperty("org.apache.tomcat.util.http.ServerCookie.STRICT_NAMING");
            propFwdSlashIsSeparator = System.getProperty("org.apache.tomcat.util.http.ServerCookie.FWD_SLASH_IS_SEPARATOR");
        } else {
            strictServletCompliance = AccessController.doPrivileged(() -> Boolean.valueOf(System.getProperty("org.apache.catalina.STRICT_SERVLET_COMPLIANCE")));
            propStrictNaming = AccessController.doPrivileged(() -> System.getProperty("org.apache.tomcat.util.http.ServerCookie.STRICT_NAMING"));
            propFwdSlashIsSeparator = AccessController.doPrivileged(() -> System.getProperty("org.apache.tomcat.util.http.ServerCookie.FWD_SLASH_IS_SEPARATOR"));
        }
        boolean strictNaming = propStrictNaming == null ? strictServletCompliance : Boolean.parseBoolean(propStrictNaming);
        boolean allowSlash = propFwdSlashIsSeparator == null ? !strictServletCompliance : !Boolean.parseBoolean(propFwdSlashIsSeparator);
        validation = strictNaming ? new RFC2109Validator(allowSlash) : new RFC6265Validator();
    }
}

