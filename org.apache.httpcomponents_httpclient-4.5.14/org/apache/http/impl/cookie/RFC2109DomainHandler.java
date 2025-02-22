/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.cookie;

import java.util.Locale;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class RFC2109DomainHandler
implements CommonCookieAttributeHandler {
    @Override
    public void parse(SetCookie cookie, String value) throws MalformedCookieException {
        Args.notNull((Object)cookie, (String)"Cookie");
        if (value == null) {
            throw new MalformedCookieException("Missing value for domain attribute");
        }
        if (value.trim().isEmpty()) {
            throw new MalformedCookieException("Blank value for domain attribute");
        }
        cookie.setDomain(value);
    }

    @Override
    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        Args.notNull((Object)cookie, (String)"Cookie");
        Args.notNull((Object)origin, (String)"Cookie origin");
        String host = origin.getHost();
        String domain = cookie.getDomain();
        if (domain == null) {
            throw new CookieRestrictionViolationException("Cookie domain may not be null");
        }
        if (!domain.equals(host)) {
            int dotIndex = domain.indexOf(46);
            if (dotIndex == -1) {
                throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" does not match the host \"" + host + "\"");
            }
            if (!domain.startsWith(".")) {
                throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" violates RFC 2109: domain must start with a dot");
            }
            dotIndex = domain.indexOf(46, 1);
            if (dotIndex < 0 || dotIndex == domain.length() - 1) {
                throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" violates RFC 2109: domain must contain an embedded dot");
            }
            if (!(host = host.toLowerCase(Locale.ROOT)).endsWith(domain)) {
                throw new CookieRestrictionViolationException("Illegal domain attribute \"" + domain + "\". Domain of origin: \"" + host + "\"");
            }
            String hostWithoutDomain = host.substring(0, host.length() - domain.length());
            if (hostWithoutDomain.indexOf(46) != -1) {
                throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" violates RFC 2109: host minus domain may not contain any dots");
            }
        }
    }

    @Override
    public boolean match(Cookie cookie, CookieOrigin origin) {
        Args.notNull((Object)cookie, (String)"Cookie");
        Args.notNull((Object)origin, (String)"Cookie origin");
        String host = origin.getHost();
        String domain = cookie.getDomain();
        if (domain == null) {
            return false;
        }
        return host.equals(domain) || domain.startsWith(".") && host.endsWith(domain);
    }

    @Override
    public String getAttributeName() {
        return "domain";
    }
}

