/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.net.InetAddressUtils
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.TextUtils
 */
package org.apache.hc.client5.http.impl.cookie;

import java.util.Locale;
import org.apache.hc.client5.http.cookie.CommonCookieAttributeHandler;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieOrigin;
import org.apache.hc.client5.http.cookie.CookieRestrictionViolationException;
import org.apache.hc.client5.http.cookie.MalformedCookieException;
import org.apache.hc.client5.http.cookie.SetCookie;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.net.InetAddressUtils;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TextUtils;

@Contract(threading=ThreadingBehavior.STATELESS)
public class BasicDomainHandler
implements CommonCookieAttributeHandler {
    public static final BasicDomainHandler INSTANCE = new BasicDomainHandler();

    @Override
    public void parse(SetCookie cookie, String value) throws MalformedCookieException {
        Args.notNull((Object)cookie, (String)"Cookie");
        if (TextUtils.isBlank((CharSequence)value)) {
            throw new MalformedCookieException("Blank or null value for domain attribute");
        }
        if (value.endsWith(".")) {
            return;
        }
        String domain = value;
        if (domain.startsWith(".")) {
            domain = domain.substring(1);
        }
        domain = domain.toLowerCase(Locale.ROOT);
        cookie.setDomain(domain);
    }

    @Override
    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        Args.notNull((Object)cookie, (String)"Cookie");
        Args.notNull((Object)origin, (String)"Cookie origin");
        String host = origin.getHost();
        String domain = cookie.getDomain();
        if (domain == null) {
            throw new CookieRestrictionViolationException("Cookie 'domain' may not be null");
        }
        if (!host.equals(domain) && !BasicDomainHandler.domainMatch(domain, host)) {
            throw new CookieRestrictionViolationException("Illegal 'domain' attribute \"" + domain + "\". Domain of origin: \"" + host + "\"");
        }
    }

    static boolean domainMatch(String domain, String host) {
        String normalizedDomain;
        if (InetAddressUtils.isIPv4Address((String)host) || InetAddressUtils.isIPv6Address((String)host)) {
            return false;
        }
        String string = normalizedDomain = domain.startsWith(".") ? domain.substring(1) : domain;
        if (host.endsWith(normalizedDomain)) {
            int prefix = host.length() - normalizedDomain.length();
            if (prefix == 0) {
                return true;
            }
            return prefix > 1 && host.charAt(prefix - 1) == '.';
        }
        return false;
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
        if (domain.startsWith(".")) {
            domain = domain.substring(1);
        }
        if (host.equals(domain = domain.toLowerCase(Locale.ROOT))) {
            return true;
        }
        if (cookie.containsAttribute("domain")) {
            return BasicDomainHandler.domainMatch(domain, host);
        }
        return false;
    }

    @Override
    public String getAttributeName() {
        return "domain";
    }
}

