/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package org.apache.commons.configuration2.tree;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class DefaultExpressionEngineSymbols {
    public static final String DEFAULT_PROPERTY_DELIMITER = ".";
    public static final String DEFAULT_ESCAPED_DELIMITER = "..";
    public static final String DEFAULT_ATTRIBUTE_START = "[@";
    public static final String DEFAULT_ATTRIBUTE_END = "]";
    public static final String DEFAULT_INDEX_START = "(";
    public static final String DEFAULT_INDEX_END = ")";
    public static final DefaultExpressionEngineSymbols DEFAULT_SYMBOLS = DefaultExpressionEngineSymbols.createDefaultSmybols();
    private final String propertyDelimiter;
    private final String escapedDelimiter;
    private final String attributeStart;
    private final String attributeEnd;
    private final String indexStart;
    private final String indexEnd;

    private DefaultExpressionEngineSymbols(Builder b) {
        this.propertyDelimiter = b.propertyDelimiter;
        this.escapedDelimiter = b.escapedDelimiter;
        this.indexStart = b.indexStart;
        this.indexEnd = b.indexEnd;
        this.attributeStart = b.attributeStart;
        this.attributeEnd = b.attributeEnd;
    }

    public String getPropertyDelimiter() {
        return this.propertyDelimiter;
    }

    public String getEscapedDelimiter() {
        return this.escapedDelimiter;
    }

    public String getAttributeStart() {
        return this.attributeStart;
    }

    public String getAttributeEnd() {
        return this.attributeEnd;
    }

    public String getIndexStart() {
        return this.indexStart;
    }

    public String getIndexEnd() {
        return this.indexEnd;
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.getPropertyDelimiter()).append((Object)this.getEscapedDelimiter()).append((Object)this.getIndexStart()).append((Object)this.getIndexEnd()).append((Object)this.getAttributeStart()).append((Object)this.getAttributeEnd()).toHashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DefaultExpressionEngineSymbols)) {
            return false;
        }
        DefaultExpressionEngineSymbols c = (DefaultExpressionEngineSymbols)obj;
        return new EqualsBuilder().append((Object)this.getPropertyDelimiter(), (Object)c.getPropertyDelimiter()).append((Object)this.getEscapedDelimiter(), (Object)c.getEscapedDelimiter()).append((Object)this.getIndexStart(), (Object)c.getIndexStart()).append((Object)this.getIndexEnd(), (Object)c.getIndexEnd()).append((Object)this.getAttributeStart(), (Object)c.getAttributeStart()).append((Object)this.getAttributeEnd(), (Object)c.getAttributeEnd()).isEquals();
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("propertyDelimiter", (Object)this.getPropertyDelimiter()).append("escapedDelimiter", (Object)this.getEscapedDelimiter()).append("indexStart", (Object)this.getIndexStart()).append("indexEnd", (Object)this.getIndexEnd()).append("attributeStart", (Object)this.getAttributeStart()).append("attributeEnd", (Object)this.getAttributeEnd()).toString();
    }

    private static DefaultExpressionEngineSymbols createDefaultSmybols() {
        return new Builder().setPropertyDelimiter(DEFAULT_PROPERTY_DELIMITER).setEscapedDelimiter(DEFAULT_ESCAPED_DELIMITER).setIndexStart(DEFAULT_INDEX_START).setIndexEnd(DEFAULT_INDEX_END).setAttributeStart(DEFAULT_ATTRIBUTE_START).setAttributeEnd(DEFAULT_ATTRIBUTE_END).create();
    }

    public static class Builder {
        private String propertyDelimiter;
        private String escapedDelimiter;
        private String attributeStart;
        private String attributeEnd;
        private String indexStart;
        private String indexEnd;

        public Builder() {
        }

        public Builder(DefaultExpressionEngineSymbols c) {
            this.propertyDelimiter = c.getPropertyDelimiter();
            this.escapedDelimiter = c.getEscapedDelimiter();
            this.indexStart = c.getIndexStart();
            this.indexEnd = c.getIndexEnd();
            this.attributeStart = c.getAttributeStart();
            this.attributeEnd = c.getAttributeEnd();
        }

        public Builder setPropertyDelimiter(String d) {
            this.propertyDelimiter = d;
            return this;
        }

        public Builder setEscapedDelimiter(String ed) {
            this.escapedDelimiter = ed;
            return this;
        }

        public Builder setIndexStart(String is) {
            this.indexStart = is;
            return this;
        }

        public Builder setIndexEnd(String ie) {
            this.indexEnd = ie;
            return this;
        }

        public Builder setAttributeStart(String as) {
            this.attributeStart = as;
            return this;
        }

        public Builder setAttributeEnd(String ae) {
            this.attributeEnd = ae;
            return this;
        }

        public DefaultExpressionEngineSymbols create() {
            return new DefaultExpressionEngineSymbols(this);
        }
    }
}

