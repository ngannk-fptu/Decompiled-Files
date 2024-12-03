/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletMapping
 *  javax.servlet.http.MappingMatch
 */
package org.apache.catalina.core;

import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.MappingMatch;
import org.apache.catalina.mapper.MappingData;

public class ApplicationMapping {
    private final MappingData mappingData;
    private volatile HttpServletMapping mapping = null;

    public ApplicationMapping(MappingData mappingData) {
        this.mappingData = mappingData;
    }

    public HttpServletMapping getHttpServletMapping() {
        if (this.mapping == null) {
            String servletName = this.mappingData.wrapper == null ? "" : this.mappingData.wrapper.getName();
            if (this.mappingData.matchType == null) {
                this.mapping = new MappingImpl("", "", null, servletName);
            } else {
                switch (this.mappingData.matchType) {
                    case CONTEXT_ROOT: {
                        this.mapping = new MappingImpl("", "", this.mappingData.matchType, servletName);
                        break;
                    }
                    case DEFAULT: {
                        this.mapping = new MappingImpl("", "/", this.mappingData.matchType, servletName);
                        break;
                    }
                    case EXACT: {
                        this.mapping = new MappingImpl(this.mappingData.wrapperPath.toString().substring(1), this.mappingData.wrapperPath.toString(), this.mappingData.matchType, servletName);
                        break;
                    }
                    case EXTENSION: {
                        String path = this.mappingData.wrapperPath.toString();
                        int extIndex = path.lastIndexOf(46);
                        this.mapping = new MappingImpl(path.substring(1, extIndex), "*" + path.substring(extIndex), this.mappingData.matchType, servletName);
                        break;
                    }
                    case PATH: {
                        String matchValue = this.mappingData.pathInfo.isNull() ? null : this.mappingData.pathInfo.toString().substring(1);
                        this.mapping = new MappingImpl(matchValue, this.mappingData.wrapperPath.toString() + "/*", this.mappingData.matchType, servletName);
                    }
                }
            }
        }
        return this.mapping;
    }

    public void recycle() {
        this.mapping = null;
    }

    private static class MappingImpl
    implements HttpServletMapping {
        private final String matchValue;
        private final String pattern;
        private final MappingMatch mappingType;
        private final String servletName;

        MappingImpl(String matchValue, String pattern, MappingMatch mappingType, String servletName) {
            this.matchValue = matchValue;
            this.pattern = pattern;
            this.mappingType = mappingType;
            this.servletName = servletName;
        }

        public String getMatchValue() {
            return this.matchValue;
        }

        public String getPattern() {
            return this.pattern;
        }

        public MappingMatch getMappingMatch() {
            return this.mappingType;
        }

        public String getServletName() {
            return this.servletName;
        }
    }
}

