/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.xwork;

import java.util.EnumSet;

public enum HttpMethod {
    GET{

        @Override
        public boolean matches(String methodName) {
            return super.matches(methodName) || HEAD.matches(methodName);
        }
    }
    ,
    POST,
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE,
    CONNECT,
    PROPFIND,
    PROPPATCH,
    MKCOL,
    COPY,
    MOVE,
    LOCK,
    UNLOCK,
    PATCH,
    ALL_RFC2616{

        @Override
        public boolean matches(String methodName) {
            for (HttpMethod method : EnumSet.range(GET, CONNECT)) {
                if (!method.matches(methodName)) continue;
                return true;
            }
            return false;
        }
    }
    ,
    ALL_DEFINED{

        @Override
        public boolean matches(String methodName) {
            for (HttpMethod method : EnumSet.range(GET, PATCH)) {
                if (!method.matches(methodName)) continue;
                return true;
            }
            return false;
        }
    }
    ,
    ANY_METHOD{

        @Override
        public boolean matches(String methodName) {
            return true;
        }
    };


    public boolean matches(String methodName) {
        return methodName != null && this.toString().equals(methodName);
    }

    public static boolean anyMatch(String methodName, HttpMethod ... httpMethods) {
        for (HttpMethod allowedMethod : httpMethods) {
            if (!allowedMethod.matches(methodName)) continue;
            return true;
        }
        return false;
    }
}

