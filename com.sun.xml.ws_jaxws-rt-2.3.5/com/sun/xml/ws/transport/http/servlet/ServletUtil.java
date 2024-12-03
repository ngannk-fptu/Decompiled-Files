/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.transport.http.servlet;

public class ServletUtil {
    public static boolean isServlet30Based() {
        try {
            Class<?> servletRequestClazz = Class.forName("javax.servlet.ServletRequest");
            servletRequestClazz.getDeclaredMethod("getServletContext", new Class[0]);
            return true;
        }
        catch (Throwable throwable) {
            return false;
        }
    }
}

