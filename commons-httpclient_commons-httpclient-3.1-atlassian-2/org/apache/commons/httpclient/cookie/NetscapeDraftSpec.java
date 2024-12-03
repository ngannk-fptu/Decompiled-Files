/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.cookie;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookieSpecBase;
import org.apache.commons.httpclient.cookie.MalformedCookieException;

public class NetscapeDraftSpec
extends CookieSpecBase {
    @Override
    public Cookie[] parse(String host, int port, String path, boolean secure, String header) throws MalformedCookieException {
        LOG.trace((Object)"enter NetscapeDraftSpec.parse(String, port, path, boolean, Header)");
        if (host == null) {
            throw new IllegalArgumentException("Host of origin may not be null");
        }
        if (host.trim().equals("")) {
            throw new IllegalArgumentException("Host of origin may not be blank");
        }
        if (port < 0) {
            throw new IllegalArgumentException("Invalid port: " + port);
        }
        if (path == null) {
            throw new IllegalArgumentException("Path of origin may not be null.");
        }
        if (header == null) {
            throw new IllegalArgumentException("Header may not be null.");
        }
        if (path.trim().equals("")) {
            path = "/";
        }
        host = host.toLowerCase(Locale.ENGLISH);
        String defaultPath = path;
        int lastSlashIndex = defaultPath.lastIndexOf("/");
        if (lastSlashIndex >= 0) {
            if (lastSlashIndex == 0) {
                lastSlashIndex = 1;
            }
            defaultPath = defaultPath.substring(0, lastSlashIndex);
        }
        HeaderElement headerelement = new HeaderElement(header.toCharArray());
        Cookie cookie = new Cookie(host, headerelement.getName(), headerelement.getValue(), defaultPath, null, false);
        NameValuePair[] parameters = headerelement.getParameters();
        if (parameters != null) {
            for (int j = 0; j < parameters.length; ++j) {
                this.parseAttribute(parameters[j], cookie);
            }
        }
        return new Cookie[]{cookie};
    }

    @Override
    public void parseAttribute(NameValuePair attribute, Cookie cookie) throws MalformedCookieException {
        if (attribute == null) {
            throw new IllegalArgumentException("Attribute may not be null.");
        }
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null.");
        }
        String paramName = attribute.getName().toLowerCase(Locale.ENGLISH);
        String paramValue = attribute.getValue();
        if (paramName.equals("expires")) {
            if (paramValue == null) {
                throw new MalformedCookieException("Missing value for expires attribute");
            }
            try {
                SimpleDateFormat expiryFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", Locale.US);
                Date date = expiryFormat.parse(paramValue);
                cookie.setExpiryDate(date);
            }
            catch (ParseException e) {
                throw new MalformedCookieException("Invalid expires attribute: " + e.getMessage());
            }
        } else {
            super.parseAttribute(attribute, cookie);
        }
    }

    @Override
    public boolean domainMatch(String host, String domain) {
        return host.endsWith(domain);
    }

    @Override
    public void validate(String host, int port, String path, boolean secure, Cookie cookie) throws MalformedCookieException {
        LOG.trace((Object)"enterNetscapeDraftCookieProcessor RCF2109CookieProcessor.validate(Cookie)");
        super.validate(host, port, path, secure, cookie);
        if (host.indexOf(".") >= 0) {
            int domainParts = new StringTokenizer(cookie.getDomain(), ".").countTokens();
            if (NetscapeDraftSpec.isSpecialDomain(cookie.getDomain())) {
                if (domainParts < 2) {
                    throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates the Netscape cookie specification for " + "special domains");
                }
            } else if (domainParts < 3) {
                throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates the Netscape cookie specification");
            }
        }
    }

    private static boolean isSpecialDomain(String domain) {
        String ucDomain = domain.toUpperCase(Locale.ENGLISH);
        return ucDomain.endsWith(".COM") || ucDomain.endsWith(".EDU") || ucDomain.endsWith(".NET") || ucDomain.endsWith(".GOV") || ucDomain.endsWith(".MIL") || ucDomain.endsWith(".ORG") || ucDomain.endsWith(".INT");
    }
}

