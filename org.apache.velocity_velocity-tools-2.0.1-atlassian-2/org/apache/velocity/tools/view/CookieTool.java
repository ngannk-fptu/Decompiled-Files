/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.velocity.runtime.log.Log
 */
package org.apache.velocity.tools.view;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.tools.ConversionUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;

@DefaultKey(value="cookies")
@ValidScope(value={"request"})
public class CookieTool {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected Log log;
    private List<Cookie> jar;

    public void setRequest(HttpServletRequest request) {
        if (request == null) {
            throw new NullPointerException("request should not be null");
        }
        this.request = request;
    }

    public void setResponse(HttpServletResponse response) {
        if (response == null) {
            throw new NullPointerException("response should not be null");
        }
        this.response = response;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public List<Cookie> getAll() {
        if (this.jar == null) {
            Cookie[] array = this.request.getCookies();
            if (array == null) {
                return null;
            }
            this.jar = new ArrayList<Cookie>(array.length);
            for (Cookie c : array) {
                SugarCookie sc = new SugarCookie(c);
                this.jar.add(sc);
            }
        }
        return this.jar;
    }

    public Cookie get(String name) {
        List<Cookie> all = this.getAll();
        if (all != null) {
            for (Cookie c : all) {
                if (!c.getName().equals(name)) continue;
                return c;
            }
        }
        return null;
    }

    public String add(Cookie c) {
        if (c == null) {
            return null;
        }
        this.response.addCookie(c);
        return "";
    }

    public String add(String name, String value) {
        return this.add(this.create(name, value));
    }

    public String add(String name, String value, Object maxAge) {
        return this.add(this.create(name, value, maxAge));
    }

    public Cookie create(String name, String value) {
        try {
            return new SugarCookie(name, value);
        }
        catch (IllegalArgumentException iae) {
            if (this.log != null && this.log.isDebugEnabled()) {
                this.log.debug((Object)("CookieTool: Could not create cookie with name \"" + name + "\""), (Throwable)iae);
            }
            return null;
        }
    }

    public Cookie create(String name, String value, Object maxAge) {
        SugarCookie sc = (SugarCookie)this.create(name, value);
        if (sc == null) {
            return null;
        }
        return sc.maxAge(maxAge);
    }

    public String delete(String name) {
        Cookie c = this.get(name);
        if (c == null) {
            return null;
        }
        c.setMaxAge(0);
        return this.add(c);
    }

    public String toString() {
        List<Cookie> all = this.getAll();
        if (all == null) {
            return super.toString();
        }
        StringBuilder out = new StringBuilder();
        out.append('[');
        for (int i = 0; i < all.size(); ++i) {
            if (i != 0) {
                out.append(", ");
            }
            Cookie c = all.get(i);
            out.append(c.getName());
            out.append('=');
            out.append(c.getValue());
        }
        out.append(']');
        return out.toString();
    }

    public static class SugarCookie
    extends Cookie {
        private Cookie plain;

        public SugarCookie(Cookie c) {
            this(c.getName(), c.getValue());
            this.setMaxAge(c.getMaxAge());
            this.setComment(c.getComment());
            this.setPath(c.getPath());
            this.setVersion(c.getVersion());
            this.setSecure(c.getSecure());
            if (c.getDomain() != null) {
                this.setDomain(c.getDomain());
            }
            this.plain = c;
        }

        public SugarCookie(String name, String value) {
            super(name, value);
        }

        public SugarCookie value(Object obj) {
            String value = ConversionUtils.toString(obj);
            this.setValue(value);
            if (this.plain != null) {
                this.plain.setValue(value);
            }
            return this;
        }

        public SugarCookie maxAge(Object obj) {
            Number maxAge = ConversionUtils.toNumber(obj);
            if (maxAge == null) {
                return null;
            }
            this.setMaxAge(maxAge.intValue());
            if (this.plain != null) {
                this.plain.setMaxAge(maxAge.intValue());
            }
            return this;
        }

        public SugarCookie comment(Object obj) {
            String comment = ConversionUtils.toString(obj);
            this.setComment(comment);
            if (this.plain != null) {
                this.plain.setComment(comment);
            }
            return this;
        }

        public SugarCookie domain(Object obj) {
            String domain = ConversionUtils.toString(obj);
            if (domain == null) {
                return null;
            }
            this.setDomain(domain);
            if (this.plain != null) {
                this.plain.setDomain(domain);
            }
            return this;
        }

        public SugarCookie path(Object obj) {
            String path = ConversionUtils.toString(obj);
            this.setPath(path);
            if (this.plain != null) {
                this.plain.setPath(path);
            }
            return this;
        }

        public SugarCookie version(Object obj) {
            Number version = ConversionUtils.toNumber(obj);
            if (version == null) {
                return null;
            }
            this.setVersion(version.intValue());
            if (this.plain != null) {
                this.plain.setVersion(version.intValue());
            }
            return this;
        }

        public SugarCookie secure(Object obj) {
            Boolean secure = ConversionUtils.toBoolean(obj);
            if (secure == null) {
                return null;
            }
            this.setSecure(secure);
            if (this.plain != null) {
                this.plain.setSecure(secure.booleanValue());
            }
            return this;
        }

        public Cookie getPlain() {
            return this.plain;
        }

        public String toString() {
            return this.getValue();
        }
    }
}

