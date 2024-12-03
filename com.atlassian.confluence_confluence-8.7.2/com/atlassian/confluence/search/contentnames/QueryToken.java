/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.search.contentnames;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class QueryToken {
    private String text;
    private Type type;

    public QueryToken(String text, Type type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return this.text;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof QueryToken)) {
            return false;
        }
        QueryToken other = (QueryToken)obj;
        return new EqualsBuilder().append((Object)this.text, (Object)other.text).append((Object)this.type, (Object)other.type).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.text).append((Object)this.type).toHashCode();
    }

    public static enum Type {
        PARTIAL,
        FULL;

    }
}

