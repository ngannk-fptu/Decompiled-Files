/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.descriptor.web.ErrorPage
 */
package org.apache.catalina.util;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tomcat.util.descriptor.web.ErrorPage;

public class ErrorPageSupport {
    private Map<String, ErrorPage> exceptionPages = new ConcurrentHashMap<String, ErrorPage>();
    private Map<Integer, ErrorPage> statusPages = new ConcurrentHashMap<Integer, ErrorPage>();

    public void add(ErrorPage errorPage) {
        String exceptionType = errorPage.getExceptionType();
        if (exceptionType == null) {
            this.statusPages.put(errorPage.getErrorCode(), errorPage);
        } else {
            this.exceptionPages.put(exceptionType, errorPage);
        }
    }

    public void remove(ErrorPage errorPage) {
        String exceptionType = errorPage.getExceptionType();
        if (exceptionType == null) {
            this.statusPages.remove(errorPage.getErrorCode(), errorPage);
        } else {
            this.exceptionPages.remove(exceptionType, errorPage);
        }
    }

    public ErrorPage find(int statusCode) {
        return this.statusPages.get(statusCode);
    }

    public ErrorPage find(String exceptionType) {
        return this.exceptionPages.get(exceptionType);
    }

    public ErrorPage find(Throwable exceptionType) {
        if (exceptionType == null) {
            return null;
        }
        Class<?> clazz = exceptionType.getClass();
        String name = clazz.getName();
        while (!Object.class.equals(clazz)) {
            ErrorPage errorPage = this.exceptionPages.get(name);
            if (errorPage != null) {
                return errorPage;
            }
            if ((clazz = clazz.getSuperclass()) == null) break;
            name = clazz.getName();
        }
        return null;
    }

    public ErrorPage[] findAll() {
        HashSet<ErrorPage> errorPages = new HashSet<ErrorPage>();
        errorPages.addAll(this.exceptionPages.values());
        errorPages.addAll(this.statusPages.values());
        return errorPages.toArray(new ErrorPage[0]);
    }
}

