/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.http;

import java.util.List;
import org.eclipse.jetty.http.ComplianceViolation;
import org.eclipse.jetty.http.CookieCompliance;
import org.eclipse.jetty.http.CookieCutter;
import org.eclipse.jetty.http.RFC6265CookieParser;

public interface CookieParser {
    public static CookieParser newParser(Handler handler, CookieCompliance compliance, ComplianceViolation.Listener complianceListener) {
        if (compliance.allows(CookieCompliance.Violation.BAD_QUOTES)) {
            return new CookieCutter(handler, compliance, complianceListener);
        }
        return new RFC6265CookieParser(handler, compliance, complianceListener);
    }

    public void parseField(String var1) throws InvalidCookieException;

    default public void parseFields(List<String> rawFields) throws InvalidCookieException {
        for (String field : rawFields) {
            this.parseField(field);
        }
    }

    public static interface Handler {
        public void addCookie(String var1, String var2, int var3, String var4, String var5, String var6);
    }

    public static class InvalidCookieException
    extends IllegalArgumentException {
        public InvalidCookieException() {
        }

        public InvalidCookieException(String s) {
            super(s);
        }

        public InvalidCookieException(String message, Throwable cause) {
            super(message, cause);
        }

        public InvalidCookieException(Throwable cause) {
            super(cause);
        }
    }
}

