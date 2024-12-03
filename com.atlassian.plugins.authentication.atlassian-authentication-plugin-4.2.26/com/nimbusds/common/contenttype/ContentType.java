/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.common.contenttype;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

public final class ContentType {
    public static final ContentType APPLICATION_JSON = new ContentType("application", "json", Parameter.CHARSET_UTF_8);
    public static final ContentType APPLICATION_JOSE = new ContentType("application", "jose", Parameter.CHARSET_UTF_8);
    public static final ContentType APPLICATION_JWT = new ContentType("application", "jwt", Parameter.CHARSET_UTF_8);
    public static final ContentType APPLICATION_URLENCODED = new ContentType("application", "x-www-form-urlencoded", Parameter.CHARSET_UTF_8);
    public static final ContentType TEXT_PLAIN = new ContentType("text", "plain", Parameter.CHARSET_UTF_8);
    private final String baseType;
    private final String subType;
    private final List<Parameter> params;

    public ContentType(String baseType, String subType, Parameter ... param) {
        if (baseType == null || baseType.trim().isEmpty()) {
            throw new IllegalArgumentException("The base type must be specified");
        }
        this.baseType = baseType;
        if (subType == null || subType.trim().isEmpty()) {
            throw new IllegalArgumentException("The subtype must be specified");
        }
        this.subType = subType;
        this.params = param != null && param.length > 0 ? Collections.unmodifiableList(Arrays.asList(param)) : Collections.emptyList();
    }

    public ContentType(String baseType, String subType, Charset charset) {
        this(baseType, subType, new Parameter("charset", charset.toString()));
    }

    public String getBaseType() {
        return this.baseType;
    }

    public String getSubType() {
        return this.subType;
    }

    public String getType() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getBaseType());
        sb.append("/");
        sb.append(this.getSubType());
        return sb.toString();
    }

    public List<Parameter> getParameters() {
        return this.params;
    }

    public boolean matches(ContentType other) {
        return other != null && this.getBaseType().equalsIgnoreCase(other.getBaseType()) && this.getSubType().equalsIgnoreCase(other.getSubType());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getType());
        if (!this.getParameters().isEmpty()) {
            for (Parameter p : this.getParameters()) {
                sb.append("; ");
                sb.append(p.getName());
                sb.append("=");
                sb.append(p.getValue());
            }
        }
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContentType)) {
            return false;
        }
        ContentType that = (ContentType)o;
        return this.getBaseType().equalsIgnoreCase(that.getBaseType()) && this.getSubType().equalsIgnoreCase(that.getSubType()) && this.params.equals(that.params);
    }

    public int hashCode() {
        return Objects.hash(this.getBaseType().toLowerCase(), this.getSubType().toLowerCase(), this.params);
    }

    public static ContentType parse(String s) throws ParseException {
        if (s == null || s.trim().isEmpty()) {
            throw new ParseException("Null or empty content type string", 0);
        }
        StringTokenizer st = new StringTokenizer(s, "/");
        if (!st.hasMoreTokens()) {
            throw new ParseException("Invalid content type string", 0);
        }
        String type = st.nextToken().trim();
        if (type.trim().isEmpty()) {
            throw new ParseException("Invalid content type string", 0);
        }
        if (!st.hasMoreTokens()) {
            throw new ParseException("Invalid content type string", 0);
        }
        String subtypeWithOptParams = st.nextToken().trim();
        if (!(st = new StringTokenizer(subtypeWithOptParams, ";")).hasMoreTokens()) {
            return new ContentType(type, subtypeWithOptParams.trim(), new Parameter[0]);
        }
        String subtype = st.nextToken().trim();
        if (!st.hasMoreTokens()) {
            return new ContentType(type, subtype, new Parameter[0]);
        }
        LinkedList<Parameter> params = new LinkedList<Parameter>();
        while (st.hasMoreTokens()) {
            String paramToken = st.nextToken().trim();
            StringTokenizer paramTokenizer = new StringTokenizer(paramToken, "=");
            if (!paramTokenizer.hasMoreTokens()) {
                throw new ParseException("Invalid parameter", 0);
            }
            String paramName = paramTokenizer.nextToken().trim();
            if (!paramTokenizer.hasMoreTokens()) {
                throw new ParseException("Invalid parameter", 0);
            }
            String paramValue = paramTokenizer.nextToken().trim();
            try {
                params.add(new Parameter(paramName, paramValue));
            }
            catch (IllegalArgumentException e) {
                throw new ParseException("Invalid parameter: " + e.getMessage(), 0);
            }
        }
        return new ContentType(type, subtype, params.toArray(new Parameter[0]));
    }

    public static final class Parameter {
        public static final Parameter CHARSET_UTF_8 = new Parameter("charset", "UTF-8");
        private final String name;
        private final String value;

        public Parameter(String name, String value) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("The parameter name must be specified");
            }
            this.name = name;
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("The parameter value must be specified");
            }
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return this.name + "=" + this.value;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Parameter)) {
                return false;
            }
            Parameter parameter = (Parameter)o;
            return this.getName().equalsIgnoreCase(parameter.getName()) && this.getValue().equalsIgnoreCase(parameter.getValue());
        }

        public int hashCode() {
            return Objects.hash(this.getName().toLowerCase(), this.getValue().toLowerCase());
        }
    }
}

