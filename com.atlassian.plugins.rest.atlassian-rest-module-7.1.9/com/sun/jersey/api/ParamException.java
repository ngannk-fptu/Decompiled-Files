/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api;

import java.lang.annotation.Annotation;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

public abstract class ParamException
extends WebApplicationException {
    private final Class<? extends Annotation> parameterType;
    private final String name;
    private final String defaultStringValue;

    protected ParamException(Throwable cause, int status, Class<? extends Annotation> parameterType, String name, String defaultStringValue) {
        super(cause, status);
        this.parameterType = parameterType;
        this.name = name;
        this.defaultStringValue = defaultStringValue;
    }

    public Class<? extends Annotation> getParameterType() {
        return this.parameterType;
    }

    public String getParameterName() {
        return this.name;
    }

    public String getDefaultStringValue() {
        return this.defaultStringValue;
    }

    public static class FormParamException
    extends ParamException {
        public FormParamException(Throwable cause, String name, String defaultStringValue) {
            super(cause, 400, FormParam.class, name, defaultStringValue);
        }
    }

    public static class CookieParamException
    extends ParamException {
        public CookieParamException(Throwable cause, String name, String defaultStringValue) {
            super(cause, 400, CookieParam.class, name, defaultStringValue);
        }
    }

    public static class HeaderParamException
    extends ParamException {
        public HeaderParamException(Throwable cause, String name, String defaultStringValue) {
            super(cause, 400, HeaderParam.class, name, defaultStringValue);
        }
    }

    public static class QueryParamException
    extends URIParamException {
        public QueryParamException(Throwable cause, String name, String defaultStringValue) {
            super(cause, QueryParam.class, name, defaultStringValue);
        }
    }

    public static class MatrixParamException
    extends URIParamException {
        public MatrixParamException(Throwable cause, String name, String defaultStringValue) {
            super(cause, MatrixParam.class, name, defaultStringValue);
        }
    }

    public static class PathParamException
    extends URIParamException {
        public PathParamException(Throwable cause, String name, String defaultStringValue) {
            super(cause, PathParam.class, name, defaultStringValue);
        }
    }

    public static abstract class URIParamException
    extends ParamException {
        protected URIParamException(Throwable cause, Class<? extends Annotation> parameterType, String name, String defaultStringValue) {
            super(cause, 404, parameterType, name, defaultStringValue);
        }
    }
}

