/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.cookie;

import java.util.StringTokenizer;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.cookie.SetCookie2;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class RFC2965PortAttributeHandler
implements CommonCookieAttributeHandler {
    private static int[] parsePortAttribute(String portValue) throws MalformedCookieException {
        StringTokenizer st = new StringTokenizer(portValue, ",");
        int[] ports = new int[st.countTokens()];
        try {
            int i = 0;
            while (st.hasMoreTokens()) {
                ports[i] = Integer.parseInt(st.nextToken().trim());
                if (ports[i] < 0) {
                    throw new MalformedCookieException("Invalid Port attribute.");
                }
                ++i;
            }
        }
        catch (NumberFormatException e) {
            throw new MalformedCookieException("Invalid Port attribute: " + e.getMessage());
        }
        return ports;
    }

    private static boolean portMatch(int port, int[] ports) {
        boolean portInList = false;
        for (int port2 : ports) {
            if (port != port2) continue;
            portInList = true;
            break;
        }
        return portInList;
    }

    @Override
    public void parse(SetCookie cookie, String portValue) throws MalformedCookieException {
        Args.notNull((Object)cookie, (String)"Cookie");
        if (cookie instanceof SetCookie2) {
            SetCookie2 cookie2 = (SetCookie2)cookie;
            if (portValue != null && !portValue.trim().isEmpty()) {
                int[] ports = RFC2965PortAttributeHandler.parsePortAttribute(portValue);
                cookie2.setPorts(ports);
            }
        }
    }

    @Override
    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        Args.notNull((Object)cookie, (String)"Cookie");
        Args.notNull((Object)origin, (String)"Cookie origin");
        int port = origin.getPort();
        if (cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("port") && !RFC2965PortAttributeHandler.portMatch(port, cookie.getPorts())) {
            throw new CookieRestrictionViolationException("Port attribute violates RFC 2965: Request port not found in cookie's port list.");
        }
    }

    @Override
    public boolean match(Cookie cookie, CookieOrigin origin) {
        Args.notNull((Object)cookie, (String)"Cookie");
        Args.notNull((Object)origin, (String)"Cookie origin");
        int port = origin.getPort();
        if (cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("port")) {
            if (cookie.getPorts() == null) {
                return false;
            }
            if (!RFC2965PortAttributeHandler.portMatch(port, cookie.getPorts())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getAttributeName() {
        return "port";
    }
}

