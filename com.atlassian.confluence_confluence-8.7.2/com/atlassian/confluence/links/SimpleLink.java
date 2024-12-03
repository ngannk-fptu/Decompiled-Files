/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.links;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SimpleLink {
    private final String text;
    private final String href;

    public SimpleLink(String text, String href) {
        this.text = text;
        this.href = href;
    }

    public String getHref() {
        return this.href;
    }

    public String getText() {
        return this.text;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SimpleLink)) {
            return false;
        }
        SimpleLink that = (SimpleLink)o;
        return new EqualsBuilder().append((Object)this.href, (Object)that.href).append((Object)this.text, (Object)that.text).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.href).append((Object)this.text).toHashCode();
    }

    public String toString() {
        return this.text + " / " + this.href;
    }
}

