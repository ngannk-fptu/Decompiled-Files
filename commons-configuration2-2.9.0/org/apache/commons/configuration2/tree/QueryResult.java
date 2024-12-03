/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package org.apache.commons.configuration2.tree;

import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class QueryResult<T> {
    private final T node;
    private final String attributeName;

    private QueryResult(T nd, String attr) {
        this.node = nd;
        this.attributeName = attr;
    }

    public static <T> QueryResult<T> createNodeResult(T resultNode) {
        return new QueryResult<T>(resultNode, null);
    }

    public static <T> QueryResult<T> createAttributeResult(T parentNode, String attrName) {
        return new QueryResult<T>(parentNode, attrName);
    }

    public T getNode() {
        return this.node;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public boolean isAttributeResult() {
        return StringUtils.isNotEmpty((CharSequence)this.getAttributeName());
    }

    public Object getAttributeValue(NodeHandler<T> handler) {
        if (!this.isAttributeResult()) {
            throw new IllegalStateException("This is not an attribute result! Attribute value cannot be fetched.");
        }
        return handler.getAttributeValue(this.getNode(), this.getAttributeName());
    }

    public int hashCode() {
        return new HashCodeBuilder().append(this.getNode()).append((Object)this.getAttributeName()).toHashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof QueryResult)) {
            return false;
        }
        QueryResult c = (QueryResult)obj;
        return new EqualsBuilder().append(this.getNode(), c.getNode()).append((Object)this.getAttributeName(), (Object)c.getAttributeName()).isEquals();
    }

    public String toString() {
        ToStringBuilder sb = new ToStringBuilder((Object)this);
        if (this.isAttributeResult()) {
            sb.append("parentNode", this.getNode()).append("attribute", (Object)this.getAttributeName());
        } else {
            sb.append("resultNode", this.getNode());
        }
        return sb.toString();
    }
}

